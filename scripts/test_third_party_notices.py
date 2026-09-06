"""Tests for preserving upstream notices without losing bundled/nested text."""

import importlib.util
import io
import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch
import zipfile


spec = importlib.util.spec_from_file_location("notices", Path(__file__).with_name("update-third-party-notices.py"))
notices = importlib.util.module_from_spec(spec)
spec.loader.exec_module(notices)


def archive(members):
    stream = io.BytesIO()
    with zipfile.ZipFile(stream, "w") as zipped:
        for name, body in members.items():
            zipped.writestr(name, body)
    return stream.getvalue()


class ArchiveNoticeTest(unittest.TestCase):
    def test_nested_jar_preserves_copyright_and_notice(self):
        data = archive({"classes.jar": archive({"META-INF/NOTICE.txt": "Author © 2026\n", "META-INF/LICENSE": "Terms\n", "code.class": b"code"})})
        self.assertEqual(
            notices.archive_notices(data, "library.aar"),
            [("library.aar!/classes.jar!/META-INF/LICENSE", "Terms\n"), ("library.aar!/classes.jar!/META-INF/NOTICE.txt", "Author © 2026\n")],
        )

    def test_google_notice_bundle_preserved_even_with_stale_offsets(self):
        # Ads 25.4.0 ships an index with a range beyond EOF. All supplied text
        # must survive, including headings, non-ASCII text and the final notice.
        text = "Library A:\nCopyright © A\n\nLibrary B:\nComplete terms B\n"
        data = archive({"third_party_licenses.json": json.dumps({"A": {"start": 10, "length": 9999}}), "third_party_licenses.txt": text})
        self.assertEqual(notices.archive_notices(data, "sdk.aar"), [("sdk.aar!/third_party_licenses.txt", text)])

    def test_missing_google_text_fails(self):
        with self.assertRaises(KeyError):
            notices.archive_notices(archive({"third_party_licenses.json": '{"A": {}}'}), "sdk.aar")

    def test_missing_google_index_fails(self):
        with self.assertRaisesRegex(ValueError, "Missing third-party license table"):
            notices.archive_notices(archive({"third_party_licenses.txt": "Terms"}), "sdk.aar")

    def test_empty_notice_fails(self):
        with self.assertRaisesRegex(ValueError, "Empty notice"):
            notices.archive_notices(archive({"META-INF/LICENSE.txt": " \n"}), "library.jar")


class ReviewedCoverageTest(unittest.TestCase):
    def test_unreviewed_guava_and_google_sdk_versions_require_review(self):
        supplements = (notices.ROOT / "licenses/supplements.json").read_text()
        for group, artifact, version, licenses in (
            ("com.google.guava", "guava", "33.0.0-android", {"spdxLicenses": [{"identifier": "Apache-2.0"}]}),
            ("com.google.android.gms", "play-services-ads", "99.0.0", {"unknownLicenses": [{"url": "https://developer.android.com/studio/terms.html"}]}),
        ):
            with self.subTest(artifact=artifact), tempfile.TemporaryDirectory() as directory:
                root = Path(directory)
                report = root / "app/build/reports/licensee/androidPlayRelease/artifacts.json"
                report.parent.mkdir(parents=True)
                report.write_text(json.dumps([{"groupId": group, "artifactId": artifact, "version": version, **licenses}]))
                resolved = root / "app/build/reports/third-party-notices/play-artifacts.json"
                resolved.parent.mkdir(parents=True)
                resolved.write_text("[]")
                (root / "licenses").mkdir()
                (root / "licenses/supplements.json").write_text(supplements)
                with patch.object(notices, "ROOT", root):
                    with self.assertRaisesRegex(ValueError, "No reviewed upstream supplement coverage"):
                        notices.generate("play")


if __name__ == "__main__":
    unittest.main()
