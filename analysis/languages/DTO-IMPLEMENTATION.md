# Language pipeline implementation

The source file defines 201 languages and 6 script/font records. All 201
language rows mapped; all `(name, source)` identities and generated IDs were
unique. There are no generated copy/version variants.

The source representation is `LanguagesFileDte`, `LanguageDte`,
`LanguageScriptDte`, and `LanguageSourceReferenceDte`. Recursive narrative
entries reuse the existing sealed `EntryDte` representation. The application
model (`NormalizedLanguageDefinition`) contains only identity and useful
classification fields. `LanguageImportProvenance` separately retains the
remaining source fields, including prose and asset paths. `LanguageMapper`
creates both as one `LanguageImportRecord`.

`LanguageCatalog` reads the packaged classpath resource once, validates script
and language identity uniqueness, creates immutable indexes, and exposes the
small sorted summary collection. It rejects unknown source properties to
prevent silent schema loss. `LanguagesService` owns filtering and lookup.
The controller returns summaries for lists and details for ID/name-source
lookups. Since the catalog has 201 records, listing is unpaginated and sorted
by case-insensitive name, source, then stable ID.

## Coverage

Every discovered source property has a typed DTE destination. Narrative
entries, tagged typical speakers, origin, reprint links, source locators,
edition flags, image availability, and fonts remain internal provenance.
`additionalSources` and `otherSources` retain distinct fields in provenance.
Public DTOs expose only stable ID, exact name/source, source type, script label,
and dialect labels. No mechanical effects were inferred.

## Validation

- Full corpus strict DTE deserialization: 201 language definitions and 6 scripts.
- Mapping checks cover identity, classification, narrative retention in
  provenance, and exclusion from public JSON.
- Duplicate identities/IDs fail catalog construction.
- IDs are stable and source-qualified (`language:phb:abyssal`).
- Public serialization omits null and empty values.
- Service filtering and exact source/name matching work case-insensitively.
- REST tests cover filtering, ID lookup, name/source lookup, and 404 behavior.
- Resource lookup is tested from a JAR classpath and the running Spring
  application loads the actual packaged resource.
- Full Maven test suite: 57 tests passed, 0 failures, 0 errors, 0 skipped.

Known limitations: the source's `type` strings are kept verbatim because their
edition-specific semantics are not uniform. `script`, `dialects`, and reprint
values are labels/source references rather than resolved language foreign
keys. Publication rights for retained prose and font assets remain a separate
decision; neither is exposed.
