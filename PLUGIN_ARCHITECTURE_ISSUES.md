# Plugin Architecture Implementation - GitHub Issues Breakdown

This document provides a detailed breakdown of GitHub issues needed to implement a plugin-based architecture for SQL dialects in the r4j framework.

## Overview

The goal is to refactor the current architecture to support a plugin system that allows:
- Extensibility for new SQL dialects
- Auto-discovery via Java ServiceLoader (SPI)
- Modular distribution of dialect implementations
- Backward compatibility with existing API
- Community contributions for new dialects

---

## Phase 1: Core Plugin Infrastructure

### Issue 1: Create SqlDialectPlugin Interface

**Title:** Define core SqlDialectPlugin interface for dialect extensibility

**Description:**
Create the foundational interface that all SQL dialect plugins must implement. This interface will define the contract for plugin registration and usage.

**Acceptance Criteria:**
- [ ] Create `SqlDialectPlugin` interface in package `lan.tlab.r4j.sql.dsl.plugin`
- [ ] Interface includes method: `String getDialectName()`
- [ ] Interface includes method: `String getVersion()`
- [ ] Interface includes method: `SqlRenderer createRenderer()`
- [ ] Interface includes method: `boolean supports(String dialectName)`
- [ ] Interface includes method: `Set<String> getSupportedFeatures()`
- [ ] Add comprehensive JavaDoc explaining each method's purpose
- [ ] Create basic unit tests for interface contract validation

**Technical Notes:**
- Place in new package: `lan.tlab.r4j.sql.dsl.plugin`
- Follow existing coding standards (no var, immutable where possible)
- Use Java 21 features where appropriate

**Dependencies:** None

**Estimated Effort:** Small (1-2 hours)

---

### Issue 2: Implement SqlDialectRegistry with ServiceLoader

**Title:** Create plugin registry with automatic discovery via ServiceLoader

**Description:**
Implement a centralized registry that manages all SQL dialect plugins and provides auto-discovery using Java's ServiceLoader mechanism.

**Acceptance Criteria:**
- [ ] Create `SqlDialectRegistry` class in package `lan.tlab.r4j.sql.dsl.plugin`
- [ ] Implement thread-safe plugin storage using `ConcurrentHashMap`
- [ ] Implement static initialization block with `ServiceLoader` for auto-discovery
- [ ] Implement `register(SqlDialectPlugin plugin)` method
- [ ] Implement `getRenderer(String dialect)` method with proper error handling
- [ ] Implement `getSupportedDialects()` method returning immutable set
- [ ] Implement `isSupported(String dialect)` method
- [ ] Handle case-insensitive dialect name matching
- [ ] Create comprehensive unit tests with mock plugins
- [ ] Test concurrent access scenarios

**Technical Notes:**
- Use `ConcurrentHashMap` for thread safety
- Dialect names should be case-insensitive (store as lowercase)
- Throw `IllegalArgumentException` for unsupported dialects with helpful message
- Consider lazy initialization if performance is a concern

**Dependencies:** Issue 1 (SqlDialectPlugin interface)

**Estimated Effort:** Medium (3-4 hours)

---

## Phase 2: Built-in Dialect Plugins

### Issue 3: Create StandardSQLDialectPlugin for SQL:2008

**Title:** Implement built-in plugin for Standard SQL:2008 dialect in separate Maven module

**Description:**
Create the first built-in plugin for the standard SQL:2008 dialect in a dedicated Maven module under `/dialect-plugins/standard`.

**Acceptance Criteria:**
- [ ] Create new Maven module `/dialect-plugins/standard`
- [ ] Create `StandardSQLDialectPlugin` class in package `lan.tlab.r4j.sql.dsl.plugin.builtin`
- [ ] Implement all `SqlDialectPlugin` interface methods
- [ ] Return "sql2008" as dialect name
- [ ] Return appropriate version string
- [ ] Delegate to `SqlRendererFactory.standardSql2008()` for renderer creation
- [ ] Support aliases: "standard", "sql2008", "ansi"
- [ ] Define supported features set (standard SQL operations)
- [ ] Create META-INF/services configuration in module
- [ ] Create unit tests verifying plugin behavior
- [ ] Create integration tests using the plugin with DSL

**Technical Notes:**
- Reuse existing `SqlRendererFactory.standardSql2008()` method
- Document supported features clearly
- Module should depend on `sql` core module

**Dependencies:** Issues 1, 2

**Estimated Effort:** Small (2-3 hours)

---

### Issue 4: Create MySQLDialectPlugin

**Title:** Implement built-in plugin for MySQL dialect in separate Maven module

**Description:**
Create a plugin for MySQL in a dedicated Maven module under `/dialect-plugins/mysql`.

**Acceptance Criteria:**
- [ ] Create new Maven module `/dialect-plugins/mysql`
- [ ] Create `MySQLDialectPlugin` class in package `lan.tlab.r4j.sql.dsl.plugin.builtin`
- [ ] Implement all `SqlDialectPlugin` interface methods
- [ ] Return "mysql" as dialect name
- [ ] Return MySQL version string (e.g., "8.0")
- [ ] Delegate to `SqlRendererFactory.mysql()` for renderer creation
- [ ] Support aliases: "mysql", "mariadb"
- [ ] Define MySQL-specific features: "limit", "offset", "backtick-identifiers", "auto-increment"
- [ ] Create META-INF/services configuration in module
- [ ] Create unit tests verifying plugin behavior
- [ ] Create integration tests with actual MySQL syntax validation

**Technical Notes:**
- Reuse existing `SqlRendererFactory.mysql()` method
- Document MySQL-specific features and version compatibility
- Consider MariaDB compatibility
- Module should depend on `sql` core module

**Dependencies:** Issues 1, 2

**Estimated Effort:** Small (2-3 hours)

---

### Issue 5: Create PostgreSQLDialectPlugin

**Title:** Implement built-in plugin for PostgreSQL dialect in separate Maven module

**Description:**
Create a plugin for PostgreSQL in a dedicated Maven module under `/dialect-plugins/postgresql`.

**Acceptance Criteria:**
- [ ] Create new Maven module `/dialect-plugins/postgresql`
- [ ] Create `PostgreSQLDialectPlugin` class in package `lan.tlab.r4j.sql.dsl.plugin.builtin`
- [ ] Implement all `SqlDialectPlugin` interface methods
- [ ] Return "postgresql" as dialect name
- [ ] Return PostgreSQL version string
- [ ] Create `SqlRendererFactory.postgresql()` method if needed
- [ ] Support aliases: "postgresql", "postgres", "pg"
- [ ] Define PostgreSQL-specific features
- [ ] Create META-INF/services configuration in module
- [ ] Create unit tests verifying plugin behavior
- [ ] Create integration tests (may need Testcontainers with PostgreSQL)

**Technical Notes:**
- May need to create new renderer configuration in `SqlRendererFactory`
- Research PostgreSQL-specific syntax differences (e.g., LIMIT/OFFSET, string concatenation, etc.)
- Consider using standard renderer with minimal modifications
- Module should depend on `sql` core module

**Dependencies:** Issues 1, 2

**Estimated Effort:** Medium (4-5 hours)

---

### Issue 6: Create SqlServerDialectPlugin

**Title:** Implement built-in plugin for SQL Server dialect in separate Maven module

**Description:**
Create a plugin for Microsoft SQL Server in a dedicated Maven module under `/dialect-plugins/sqlserver`.

**Acceptance Criteria:**
- [ ] Create new Maven module `/dialect-plugins/sqlserver`
- [ ] Create `SqlServerDialectPlugin` class in package `lan.tlab.r4j.sql.dsl.plugin.builtin`
- [ ] Implement all `SqlDialectPlugin` interface methods
- [ ] Return "sqlserver" as dialect name
- [ ] Return SQL Server version string
- [ ] Delegate to `SqlRendererFactory.sqlServer()` for renderer creation
- [ ] Support aliases: "sqlserver", "mssql", "tsql"
- [ ] Define SQL Server-specific features
- [ ] Create META-INF/services configuration in module
- [ ] Create unit tests verifying plugin behavior
- [ ] Create integration tests

**Technical Notes:**
- Reuse existing `SqlRendererFactory.sqlServer()` method
- Document T-SQL specific features
- Module should depend on `sql` core module

**Dependencies:** Issues 1, 2

**Estimated Effort:** Small (2-3 hours)

---

### Issue 7: Create OracleDialectPlugin (Optional/Future)

**Title:** Implement built-in plugin for Oracle dialect

**Description:**
Create a plugin for Oracle Database. The existing Oracle renderer is incomplete (marked as TODO), so this plugin should be created as a placeholder for future work.

**Acceptance Criteria:**
- [ ] Create `OracleDialectPlugin` class in package `lan.tlab.r4j.sql.dsl.plugin.builtin`
- [ ] Implement all `SqlDialectPlugin` interface methods
- [ ] Return "oracle" as dialect name
- [ ] Complete the `SqlRendererFactory.oracle()` implementation
- [ ] Support aliases: "oracle", "oracledb"
- [ ] Define Oracle-specific features
- [ ] Create unit tests
- [ ] Mark as experimental/incomplete in documentation

**Technical Notes:**
- May require significant research for Oracle-specific syntax
- Consider making this a separate, optional module in the future

**Dependencies:** Issues 1, 2

**Estimated Effort:** Large (6-8 hours or more)

---

## Phase 3: Service Provider Interface Configuration

### Issue 8: Create META-INF/services Configuration

**Title:** Set up SPI configuration for auto-discovery of built-in plugins in each module

**Description:**
Create the ServiceLoader configuration files in each dialect plugin Maven module that enable automatic discovery of built-in dialect plugins.

**Acceptance Criteria:**
- [ ] Create META-INF/services configuration in `/dialect-plugins/standard` module
- [ ] Create META-INF/services configuration in `/dialect-plugins/mysql` module
- [ ] Create META-INF/services configuration in `/dialect-plugins/postgresql` module
- [ ] Create META-INF/services configuration in `/dialect-plugins/sqlserver` module
- [ ] Each file should be `lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin`
- [ ] Each file lists only its own plugin implementation
- [ ] Verify ServiceLoader can discover plugins at runtime
- [ ] Create test to validate SPI configuration
- [ ] Document SPI configuration format for external plugin developers

**File Contents (per module):**

In `/dialect-plugins/standard/src/main/resources/META-INF/services/lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin`:

```
lan.tlab.r4j.sql.dsl.plugin.builtin.StandardSQLDialectPlugin
```

In `/dialect-plugins/mysql/src/main/resources/META-INF/services/lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin`:

```
lan.tlab.r4j.sql.dsl.plugin.builtin.MySQLDialectPlugin
```

(Similar pattern for postgresql and sqlserver modules)

**Technical Notes:**
- Ensure file encoding is UTF-8
- One fully qualified class name per line
- Lines starting with # are comments

**Dependencies:** Issues 3, 4, 5, 6

**Estimated Effort:** Small (1 hour)

---

## Phase 4: DSL Refactoring

### Issue 9: Refactor DSL Class to Support Plugins

**Title:** Refactor DSL class to support plugin-based dialect selection

**Description:**
Refactor the main DSL class to support the plugin-based architecture with dynamic dialect selection via `forDialect()` method.

**Acceptance Criteria:**
- [ ] Add instance fields: `SqlRenderer sqlRenderer` and `String dialectName`
- [ ] Add private constructor: `DSL(SqlRenderer sqlRenderer, String dialectName)`
- [ ] Implement `forDialect(String dialectName)` factory method using registry
- [ ] Add instance methods: `select()`, `selectAll()`, `createTable()`, `insertInto()`, `deleteFrom()`, `update()`
- [ ] Implement `getDialect()` method
- [ ] Implement static `getSupportedDialects()` method
- [ ] Create comprehensive unit tests
- [ ] Remove old static convenience methods

**Technical Notes:**
- Only `forDialect()` method for instantiation
- Instance methods should use the configured renderer
- Clean, simple API without convenience methods

**Dependencies:** Issues 1-8

**Estimated Effort:** Medium (3-4 hours)

---

### Issue 10: Update Builder Classes to Accept SqlRenderer

**Title:** Ensure all builder classes support custom SqlRenderer instances

**Description:**
Verify and update if necessary that all builder classes (SelectBuilder, InsertBuilder, etc.) properly accept and use the SqlRenderer passed from DSL.

**Acceptance Criteria:**
- [ ] Review `SelectBuilder` constructor
- [ ] Review `InsertBuilder` constructor
- [ ] Review `UpdateBuilder` constructor
- [ ] Review `DeleteBuilder` constructor
- [ ] Review `CreateTableBuilder` constructor
- [ ] Review `SelectProjectionBuilder` constructor
- [ ] Ensure all builders store and use the provided renderer
- [ ] Create tests with different renderers to verify correct behavior
- [ ] Update any builder that doesn't properly support custom renderers

**Technical Notes:**
- Most builders already accept SqlRenderer in constructor
- Focus on verification and testing
- Ensure no builders have hardcoded renderer references

**Dependencies:** Issue 9

**Estimated Effort:** Small (2-3 hours)

---

## Phase 5: Documentation and Examples

### Issue 11: Create Plugin Architecture Documentation

**Title:** Document plugin architecture and usage patterns

**Description:**
Create comprehensive documentation explaining the plugin architecture, how to use it, and how to create external plugins.

**Acceptance Criteria:**
- [ ] Update README.md with plugin usage examples
- [ ] Create PLUGIN_DEVELOPMENT.md guide for external plugin developers
- [ ] Document all public APIs with JavaDoc
- [ ] Create example code for:
- Basic dialect selection
- Multi-database usage
- Creating external plugins
- Registering custom plugins
- [ ] Document ServiceLoader SPI requirements
- [ ] Create migration guide from old API to new API
- [ ] Document supported features by dialect
- [ ] Add troubleshooting section

**Technical Notes:**
- Use clear, runnable code examples
- Explain instance-based usage with `forDialect()`
- Provide complete external plugin example

**Dependencies:** Issues 1-10

**Estimated Effort:** Medium (3-4 hours)

---

### Issue 12: Create Example External Plugin

**Title:** Create reference implementation of external plugin

**Description:**
Create a complete example of an external plugin to serve as a reference for third-party developers.

**Acceptance Criteria:**
- [ ] Create example plugin in `spike` module or separate demo directory
- [ ] Implement fictional dialect (e.g., "h2" or "example") as demonstration
- [ ] Include complete SPI configuration
- [ ] Include build configuration (pom.xml)
- [ ] Include README with step-by-step instructions
- [ ] Show how to package as separate JAR
- [ ] Show how to test plugin in isolation
- [ ] Demonstrate integration with main DSL

**Technical Notes:**
- Keep example simple but complete
- Show best practices for plugin development
- Include error handling examples

**Dependencies:** Issue 11

**Estimated Effort:** Medium (3-4 hours)

---

## Phase 6: Testing and Validation

### Issue 13: Create Integration Tests for Plugin System

**Title:** Comprehensive integration tests for plugin architecture

**Description:**
Create thorough integration tests that validate the entire plugin system working end-to-end.

**Acceptance Criteria:**
- [ ] Test auto-discovery of built-in plugins
- [ ] Test manual plugin registration
- [ ] Test dialect selection via DSL factory methods
- [ ] Test concurrent access to registry
- [ ] Test error cases (unknown dialect, null values, etc.)
- [ ] Test plugin override (registering over existing dialect)
- [ ] Test feature detection across dialects
- [ ] Test backward compatibility with existing code
- [ ] Use Testcontainers to validate actual SQL execution with different databases
- [ ] Create performance benchmarks

**Technical Notes:**
- Place in `test-integration` module
- Use Testcontainers for MySQL, PostgreSQL testing
- Consider using H2 for SQL Server emulation if needed

**Dependencies:** Issues 1-12

**Estimated Effort:** Large (6-8 hours)

---

### Issue 14: Create Multi-Dialect Integration Tests

**Title:** Create integration tests for multi-dialect support

**Description:**
Create tests that verify the plugin system works correctly with multiple dialects simultaneously.

**Acceptance Criteria:**
- [ ] Create tests for multiple dialect usage in same application
- [ ] Test dialect-specific SQL generation
- [ ] Verify plugin isolation (one dialect doesn't affect another)
- [ ] Test dynamic dialect switching
- [ ] Document multi-dialect usage patterns

**Technical Notes:**
- Focus on multi-dialect scenarios
- Use Testcontainers for real database testing

**Dependencies:** Issues 9, 10

**Estimated Effort:** Medium (3-4 hours)

---

## Phase 7: Performance and Optimization

### Issue 15: Optimize Plugin Registry Performance

**Title:** Performance optimization and caching for plugin registry

**Description:**
Analyze and optimize the plugin registry for production use, ensuring minimal overhead.

**Acceptance Criteria:**
- [ ] Profile registry initialization time
- [ ] Profile renderer creation time
- [ ] Implement caching if needed
- [ ] Add metrics/logging for plugin loading
- [ ] Benchmark against baseline (current implementation)
- [ ] Ensure no performance regression
- [ ] Document performance characteristics

**Technical Notes:**
- Use JMH for microbenchmarks
- Consider lazy loading vs eager loading tradeoffs
- Cache frequently used renderers if beneficial

**Dependencies:** Issues 1-13

**Estimated Effort:** Medium (3-4 hours)

---

## Phase 8: Advanced Features (Future/Optional)

### Issue 16: Plugin Feature Detection API

**Title:** Implement advanced feature detection and capability queries

**Description:**
Enhance the plugin system with advanced feature detection capabilities to allow runtime querying of dialect capabilities.

**Acceptance Criteria:**
- [ ] Design Feature enum or capability system
- [ ] Implement feature querying API
- [ ] Allow plugins to declare supported SQL features
- [ ] Create utility methods for feature-based logic
- [ ] Document feature detection patterns
- [ ] Create examples of conditional SQL generation based on features

**Technical Notes:**
- Consider using enums for common features
- Allow custom feature strings for plugin-specific capabilities
- May want versioning support for features

**Dependencies:** Issues 1-13

**Estimated Effort:** Medium (4-5 hours)

---

### Issue 17: Plugin Configuration and Customization

**Title:** Add configuration support for plugin customization

**Description:**
Allow plugins to accept configuration parameters for fine-tuning dialect behavior.

**Acceptance Criteria:**
- [ ] Design configuration API
- [ ] Add configuration support to SqlDialectPlugin interface
- [ ] Allow runtime configuration of dialect behavior
- [ ] Support property files or programmatic configuration
- [ ] Create examples of configurable plugins
- [ ] Document configuration patterns

**Technical Notes:**
- Consider using Builder pattern for configuration
- Allow both global and per-instance configuration
- Think about thread safety

**Dependencies:** Issues 1-13

**Estimated Effort:** Large (5-6 hours)

---

### Issue 18: Multi-Module Plugin Distribution

**Title:** Create separate Maven modules for dialect plugins

**Description:**
Refactor plugin implementations into separate Maven modules to allow independent versioning and distribution.

**Acceptance Criteria:**
- [ ] Create new Maven module: `sql-dialect-mysql`
- [ ] Create new Maven module: `sql-dialect-postgresql`
- [ ] Create new Maven module: `sql-dialect-sqlserver`
- [ ] Move plugin implementations to respective modules
- [ ] Update parent POM
- [ ] Update dependencies
- [ ] Ensure core module works with optional dialect modules
- [ ] Update build and release process
- [ ] Document modular architecture

**Technical Notes:**
- Core module should include only interface and standard dialect
- Each dialect can be an optional dependency
- Consider OSGi compatibility for future

**Dependencies:** Issues 1-13

**Estimated Effort:** Large (8-10 hours)

---

## Summary

**Total Issues:** 18
**Priority Order:**
1. Issues 1-2: Core infrastructure (CRITICAL)
2. Issues 3-6: Built-in plugins (HIGH)
3. Issue 8: SPI configuration (HIGH)
4. Issues 9-10: DSL refactoring (HIGH)
5. Issue 11-12: Documentation (MEDIUM)
6. Issues 13-14: Testing (MEDIUM)
7. Issue 15: Performance (MEDIUM)
8. Issues 16-18: Advanced features (LOW/FUTURE)

**Minimum Viable Product:** Issues 1-10, 13
**Complete Initial Release:** Issues 1-14
**Enhanced Release:** Issues 1-18

---

## Implementation Order Recommendation

### Sprint 1: Foundation (Issues 1-2)

- Create plugin interface and registry
- Establish core architecture

### Sprint 2: Built-in Plugins (Issues 3-6, 8)

- Implement standard, MySQL, PostgreSQL, SQL Server plugins
- Configure SPI

### Sprint 3: DSL Integration (Issues 9-10)

- Refactor DSL class
- Update builders

### Sprint 4: Documentation & Testing (Issues 11-14)

- Create comprehensive documentation
- Build integration and migration tests

### Sprint 5: Optimization & Advanced Features (Issues 15-18)

- Performance tuning
- Optional advanced features

---

## Notes for Implementation

1. **Code Style:** Follow existing conventions (no var, Java 21, immutable structures)
2. **Testing:** Always run `./mvnw spotless:apply` before committing
3. **Documentation:** Update JavaDoc and README as you go
4. **Backward Compatibility:** Critical requirement - never break existing code
5. **Thread Safety:** Registry must be thread-safe
6. **Error Messages:** Provide helpful error messages for debugging
7. **Extensibility:** Design for external plugin developers

