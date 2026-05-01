# Release Notes - v1.4.0

Release date: 2026-05-01

## Highlights

- 01be70c5 - feat: introduce build hooks for SQL statement preparation (#200) (Auspis)

## Included Commits

- d9038d82 - Updates 202600501 (#205) (Auspis)
- 01be70c5 - feat: introduce build hooks for SQL statement preparation (#200) (Auspis)

## Full Changelog

- https://github.com/auspis/fluent-sql-4j/compare/v1.3.4...v1.4.0

## Breaking Changes and Migration Guide

### Release Context

This branch introduces the Build Hook architecture for SQL statement preparation and refactors dialect resolution responsibilities.

### Who Is Impacted

- Plugin authors implementing custom SqlDialectPlugin instances.
- Integrators using low-level dialect registry APIs (SqlDialectPluginRegistry) to directly obtain runtime artifacts.

Application code that only uses DSLRegistry.createWithServiceLoader() and dslFor(...) is mostly unaffected.

### Breaking Changes

#### 1) SqlDialectPlugin factory signature changed

##### What changed

The plugin DSL factory changed from a no-arg supplier style to a function that receives a BuildHookFactory.

##### Before

new SqlDialectPlugin("mysql", "^8.0.0", () -> createDsl())

plugin.createDSL()

##### After

new SqlDialectPlugin("mysql", "^8.0.0", hookFactory -> createDsl(hookFactory))

plugin.createDSL(hookFactory)

##### Why

This enables hook policy injection at DSL construction time and keeps hook behavior consistent across dialects.

#### 2) SqlDialectPluginRegistry is now metadata-focused

##### What changed

Registry responsibilities were reduced to plugin lookup and matching only.
Direct runtime materialization patterns from the registry were removed/refactored.

New flow:
1. Resolve plugin metadata with registry.
2. Materialize DSL through SqlDialectResolver with explicit BuildHookFactory.

##### Before (conceptual)

registry.getDsl(dialect, version)
registry.getSpecFactory(dialect, version)

##### After

SqlDialectResolver resolver = new SqlDialectResolver(pluginRegistry, hookFactory)
Result<DSL> dsl = resolver.resolve(dialect, version)

##### Why

This cleanly separates:
- plugin discovery and matching
- runtime DSL creation with hook policy
- caching (in DSLRegistry)

### Migration Guide

#### Step 1: Update custom plugin definitions

Replace no-arg DSL suppliers with hook-aware factories.

Old pattern:
SqlDialectPlugin plugin = new SqlDialectPlugin(
"customdb",
"^1.0.0",
() -> new CustomDSL(new PreparedStatementSpecFactory(visitor))
)

New pattern:
SqlDialectPlugin plugin = new SqlDialectPlugin(
"customdb",
"^1.0.0",
hookFactory -> new CustomDSL(new PreparedStatementSpecFactory(visitor, hookFactory))
)

#### Step 2: Replace direct registry runtime materialization with resolver

If your code expected runtime artifacts directly from the registry, move to resolver-based materialization.

SqlDialectPluginRegistry pluginRegistry = SqlDialectPluginRegistry.createWithServiceLoader()
BuildHookFactory hookFactory = BuildHookFactory.nullObject()  // or ServiceLoaderBuildHookFactory/custom
SqlDialectResolver resolver = new SqlDialectResolver(pluginRegistry, hookFactory)
Result<DSL> result = resolver.resolve("customdb", "1.2.3")

#### Step 3: Prefer high-level entrypoint when possible

If you do not need low-level control, use:
DSLRegistry registry = DSLRegistry.createWithServiceLoader()
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow()

Or use programmatic hook injection:
DSLRegistry registry = DSLRegistry.create(customHookFactory)

#### Step 4: Validate tests and SPI wiring

- Update plugin unit tests to pass a BuildHookFactory when creating DSL.
- Keep ServiceLoader tests for plugin discovery.
- Add or adjust tests for hook-aware behavior if your plugin customizes statement creation.

### Compatibility Notes

- Build hook internal error logging toggle was introduced as property:
  - fluentsql.hooks.build.internal-errors.enabled (default false)
- This is additive and not a breaking API change.

### Quick Migration Checklist

- [ ] Replace plugin supplier with hook-aware factory (Function<BuildHookFactory, DSL>).
- [ ] Pass BuildHookFactory to plugin.createDSL(...).
- [ ] Move direct registry runtime materialization code to SqlDialectResolver.
- [ ] Keep or adopt DSLRegistry for high-level usage and caching.
- [ ] Update tests accordingly.

