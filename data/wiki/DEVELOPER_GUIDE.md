# Developer Guide

This guide covers the development workflow, testing strategies, code coverage, and maintenance tasks for JDSQL.

## Table of Contents

- [Running Tests](#running-tests)
  - [Test Categories](#test-categories)
  - [Project Structure](#project-structure)
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
- [Code Formatting](#code-formatting)
  - [Install GIT Hook](#install-git-hook)
  - [Manually Format Code](#manually-format-code)
- [Dependency Management](#dependency-management)
  - [Check Updates](#check-updates)

## Running Tests

This project uses a structured approach to test execution with three distinct test categories organized within the `jdsql-core` module.

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

All tests are consolidated within the `jdsql-core` module with the following organization:

```
jdsql-core/src/test/java/
├── lan/tlab/r4j/jdsql/       # Unit tests (205+)
├── integration/              # Integration tests (21)
└── e2e/system/               # E2E tests (3)
```

### Basic Commands

```bash
# Run only unit tests (fast, no containers)
./mvnw test -pl jdsql-core

# Run all tests (unit + integration + e2e)
./mvnw verify -pl jdsql-core

# Run tests with dependencies
./mvnw clean verify -am -pl jdsql-core
```

### Selective Test Execution

```bash
# Run only integration tests
./mvnw verify -pl jdsql-core -Dgroups=integration

# Run only e2e tests
./mvnw verify -pl jdsql-core -Dgroups=e2e

# Run both integration and e2e (skip unit tests)
./mvnw verify -pl jdsql-core -Dgroups=integration,e2e
```

### Development Workflow

```bash
# Fast feedback loop (unit tests only)
./mvnw clean test

# Pre-commit check (unit + integration + e2e)
./mvnw clean verify

# Quick integration check (no slow E2E tests)
./mvnw verify -pl jdsql-core -Dgroups=integration

# Full e2e validation with real databases
./mvnw verify -pl jdsql-core -Dgroups=e2e
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

Custom test annotations are located in `lan.tlab.r4j.jdsql.util.annotation`:

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
./mvnw clean test -pl jdsql-core -am jacoco:report
./mvnw clean verify -pl jdsql-core -am jacoco:report
```

### Viewing Reports

Coverage reports are generated as HTML files in each module's `target/site/jacoco/` directory:

- **jdsql-core Module**: `jdsql-core/target/site/jacoco/index.html`

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

