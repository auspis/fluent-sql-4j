# Copilot Repository Instructions

## Overview

This repository is a multi-module Java project managed with Maven. It is structured to support modular development and integration testing, and is intended for use with Java 21.

## Java Version

- Java 21 is required for all modules.

## Build Tool

- Maven (multi-module project)
- Each module contains its own `pom.xml` and is managed from the root `pom.xml`.

## How to Build

- To compile the entire project, run `./mvnw clean compile` from the root directory.
- Since this is a multi-module project, when compiling a specific module, you may need to add `-am` to also compile dependencies: `./mvnw clean compile -am -pl <module>`

## Project Structure

- Root directory contains:
  - `pom.xml` (parent POM)
  - Subfolders for each module (e.g., `jdsql-core/`, `plugins/`, `test-support/`)
  - `.github/` for GitHub-specific configuration
- Common modules:
  - `jdsql-core/`: Core SQL AST and DSL logic, plus all tests (unit, integration, E2E)
  - `plugins/jdsql-mysql/`: MySQL dialect plugin
  - `test-support/`: Shared test utilities and helpers

## Conventions

- Source code in `src/main/java/`
- Tests in `src/test/java/`
- never user var to declare a variable
- never user java reflection to solve problems
- Use AssertJ for assertions in tests
- Use JUnit 5 for unit and integration tests
- keep the test name compact avoiding to prefix it with `test` or `with` or `handle` and explaining the expected behavior
- Integration tests may use Testcontainers for database emulation
- SQL code is generated using the project's SQL builder classes

## Dependencies

- Testcontainers for integration and E2E testing
- AssertJ for fluent assertions
- JUnit 5 for testing
- MySQL and PostgreSQL (via Testcontainers) for integration and E2E tests

## Coding Guidelines

- Follow standard Java 21 conventions
- Use builder patterns for constructing SQL ASTs when there are more than two fields, otherwise an all args constructor is ok
- Keep modules decoupled and reusable
- Prefer immutable data structures where possible

### Java Helper Classes

A helper class provides functionalities necessary for the overall running of a Java program. Helper classes contain methods used by other classes to perform repetitive tasks, which aren't the core purpose of an application.

As the name suggests, they help other classes by providing some functionalities that complement the services provided by those classes.

They contain methods for implementing mundane and repetitive tasks, making the overall code base modular and reusable across multiple classes.

A helper class can be instantiated and may contain instance variables, instance, and static methods.

Multiple instances of a helper class can exist in our application. When different classes have common functionalities, we can group these functionalities together to form a helper class that's accessible across certain classes in our application.

**Package Convention**: Helper classes must be placed in a package named `*.helper` (e.g., `lan.tlab.r4j.jdsql.dsl.helper`).

**Example of a Helper Class**:

```java
package lan.tlab.r4j.jdsql.dsl.helper;

public class QueryBuilderHelper {
    private String schema;
    
    public QueryBuilderHelper(String schema) {
        this.schema = schema;
    }
    
    public String buildQualifiedTableName(String tableName) {
        return schema + "." + tableName;
    }
    
    public static String escapeIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }
}
```

### Java Utility Classes

A utility class in Java is a class that provides static methods that are accessible for use across an application. The static methods in utility classes are used for performing common routines in our application.

Utility classes cannot be instantiated and are sometimes stateless without static variables. We declare a utility class as final, and all its methods must be static.

Since we don't want our utility classes to be instantiated, a private constructor is introduced. Having a private constructor means that Java won't create a default constructor for our utility class. The constructor can be empty.

The purpose of a utility class is to provide methods for executing certain functionalities within a program, while the main class focuses on the core problem it solves.

Methods of a utility are accessed via the class name. It makes our code more flexible for use while remaining modular.

Java has utility classes such as java.util.Arrays, java.lang.Math, java.util.Scanner, java.util.Collections, etc.

**Package Convention**: Utility classes must be placed in a package named `*.util` (e.g., `lan.tlab.r4j.jdsql.dsl.util`).

**Mandatory Requirements for Utility Classes**:
- The class must be declared as `final`
- All methods must be `static`
- The class must have a `private` no-args constructor to prevent instantiation
- The class cannot be instantiated

**Example of a Utility Class**:

```java
package lan.tlab.r4j.jdsql.dsl.util;

import lan.tlab.r4j.jdsql.ast.expression.scalar.ColumnReference;

public final class ColumnReferenceUtil {

    private ColumnReferenceUtil() {
        // Utility class - prevent instantiation
    }

    public static ColumnReference parseColumnReference(String column, String defaultTableReference) {
        if (column.contains(".")) {
            String[] parts = column.split("\\.", 2);
            return ColumnReference.of(parts[0], parts[1]);
        }
        return ColumnReference.of(defaultTableReference, column);
    }
}
```

For more details: https://www.baeldung.com/java-helper-vs-utility-classes

## How to Add a Module

1. Create a new directory at the root
2. Add a `pom.xml` for the module
3. Register the module in the root `pom.xml` under `<modules>`

**Note**: All tests (unit, integration, E2E) are now consolidated in the `jdsql-core/` module. Do not create separate test modules.

## How to Run Tests

- make sure you are in the root folder or `cd` to it
- Use `./mvnw clean test -am -pl jdsql-core` to run unit and component tests (fast feedback, no database)
- Use `./mvnw clean verify -am -pl jdsql-core` to run all tests (unit + component + integration + E2E)
- Use `./mvnw test -am -pl jdsql-core -Dgroups=component` to run only component tests
- Use `./mvnw verify -am -pl jdsql-core -Dgroups=integration` to run only integration tests
- Use `./mvnw verify -am -pl jdsql-core -Dgroups=e2e` to run only E2E tests
- The project is a multi module maven project, so in some cases you may need to add -am to compile dependencies
- When you need to run integration tests try to run only the needed ones
- All tests are now consolidated in the `jdsql-core/` module with the following structure:
  - `jdsql-core/src/test/java/lan/tlab/r4j/jdsql/`: Unit tests (fast, isolated, single class)
  - `jdsql-core/src/test/java/lan/tlab/r4j/jdsql/dsl/*ComponentTest.java`: Component tests (fast, multiple classes, mocked JDBC)
  - `jdsql-core/src/test/java/integration/`: Integration tests (medium speed, with H2/Testcontainers)
  - `jdsql-core/src/test/java/e2e/system/`: E2E tests (slow, with real databases)

## Test Categories (Test Pyramid)

The project uses a structured test pyramid with four main categories:

### Unit Tests (`jdsql-core/src/test/java/lan/tlab/r4j/jdsql/`)

- **Purpose**: Test individual components in isolation
- **Speed**: Very fast (🚀)
- **Dependencies**: None
- **Database**: No database access
- **Examples**: `SemVerUtilTest`, `StandardSqlColumnReferencePsStrategyTest`

### Component Tests (`jdsql-core/src/test/java/lan/tlab/r4j/jdsql/dsl/*ComponentTest.java`)

- **Purpose**: Test interaction between multiple classes within a component (DSL → AST → Visitor → SQL Renderer)
- **Speed**: Fast (🚀)
- **Dependencies**: Real component classes, mocked JDBC (Connection, PreparedStatement)
- **Database**: No database access (mocked JDBC)
- **Annotation**: `@ComponentTest` (JUnit tag: `component`)
- **Examples**: `SelectDSLComponentTest`, `DSLRegistryComponentTest`

### Integration Tests (`jdsql-core/src/test/java/integration/`)

- **Purpose**: Test component interactions and data flow
- **Speed**: Medium (🏃)
- **Dependencies**: Internal components, H2 in-memory database
- **Database**: Embedded H2 or Testcontainers
- **Examples**: `SelectBuilderIntegrationTest`, `MysqlDialectPluginIntegrationTest`

### End-to-End Tests (`jdsql-core/src/test/java/e2e/system/`)

- **Purpose**: Test complete workflows from start to finish
- **Speed**: Slow (🐌)
- **Dependencies**: Full system, real databases
- **Database**: Real databases via Testcontainers
- **Examples**: `AstToPreparedStatementSpecVisitorTest`, `StandardSqlRendererMySqlE2E`

### Test Annotations

- `@ComponentTest`: Marks component tests (JUnit tag: `component`)
- `@IntegrationTest`: Marks integration tests (JUnit tag: `integration`)
- `@E2ETest`: Marks end-to-end tests (JUnit tag: `e2e`)

### Writing New Tests

- **Unit tests**: Place in `jdsql-core/src/test/java/lan/tlab/r4j/jdsql/` following existing package structure
- **Component tests**: Place in `jdsql-core/src/test/java/lan/tlab/r4j/jdsql/dsl/` with `@ComponentTest` annotation and suffix `*ComponentTest.java`
- **Integration tests**: Place in `jdsql-core/src/test/java/integration/` with `@IntegrationTest` annotation and descriptive comments
- **E2E tests**: Place in `jdsql-core/src/test/java/e2e/system/` with `@E2ETest` annotation

## Test Helper and Assertion Utilities

The project provides helper and utility classes to reduce boilerplate in mocked JDBC tests:

### SqlCaptureHelper (Helper Class)

**Location**: `test-support/src/main/java/lan/tlab/r4j/jdsql/test/helper/SqlCaptureHelper.java`

**Purpose**: Encapsulates common JDBC mock setup (Connection, PreparedStatement, SQL ArgumentCaptor).

**Usage** (in unit and component tests with mocks):

```java
class MyBuilderTest {
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();  // All mocks created automatically
    }

    @Test
    void myTest() throws SQLException {
        builder.buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT \"name\" FROM \"users\"");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, value);
    }
}
```

**When to use**:
- Writing unit tests for SQL builders (SelectBuilder, InsertBuilder, etc.)
- Writing component tests with mock JDBC (no real database)
- **Do NOT use** for integration tests with real H2/database - use `TestDatabaseUtil` instead

### SqlAssert (Custom AssertJ Assertion)

**Location**: `test-support/src/main/java/lan/tlab/r4j/jdsql/test/SqlAssert.java`

**Purpose**: Fluent assertions for SQL strings, similar to JsonAssert.

**Usage**:

```java
import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;

assertThatSql(sql)
    .isEqualTo("SELECT \"name\" FROM \"users\"")
    .contains("WHERE")
    .containsInOrder("SELECT", "FROM", "WHERE")
    .isEqualToNormalizingWhitespace("SELECT   \"name\"   FROM   \"users\"");
```

**Available methods**: `isEqualTo()`, `isEqualToNormalizingWhitespace()`, `contains()`, `containsAll()`, `containsInOrder()`, `doesNotContain()`, `startsWith()`, `endsWith()`

### Benefits

- **~50% boilerplate reduction**: Eliminate repetitive mock setup code
- **Fluent SQL assertions**: More readable than `assertThat(sqlCaptor.getValue()).contains(...)`
- **Composition over inheritance**: No rigid test base classes needed

### Related Documentation

- **Full guide**: `jdsql-core/data/test-helpers-usage-guide.md`
- **Example**: `jdsql-core/src/test/java/lan/tlab/r4j/jdsql/dsl/select/SelectBuilderRefactoredExampleTest.java`

## Code Formatting

**IMPORTANT**: Before each commit, you must run `./mvnw spotless:apply` to format the code correctly. This prevents pipeline failures due to formatting issues.

```bash
./mvnw spotless:apply
```

This command will:
- Format all Java files according to the project's code style
- Sort POM files
- Format Markdown files

Always run this before committing changes to ensure the CI/CD pipeline succeeds.

## DSL Builder API

All DSL builders (SelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, MergeBuilder, CreateTableBuilder) use **only** `.buildPreparedStatement(Connection connection)` to generate SQL with automatic parameter binding.

- **No `.build()` method**: The `.build()` method has been removed from all DML/DQL builders
- **PreparedStatement only**: All builders return `PreparedStatement` with parameters already bound
- **SQL Injection Prevention**: Parameter binding is automatic, preventing SQL injection attacks
- **Connection Management**: The `Connection` parameter is required and must be managed by the caller (not closed automatically by builders)

Example:

```java
// Correct usage
PreparedStatement ps = dsl.select("name", "email")
    .from("users")
    .where("age").gt(18)
    .buildPreparedStatement(connection);

// execute query
ResultSet rs = ps.executeQuery();
```

## Additional Notes

- The repository may contain scripts in `data/scripts/` for development automation
- Use the provided Maven Wrapper (`mvnw`) for consistent builds
- For any new code, ensure it is covered by tests and follows the project structure

## Contact

For questions or contributions, refer to the `README.md` or contact the maintainers listed there.
