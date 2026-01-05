# Developer Guide

This guide covers the development workflow, testing strategies, code coverage, and maintenance tasks for JDSQL.

## Table of Contents

- [Running Tests](#running-tests)
  - [Test Categories](#test-categories)
  - [Project Structure](#project-structure)
  - [Basic Commands](#basic-commands)
  - [Selective Test Execution](#selective-test-execution)
  - [Development Workflow](#development-workflow)
  - [CI/CD Pipeline](#ci-cd-pipeline)
  - [Writing Tests](#writing-tests)
  - [Test Annotations](#test-annotations)
- [Code Coverage](#code-coverage)
  - [Generating Coverage Reports](#generating-coverage-reports)
  - [Viewing Reports](#viewing-reports)
  - [Coverage Metrics](#coverage-metrics)
  - [Configuration](#configuration)
- [Code Formatting](#code-formatting)
  - [Install GIT Hook](#install-git-hook)
  - [Manually Format Code](#manually-format-code)
- [Dependency Management](#dependency-management)
  - [Check Updates](#check-updates)

## Running Tests

This project uses a structured approach to test execution with three distinct test categories organized within the `core` module.

### Test Categories

The project uses a structured test pyramid with four main categories:

|     Level      |              Type               |    Isolation    |  Dependencies  |  Database   | Speed |                              Examples                               |
|----------------|---------------------------------|-----------------|----------------|-------------|-------|---------------------------------------------------------------------|
| 🧱 Unit        | Individual classes in isolation | Complete        | None           | No          | 🚀    | `SemVerUtilTest`, `StandardSqlColumnReferencePsStrategyTest`        |
| 🔗 Component   | DSL + AST + Visitor + Renderer  | Real components | Mocked JDBC    | No (mocked) | 🚀    | `SelectDSLComponentTest`, `DSLRegistryComponentTest`                |
| 🔗 Integration | DSL/Plugin + H2 database        | Complete API    | H2 (embedded)  | Yes (H2)    | 🏃    | `SelectBuilderIntegrationTest`, `MysqlDialectPluginIntegrationTest` |
| 🌐 End-to-End  | Complete system workflow        | Entire System   | Testcontainers | Yes (real)  | 🐌    | `AstToPreparedStatementSpecVisitorTest`                             |

### Project Structure

All tests are consolidated within the `core` module with the following organization:

```
core/src/test/java/
├── io/github/auspis/fluentsql4j/                    # Unit tests (~200)
├── io/github/auspis/fluentsql4j/dsl/*ComponentTest  # Component tests (~50)
├── integration/                           # Integration tests (~20)
└── e2e/system/                            # E2E tests (~3)
```

### Basic Commands

```bash
# Run unit + component tests (fast feedback, no database)
./mvnw test -pl core

# Run all tests (unit + component + integration + e2e)
./mvnw verify -pl core

# Run tests with dependencies
./mvnw clean verify -am -pl core
```

### Selective Test Execution

```bash
# Run only unit tests
./mvnw test -pl core -Dgroups="\!component,\!integration,\!e2e"

# Run unit + component tests (fast feedback, no database)
./mvnw test -pl core -Dgroups="\!integration,\!e2e"

# Run only component tests
./mvnw test -pl core -Dgroups=component

# Run only integration tests
./mvnw verify -pl core -Dgroups=integration

# Run only e2e tests
./mvnw verify -pl core -Dgroups=e2e

# Run integration + e2e (skip unit + component)
./mvnw verify -pl core -Dgroups=integration,e2e
```

### Development Workflow

```bash
# Fast feedback loop (unit + component tests only, no database)
./mvnw clean test

# Pre-commit check (all tests)
./mvnw clean verify

# Integration check (skip unit + component tests)
./mvnw verify -pl core -Dgroups=integration

# Full e2e validation with real databases
./mvnw verify -pl core -Dgroups=e2e
```

### CI CD Pipeline

The GitHub Actions pipeline executes tests in stages:

1. **Fast Feedback** - Unit + Component tests (`mvn test`) - No database, complete in seconds
2. **Integration** - Integration tests with H2 (`mvn verify -Dgroups=integration`) - Embedded H2, complete in ~30s
3. **E2E Validation** - E2E tests with Testcontainers (`mvn verify -Dgroups=e2e`) - Real databases, complete in a few minutes

This staged approach provides:

- **Quick feedback** for developers (unit + component tests run in ~15-20s)
- **Medium feedback** for database integration issues (H2 integration tests run in ~30s)
- **Complete validation** for production readiness (E2E tests with real databases run in a few minutes)

### Writing Tests

#### Unit Test

```java
@Test
void shouldDoSomething() {
    // Fast, isolated test with no external dependencies
    assertThat(result).isEqualTo(expected);
}
```

#### Component Test

```java
@ComponentTest  // includes @Tag("component")
class SelectDSLComponentTest {
    
    @Test
    void shouldGenerateCorrectSQL() throws SQLException {
        // Test DSL API with mocked JDBC (real components, mocked database)
        PreparedStatement result = new SelectBuilder(specFactory, "id", "name")
                .from("users")
                .where()
                .column("age")
                .gt(18)
                .build(sqlCaptureHelper.getConnection());
        
        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT \"id\", \"name\" FROM \"users\" WHERE \"age\" > ?");
    }
}
```

#### Integration Test

```java
@IntegrationTest  // includes @Tag("integration")
class SelectBuilderIntegrationTest {
    
    @Test
    void shouldIntegrateWithDatabase() {
        // Test with H2 in-memory database
        Connection conn = TestDatabaseUtil.createH2Connection();
        // ... test code with real H2 database
    }
}
```

#### E2E Test

```java
@E2ETest  // includes @Tag("e2e")
@Testcontainers
class AstToPreparedStatementSpecVisitorE2E {
    
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

Custom test annotations are located in `io.github.auspis.fluentsql4j.test.util.annotation`:

- `@ComponentTest` - Marks component tests (tagged with `component`)
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
./mvnw clean test -pl core -am jacoco:report
./mvnw clean verify -pl core -am jacoco:report
```

### Viewing Reports

Coverage reports are generated as HTML files in each module's `target/site/jacoco/` directory:

- **core Module**: `core/target/site/jacoco/index.html`

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

## Code Formatting

### Install GIT Hook

To reduce conflicts, a hook is provided to automatically format code with Spotless on each commit.

```bash
./mvnw process-resources
```

This will install the pre-commit hook that runs `./mvnw spotless:apply` before each commit.

### Manually Format Code

You can manually format all code at any time:

```bash
./mvnw spotless:apply
```

This command will:
- Format all Java files according to the project's code style
- Sort POM files
- Format Markdown files

**IMPORTANT**: Always run this before committing changes to ensure the CI/CD pipeline succeeds.

## Dependency Management

### Check Updates

The project provides scripts to check for available updates to dependencies and plugins.

#### Dependencies

```bash
data/scripts/dependency-updates-show.sh
```

This script displays available updates for all project dependencies.

#### Plugins

```bash
data/scripts/plugin-updates-show.sh
```

This script displays available updates for all Maven plugins.

## See Also

- [DSL Usage Guide](DSL_USAGE_GUIDE.md) - Examples of using the JDSQL DSL
- [Plugin Development Guide](PLUGIN_DEVELOPMENT.md) - Creating custom dialect plugins

