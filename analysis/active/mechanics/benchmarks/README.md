# Spell mechanics benchmarks

Progress and authorization are controlled by the [backend roadmap](../../../../docs/THE_DND_PUB_BACKEND_ROADMAP.md).
Step 7.3.1 establishes executable comparison infrastructure, not a Spell interpreter.

- [Implementation and review handoff](../../../mechanics/step-7/7.3.1-spell-benchmark-implementation.md)
- [Six versioned fixtures](../../../../src/test/resources/mechanics/benchmarks/spells/v1/)
- [Benchmark library](../../../../src/main/java/com/thedndpub/data/mechanics/benchmark/)
- [Comparator, review and corpus tests](../../../../src/test/java/com/thedndpub/data/mechanics/benchmark/)

All six committed fixtures are `PENDING_HUMAN_REVIEW`. There is **no approved gold set** and no
measured extractor accuracy. Synthetic documents in tests validate the harness only. Fixtures are
test resources and are not packaged into the application or injected into the inspection catalog.

## Reviewing a fixture

1. Open its `source.resourcePath` under `src/main/resources/` and locate `ownerPointer`.
2. Verify the Spell ID, source/page and SHA-256 against the actual pinned bytes. PHB and XPHB
   Shield must be reviewed separately.
3. Check each typed payload, subject, ordered bindings, evidence association, UTF-16 `[start,end)`
   span and structured-field hash. Spans index the decoded Java string, not JSON file bytes.
4. Review every negative rule and deferred concept, including its proposed constraint, reason and
   adjudication question. Silence is not an explicit reported abstention.
5. Resolve disputed assertions before approval. Change substantive content, increment the revision
   and recompute its content hash; never carry forward an old decision. Have another human
   adjudicate disagreements. The authoring utility cannot establish independence.
6. Record an attributable human decision against the exact fixture ID, revision/content hash and
   source hash. Configure reviewer authority separately in the evaluating caller. Do not copy the
   synthetic test authority or claim that a JSON identity authenticates a reviewer.
7. Revalidate source joins and run approved-mode evaluation only after that explicit review change.
   Initial pending-state tests must be deliberately updated in that separately authorized change.

The exact schema is the strict Java record graph rooted at `SpellBenchmarkFixture`; unknown
properties, discriminators and versions fail parsing. There is no second, divergent schema parser.

## Reproducibility

Run the focused suite from the repository root:

```shell
mvn -Dtest=SpellBenchmarkFixtureTests,BenchmarkReviewTests,BenchmarkComparisonTests test
```

Ordinary tests do not write fixtures. To regenerate the manually proposed pending artifacts:

```shell
mvn test-compile dependency:build-classpath -Dmdep.outputFile=target/benchmark-classpath.txt
```

Then run the test-only Java main `BenchmarkFixtureAuthoring --write-pending-fixtures` with
`target/test-classes`, `target/classes` and that dependency classpath. Use the platform's classpath
separator. This command is explicit, is not part of a normal Maven build, and refuses to overwrite
human-adjudicated files. It reproduces proposed assertions, never approvals. Commit any intended
changes only after reviewing their diff.
