#!/usr/bin/env python3
"""Assemble reviewed, offline release notices from Licensee reports and Gradle artifacts.

Run the two licenseeAndroid*Release tasks first. This maintenance tool does not
download anything. Review upstream supplements when dependencies change.
"""

import argparse
import hashlib
import io
import json
from pathlib import Path
import re
import zipfile


ROOT = Path(__file__).resolve().parents[1]


def digest(data):
    return hashlib.sha256(data).hexdigest()


def archive_notices(data, source):
    """Preserve nested JAR notices and complete Google SDK notice bundles."""
    result = []
    with zipfile.ZipFile(io.BytesIO(data)) as archive:
        names = archive.namelist()
        for name in sorted(names):
            if name.endswith("/"):
                continue
            member = archive.read(name)
            member_source = source + "!/" + name
            if name.endswith(".jar"):
                result.extend(archive_notices(member, member_source))
            elif name.endswith("third_party_licenses.json"):
                text_name = name[:-5] + ".txt"
                text = archive.read(text_name)
                entries = json.loads(member)
                if not isinstance(entries, dict) or not entries:
                    raise ValueError("Empty third-party license table: " + member_source)
                # Preserve the complete supplied text, including its component
                # headings. Ads 25.4.0 has an index range extending beyond EOF;
                # splitting on those offsets would truncate/misattribute notices.
                body = text.decode("utf-8")
                if not body.strip():
                    raise ValueError("Empty third-party license text: " + member_source)
                result.append((source + "!/" + text_name, body))
            elif name.endswith("third_party_licenses.txt"):
                if name[:-4] + ".json" not in names:
                    raise ValueError("Missing third-party license table: " + member_source)
            elif re.search(r"(?i)(?:^|/)(?:license|notice|copyright|copying)(?:[._-].*)?$", name):
                body = member.decode("utf-8")
                if not body.strip():
                    raise ValueError("Empty notice: " + member_source)
                result.append((member_source, body))
    return result


def generate(flavor):
    report = ROOT / f"app/build/reports/licensee/android{flavor.title()}Release/artifacts.json"
    artifacts = json.loads(report.read_text())
    resolved = json.loads((ROOT / f"app/build/reports/third-party-notices/{flavor}-artifacts.json").read_text())
    report_coordinates = {f"{a['groupId']}:{a['artifactId']}:{a['version']}" for a in artifacts}
    if {a["coordinate"] for a in resolved} - report_coordinates:
        raise ValueError("Resolved artifacts are missing from the Licensee report")
    supplements = json.loads((ROOT / "licenses/supplements.json").read_text())
    documents = {}
    dependencies = []
    inventory = []

    def add(source, text, coordinate):
        # Preserve the supplied text exactly. Only identical documents are deduplicated.
        key = digest(text.encode("utf-8"))
        document = documents.setdefault(key, {"text": text, "sources": set()})
        document["sources"].add((coordinate, source))
        return key

    for artifact in sorted(artifacts, key=lambda x: (x["groupId"], x["artifactId"], x["version"])):
        group, name, version = (artifact[k] for k in ("groupId", "artifactId", "version"))
        coordinate = f"{group}:{name}:{version}"
        dependencies.append(coordinate)
        identifiers = {item["identifier"] for item in artifact.get("spdxLicenses", [])}
        urls = {item["url"] for item in artifact.get("unknownLicenses", [])}
        if not identifiers and not urls:
            raise ValueError("No license metadata: " + coordinate)
        if identifiers - {"Apache-2.0", "BSD-3-Clause", "MIT"}:
            raise ValueError("Review new license before generating notices: " + coordinate)
        if urls - {"https://developer.android.com/studio/terms.html"}:
            raise ValueError("Review new proprietary terms: " + coordinate)
        paths = sorted(Path(a["file"]) for a in resolved if a["coordinate"] == coordinate)
        covered = set()
        for path in paths:
            data = path.read_bytes()
            inventory.append({"coordinate": coordinate, "file": path.name, "sha256": digest(data)})
            if path.suffix in {".aar", ".jar"}:
                for source, body in archive_notices(data, path.name):
                    covered.add(add(source, body, coordinate))
        matched = False
        for supplement in supplements:
            if re.fullmatch(supplement["coordinates"], coordinate):
                matched = True
                path = ROOT / "licenses/sources" / supplement["file"]
                data = path.read_bytes()
                if digest(data) != supplement["sha256"]:
                    raise ValueError("Supplement checksum mismatch: " + str(path))
                covered.add(add(supplement["source"], data.decode("utf-8"), coordinate))
        if not matched:
            raise ValueError("No reviewed upstream supplement coverage for " + coordinate)
        if not covered:
            raise ValueError("No notice text for " + coordinate)

    # Artwork is common to both distributions, and is not a Maven dependency.
    for supplement in supplements:
        if supplement["coordinates"] == "@artwork":
            path = ROOT / "licenses/sources" / supplement["file"]
            data = path.read_bytes()
            if digest(data) != supplement["sha256"]:
                raise ValueError("Artwork checksum mismatch")
            add(supplement["source"], data.decode("utf-8"), "Android robot artwork")

    sections = [
        f"Aplin — {flavor.upper()} release third-party licenses and notices\n\n"
        "This document reproduces upstream license and attribution texts. "
        "SDK-bundled notices may also describe components removed by optimization. "
        "They do not change the license of Aplin or grant additional SDK/trademark rights.\n\n"
        "Release dependency versions:\n" + "\n".join(dependencies)
    ]
    manifest_documents = []
    for key, document in sorted(documents.items()):
        sources = [{"coordinate": c, "source": s} for c, s in sorted(document["sources"])]
        attribution = "\n".join(f"{s['coordinate']} — {s['source']}" for s in sources)
        sections.append(f"Document SHA-256: {key}\n{attribution}\n\n{document['text']}")
        manifest_documents.append({"sha256": key, "sources": sources})
    text = ("\n\n" + "=" * 72 + "\n\n").join(sections) + "\n"
    manifest = {
        "schema": 1,
        "dependencies": sorted(dependencies),
        "noticesSha256": digest(text.encode("utf-8")),
        "artifacts": inventory,
        "documents": manifest_documents,
    }
    return text, json.dumps(manifest, ensure_ascii=False, indent=2) + "\n"


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="Fail if committed outputs differ")
    args = parser.parse_args()
    for flavor in ("play", "foss"):
        text, manifest = generate(flavor)
        outputs = {
            ROOT / f"app/src/{flavor}/assets/licenses/third-party-notices.txt": text,
            ROOT / f"licenses/{flavor}.json": manifest,
        }
        for path, content in outputs.items():
            if args.check:
                if not path.is_file() or path.read_text() != content:
                    raise ValueError("Stale notices: " + str(path))
            else:
                path.parent.mkdir(parents=True, exist_ok=True)
                path.write_text(content, encoding="utf-8")
        print(f"{flavor}: {len(text.encode('utf-8')):,} bytes; notices {'verified' if args.check else 'generated'}")


if __name__ == "__main__":
    main()
