# Custom Functions Support - Implementation Plan

## 📋 Status Overview

Last Updated: 2025-11-09

### Task List

| #  |                      Task                       | Status |                       Notes                        |
|----|-------------------------------------------------|--------|----------------------------------------------------|
| 1  | Create CustomFunctionCall AST node              | ✅ DONE | New generic node for dialect-specific functions    |
| 2  | Add visit method to Visitor interface           | ✅ DONE | `T visit(CustomFunctionCall call, AstContext ctx)` |
| 3  | Implement fallback rendering in SqlRenderer     | ⏳ TODO | Generic rendering for CustomFunctionCall           |
| 4  | Implement fallback in PreparedStatementRenderer | ⏳ TODO | Generic PS rendering for CustomFunctionCall        |
| 5  | Make DSL class non-final and extendible         | ⏳ TODO | Allow dialects to extend base DSL                  |
| 6  | Update SqlDialectPlugin interface               | ⏳ TODO | Add `createDSL()` method                           |
| 7  | Create MySQLDSL extension                       | ⏳ TODO | MySQL-specific DSL with custom functions           |
| 8  | Implement MySQL custom function rendering       | ⏳ TODO | GROUP_CONCAT, IF, DATE_FORMAT, etc.                |
| 9  | Update MySQLDialectPlugin                       | ⏳ TODO | Return MySQLDSL from createDSL()                   |
| 10 | Update DSLRegistry                              | ⏳ TODO | Use plugin.createDSL() instead of new DSL()        |
| 11 | Update SelectProjectionBuilder                  | ⏳ TODO | Add expression(ScalarExpression) method            |
| 12 | Write unit tests for CustomFunctionCall         | ⏳ TODO | Test AST node and basic rendering                  |
| 13 | Write unit tests for MySQLDSL                   | ⏳ TODO | Test custom function builders                      |
| 14 | Write integration tests                         | ⏳ TODO | E2E tests with real SQL generation                 |
| 15 | Update documentation                            | ⏳ TODO | README with examples                               |

**Legend:** ✅ DONE | 🚧 IN PROGRESS | ⏳ TODO | ⏸️ BLOCKED | ❌ CANCELLED

---

## 🎯 Architecture Overview

### Current Architecture

```
DSLRegistry
    ↓ (uses)
SqlDialectPluginRegistry
    ↓ (provides)
SqlDialectPlugin
    ↓ (creates)
DialectRenderer (SqlRenderer + PreparedStatementRenderer)
    ↓ (used by)
DSL (creates builders)
    ↓ (creates)
Builders (SelectBuilder, InsertBuilder, etc.)
    ↓ (build)
AST Statements
    ↓ (visited by)
Visitor (renders SQL/PreparedStatement)
```

### New Architecture (with Custom Functions)

```
DSLRegistry
    ↓ (uses)
SqlDialectPluginRegistry
    ↓ (provides)
SqlDialectPlugin
    ↓ (creates)
    ├── DialectRenderer
    └── DSL (base or dialect-specific)
            ↓ (extended by)
        MySQLDSL (with custom function methods)
            ↓ (creates)
        CustomFunctionCall (new AST node)
            ↓ (visited by)
        Visitor (with new visit method)
            ↓ (implemented by)
        MySQLSqlRenderer (custom rendering)
```

---

## 📦 Components to Create/Modify

### 1. New AST Node: CustomFunctionCall

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/ast/common/expression/scalar/function/CustomFunctionCall.java`

**Purpose:** Generic AST node to represent dialect-specific functions

**Structure:**

```java
package lan.tlab.r4j.sql.ast.common.expression.scalar.function;

import java.util.List;
import java.util.Map;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents a custom/dialect-specific SQL function call.
 * <p>
 * This node is used for functions that are not part of standard SQL
 * and are specific to a particular database dialect (e.g., MySQL's GROUP_CONCAT,
 * PostgreSQL's STRING_AGG, etc.).
 * <p>
 * The function is represented by:
 * <ul>
 *   <li><b>functionName</b>: The name of the function (e.g., "GROUP_CONCAT")</li>
 *   <li><b>arguments</b>: List of scalar expressions as function arguments</li>
 *   <li><b>options</b>: Additional options/modifiers (e.g., ORDER BY, SEPARATOR, DISTINCT)</li>
 * </ul>
 *
 * @param functionName the name of the custom function
 * @param arguments the list of arguments to the function
 * @param options additional options/modifiers as key-value pairs
 */
public record CustomFunctionCall(
    String functionName,
    List<ScalarExpression> arguments,
    Map<String, Object> options
) implements ScalarExpression {

    /**
     * Compact constructor for validation.
     */
    public CustomFunctionCall {
        if (functionName == null || functionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Function name cannot be null or empty");
        }
        arguments = arguments == null ? List.of() : List.copyOf(arguments);
        options = options == null ? Map.of() : Map.copyOf(options);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
```

---

### 2. Update Visitor Interface

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/ast/visitor/Visitor.java`

**Change:** Add new method at the end of the interface

```java
// Add after the last visit method (after Lead, OverClause, etc.)

/**
 * Visits a custom function call.
 * <p>
 * Custom functions are dialect-specific functions not part of standard SQL.
 * Each dialect's renderer can implement specific rendering logic for these functions.
 *
 * @param functionCall the custom function call to visit
 * @param ctx the AST context
 * @return the result of visiting the custom function call
 */
T visit(CustomFunctionCall functionCall, AstContext ctx);
```

**Import to add:**

```java
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
```

---

### 3. Update SqlRenderer (Fallback Implementation)

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/ast/visitor/sql/SqlRenderer.java`

**Change:** Add implementation at the end of the class

```java
@Override
public String visit(CustomFunctionCall functionCall, AstContext ctx) {
    // Generic fallback rendering: FUNCTION_NAME(arg1, arg2, ...)
    String args = functionCall.arguments().stream()
        .map(arg -> arg.accept(this, ctx))
        .collect(java.util.stream.Collectors.joining(", "));
    
    return functionCall.functionName() + "(" + args + ")";
}
```

**Import to add:**

```java
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
```

---

### 4. Update PreparedStatementRenderer (Fallback Implementation)

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/ast/visitor/ps/PreparedStatementRenderer.java`

**Change:** Add implementation at the end of the class

```java
@Override
public PsDto visit(CustomFunctionCall functionCall, AstContext ctx) {
    // Generic fallback rendering for PreparedStatement
    List<Object> allParams = new ArrayList<>();
    
    String args = functionCall.arguments().stream()
        .map(arg -> {
            PsDto argDto = arg.accept(this, ctx);
            allParams.addAll(argDto.parameters());
            return argDto.sql();
        })
        .collect(java.util.stream.Collectors.joining(", "));
    
    String sql = functionCall.functionName() + "(" + args + ")";
    return new PsDto(sql, allParams);
}
```

**Imports to add:**

```java
import java.util.ArrayList;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
```

---

### 5. Make DSL Class Extendible

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/dsl/DSL.java`

**Changes:**
1. Remove `final` from class declaration
2. Change `private final` to `protected final` for renderer field

```java
// Before:
public final class DSL {
    private final DialectRenderer renderer;

// After:
public class DSL {
    protected final DialectRenderer renderer;
```

---

### 6. Update SqlDialectPlugin Interface

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/plugin/SqlDialectPlugin.java`

**Change:** Add new method to the record

```java
public record SqlDialectPlugin(
    String dialectName,
    String dialectVersion,
    Supplier<DialectRenderer> rendererSupplier,
    Supplier<DSL> dslSupplier  // NEW: supplier for DSL creation
) {
    // Compact constructor with validation
    public SqlDialectPlugin {
        Objects.requireNonNull(dialectName, "Dialect name must not be null");
        Objects.requireNonNull(dialectVersion, "Dialect version must not be null");
        Objects.requireNonNull(rendererSupplier, "Renderer supplier must not be null");
        Objects.requireNonNull(dslSupplier, "DSL supplier must not be null");
        
        if (dialectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Dialect name must not be empty");
        }
        if (dialectVersion.trim().isEmpty()) {
            throw new IllegalArgumentException("Dialect version must not be empty");
        }
    }
    
    /**
     * Creates a new DialectRenderer instance.
     */
    public DialectRenderer createRenderer() {
        return rendererSupplier.get();
    }
    
    /**
     * Creates a new DSL instance for this dialect.
     * <p>
     * The DSL instance may be the base DSL class or a dialect-specific
     * extension (e.g., MySQLDSL) that provides additional custom functions.
     *
     * @return a new DSL instance
     */
    public DSL createDSL() {
        return dslSupplier.get();
    }
}
```

**Import to add:**

```java
import lan.tlab.r4j.sql.dsl.DSL;
```

---

### 7. Create MySQLDSL Extension

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/mysql/dsl/MySQLDSL.java`

**Purpose:** MySQL-specific DSL with custom function support

**Full Implementation:**

```java
package lan.tlab.r4j.sql.plugin.builtin.mysql.dsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.ast.common.predicate.Predicate;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.DSL;

/**
 * MySQL-specific DSL extension that provides access to MySQL custom functions.
 * <p>
 * This class extends the base DSL with MySQL-specific functions like:
 * <ul>
 *   <li>GROUP_CONCAT - Concatenates values from a group</li>
 *   <li>IF - Conditional expression</li>
 *   <li>DATE_FORMAT - Formats a date value</li>
 *   <li>CONCAT_WS - Concatenates with separator</li>
 *   <li>RAND - Random number generator</li>
 *   <li>MD5 - MD5 hash</li>
 * </ul>
 */
public class MySQLDSL extends DSL {
    
    public MySQLDSL(DialectRenderer renderer) {
        super(renderer);
    }
    
    /**
     * Creates a GROUP_CONCAT function builder.
     * <p>
     * Example usage:
     * <pre>{@code
     * mysql.groupConcat("name")
     *     .separator(", ")
     *     .orderBy("name")
     *     .distinct()
     *     .build()
     * }</pre>
     *
     * @param column the column name to concatenate
     * @return a builder for GROUP_CONCAT options
     */
    public GroupConcatBuilder groupConcat(String column) {
        return new GroupConcatBuilder(column);
    }
    
    /**
     * Creates an IF function call.
     * <p>
     * Syntax: IF(condition, true_value, false_value)
     *
     * @param condition the condition to evaluate
     * @param trueValue the value to return if condition is true
     * @param falseValue the value to return if condition is false
     * @return a CustomFunctionCall representing IF
     */
    public ScalarExpression ifExpr(Predicate condition, Object trueValue, Object falseValue) {
        return new CustomFunctionCall(
            "IF",
            List.of(condition, toLiteral(trueValue), toLiteral(falseValue)),
            Map.of()
        );
    }
    
    /**
     * Creates a DATE_FORMAT function call.
     * <p>
     * Syntax: DATE_FORMAT(date, format)
     *
     * @param date the date expression to format
     * @param format the format string (e.g., "%Y-%m-%d")
     * @return a CustomFunctionCall representing DATE_FORMAT
     */
    public ScalarExpression dateFormat(ScalarExpression date, String format) {
        return new CustomFunctionCall(
            "DATE_FORMAT",
            List.of(date, Literal.of(format)),
            Map.of()
        );
    }
    
    /**
     * Creates a CONCAT_WS function call.
     * <p>
     * Syntax: CONCAT_WS(separator, str1, str2, ...)
     *
     * @param separator the separator string
     * @param columns the column names to concatenate
     * @return a CustomFunctionCall representing CONCAT_WS
     */
    public ScalarExpression concatWs(String separator, String... columns) {
        List<ScalarExpression> args = new ArrayList<>();
        args.add(Literal.of(separator));
        for (String col : columns) {
            args.add(ColumnReference.of("", col));
        }
        return new CustomFunctionCall("CONCAT_WS", args, Map.of());
    }
    
    /**
     * Creates a RAND function call.
     * <p>
     * Syntax: RAND()
     *
     * @return a CustomFunctionCall representing RAND
     */
    public ScalarExpression rand() {
        return new CustomFunctionCall("RAND", List.of(), Map.of());
    }
    
    /**
     * Creates an MD5 function call.
     * <p>
     * Syntax: MD5(expr)
     *
     * @param expr the expression to hash
     * @return a CustomFunctionCall representing MD5
     */
    public ScalarExpression md5(ScalarExpression expr) {
        return new CustomFunctionCall("MD5", List.of(expr), Map.of());
    }
    
    /**
     * Builder for GROUP_CONCAT function with all its options.
     */
    public class GroupConcatBuilder {
        private final String column;
        private String orderBy;
        private String separator = ",";
        private boolean distinct = false;
        
        GroupConcatBuilder(String column) {
            this.column = column;
        }
        
        /**
         * Adds ORDER BY clause to GROUP_CONCAT.
         *
         * @param column the column to order by
         * @return this builder
         */
        public GroupConcatBuilder orderBy(String column) {
            this.orderBy = column;
            return this;
        }
        
        /**
         * Sets the separator for GROUP_CONCAT.
         *
         * @param separator the separator string
         * @return this builder
         */
        public GroupConcatBuilder separator(String separator) {
            this.separator = separator;
            return this;
        }
        
        /**
         * Adds DISTINCT to GROUP_CONCAT.
         *
         * @return this builder
         */
        public GroupConcatBuilder distinct() {
            this.distinct = true;
            return this;
        }
        
        /**
         * Builds the GROUP_CONCAT CustomFunctionCall.
         *
         * @return a CustomFunctionCall representing GROUP_CONCAT
         */
        public ScalarExpression build() {
            Map<String, Object> options = new HashMap<>();
            if (orderBy != null) {
                options.put("ORDER_BY", orderBy);
            }
            options.put("SEPARATOR", separator);
            options.put("DISTINCT", distinct);
            
            return new CustomFunctionCall(
                "GROUP_CONCAT",
                List.of(ColumnReference.of("", column)),
                options
            );
        }
    }
    
    /**
     * Converts a value to a ScalarExpression.
     * If already a ScalarExpression, returns it as-is.
     * Otherwise, wraps it in a Literal.
     */
    private ScalarExpression toLiteral(Object value) {
        if (value instanceof ScalarExpression se) {
            return se;
        }
        return Literal.of(value);
    }
}
```

---

### 8. Create MySQL Custom Function Rendering Strategy

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/mysql/ast/visitor/sql/strategy/expression/MySQLCustomFunctionRenderStrategy.java`

**Purpose:** Specific rendering logic for MySQL custom functions

**Full Implementation:**

```java
package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

/**
 * MySQL-specific rendering strategy for custom functions.
 * <p>
 * Handles MySQL-specific syntax for functions like GROUP_CONCAT, IF, DATE_FORMAT, etc.
 */
public class MySQLCustomFunctionRenderStrategy {
    
    /**
     * Renders a MySQL custom function call.
     *
     * @param functionCall the custom function to render
     * @param renderer the SQL renderer
     * @param ctx the AST context
     * @return the rendered SQL string
     */
    public String render(CustomFunctionCall functionCall, SqlRenderer renderer, AstContext ctx) {
        return switch (functionCall.functionName()) {
            case "GROUP_CONCAT" -> renderGroupConcat(functionCall, renderer, ctx);
            case "IF" -> renderIf(functionCall, renderer, ctx);
            case "DATE_FORMAT" -> renderDateFormat(functionCall, renderer, ctx);
            case "CONCAT_WS" -> renderConcatWs(functionCall, renderer, ctx);
            case "RAND" -> renderRand(functionCall, renderer, ctx);
            case "MD5" -> renderMd5(functionCall, renderer, ctx);
            default -> renderGeneric(functionCall, renderer, ctx);
        };
    }
    
    private String renderGroupConcat(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("GROUP_CONCAT(");
        
        // DISTINCT
        boolean distinct = (Boolean) call.options().getOrDefault("DISTINCT", false);
        if (distinct) {
            sql.append("DISTINCT ");
        }
        
        // Column expression
        sql.append(call.arguments().get(0).accept(renderer, ctx));
        
        // ORDER BY
        if (call.options().containsKey("ORDER_BY")) {
            sql.append(" ORDER BY ").append(call.options().get("ORDER_BY"));
        }
        
        // SEPARATOR
        String separator = (String) call.options().getOrDefault("SEPARATOR", ",");
        sql.append(" SEPARATOR '").append(escapeSingleQuotes(separator)).append("'");
        
        sql.append(")");
        return sql.toString();
    }
    
    private String renderIf(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String condition = call.arguments().get(0).accept(renderer, ctx);
        String trueVal = call.arguments().get(1).accept(renderer, ctx);
        String falseVal = call.arguments().get(2).accept(renderer, ctx);
        return "IF(" + condition + ", " + trueVal + ", " + falseVal + ")";
    }
    
    private String renderDateFormat(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String date = call.arguments().get(0).accept(renderer, ctx);
        String format = call.arguments().get(1).accept(renderer, ctx);
        return "DATE_FORMAT(" + date + ", " + format + ")";
    }
    
    private String renderConcatWs(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String args = call.arguments().stream()
            .map(arg -> arg.accept(renderer, ctx))
            .collect(Collectors.joining(", "));
        return "CONCAT_WS(" + args + ")";
    }
    
    private String renderRand(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        return "RAND()";
    }
    
    private String renderMd5(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String arg = call.arguments().get(0).accept(renderer, ctx);
        return "MD5(" + arg + ")";
    }
    
    private String renderGeneric(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String args = call.arguments().stream()
            .map(arg -> arg.accept(renderer, ctx))
            .collect(Collectors.joining(", "));
        return call.functionName() + "(" + args + ")";
    }
    
    private String escapeSingleQuotes(String str) {
        return str.replace("'", "''");
    }
}
```

---

### 9. Update MySQL SqlRenderer

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/mysql/ast/visitor/sql/MySQLSqlRenderer.java`

**Change:** Override the visit method for CustomFunctionCall

```java
// Add at the end of the class, before the closing brace

private final MySQLCustomFunctionRenderStrategy customFunctionStrategy = 
    new MySQLCustomFunctionRenderStrategy();

@Override
public String visit(CustomFunctionCall functionCall, AstContext ctx) {
    return customFunctionStrategy.render(functionCall, this, ctx);
}
```

**Import to add:**

```java
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.sql.strategy.expression.MySQLCustomFunctionRenderStrategy;
```

---

### 10. Update MySQL PreparedStatementRenderer

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/mysql/ast/visitor/ps/MySQLPreparedStatementRenderer.java`

**Note:** May not exist yet. If it doesn't, MySQL uses the base PreparedStatementRenderer which already has the fallback implementation. No changes needed unless there's a MySQL-specific PS renderer.

---

### 11. Update MySQLDialectPlugin

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/mysql/MySQLDialectPlugin.java`

**Change:** Update the plugin to provide MySQLDSL

```java
// Before:
return new SqlDialectPlugin(
    "mysql",
    "^8.0.0",
    this::createDialectRenderer
);

// After:
return new SqlDialectPlugin(
    "mysql",
    "^8.0.0",
    this::createDialectRenderer,
    this::createMySQLDSL
);

// Add new method:
private DSL createMySQLDSL() {
    DialectRenderer renderer = createDialectRenderer();
    return new MySQLDSL(renderer);
}
```

**Import to add:**

```java
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.plugin.builtin.mysql.dsl.MySQLDSL;
```

---

### 12. Update Standard SQL Plugin

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/sql2016/StandardSQLDialectPlugin.java`

**Change:** Add DSL supplier (returns base DSL)

```java
// After:
return new SqlDialectPlugin(
    "standardsql",
    "2008",
    this::createDialectRenderer,
    this::createStandardDSL
);

// Add new method:
private DSL createStandardDSL() {
    DialectRenderer renderer = createDialectRenderer();
    return new DSL(renderer);
}
```

**Import to add:**

```java
import lan.tlab.r4j.sql.dsl.DSL;
```

---

### 13. Update DSLRegistry

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/dsl/DSLRegistry.java`

**Change:** Use plugin.createDSL() instead of new DSL(renderer)

Find the `dslFor` method and update it:

```java
// Before:
public Result<DSL> dslFor(String dialect, String version) {
    return pluginRegistry.getDialectRenderer(dialect, version)
        .map(DSL::new);
}

// After:
public Result<DSL> dslFor(String dialect, String version) {
    // Get the plugin first
    List<SqlDialectPlugin> matchingPlugins = pluginRegistry.findMatchingPlugins(
        pluginRegistry.getPluginsForDialect(dialect), 
        version
    );
    
    if (matchingPlugins.isEmpty()) {
        return new Failure<>("No plugin found for dialect '" + dialect + "' version '" + version + "'");
    }
    
    SqlDialectPlugin plugin = matchingPlugins.get(0);
    return new Success<>(plugin.createDSL());
}
```

**Note:** This requires adding a helper method to SqlDialectPluginRegistry to expose plugins. Alternative approach below:

**Better Approach - Update SqlDialectPluginRegistry:**

Add method to SqlDialectPluginRegistry:

```java
/**
 * Retrieves a {@link SqlDialectPlugin} for the specified SQL dialect and version.
 *
 * @param dialect the name of the SQL dialect
 * @param version the database version
 * @return a result containing the plugin, or a failure if not found
 */
public Result<SqlDialectPlugin> getPlugin(String dialect, String version) {
    if (dialect == null) {
        return new Failure<>("Dialect name must not be null");
    }

    List<SqlDialectPlugin> dialectPlugins =
            plugins.getOrDefault(getNormalizedDialect(dialect), Collections.emptyList());

    List<SqlDialectPlugin> matchingPlugins = findMatchingPlugins(dialectPlugins, version);

    if (matchingPlugins.isEmpty()) {
        String versionInfo = version != null ? " version '" + version + "'" : "";
        return new Failure<>("No plugin found for dialect '" + dialect + "'" + versionInfo
                + ". Supported dialects: " + getSupportedDialects());
    }

    if (matchingPlugins.size() > 1) {
        logMultipleMatches(dialect, version, matchingPlugins);
    }

    return new Success<>(matchingPlugins.get(0));
}
```

Then update DSLRegistry:

```java
public Result<DSL> dslFor(String dialect, String version) {
    return pluginRegistry.getPlugin(dialect, version)
        .map(SqlDialectPlugin::createDSL);
}
```

---

### 14. Update SelectProjectionBuilder

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/dsl/select/SelectProjectionBuilder.java`

**Change:** Add method to accept ScalarExpression

```java
// Add after existing methods, before the build() method

/**
 * Adds a scalar expression to the projection.
 * <p>
 * This method allows adding any scalar expression, including custom functions
 * from dialect-specific DSL extensions.
 * <p>
 * Example:
 * <pre>{@code
 * MySQLDSL mysql = ...;
 * select()
 *     .expression(mysql.groupConcat("name").separator(", ").build())
 *     .as("names")
 *     .from("users")
 * }</pre>
 *
 * @param expression the scalar expression to add
 * @return this builder
 */
public SelectProjectionBuilder expression(ScalarExpression expression) {
    finalizePendingProjection();
    pendingProjection = new ScalarExpressionProjection(expression);
    return this;
}
```

**Import to add:**

```java
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
```

---

## 🧪 Testing Strategy

### Unit Tests

#### 1. CustomFunctionCall Tests

**Location:** `sql/src/test/java/lan/tlab/r4j/sql/ast/common/expression/scalar/function/CustomFunctionCallTest.java`

Test cases:
- Creation with valid parameters
- Validation (null/empty function name)
- Immutability of arguments and options
- Accept visitor method

#### 2. MySQLDSL Tests

**Location:** `sql/src/test/java/lan/tlab/r4j/sql/plugin/builtin/mysql/dsl/MySQLDSLTest.java`

Test cases:
- groupConcat() builder with various options
- ifExpr() with different value types
- dateFormat() function
- concatWs() with multiple columns
- rand() function
- md5() function

#### 3. MySQLCustomFunctionRenderStrategy Tests

**Location:** `sql/src/test/java/lan/tlab/r4j/sql/plugin/builtin/mysql/ast/visitor/sql/strategy/expression/MySQLCustomFunctionRenderStrategyTest.java`

Test cases:
- GROUP_CONCAT rendering with all options
- IF function rendering
- DATE_FORMAT rendering
- Generic fallback rendering

### Integration Tests

#### Location: `sql/src/test/java/lan/tlab/r4j/sql/e2e/MySQLCustomFunctionsE2E.java`

Test scenarios:
1. GROUP_CONCAT in SELECT
2. GROUP_CONCAT with GROUP BY and HAVING
3. IF function in projection
4. DATE_FORMAT in WHERE clause
5. RAND in ORDER BY
6. Combined query with multiple custom functions

---

## 📝 Usage Examples

### Example 1: Simple GROUP_CONCAT

```java
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
MySQLDSL mysql = (MySQLDSL) registry.dslFor("mysql", "8.0.35").orElseThrow();

String sql = mysql.select()
    .expression(mysql.groupConcat("name").separator(", ").build())
    .as("names")
    .from("users")
    .build();

// Output: SELECT GROUP_CONCAT("users"."name" SEPARATOR ', ') AS names FROM "users"
```

### Example 2: GROUP_CONCAT with ORDER BY and DISTINCT

```java
String sql = mysql.select()
    .column("department")
    .expression(
        mysql.groupConcat("email")
            .distinct()
            .orderBy("email")
            .separator("; ")
            .build()
    ).as("emails")
    .from("employees")
    .groupBy("department")
    .build();

// Output: 
// SELECT "employees"."department", 
//        GROUP_CONCAT(DISTINCT "employees"."email" ORDER BY email SEPARATOR '; ') AS emails 
// FROM "employees" 
// GROUP BY "employees"."department"
```

### Example 3: IF Function

```java
String sql = mysql.select()
    .column("name")
    .expression(
        mysql.ifExpr(
            new Comparison(
                ColumnReference.of("users", "age"), 
                Operator.GREATER_THAN, 
                Literal.of(18)
            ),
            "Adult",
            "Minor"
        )
    ).as("age_group")
    .from("users")
    .build();

// Output: 
// SELECT "users"."name", 
//        IF("users"."age" > 18, 'Adult', 'Minor') AS age_group 
// FROM "users"
```

### Example 4: DATE_FORMAT

```java
String sql = mysql.select()
    .column("order_id")
    .expression(
        mysql.dateFormat(
            ColumnReference.of("orders", "created_at"),
            "%Y-%m-%d %H:%i:%s"
        )
    ).as("formatted_date")
    .from("orders")
    .where("status").eq("completed")
    .build();

// Output: 
// SELECT "orders"."order_id", 
//        DATE_FORMAT("orders"."created_at", '%Y-%m-%d %H:%i:%s') AS formatted_date 
// FROM "orders" 
// WHERE "orders"."status" = 'completed'
```

### Example 5: Random Ordering

```java
String sql = mysql.select()
    .column("id", "title")
    .from("articles")
    .orderBy(mysql.rand())
    .limit(10)
    .build();

// Output: 
// SELECT "articles"."id", "articles"."title" 
// FROM "articles" 
// ORDER BY RAND() 
// LIMIT 10
```

### Example 6: Complex Query

```java
String sql = mysql.select()
    .column("category")
    .expression(
        mysql.groupConcat("product_name")
            .orderBy("price DESC")
            .separator(" | ")
            .build()
    ).as("top_products")
    .expression(AggregateCall.avg(ColumnReference.of("products", "price")))
        .as("avg_price")
    .expression(AggregateCall.count(ColumnReference.of("products", "id")))
        .as("product_count")
    .from("products")
    .groupBy("category")
    .having(AggregateCall.count(ColumnReference.of("products", "id")))
        .greaterThan(5)
    .orderBy("avg_price DESC")
    .build();

// Output:
// SELECT "products"."category", 
//        GROUP_CONCAT("products"."product_name" ORDER BY price DESC SEPARATOR ' | ') AS top_products,
//        AVG("products"."price") AS avg_price,
//        COUNT("products"."id") AS product_count
// FROM "products"
// GROUP BY "products"."category"
// HAVING COUNT("products"."id") > 5
// ORDER BY avg_price DESC
```

---

## 🔄 Implementation Order

Execute tasks in this order to minimize dependencies:

### Phase 1: Core Infrastructure (Tasks 1-5)

1. Create CustomFunctionCall AST node
2. Add visit method to Visitor interface
3. Implement fallback in SqlRenderer
4. Implement fallback in PreparedStatementRenderer
5. Make DSL class extendible

### Phase 2: Plugin Updates (Tasks 6, 10, 13)

6. Update SqlDialectPlugin interface
7. Update SqlDialectPluginRegistry (add getPlugin method)
8. Update DSLRegistry

### Phase 3: MySQL Implementation (Tasks 7-9, 11)

7. Create MySQLDSL extension
8. Create MySQL custom function rendering strategy
9. Update MySQL SqlRenderer
10. Update SelectProjectionBuilder
    9b. Update MySQLDialectPlugin

### Phase 4: Standard SQL Plugin Update (Task 12)

12. Update StandardSQLDialectPlugin

### Phase 5: Testing (Tasks 14-15)

14. Write unit tests
15. Write integration tests

### Phase 6: Documentation (Task 16)

16. Update README and documentation

---

## 🚨 Important Notes

### Breaking Changes

- `SqlDialectPlugin` record now requires a 4th parameter (dslSupplier)
- All existing plugin implementations must be updated
- DSL class is no longer final

### Backward Compatibility

- Not required per user request
- All plugins must be updated simultaneously

### Migration Path

1. Update core infrastructure first
2. Update all plugins (MySQL, StandardSQL)
3. Update tests
4. Deploy as a single release

---

## ✅ Completion Criteria

Each task is considered complete when:
1. Code is implemented according to specifications
2. Code compiles without errors
3. `./mvnw spotless:apply` passes
4. Unit tests are written and pass
5. Integration tests pass (where applicable)
6. Documentation is updated

---

## 📚 References

### Key Files to Review

- `sql/src/main/java/lan/tlab/r4j/sql/ast/visitor/Visitor.java`
- `sql/src/main/java/lan/tlab/r4j/sql/ast/visitor/sql/SqlRenderer.java`
- `sql/src/main/java/lan/tlab/r4j/sql/plugin/SqlDialectPlugin.java`
- `sql/src/main/java/lan/tlab/r4j/sql/dsl/DSL.java`
- `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/mysql/MySQLDialectPlugin.java`

### Existing Patterns to Follow

- AST nodes: See `AggregateCall.java` for scalar expression examples
- Render strategies: See `MySqlConcatRenderStrategy.java`
- DSL builders: See `SelectProjectionBuilder.java`
- Tests: See `SelectDSLIntegrationTest.java`

### Coding Style Guidelines

- **JavaDoc for classes:** Required - Document purpose, usage, and examples for main classes
- **JavaDoc for methods:** NOT required - Keep methods clean without JavaDoc unless absolutely necessary
- **JavaDoc for public APIs:** Only if the method signature is not self-explanatory
- **Follow project conventions:** See `.github/copilot-instructions.md` for full guidelines

---

## � Development Workflow

For each task, follow this process:

### Step-by-Step Process

1. **📖 Read Plan**
   - Read this file to identify the next task
   - Review the task specifications and requirements
   - Check dependencies and prerequisites
2. **💻 Implement**
   - Write the code according to specifications
   - Write corresponding unit tests
   - Follow project conventions (see `.github/copilot-instructions.md`)
3. **🔨 Verify Compilation**
   - Run: `./mvnw clean compile -am -pl sql`
   - **Important:** Use `-am` flag (also-make) for multi-module projects
   - Fix any compilation errors before proceeding
4. **✅ Run Tests**
   - Run: `./mvnw clean test -am -pl sql`
   - **Important:** Use `-am` flag to compile dependencies
   - Ensure all tests pass (both new and existing)
5. **🔍 Request Review**
   - Ask for code review
   - Explain changes and decisions made
   - Wait for approval or feedback
6. **🔧 Apply Feedback**
   - Implement any changes requested in review
   - Re-run compilation and tests
   - Request re-review if needed
7. **✔️ Commit** (Done by user)
   - User will handle git commit
   - Move to next task
8. **🔄 Repeat**
   - Update task status in this file
   - Move to next task
   - Repeat process

### Maven Commands Reference

```bash
# Compile (with dependencies)
./mvnw clean compile -am -pl sql

# Run tests (with dependencies)
./mvnw clean test -am -pl sql

# Run specific test class
./mvnw test -am -pl sql -Dtest=CustomFunctionCallTest

# Format code (before commit)
./mvnw spotless:apply

# Verify everything
./mvnw clean verify -am -pl sql
```

### Important Notes

- **Always use `-am` flag** when working with `sql` module
- **Run `spotless:apply`** before requesting review
- **Write tests immediately** after implementation
- **Don't skip verification steps** even if confident

---

## �🔍 Current Progress

**Last Task Completed:** None (planning phase)
**Next Task:** Task 1 - Create CustomFunctionCall AST node
**Blockers:** None
**Notes:** Ready to start implementation

---

_This document will be updated after each task completion to track progress._
