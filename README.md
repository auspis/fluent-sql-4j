# r4j - Repo4J

## Table of contents

- [Quick Start](#quick-start)
- [DSL Features](#dsl-features)
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
- [Project structure](#project-structure)
- [Running Tests](#running-tests)
  - [Test Categories](#test-categories)
  - [Basic Commands](#basic-commands)
  - [Selective Test Execution](#selective-test-execution)
  - [Development Workflow](#development-workflow)
  - [CI/CD Pipeline](#cicd-pipeline)
  - [Writing Tests](#writing-tests)
  - [Test Annotations](#test-annotations)
- [Code Coverage](#code-coverage)
  - [Generating Coverage Reports](#generating-coverage-reports)
  - [Viewing Reports](#viewing-reports)
  - [Coverage Metrics](#coverage-metrics)
  - [Configuration](#configuration)
- [Install GIT hook](#install-git-hook)
- [Manually format code](#manually-format-code)
- [Check updates](#check-updates)
  - [Dependencies](#dependencies)
  - [Plugins](#plugins)

A Java library for building SQL statements using a fluent DSL (Domain-Specific Language).

## Quick Start

The DSL provides a fluent API for building SQL statements with type safety and compile-time validation.

### Using DSLRegistry (Recommended)

The recommended way to use the DSL is through `DSLRegistry`, which provides a simplified API for working with different SQL dialects:

```java
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.dsl.DSLRegistry;

// Create a registry and get a DSL instance for your database
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Simple SELECT
String sql = dsl.select("name", "email")
    .from("users")
    .where("age").gt(18)
    .build();

// Column API with alias
String sql = dsl.select()
    .column("name")
    .column("email").as("emailAddress")
    .from("users")
    .build();
// → SELECT `users`.`name`, `users`.`email` as emailAddress FROM `users`

// Build a PreparedStatement directly
PreparedStatement ps = dsl.select("name", "email")
    .from("users")
    .where("age").gt(18)
    .buildPreparedStatement(connection);
```

### Working with Multiple Dialects

DSLRegistry makes it easy to work with different database dialects:

```java
DSLRegistry registry = DSLRegistry.createWithServiceLoader();

// MySQL uses backticks for identifiers
DSL mysqlDsl = registry.dslFor("mysql", "8.0.35").orElseThrow();
String mysqlSql = mysqlDsl.select("name").from("users").build();
// → SELECT `users`.`name` FROM `users`

// Standard SQL uses double quotes
DSL standardDsl = registry.dslFor("standardsql", "2008").orElseThrow();
String standardSql = standardDsl.select("name").from("users").build();
// → SELECT "users"."name" FROM "users"
```

### Using Static Methods (Advanced)

For advanced use cases where you need explicit control over the renderer:

```java
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginRegistry;

SqlDialectPluginRegistry pluginRegistry = SqlDialectPluginRegistry.createWithServiceLoader();
DialectRenderer renderer = pluginRegistry.getDialectRenderer("mysql", "8.0.35").orElseThrow();

String sql = DSL.select(renderer, "name", "email")
    .from("users")
    .build();
```

## DSL Features

### CREATE TABLE

Create tables with column definitions and constraints:

```java
// Get a DSL instance for your database dialect
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Simple table with primary key
String sql = dsl.createTable("users")
    .column("id").integer().notNull()
    .column("name").varchar(100)
    .column("email").varchar(255)
    .primaryKey("id")
    .build();

// Table with multiple data types
String sql = dsl.createTable("products")
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
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Select specific columns
PreparedStatement ps = dsl.select("name", "email")
    .from("users")
    .buildPreparedStatement(connection);

// Select all columns
PreparedStatement ps = dsl.selectAll()
    .from("users")
    .buildPreparedStatement(connection);
```

#### WHERE Clause

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Equal condition
PreparedStatement ps = dsl.select("name", "age")
    .from("users")
    .where("name").eq("John Doe")
    .buildPreparedStatement(connection);

// Greater than
PreparedStatement ps = dsl.select("name", "age")
    .from("users")
    .where("age").gt(25)
    .buildPreparedStatement(connection);

// Multiple conditions with AND
PreparedStatement ps = dsl.select("name", "age", "active")
    .from("users")
    .where("age").gt(18)
    .and("active").eq(true)
    .buildPreparedStatement(connection);

// Multiple conditions with OR
PreparedStatement ps = dsl.select("name", "age")
    .from("users")
    .where("name").eq("John Doe")
    .or("name").eq("Jane Smith")
    .buildPreparedStatement(connection);

// Complex conditions (AND + OR)
PreparedStatement ps = dsl.select("name", "age", "active")
    .from("users")
    .where("age").gt(20)
    .and("active").eq(true)
    .or("name").eq("Bob")
    .buildPreparedStatement(connection);
```

#### JOIN Operations

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// INNER JOIN
String sql = dsl.select("*")
    .from("users")
    .innerJoin("orders")
    .on("users.id", "orders.user_id")
    .build();

// LEFT JOIN
String sql = dsl.select("*")
    .from("users")
    .leftJoin("profiles")
    .on("users.id", "profiles.user_id")
    .build();

// RIGHT JOIN
String sql = dsl.select("*")
    .from("users")
    .rightJoin("departments")
    .on("users.dept_id", "departments.id")
    .build();

// FULL JOIN
String sql = dsl.select("*")
    .from("users")
    .fullJoin("roles")
    .on("users.role_id", "roles.id")
    .build();

// CROSS JOIN
String sql = dsl.select("*")
    .from("users")
    .crossJoin("settings")
    .build();

// JOIN with table aliases
String sql = dsl.select("*")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u.id", "o.user_id")
    .build();

// Multiple JOINs
String sql = dsl.select("*")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u.id", "o.user_id")
    .leftJoin("products").as("p")
    .on("o.product_id", "p.id")
    .build();

// JOIN with WHERE clause
String sql = dsl.select("*")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u.id", "o.user_id")
    .where("status").eq("active")
    .build();
```

#### GROUP BY

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Simple GROUP BY
PreparedStatement ps = dsl.select("customer_id")
    .from("orders")
    .groupBy("customer_id")
    .buildPreparedStatement(connection);

// GROUP BY with multiple columns
PreparedStatement ps = dsl.select("customer_id", "status")
    .from("orders")
    .groupBy("customer_id", "status")
    .buildPreparedStatement(connection);
```

#### HAVING Clause

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// HAVING with single condition
PreparedStatement ps = dsl.select("customer_id")
    .from("orders")
    .groupBy("customer_id")
    .having("customer_id").gt(100)
    .buildPreparedStatement(connection);

// HAVING with AND condition
PreparedStatement ps = dsl.select("region")
    .from("sales")
    .groupBy("region")
    .having("region").ne("South")
    .andHaving("region").ne("East")
    .buildPreparedStatement(connection);

// HAVING with OR condition
PreparedStatement ps = dsl.select("category")
    .from("inventory")
    .groupBy("category")
    .having("category").eq("Electronics")
    .orHaving("category").eq("Books")
    .buildPreparedStatement(connection);

// Complete query with WHERE, GROUP BY, HAVING, and ORDER BY
PreparedStatement ps = dsl.select("user_id")
    .from("transactions")
    .where("status").eq("completed")
    .groupBy("user_id")
    .having("user_id").gt(0)
    .orderBy("user_id")
    .buildPreparedStatement(connection);
```

#### Subqueries

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// FROM subquery
SelectBuilder subquery = dsl.select("name", "age")
    .from("users")
    .where("age").gt(20);

PreparedStatement ps = dsl.select("name", "age")
    .from(subquery, "u")
    .buildPreparedStatement(connection);

// FROM subquery with additional WHERE
SelectBuilder subquery = dsl.select("name", "age")
    .from("users")
    .where("active").eq(true);

PreparedStatement ps = dsl.select("name", "age")
    .from(subquery, "u")
    .where("age").gte(30)
    .buildPreparedStatement(connection);

// Scalar subquery in WHERE clause
SelectBuilder avgAgeSubquery = dsl.select("age")
    .from("users")
    .fetch(1);

PreparedStatement ps = dsl.select("name", "age")
    .from("users")
    .where("age").gte(avgAgeSubquery)
    .buildPreparedStatement(connection);
```

#### ORDER BY and Pagination

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// ORDER BY ascending
PreparedStatement ps = dsl.select("name", "age")
    .from("users")
    .orderBy("age")
    .buildPreparedStatement(connection);

// ORDER BY descending
PreparedStatement ps = dsl.select("name", "age")
    .from("users")
    .orderByDesc("age")
    .buildPreparedStatement(connection);

// FETCH (LIMIT)
PreparedStatement ps = dsl.select("name")
    .from("users")
    .fetch(2)
    .buildPreparedStatement(connection);

// OFFSET
PreparedStatement ps = dsl.select("name")
    .from("users")
    .offset(2)
    .fetch(2)
    .buildPreparedStatement(connection);

// Complete query with all clauses
PreparedStatement ps = dsl.select("name", "email", "age")
    .from("users")
    .where("age").gte(18)
    .and("active").eq(true)
    .orderByDesc("age")
    .fetch(2)
    .offset(0)
    .buildPreparedStatement(connection);
```

#### Aggregate Functions

The DSL supports SQL aggregate functions with a fluent API:

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("standardsql", "2008").orElseThrow();

// COUNT all users
String sql = dsl.select().countStar().from("users").build();
// → SELECT COUNT(*) FROM "users"

// SUM with GROUP BY
String sql = dsl.select()
    .sum("amount").as("total")
    .from("orders")
    .groupBy("customer_id")
    .build();
// → SELECT SUM("orders"."amount") AS total FROM "orders" GROUP BY "orders"."customer_id"

// AVG with HAVING clause
String sql = dsl.select()
    .avg("salary")
    .from("employees")
    .groupBy("department")
    .having("department").ne("HR")
    .build();
// → SELECT AVG("employees"."salary") FROM "employees" 
//   GROUP BY "employees"."department" 
//   HAVING "employees"."department" != 'HR'

// COUNT DISTINCT with WHERE
String sql = dsl.select()
    .countDistinct("email").as("unique_emails")
    .from("users")
    .where("active").eq(true)
    .build();
// → SELECT COUNT(DISTINCT "users"."email") AS unique_emails 
//   FROM "users" WHERE "users"."active" = true

// Multiple aggregates
String sql = dsl.select()
    .sum("score").as("total_score")
    .max("createdAt").as("latest")
    .from("users")
    .build();
// → SELECT SUM("users"."score") AS total_score, MAX("users"."createdAt") AS latest FROM "users"

// Table-qualified columns
String sql = dsl.select()
    .sum("orders", "amount").as("total_amount")
    .from("users").as("u")
    .innerJoin("orders").as("o").on("u.id", "o.user_id")
    .build();
// → SELECT SUM("orders"."amount") AS total_amount FROM "users" AS "u" 
//   INNER JOIN "orders" AS "o" ON "u"."id" = "o"."user_id"
```

### INSERT Statements

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Simple INSERT
PreparedStatement ps = dsl.insertInto("users")
    .set("id", 1)
    .set("name", "John")
    .buildPreparedStatement(connection);

// INSERT with multiple columns and mixed types
PreparedStatement ps = dsl.insertInto("users")
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
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// UPDATE with WHERE condition
PreparedStatement ps = dsl.update("users")
    .set("name", "Johnny")
    .where("id").eq(1)
    .buildPreparedStatement(connection);

// UPDATE multiple columns
PreparedStatement ps = dsl.update("users")
    .set("name", "Johnny")
    .set("age", 35)
    .where("id").eq(1)
    .buildPreparedStatement(connection);

// UPDATE with complex WHERE conditions
PreparedStatement ps = dsl.update("users")
    .set("email", "jane.updated@example.com")
    .where("age").gt(18)
    .and("name").eq("Jane Smith")
    .buildPreparedStatement(connection);
```

### DELETE Statements

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// DELETE with WHERE condition
PreparedStatement ps = dsl.deleteFrom("users")
    .where("id").eq(2)
    .buildPreparedStatement(connection);

// DELETE with complex conditions
PreparedStatement ps = dsl.deleteFrom("users")
    .where("age").lt(18)
    .buildPreparedStatement(connection);

// DELETE with AND condition
PreparedStatement ps = dsl.deleteFrom("users")
    .where("age").gt(18)
    .and("name").eq("Alice")
    .buildPreparedStatement(connection);
```

### MERGE Statements

```java
// Get a DSL instance
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Basic MERGE with WHEN MATCHED and WHEN NOT MATCHED
String sql = dsl.mergeInto("users")
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
    .build();

// MERGE with multiple columns
String sql = dsl.mergeInto("users")
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
    .build();

// MERGE with additional columns (timestamps, etc.)
String sql = dsl.mergeInto("users")
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
    .build();
```

## Project structure

The project is split into Maven modules:

- `sql`: contains the SQL AST, DSL, unit tests, integration tests, and E2E tests
- `spike`: experimental code and prototypes

## Running Tests

This project uses a structured approach to test execution with three distinct test categories organized within the `sql` module:

### Test Categories

📊 Refined Test Classification

|     Level      |         Type          |      Isolation      |  Dependencies  |    Database    | Speed |                                    Examples                                     |
|----------------|-----------------------|---------------------|----------------|----------------|-------|---------------------------------------------------------------------------------|
| 🧱 UNIT        | Logic Unit Tests      | Complete            | None           | No             | 🚀    | SemVerUtilTest                                                                  |
| 🧱 UNIT        | Strategy Unit Tests   | Complete            | None           | No             | 🚀    | StandardSqlColumnReferencePsStrategyTest, MysqlCustomFunctionCallPsStrategyTest |
| 🔗 INTEGRATION | Plugin/ServiceLoader  | ServiceLoader       | Java SPI       | No             | 🏃    | MysqlDialectPluginServiceLoaderTest                                             |
| 🔗 INTEGRATION | Component Integration | Internal Components | Registry/DSL   | No             | 🏃    | DSLRegistryTest                                                                 |
| 🔗 INTEGRATION | DSL Integration       | Complete API        | H2 in-memory   | Yes (embedded) | 🏃    | SelectBuilderIntegrationTest                                                    |
| 🔗 INTEGRATION | Database Integration  | Complete Plugin     | Testcontainers | Yes (real)     | 🏃    | MysqlDialectPluginIntegrationTest                                               |
| 🌐 END-TO-END  | System E2E            | Entire System       | Complete JDBC  | Yes (real)     | 🐌    | PreparedStatementRendererTest                                                   |

### Project Structure

All tests are now consolidated within the `jdsql` module with the following organization:

```
jdsql/src/test/java/
├── lan/tlab/r4j/sql/          # Unit tests (205+)
├── integration/               # Integration tests (21)
└── e2e/system/                # E2E tests (3)
```

### Basic Commands

```bash
# Run only unit tests (fast, no containers)
./mvnw test -pl sql

# Run unit tests for specific module
./mvnw test -pl sql

# Run all tests (unit + integration + e2e)
./mvnw verify -pl sql

# Run all tests for specific module
./mvnw verify -pl sql
```

### Selective Test Execution

```bash
# Run only integration tests
./mvnw verify -pl sql -Dgroups=integration

# Run only e2e tests
./mvnw verify -pl sql -Dgroups=e2e

# Run both integration and e2e (skip unit tests)
./mvnw verify -pl sql -Dgroups=integration,e2e
```

### Development Workflow

```bash
# Fast feedback loop (unit tests only)
./mvnw clean test

# Pre-commit check (unit + integration + e2e)
./mvnw clean verify

# Quick integration check (no slow E2E tests)
./mvnw verify -pl sql -Dgroups=integration

# Full e2e validation with real databases
./mvnw verify -pl sql -Dgroups=e2e
```

### CI/CD Pipeline

The GitHub Actions pipeline executes tests in stages:

1. **Fast Feedback** - Unit tests only (`mvn test`)
2. **Integration** - Integration tests with H2 (`mvn verify -Dgroups=integration`)
3. **E2E Validation** - E2E tests with Testcontainers (`mvn verify -Dgroups=e2e`)

This staged approach provides:

- **Quick feedback** for developers (unit tests complete in seconds)
- **Medium feedback** for integration issues (H2 tests complete in under a minute)
- **Complete validation** for production readiness (E2E tests complete in a few minutes)

### Writing Tests

#### Unit Test

```java
@Test
void shouldDoSomething() {
    // Fast, isolated test with no external dependencies
    assertThat(result).isEqualTo(expected);
}
```

#### Integration Test

```java
@IntegrationTest  // includes @Tag("integration")
class MyIntegrationTest {
    
    @Test
    void shouldIntegrateWithDatabase() {
        // Test with H2 in-memory database
        Connection conn = TestDatabaseUtil.createH2Connection();
        // ... test code
    }
}
```

#### E2E Test

```java
@E2ETest  // includes @Tag("e2e")
@Testcontainers
class MyE2E {
    
    @Container
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    @Test
    void shouldWorkEndToEnd() {
        // Full system test with real MySQL database
        Connection conn = DriverManager.getConnection(mysql.getJdbcUrl(), ...);
        // ... test code
    }
}
```

### Test Annotations

Custom test annotations are located in `lan.tlab.r4j.sql.util.annotation`:

- `@IntegrationTest` - Marks integration tests (tagged with `integration`)
- `@E2ETest` - Marks end-to-end tests (tagged with `e2e`)

Both annotations include JUnit tags for Maven filtering. Apply them at the **class level** only.

## Code Coverage

This project uses [JaCoCo](https://www.jacoco.org/jacoco/) for code coverage analysis. JaCoCo is configured centrally in the root `pom.xml` and inherited by all modules.

### Generating Coverage Reports

#### Unit Tests Only

To generate coverage reports for unit tests only (fast feedback):

```bash
./mvnw clean test jacoco:report
```

#### Full Verification (Unit + Integration + E2E)

To generate coverage reports after running all tests:

```bash
./mvnw clean verify jacoco:report
```

#### Module-Specific Coverage

To generate coverage for a specific module and its dependencies:

```bash
./mvnw clean test -pl <module> -am jacoco:report
./mvnw clean verify -pl <module> -am jacoco:report
```

### Viewing Reports

Coverage reports are generated as HTML files in each module's `target/site/jacoco/` directory:

- **SQL Module**: `jdsql/target/site/jacoco/index.html`

Open the `index.html` file in your browser to view detailed coverage information including:
- Overall coverage percentages (instructions, branches, lines, methods, classes)
- Coverage breakdown by package and class
- Source code highlighting showing covered/uncovered lines

### Coverage Metrics

- **Instructions**: Bytecode instructions executed
- **Branches**: Decision points (if/else, loops) covered
- **Lines**: Source code lines executed
- **Methods**: Methods called during tests
- **Classes**: Classes instantiated during tests

### Configuration

JaCoCo is configured in the root `pom.xml` under the `<plugins>` section, ensuring consistent coverage settings across all modules. The configuration includes:

- Coverage data collection during test execution
- HTML report generation
- Exclusion of generated code from coverage analysis

## Install GIT hook

To reduce conflicts, a hook is provided to automatically format code with Spotless on each commit.

```bash
./mvnw process-resources
```

## Manually format code

```bash
./mvnw spotless:apply
```

## Check updates

### Dependencies

```bash
data/scripts/dependency-updates-show.sh
```

### Plugins

```bash
data/scripts/plugin-updates-show.sh
```

