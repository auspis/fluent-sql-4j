# PostgreSQL Custom Functions Implementation Guide

## 📋 Overview

This guide provides step-by-step instructions for implementing PostgreSQL-specific custom functions in the r4j SQL DSL, following the same architectural patterns and best practices established for the MySQL implementation.

**Reference Implementation:** MySQL custom functions (see `MysqlDSL`, `MysqlCustomFunctionCallRenderStrategy`)

**Target Dialect:** PostgreSQL 15.x+

**Status:** Implementation Plan

---

## 🎯 Goals

1. Create `PostgreSqlDSL` extension with PostgreSQL-specific functions
2. Implement rendering strategy for PostgreSQL custom functions
3. Maintain architectural consistency with MySQL implementation
4. Ensure comprehensive test coverage (unit + integration)
5. Update documentation with PostgreSQL examples

---

## 📦 PostgreSQL Functions to Implement

### Priority 1: String Aggregation

|  Function  |                   Description                   | MySQL Equivalent |
|------------|-------------------------------------------------|------------------|
| STRING_AGG | Concatenates values with delimiter and ordering | GROUP_CONCAT     |

**Syntax:**

```sql
STRING_AGG(expression, delimiter [ORDER BY sort_expression])
```

**Example:**

```sql
SELECT department, STRING_AGG(name, ', ' ORDER BY name) AS employees
FROM employees
GROUP BY department;
```

### Priority 2: Conditional Functions

| Function |         Description          | MySQL Equivalent |
|----------|------------------------------|------------------|
| COALESCE | Returns first non-null value | COALESCE/IFNULL  |
| NULLIF   | Returns NULL if values equal | NULLIF           |

### Priority 3: Date/Time Functions

|  Function  |        Description         | MySQL Equivalent |
|------------|----------------------------|------------------|
| AGE        | Calculate age/interval     | DATEDIFF         |
| DATE_TRUNC | Truncate to precision      | DATE_FORMAT      |
| EXTRACT    | Extract date part          | EXTRACT          |
| TO_CHAR    | Format timestamp to string | DATE_FORMAT      |

### Priority 4: Array Functions

|    Function     |         Description         | MySQL Equivalent |
|-----------------|-----------------------------|------------------|
| ARRAY_AGG       | Aggregate values into array | JSON_ARRAYAGG    |
| ARRAY_TO_STRING | Convert array to string     | N/A              |
| STRING_TO_ARRAY | Convert string to array     | N/A              |

### Priority 5: JSON Functions (PostgreSQL-specific)

|     Function     |        Description        | MySQL Equivalent |
|------------------|---------------------------|------------------|
| JSONB_AGG        | Aggregate as JSONB array  | JSON_ARRAYAGG    |
| JSONB_OBJECT_AGG | Aggregate as JSONB object | JSON_OBJECTAGG   |
| ->               | JSON field accessor       | JSON_EXTRACT     |
| ->>              | JSON field as text        | JSON_UNQUOTE     |

---

## 🏗️ Architecture Overview

Following the same pattern as MySQL implementation:

```
PostgreSqlDSL (extends DSL)
    ↓ (creates)
CustomFunctionCall (AST node)
    ↓ (visited by)
PostgreSqlCustomFunctionCallRenderStrategy
    ↓ (renders)
PostgreSQL-specific SQL
```

### Key Components

1. **PostgreSqlDSL**: Extension of base DSL with PostgreSQL-specific methods
2. **CustomFunctionCall**: Generic AST node (already exists)
3. **PostgreSqlCustomFunctionCallRenderStrategy**: PostgreSQL-specific rendering logic
4. **PostgreSqlDialectPlugin**: Updated to provide PostgreSqlDSL

---

## 📝 Implementation Steps

### Step 1: Create PostgreSqlDSL

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/postgresql/dsl/PostgreSqlDSL.java`

**Purpose:** PostgreSQL-specific DSL extension with custom functions

```java
package lan.tlab.r4j.sql.plugin.builtin.postgresql.dsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.DSL;

/**
 * PostgreSQL-specific DSL extension providing access to PostgreSQL custom functions.
 * <p>
 * This class extends the base DSL with PostgreSQL-specific functions like:
 * <ul>
 *   <li>STRING_AGG - String aggregation with delimiter and ordering</li>
 *   <li>ARRAY_AGG - Array aggregation</li>
 *   <li>JSONB_AGG - JSONB array aggregation</li>
 *   <li>TO_CHAR - Format timestamp to string</li>
 *   <li>DATE_TRUNC - Truncate date to specified precision</li>
 *   <li>AGE - Calculate interval between dates</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre>{@code
 * PostgreSqlDSL postgres = (PostgreSqlDSL) registry.dslFor("postgresql", "15.0.0").orElseThrow();
 * 
 * String sql = postgres.select()
 *     .column("department")
 *     .expression(
 *         postgres.stringAgg("name")
 *             .orderBy("name")
 *             .separator(", ")
 *             .build()
 *     ).as("employees")
 *     .from("employees")
 *     .groupBy("department")
 *     .build();
 * }</pre>
 */
public class PostgreSqlDSL extends DSL {
    
    public PostgreSqlDSL(DialectRenderer renderer) {
        super(renderer);
    }
    
    /**
     * Creates a STRING_AGG function builder.
     * <p>
     * STRING_AGG concatenates input values into a string with a specified delimiter.
     * <p>
     * Example:
     * <pre>{@code
     * postgres.stringAgg("name")
     *     .separator(", ")
     *     .orderBy("name")
     *     .build()
     * }</pre>
     * <p>
     * Generated SQL: {@code STRING_AGG(name, ', ' ORDER BY name)}
     *
     * @param column the column name to aggregate
     * @return a builder for STRING_AGG options
     */
    public StringAggBuilder stringAgg(String column) {
        return new StringAggBuilder(column);
    }
    
    /**
     * Creates a STRING_AGG function builder with explicit table reference.
     *
     * @param table the table name
     * @param column the column name to aggregate
     * @return a builder for STRING_AGG options
     */
    public StringAggBuilder stringAgg(String table, String column) {
        return new StringAggBuilder(table, column);
    }
    
    /**
     * Creates an ARRAY_AGG function call.
     * <p>
     * ARRAY_AGG aggregates input values into an array.
     * <p>
     * Syntax: {@code ARRAY_AGG(expression [ORDER BY sort_expression])}
     *
     * @param column the column to aggregate
     * @return a builder for ARRAY_AGG options
     */
    public ArrayAggBuilder arrayAgg(String column) {
        return new ArrayAggBuilder(column);
    }
    
    /**
     * Creates a JSONB_AGG function call.
     * <p>
     * JSONB_AGG aggregates values into a JSONB array.
     * <p>
     * Syntax: {@code JSONB_AGG(expression [ORDER BY sort_expression])}
     *
     * @param column the column to aggregate
     * @return a CustomFunctionCall representing JSONB_AGG
     */
    public JsonbAggBuilder jsonbAgg(String column) {
        return new JsonbAggBuilder(column);
    }
    
    /**
     * Creates a TO_CHAR function call for date/time formatting.
     * <p>
     * Syntax: {@code TO_CHAR(timestamp, format)}
     * <p>
     * Example formats:
     * <ul>
     *   <li>{@code "YYYY-MM-DD"} - ISO date format</li>
     *   <li>{@code "YYYY-MM-DD HH24:MI:SS"} - ISO timestamp format</li>
     *   <li>{@code "Day, DD Month YYYY"} - Verbose date format</li>
     * </ul>
     *
     * @param expression the timestamp expression to format
     * @param format the format pattern
     * @return a CustomFunctionCall representing TO_CHAR
     */
    public ScalarExpression toChar(ScalarExpression expression, String format) {
        return new CustomFunctionCall(
            "TO_CHAR",
            List.of(expression, Literal.of(format)),
            Map.of()
        );
    }
    
    /**
     * Creates a DATE_TRUNC function call.
     * <p>
     * Truncates a timestamp to the specified precision.
     * <p>
     * Syntax: {@code DATE_TRUNC(field, timestamp)}
     * <p>
     * Valid fields: 'microseconds', 'milliseconds', 'second', 'minute', 
     * 'hour', 'day', 'week', 'month', 'quarter', 'year', 'decade', 
     * 'century', 'millennium'
     *
     * @param field the precision field
     * @param expression the timestamp expression
     * @return a CustomFunctionCall representing DATE_TRUNC
     */
    public ScalarExpression dateTrunc(String field, ScalarExpression expression) {
        return new CustomFunctionCall(
            "DATE_TRUNC",
            List.of(Literal.of(field), expression),
            Map.of()
        );
    }
    
    /**
     * Creates an AGE function call to calculate interval.
     * <p>
     * Syntax: {@code AGE(timestamp1, timestamp2)} or {@code AGE(timestamp)}
     * <p>
     * When called with one argument, calculates age from current date.
     * When called with two arguments, calculates interval between them.
     *
     * @param expressions one or two timestamp expressions
     * @return a CustomFunctionCall representing AGE
     */
    public ScalarExpression age(ScalarExpression... expressions) {
        if (expressions.length == 0 || expressions.length > 2) {
            throw new IllegalArgumentException("AGE requires 1 or 2 arguments");
        }
        return new CustomFunctionCall(
            "AGE",
            List.of(expressions),
            Map.of()
        );
    }
    
    /**
     * Creates a COALESCE function call.
     * <p>
     * Returns the first non-null expression.
     * <p>
     * Syntax: {@code COALESCE(expression1, expression2, ...)}
     *
     * @param expressions the expressions to evaluate
     * @return a CustomFunctionCall representing COALESCE
     */
    public ScalarExpression coalesce(ScalarExpression... expressions) {
        if (expressions.length < 2) {
            throw new IllegalArgumentException("COALESCE requires at least 2 arguments");
        }
        return new CustomFunctionCall(
            "COALESCE",
            List.of(expressions),
            Map.of()
        );
    }
    
    /**
     * Creates a NULLIF function call.
     * <p>
     * Returns NULL if the two expressions are equal, otherwise returns the first expression.
     * <p>
     * Syntax: {@code NULLIF(expression1, expression2)}
     *
     * @param expr1 the first expression
     * @param expr2 the second expression
     * @return a CustomFunctionCall representing NULLIF
     */
    public ScalarExpression nullIf(ScalarExpression expr1, ScalarExpression expr2) {
        return new CustomFunctionCall(
            "NULLIF",
            List.of(expr1, expr2),
            Map.of()
        );
    }
    
    /**
     * Builder for STRING_AGG function with all its options.
     */
    public class StringAggBuilder {
        private final ColumnReference column;
        private String orderBy;
        private String separator = ",";
        private boolean distinct = false;
        
        StringAggBuilder(String column) {
            this.column = ColumnReference.of("", column);
        }
        
        StringAggBuilder(String table, String column) {
            this.column = ColumnReference.of(table, column);
        }
        
        /**
         * Adds ORDER BY clause to STRING_AGG.
         * <p>
         * Example: {@code .orderBy("name DESC")}
         *
         * @param orderBy the order by expression
         * @return this builder
         */
        public StringAggBuilder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }
        
        /**
         * Sets the separator for STRING_AGG.
         * <p>
         * Default is comma (,).
         *
         * @param separator the separator string
         * @return this builder
         */
        public StringAggBuilder separator(String separator) {
            this.separator = separator;
            return this;
        }
        
        /**
         * Adds DISTINCT to STRING_AGG.
         * <p>
         * Example: {@code STRING_AGG(DISTINCT name, ', ')}
         *
         * @return this builder
         */
        public StringAggBuilder distinct() {
            this.distinct = true;
            return this;
        }
        
        /**
         * Builds the STRING_AGG CustomFunctionCall.
         *
         * @return a CustomFunctionCall representing STRING_AGG
         */
        public ScalarExpression build() {
            Map<String, Object> options = new HashMap<>();
            if (orderBy != null) {
                options.put("ORDER_BY", orderBy);
            }
            options.put("SEPARATOR", separator);
            options.put("DISTINCT", distinct);
            
            return new CustomFunctionCall(
                "STRING_AGG",
                List.of(column),
                options
            );
        }
    }
    
    /**
     * Builder for ARRAY_AGG function.
     */
    public class ArrayAggBuilder {
        private final ColumnReference column;
        private String orderBy;
        private boolean distinct = false;
        
        ArrayAggBuilder(String column) {
            this.column = ColumnReference.of("", column);
        }
        
        public ArrayAggBuilder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }
        
        public ArrayAggBuilder distinct() {
            this.distinct = true;
            return this;
        }
        
        public ScalarExpression build() {
            Map<String, Object> options = new HashMap<>();
            if (orderBy != null) {
                options.put("ORDER_BY", orderBy);
            }
            options.put("DISTINCT", distinct);
            
            return new CustomFunctionCall(
                "ARRAY_AGG",
                List.of(column),
                options
            );
        }
    }
    
    /**
     * Builder for JSONB_AGG function.
     */
    public class JsonbAggBuilder {
        private final ColumnReference column;
        private String orderBy;
        
        JsonbAggBuilder(String column) {
            this.column = ColumnReference.of("", column);
        }
        
        public JsonbAggBuilder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }
        
        public ScalarExpression build() {
            Map<String, Object> options = new HashMap<>();
            if (orderBy != null) {
                options.put("ORDER_BY", orderBy);
            }
            
            return new CustomFunctionCall(
                "JSONB_AGG",
                List.of(column),
                options
            );
        }
    }
}
```

---

### Step 2: Create PostgreSqlCustomFunctionCallRenderStrategy

**Location:** `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/postgresql/ast/visitor/sql/strategy/expression/PostgreSqlCustomFunctionCallRenderStrategy.java`

**Purpose:** PostgreSQL-specific rendering logic for custom functions

```java
package lan.tlab.r4j.sql.plugin.builtin.postgresql.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CustomFunctionCallRenderStrategy;

/**
 * PostgreSQL-specific rendering strategy for custom functions.
 * <p>
 * Handles PostgreSQL-specific syntax for functions like STRING_AGG, ARRAY_AGG,
 * JSONB_AGG, TO_CHAR, DATE_TRUNC, AGE, etc.
 * <p>
 * This strategy is similar to {@link MysqlCustomFunctionCallRenderStrategy} but
 * implements PostgreSQL-specific rendering rules.
 */
public class PostgreSqlCustomFunctionCallRenderStrategy implements CustomFunctionCallRenderStrategy {
    
    @Override
    public String render(CustomFunctionCall functionCall, SqlRenderer renderer, AstContext ctx) {
        return switch (functionCall.functionName()) {
            case "STRING_AGG" -> renderStringAgg(functionCall, renderer, ctx);
            case "ARRAY_AGG" -> renderArrayAgg(functionCall, renderer, ctx);
            case "JSONB_AGG" -> renderJsonbAgg(functionCall, renderer, ctx);
            case "TO_CHAR" -> renderToChar(functionCall, renderer, ctx);
            case "DATE_TRUNC" -> renderDateTrunc(functionCall, renderer, ctx);
            case "AGE" -> renderAge(functionCall, renderer, ctx);
            case "COALESCE" -> renderCoalesce(functionCall, renderer, ctx);
            case "NULLIF" -> renderNullIf(functionCall, renderer, ctx);
            default -> renderGeneric(functionCall, renderer, ctx);
        };
    }
    
    /**
     * Renders STRING_AGG with PostgreSQL syntax.
     * <p>
     * Syntax: {@code STRING_AGG([DISTINCT] expression, delimiter [ORDER BY sort_expression])}
     * <p>
     * Example: {@code STRING_AGG(DISTINCT name, ', ' ORDER BY name DESC)}
     */
    private String renderStringAgg(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("STRING_AGG(");
        
        // DISTINCT (PostgreSQL supports DISTINCT in STRING_AGG)
        boolean distinct = (Boolean) call.options().getOrDefault("DISTINCT", false);
        if (distinct) {
            sql.append("DISTINCT ");
        }
        
        // Column expression
        sql.append(call.arguments().get(0).accept(renderer, ctx));
        
        // SEPARATOR (required in PostgreSQL)
        String separator = (String) call.options().getOrDefault("SEPARATOR", ",");
        sql.append(", '").append(escapeSingleQuotes(separator)).append("'");
        
        // ORDER BY (inside the function call in PostgreSQL)
        if (call.options().containsKey("ORDER_BY")) {
            sql.append(" ORDER BY ").append(call.options().get("ORDER_BY"));
        }
        
        sql.append(")");
        return sql.toString();
    }
    
    /**
     * Renders ARRAY_AGG with PostgreSQL syntax.
     * <p>
     * Syntax: {@code ARRAY_AGG([DISTINCT] expression [ORDER BY sort_expression])}
     */
    private String renderArrayAgg(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("ARRAY_AGG(");
        
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
        
        sql.append(")");
        return sql.toString();
    }
    
    /**
     * Renders JSONB_AGG with PostgreSQL syntax.
     * <p>
     * Syntax: {@code JSONB_AGG(expression [ORDER BY sort_expression])}
     */
    private String renderJsonbAgg(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("JSONB_AGG(");
        
        // Column expression
        sql.append(call.arguments().get(0).accept(renderer, ctx));
        
        // ORDER BY
        if (call.options().containsKey("ORDER_BY")) {
            sql.append(" ORDER BY ").append(call.options().get("ORDER_BY"));
        }
        
        sql.append(")");
        return sql.toString();
    }
    
    /**
     * Renders TO_CHAR function.
     * <p>
     * Syntax: {@code TO_CHAR(timestamp, format)}
     */
    private String renderToChar(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String timestamp = call.arguments().get(0).accept(renderer, ctx);
        String format = call.arguments().get(1).accept(renderer, ctx);
        return "TO_CHAR(" + timestamp + ", " + format + ")";
    }
    
    /**
     * Renders DATE_TRUNC function.
     * <p>
     * Syntax: {@code DATE_TRUNC(field, timestamp)}
     */
    private String renderDateTrunc(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String field = call.arguments().get(0).accept(renderer, ctx);
        String timestamp = call.arguments().get(1).accept(renderer, ctx);
        return "DATE_TRUNC(" + field + ", " + timestamp + ")";
    }
    
    /**
     * Renders AGE function.
     * <p>
     * Syntax: {@code AGE(timestamp)} or {@code AGE(timestamp1, timestamp2)}
     */
    private String renderAge(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String args = call.arguments().stream()
            .map(arg -> arg.accept(renderer, ctx))
            .collect(Collectors.joining(", "));
        return "AGE(" + args + ")";
    }
    
    /**
     * Renders COALESCE function.
     * <p>
     * Syntax: {@code COALESCE(expression1, expression2, ...)}
     */
    private String renderCoalesce(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String args = call.arguments().stream()
            .map(arg -> arg.accept(renderer, ctx))
            .collect(Collectors.joining(", "));
        return "COALESCE(" + args + ")";
    }
    
    /**
     * Renders NULLIF function.
     * <p>
     * Syntax: {@code NULLIF(expression1, expression2)}
     */
    private String renderNullIf(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String expr1 = call.arguments().get(0).accept(renderer, ctx);
        String expr2 = call.arguments().get(1).accept(renderer, ctx);
        return "NULLIF(" + expr1 + ", " + expr2 + ")";
    }
    
    /**
     * Generic fallback rendering for unknown functions.
     * <p>
     * Renders as: {@code FUNCTION_NAME(arg1, arg2, ...)}
     */
    private String renderGeneric(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String args = call.arguments().stream()
            .map(arg -> arg.accept(renderer, ctx))
            .collect(Collectors.joining(", "));
        return call.functionName() + "(" + args + ")";
    }
    
    /**
     * Escapes single quotes in strings for SQL.
     */
    private String escapeSingleQuotes(String str) {
        return str.replace("'", "''");
    }
}
```

---

### Step 3: Update PostgreSQL SqlRenderer

**Location:** Find the existing PostgreSQL SqlRenderer (likely `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/postgresql/ast/visitor/sql/PostgreSqlRenderer.java` or similar)

**Changes:**

1. Add the custom function strategy field
2. Override the `visit(CustomFunctionCall)` method

```java
// Add at the class level
private final PostgreSqlCustomFunctionCallRenderStrategy customFunctionStrategy = 
    new PostgreSqlCustomFunctionCallRenderStrategy();

// Add at the end of the class
@Override
public String visit(CustomFunctionCall functionCall, AstContext ctx) {
    return customFunctionStrategy.render(functionCall, this, ctx);
}
```

**Imports to add:**

```java
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.plugin.builtin.postgresql.ast.visitor.sql.strategy.expression.PostgreSqlCustomFunctionCallRenderStrategy;
```

**Note:** If PostgreSQL SqlRenderer doesn't exist yet, you may need to create it following the pattern used in `MysqlDialectPlugin.createMySqlRenderer()`.

---

### Step 4: Update PostgreSqlDialectPlugin

**Location:** Find the existing PostgreSQL plugin (e.g., `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/postgresql/PostgreSqlDialectPlugin.java`)

**Changes:**

```java
@Override
public SqlDialectPlugin getPlugin() {
    return new SqlDialectPlugin(
        "postgresql",
        "^15.0.0",
        this::createDialectRenderer,
        this::createPostgreSqlDSL  // NEW: Add DSL supplier
    );
}

// Add new method
private DSL createPostgreSqlDSL() {
    DialectRenderer renderer = createDialectRenderer();
    return new PostgreSqlDSL(renderer);
}
```

**Imports to add:**

```java
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.plugin.builtin.postgresql.dsl.PostgreSqlDSL;
```

---

### Step 5: Create Unit Tests for PostgreSqlDSL

**Location:** `sql/src/test/java/lan/tlab/r4j/sql/plugin/builtin/postgresql/dsl/PostgreSqlDSLTest.java`

**Purpose:** Unit tests for PostgreSQL DSL builders

```java
package lan.tlab.r4j.sql.plugin.builtin.postgresql.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostgreSqlDSLTest {

    private PostgreSqlDSL dsl;

    @BeforeEach
    void setUp() {
        // Use a test renderer for PostgreSQL
        dsl = new PostgreSqlDSL(TestDialectRendererFactory.dialectRendererPostgreSql());
    }

    // STRING_AGG Tests
    
    @Test
    void stringAggBasic() {
        ScalarExpression expr = dsl.stringAgg("name").build();
        
        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("STRING_AGG");
        assertThat(call.arguments()).hasSize(1);
        assertThat(call.options()).containsEntry("SEPARATOR", ",");
    }
    
    @Test
    void stringAggWithSeparator() {
        ScalarExpression expr = dsl.stringAgg("name")
            .separator(", ")
            .build();
        
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.options()).containsEntry("SEPARATOR", ", ");
    }
    
    @Test
    void stringAggWithOrderBy() {
        ScalarExpression expr = dsl.stringAgg("name")
            .orderBy("name")
            .build();
        
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.options()).containsEntry("ORDER_BY", "name");
    }
    
    @Test
    void stringAggWithDistinct() {
        ScalarExpression expr = dsl.stringAgg("name")
            .distinct()
            .build();
        
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.options()).containsEntry("DISTINCT", true);
    }
    
    @Test
    void stringAggWithAllOptions() {
        ScalarExpression expr = dsl.stringAgg("email")
            .distinct()
            .orderBy("email DESC")
            .separator(" | ")
            .build();
        
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("STRING_AGG");
        assertThat(call.options())
            .containsEntry("DISTINCT", true)
            .containsEntry("ORDER_BY", "email DESC")
            .containsEntry("SEPARATOR", " | ");
    }
    
    @Test
    void stringAggWithTableReference() {
        ScalarExpression expr = dsl.stringAgg("users", "name")
            .separator(", ")
            .build();
        
        CustomFunctionCall call = (CustomFunctionCall) expr;
        ColumnReference col = (ColumnReference) call.arguments().get(0);
        assertThat(col.table()).isEqualTo("users");
        assertThat(col.column()).isEqualTo("name");
    }
    
    // ARRAY_AGG Tests
    
    @Test
    void arrayAggBasic() {
        ScalarExpression expr = dsl.arrayAgg("tags").build();
        
        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("ARRAY_AGG");
        assertThat(call.arguments()).hasSize(1);
    }
    
    @Test
    void arrayAggWithOrderBy() {
        ScalarExpression expr = dsl.arrayAgg("tags")
            .orderBy("tags")
            .build();
        
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.options()).containsEntry("ORDER_BY", "tags");
    }
    
    @Test
    void arrayAggWithDistinct() {
        ScalarExpression expr = dsl.arrayAgg("category")
            .distinct()
            .build();
        
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.options()).containsEntry("DISTINCT", true);
    }
    
    // JSONB_AGG Tests
    
    @Test
    void jsonbAggBasic() {
        ScalarExpression expr = dsl.jsonbAgg("data").build();
        
        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("JSONB_AGG");
    }
    
    @Test
    void jsonbAggWithOrderBy() {
        ScalarExpression expr = dsl.jsonbAgg("item")
            .orderBy("created_at")
            .build();
        
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.options()).containsEntry("ORDER_BY", "created_at");
    }
    
    // TO_CHAR Tests
    
    @Test
    void toCharWithDateFormat() {
        ScalarExpression expr = dsl.toChar(
            ColumnReference.of("orders", "created_at"),
            "YYYY-MM-DD"
        );
        
        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("TO_CHAR");
        assertThat(call.arguments()).hasSize(2);
    }
    
    @Test
    void toCharWithTimestampFormat() {
        ScalarExpression expr = dsl.toChar(
            ColumnReference.of("events", "timestamp"),
            "YYYY-MM-DD HH24:MI:SS"
        );
        
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.arguments().get(1)).isInstanceOf(Literal.class);
        Literal format = (Literal) call.arguments().get(1);
        assertThat(format.value()).isEqualTo("YYYY-MM-DD HH24:MI:SS");
    }
    
    // DATE_TRUNC Tests
    
    @Test
    void dateTruncDay() {
        ScalarExpression expr = dsl.dateTrunc(
            "day",
            ColumnReference.of("orders", "created_at")
        );
        
        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("DATE_TRUNC");
        assertThat(call.arguments()).hasSize(2);
    }
    
    @Test
    void dateTruncMonth() {
        ScalarExpression expr = dsl.dateTrunc(
            "month",
            ColumnReference.of("sales", "sale_date")
        );
        
        CustomFunctionCall call = (CustomFunctionCall) expr;
        Literal field = (Literal) call.arguments().get(0);
        assertThat(field.value()).isEqualTo("month");
    }
    
    // AGE Tests
    
    @Test
    void ageWithOneArgument() {
        ScalarExpression expr = dsl.age(
            ColumnReference.of("users", "birth_date")
        );
        
        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("AGE");
        assertThat(call.arguments()).hasSize(1);
    }
    
    @Test
    void ageWithTwoArguments() {
        ScalarExpression expr = dsl.age(
            ColumnReference.of("orders", "completed_at"),
            ColumnReference.of("orders", "created_at")
        );
        
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.arguments()).hasSize(2);
    }
    
    @Test
    void ageWithNoArguments_throwsException() {
        assertThatThrownBy(() -> dsl.age())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("AGE requires 1 or 2 arguments");
    }
    
    @Test
    void ageWithTooManyArguments_throwsException() {
        assertThatThrownBy(() -> dsl.age(
            Literal.of("2023-01-01"),
            Literal.of("2023-06-01"),
            Literal.of("2023-12-31")
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("AGE requires 1 or 2 arguments");
    }
    
    // COALESCE Tests
    
    @Test
    void coalesceWithTwoArguments() {
        ScalarExpression expr = dsl.coalesce(
            ColumnReference.of("users", "nickname"),
            ColumnReference.of("users", "name")
        );
        
        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("COALESCE");
        assertThat(call.arguments()).hasSize(2);
    }
    
    @Test
    void coalesceWithMultipleArguments() {
        ScalarExpression expr = dsl.coalesce(
            ColumnReference.of("users", "mobile"),
            ColumnReference.of("users", "phone"),
            Literal.of("N/A")
        );
        
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.arguments()).hasSize(3);
    }
    
    @Test
    void coalesceWithOneArgument_throwsException() {
        assertThatThrownBy(() -> dsl.coalesce(Literal.of("value")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("COALESCE requires at least 2 arguments");
    }
    
    // NULLIF Tests
    
    @Test
    void nullIfBasic() {
        ScalarExpression expr = dsl.nullIf(
            ColumnReference.of("products", "status"),
            Literal.of("deleted")
        );
        
        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("NULLIF");
        assertThat(call.arguments()).hasSize(2);
    }
}
```

---

### Step 6: Create Integration Tests

**Location:** `sql/src/test/java/lan/tlab/r4j/sql/plugin/builtin/postgresql/dsl/PostgreSqlDSLIntegrationTest.java`

**Purpose:** Integration tests with real SQL generation

```java
package lan.tlab.r4j.sql.plugin.builtin.postgresql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.sql.dsl.DSLRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostgreSqlDSLIntegrationTest {

    private PostgreSqlDSL postgres;

    @BeforeEach
    void setUp() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();
        postgres = (PostgreSqlDSL) registry.dslFor("postgresql", "15.0.0")
            .orElseThrow(() -> new IllegalStateException("PostgreSQL plugin not found"));
    }

    @Test
    void stringAggBasicQuery() {
        String sql = postgres.select()
            .column("department")
            .expression(
                postgres.stringAgg("name")
                    .separator(", ")
                    .build()
            ).as("employees")
            .from("employees")
            .groupBy("department")
            .build();

        assertThat(sql).contains("STRING_AGG(");
        assertThat(sql).contains("SEPARATOR ', '");
        assertThat(sql).contains("AS employees");
    }

    @Test
    void stringAggWithOrderBy() {
        String sql = postgres.select()
            .column("department")
            .expression(
                postgres.stringAgg("name")
                    .orderBy("salary DESC")
                    .separator(" | ")
                    .build()
            ).as("top_earners")
            .from("employees")
            .groupBy("department")
            .build();

        assertThat(sql).contains("ORDER BY salary DESC");
        assertThat(sql).contains("SEPARATOR ' | '");
    }

    @Test
    void stringAggWithDistinct() {
        String sql = postgres.select()
            .column("category")
            .expression(
                postgres.stringAgg("tag")
                    .distinct()
                    .separator(", ")
                    .build()
            ).as("unique_tags")
            .from("products")
            .groupBy("category")
            .build();

        assertThat(sql).contains("STRING_AGG(DISTINCT");
    }

    @Test
    void arrayAggQuery() {
        String sql = postgres.select()
            .column("user_id")
            .expression(
                postgres.arrayAgg("tag")
                    .orderBy("tag")
                    .build()
            ).as("tags")
            .from("user_tags")
            .groupBy("user_id")
            .build();

        assertThat(sql).contains("ARRAY_AGG(");
        assertThat(sql).contains("ORDER BY tag");
    }

    @Test
    void complexQueryWithMultipleFunctions() {
        String sql = postgres.select()
            .column("region")
            .expression(
                postgres.stringAgg("store_name")
                    .orderBy("revenue DESC")
                    .separator(" > ")
                    .build()
            ).as("top_stores")
            .expression(AggregateCall.sum(ColumnReference.of("stores", "revenue")))
                .as("total_revenue")
            .expression(AggregateCall.count(ColumnReference.of("stores", "id")))
                .as("store_count")
            .from("stores")
            .groupBy("region")
            .having()
                .column("store_count")
                .gt(3)
            .build();

        assertThat(sql).contains("STRING_AGG(");
        assertThat(sql).contains("SUM(");
        assertThat(sql).contains("COUNT(");
        assertThat(sql).contains("HAVING");
    }

    @Test
    void toCharFormatting() {
        String sql = postgres.select()
            .column("order_id")
            .expression(
                postgres.toChar(
                    ColumnReference.of("orders", "created_at"),
                    "YYYY-MM-DD"
                )
            ).as("order_date")
            .from("orders")
            .build();

        assertThat(sql).contains("TO_CHAR(");
        assertThat(sql).contains("'YYYY-MM-DD'");
    }

    @Test
    void dateTruncQuery() {
        String sql = postgres.select()
            .expression(
                postgres.dateTrunc(
                    "month",
                    ColumnReference.of("sales", "sale_date")
                )
            ).as("month")
            .expression(AggregateCall.sum(ColumnReference.of("sales", "amount")))
                .as("monthly_total")
            .from("sales")
            .groupBy("month")
            .build();

        assertThat(sql).contains("DATE_TRUNC(");
        assertThat(sql).contains("'month'");
    }
}
```

---

### Step 7: Create E2E Tests (Optional but Recommended)

**Location:** `sql/src/test/java/lan/tlab/r4j/sql/e2e/plugin/builtin/PostgreSqlDialectPluginE2E.java`

**Purpose:** End-to-end tests with Testcontainers

```java
package lan.tlab.r4j.sql.e2e.plugin.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import lan.tlab.r4j.sql.dsl.DSLRegistry;
import lan.tlab.r4j.sql.plugin.builtin.postgresql.dsl.PostgreSqlDSL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class PostgreSqlDialectPluginE2E {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    private static Connection connection;
    private static PostgreSqlDSL dsl;

    @BeforeAll
    static void setUp() throws Exception {
        connection = postgres.createConnection("");
        
        // Create test table and insert data
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE employees (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100),
                    department VARCHAR(50),
                    salary DECIMAL(10, 2)
                )
            """);
            
            stmt.execute("""
                INSERT INTO employees (name, department, salary) VALUES
                ('Alice', 'Engineering', 95000),
                ('Bob', 'Engineering', 85000),
                ('Charlie', 'Sales', 75000),
                ('Diana', 'Sales', 80000),
                ('Eve', 'Engineering', 90000)
            """);
        }
        
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();
        dsl = (PostgreSqlDSL) registry.dslFor("postgresql", "15.0.0").orElseThrow();
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void stringAggExecutesSuccessfully() throws Exception {
        String sql = dsl.select()
            .column("department")
            .expression(
                dsl.stringAgg("name")
                    .separator(", ")
                    .orderBy("salary DESC")
                    .build()
            ).as("employees")
            .from("employees")
            .groupBy("department")
            .orderBy("department")
            .build();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            assertThat(rs.next()).isTrue();
            String dept = rs.getString("department");
            String employees = rs.getString("employees");
            
            assertThat(dept).isEqualTo("Engineering");
            assertThat(employees).contains("Alice");
            assertThat(employees).contains("Bob");
            assertThat(employees).contains("Eve");
        }
    }

    @Test
    void stringAggWithDistinctExecutesSuccessfully() throws Exception {
        String sql = dsl.select()
            .expression(
                dsl.stringAgg("department")
                    .distinct()
                    .separator(" | ")
                    .build()
            ).as("departments")
            .from("employees")
            .build();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            assertThat(rs.next()).isTrue();
            String departments = rs.getString("departments");
            
            assertThat(departments).contains("Engineering");
            assertThat(departments).contains("Sales");
        }
    }

    @Test
    void arrayAggExecutesSuccessfully() throws Exception {
        String sql = dsl.select()
            .column("department")
            .expression(
                dsl.arrayAgg("name")
                    .orderBy("name")
                    .build()
            ).as("employee_names")
            .from("employees")
            .groupBy("department")
            .orderBy("department")
            .build();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("department")).isEqualTo("Engineering");
            // Array will be returned as PostgreSQL array type
        }
    }
}
```

---

## 🧪 Testing Strategy

### Test Coverage Requirements

Following MySQL implementation standards:

|     Test Type     | Minimum Coverage | Target Coverage |
|-------------------|------------------|-----------------|
| Unit Tests        | 80%              | 90%+            |
| Integration Tests | 70%              | 85%+            |
| E2E Tests         | Key scenarios    | All functions   |

### Test Categories

1. **Unit Tests (PostgreSqlDSLTest)**
   - Builder pattern validation
   - Option handling (ORDER BY, DISTINCT, SEPARATOR)
   - Argument validation
   - Edge cases and error conditions
2. **Integration Tests (PostgreSqlDSLIntegrationTest)**
   - SQL generation correctness
   - Complex queries with multiple functions
   - Combination with standard SQL features
   - GROUP BY and HAVING clauses
3. **E2E Tests (PostgreSqlDialectPluginE2E)**
   - Actual query execution with Testcontainers
   - Result validation
   - Performance verification
   - Compatibility testing

### Maven Commands

```bash
# Run unit tests only
./mvnw test -am -pl sql -Dtest=PostgreSqlDSLTest

# Run integration tests only
./mvnw verify -am -pl sql -Dit.test=PostgreSqlDSLIntegrationTest

# Run E2E tests only
./mvnw verify -am -pl sql -Dit.test=PostgreSqlDialectPluginE2E

# Run all PostgreSQL tests
./mvnw verify -am -pl sql -Dtest=*PostgreSql*
```

---

## 📊 Implementation Checklist

### Phase 1: Core Implementation

- [ ] Create `PostgreSqlDSL` class
  - [ ] STRING_AGG builder with all options
  - [ ] ARRAY_AGG builder
  - [ ] JSONB_AGG builder
  - [ ] TO_CHAR method
  - [ ] DATE_TRUNC method
  - [ ] AGE method
  - [ ] COALESCE method
  - [ ] NULLIF method
- [ ] Create `PostgreSqlCustomFunctionCallRenderStrategy`
  - [ ] STRING_AGG rendering
  - [ ] ARRAY_AGG rendering
  - [ ] JSONB_AGG rendering
  - [ ] TO_CHAR rendering
  - [ ] DATE_TRUNC rendering
  - [ ] AGE rendering
  - [ ] COALESCE rendering
  - [ ] NULLIF rendering
  - [ ] Generic fallback rendering
- [ ] Update `PostgreSqlDialectPlugin`
  - [ ] Add `dslSupplier` to plugin
  - [ ] Implement `createPostgreSqlDSL()` method

### Phase 2: Testing

- [ ] Unit Tests (PostgreSqlDSLTest)
  - [ ] STRING_AGG builder tests (basic, separator, orderBy, distinct, all options)
  - [ ] ARRAY_AGG builder tests
  - [ ] JSONB_AGG builder tests
  - [ ] TO_CHAR tests
  - [ ] DATE_TRUNC tests
  - [ ] AGE tests (1 arg, 2 args, validation)
  - [ ] COALESCE tests (validation)
  - [ ] NULLIF tests
- [ ] Integration Tests (PostgreSqlDSLIntegrationTest)
  - [ ] Basic STRING_AGG query
  - [ ] STRING_AGG with ORDER BY
  - [ ] STRING_AGG with DISTINCT
  - [ ] ARRAY_AGG query
  - [ ] Complex query with multiple functions
  - [ ] TO_CHAR formatting
  - [ ] DATE_TRUNC query
- [ ] E2E Tests (PostgreSqlDialectPluginE2E)
  - [ ] Setup Testcontainers with PostgreSQL 15
  - [ ] STRING_AGG execution test
  - [ ] STRING_AGG with DISTINCT execution test
  - [ ] ARRAY_AGG execution test

### Phase 3: Quality Assurance

- [ ] Code Review
  - [ ] Consistent with MySQL implementation
  - [ ] Follows project conventions
  - [ ] Proper JavaDoc (classes only)
  - [ ] No code duplication
- [ ] Verification
  - [ ] All tests passing
  - [ ] Code coverage targets met
  - [ ] No compilation warnings
  - [ ] Spotless formatting applied

### Phase 4: Documentation

- [ ] Update `README.md`
  - [ ] Add PostgreSQL section after MySQL
  - [ ] Include usage examples
  - [ ] Document all functions
- [ ] Update Implementation Plan
  - [ ] Mark tasks as complete
  - [ ] Document decisions
  - [ ] Note any deviations from plan

---

## 🎯 Success Criteria

Implementation is considered complete when:

1. ✅ All 8 Priority 1-2 functions implemented and tested
2. ✅ Unit test coverage ≥ 90%
3. ✅ Integration test coverage ≥ 85%
4. ✅ All E2E tests passing with Testcontainers
5. ✅ Code review approved
6. ✅ Documentation updated
7. ✅ No compilation warnings
8. ✅ `./mvnw spotless:apply` passes
9. ✅ `./mvnw clean verify -am -pl sql` passes

---

## 🔗 References

### Project Documentation

- **MySQL Implementation**: `data/wiki/CUSTOM_FUNCTIONS_IMPLEMENTATION_PLAN.md`
- **Plugin Architecture**: `data/wiki/README_PLUGIN_ARCHITECTURE.md`
- **Copilot Instructions**: `.github/copilot-instructions.md`

### Key Source Files

- **MysqlDSL**: `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/mysql/dsl/MysqlDSL.java`
- **MysqlCustomFunctionCallRenderStrategy**: `sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/mysql/ast/visitor/sql/strategy/expression/MysqlCustomFunctionCallRenderStrategy.java`
- **CustomFunctionCall**: `sql/src/main/java/lan/tlab/r4j/sql/ast/common/expression/scalar/function/CustomFunctionCall.java`

### PostgreSQL Documentation

- **PostgreSQL 15 Documentation**: https://www.postgresql.org/docs/15/
- **Aggregate Functions**: https://www.postgresql.org/docs/15/functions-aggregate.html
- **String Functions**: https://www.postgresql.org/docs/15/functions-string.html
- **Date/Time Functions**: https://www.postgresql.org/docs/15/functions-datetime.html
- **Array Functions**: https://www.postgresql.org/docs/15/functions-array.html

---

## 💡 Key Differences from MySQL

### Syntax Differences

|      Feature       |               MySQL                |          PostgreSQL          |
|--------------------|------------------------------------|------------------------------|
| String Aggregation | GROUP_CONCAT(col SEPARATOR ', ')   | STRING_AGG(col, ', ')        |
| Separator Position | After expression                   | As second argument           |
| ORDER BY Position  | After expression, before SEPARATOR | After delimiter              |
| DISTINCT Support   | Yes (before expression)            | Yes (before expression)      |
| Array Aggregation  | JSON_ARRAYAGG()                    | ARRAY_AGG()                  |
| Date Formatting    | DATE_FORMAT(date, '%Y-%m-%d')      | TO_CHAR(date, 'YYYY-MM-DD')  |
| Date Truncation    | DATE()                             | DATE_TRUNC('day', timestamp) |

### Implementation Differences

1. **Separator Handling**: In PostgreSQL, separator is a required argument, not an option
2. **ORDER BY Syntax**: PostgreSQL places ORDER BY inside the function call
3. **DISTINCT Position**: Same as MySQL but with different overall syntax
4. **Array vs JSON**: PostgreSQL has native array types

---

## 🚀 Getting Started

### Quick Start

1. **Review MySQL Implementation**

   ```bash
   # Study the MySQL implementation first
   cat sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/mysql/dsl/MysqlDSL.java
   ```
2. **Create PostgreSqlDSL**

   ```bash
   # Create the new DSL class
   mkdir -p sql/src/main/java/lan/tlab/r4j/sql/plugin/builtin/postgresql/dsl
   # Copy and modify MysqlDSL as a starting point
   ```
3. **Implement STRING_AGG First**
   - Focus on the most important function
   - Write tests as you go
   - Verify it works before moving to next function
4. **Follow the Checklist**
   - Work through Phase 1, then Phase 2, etc.
   - Mark items complete as you finish them
   - Don't skip testing!

### Development Workflow

For each function:
1. 📝 Implement DSL builder method
2. 🔧 Implement rendering strategy
3. ✅ Write unit tests
4. 🧪 Write integration tests
5. 🎯 Run E2E tests
6. 📖 Update documentation

---

_Document created: 2025-11-09_  
_Last updated: 2025-11-09_  
_Status: Implementation Guide (Not Started)_
