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

## SQL DSL Usage Examples

### MERGE Statement

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

