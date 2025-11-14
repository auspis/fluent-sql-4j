# Plugin Architecture Overview

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         User Application                         │
│                                                                   │
│  DSL.forDialect("mysql").select("name").from("users").build()   │
│  DSL.forDialect("mysql", "8.0.1").select("name")...             │
│  DSL.forDialect("postgresql", "[12.0,)")...                     │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                          DSL Class                               │
│                                                                   │
│  - sqlRenderer: SqlRenderer                                      │
│  - dialectName: String                                           │
│  - dialectVersion: String                                        │
│                                                                   │
│  + forDialect(String): DSL                                       │
│  + forDialect(String, String): DSL                               │
│  + getSupportedDialects(): Set<String>                           │
│                                                                   │
│  Instance methods:                                               │
│  + select(), selectAll(), createTable(),                         │
│    insertInto(), deleteFrom(), update()                          │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            │ uses
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SqlDialectRegistry                            │
│                                                                   │
│  - plugins: ConcurrentHashMap<String, SqlDialectPlugin>          │
│                                                                   │
│  static {                                                         │
│    ServiceLoader<SqlDialectPlugin> loader = ...                  │
│    loader.forEach(SqlDialectRegistry::register);                 │
│  }                                                                │
│                                                                   │
│  + register(SqlDialectPlugin): void                              │
│  + getRenderer(String): SqlRenderer                              │
│  + getRenderer(String, String): SqlRenderer                      │
│  + getSupportedDialects(): Set<String>                           │
│  + isSupported(String): boolean                                  │
│  + isSupported(String, String): boolean                          │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            │ auto-discovers via ServiceLoader
                            ▼
              ┌─────────────────────────────────┐
              │  <<interface>>                  │
              │   SqlDialectPlugin              │
              │                                 │
              │ + getDialectName(): String      │
              │ + getVersion(): String          │
              │ + createRenderer(): Renderer    │
              │ + supports(String): boolean     │
              │ + supportsVersion(String): bool │
              │ + getSupportedFeatures(): Set   │
              └──────────────┬──────────────────┘
                             │
                             │ implements
         ┌───────────────────┼───────────────────┬─────────────────────┐
         │                   │                   │                     │
         ▼                   ▼                   ▼                     ▼
┌─────────────────┐ ┌────────────────┐ ┌──────────────────┐ ┌───────────────┐
│ StandardSQL     │ │ MySQLDialect   │ │ PostgreSQLDialect│ │ SqlServerDialect│
│ DialectPlugin   │ │ Plugin         │ │ Plugin           │ │ Plugin         │
│                 │ │                │ │                  │ │                │
│ name: sql2008   │ │ name: mysql    │ │ name: postgresql │ │ name: sqlserver│
│ supports:       │ │ supports:      │ │ supports:        │ │ supports:      │
│  - standard     │ │  - mysql       │ │  - postgresql    │ │  - sqlserver   │
│  - sql2008      │ │  - mariadb     │ │  - postgres      │ │  - mssql       │
│  - ansi         │ │                │ │  - pg            │ │  - tsql        │
└────────┬────────┘ └───────┬────────┘ └────────┬─────────┘ └───────┬───────┘
         │                  │                   │                    │
         │ creates          │ creates           │ creates            │ creates
         ▼                  ▼                   ▼                    ▼
┌─────────────────┐ ┌────────────────┐ ┌──────────────────┐ ┌───────────────┐
│ SqlRenderer     │ │ SqlRenderer    │ │ SqlRenderer      │ │ SqlRenderer   │
│ (standard)      │ │ (mysql config) │ │ (postgres config)│ │ (sqlserver)   │
└─────────────────┘ └────────────────┘ └──────────────────┘ └───────────────┘
```

## Component Descriptions

### DSL Class

- **Purpose:** Main entry point for building SQL statements
- **Responsibilities:**
  - Provide fluent API for SQL construction
  - Manage SqlRenderer for current dialect
  - Delegate to SqlDialectRegistry for plugin lookup

### SqlDialectRegistry

- **Purpose:** Central registry for all SQL dialect plugins
- **Key Features:**
  - Auto-discovery via Java ServiceLoader (SPI)
  - Thread-safe plugin storage
  - Case-insensitive dialect name matching
- **Responsibilities:**
  - Load plugins on startup
  - Allow manual plugin registration
  - Provide renderer instances for dialects
  - List supported dialects

### SqlDialectPlugin Interface

- **Purpose:** Contract that all dialect plugins must implement
- **Key Methods:**
  - `getDialectName()`: Canonical name
  - `getVersion()`: Version of the dialect implementation
  - `createRenderer()`: Factory for SqlRenderer
  - `supports(String)`: Check alias support
  - `supportsVersion(String)`: Check version compatibility
  - `getSupportedFeatures()`: List dialect capabilities

## Version Handling

The plugin architecture supports semantic versioning for dialect compatibility. This allows applications to specify version requirements and ensures compatibility with specific database versions.

### Version Specification Syntax

Following Maven/Gradle conventions:

```java
// Latest version (default)
DSL dsl = DSL.forDialect("mysql");

// Exact version match
DSL dsl = DSL.forDialect("mysql", "8.0.1");

// Minimum version (inclusive) - shorthand
DSL dsl = DSL.forDialect("mysql", "8.0.0+");

// Minimum version (inclusive) - Maven style
DSL dsl = DSL.forDialect("mysql", "[8.0.0,)");

// Maximum version (exclusive)
DSL dsl = DSL.forDialect("mysql", "(,9.0.0)");

// Version range [min, max)
DSL dsl = DSL.forDialect("mysql", "[8.0.0,9.0.0)");

// Version range (min, max]
DSL dsl = DSL.forDialect("mysql", "(8.0.0,8.5.0]");
```

### Version Specification Components

**VersionSpecification Utility Class:**

```java
public class VersionSpecification {
    /**
     * Parses a version specification string.
     * Supports:
     * - Exact: "8.0.1"
     * - Minimum: "8.0.0+", "[8.0.0,)"
     * - Maximum: "(,9.0.0)"
     * - Range: "[8.0.0,9.0.0)", "(8.0.0,8.5.0]"
     */
    public static VersionSpecification parse(String spec);
    
    /**
     * Checks if a version satisfies this specification.
     */
    public boolean isSatisfiedBy(String version);
}
```

### Updated Interface Methods

**SqlDialectPlugin:**

```java
public interface SqlDialectPlugin {
    String getDialectName();
    String getVersion();
    SqlRenderer createRenderer();
    boolean supports(String dialectName);
    
    /**
     * Checks if this plugin supports the specified version.
     * @param versionSpec Version specification (exact, range, or constraint)
     * @return true if this plugin's version satisfies the specification
     */
    boolean supportsVersion(String versionSpec);
    
    Set<String> getSupportedFeatures();
}
```

**SqlDialectRegistry:**

```java
public class SqlDialectRegistry {
    /**
     * Get renderer for dialect with version constraint.
     * @param dialect Dialect name
     * @param versionSpec Version specification
     * @return SqlRenderer for matching plugin
     * @throws IllegalArgumentException if no compatible plugin found
     */
    public static SqlRenderer getRenderer(String dialect, String versionSpec);
    
    /**
     * Check if dialect with version is supported.
     */
    public static boolean isSupported(String dialect, String versionSpec);
}
```

**DSL Class:**

```java
public class DSL {
    private final String dialectVersion;
    
    /**
     * Create DSL for specific dialect and version.
     * @param dialectName Dialect name (e.g., "mysql")
     * @param versionSpec Version specification (e.g., "8.0.1", "[8.0,9.0)")
     */
    public static DSL forDialect(String dialectName, String versionSpec);
    
    /**
     * Get the version of the dialect in use.
     */
    public String getDialectVersion();
}
```

### Version Matching Examples

```java
// Plugin implementation
public class MySQLDialectPlugin implements SqlDialectPlugin {
    @Override
    public String getVersion() {
        return "8.0.30";
    }
    
    @Override
    public boolean supportsVersion(String versionSpec) {
        return VersionSpecification.parse(versionSpec)
                                   .isSatisfiedBy(getVersion());
    }
}

// Usage scenarios
DSL dsl1 = DSL.forDialect("mysql", "8.0.30");    // ✓ Exact match
DSL dsl2 = DSL.forDialect("mysql", "8.0.0+");    // ✓ 8.0.30 >= 8.0.0
DSL dsl3 = DSL.forDialect("mysql", "[8.0,9.0)"); // ✓ 8.0.30 in range
DSL dsl4 = DSL.forDialect("mysql", "9.0.0+");    // ✗ 8.0.30 < 9.0.0
```

### Version Comparison Rules

Uses semantic versioning (MAJOR.MINOR.PATCH):
- **Exact match:** Version must be identical
- **Range match:** Version must fall within specified range
- **Minimum/Maximum:** Inclusive `[` or exclusive `(` boundaries
- **Shorthand `+`:** Equivalent to `[version,)` (minimum inclusive)

### Built-in Plugins

**First Release (v1.0):**
- **StandardSQLDialectPlugin:** SQL:2008 standard ✓
- **MySQLDialectPlugin:** MySQL/MariaDB ✓

**Future Releases:**
- **PostgreSQLDialectPlugin:** PostgreSQL (planned)
- **SqlServerDialectPlugin:** Microsoft SQL Server (planned)
- **OracleDialectPlugin:** Oracle Database (optional)

Each plugin:
- Implements SqlDialectPlugin interface
- Delegates to SqlRendererFactory for renderer creation
- Supports multiple dialect name aliases
- Documents supported SQL features

## Data Flow

### Usage Example 1: Dynamic Dialect Selection

```
User Code:
  DSL.forDialect("mysql").select("name").from("users").build()
    │
    ▼
  DSL.forDialect("mysql")
    │
    ▼
  SqlDialectRegistry.getRenderer("mysql")
    │
    ▼
  Find MySQLDialectPlugin in registry
    │
    ▼
  MySQLDialectPlugin.createRenderer()
    │
    ▼
  SqlRendererFactory.mysql()
    │
    ▼
  Returns configured SqlRenderer
    │
    ▼
  new DSL(renderer, "mysql")
    │
    ▼
  dsl.select("name")  // instance method using MySQL renderer
    │
    ▼
  new SelectBuilder(mysqlRenderer, "name")
    │
    ▼
  builder.from("users").build()
    │
    ▼
  Generates MySQL-specific SQL with backticks: SELECT `name` FROM `users`
```

### Usage Example 2: Multi-Database Support

```
User Code:
  DSL mysqlDSL = DSL.forDialect("mysql");
  DSL postgresDSL = DSL.forDialect("postgresql");
  
  mysqlDSL.select("name").from("users").build()
  postgresDSL.select("name").from("users").build()
    │
    ▼
  Each DSL instance uses its configured renderer
    │
    ▼
  Generates dialect-specific SQL
```

## ServiceLoader (SPI) Configuration

### File Location

```
jdsql/src/main/resources/META-INF/services/lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin
```

### File Contents

```
lan.tlab.r4j.sql.dsl.plugin.builtin.StandardSQLDialectPlugin
lan.tlab.r4j.sql.dsl.plugin.builtin.MySQLDialectPlugin
lan.tlab.r4j.sql.dsl.plugin.builtin.PostgreSQLDialectPlugin
lan.tlab.r4j.sql.dsl.plugin.builtin.SqlServerDialectPlugin
```

### How ServiceLoader Works

1. At startup, static initializer in SqlDialectRegistry executes
2. ServiceLoader.load(SqlDialectPlugin.class) is called
3. ServiceLoader reads the configuration file
4. For each line, ServiceLoader instantiates the class
5. Each plugin is registered in the registry
6. Plugins are now available for use

## External Plugin Development

### Steps to Create External Plugin

1. **Create Plugin Class**

```java
package com.example.myplugin;

import lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin;

public class OracleDialectPlugin implements SqlDialectPlugin {
    // implement interface methods
}
```

2. **Create SPI Configuration**

```
# File: src/main/resources/META-INF/services/lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin
com.example.myplugin.OracleDialectPlugin
```

3. **Package as JAR**

```bash
mvn clean package
```

4. **Add to Classpath**

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>oracle-dialect-plugin</artifactId>
    <version>1.0.0</version>
</dependency>
```

5. **Use in Application**

```java
// Plugin is auto-discovered
DSL oracle = DSL.forDialect("oracle");
```

## Package Structure

```
r4j/
├── sql                                       [Core module]
│   └── src/main/java/lan/tlab/r4j/sql
│       ├── dsl
│       │   ├── DSL.java                     [Modified]
│       │   ├── plugin                       [NEW]
│       │   │   ├── SqlDialectPlugin.java    [NEW - Interface]
│       │   │   └── SqlDialectRegistry.java  [NEW - Registry]
│       │   ├── select
│       │   │   └── SelectBuilder.java       [Unchanged]
│       │   └── ...
│       └── ast
│           └── visitor
│               └── sql
│                   ├── SqlRenderer.java     [Unchanged]
│                   └── factory
│                       └── SqlRendererFactory.java [Unchanged]
│
├── dialect-plugins                          [NEW parent module]
│   ├── mysql                                [NEW module]
│   │   └── src/main/java/.../plugin/builtin
│   │       └── MySQLDialectPlugin.java
│   │   └── src/main/resources/META-INF/services
│   │       └── lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin
│   │
│   ├── postgresql                           [NEW module]
│   │   └── src/main/java/.../plugin/builtin
│   │       └── PostgreSQLDialectPlugin.java
│   │   └── src/main/resources/META-INF/services
│   │       └── lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin
│   │
│   ├── sqlserver                            [NEW module]
│   │   └── src/main/java/.../plugin/builtin
│   │       └── SqlServerDialectPlugin.java
│   │   └── src/main/resources/META-INF/services
│   │       └── lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin
│   │
│   └── standard                             [NEW module]
│       └── src/main/java/.../plugin/builtin
│           └── StandardSQLDialectPlugin.java
│       └── src/main/resources/META-INF/services
│           └── lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin
│
└── test-integration                         [Existing module]
```

## Benefits

1. **Extensibility:** Community can create plugins for any database
2. **Auto-Discovery:** No manual registration code needed
3. **Modularity:** Each dialect is a separate Maven module
4. **Type Safety:** Compile-time checking with defined interface
5. **Testability:** Easy to mock plugins for testing
6. **Performance:** Cached registry, no runtime overhead after initialization
7. **Distribution:** Plugins can be distributed independently

## Usage Pattern

### Basic Usage

```java
// Instantiate DSL for specific dialect
DSL dsl = DSL.forDialect("mysql");
String sql = dsl.select("name").from("users").build();
```

### Multi-Database Support

```java
public class MultiDatabaseService {
    private final DSL mysqlDSL = DSL.forDialect("mysql");
    private final DSL postgresDSL = DSL.forDialect("postgresql");
    
    public String getUsersFromMySQL() {
        return mysqlDSL.select("name", "email")
                      .from("users")
                      .where("active").eq(true)
                      .build();
    }
    
    public String getUsersFromPostgres() {
        return postgresDSL.select("name", "email")
                         .from("users") 
                         .where("active").eq(true)
                         .build();
    }
}
```

### Dynamic Dialect Selection

```java
// Select dialect at runtime based on configuration
String dialect = config.getDatabaseType();
DSL dsl = DSL.forDialect(dialect);
```

## Thread Safety

- **SqlDialectRegistry:** Uses ConcurrentHashMap for thread-safe access
- **ServiceLoader:** Initialized in static block, happens-before any use
- **DSL Instances:** Immutable after construction (sqlRenderer and dialectName are final)
- **Builders:** Each query creates new builder instances

## Error Handling

### Unsupported Dialect

```java
try {
    DSL dsl = DSL.forDialect("unsupported");
} catch (IllegalArgumentException e) {
    // "Unsupported SQL dialect: unsupported"
    // "Supported dialects: [sql2008, mysql, postgresql, sqlserver]"
}
```

### Plugin Load Failure

If a plugin class cannot be loaded:
- ServiceLoader logs warning
- Other plugins continue to load
- Application can still use successfully loaded plugins

## Performance Considerations

### Initialization Cost

- ServiceLoader runs once at class loading time
- All plugins instantiated during static initialization
- One-time cost, amortized over application lifetime

### Runtime Cost

- Plugin lookup: O(1) HashMap lookup
- Renderer creation: Delegates to existing factory methods
- No overhead compared to current implementation

### Optimization Opportunities

- Lazy loading of plugins (if initialization is expensive)
- Caching of renderer instances (if creation is expensive)
- Pre-warming commonly used dialects

## Testing Strategy

### Unit Tests

- Test each plugin in isolation
- Test registry operations
- Test DSL factory methods
- Mock plugins for testing

### Integration Tests

- Test with real databases using Testcontainers
- Test ServiceLoader discovery
- Test multi-dialect scenarios

### Performance Tests

- Benchmark plugin loading time
- Benchmark renderer creation
- Compare with baseline (current implementation)
- Ensure no regression

## Documentation Requirements

### User Documentation

- README.md: Basic usage examples
- Dialect comparison: Feature matrix

### Developer Documentation

- PLUGIN_DEVELOPMENT.md: Creating external plugins
- Architecture overview: This document
- API documentation: JavaDoc

### Example Code

- Basic examples in README
- Advanced examples in docs/
- External plugin example in spike/ or examples/

## Future Enhancements

### Phase 1 (MVP)

- Core interface and registry
- Built-in plugins as separate Maven modules
- DSL refactoring with forDialect() support
- Basic documentation

### Phase 2 (Complete)

- Comprehensive testing
- Performance optimization
- Full documentation
- Example external plugin

### Phase 3 (Advanced)

- Feature detection API
- Plugin configuration system
- Multi-module distribution
- OSGi support (optional)

### Phase 4 (Community)

- Community-contributed plugins
- Plugin marketplace/registry
- Plugin certification/validation
- Version compatibility matrix

