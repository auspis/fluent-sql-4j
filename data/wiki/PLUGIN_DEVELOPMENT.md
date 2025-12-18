# Plugin Development Guide

This guide explains how to create custom SQL dialect plugins for JDSQL, extending the DSL to support database-specific features.

## Table of Contents

- [Plugin Architecture Overview](#plugin-architecture-overview)
- [How to Create a New Plugin](#how-to-create-a-new-plugin)
- [Best Practices](#best-practices)
- [Common Pitfalls](#common-pitfalls)
- [Testing Strategies](#testing-strategies)
- [Performance Considerations](#performance-considerations)
- [Complete Example: PostgreSQL Plugin](#complete-example-postgresql-plugin)

## Plugin Architecture Overview

The JDSQL plugin system uses Java's ServiceLoader (SPI) mechanism for automatic discovery and registration of SQL dialect plugins.

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Application Code                      │
│                                                           │
│   DSLRegistry registry = DSLRegistry.createWithServiceLoader();
│   DSL dsl = registry.dslFor("mysql", "8.0.35");         │
└─────────────────────────┬───────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                      DSLRegistry                         │
│                                                           │
│  - Provides simplified API for dialect selection         │
│  - Caches DSL instances per dialect                      │
│  - Delegates to SqlDialectPluginRegistry                 │
└─────────────────────────┬───────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│              SqlDialectPluginRegistry                    │
│                                                           │
│  - ConcurrentHashMap<String, List<SqlDialectPlugin>>     │
│  - Auto-discovers plugins via ServiceLoader             │
│  - Provides dialect renderers                            │
│  - Manages plugin versioning (semantic versioning)       │
└─────────────────────────┬───────────────────────────────┘
                          │
                          │ ServiceLoader.load()
                          ▼
┌─────────────────────────────────────────────────────────┐
│            SqlDialectPluginProvider (SPI)                │
│                                                           │
│  + get(): SqlDialectPlugin                               │
└─────────────────────────┬───────────────────────────────┘
                          │
                          │ creates
                          ▼
┌─────────────────────────────────────────────────────────┐
│                  SqlDialectPlugin                        │
│                                                           │
│  - dialectName: String                                   │
│  - dialectVersion: String (semver regex)                 │
│  - rendererFactory: Supplier<PreparedStatementSpecFactory>            │
│  - dslFactory: Supplier<DSL>                             │
└─────────────────────────────────────────────────────────┘
```

### Key Components

1. **SqlDialectPluginProvider**: SPI interface for plugin discovery
2. **SqlDialectPlugin**: Encapsulates dialect-specific configuration
3. **SqlDialectPluginRegistry**: Central registry using ServiceLoader
4. **DSLRegistry**: User-facing API for dialect selection
5. **PreparedStatementSpecFactory**: Renders AST to SQL for specific dialect

### Plugin Lifecycle

1. Application starts
2. ServiceLoader discovers all `SqlDialectPluginProvider` implementations
3. Each provider creates a `SqlDialectPlugin` instance
4. Plugins are registered in `SqlDialectPluginRegistry`
5. User requests DSL for specific dialect and version
6. Registry matches dialect and version using semantic versioning
7. Plugin creates renderer and DSL instance
8. DSL instance is cached for reuse

## How to Create a New Plugin

### Step 1: Create Maven Module Structure

Create a new module under `plugins/`:

```
plugins/
└── jdsql-postgresql/
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── lan/tlab/r4j/jdsql/plugin/builtin/postgresql/
        │   │       ├── PostgreSqlDialectPluginProvider.java
        │   │       ├── PostgreSqlDialectPlugin.java
        │   │       ├── renderer/
        │   │       │   ├── PostgreSqlRenderer.java
        │   │       │   └── PostgreSqlCustomFunctionRenderStrategy.java
        │   │       └── dsl/
        │   │           └── PostgreSqlDSL.java
        │   └── resources/
        │       └── META-INF/
        │           └── services/
        │               └── lan.tlab.r4j.jdsql.plugin.SqlDialectPluginProvider
        └── test/
            └── java/
                └── lan/tlab/r4j/jdsql/plugin/builtin/postgresql/
                    ├── PostgreSqlDialectPluginProviderTest.java
                    └── PostgreSqlCustomFunctionRenderStrategyTest.java
```

### Step 2: Implement SqlDialectPluginProvider

Create the provider that ServiceLoader will discover:

```java
package lan.tlab.r4j.jdsql.plugin.builtin.postgresql;

import lan.tlab.r4j.jdsql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginProvider;

/**
 * Service provider for PostgreSQL dialect plugin.
 * Discovered automatically via Java ServiceLoader.
 */
public final class PostgreSqlDialectPluginProvider implements SqlDialectPluginProvider {

    @Override
    public SqlDialectPlugin get() {
        return SqlDialectPlugin.builder()
            .dialectName("postgresql")
            .dialectVersion("^15\\.0\\.0")  // Semantic versioning regex
            .rendererFactory(this::createRenderer)
            .dslFactory(this::createDSL)
            .build();
    }

    private PreparedStatementSpecFactory createRenderer() {
        return PreparedStatementSpecFactory.of(
            createSqlRenderer(),
            createAstToPreparedStatementSpecVisitor()
        );
    }

    private SqlRenderer createSqlRenderer() {
        return SqlRenderer.builder()
            .customFunctionCallStrategy(new PostgreSqlCustomFunctionRenderStrategy())
            // ... other strategies
            .build();
    }

    private AstToPreparedStatementSpecVisitor createAstToPreparedStatementSpecVisitor() {
        return AstToPreparedStatementSpecVisitor.builder()
            // ... PS strategies
            .build();
    }

    private DSL createDSL() {
        return new PostgreSqlDSL(createRenderer());
    }
}
```

### Step 3: Configure META-INF/services

Create the ServiceLoader configuration file:

**File**: `src/main/resources/META-INF/services/lan.tlab.r4j.jdsql.plugin.SqlDialectPluginProvider`

```
lan.tlab.r4j.jdsql.plugin.builtin.postgresql.PostgreSqlDialectPluginProvider
```

**CRITICAL**: The file path and name must be exact:
- Directory: `META-INF/services/`
- Filename: `lan.tlab.r4j.jdsql.plugin.SqlDialectPluginProvider` (interface FQN)
- Content: Fully qualified class name of your provider implementation

### Step 4: Create Custom Rendering Strategy

Implement dialect-specific SQL rendering:

```java
package lan.tlab.r4j.jdsql.plugin.builtin.postgresql.renderer;

import lan.tlab.r4j.jdsql.ast.expression.scalar.CustomFunctionCall;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.CustomFunctionCallRenderStrategy;

public class PostgreSqlCustomFunctionRenderStrategy 
        implements CustomFunctionCallRenderStrategy {
    
    @Override
    public String render(CustomFunctionCall functionCall, 
                        SqlRenderer renderer, 
                        AstContext ctx) {
        return switch (functionCall.functionName()) {
            case "STRING_AGG" -> renderStringAgg(functionCall, renderer, ctx);
            default -> renderGeneric(functionCall, renderer, ctx);
        };
    }
    
    private String renderStringAgg(CustomFunctionCall call, 
                                   SqlRenderer renderer, 
                                   AstContext ctx) {
        StringBuilder sql = new StringBuilder("STRING_AGG(");
        sql.append(call.arguments().get(0).accept(renderer, ctx));
        
        String separator = (String) call.options().get("SEPARATOR");
        sql.append(", '").append(separator).append("'");
        
        if (call.options().containsKey("ORDER_BY")) {
            sql.append(" ORDER BY ").append(call.options().get("ORDER_BY"));
        }
        
        sql.append(")");
        return sql.toString();
    }
    
    private String renderGeneric(CustomFunctionCall call, 
                                 SqlRenderer renderer, 
                                 AstContext ctx) {
        String args = call.arguments().stream()
            .map(arg -> arg.accept(renderer, ctx))
            .collect(Collectors.joining(", "));
        return call.functionName() + "(" + args + ")";
    }
}
```

### Step 5: Extend DSL (Optional)

Create dialect-specific DSL extensions:

```java
package lan.tlab.r4j.jdsql.plugin.builtin.postgresql.dsl;

import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;

public class PostgreSqlDSL extends DSL {
    
    public PostgreSqlDSL(PreparedStatementSpecFactory renderer) {
        super(renderer);
    }
    
    /**
     * PostgreSQL's STRING_AGG function.
     * Example: STRING_AGG(name, ', ' ORDER BY name)
     */
    public StringAggBuilder stringAgg(String column) {
        return new StringAggBuilder(column);
    }
    
    public class StringAggBuilder {
        private final String column;
        private String orderBy;
        private String separator = ",";
        
        StringAggBuilder(String column) {
            this.column = column;
        }
        
        public StringAggBuilder orderBy(String column) {
            this.orderBy = column;
            return this;
        }
        
        public StringAggBuilder separator(String separator) {
            this.separator = separator;
            return this;
        }
        
        public ScalarExpression build() {
            Map<String, Object> options = new HashMap<>();
            if (orderBy != null) {
                options.put("ORDER_BY", orderBy);
            }
            options.put("SEPARATOR", separator);
            
            return new CustomFunctionCall(
                "STRING_AGG",
                List.of(ColumnReference.of("", column)),
                options
            );
        }
    }
}
```

### Step 6: Write Tests

#### Unit Test for Provider

```java
@Test
void shouldBeDiscoverableViaServiceLoader() {
    ServiceLoader<SqlDialectPluginProvider> loader = 
        ServiceLoader.load(SqlDialectPluginProvider.class);

    List<SqlDialectPlugin> plugins = loader.stream()
        .map(ServiceLoader.Provider::get)
        .map(SqlDialectPluginProvider::get)
        .toList();

    boolean foundPostgreSQL = plugins.stream()
        .anyMatch(p -> "postgresql".equals(p.dialectName()) 
                    && "^15.0.0".equals(p.dialectVersion()));

    assertThat(foundPostgreSQL).isTrue();
}
```

#### Integration Test

```java
@IntegrationTest
class PostgreSqlDialectPluginIntegrationTest {
    
    @Test
    void shouldRegisterInRegistry() {
        SqlDialectPluginRegistry registry = 
            SqlDialectPluginRegistry.createWithServiceLoader();

        assertThat(registry.isSupported("postgresql")).isTrue();
        assertThat(registry.isSupported("PostgreSQL")).isTrue(); // case-insensitive
    }

    @Test
    void shouldProvideRenderer() {
        SqlDialectPluginRegistry registry = 
            SqlDialectPluginRegistry.createWithServiceLoader();

        Result<PreparedStatementSpecFactory> result = 
            registry.getSpecFactory("postgresql", "15.0.0");

        assertThat(result).isInstanceOf(Result.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }
}
```

#### E2E Test

```java
@E2ETest
@Testcontainers
class PostgreSqlE2E {
    
    @Container
    private static final PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:15.0");
    
    @Test
    void shouldExecuteStringAgg() throws SQLException {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();
        PostgreSqlDSL dsl = registry
            .dslFor("postgresql", "15.0.0", PostgreSqlDSL.class)
            .orElseThrow();

        try (Connection conn = DriverManager.getConnection(
                postgres.getJdbcUrl(), 
                postgres.getUsername(), 
                postgres.getPassword())) {
            
            PreparedStatement ps = dsl.select()
                .column("department")
                .expression(
                    dsl.stringAgg("name")
                        .separator(", ")
                        .orderBy("name")
                        .build()
                ).as("employees")
                .from("employees")
                .groupBy("department")
                .buildPreparedStatement(conn);
            
            ResultSet rs = ps.executeQuery();
            // Verify results
        }
    }
}
```

## Best Practices

### Thread Safety

- **Renderers**: Must be stateless or use immutable state
- **DSL instances**: Should be immutable after construction
- **Plugin registration**: Handled automatically by registry (thread-safe)

```java
// ✅ GOOD: Stateless renderer strategy
public class MyRenderStrategy implements CustomFunctionCallRenderStrategy {
    @Override
    public String render(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        // No instance state, thread-safe
        return "...";
    }
}

// ❌ BAD: Mutable state in renderer
public class BadRenderStrategy implements CustomFunctionCallRenderStrategy {
    private int counter; // Mutable state - not thread-safe!
    
    @Override
    public String render(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        counter++; // Race condition
        return "...";
    }
}
```

### Immutability

- Use immutable collections (`List.of()`, `Map.of()`)
- Make AST nodes immutable
- Use builders for complex objects

```java
// ✅ GOOD: Immutable options
Map<String, Object> options = Map.of(
    "ORDER_BY", orderBy,
    "SEPARATOR", separator
);

// ❌ BAD: Mutable options
Map<String, Object> options = new HashMap<>();
options.put("ORDER_BY", orderBy); // Can be modified externally
```

### Error Handling

- Use `Result<T>` type for operations that can fail
- Provide clear error messages
- Include supported dialects in error messages

```java
// ✅ GOOD: Clear error handling
Result<PreparedStatementSpecFactory> result = registry.getSpecFactory("unknown", "1.0");
if (result instanceof Result.Failure<PreparedStatementSpecFactory> failure) {
    System.err.println("Error: " + failure.error());
    System.err.println("Supported dialects: " + registry.supportedDialects());
}

// ❌ BAD: Throwing generic exceptions
try {
    PreparedStatementSpecFactory renderer = registry.getSpecFactory("unknown", "1.0").orElseThrow();
} catch (Exception e) {
    // Generic error, no context
}
```

## Common Pitfalls

### 1. Incorrect META-INF/services Path

**Problem**: ServiceLoader cannot find your plugin.

```
❌ WRONG paths:
- META-INF/service/ (missing 's')
- resources/META-INF/services/
- src/META-INF/services/

✅ CORRECT path:
- src/main/resources/META-INF/services/lan.tlab.r4j.jdsql.plugin.SqlDialectPluginProvider
```

### 2. Non-Thread-Safe Plugin

**Problem**: Race conditions when multiple threads use the same renderer.

```java
❌ BAD: Mutable state
public class MyRenderer implements SqlRenderer {
    private StringBuilder buffer = new StringBuilder(); // Shared state!
}

✅ GOOD: Method-local state
public class MyRenderer implements SqlRenderer {
    public String render(AstNode node) {
        StringBuilder buffer = new StringBuilder(); // Thread-local
        // ...
    }
}
```

### 3. Invalid Versioning Regex

**Problem**: Version matching fails or matches too broadly.

```java
❌ BAD: Too broad
.dialectVersion("8")  // Matches 8, 80, 81, 82...

❌ BAD: Invalid regex
.dialectVersion("8.0")  // Literal "8.0", not regex

✅ GOOD: Semantic versioning regex
.dialectVersion("^8\\.0\\.0")  // Matches 8.0.x only
.dialectVersion("^8\\.0\\.[0-9]+")  // More explicit
```

### 4. Missing Provider Implementation

**Problem**: ServiceLoader configuration points to non-existent class.

```
❌ File content:
com.example.MyProvider  (class doesn't exist or typo)

✅ File content:
lan.tlab.r4j.jdsql.plugin.builtin.postgresql.PostgreSqlDialectPluginProvider
(fully qualified, correct class name)
```

## Testing Strategies

### Unit Tests

Test individual components in isolation:

```java
@Test
void shouldRenderStringAggWithSeparator() {
    CustomFunctionCall call = new CustomFunctionCall(
        "STRING_AGG",
        List.of(ColumnReference.of("employees", "name")),
        Map.of("SEPARATOR", ", ")
    );

    SqlRenderer renderer = mock(SqlRenderer.class);
    when(renderer.render(any(), any())).thenReturn("\"employees\".\"name\"");

    PostgreSqlCustomFunctionRenderStrategy strategy = 
        new PostgreSqlCustomFunctionRenderStrategy();
    
    String result = strategy.render(call, renderer, AstContext.empty());

    assertThat(result).isEqualTo("STRING_AGG(\"employees\".\"name\", ', ')");
}
```

### Integration Tests with H2

Use H2 in MySQL/PostgreSQL compatibility mode:

```java
@IntegrationTest
class PostgreSqlDialectPluginIntegrationTest {
    
    @Test
    void shouldGenerateValidSQL() throws SQLException {
        Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:test;MODE=PostgreSQL",
            "sa",
            ""
        );

        DSLRegistry registry = DSLRegistry.createWithServiceLoader();
        DSL dsl = registry.dslFor("postgresql", "15.0.0").orElseThrow();

        String sql = dsl.select("name").from("users").build();
        
        // Verify SQL is valid by preparing statement
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            assertThat(ps).isNotNull();
        }
    }
}
```

### E2E Tests with Testcontainers

Test against real database:

```java
@E2ETest
@Testcontainers
class PostgreSqlE2E {
    
    @Container
    private static final PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:15.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Test
    void shouldExecuteCustomFunction() throws SQLException {
        // Setup test data
        try (Connection conn = getConnection()) {
            conn.createStatement().execute(
                "CREATE TABLE employees (id INT, name VARCHAR(100), dept VARCHAR(50))"
            );
            conn.createStatement().execute(
                "INSERT INTO employees VALUES (1, 'Alice', 'IT'), (2, 'Bob', 'IT')"
            );
        }

        // Test custom function
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();
        PostgreSqlDSL dsl = registry
            .dslFor("postgresql", "15.0.0", PostgreSqlDSL.class)
            .orElseThrow();

        PreparedStatement ps = dsl.select()
            .column("dept")
            .expression(dsl.stringAgg("name").separator(", ").build())
                .as("names")
            .from("employees")
            .groupBy("dept")
            .buildPreparedStatement(getConnection());

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("names")).isEqualTo("Alice, Bob");
        }
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            postgres.getJdbcUrl(),
            postgres.getUsername(),
            postgres.getPassword()
        );
    }
}
```

## Performance Considerations

### Renderer Caching

DSL instances and renderers are cached by DSLRegistry:

```java
// First call: creates new DSL instance
DSL dsl1 = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Second call: returns cached instance (same object)
DSL dsl2 = registry.dslFor("mysql", "8.0.35").orElseThrow();

assertThat(dsl1).isSameAs(dsl2); // true
```

### Immutable AST Nodes

AST nodes are immutable, allowing safe sharing between threads:

```java
// Can be safely shared
ColumnReference col = ColumnReference.of("users", "name");

// Used by multiple threads
String sql1 = builder1.column(col).build();
String sql2 = builder2.column(col).build(); // Same instance, thread-safe
```

### Avoid Reflection

Use direct method calls instead of reflection:

```java
// ❌ BAD: Reflection overhead
Method method = strategy.getClass().getMethod("render", ...);
String result = (String) method.invoke(strategy, ...);

// ✅ GOOD: Direct call
String result = strategy.render(call, renderer, ctx);
```

## Complete Example: PostgreSQL Plugin

See the MySQL plugin implementation as a reference:

- [`jdsql-mysql/`](../../plugins/jdsql-mysql/) - Complete working example
- Provider: `MysqlDialectPluginProvider`
- DSL extension: `MysqlDSL` with `GROUP_CONCAT` support
- Custom rendering: `MysqlCustomFunctionCallRenderStrategy`
- Tests: Unit, integration, and E2E tests

## See Also

- [DSL Usage Guide](DSL_USAGE_GUIDE.md) - Examples of using the JDSQL DSL
- [Developer Guide](DEVELOPER_GUIDE.md) - Testing and development workflow
- [jdsql-core README](../../jdsql-core/README.md) - Core architecture and AST

