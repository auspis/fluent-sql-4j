# Copilot Repository Instructions

## Overview

This repository is a multi-module Java project managed with Maven. It is structured to support modular development and integration testing, and is intended for use with Java 21.

## Java Version

- Java 21 is required for all modules.

## Build Tool

- Maven (multi-module project)
- Each module contains its own `pom.xml` and is managed from the root `pom.xml`.

## Project Structure

- Root directory contains:
  - `pom.xml` (parent POM)
  - Subfolders for each module (e.g., `sql/`, `test-integration/`, etc.)
  - `.github/` for GitHub-specific configuration
- Common modules:
  - `sql/`: Core SQL AST and DSL logic
  - `test-integration/`: Integration tests, uses Testcontainers
  - Other modules for shared code, applications, and services

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

- Testcontainers for integration testing
- AssertJ for fluent assertions
- JUnit 5 for testing
- MySQL (via Testcontainers) for integration tests

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

**Package Convention**: Helper classes must be placed in a package named `*.helper` (e.g., `lan.tlab.r4j.sql.dsl.helper`).

**Example of a Helper Class**:

```java
package lan.tlab.r4j.sql.dsl.helper;

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

**Package Convention**: Utility classes must be placed in a package named `*.util` (e.g., `lan.tlab.r4j.sql.dsl.util`).

**Mandatory Requirements for Utility Classes**:
- The class must be declared as `final`
- All methods must be `static`
- The class must have a `private` no-args constructor to prevent instantiation
- The class cannot be instantiated

**Example of a Utility Class**:

```java
package lan.tlab.r4j.sql.dsl.util;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;

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

## How to Run Tests

- make sure you are in the root folder or `cd` to it
- Use `./mvnw clean test` at the root to run unit tests
- Use `./mvnw clean verify` at the root to run all tests (unit and integration)
- The project is a multi module maven project, so in some cases you may need to add -am to compile dependencies
- When you need to run integration tests try to run only the needed ones
- Integration tests are located in `test-integration/`

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

## Additional Notes

- The repository may contain scripts in `data/scripts/` for development automation
- Use the provided Maven Wrapper (`mvnw`) for consistent builds
- For any new code, ensure it is covered by tests and follows the project structure

## Contact

For questions or contributions, refer to the `README.md` or contact the maintainers listed there.
