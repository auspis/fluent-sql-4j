# DSL Usage Guide

This guide provides comprehensive examples of using the JDSQL DSL to build SQL statements programmatically with type safety and compile-time validation.

## Table of Contents

- [Getting Started](#getting-started)
- [CREATE TABLE](#create-table)
- [SELECT Queries](#select-queries)
  - [Basic SELECT](#basic-select)
  - [WHERE Clause](#where-clause)
  - [JOIN Operations](#join-operations)
  - [GROUP BY](#group-by)
  - [HAVING Clause](#having-clause)
  - [Subqueries](#subqueries)
  - [ORDER BY and Pagination](#order-by-and-pagination)
  - [Aggregate Functions](#aggregate-functions)
- [INSERT Statements](#insert-statements)
- [UPDATE Statements](#update-statements)
- [DELETE Statements](#delete-statements)
- [MERGE Statements](#merge-statements)

## Getting Started

The DSL provides a fluent API for building SQL statements. Get started by obtaining a DSL instance for your target database:

```java
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.dsl.DSLRegistry;

// Create a registry and get a DSL instance for your database
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();
```

## PreparedStatement and SQL Injection Prevention

All DSL builders generate SQL with automatic parameter binding through `PreparedStatement`, providing built-in protection against SQL injection attacks:

```java
// User input is safely bound as a parameter, not concatenated into SQL
String userInput = "'; DROP TABLE users; --";
PreparedStatement ps = dsl.select("name", "email")
    .from("users")
    .where().column("name").eq(userInput)  // Safe: becomes a parameter (?)
    .build(connection);
```

**Key Points:**

- **Automatic Parameter Binding**: All values are bound as parameters (`?` placeholders)
- **SQL Injection Prevention**: Values never concatenated directly into SQL strings
- **Connection Management**: The `Connection` object must be provided and managed by your code
- **Resource Cleanup**: Remember to close `PreparedStatement` and `Connection` when done (e.g., using try-with-resources)

```java
// Proper resource management
try (Connection conn = dataSource.getConnection();
     PreparedStatement ps = dsl.select("name").from("users").build(conn);
     ResultSet rs = ps.executeQuery()) {
    // Process results
}
```

## CREATE TABLE

Create tables with column definitions and constraints:

```java
// Get a DSL instance for your database dialect
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Simple table with primary key
PreparedStatement ps = dsl.createTable("users")
    .column("id").integer().notNull()
    .column("name").varchar(100)
    .column("email").varchar(255)
    .primaryKey("id")
    .build(connection);

// Table with multiple data types
PreparedStatement ps = dsl.createTable("products")
    .column("id").integer().notNull()
    .column("name").varchar(100).notNull()
    .column("price").decimal(10, 2)
    .column("quantity").integer()
    .column("active").bool()
    .column("created_at").timestamp()
    .column("birth_date").date()
    .primaryKey("id")
    .build(connection);
```

## SELECT Queries

### Basic SELECT

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Select specific columns
PreparedStatement ps = dsl.select("name", "email")
    .from("users")
    .build(connection);

// Select all columns
PreparedStatement ps = dsl.selectAll()
    .from("users")
    .build(connection);

// Column API with alias
PreparedStatement ps = dsl.select()
    .column("name")
    .column("email").as("emailAddress")
    .from("users")
    .build(connection);
```

### WHERE Clause

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Equal condition
PreparedStatement ps = dsl.select("name", "age")
    .from("users")
    .where()
    .column("name").eq("John Doe")
    .build(connection);

// Greater than
PreparedStatement ps = dsl.select("name", "age")
    .from("users")
    .where()
    .column("age").gt(25)
    .build(connection);

// Multiple conditions with AND
PreparedStatement ps = dsl.select("name", "age", "active")
    .from("users")
    .where()
    .column("age").gt(18)
    .and()
    .column("active").eq(true)
    .build(connection);

// Multiple conditions with OR
PreparedStatement ps = dsl.select("name", "age")
    .from("users")
    .where()
    .column("name").eq("John Doe")
    .or()
    .column("name").eq("Jane Smith")
    .build(connection);

// Complex conditions (AND + OR)
PreparedStatement ps = dsl.select("name", "age", "active")
    .from("users")
    .where()
    .column("age").gt(20)
    .and()
    .column("active").eq(true)
    .or()
    .column("name").eq("Bob")
    .build(connection);

// WHERE with multiple JOINs (cross-table references with explicit aliases)
PreparedStatement ps = dsl.select("*")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u.id", "o.user_id")
    .where()
    .column("u", "age").gt(18)           // Column from users table
    .and()
    .column("o", "status").eq("COMPLETED")  // Column from orders table
    .and()
    .column("u", "active").eq(true)     // Column from users table
    .build(connection);

// Complex multi-table WHERE with multiple JOINs
PreparedStatement ps = dsl.select("*")
    .from("customers").as("c")
    .leftJoin("orders").as("o")
    .on("c.id", "o.customer_id")
    .innerJoin("products").as("p")
    .on("o.product_id", "p.id")
    .where()
    .column("c", "country").eq("IT")     // Customer from Italy
    .and()
    .column("o", "total").gte(1000)      // Order total >= 1000
    .and()
    .column("p", "category").eq("Electronics")  // Electronics products
    .build(connection);
```

**Note on cross-table references**: Use `column(alias, columnName)` syntax for explicit table references in multi-table queries. Dot notation (e.g., `"users.age"`) is not supported and will throw `IllegalArgumentException`.

### JOIN Operations

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// INNER JOIN
PreparedStatement ps = dsl.select("*")
    .from("users")
    .innerJoin("orders")
    .on("users.id", "orders.user_id")
    .build(connection);

// LEFT JOIN
PreparedStatement ps = dsl.select("*")
    .from("users")
    .leftJoin("profiles")
    .on("users.id", "profiles.user_id")
    .build(connection);

// RIGHT JOIN
PreparedStatement ps = dsl.select("*")
    .from("users")
    .rightJoin("departments")
    .on("users.dept_id", "departments.id")
    .build(connection);

// FULL JOIN
PreparedStatement ps = dsl.select("*")
    .from("users")
    .fullJoin("roles")
    .on("users.role_id", "roles.id")
    .build(connection);

// CROSS JOIN
PreparedStatement ps = dsl.select("*")
    .from("users")
    .crossJoin("settings")
    .build(connection);

// JOIN with table aliases
PreparedStatement ps = dsl.select("*")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u.id", "o.user_id")
    .build(connection);

// Multiple JOINs
PreparedStatement ps = dsl.select("*")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u.id", "o.user_id")
    .leftJoin("products").as("p")
    .on("o.product_id", "p.id")
    .build(connection);

// JOIN with WHERE clause
PreparedStatement ps = dsl.select("*")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u.id", "o.user_id")
    .where()
    .column("status").eq("active")
    .build(connection);
```

### GROUP BY

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Simple GROUP BY
PreparedStatement ps = dsl.select("customer_id")
    .from("orders")
    .groupBy("customer_id")
    .build(connection);

// GROUP BY with multiple columns
PreparedStatement ps = dsl.select("customer_id", "status")
    .from("orders")
    .groupBy("customer_id", "status")
    .build(connection);
```

### HAVING Clause

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// HAVING with single condition
PreparedStatement ps = dsl.select("customer_id")
    .from("orders")
    .groupBy("customer_id")
    .having()
    .column("customer_id").gt(100)
    .build(connection);

// HAVING with AND condition
PreparedStatement ps = dsl.select("region")
    .from("sales")
    .groupBy("region")
    .having()
    .column("region").ne("South")
    .andHaving()
    .column("region").ne("East")
    .build(connection);

// HAVING with OR condition
PreparedStatement ps = dsl.select("category")
    .from("inventory")
    .groupBy("category")
    .having()
    .column("category").eq("Electronics")
    .orHaving()
    .column("category").eq("Books")
    .build(connection);

// Complete query with WHERE, GROUP BY, HAVING, and ORDER BY
PreparedStatement ps = dsl.select("user_id")
    .from("transactions")
    .where()
    .column("status").eq("completed")
    .groupBy("user_id")
    .having()
    .column("user_id").gt(0)
    .orderBy("user_id")
    .build(connection);

// HAVING with cross-table references (multiple JOINs)
PreparedStatement ps = dsl.select("*")
    .from("orders").as("o")
    .innerJoin("customers").as("c")
    .on("o.customer_id", "c.id")
    .groupBy("customer_id")
    .having()
    .column("o", "total").gt(1000)       // Condition on orders table
    .and()
    .column("c", "country").eq("IT")     // Condition on customers table
    .build(connection);
```

### Subqueries

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// FROM subquery
SelectBuilder subquery = dsl.select("name", "age")
    .from("users")
    .where().column("age").gt(20);

PreparedStatement ps = dsl.select("name", "age")
    .from(subquery, "u")
    .build(connection);

// FROM subquery with additional WHERE
SelectBuilder subquery = dsl.select("name", "age")
    .from("users")
    .where().column("active").eq(true);

PreparedStatement ps = dsl.select("name", "age")
    .from(subquery, "u")
    .where().column("age").gte(30)
    .build(connection);

// Scalar subquery in WHERE clause
SelectBuilder avgAgeSubquery = dsl.select("age")
    .from("users")
    .fetch(1);

PreparedStatement ps = dsl.select("name", "age")
    .from("users")
    .where().column("age").gte(avgAgeSubquery)
    .build(connection);
```

### ORDER BY and Pagination

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// ORDER BY ascending
PreparedStatement ps = dsl.select("name", "age")
    .from("users")
    .orderBy("age")
    .build(connection);

// ORDER BY descending
PreparedStatement ps = dsl.select("name", "age")
    .from("users")
    .orderByDesc("age")
    .build(connection);

// FETCH (LIMIT)
PreparedStatement ps = dsl.select("name")
    .from("users")
    .fetch(2)
    .build(connection);

// OFFSET
PreparedStatement ps = dsl.select("name")
    .from("users")
    .offset(2)
    .fetch(2)
    .build(connection);

// Complete query with all clauses
PreparedStatement ps = dsl.select("name", "email", "age")
    .from("users")
    .where().column("age").gte(18)
    .and().column("active").eq(true)
    .orderByDesc("age")
    .fetch(2)
    .offset(0)
    .build(connection);
```

### Aggregate Functions

The DSL supports SQL aggregate functions with a fluent API:

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("standardsql", "2008").orElseThrow();

// COUNT all users
PreparedStatement ps = dsl.select().countStar().from("users").build(connection);
// → SELECT COUNT(*) FROM "users"

// SUM with GROUP BY
PreparedStatement ps = dsl.select()
    .sum("amount").as("total")
    .from("orders")
    .groupBy("customer_id")
    .build(connection);
// → SELECT SUM("orders"."amount") AS total FROM "orders" GROUP BY "orders"."customer_id"

// AVG with HAVING clause
PreparedStatement ps = dsl.select()
    .avg("salary")
    .from("employees")
    .groupBy("department")
    .having()
    .column("department").ne("HR")
    .build(connection);
// → SELECT AVG("employees"."salary") FROM "employees" 
//   GROUP BY "employees"."department" 
//   HAVING "employees"."department" != 'HR'

// COUNT DISTINCT with WHERE
PreparedStatement ps = dsl.select()
    .countDistinct("email").as("unique_emails")
    .from("users")
    .where().column("active").eq(true)
    .build(connection);
// → SELECT COUNT(DISTINCT "users"."email") AS unique_emails 
//   FROM "users" WHERE "users"."active" = true

// Multiple aggregates
PreparedStatement ps = dsl.select()
    .sum("score").as("total_score")
    .max("createdAt").as("latest")
    .from("users")
    .build(connection);
// → SELECT SUM("users"."score") AS total_score, MAX("users"."createdAt") AS latest FROM "users"

// Table-qualified columns
PreparedStatement ps = dsl.select()
    .sum("orders", "amount").as("total_amount")
    .from("users").as("u")
    .innerJoin("orders").as("o").on("u.id", "o.user_id")
    .build(connection);
// → SELECT SUM("orders"."amount") AS total_amount FROM "users" AS "u" 
//   INNER JOIN "orders" AS "o" ON "u"."id" = "o"."user_id"
```

## INSERT Statements

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Simple INSERT
PreparedStatement ps = dsl.insertInto("users")
    .set("id", 1)
    .set("name", "John")
    .build(connection);

// INSERT with multiple columns and mixed types
PreparedStatement ps = dsl.insertInto("users")
    .set("id", 2)
    .set("name", "Jane")
    .set("email", "jane@example.com")
    .set("age", 25)
    .set("active", true)
    .set("birthdate", LocalDate.of(1999, 5, 15))
    .set("createdAt", LocalDateTime.of(2023, 10, 10, 12, 0, 0))
    .build(connection);
```

## UPDATE Statements

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// UPDATE with WHERE condition
PreparedStatement ps = dsl.update("users")
    .set("name", "Johnny")
    .where().column("id").eq(1)
    .build(connection);

// UPDATE multiple columns
PreparedStatement ps = dsl.update("users")
    .set("name", "Johnny")
    .set("age", 35)
    .where().column("id").eq(1)
    .build(connection);

// UPDATE with complex WHERE conditions
PreparedStatement ps = dsl.update("users")
    .set("email", "jane.updated@example.com")
    .where().column("age").gt(18)
    .and().column("name").eq("Jane Smith")
    .build(connection);
```

## DELETE Statements

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// DELETE with WHERE condition
PreparedStatement ps = dsl.deleteFrom("users")
    .where().column("id").eq(2)
    .build(connection);

// DELETE with complex conditions
PreparedStatement ps = dsl.deleteFrom("users")
    .where().column("age").lt(18)
    .build(connection);

// DELETE with AND condition
PreparedStatement ps = dsl.deleteFrom("users")
    .where().column("age").gt(18)
    .and().column("name").eq("Alice")
    .build(connection);
```

## MERGE Statements

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Basic MERGE with WHEN MATCHED and WHEN NOT MATCHED
PreparedStatement ps = dsl.mergeInto("users")
    .as("tgt")
    .using("users_updates", "src")
    .on("tgt.id", "src.id")
    .whenMatched()
    .set("name", "src.name")
    .set("email", "src.email")
    .whenNotMatched()
    .set("id", "src.id")
    .set("name", "src.name")
    .set("email", "src.email")
    .build(connection);

// MERGE with multiple columns
PreparedStatement ps = dsl.mergeInto("users")
    .as("tgt")
    .using("users_updates", "src")
    .on("tgt.id", "src.id")
    .whenMatched()
    .set("name", "src.name")
    .set("email", "src.email")
    .set("age", "src.age")
    .set("active", "src.active")
    .whenNotMatched()
    .set("id", "src.id")
    .set("name", "src.name")
    .set("email", "src.email")
    .set("age", "src.age")
    .set("active", "src.active")
    .build(connection);

// MERGE with additional columns (timestamps, etc.)
PreparedStatement ps = dsl.mergeInto("users")
    .as("tgt")
    .using("users_updates", "src")
    .on("tgt.id", "src.id")
    .whenMatched()
    .set("name", "src.name")
    .set("email", "src.email")
    .set("age", "src.age")
    .set("active", "src.active")
    .set("birthdate", "src.birthdate")
    .set("createdAt", "src.createdAt")
    .whenNotMatched()
    .set("id", "src.id")
    .set("name", "src.name")
    .set("email", "src.email")
    .set("age", "src.age")
    .set("active", "src.active")
    .set("birthdate", "src.birthdate")
    .set("createdAt", "src.createdAt")
    .build(connection);
```

## See Also

- [Developer Guide](DEVELOPER_GUIDE.md) - Testing, code coverage, and development workflow
- [Plugin Development Guide](PLUGIN_DEVELOPMENT.md) - Creating custom dialect plugins

