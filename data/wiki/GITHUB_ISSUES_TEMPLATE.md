# GitHub Issues Templates for Plugin Architecture

This document contains templates for creating GitHub issues. Each template can be copied directly into GitHub's issue creation form.

---

## Issue #1: Define core SqlDialectPlugin interface

**Labels:** enhancement, architecture, plugin-system  
**Milestone:** Plugin Architecture - Phase 1  
**Assignees:** (assign as needed)

### Description

Create the foundational interface that all SQL dialect plugins must implement. This interface will define the contract for plugin registration and usage within the r4j framework.

### Motivation

To support an extensible plugin architecture that allows the community to contribute new SQL dialect implementations, we need a clear interface that defines what a dialect plugin must provide.

### Acceptance Criteria

- [ ] Create `SqlDialectPlugin` interface in package `lan.tlab.r4j.sql.dsl.plugin`
- [ ] Interface includes method: `String getDialectName()` - returns the canonical name of the dialect
- [ ] Interface includes method: `String getVersion()` - returns the version of the dialect supported
- [ ] Interface includes method: `SqlRenderer createRenderer()` - creates a configured renderer for this dialect
- [ ] Interface includes method: `boolean supports(String dialectName)` - checks if this plugin supports the given dialect name (including aliases)
- [ ] Interface includes method: `Set<String> getSupportedFeatures()` - returns a set of feature identifiers this dialect supports
- [ ] Add comprehensive JavaDoc explaining each method's purpose and expected behavior
- [ ] Create basic unit tests demonstrating interface contract

### Technical Notes

- Place in new package: `lan.tlab.r4j.sql.dsl.plugin`
- Follow existing coding standards (no var keyword, prefer immutable structures)
- Use Java 21 features where appropriate
- Interface should be focused and minimal

### Example Implementation Structure

```java
package lan.tlab.r4j.sql.dsl.plugin;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import java.util.Set;

/**
 * Interface for SQL dialect plugins.
 * Implementations provide SQL-dialect-specific rendering capabilities.
 */
public interface SqlDialectPlugin {
    /**
     * Returns the canonical name of this dialect.
     * @return dialect name in lowercase (e.g., "mysql", "postgresql")
     */
    String getDialectName();
    
    /**
     * Returns the version of the SQL dialect this plugin supports.
     * @return version string (e.g., "8.0", "14.0")
     */
    String getVersion();
    
    /**
     * Creates a new SqlRenderer configured for this dialect.
     * @return configured SqlRenderer instance
     */
    SqlRenderer createRenderer();
    
    /**
     * Checks if this plugin supports the given dialect name.
     * This allows for dialect aliases (e.g., "postgres" -> "postgresql").
     * @param dialectName the dialect name to check
     * @return true if this plugin supports the dialect
     */
    boolean supports(String dialectName);
    
    /**
     * Returns the set of SQL features supported by this dialect.
     * @return immutable set of feature identifiers
     */
    Set<String> getSupportedFeatures();
}
```

### Dependencies

None - this is the foundation

### Estimated Effort

Small (1-2 hours)

---

## Issue #2: Implement SqlDialectRegistry with ServiceLoader

**Labels:** enhancement, architecture, plugin-system  
**Milestone:** Plugin Architecture - Phase 1  
**Assignees:** (assign as needed)

### Description

Implement a centralized registry that manages all SQL dialect plugins and provides auto-discovery using Java's ServiceLoader mechanism (SPI).

### Motivation

We need a central registry to:
1. Automatically discover and load plugins via Java SPI
2. Provide thread-safe access to registered plugins
3. Enable dynamic plugin registration for testing and extensibility
4. Offer a clean API for dialect lookup

### Acceptance Criteria

- [ ] Create `SqlDialectRegistry` class in package `lan.tlab.r4j.sql.dsl.plugin`
- [ ] Implement thread-safe plugin storage using `ConcurrentHashMap<String, SqlDialectPlugin>`
- [ ] Implement static initialization block with `ServiceLoader<SqlDialectPlugin>` for auto-discovery
- [ ] Implement `public static void register(SqlDialectPlugin plugin)` method
- [ ] Implement `public static SqlRenderer getRenderer(String dialect)` method with proper error handling
- [ ] Implement `public static Set<String> getSupportedDialects()` method returning immutable set
- [ ] Implement `public static boolean isSupported(String dialect)` method
- [ ] Handle case-insensitive dialect name matching (store keys as lowercase)
- [ ] Throw `IllegalArgumentException` with helpful message for unsupported dialects
- [ ] Create comprehensive unit tests with mock plugins
- [ ] Test concurrent access scenarios
- [ ] Test duplicate registration handling

### Technical Notes

- Use `ConcurrentHashMap` for thread safety
- Dialect names should be case-insensitive (normalize to lowercase when storing/retrieving)
- Consider what happens if a plugin is registered multiple times for the same dialect (override or throw exception?)
- ServiceLoader should run in static initializer to ensure plugins are loaded before first use

### Example Implementation Structure

```java
package lan.tlab.r4j.sql.dsl.plugin;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SqlDialectRegistry {
    private static final ConcurrentHashMap<String, SqlDialectPlugin> plugins = new ConcurrentHashMap<>();
    
    static {
        // Auto-discovery via ServiceLoader
        ServiceLoader<SqlDialectPlugin> loader = ServiceLoader.load(SqlDialectPlugin.class);
        loader.forEach(SqlDialectRegistry::register);
    }
    
    private SqlDialectRegistry() {
        // Utility class - prevent instantiation
    }
    
    public static void register(SqlDialectPlugin plugin) {
        // Implementation
    }
    
    public static SqlRenderer getRenderer(String dialect) {
        // Implementation - throw IllegalArgumentException if not found
    }
    
    public static Set<String> getSupportedDialects() {
        // Return immutable copy
    }
    
    public static boolean isSupported(String dialect) {
        // Implementation
    }
}
```

### Test Scenarios

1. Auto-discovery loads plugins correctly
2. Manual registration works
3. Case-insensitive lookup works (MySQL, mysql, MYSQL all work)
4. Unknown dialect throws appropriate exception with helpful message
5. getSupportedDialects returns all registered dialects
6. Concurrent access from multiple threads works correctly
7. Plugin can support multiple dialect names via supports() method

### Dependencies

- Issue #1 (SqlDialectPlugin interface)

### Estimated Effort

Medium (3-4 hours)

---

## Issue #2.5: Add Version Specification Support

**Labels:** enhancement, architecture, plugin-system, versioning  
**Milestone:** Plugin Architecture - Phase 1  
**Assignees:** (assign as needed)

### Description

Implement semantic version handling to allow applications to specify dialect version requirements. This enables compatibility checking and ensures the correct plugin version is selected.

### Motivation

Different database versions may have different SQL syntax or feature support. Applications need to be able to specify version constraints to ensure compatibility with their target database version.

### Acceptance Criteria

- [ ] Create `VersionSpecification` utility class in package `lan.tlab.r4j.sql.dsl.plugin.util`
- [ ] Support version specification formats:
  - Exact version: `"8.0.1"`
  - Minimum version shorthand: `"8.0.0+"`
  - Minimum version Maven-style: `"[8.0.0,)"`
  - Maximum version: `"(,9.0.0)"`
  - Version ranges: `"[8.0.0,9.0.0)"`, `"(8.0.0,8.5.0]"`
- [ ] Implement `VersionSpecification.parse(String spec)` static method
- [ ] Implement `boolean isSatisfiedBy(String version)` method
- [ ] Add `boolean supportsVersion(String versionSpec)` method to `SqlDialectPlugin` interface
- [ ] Update `SqlDialectRegistry` with `getRenderer(String dialect, String versionSpec)` method
- [ ] Update `SqlDialectRegistry` with `isSupported(String dialect, String versionSpec)` method
- [ ] Add `forDialect(String dialectName, String versionSpec)` overload to `DSL` class
- [ ] Add `String dialectVersion` field to `DSL` class
- [ ] Add `getDialectVersion()` method to `DSL` class
- [ ] Implement semantic version comparison (MAJOR.MINOR.PATCH)
- [ ] Create comprehensive unit tests for version parsing and matching
- [ ] Create integration tests with various version specifications

### Technical Notes

- Follow Maven/Gradle version specification conventions
- Use semantic versioning (semver) for comparisons
- Handle inclusive `[` and exclusive `(` boundary notation
- Throw `IllegalArgumentException` for invalid version specifications
- Default behavior when no version specified: use latest available
- VersionSpecification utility class must be final with private constructor
- All parsing and comparison methods should be static
- Consider using a well-tested library for version comparison if available

### Example Implementation

```java
package lan.tlab.r4j.sql.dsl.plugin.util;

/**
 * Utility for parsing and matching semantic version specifications.
 * Supports Maven/Gradle-style version ranges.
 */
public final class VersionSpecification {
    private VersionSpecification() {
        // Utility class
    }
    
    /**
     * Parses a version specification string.
     * Supported formats:
     * - Exact: "8.0.1"
     * - Minimum: "8.0.0+", "[8.0.0,)"
     * - Maximum: "(,9.0.0)"
     * - Range: "[8.0.0,9.0.0)", "(8.0.0,8.5.0]"
     */
    public static VersionSpecification parse(String spec) {
        // Implementation
    }
    
    /**
     * Checks if a version satisfies this specification.
     */
    public boolean isSatisfiedBy(String version) {
        // Implementation
    }
}
```

### Usage Examples

```java
// Exact version
DSL dsl = DSL.forDialect("mysql", "8.0.1");

// Minimum version (8.0 or higher)
DSL dsl = DSL.forDialect("mysql", "8.0.0+");
DSL dsl = DSL.forDialect("mysql", "[8.0.0,)");

// Version range (8.x versions only)
DSL dsl = DSL.forDialect("mysql", "[8.0.0,9.0.0)");

// Latest version (default)
DSL dsl = DSL.forDialect("mysql");

// Check version
String version = dsl.getDialectVersion(); // e.g., "8.0.30"
```

### Test Scenarios

1. Parse exact version specification
2. Parse minimum version with `+` shorthand
3. Parse minimum version with `[x,)` notation
4. Parse version ranges with inclusive/exclusive boundaries
5. Version comparison: 8.0.1 satisfies "8.0.0+"
6. Version comparison: 8.0.1 satisfies "[8.0.0,9.0.0)"
7. Version comparison: 8.0.1 does not satisfy "9.0.0+"
8. Invalid version specification throws exception
9. Plugin with version 8.0.30 matches various specifications
10. Multiple plugins with different versions, select correct one

### Dependencies

- Issue #1 (SqlDialectPlugin interface)
- Issue #2 (SqlDialectRegistry)

### Estimated Effort

Medium (4-5 hours)

---

## Issue #3: Create StandardSQLDialectPlugin

**Labels:** enhancement, plugin-system, dialect  
**Milestone:** Plugin Architecture - Phase 2  
**Assignees:** (assign as needed)

### Description

Create the first built-in plugin for the standard SQL:2008 dialect, which is currently the default in the system.

### Motivation

- Provide a reference implementation for other plugin developers
- Maintain the existing standard SQL functionality within the new plugin architecture
- Ensure backward compatibility

### Acceptance Criteria

- [ ] Create `StandardSQLDialectPlugin` class in package `lan.tlab.r4j.sql.dsl.plugin.builtin`
- [ ] Implement all `SqlDialectPlugin` interface methods
- [ ] `getDialectName()` returns "sql2008"
- [ ] `getVersion()` returns "2008" or appropriate version string
- [ ] `createRenderer()` delegates to `SqlRendererFactory.standardSql2008()`
- [ ] `supports()` returns true for: "standard", "sql2008", "ansi" (case-insensitive)
- [ ] `getSupportedFeatures()` returns appropriate feature set for standard SQL
- [ ] Add comprehensive JavaDoc
- [ ] Create unit tests verifying plugin behavior
- [ ] Create integration tests using the plugin with DSL

### Technical Notes

- Reuse existing `SqlRendererFactory.standardSql2008()` method
- Document what "standard SQL:2008" means in this context
- This will be the default dialect for backward compatibility

### Example Implementation

```java
package lan.tlab.r4j.sql.dsl.plugin.builtin;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin;
import java.util.Set;

public class StandardSQLDialectPlugin implements SqlDialectPlugin {
    
    @Override
    public String getDialectName() {
        return "sql2008";
    }
    
    @Override
    public String getVersion() {
        return "2008";
    }
    
    @Override
    public SqlRenderer createRenderer() {
        return SqlRendererFactory.standardSql2008();
    }
    
    @Override
    public boolean supports(String dialectName) {
        String normalized = dialectName.toLowerCase();
        return normalized.equals("sql2008") 
            || normalized.equals("standard") 
            || normalized.equals("ansi");
    }
    
    @Override
    public Set<String> getSupportedFeatures() {
        return Set.of(
            "select", "insert", "update", "delete",
            "join", "subquery", "aggregation", "groupby",
            "orderby", "where", "having"
        );
    }
}
```

### Dependencies

- Issue #1 (SqlDialectPlugin interface)
- Issue #2 (SqlDialectRegistry)

### Estimated Effort

Small (2-3 hours)

---

## Issue #4: Create MySQLDialectPlugin

**Labels:** enhancement, plugin-system, dialect, mysql  
**Milestone:** Plugin Architecture - Phase 2  
**Assignees:** (assign as needed)

### Description

Create a built-in plugin for MySQL that leverages the existing MySQL-specific renderer configuration.

### Motivation

MySQL is a widely used database and the framework already has MySQL-specific rendering logic. This plugin will make that functionality available through the plugin architecture.

### Acceptance Criteria

- [ ] Create `MySQLDialectPlugin` class in package `lan.tlab.r4j.sql.dsl.plugin.builtin`
- [ ] Implement all `SqlDialectPlugin` interface methods
- [ ] `getDialectName()` returns "mysql"
- [ ] `getVersion()` returns "8.0" (or current target version)
- [ ] `createRenderer()` delegates to `SqlRendererFactory.mysql()`
- [ ] `supports()` returns true for: "mysql", "mariadb" (case-insensitive)
- [ ] `getSupportedFeatures()` includes MySQL-specific features: "limit", "offset", "backtick-identifiers", "auto-increment", "concat-function"
- [ ] Add comprehensive JavaDoc documenting MySQL-specific behaviors
- [ ] Create unit tests verifying plugin behavior
- [ ] Create integration tests with Testcontainers MySQL

### Technical Notes

- Reuse existing `SqlRendererFactory.mysql()` method
- Document MySQL version compatibility
- MariaDB support is included as it's MySQL-compatible
- Document any MySQL-specific limitations or extensions

### MySQL-Specific Features to Document

- Backtick identifier escaping (not double quotes)
- LIMIT/OFFSET syntax
- CONCAT() function for string concatenation
- DATE_ADD/DATE_SUB for date arithmetic
- NOW() and CURDATE() functions

### Example Implementation

```java
package lan.tlab.r4j.sql.dsl.plugin.builtin;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin;
import java.util.Set;

public class MySQLDialectPlugin implements SqlDialectPlugin {
    
    @Override
    public String getDialectName() {
        return "mysql";
    }
    
    @Override
    public String getVersion() {
        return "8.0";
    }
    
    @Override
    public SqlRenderer createRenderer() {
        return SqlRendererFactory.mysql();
    }
    
    @Override
    public boolean supports(String dialectName) {
        String normalized = dialectName.toLowerCase();
        return normalized.equals("mysql") || normalized.equals("mariadb");
    }
    
    @Override
    public Set<String> getSupportedFeatures() {
        return Set.of(
            "limit", "offset", "backtick-identifiers", 
            "auto-increment", "concat-function",
            "date-add", "date-sub"
        );
    }
}
```

### Dependencies

- Issue #1 (SqlDialectPlugin interface)
- Issue #2 (SqlDialectRegistry)

### Estimated Effort

Small (2-3 hours)

---

## Issue #5: Create PostgreSQLDialectPlugin

**Labels:** enhancement, plugin-system, dialect, postgresql  
**Milestone:** Plugin Architecture - Phase 2  
**Assignees:** (assign as needed)

### Description

Create a built-in plugin for PostgreSQL. This may require creating a new PostgreSQL-specific renderer or using the standard renderer with PostgreSQL-specific configurations.

### Motivation

PostgreSQL is a popular open-source database with some syntax differences from standard SQL. Supporting it via a plugin will make the framework more versatile.

### Acceptance Criteria

- [ ] Create `PostgreSQLDialectPlugin` class in package `lan.tlab.r4j.sql.dsl.plugin.builtin`
- [ ] Implement all `SqlDialectPlugin` interface methods
- [ ] `getDialectName()` returns "postgresql"
- [ ] `getVersion()` returns appropriate PostgreSQL version (e.g., "14.0")
- [ ] `createRenderer()` returns appropriate renderer (may need to create `SqlRendererFactory.postgresql()`)
- [ ] `supports()` returns true for: "postgresql", "postgres", "pg" (case-insensitive)
- [ ] `getSupportedFeatures()` includes PostgreSQL-specific features
- [ ] Add comprehensive JavaDoc
- [ ] Create unit tests verifying plugin behavior
- [ ] Create integration tests with Testcontainers PostgreSQL

### Research Needed

PostgreSQL syntax differences to consider:
- String concatenation: uses `||` operator (like standard SQL)
- Identifier escaping: uses double quotes (like standard SQL)
- LIMIT/OFFSET: standard syntax
- Date/time functions: PostgreSQL-specific (NOW(), CURRENT_DATE, etc.)
- Array support
- JSON operators
- Window functions

### Technical Notes

- May be able to use standard renderer with minimal modifications
- If creating new renderer, add `postgresql()` method to `SqlRendererFactory`
- Document PostgreSQL-specific capabilities and extensions

### Example Implementation

```java
package lan.tlab.r4j.sql.dsl.plugin.builtin;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin;
import java.util.Set;

public class PostgreSQLDialectPlugin implements SqlDialectPlugin {
    
    @Override
    public String getDialectName() {
        return "postgresql";
    }
    
    @Override
    public String getVersion() {
        return "14.0";
    }
    
    @Override
    public SqlRenderer createRenderer() {
        // May need to create SqlRendererFactory.postgresql()
        // or return SqlRendererFactory.standardSql2008() if compatible enough
        return SqlRendererFactory.postgresql(); 
    }
    
    @Override
    public boolean supports(String dialectName) {
        String normalized = dialectName.toLowerCase();
        return normalized.equals("postgresql") 
            || normalized.equals("postgres")
            || normalized.equals("pg");
    }
    
    @Override
    public Set<String> getSupportedFeatures() {
        return Set.of(
            "limit", "offset", "returning",
            "array-types", "json-operators",
            "window-functions", "cte"
        );
    }
}
```

### Dependencies

- Issue #1 (SqlDialectPlugin interface)
- Issue #2 (SqlDialectRegistry)
- May need to create PostgreSQL renderer in SqlRendererFactory

### Estimated Effort

Medium (4-5 hours) - includes research and possible renderer creation

---

## Issue #6: Create SqlServerDialectPlugin

**Labels:** enhancement, plugin-system, dialect, sqlserver  
**Milestone:** Plugin Architecture - Phase 2  
**Assignees:** (assign as needed)

### Description

Create a built-in plugin for Microsoft SQL Server, leveraging the existing SQL Server renderer configuration.

### Motivation

SQL Server is widely used in enterprise environments. The framework already has SQL Server rendering logic that should be exposed through the plugin architecture.

### Acceptance Criteria

- [ ] Create `SqlServerDialectPlugin` class in package `lan.tlab.r4j.sql.dsl.plugin.builtin`
- [ ] Implement all `SqlDialectPlugin` interface methods
- [ ] `getDialectName()` returns "sqlserver"
- [ ] `getVersion()` returns appropriate version
- [ ] `createRenderer()` delegates to `SqlRendererFactory.sqlServer()`
- [ ] `supports()` returns true for: "sqlserver", "mssql", "tsql" (case-insensitive)
- [ ] `getSupportedFeatures()` includes SQL Server-specific features
- [ ] Add comprehensive JavaDoc documenting T-SQL specific behaviors
- [ ] Create unit tests verifying plugin behavior
- [ ] Create integration tests

### Technical Notes

- Reuse existing `SqlRendererFactory.sqlServer()` method
- Document T-SQL specific features and syntax differences
- Consider SQL Server version compatibility

### SQL Server-Specific Features

- TOP instead of LIMIT
- OFFSET/FETCH NEXT syntax
- Square bracket identifier escaping
- GETDATE() function
- LEN() function
- T-SQL specific functions

### Example Implementation

```java
package lan.tlab.r4j.sql.dsl.plugin.builtin;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin;
import java.util.Set;

public class SqlServerDialectPlugin implements SqlDialectPlugin {
    
    @Override
    public String getDialectName() {
        return "sqlserver";
    }
    
    @Override
    public String getVersion() {
        return "2019";
    }
    
    @Override
    public SqlRenderer createRenderer() {
        return SqlRendererFactory.sqlServer();
    }
    
    @Override
    public boolean supports(String dialectName) {
        String normalized = dialectName.toLowerCase();
        return normalized.equals("sqlserver") 
            || normalized.equals("mssql")
            || normalized.equals("tsql");
    }
    
    @Override
    public Set<String> getSupportedFeatures() {
        return Set.of(
            "top", "offset-fetch", "square-bracket-identifiers",
            "tsql-functions", "cte", "window-functions"
        );
    }
}
```

### Dependencies

- Issue #1 (SqlDialectPlugin interface)
- Issue #2 (SqlDialectRegistry)

### Estimated Effort

Small (2-3 hours)

---

## Issue #8: Create META-INF/services Configuration

**Labels:** enhancement, plugin-system, configuration  
**Milestone:** Plugin Architecture - Phase 3  
**Assignees:** (assign as needed)

### Description

Create the ServiceLoader configuration file that enables automatic discovery of built-in dialect plugins using Java's SPI (Service Provider Interface) mechanism.

### Motivation

Java's ServiceLoader requires a specific file structure to discover service implementations at runtime. This configuration is essential for auto-discovery of plugins.

### Acceptance Criteria

- [ ] Create directory structure: `sql/src/main/resources/META-INF/services/`
- [ ] Create file: `lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin`
- [ ] List all built-in plugin implementations in the file (one per line)
- [ ] Verify ServiceLoader can discover plugins at runtime
- [ ] Create test to validate SPI configuration
- [ ] Document SPI configuration format for external plugin developers
- [ ] Ensure file is included in build artifacts

### File Location

```
sql/src/main/resources/META-INF/services/lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin
```

### File Contents

```
# Built-in SQL dialect plugins for r4j
lan.tlab.r4j.sql.dsl.plugin.builtin.StandardSQLDialectPlugin
lan.tlab.r4j.sql.dsl.plugin.builtin.MySQLDialectPlugin
lan.tlab.r4j.sql.dsl.plugin.builtin.PostgreSQLDialectPlugin
lan.tlab.r4j.sql.dsl.plugin.builtin.SqlServerDialectPlugin
```

### Technical Notes

- File encoding must be UTF-8
- One fully qualified class name per line
- Lines starting with `#` are comments
- Blank lines are ignored
- Classes must be on classpath and have no-arg constructor

### Testing

Create a test that:
1. Uses ServiceLoader to load plugins
2. Verifies all expected plugins are discovered
3. Verifies each plugin can be instantiated
4. Verifies each plugin's getDialectName() returns expected value

Example test:

```java
@Test
void serviceLoaderDiscoversAllBuiltinPlugins() {
    ServiceLoader<SqlDialectPlugin> loader = ServiceLoader.load(SqlDialectPlugin.class);
    List<String> dialectNames = new ArrayList<>();
    
    loader.forEach(plugin -> dialectNames.add(plugin.getDialectName()));
    
    assertThat(dialectNames).containsExactlyInAnyOrder(
        "sql2008", "mysql", "postgresql", "sqlserver"
    );
}
```

### Documentation

Add to README or separate PLUGIN_DEVELOPMENT.md:

```markdown
## Creating External Plugins

To create an external plugin:

1. Implement the `SqlDialectPlugin` interface
2. Create `META-INF/services/lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin`
3. Add your plugin's fully qualified class name to that file
4. Package as JAR and add to classpath

The plugin will be automatically discovered and registered.
```

### Dependencies

- Issue #1 (SqlDialectPlugin interface)
- Issue #3 (StandardSQLDialectPlugin)
- Issue #4 (MySQLDialectPlugin)
- Issue #5 (PostgreSQLDialectPlugin)
- Issue #6 (SqlServerDialectPlugin)

### Estimated Effort

Small (1 hour)

---

## Issue #9: Refactor DSL class to support plugins

**Labels:** enhancement, plugin-system, refactoring, breaking-change  
**Milestone:** Plugin Architecture - Phase 4  
**Assignees:** (assign as needed)

### Description

Refactor the main DSL class to support both the plugin-based architecture and maintain backward compatibility with the existing static method API.

### Motivation

The current DSL class uses a static SqlRenderer. To support multiple dialects via plugins, we need to allow instance-based DSL objects that can use different renderers while maintaining backward compatibility.

### Acceptance Criteria

- [ ] Add instance fields: `SqlRenderer sqlRenderer` and `String dialectName`
- [ ] Add private constructor: `DSL(SqlRenderer sqlRenderer, String dialectName)`
- [ ] Implement `public static DSL forDialect(String dialectName)` factory method using registry
- [ ] Implement `public static DSL standard()` factory method
- [ ] Implement `public static DSL mysql()` factory method
- [ ] Implement `public static DSL postgresql()` factory method
- [ ] Implement `public static DSL sqlserver()` factory method (if SqlServerDialectPlugin is implemented)
- [ ] Add instance methods: `select()`, `select(String...)`, `selectAll()`, `createTable()`, `insertInto()`, `deleteFrom()`, `update()`
- [ ] Keep existing static methods for backward compatibility (delegate to `standard()`)
- [ ] Implement `public String getDialect()` method
- [ ] Implement `public static Set<String> getSupportedDialects()` method
- [ ] Create comprehensive unit tests for new functionality
- [ ] Create migration tests to ensure backward compatibility
- [ ] Update all existing tests to pass without modification

### Backward Compatibility Requirements

**CRITICAL:** Existing code must continue to work without any changes. All current static methods must remain and work identically:

```java
// This must continue to work
DSL.select("name").from("users").build();
DSL.createTable("users");
DSL.insertInto("users");
// etc.
```

### New Usage Patterns

```java
// New: dialect-specific DSL instances
DSL mysql = DSL.mysql();
String sql = mysql.select("name").from("users").build();

// New: dynamic dialect selection
DSL dsl = DSL.forDialect("postgresql");

// New: query supported dialects
Set<String> dialects = DSL.getSupportedDialects();

// New: get current dialect
String dialect = mysql.getDialect(); // returns "mysql"
```

### Implementation Structure

```java
package lan.tlab.r4j.sql.dsl;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.dsl.plugin.SqlDialectRegistry;
// ... other imports

public class DSL {
    
    // Instance fields
    private final SqlRenderer sqlRenderer;
    private final String dialectName;
    
    // Static default renderer for backward compatibility
    private static final SqlRenderer DEFAULT_RENDERER = SqlRendererFactory.standardSql2008();
    
    // Private constructor for instance creation
    private DSL(SqlRenderer sqlRenderer, String dialectName) {
        this.sqlRenderer = sqlRenderer;
        this.dialectName = dialectName;
    }
    
    // Factory methods for dialect-specific instances
    public static DSL forDialect(String dialectName) {
        SqlRenderer renderer = SqlDialectRegistry.getRenderer(dialectName);
        return new DSL(renderer, dialectName);
    }
    
    public static DSL standard() {
        return new DSL(DEFAULT_RENDERER, "sql2008");
    }
    
    public static DSL mysql() {
        return forDialect("mysql");
    }
    
    public static DSL postgresql() {
        return forDialect("postgresql");
    }
    
    public static DSL sqlserver() {
        return forDialect("sqlserver");
    }
    
    // Instance methods (use this.sqlRenderer)
    public SelectBuilder select(String... columns) {
        return new SelectBuilder(this.sqlRenderer, columns);
    }
    
    public SelectBuilder selectAll() {
        return new SelectBuilder(this.sqlRenderer, "*");
    }
    
    public CreateTableBuilder createTable(String tableName) {
        return new CreateTableBuilder(this.sqlRenderer, tableName);
    }
    
    public InsertBuilder insertInto(String tableName) {
        return new InsertBuilder(this.sqlRenderer, tableName);
    }
    
    public DeleteBuilder deleteFrom(String tableName) {
        return new DeleteBuilder(this.sqlRenderer, tableName);
    }
    
    public UpdateBuilder update(String tableName) {
        return new UpdateBuilder(this.sqlRenderer, tableName);
    }
    
    public SelectProjectionBuilder select() {
        return new SelectProjectionBuilder(this.sqlRenderer);
    }
    
    // Dialect info methods
    public String getDialect() {
        return this.dialectName;
    }
    
    public static Set<String> getSupportedDialects() {
        return SqlDialectRegistry.getSupportedDialects();
    }
    
    // Static methods for backward compatibility (delegate to standard())
    public static SelectBuilder select(String... columns) {
        return standard().select(columns);
    }
    
    public static SelectBuilder selectAll() {
        return standard().selectAll();
    }
    
    public static CreateTableBuilder createTable(String tableName) {
        return standard().createTable(tableName);
    }
    
    public static InsertBuilder insertInto(String tableName) {
        return standard().insertInto(tableName);
    }
    
    public static DeleteBuilder deleteFrom(String tableName) {
        return standard().deleteFrom(tableName);
    }
    
    public static UpdateBuilder update(String tableName) {
        return standard().update(tableName);
    }
    
    public static SelectProjectionBuilder select() {
        return standard().select();
    }
}
```

### Testing Strategy

1. **Backward Compatibility Tests:** Ensure all existing usage patterns work
2. **New API Tests:** Test new factory methods and instance usage
3. **Multi-Dialect Tests:** Create same query with different dialects, verify different SQL
4. **Integration Tests:** Test with actual databases via Testcontainers

### Migration Guide

Create a migration guide showing:
- Old API (still works)
- New API (recommended for multi-dialect support)
- When to use each approach

### Dependencies

- Issues #1-8 (all plugin infrastructure and built-in plugins)

### Estimated Effort

Medium (4-5 hours)

---

## Additional Issues Summary

The remaining issues (10-18) follow similar patterns. Would you like me to generate templates for those as well? Here's a brief summary:

- **Issue #10:** Update builder classes (verification task)
- **Issue #11:** Documentation
- **Issue #12:** Example external plugin
- **Issue #13:** Integration tests
- **Issue #14:** Migration tests
- **Issue #15:** Performance optimization
- **Issue #16:** Feature detection API (advanced)
- **Issue #17:** Plugin configuration (advanced)
- **Issue #18:** Multi-module distribution (advanced)

---

## How to Create Issues in GitHub

1. Go to your repository on GitHub
2. Click "Issues" tab
3. Click "New Issue"
4. Copy the content from one of these templates
5. Set appropriate labels, milestone, and assignees
6. Create the issue

## Recommended Issue Creation Order

Create in phases:

**Phase 1 (Core):**
- Issues #1, #2

**Phase 2 (Plugins):**
- Issues #3, #4, #5, #6, #8

**Phase 3 (DSL):**
- Issues #9, #10

**Phase 4 (Documentation):**
- Issues #11, #12

**Phase 5 (Testing):**
- Issues #13, #14

**Phase 6 (Optimization):**
- Issue #15

**Phase 7 (Advanced - Optional):**
- Issues #16, #17, #18
