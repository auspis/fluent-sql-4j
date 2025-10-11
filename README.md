# r4j - Repo4J

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
String sql = DSL.selectCountStar().from("users").build();
// → SELECT COUNT(*) FROM "users"

// SUM with GROUP BY
String sql = DSL.selectSum("amount", "total")
    .from("orders")
    .groupBy("customer_id")
    .build();
// → SELECT SUM("orders"."amount") AS total FROM "orders" GROUP BY "orders"."customer_id"

// AVG with HAVING clause
String sql = DSL.selectAvg("salary")
    .from("employees")
    .groupBy("department")
    .having("department").ne("HR")
    .build();
// → SELECT AVG("employees"."salary") FROM "employees" 
//   GROUP BY "employees"."department" 
//   HAVING "employees"."department" != 'HR'

// COUNT DISTINCT with WHERE
String sql = DSL.selectCountDistinct("email", "unique_emails")
    .from("users")
    .where("active").eq(true)
    .build();
// → SELECT COUNT(DISTINCT "users"."email") AS unique_emails 
//   FROM "users" WHERE "users"."active" = true
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

