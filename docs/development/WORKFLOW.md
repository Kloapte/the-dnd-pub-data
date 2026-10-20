# Development and evidence workflow

Read [AGENTS.md](../../AGENTS.md), the [roadmap](../THE_DND_PUB_BACKEND_ROADMAP.md), and the current
task before implementation. The roadmap selects progress and scope; archived implementation orders
are historical evidence. The [analysis index](../../analysis/README.md) identifies active references.

## Build and verification

The project uses Java 21 and Spring Boot 4 through [pom.xml](../../pom.xml).
Use the Maven wrapper or the installed Maven executable:

```powershell
.\mvnw.cmd package
java -jar target/dnd-pub-data-0.0.1-SNAPSHOT.jar
```

On a Unix shell, use `./mvnw package`. Wrapper/dependency downloads require the usual network/cache
access. Do not commit personal cache locations or credentials to make commands work locally.

The latest implementation baseline is recorded in the roadmap and implementation report, rather
than copied into every historical report. For source/inspection/publication checks:

```powershell
node analysis/mechanics/batch-11b-item-charge-syntax/verify-source-hashes.mjs
node analysis/mechanics/public-v1/spells/p1d/export-runtime.mjs --check
& ./analysis/mechanics/step-7/7.2.3/verify-packaged.ps1
```

The packaged script accepts `-JavaExecutable` and `-JarPath` overrides. Its historical default is
machine-specific; supply the installed Java path on other machines. It starts a temporary localhost
process and stops that process in `finally`. Output belongs in ignored `target/`.

Offline inventory and benchmark implementations, plus `SchemaDiscovery`, live in the Maven test
source set and are excluded from the application JAR. The inventory assertions and candidate
benchmark tests run with the normal test suite. To run schema discovery after compiling test sources,
write its output under `target/`:

```powershell
mvn test-compile exec:java "-Dexec.classpathScope=test" `
    -Dexec.args="src/main/resources/data target/schema-discovery"
```

The moved inventory implementation comprises `SpellCapabilityInventoryRunner`,
`SpellCapabilityRecord`, `SpellCapabilityInventory`, `SpellCapabilityClassification`,
`SpellCapabilityInventoryComparison`, and the three inventory/comparison report writers. The
`mechanics.benchmark` package moved as a unit: fixture loading and validation, typed expectations,
review authority, comparison logic, and result types. These retain their Java package names in
`src/test/java` so existing test and report-generation callers remain compatible.

`SpellMechanicsPattern` no longer includes inventory-only methods, and runtime and inventory use the
same standard pattern composition. Wider PHB candidate assessment still calls the package-private
`supportsForInventory` and `extractForInventory` hooks on `PhbSpellMechanicsExtractor`; those hooks
reuse its extraction implementation and do not alter runtime eligibility. Removing that remaining
coupling cleanly requires separating PHB rule execution from production owner eligibility. Defer that
broader refactor rather than duplicate extraction logic or expose candidate matching at runtime.

## Documentation checks

```powershell
node docs/development/documentation-consolidation/audit-repository.mjs --verify
node docs/development/documentation-consolidation/verify-preservation.mjs
git diff --check
```

The preservation verifier describes this maintenance checkpoint; future implementation changes are
not expected to keep its full historical snapshot unchanged. The link audit checks current relative
Markdown paths, case and heading anchors, without fetching external URLs.

## Artifact safety

Do not relocate an analysis JSON, schema, script or gold set without checking Java, tests, Maven,
script imports, dynamic directory scopes, manifests, fingerprint maps and documentation references.
Some analysis files are actual test inputs. Many reports are themselves named in historical hashes.

Retain original source bytes, publication decisions and evidence. Run generators in a scratch output
directory when supported; several historical scripts write committed evidence at fixed paths.
Do not regenerate old reports merely to make a past baseline agree with today's implementation.
Whole-workspace batch verifiers can intentionally reject later code changes; their original scope
is not a permanent requirement to freeze the repository.

The 7.4.4A Spell capability inventory and 7.4.4C comparison have an explicit opt-in regeneration
test. Ordinary test runs only write report serialization checks to JUnit temporary directories. To
intentionally replace the three current report artifacts using the preserved 7.4.4B baseline, run
from the repository root:

```powershell
mvn -Dtest=SpellCapabilityInventoryTests#regenerateAuthoritativeReports `
    -Dmechanics.inventory.regenerate=true test
```

Review the resulting analysis diff before keeping it. The regeneration test writes report content to
`@TempDir` first and copies those outputs to the tracked paths only when this explicit property and
test selector are both supplied. To preview regeneration without replacing tracked reports, add
`-Dmechanics.inventory.reportDirectory=target/mechanics-inventory-preview`. It does not rewrite the
historical baseline.

The [consolidation report](DOCUMENTATION-CONSOLIDATION.md) and machine-readable inventories record
the exact retained paths and two original Markdown snapshots. Restore those snapshots only in a
separate historical reconstruction workspace, never over current guidance.

## Public repository review

Keep secrets and deployment-specific configuration out of new documentation. Existing hygiene
findings require a separate owner decision; this maintenance milestone does not delete source data,
rewrite Git history, change license notices, rotate credentials or alter application configuration.
The application code license is not a blanket approval for every source dataset or evidence excerpt.
