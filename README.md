# r4j - Repo4J

## Project structure

The project is split into Maven modules:
- `sqlbuilder`: contains the main code and unit tests
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
  - **Unit tests** in the `sqlbuilder` module
  - **H2 integration tests** in the `test-integration` module (fast, in-memory database)
- Running `./mvnw verify` will additionally execute:
  - **E2E tests** with Testcontainers in the `test-integration` module (slower, real databases)
- The `test-integration` module depends on `sqlbuilder` and contains all the necessary dependencies for testing (JUnit, Testcontainers, H2, etc).
- **H2 integration tests** are named with the `*Test.java` suffix and run with Surefire.
- **E2E tests** are named with the `*E2E.java` suffix and run with Failsafe.
- This ensures optimal development workflow: fast feedback with `test`, complete validation with `verify`.

## check updates

### dependencies

```bash
data/scripts/dependency-updates-show.sh
```

### plugins

```bash
data/scripts/plugin-updates-show.sh
```

