# Copy and version analysis

The complete recursive spell directory has **zero `_copy` declarations, zero `_versions` declarations, and zero `_abstract`, `_implementations`, `_variables`, or other template roots**. Therefore there are no spell copy bases to resolve, no copy modification operations or ordering to specify, no dependency cycles, and no generated/expanded spell candidates in this corpus. All 969 records are source roots.

`reprintedAs` is present and distinct from copying: 389 authored explicit spell UID edges point to 389 targets, all resolved by exact name/source in the root corpus. `additionalSources`/`otherSources` are publication references. Neither means field inheritance. PHB-to-XPHB links remain separate definitions.

No resolver semantics should be borrowed from Monster, Race, Feat, or Class pipelines for this domain absent actual spell copy instructions. If future source revisions add directives, first define exact base identity, metadata inheritance, operation vocabulary/order, and provenance from that corpus revision; reject unknown operations and cycles rather than applying another resolver's defaults.
