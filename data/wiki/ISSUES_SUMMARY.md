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

## Issue #5: Create PostgreSQLDialectPlugin (Optional - Future)

**Labels:** `enhancement`, `plugin-system`, `dialect`, `postgresql`, `future`  
**Milestone:** Plugin Architecture - Phase 3 (Future)  
**Priority:** Low (Optional)  
**Effort:** Medium (4-5 hours)  
**Dependencies:** Issues #1, #2

**Summary:** **[FUTURE RELEASE]** Create `PostgreSQLDialectPlugin` in package `lan.tlab.r4j.sql.dsl.plugin.builtin` for PostgreSQL. May need to create `SqlRendererFactory.postgresql()`. Supports aliases: "postgresql", "postgres", "pg".

**Deliverables:**
- Plugin implementation
- PostgreSQL renderer (if needed)
- Unit tests
- Integration tests with Testcontainers PostgreSQL

---

## Issue #6: Create SqlServerDialectPlugin (Optional - Future)

**Labels:** `enhancement`, `plugin-system`, `dialect`, `sqlserver`, `future`  
**Milestone:** Plugin Architecture - Phase 3 (Future)  
**Priority:** Low (Optional)  
**Effort:** Small (2-3 hours)  
**Dependencies:** Issues #1, #2

**Summary:** **[FUTURE RELEASE]** Create `SqlServerDialectPlugin` in package `lan.tlab.r4j.sql.dsl.plugin.builtin` for Microsoft SQL Server. Features: square brackets, TOP clause, GETDATE(), LEN(). Supports aliases: "sqlserver", "mssql", "tsql".

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
**Milestone:** Plugin Architecture - Phase 2  
**Priority:** High  
**Effort:** Small (1 hour)  
**Dependencies:** Issues #3, #4

**Summary:** Create ServiceLoader configuration files in each dialect plugin module for Standard SQL and MySQL (first release). Future releases will add PostgreSQL and SQL Server.

**File Contents for First Release:**

In `/dialect-plugins/standard/src/main/resources/META-INF/services/lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin`:

```
lan.tlab.r4j.sql.dsl.plugin.builtin.StandardSQLDialectPlugin
```

In `/dialect-plugins/mysql/src/main/resources/META-INF/services/lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin`:

```
lan.tlab.r4j.sql.dsl.plugin.builtin.MySQLDialectPlugin
```

**Note:** PostgreSQL and SQL Server configurations will be added in future releases.

**Deliverables:**
- SPI configuration files for Standard SQL and MySQL modules
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

### First Release (Standard SQL + MySQL)

|         Category          | Count | Total Effort |
|---------------------------|-------|--------------|
| **Critical Priority**     | 3     | 9-11 hours   |
| **High Priority**         | 7     | 24-29 hours  |
| **Medium Priority**       | 3     | 9-11 hours   |
| **TOTAL (First Release)** | 13    | 42-51 hours  |

### Future Releases

|          Category           | Count | Total Effort |
|-----------------------------|-------|--------------|
| **Low Priority (Optional)** | 6     | 32-44 hours  |
| **PostgreSQL & SQL Server** | 2     | 6-8 hours    |
| **Advanced Features**       | 4     | 23-29 hours  |
| **TOTAL (Future)**          | 6     | 12-19 hours  |

### Overall Project

|       Category       | Count | Total Effort |
|----------------------|-------|--------------|
| **TOTAL ALL ISSUES** | 19    | 54-70 hours  |

## Phase Breakdown

### First Release

|      Phase       |    Issues     |   Effort   |   Status    |             Notes             |
|------------------|---------------|------------|-------------|-------------------------------|
| Phase 1: Core    | #1-2, #2.5    | 8-11h      | Not Started | Foundation                    |
| Phase 2: Plugins | #3-4, #8      | 5-7h       | Not Started | **Standard SQL + MySQL only** |
| Phase 3: DSL     | #9-10         | 6-8h       | Not Started | DSL refactor                  |
| Phase 4: Docs    | #11-12        | 6-7h       | Not Started | Documentation                 |
| Phase 5: Testing | #13-14        | 9-12h      | Not Started | Integration tests             |
| Phase 6: Perf    | #15           | 3-4h       | Not Started | Optimization                  |
| **SUBTOTAL**     | **13 issues** | **37-49h** |             | **MVP Release**               |

### Future Releases

|       Phase       |    Issues    |   Effort   |  Status  |          Notes          |
|-------------------|--------------|------------|----------|-------------------------|
| Phase 2+: Plugins | #5-6         | 6-8h       | Future   | PostgreSQL + SQL Server |
| Phase 7: Advanced | #16-18       | 17-23h     | Optional | Advanced features       |
| Phase 8: Oracle   | #7           | 6-8h       | Optional | Oracle plugin           |
| **SUBTOTAL**      | **6 issues** | **29-39h** |          | **Future Work**         |

## Issue Dependencies Graph

```
#1 (Plugin Interface)
 ├─→ #2 (Registry)
 │    ├─→ #2.5 (Version Support)
 │    │    ├─→ #3 (StandardSQL Plugin) ✓ FIRST RELEASE
 │    │    ├─→ #4 (MySQL Plugin) ✓ FIRST RELEASE
 │    │    ├─→ #5 (PostgreSQL Plugin) ⏸ FUTURE
 │    │    ├─→ #6 (SqlServer Plugin) ⏸ FUTURE
 │    │    └─→ #7 (Oracle Plugin) ⏸ OPTIONAL
 │    │         └─→ #8 (SPI Config) ✓ FIRST RELEASE (Standard + MySQL only)
 │    │              └─→ #9 (DSL Refactor)
 │    │                   ├─→ #10 (Builder Verification)
 │    │                   │    ├─→ #11 (Documentation)
 │    │                   │    │    └─→ #12 (Example Plugin)
 │    │                   │    │         └─→ #13 (Integration Tests)
 │    │                   │    │              ├─→ #14 (Migration Tests)
 │    │                   │    │              ├─→ #15 (Performance)
 │    │                   │    │              ├─→ #16 (Feature API) ⏸ OPTIONAL
 │    │                   │    │              ├─→ #17 (Configuration) ⏸ OPTIONAL
 │    │                   │    │              └─→ #18 (Multi-module) ⏸ OPTIONAL
```

**Legend:**
- ✓ = First Release (Standard SQL + MySQL)
- ⏸ = Future Release / Optional

## Recommended Implementation Order

### First Release (Standard SQL + MySQL)

**Phase 1: Foundation**
1. Issue #1 (Plugin Interface)
2. Issue #2 (Registry)
3. Issue #2.5 (Version Support)

**Phase 2: Core Plugins**
4. Issue #3 (StandardSQL Plugin)
5. Issue #4 (MySQL Plugin)
6. Issue #8 (SPI Config for Standard + MySQL)

**Phase 3: DSL Integration**
7. Issue #9 (DSL Refactor)
8. Issue #10 (Builder Verification)

**Phase 4: Documentation**
9. Issue #11 (Documentation)
10. Issue #12 (Example Plugin)

**Phase 5: Testing & Polish**
11. Issue #13 (Integration Tests)
12. Issue #14 (Migration Tests)
13. Issue #15 (Performance)

**Total: 13 issues, 42-51 hours**

### Future Releases

**PostgreSQL & SQL Server Support**
1. Issue #5 (PostgreSQL Plugin)
2. Issue #6 (SQL Server Plugin)
3. Update Issue #8 (Add to SPI Config)

**Advanced Features (Optional)**
1. Issue #16 (Feature API)
2. Issue #17 (Configuration)
3. Issue #18 (Multi-module)

**Oracle Support (Optional)**
1. Issue #7 (Oracle Plugin)

**Total: 6 additional issues, 12-19 hours**

---

**Note:** For detailed issue descriptions with full acceptance criteria, code examples, and technical notes, see [GITHUB_ISSUES_TEMPLATE.md](GITHUB_ISSUES_TEMPLATE.md).
