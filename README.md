# r4j - Repo4J

## Project structure

The project is split into Maven modules:
- `sqlbuilder`: contains the main code and unit tests
- `test-integration`: contains only integration tests (slow, using Testcontainers)

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

### To run unit  tests

```bash
./mvnw test
```

### To run all tests (unit + integration)

```bash
./mvnw verify
```

## Integration test naming convention

Integration tests must use the `*IT.java` suffix (e.g. `MyFeatureIT.java`).
This is required for the Maven Failsafe Plugin to detect and execute them correctly.

## How to run only integration tests

To run only the integration tests (without running unit tests in other modules):

```bash
./mvnw verify -pl test-integration -am -DskipTests
```

This will:
- Build all required modules (using `-am`)
- Skip unit tests in dependencies (using `-DskipTests`)
- Run only the integration tests in `test-integration` (using Failsafe)

### Technical details

- Running `./mvnw test` will execute **only the unit tests** in the `sqlbuilder` module.
- Integration tests are **NOT** executed, because they are in a separate module (`test-integration`) that contains only tests and no production code.
- Integration tests are located only in the `test-integration` module.
- The `test-integration` module depends on `sqlbuilder` and contains all the necessary dependencies for testing (JUnit, Testcontainers, etc).
- The standard build cycle (`test`, `package`, `install`) on `sqlbuilder` excludes integration tests.
- The build cycle on `test-integration` runs only the integration tests.
- Integration tests must be named with the `*IT.java` suffix.
- The Maven Failsafe Plugin is configured to run only these tests in the `test-integration` module.
- Surefire is configured to exclude `*IT.java` files.
- This ensures a clear separation between unit and integration tests.

## check updates

### dependencies

```bash
data/scripts/dependency-updates-show.sh
```

### plugins

```bash
data/scripts/plugin-updates-show.sh
```

