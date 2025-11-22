## AST and the SQL Categories

An **Abstract Syntax Tree (AST)** is a fundamental data structure in computer science, used by compilers and interpreters to represent the syntactic structure of source code. It is a hierarchical, tree-like structure where each node denotes a construct occurring in the source code.

For SQL, the AST acts as an essential intermediary representation of a query. It **removes ambiguity** and irrelevant details (like parentheses, semicolons, and whitespace), focusing solely on the structural and conceptual components of the statement.

SQL statements are broadly categorized by their function, and the AST naturally groups these operations:

* **Data Definition Language (DDL):** Statements that **define or modify the structure** of database objects (e.g., `CREATE`, `DROP`).
* **Data Manipulation Language (DML):** Statements that **modify data** within the objects (e.g., `INSERT`, `UPDATE`, `DELETE`).
* **Data Query Language (DQL):** Statements used for **retrieving data** (e.g., `SELECT`).
* **Data Control Language (DCL):** Statements used to **control access rights** to the data and objects (e.g., `GRANT`, `REVOKE`).

The AST structure begins with a **Statement** node, classified by one of these four types, which then branches out to define the query's full logic.

---

## Core Structure and Grouping

The SQL AST is built upon a hierarchical model where every query begins with a **Statement** node, corresponding to the categories defined above.

| Category (Grouping) | Core Statement Nodes                                        | Purpose in the AST                                                                                                   |
|:--------------------|:------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------|
| **DQL**             | **`Select Statement`**.                                     | The primary node for data retrieval; it often forms the most complex subtrees due to the number of possible clauses. |
| **DML**             | `Insert Statement`, `Update Statement`, `Delete Statement`. | Nodes representing operations that modify data within existing database objects.                                     |
| **DDL**             | `Create Statement`, `Drop Statement`, `Alter Statement`.    | Nodes representing operations that define or modify the structure of database objects (schemas, tables, indexes).    |
| **DCL**             | `Grant Statement`, `Revoke Statement`.                      | Nodes for managing access rights and permissions on database objects.                                                |

Below the statement level, the AST branches into the following functional units:

| Functional Component | Example Nodes                                                                              | Role in the Query Structure                                                                                      |
|:---------------------|:-------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------|
| **Clauses**          | `Projection Clause` (SELECT list), `Source Clause` (FROM), `Filter Clause` (WHERE/HAVING). | Structural nodes that define the major sections and organization of DML/DQL logic.                               |
| **Predicates**       | `Equals`, `LessThan`, `And`, `Or`, `In predicate`.                                         | Nodes that **evaluate to a boolean value** (TRUE/FALSE) used in conditional logic.                               |
| **Expressions**      | `Addition`, `Sum Function`, `Case Expression`.                                             | Nodes that **evaluate to a scalar value** (number, string, date), forming the basis of calculations and results. |
| **Catalog Objects**  | `Table Identifier`, `Column Identifier`, `Schema Identifier`.                              | References to database entities, forming the terminal leaves that define the data sources or targets.            |

---

## Relationships Within the AST

The connectivity (edges) in the AST defines its coherence and structure, relying on **Composition** (structural grouping) and **Inheritance** (type hierarchy).

### 1. Structural Relationships (Composition)

These relationships define the parent-child structure and are critical for preserving the query's meaning and execution order:

* **Statement-to-Clause:** A high-level **Statement** node is linked to its constituent **Clause** nodes. The relationships are often named properties (e.g., `sql:from`, `sql:where`) to clarify their grammatical role.
* **Argument Ordering:** Within **Expressions**, **Predicates**, or **Identifiers**, child nodes (arguments/operands) are stored in an **ordered list** (often referred to as `sql:args`). This ordering is crucial for correct interpretation (e.g., a function's arguments must be in the correct sequence).
* **Hierarchical Identifiers:** A qualified reference (e.g., `schema.table.column`) is represented as an ordered sequence of identifier nodes, with the order determining the scope of the reference.

### 2. Hierarchical Relationships (Inheritance)

Conceptual groupings are maintained through an inheritance structure (a textual class hierarchy), ensuring that specific constructs are correctly classified under broader types:

* `Statement` $\rightarrow$ `Data Definition Statement` (DDL) $\rightarrow$ **`Create Statement`**.
* `Expression` $\rightarrow$ `Operator` $\rightarrow$ `Logical Operator` $\rightarrow$ **`And`**.

In summary, the SQL AST transforms a raw SQL query into a precise, navigable graph where the nodes are the language concepts (grouped by DDL, DML, DQL, DCL) and the ordered edges dictate the exact logical and structural relationships between them.

sources: [AST](https://ns.inria.fr/ast/sql/index.html)

---

## SQL DSL Overview

The SQL DSL provides a fluent, type-safe API for building SQL queries programmatically. It supports two levels of functionality:

1. **Standard SQL DSL**: Base functionality available for all SQL dialects (SQL:2008/2016 compliant)
2. **Dialect-Specific DSL**: Extended functionality for specific databases (e.g., MySQL, PostgreSQL)

### Getting Started

```java
// Get DSL for a specific dialect
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("standardsql", "2008").orElseThrow();

// Or for MySQL-specific features
MysqlDSL mysql = (MysqlDSL) registry.dslFor("mysql", "8.0.35").orElseThrow();
```

### Standard SQL DSL Features

The base DSL provides:

- **SELECT Queries**: Projections, joins, filtering, grouping, ordering
- **INSERT Statements**: Single and batch inserts
- **UPDATE Statements**: Conditional updates
- **DELETE Statements**: Conditional deletes
- **MERGE Statements**: Upsert operations (SQL:2008)
- **CREATE TABLE**: Table definitions with constraints
- **Window Functions**: ROW_NUMBER, RANK, DENSE_RANK, NTILE, LAG, LEAD
- **JSON Functions**: JSON_EXISTS, JSON_VALUE, JSON_QUERY (SQL:2016)
- **Aggregate Functions**: COUNT, SUM, AVG, MIN, MAX
- **Prepared Statements**: Full support for parameterized queries

### MySQL-Specific DSL Features

The `MysqlDSL` extension adds MySQL-specific functionality:

- **GROUP_CONCAT**: String aggregation with custom separators and ordering
- Additional custom functions (coming soon): IF, DATE_FORMAT, CONCAT_WS, etc.

---

## Standard SQL DSL Examples

### SELECT with Window Functions

```java
DSL dsl = registry.dslFor("standardsql", "2008").orElseThrow();

String sql = dsl.select()
    .column("name")
    .column("department")
    .rowNumber()
        .partitionBy("department")
        .orderByDesc("salary")
        .as("rank")
    .from("employees")
    .build();
```

**Generated SQL:**

```sql
SELECT "employees"."name", 
       "employees"."department", 
       ROW_NUMBER() OVER (PARTITION BY "employees"."department" ORDER BY "employees"."salary" DESC) AS rank
FROM "employees"
```

### SELECT with JSON Functions

```java
String sql = dsl.select()
    .column("user_id")
    .jsonValue("profile", "$.email")
        .returning("VARCHAR(255)")
        .as("email")
    .jsonExists("settings", "$.notifications")
        .as("has_notifications")
    .from("users")
    .build();
```

**Generated SQL:**

```sql
SELECT "users"."user_id",
       JSON_VALUE("users"."profile", '$.email' RETURNING VARCHAR(255)) AS email,
       JSON_EXISTS("users"."settings", '$.notifications') AS has_notifications
FROM "users"
```

---

## MySQL-Specific DSL Examples

### GROUP_CONCAT - Basic Usage

```java
MysqlDSL mysql = (MysqlDSL) registry.dslFor("mysql", "8.0.35").orElseThrow();

String sql = mysql.select()
    .column("department")
    .expression(
        mysql.groupConcat("name")
            .separator(", ")
            .build()
    ).as("employee_names")
    .from("employees")
    .groupBy("department")
    .build();
```

**Generated SQL:**

```sql
SELECT "employees"."department",
       GROUP_CONCAT("employees"."name" SEPARATOR ', ') AS employee_names
FROM "employees"
GROUP BY "employees"."department"
```

### GROUP_CONCAT - Advanced Usage with DISTINCT and ORDER BY

```java
String sql = mysql.select()
    .column("category")
    .expression(
        mysql.groupConcat("product_name")
            .distinct()
            .orderBy("price DESC")
            .separator(" | ")
            .build()
    ).as("top_products")
    .from("products")
    .groupBy("category")
    .build();
```

**Generated SQL:**

```sql
SELECT "products"."category",
       GROUP_CONCAT(DISTINCT "products"."product_name" ORDER BY price DESC SEPARATOR ' | ') AS top_products
FROM "products"
GROUP BY "products"."category"
```

### GROUP_CONCAT - Complex Query Example

```java
String sql = mysql.select()
    .column("department")
    .expression(
        mysql.groupConcat("name")
            .orderBy("salary DESC")
            .separator(", ")
            .build()
    ).as("employees_by_salary")
    .expression(AggregateCall.avg(ColumnReference.of("employees", "salary")))
        .as("avg_salary")
    .expression(AggregateCall.count(ColumnReference.of("employees", "id")))
        .as("employee_count")
    .from("employees")
    .groupBy("department")
    .having()
        .column("employee_count")
        .gt(5)
    .orderBy("avg_salary DESC")
    .build();
```

**Generated SQL:**

```sql
SELECT "employees"."department",
       GROUP_CONCAT("employees"."name" ORDER BY salary DESC SEPARATOR ', ') AS employees_by_salary,
       AVG("employees"."salary") AS avg_salary,
       COUNT("employees"."id") AS employee_count
FROM "employees"
GROUP BY "employees"."department"
HAVING COUNT("employees"."id") > 5
ORDER BY avg_salary DESC
```

### Combining Standard and MySQL-Specific Features

```java
// Use both standard window functions and MySQL GROUP_CONCAT
String sql = mysql.select()
    .column("region")
    .rowNumber()
        .partitionBy("region")
        .orderByDesc("total_sales")
        .as("region_rank")
    .expression(
        mysql.groupConcat("store_name")
            .orderBy("total_sales DESC")
            .separator(" > ")
            .build()
    ).as("top_stores")
    .expression(AggregateCall.sum(ColumnReference.of("stores", "total_sales")))
        .as("region_total")
    .from("stores")
    .groupBy("region")
    .build();
```

**Generated SQL:**

```sql
SELECT "stores"."region",
       ROW_NUMBER() OVER (PARTITION BY "stores"."region" ORDER BY "stores"."total_sales" DESC) AS region_rank,
       GROUP_CONCAT("stores"."store_name" ORDER BY total_sales DESC SEPARATOR ' > ') AS top_stores,
       SUM("stores"."total_sales") AS region_total
FROM "stores"
GROUP BY "stores"."region"
```

---

## MERGE Statement Examples

The MERGE statement (also known as UPSERT) combines INSERT and UPDATE operations into a single atomic statement, useful for data synchronization and ETL operations.

#### Basic MERGE: Update Existing, Insert New

```java
DSL dsl = TestDialectRendererFactory.dslStandardSql2008();

String sql = dsl.mergeInto("products")
    .as("p")
    .using("new_products", "np")
    .on("p.product_id", "np.product_id")
    .whenMatched()
        .set("name", ColumnReference.of("np", "name"))
        .set("price", ColumnReference.of("np", "price"))
        .set("updated_at", LocalDateTime.now())
    .whenNotMatched()
        .set("product_id", ColumnReference.of("np", "product_id"))
        .set("name", ColumnReference.of("np", "name"))
        .set("price", ColumnReference.of("np", "price"))
        .set("created_at", LocalDateTime.now())
    .build();
```

**Generated SQL:**

```sql
MERGE INTO "products" AS "p"
USING "new_products" AS "np"
ON "p"."product_id" = "np"."product_id"
WHEN MATCHED THEN UPDATE SET "name" = "np"."name", "price" = "np"."price", "updated_at" = ...
WHEN NOT MATCHED THEN INSERT ("product_id", "name", "price", "created_at") VALUES (...)
```

#### MERGE with Subquery Source

```java
SelectStatement subquery = dsl.select("id", "name", "status")
    .from("staging_products")
    .where("status").eq("active")
    .getCurrentStatement();

String sql = dsl.mergeInto("products")
    .using(subquery, "src")
    .on("products.id", "src.id")
    .whenMatched()
        .set("name", ColumnReference.of("src", "name"))
        .set("status", ColumnReference.of("src", "status"))
    .build();
```

#### Conditional MERGE: Update or Delete Based on Condition

```java
String sql = dsl.mergeInto("inventory")
    .using("stock_updates", "su")
    .on("inventory.product_id", "su.product_id")
    .whenMatched(Comparison.gt(ColumnReference.of("su", "quantity"), Literal.of(0)))
        .set("quantity", ColumnReference.of("su", "quantity"))
        .set("last_updated", LocalDateTime.now())
    .whenMatched(Comparison.eq(ColumnReference.of("su", "quantity"), Literal.of(0)))
        .delete()
    .build();
```

**Generated SQL:**

```sql
MERGE INTO "inventory"
USING "stock_updates" AS "su"
ON "inventory"."product_id" = "su"."product_id"
WHEN MATCHED AND "su"."quantity" > 0 THEN UPDATE SET "quantity" = "su"."quantity", ...
WHEN MATCHED AND "su"."quantity" = 0 THEN DELETE
```

#### Simple MERGE: Insert Only

```java
String sql = dsl.mergeInto("users")
    .using("new_signups", "ns")
    .on("users.email", "ns.email")
    .whenNotMatched()
        .set("email", ColumnReference.of("ns", "email"))
        .set("name", ColumnReference.of("ns", "name"))
        .set("created_at", LocalDateTime.now())
    .build();
```

#### MERGE with Mixed Data Types

```java
String sql = dsl.mergeInto("products")
    .using("updates", "u")
    .on("products.id", "u.id")
    .whenMatched()
        .set("name", "Updated Product")           // String literal
        .set("price", 29.99)                       // Number literal
        .set("active", true)                       // Boolean literal
        .set("stock", ColumnReference.of("u", "stock"))  // Column reference
    .build();
```

### Common Use Cases

1. **Data Synchronization**: Keep two tables in sync based on a key
2. **ETL Operations**: Load data from staging tables into production
3. **Incremental Updates**: Update changed records and insert new ones
4. **Deduplication**: Remove duplicates while keeping the latest version
5. **Soft Deletes**: Mark records as deleted instead of removing them

### Key Features

- ✅ **Fluent API**: Intuitive method chaining consistent with UPDATE and INSERT builders
- ✅ **Type Safety**: Compile-time checking with Java types
- ✅ **Multiple Data Types**: Support for String, Number, Boolean, and Expression values
- ✅ **Conditional Actions**: Add conditions to WHEN clauses using predicates
- ✅ **Subquery Support**: Use SELECT statements as data source
- ✅ **Prepared Statements**: Full support for parameterized queries
- ✅ **SQL:2008 Compliant**: Generates standard SQL MERGE statements

### Technical Documentation

For implementation details, architecture, and technical specifications, see [MERGE_IMPLEMENTATION.md](./MERGE_IMPLEMENTATION.md).

---

## Extending the DSL for Custom Dialects

The SQL DSL is designed to be extensible, allowing you to add dialect-specific functionality for any database.

### Architecture Overview

The DSL extension mechanism consists of three main components:

1. **Custom DSL Class**: Extends the base `DSL` class with dialect-specific methods
2. **Custom Function AST Node**: `CustomFunctionCall` represents dialect-specific functions
3. **Custom Rendering Strategy**: Dialect-specific rendering logic for custom functions

### Example: Adding a Custom Dialect

#### Step 1: Create Dialect-Specific DSL

```java
public class PostgreSqlDSL extends DSL {
    
    public PostgreSqlDSL(DialectRenderer renderer) {
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

#### Step 2: Create Custom Rendering Strategy

```java
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

#### Step 3: Update Dialect Plugin

```java
public class PostgreSqlDialectPlugin implements SqlDialectPluginProvider {
    
    @Override
    public SqlDialectPlugin getPlugin() {
        return new SqlDialectPlugin(
            "postgresql",
            "^15.0.0",
            this::createDialectRenderer,
            this::createPostgreSqlDSL
        );
    }
    
    private DialectRenderer createDialectRenderer() {
        return DialectRenderer.of(
            createPostgreSqlRenderer(),
            createPostgreSqlPreparedStatementRenderer()
        );
    }
    
    private SqlRenderer createPostgreSqlRenderer() {
        return SqlRenderer.builder()
            .customFunctionCallStrategy(new PostgreSqlCustomFunctionRenderStrategy())
            // ... other strategies
            .build();
    }
    
    private DSL createPostgreSqlDSL() {
        return new PostgreSqlDSL(createDialectRenderer());
    }
}
```

#### Step 4: Use Your Custom DSL

```java
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
PostgreSqlDSL postgres = (PostgreSqlDSL) registry.dslFor("postgresql", "15.0.0").orElseThrow();

String sql = postgres.select()
    .column("department")
    .expression(
        postgres.stringAgg("name")
            .orderBy("name")
            .separator(", ")
            .build()
    ).as("employees")
    .from("employees")
    .groupBy("department")
    .build();

// Output: SELECT "department", STRING_AGG("name", ', ' ORDER BY name) AS employees ...
```

### Key Extension Points

1. **CustomFunctionCall**: Generic AST node for any dialect-specific function
2. **CustomFunctionCallRenderStrategy**: Interface for custom rendering logic
3. **DSL Extension**: Add fluent builder methods for your dialect
4. **SqlDialectPlugin**: Register your DSL and renderer with the plugin system

### Benefits of This Architecture

- ✅ **Type Safety**: Full Java type checking at compile time
- ✅ **Fluent API**: Intuitive builder pattern for custom functions
- ✅ **Separation of Concerns**: DSL logic separate from rendering logic
- ✅ **Extensibility**: Add new dialects without modifying core code
- ✅ **Testability**: Easy to unit test DSL builders and rendering strategies
- ✅ **Plugin System**: Automatic discovery via ServiceLoader

### More Information

- **Implementation Guide**: See [CUSTOM_FUNCTIONS_IMPLEMENTATION_PLAN.md](../../data/wiki/CUSTOM_FUNCTIONS_IMPLEMENTATION_PLAN.md)
- **Plugin Architecture**: See [README_PLUGIN_ARCHITECTURE.md](../../data/wiki/README_PLUGIN_ARCHITECTURE.md)
- **MySQL Example**: See [MysqlDSL.java](src/main/java/lan/tlab/r4j/sql/plugin/builtin/mysql/dsl/MysqlDSL.java)

