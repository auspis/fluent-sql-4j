# JDSQL - Java Domain Structured Query Language

A type-safe SQL builder for Java with multi-dialect support through a plugin system. Build SQL statements programmatically using a fluent DSL with compile-time validation.

## Features

- ✅ Type-safe SQL building with compile-time validation
- ✅ Multi-dialect support (MySQL, PostgreSQL, Standard SQL)
- ✅ Plugin architecture for custom dialects
- ✅ Fluent DSL API
- ✅ PreparedStatement support with automatic parameter binding and SQL injection prevention
- ✅ Complex queries: JOINs, subqueries, window functions, aggregates
- ✅ DDL operations: CREATE TABLE, ALTER, DROP
- ✅ DML operations: SELECT, INSERT, UPDATE, DELETE, MERGE

## Quick Start

### Basic SELECT Query

```java
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.dsl.DSLRegistry;

// Get a DSL instance for your database
DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

// Build and execute a query
PreparedStatement ps = dsl.select("name", "email")
    .from("users")
    .where()
    .column("age").gt(18)
    .build(connection);
```

### INSERT Statement

```java
PreparedStatement ps = dsl.insertInto("users")
    .set("name", "John Doe")
    .set("email", "john@example.com")
    .set("age", 25)
    .build(connection);
```

### Multi-Table Query with JOIN

```java
// Query with explicit cross-table column references
PreparedStatement ps = dsl.select("*")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u", "id", "o", "user_id")
    .where()
    .column("u", "age").gt(18)           // Column from users table
    .and()
    .column("o", "status").eq("COMPLETED")  // Column from orders table
    .build(connection);
```

**Note**: All builders use `.build(connection)` which automatically handles parameter binding, preventing SQL injection attacks. The `Connection` object must be managed by the caller (not closed automatically by the builder).

For more examples, see the [DSL Usage Guide](data/wiki/DSL_USAGE_GUIDE.md).

## Project Structure

The project is organized as a multi-module Maven project with two main artifacts for users:

### Published Artifacts

- **[`jdsql-api/`](jdsql-api/)**: **Public API for DSL users**
  - Use this dependency in your application to build SQL queries
  - Provides DSL, builders (SELECT, INSERT, UPDATE, DELETE, MERGE, CREATE TABLE), and PreparedStatement support
  - Maven dependency:

    ```xml
    <dependency>
      <groupId>lan.tlab</groupId>
      <artifactId>jdsql-api</artifactId>
      <version>1.0</version>
    </dependency>
    ```
- **[`jdsql-spi/`](jdsql-spi/)**: **Service Provider Interface for plugin developers**
  - Use this dependency to develop custom SQL dialect plugins
  - Provides AST interfaces, Visitor pattern, rendering strategies, and plugin registry
  - Maven dependency:

    ```xml
    <dependency>
      <groupId>lan.tlab</groupId>
      <artifactId>jdsql-spi</artifactId>
      <version>1.0</version>
    </dependency>
    ```

### Internal Modules (Not Published)

- **[`jdsql-core/`](jdsql-core/)**: Internal implementation (AST, DSL builders, plugin system)
  - Not intended for direct use - access via `jdsql-api` or `jdsql-spi`
  - Contains all implementation code
- **[`plugins/`](plugins/)**: Dialect-specific plugins
  - **[`jdsql-mysql/`](plugins/jdsql-mysql/)**: MySQL dialect plugin
  - **[`jdsql-postgresql/`](plugins/jdsql-postgresql/)**: PostgreSQL dialect plugin
- **[`test-support/`](test-support/)**: Shared test utilities and helpers

### Usage Patterns

**For Application Developers (using the DSL):**

```xml
<dependency>
  <groupId>lan.tlab</groupId>
  <artifactId>jdsql-api</artifactId>
  <version>1.0</version>
</dependency>
<!-- Add dialect plugins as needed -->
<dependency>
  <groupId>lan.tlab</groupId>
  <artifactId>jdsql-mysql</artifactId>
  <version>1.0</version>
</dependency>
```

**For Plugin Developers (creating custom dialects):**

```xml
<dependency>
  <groupId>lan.tlab</groupId>
  <artifactId>jdsql-spi</artifactId>
  <version>1.0</version>
</dependency>
```

## Building the Project

To build all modules locally:

```bash
./mvnw clean install
```

To build without running tests:

```bash
./mvnw clean install -DskipTests
```

To run only unit and component tests (fast):

```bash
./mvnw clean test
```

To run all tests including integration and E2E tests:

```bash
./mvnw clean verify
```

## Documentation

- **[DSL Usage Guide](data/wiki/DSL_USAGE_GUIDE.md)**: Comprehensive examples for all DSL operations (SELECT, INSERT, UPDATE, DELETE, MERGE, CREATE TABLE)
- **[Developer Guide](data/wiki/DEVELOPER_GUIDE.md)**: Testing strategies, code coverage, formatting guidelines
- **[Plugin Development Guide](data/wiki/PLUGIN_DEVELOPMENT.md)**: How to create custom dialect plugins

