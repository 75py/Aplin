# Third-party license and notice packaging

Each distribution ships an offline UTF-8 document at
`app/src/<distribution>/assets/licenses/third-party-notices.txt`. The app's
**Open source licenses → View full license notices** screen displays it.
The existing Licensee catalog remains available for dependency coordinates.

## Reviewed inputs

The initial inventory covers the release graphs in `play.json` and `foss.json`:
188 / 161 Licensee coordinates, including metadata-only multiplatform modules,
and 141 / 108 resolved AAR/JAR files, respectively. Debug builds show these release
notices; development-only tooling is outside the distributed release inventory.

The manifests record the exact dependency versions, archive SHA-256 values,
document provenance and the final text checksum. `supplements.json` records
the original URLs, applicable coordinates and SHA-256 values of additional
upstream texts stored in `sources/`.

The inventory includes:

- `LICENSE`, `NOTICE`, `COPYRIGHT` and `COPYING` files in resolved JARs/AARs,
  including nested JARs.
- Complete `third_party_licenses.txt` bundles from the 11 Google SDK AARs in
  Play. The proprietary SDKs retain their own terms reference and are not
  relabeled as open source. Google Ads 25.4.0 contains a license index range
  extending beyond its text file; preserving the complete text avoids losing
  or misattributing content through those offsets.
- The full Apache 2.0 license for the reviewed Apache-licensed coordinates;
  version-pinned upstream notices/copyright texts for Kotlin, Koin, coroutines,
  serialization, atomicfu, Stately, Okio, logcat, Guava and JetBrains annotations.
- Kotlin's runtime third-party license texts (GWT, Guava, ThreeTenBP, Boost and
  protobuf). Kotlin's upstream root NOTICE is retained verbatim, including its
  original reference to the compiler distribution.
- The full Checker Qual MIT license from its actual JAR, and protobuf v28.2's
  BSD license including the copyright line absent from the relocated DataStore
  protobuf license file. DataStore 1.2.1's embedded `RuntimeVersion` identifies
  protobuf 4.28.2.
- The copyright headers of graphics-path 1.0.1 native/Filament-derived sources
  at its published AndroidX release commit, plus the Apache 2.0 text. The
  DataStore native library is part of the reviewed AndroidX Apache distribution.
- Android robot artwork attribution and the complete CC BY 3.0 license.

SDK-supplied bundles can include notices for code removed by R8. They are retained
as provided. Text bodies are preserved exactly; only byte-identical documents are
deduplicated, retaining every associated coordinate/source reference.

## Updating dependencies

1. Review the changed libraries' actual release archives and upstream
   `LICENSE`/`NOTICE` files, including bundled components. Refresh the relevant
   files, source URLs, coordinate rules and hashes in `supplements.json`.
   New Apache dependencies must be explicitly added to the reviewed coordinate
   rule after that review; the generator does not assume every new library is
   covered by a generic Apache text.
2. Resolve both release graphs and export the actual archive paths:

   ```sh
   ./gradlew exportPlayNoticeArtifacts exportFossNoticeArtifacts
   python3 scripts/update-third-party-notices.py
   ```

   These Gradle tasks do not need `ads.properties`. The Python script uses the
   exported files and checked-in supplements without network access.
3. Review changes to both generated documents and both manifests. Commit them
   together with the dependency and supplement changes.
4. Verify:

   ```sh
   python3 -m unittest discover -s scripts -p 'test_*.py'
   python3 scripts/update-third-party-notices.py --check
   ./gradlew validatePlayThirdPartyNotices validateFossThirdPartyNotices
   ```

Ordinary debug/release builds run the corresponding validation task before
building. A changed graph, changed archive bytes, or modified/missing bundled
text fails validation. CI also regenerates the text in check mode, which verifies
the supplements and reproduces the committed output.

The checks verify consistency with the reviewed inputs. New dependencies still
require review of their upstream distribution terms and bundled components;
passing a checksum check does not perform that review automatically.
