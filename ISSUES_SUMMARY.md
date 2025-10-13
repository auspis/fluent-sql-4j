# GitHub Issues Summary for Plugin Architecture

This document provides a concise summary of all 18 issues for quick reference and GitHub issue creation.

## How to Use This Document

1. **Manual Issue Creation:** Copy each issue section into GitHub's issue form
2. **Batch Creation:** Use GitHub CLI or API with this structured data
3. **Project Planning:** Review all issues at a glance for sprint planning

---

## Issue #1: Define core SqlDialectPlugin interface

**Labels:** `enhancement`, `architecture`, `plugin-system`  
**Milestone:** Plugin Architecture - Phase 1  
**Priority:** Critical  
**Effort:** Small (1-2 hours)  
**Dependencies:** None

**Summary:** Create the foundational `SqlDialectPlugin` interface in package `lan.tlab.r4j.sql.dsl.plugin` with methods: `getDialectName()`, `getVersion()`, `createRenderer()`, `supports(String)`, `getSupportedFeatures()`.

**Deliverables:**
- Interface with 5 methods
- Comprehensive JavaDoc
- Basic unit tests

---

## Issue #2: Implement SqlDialectRegistry with ServiceLoader

**Labels:** `enhancement`, `architecture`, `plugin-system`  
**Milestone:** Plugin Architecture - Phase 1  
**Priority:** Critical  
**Effort:** Medium (3-4 hours)  
**Dependencies:** Issue #1

**Summary:** Create `SqlDialectRegistry` class with thread-safe plugin storage using `ConcurrentHashMap`, auto-discovery via `ServiceLoader`, and methods: `register()`, `getRenderer()`, `getSupportedDialects()`, `isSupported()`.

**Deliverables:**
- Registry class with ServiceLoader integration
- Thread-safe plugin management
- Comprehensive unit tests
- Concurrent access tests

---

## Issue #2.5: Add Version Specification Support

**Labels:** `enhancement`, `architecture`, `plugin-system`, `versioning`  
**Milestone:** Plugin Architecture - Phase 1  
**Priority:** High  
**Effort:** Medium (4-5 hours)  
**Dependencies:** Issues #1, #2

**Summary:** Implement semantic version handling to allow applications to specify dialect version requirements. Create `VersionSpecification` utility class supporting Maven/Gradle-style version specifications: exact (`"8.0.1"`), minimum (`"8.0.0+"`), ranges (`"[8.0.0,9.0.0)"`). Add `supportsVersion()` to `SqlDialectPlugin` interface and `forDialect(String, String)` overload to DSL class.

**Deliverables:**
- VersionSpecification utility class
- Version parsing and comparison logic
- Updated SqlDialectPlugin interface
- Updated SqlDialectRegistry with version support
- Updated DSL class with version parameter
- Comprehensive unit and integration tests

---

## Issue #3: Create StandardSQLDialectPlugin

**Labels:** `enhancement`, `plugin-system`, `dialect`  
**Milestone:** Plugin Architecture - Phase 2  
**Priority:** High  
**Effort:** Small (2-3 hours)  
**Dependencies:** Issues #1, #2

**Summary:** Create `StandardSQLDialectPlugin` in package `lan.tlab.r4j.sql.dsl.plugin.builtin` for SQL:2008 standard. Supports aliases: "standard", "sql2008", "ansi".

**Deliverables:**
- Plugin implementation
- Unit tests
- Integration tests with DSL

---

## Issue #4: Create MySQLDialectPlugin

**Labels:** `enhancement`, `plugin-system`, `dialect`, `mysql`  
**Milestone:** Plugin Architecture - Phase 2  
**Priority:** High  
**Effort:** Small (2-3 hours)  
**Dependencies:** Issues #1, #2

**Summary:** Create `MySQLDialectPlugin` in package `lan.tlab.r4j.sql.dsl.plugin.builtin` for MySQL/MariaDB. Features: backtick identifiers, LIMIT/OFFSET, CONCAT(), DATE_ADD/SUB.

**Deliverables:**
- Plugin implementation
- Unit tests
- Integration tests with Testcontainers MySQL

---

## Issue #5: Create PostgreSQLDialectPlugin

**Labels:** `enhancement`, `plugin-system`, `dialect`, `postgresql`  
**Milestone:** Plugin Architecture - Phase 2  
**Priority:** High  
**Effort:** Medium (4-5 hours)  
**Dependencies:** Issues #1, #2

**Summary:** Create `PostgreSQLDialectPlugin` in package `lan.tlab.r4j.sql.dsl.plugin.builtin` for PostgreSQL. May need to create `SqlRendererFactory.postgresql()`. Supports aliases: "postgresql", "postgres", "pg".

**Deliverables:**
- Plugin implementation
- PostgreSQL renderer (if needed)
- Unit tests
- Integration tests with Testcontainers PostgreSQL

---

## Issue #6: Create SqlServerDialectPlugin

**Labels:** `enhancement`, `plugin-system`, `dialect`, `sqlserver`  
**Milestone:** Plugin Architecture - Phase 2  
**Priority:** High  
**Effort:** Small (2-3 hours)  
**Dependencies:** Issues #1, #2

**Summary:** Create `SqlServerDialectPlugin` in package `lan.tlab.r4j.sql.dsl.plugin.builtin` for Microsoft SQL Server. Features: square brackets, TOP clause, GETDATE(), LEN(). Supports aliases: "sqlserver", "mssql", "tsql".

**Deliverables:**
- Plugin implementation
- Unit tests
- Integration tests

---

## Issue #7: Create OracleDialectPlugin (Optional/Future)

**Labels:** `enhancement`, `plugin-system`, `dialect`, `oracle`, `future`  
**Milestone:** Plugin Architecture - Phase 7 (Optional)  
**Priority:** Low  
**Effort:** Large (6-8 hours)  
**Dependencies:** Issues #1, #2

**Summary:** Create `OracleDialectPlugin` for Oracle Database. Complete the `SqlRendererFactory.oracle()` implementation. Mark as experimental/incomplete.

**Deliverables:**
- Plugin implementation
- Complete Oracle renderer
- Unit tests
- Documentation of Oracle-specific features

---

## Issue #8: Create META-INF/services Configuration

**Labels:** `enhancement`, `plugin-system`, `configuration`  
**Milestone:** Plugin Architecture - Phase 3  
**Priority:** High  
**Effort:** Small (1 hour)  
**Dependencies:** Issues #3, #4, #5, #6

**Summary:** Create ServiceLoader configuration file at `sql/src/main/resources/META-INF/services/lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin` listing all built-in plugins.

**File Contents:**

```
lan.tlab.r4j.sql.dsl.plugin.builtin.StandardSQLDialectPlugin
lan.tlab.r4j.sql.dsl.plugin.builtin.MySQLDialectPlugin
lan.tlab.r4j.sql.dsl.plugin.builtin.PostgreSQLDialectPlugin
lan.tlab.r4j.sql.dsl.plugin.builtin.SqlServerDialectPlugin
```

**Deliverables:**
- SPI configuration file
- Test validating ServiceLoader discovery
- Documentation for external plugin developers

---

## Issue #9: Refactor DSL class to support plugins

**Labels:** `enhancement`, `plugin-system`, `refactoring`  
**Milestone:** Plugin Architecture - Phase 4  
**Priority:** Critical  
**Effort:** Medium (4-5 hours)  
**Dependencies:** Issues #1-8

**Summary:** Refactor `DSL` class to support instance-based API with dialect-specific renderers while maintaining 100% backward compatibility with existing static methods.

**New Methods:**
- `forDialect(String)`, `standard()`, `mysql()`, `postgresql()`, `sqlserver()`
- Instance methods: `select()`, `selectAll()`, `createTable()`, etc.
- `getDialect()`, `getSupportedDialects()`

**Deliverables:**
- Refactored DSL class
- All existing tests pass unchanged
- New tests for instance API
- Backward compatibility tests

---

## Issue #10: Update builder classes to accept SqlRenderer

**Labels:** `enhancement`, `plugin-system`, `verification`  
**Milestone:** Plugin Architecture - Phase 4  
**Priority:** Medium  
**Effort:** Small (2-3 hours)  
**Dependencies:** Issue #9

**Summary:** Verify and update if necessary that all builder classes properly accept and use the SqlRenderer passed from DSL.

**Classes to Review:**
- SelectBuilder
- InsertBuilder
- UpdateBuilder
- DeleteBuilder
- CreateTableBuilder
- SelectProjectionBuilder

**Deliverables:**
- Verification of all builders
- Tests with different renderers
- Updates if needed

---

## Issue #11: Create plugin architecture documentation

**Labels:** `documentation`, `plugin-system`  
**Milestone:** Plugin Architecture - Phase 5  
**Priority:** High  
**Effort:** Medium (3-4 hours)  
**Dependencies:** Issues #1-10

**Summary:** Create comprehensive documentation explaining the plugin architecture, usage patterns, and how to create external plugins.

**Deliverables:**
- Update README.md with plugin usage
- Create PLUGIN_DEVELOPMENT.md guide
- JavaDoc for all public APIs
- Code examples for common scenarios
- Migration guide from old to new API
- Feature matrix by dialect
- Troubleshooting section

---

## Issue #12: Create example external plugin

**Labels:** `documentation`, `plugin-system`, `example`  
**Milestone:** Plugin Architecture - Phase 5  
**Priority:** Medium  
**Effort:** Medium (3-4 hours)  
**Dependencies:** Issue #11

**Summary:** Create a complete reference implementation of an external plugin to serve as a guide for third-party developers.

**Deliverables:**
- Example plugin (e.g., H2 or fictional dialect)
- Complete SPI configuration
- Build configuration (pom.xml)
- README with step-by-step instructions
- Packaging as separate JAR
- Integration example with main DSL

---

## Issue #13: Create integration tests for plugin system

**Labels:** `testing`, `plugin-system`, `integration`  
**Milestone:** Plugin Architecture - Phase 6  
**Priority:** High  
**Effort:** Large (6-8 hours)  
**Dependencies:** Issues #1-12

**Summary:** Create comprehensive integration tests validating the entire plugin system end-to-end with real databases using Testcontainers.

**Test Scenarios:**
- Auto-discovery of built-in plugins
- Manual plugin registration
- Dialect selection via DSL factory methods
- Concurrent access to registry
- Error cases (unknown dialect, null values)
- Plugin override
- Feature detection
- Backward compatibility
- SQL execution with different databases
- Performance benchmarks

**Deliverables:**
- Integration test suite in `test-integration` module
- Testcontainers tests for MySQL, PostgreSQL
- Performance benchmarks
- Error handling tests

---

## Issue #14: Create migration tests

**Labels:** `testing`, `plugin-system`, `backward-compatibility`  
**Milestone:** Plugin Architecture - Phase 6  
**Priority:** High  
**Effort:** Medium (3-4 hours)  
**Dependencies:** Issues #9, #10

**Summary:** Create specific tests verifying existing code continues to work without modification after plugin architecture introduction.

**Deliverables:**
- Tests using old static API patterns
- Verify all existing tests pass
- Verify SQL output is identical
- Document any behavioral changes
- Deprecation warnings if needed

---

## Issue #15: Optimize plugin registry performance

**Labels:** `performance`, `plugin-system`, `optimization`  
**Milestone:** Plugin Architecture - Phase 7  
**Priority:** Medium  
**Effort:** Medium (3-4 hours)  
**Dependencies:** Issues #1-13

**Summary:** Analyze and optimize the plugin registry for production use, ensuring minimal overhead.

**Deliverables:**
- Profile registry initialization time
- Profile renderer creation time
- Implement caching if needed
- Add metrics/logging for plugin loading
- Benchmark against baseline
- Ensure no performance regression
- Document performance characteristics

---

## Issue #16: Plugin feature detection API

**Labels:** `enhancement`, `plugin-system`, `advanced`, `future`  
**Milestone:** Plugin Architecture - Phase 8 (Optional)  
**Priority:** Low  
**Effort:** Medium (4-5 hours)  
**Dependencies:** Issues #1-13

**Summary:** Enhance the plugin system with advanced feature detection capabilities to allow runtime querying of dialect capabilities.

**Deliverables:**
- Feature enum or capability system
- Feature querying API
- Plugin feature declaration
- Utility methods for feature-based logic
- Documentation and examples

---

## Issue #17: Plugin configuration and customization

**Labels:** `enhancement`, `plugin-system`, `advanced`, `future`  
**Milestone:** Plugin Architecture - Phase 8 (Optional)  
**Priority:** Low  
**Effort:** Large (5-6 hours)  
**Dependencies:** Issues #1-13

**Summary:** Allow plugins to accept configuration parameters for fine-tuning dialect behavior.

**Deliverables:**
- Configuration API design
- Add configuration support to interface
- Runtime configuration of dialects
- Property file or programmatic configuration
- Examples of configurable plugins
- Documentation

---

## Issue #18: Multi-module plugin distribution

**Labels:** `enhancement`, `plugin-system`, `advanced`, `future`, `architecture`  
**Milestone:** Plugin Architecture - Phase 8 (Optional)  
**Priority:** Low  
**Effort:** Large (8-10 hours)  
**Dependencies:** Issues #1-13

**Summary:** Refactor plugin implementations into separate Maven modules to allow independent versioning and distribution.

**New Modules:**
- `sql-dialect-mysql`
- `sql-dialect-postgresql`
- `sql-dialect-sqlserver`

**Deliverables:**
- New Maven modules for each dialect
- Move plugin implementations
- Update parent POM
- Core module with only interface and standard dialect
- Updated build and release process
- Documentation of modular architecture

---

## Quick Statistics

|          Category           | Count | Total Effort |
|-----------------------------|-------|--------------|
| **Critical Priority**       | 3     | 9-11 hours   |
| **High Priority**           | 9     | 33-43 hours  |
| **Medium Priority**         | 3     | 9-11 hours   |
| **Low Priority (Optional)** | 4     | 23-29 hours  |
| **TOTAL**                   | 19    | 54-70 hours  |

## Phase Breakdown

|       Phase       |   Issues   | Effort |   Status    |
|-------------------|------------|--------|-------------|
| Phase 1: Core     | #1-2, #2.5 | 8-11h  | Not Started |
| Phase 2: Plugins  | #3-6, #8   | 10-13h | Not Started |
| Phase 3: DSL      | #9-10      | 6-8h   | Not Started |
| Phase 4: Docs     | #11-12     | 6-7h   | Not Started |
| Phase 5: Testing  | #13-14     | 9-12h  | Not Started |
| Phase 6: Perf     | #15        | 3-4h   | Not Started |
| Phase 7: Advanced | #16-18     | 17-23h | Optional    |

## Issue Dependencies Graph

```
#1 (Plugin Interface)
 ├─→ #2 (Registry)
 │    ├─→ #2.5 (Version Support)
 │    │    ├─→ #3 (StandardSQL Plugin)
 │    │    ├─→ #4 (MySQL Plugin)
 │    │    ├─→ #5 (PostgreSQL Plugin)
 │    │    ├─→ #6 (SqlServer Plugin)
 │    │    └─→ #7 (Oracle Plugin - Optional)
 │    │         └─→ #8 (SPI Config)
 │    │              └─→ #9 (DSL Refactor)
 │    │                   ├─→ #10 (Builder Verification)
 │    │                   │    ├─→ #11 (Documentation)
 │    │                   │    │    └─→ #12 (Example Plugin)
 │    │                   │    │         └─→ #13 (Integration Tests)
 │    │                   │    │              ├─→ #14 (Migration Tests)
 │    │                   │    │              ├─→ #15 (Performance)
 │    │                   │    │              ├─→ #16 (Feature API)
 │    │                   │    │              ├─→ #17 (Configuration)
 │    │                   │    │              └─→ #18 (Multi-module)
```

## Recommended Implementation Order

### MVP (Minimum Viable Product)

1. Issue #1 → Issue #2 → Issue #2.5
2. Issues #3, #4, #5, #6 (can be parallel)
3. Issue #8
4. Issue #9 → Issue #10
5. Issue #13

### Complete Release

1. All MVP issues
2. Issue #11 → Issue #12
3. Issue #14

### Enhanced Release (Optional)

1. All Complete Release issues
2. Issue #15
3. Issues #16, #17, #18 (can be parallel)

---

**Note:** For detailed issue descriptions with full acceptance criteria, code examples, and technical notes, see [GITHUB_ISSUES_TEMPLATE.md](GITHUB_ISSUES_TEMPLATE.md).
