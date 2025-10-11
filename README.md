# r4j - Repo4J

A Java library for building SQL statements using a fluent DSL (Domain-Specific Language).

## Quick Start

The DSL provides a fluent API for building SQL statements with type safety and compile-time validation. All operations start with the `DSL` class:

```java
import lan.tlab.r4j.sql.dsl.DSL;

// Simple SELECT
String sql = DSL.select("name", "email")
    .from("users")
    .where("age").gt(18)
    .build();

// Build a PreparedStatement directly
PreparedStatement ps = DSL.select("name", "email")
    .from("users")
    .where("age").gt(18)
    .buildPreparedStatement(connection);
```

## DSL Features

### CREATE TABLE

Create tables with column definitions and constraints:

```java
// Simple table with primary key
String sql = DSL.createTable("users")
    .column("id").integer().notNull()
    .column("name").varchar(100)
    .column("email").varchar(255)
    .primaryKey("id")
    .build();

// Table with multiple data types
String sql = DSL.createTable("products")
    .column("id").integer().notNull()
    .column("name").varchar(100).notNull()
    .column("price").decimal(10, 2)
    .column("quantity").integer()
    .column("active").bool()
    .column("created_at").timestamp()
    .column("birth_date").date()
    .primaryKey("id")
    .build();
```

### SELECT Queries

#### Basic SELECT

```java
// Select specific columns
PreparedStatement ps = DSL.select("name", "email")
    .from("users")
    .buildPreparedStatement(connection);

// Select all columns
PreparedStatement ps = DSL.selectAll()
    .from("users")
    .buildPreparedStatement(connection);
```

#### WHERE Clause

```java
// Equal condition
PreparedStatement ps = DSL.select("name", "age")
    .from("users")
    .where("name").eq("John Doe")
    .buildPreparedStatement(connection);

// Greater than
PreparedStatement ps = DSL.select("name", "age")
    .from("users")
    .where("age").gt(25)
    .buildPreparedStatement(connection);

// Multiple conditions with AND
PreparedStatement ps = DSL.select("name", "age", "active")
    .from("users")
    .where("age").gt(18)
    .and("active").eq(true)
    .buildPreparedStatement(connection);

// Multiple conditions with OR
PreparedStatement ps = DSL.select("name", "age")
    .from("users")
    .where("name").eq("John Doe")
    .or("name").eq("Jane Smith")
    .buildPreparedStatement(connection);

// Complex conditions (AND + OR)
PreparedStatement ps = DSL.select("name", "age", "active")
    .from("users")
    .where("age").gt(20)
    .and("active").eq(true)
    .or("name").eq("Bob")
    .buildPreparedStatement(connection);
```

#### JOIN Operations

```java
// INNER JOIN
String sql = DSL.select("*")
    .from("users")
    .innerJoin("orders")
    .on("users.id", "orders.user_id")
    .build();

// LEFT JOIN
String sql = DSL.select("*")
    .from("users")
    .leftJoin("profiles")
    .on("users.id", "profiles.user_id")
    .build();

// RIGHT JOIN
String sql = DSL.select("*")
    .from("users")
    .rightJoin("departments")
    .on("users.dept_id", "departments.id")
    .build();

// FULL JOIN
String sql = DSL.select("*")
    .from("users")
    .fullJoin("roles")
    .on("users.role_id", "roles.id")
    .build();

// CROSS JOIN
String sql = DSL.select("*")
    .from("users")
    .crossJoin("settings")
    .build();

// JOIN with table aliases
String sql = DSL.select("*")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u.id", "o.user_id")
    .build();

// Multiple JOINs
String sql = DSL.select("*")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u.id", "o.user_id")
    .leftJoin("products").as("p")
    .on("o.product_id", "p.id")
    .build();

// JOIN with WHERE clause
String sql = DSL.select("*")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u.id", "o.user_id")
    .where("status").eq("active")
    .build();
```

#### GROUP BY

```java
// Simple GROUP BY
PreparedStatement ps = DSL.select("customer_id")
    .from("orders")
    .groupBy("customer_id")
    .buildPreparedStatement(connection);

// GROUP BY with multiple columns
PreparedStatement ps = DSL.select("customer_id", "status")
    .from("orders")
    .groupBy("customer_id", "status")
    .buildPreparedStatement(connection);
```

#### HAVING Clause

```java
// HAVING with single condition
PreparedStatement ps = DSL.select("customer_id")
    .from("orders")
    .groupBy("customer_id")
    .having("customer_id").gt(100)
    .buildPreparedStatement(connection);

// HAVING with AND condition
PreparedStatement ps = DSL.select("region")
    .from("sales")
    .groupBy("region")
    .having("region").ne("South")
    .andHaving("region").ne("East")
    .buildPreparedStatement(connection);

// HAVING with OR condition
PreparedStatement ps = DSL.select("category")
    .from("inventory")
    .groupBy("category")
    .having("category").eq("Electronics")
    .orHaving("category").eq("Books")
    .buildPreparedStatement(connection);

// Complete query with WHERE, GROUP BY, HAVING, and ORDER BY
PreparedStatement ps = DSL.select("user_id")
    .from("transactions")
    .where("status").eq("completed")
    .groupBy("user_id")
    .having("user_id").gt(0)
    .orderBy("user_id")
    .buildPreparedStatement(connection);
```

#### Subqueries

```java
// FROM subquery
SelectBuilder subquery = DSL.select("name", "age")
    .from("users")
    .where("age").gt(20);

PreparedStatement ps = DSL.select("name", "age")
    .from(subquery, "u")
    .buildPreparedStatement(connection);

// FROM subquery with additional WHERE
SelectBuilder subquery = DSL.select("name", "age")
    .from("users")
    .where("active").eq(true);

PreparedStatement ps = DSL.select("name", "age")
    .from(subquery, "u")
    .where("age").gte(30)
    .buildPreparedStatement(connection);

// Scalar subquery in WHERE clause
SelectBuilder avgAgeSubquery = DSL.select("age")
    .from("users")
    .fetch(1);

PreparedStatement ps = DSL.select("name", "age")
    .from("users")
    .where("age").gte(avgAgeSubquery)
    .buildPreparedStatement(connection);
```

#### ORDER BY and Pagination

```java
// ORDER BY ascending
PreparedStatement ps = DSL.select("name", "age")
    .from("users")
    .orderBy("age")
    .buildPreparedStatement(connection);

// ORDER BY descending
PreparedStatement ps = DSL.select("name", "age")
    .from("users")
    .orderByDesc("age")
    .buildPreparedStatement(connection);

// FETCH (LIMIT)
PreparedStatement ps = DSL.select("name")
    .from("users")
    .fetch(2)
    .buildPreparedStatement(connection);

// OFFSET
PreparedStatement ps = DSL.select("name")
    .from("users")
    .offset(2)
    .fetch(2)
    .buildPreparedStatement(connection);

// Complete query with all clauses
PreparedStatement ps = DSL.select("name", "email", "age")
    .from("users")
    .where("age").gte(18)
    .and("active").eq(true)
    .orderByDesc("age")
    .fetch(2)
    .offset(0)
    .buildPreparedStatement(connection);
```

### INSERT Statements

```java
// Simple INSERT
PreparedStatement ps = DSL.insertInto("users")
    .set("id", 1)
    .set("name", "John")
    .buildPreparedStatement(connection);

// INSERT with multiple columns and mixed types
PreparedStatement ps = DSL.insertInto("users")
    .set("id", 2)
    .set("name", "Jane")
    .set("email", "jane@example.com")
    .set("age", 25)
    .set("active", true)
    .set("birthdate", LocalDate.of(1999, 5, 15))
    .set("createdAt", LocalDateTime.of(2023, 10, 10, 12, 0, 0))
    .buildPreparedStatement(connection);
```

### UPDATE Statements

```java
// UPDATE with WHERE condition
PreparedStatement ps = DSL.update("users")
    .set("name", "Johnny")
    .where("id").eq(1)
    .buildPreparedStatement(connection);

// UPDATE multiple columns
PreparedStatement ps = DSL.update("users")
    .set("name", "Johnny")
    .set("age", 35)
    .where("id").eq(1)
    .buildPreparedStatement(connection);

// UPDATE with complex WHERE conditions
PreparedStatement ps = DSL.update("users")
    .set("email", "jane.updated@example.com")
    .where("age").gt(18)
    .and("name").eq("Jane Smith")
    .buildPreparedStatement(connection);
```

### DELETE Statements

```java
// DELETE with WHERE condition
PreparedStatement ps = DSL.deleteFrom("users")
    .where("id").eq(2)
    .buildPreparedStatement(connection);

// DELETE with complex conditions
PreparedStatement ps = DSL.deleteFrom("users")
    .where("age").lt(18)
    .buildPreparedStatement(connection);

// DELETE with AND condition
PreparedStatement ps = DSL.deleteFrom("users")
    .where("age").gt(18)
    .and("name").eq("Alice")
    .buildPreparedStatement(connection);
```

## Project structure

The project is split into Maven modules:
- `sql`: contains the SQL AST, DSL and unit tests
- `test-integration`: contains integration tests (fast H2 tests and slow E2E tests with Testcontainers)

## Install GIT hook

To reduce conflicts, a hook is provided to automatically format code with Spotless on each commit.

```bash
./mvnw process-resources
```

## Manually format code

```bash
./mvnw spotless:apply
```

## Run tests

### To run fast tests (unit tests + H2 integration tests)

```bash
./mvnw test
```

### To run all tests (unit + H2 integration + E2E tests)

```bash
./mvnw verify
```

## Test naming convention

- **Unit tests**: `*Test.java` suffix (e.g. `MyFeatureTest.java`)
- **Integration tests (H2)**: `*Test.java` suffix (e.g. `PreparedStatementVisitorTest.java`)
- **End-to-end tests (Testcontainers)**: `*E2E.java` suffix (e.g. `StandardSqlRendererMySqlE2E.java`)

This naming convention allows for:
- Fast feedback during development: `./mvnw test` runs unit and H2 integration tests
- Complete validation: `./mvnw verify` runs all tests including E2E tests

## How to run specific test types

### To run only H2 integration tests

```bash
./mvnw test -pl test-integration -am
```

### To run only E2E tests

```bash
./mvnw verify -Dtest=skip -Dit.test="*E2E" -pl test-integration -am
```

### To run all integration tests (H2 + E2E)

```bash
./mvnw verify -pl test-integration -am
```

### Technical details

- Running `./mvnw test` will execute:
  - **Unit tests** in the `sql` module
  - **H2 integration tests** in the `test-integration` module (fast, in-memory database)
- Running `./mvnw verify` will additionally execute:
  - **E2E tests** with Testcontainers in the `test-integration` module (slower, real databases)
- The `test-integration` module depends on `sql` and contains all the necessary dependencies for testing (JUnit, Testcontainers, H2, etc).
- **H2 integration tests** are named with the `*Test.java` suffix and run with Surefire.
- **E2E tests** are named with the `*E2E.java` suffix and run with Failsafe.
- This ensures optimal development workflow: fast feedback with `test`, complete validation with `verify`.

## DSL Usage Examples

### Aggregate Functions

The DSL supports SQL aggregate functions with a fluent API:

```java
// COUNT all users
String sql = DSL.select().countStar().from("users").build();
// → SELECT COUNT(*) FROM "users"

// SUM with GROUP BY
String sql = DSL.select()
    .sum("amount").as("total")
    .from("orders")
    .groupBy("customer_id")
    .build();
// → SELECT SUM("orders"."amount") AS total FROM "orders" GROUP BY "orders"."customer_id"

// AVG with HAVING clause
String sql = DSL.select()
    .avg("salary")
    .from("employees")
    .groupBy("department")
    .having("department").ne("HR")
    .build();
// → SELECT AVG("employees"."salary") FROM "employees" 
//   GROUP BY "employees"."department" 
//   HAVING "employees"."department" != 'HR'

// COUNT DISTINCT with WHERE
String sql = DSL.select()
    .countDistinct("email").as("unique_emails")
    .from("users")
    .where("active").eq(true)
    .build();
// → SELECT COUNT(DISTINCT "users"."email") AS unique_emails 
//   FROM "users" WHERE "users"."active" = true

// Multiple aggregates
String sql = DSL.select()
    .sum("score").as("total_score")
    .max("createdAt").as("latest")
    .from("users")
    .build();
// → SELECT SUM("users"."score") AS total_score, MAX("users"."createdAt") AS latest FROM "users"

// Regular columns
String sql = DSL.select()
    .column("name")
    .column("email")
    .from("users")
    .build();
// → SELECT "users"."name", "users"."email" FROM "users"

// Table-qualified columns
String sql = DSL.select()
    .sum("orders", "amount").as("total_amount")
    .from("users").as("u")
    .innerJoin("orders").as("o").on("u.id", "o.user_id")
    .build();
// → SELECT SUM("orders"."amount") AS total_amount FROM "users" AS "u" 
//   INNER JOIN "orders" AS "o" ON "u"."id" = "o"."user_id"
```

## check updates

### dependencies

```bash
data/scripts/dependency-updates-show.sh
```

### plugins

```bash
data/scripts/plugin-updates-show.sh
```

