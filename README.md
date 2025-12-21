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

**Note**: All builders use `.build(connection)` which automatically handles parameter binding, preventing SQL injection attacks. The `Connection` object must be managed by the caller (not closed automatically by the builder).

For more examples, see the [DSL Usage Guide](data/wiki/DSL_USAGE_GUIDE.md).

## Project Structure

The project is organized as a multi-module Maven project:

- **[`jdsql-core/`](jdsql-core/)**: Core SQL AST, DSL builders, and plugin system
- **[`plugins/`](plugins/)**: Dialect-specific plugins
  - **[`jdsql-mysql/`](plugins/jdsql-mysql/)**: MySQL dialect plugin
- **[`test-support/`](test-support/)**: Shared test utilities and helpers

Dependencies (see [jdsql-core/README.md](jdsql-core/README.md) for more details):
- Each module provides specific functionalities
- Modules are version-controlled together for consistency

## Documentation

- **[DSL Usage Guide](data/wiki/DSL_USAGE_GUIDE.md)**: Comprehensive examples for all DSL operations (SELECT, INSERT, UPDATE, DELETE, MERGE, CREATE TABLE)
- **[Developer Guide](data/wiki/DEVELOPER_GUIDE.md)**: Testing strategies, code coverage, formatting guidelines
- **[Plugin Development Guide](data/wiki/PLUGIN_DEVELOPMENT.md)**: How to create custom dialect plugins

