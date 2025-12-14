# Test Helper and Assertion Utilities - Usage Guide

## Overview

This document describes the new helper and assertion utilities created to reduce boilerplate code in tests and provide fluent, readable assertions for SQL and JDBC mock objects.

## Components

### 1. SqlAssert - Custom AssertJ Assertion for SQL

**Location**: `test-support/src/main/java/lan/tlab/r4j/jdsql/test/SqlAssert.java`

**Purpose**: Provides fluent assertions for SQL strings, following the same pattern as `JsonAssert`.

**Key Features**:
- Exact equality comparison
- Whitespace-normalized comparison
- Fragment containment (single or multiple)
- Ordered fragment verification
- Prefix/suffix matching

**Usage Example**:

```java
import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;

@Test
void exampleSqlAssertion() {
    String sql = "SELECT \"name\", \"email\" FROM \"users\" WHERE \"age\" > ?";
    
    // Exact match
    assertThatSql(sql).isEqualTo("SELECT \"name\", \"email\" FROM \"users\" WHERE \"age\" > ?");
    
    // Contains fragments
    assertThatSql(sql)
        .contains("SELECT")
        .contains("FROM \"users\"")
        .contains("WHERE \"age\" > ?");
    
    // Ordered fragments
    assertThatSql(sql).containsInOrder("SELECT", "FROM", "WHERE");
    
    // Normalized whitespace comparison (ignores extra spaces/newlines)
    assertThatSql(sql)
        .isEqualToNormalizingWhitespace("SELECT \"name\", \"email\"   FROM   \"users\"   WHERE \"age\" > ?");
}
```

### 2. MockedConnectionHelper - Helper Class for JDBC Mocks

**Location**: `test-support/src/main/java/lan/tlab/r4j/jdsql/test/helper/MockedConnectionHelper.java`

**Purpose**: Encapsulates common JDBC mock objects (Connection, PreparedStatement, SQL captor) and automatically sets them up for testing.

**Key Features**:
- Automatically creates and configures mock Connection and PreparedStatement
- Captures SQL passed to `prepareStatement(String)`
- Provides convenient accessors: `getConnection()`, `getPreparedStatement()`, `getSql()`
- Zero-boilerplate setup in constructor

**Usage Example**:

```java
import lan.tlab.r4j.jdsql.test.helper.MockedConnectionHelper;
import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.mockito.Mockito.*;

class MyBuilderTest {
    private MockedConnectionHelper mockHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        // All mocks are created automatically in constructor
        mockHelper = new MockedConnectionHelper();
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
    }

    @Test
    void myTest() throws SQLException {
        new SelectBuilder(specFactory, "name")
            .from("users")
            .where()
            .column("age")
            .gt(18)
            .buildPreparedStatement(mockHelper.getConnection());

        // Use helper to get captured SQL and assert
        assertThatSql(mockHelper.getSql())
            .contains("SELECT \"name\"")
            .contains("FROM \"users\"")
            .contains("WHERE \"age\" > ?");

        // Verify parameter binding
        verify(mockHelper.getPreparedStatement()).setObject(1, 18);
    }
}
```

## Benefits

### Code Reduction

- **Before**: ~30 lines of boilerplate setup per test class
- **After**: ~15 lines with MockedConnectionHelper
- **Savings**: ~50% reduction in setup code

### Improved Readability

- SQL assertions are now fluent and self-documenting
- Mock setup is centralized and consistent
- Less noise in test methods

### Comparison: Before vs After

#### Before (Without Helpers)

```java
class SelectBuilderTest {
    private PreparedStatementSpecFactory specFactory;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
    }

    @Test
    void simpleSelect() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "name", "email")
            .from("users")
            .buildPreparedStatement(connection);

        assertThat(result).isSameAs(ps);
        assertThat(sqlCaptor.getValue()).isEqualTo("SELECT \"name\", \"email\" FROM \"users\"");
    }
}
```

#### After (With Helpers)

```java
class SelectBuilderTest {
    private MockedConnectionHelper mockHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        mockHelper = new MockedConnectionHelper();  // All mocks created automatically!
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
    }

    @Test
    void simpleSelect() throws SQLException {
        new SelectBuilder(specFactory, "name", "email")
            .from("users")
            .buildPreparedStatement(mockHelper.getConnection());

        assertThatSql(mockHelper.getSql()).isEqualTo("SELECT \"name\", \"email\" FROM \"users\"");
    }
}
```

## Migration Guide

### When to Use These Utilities

**Use `MockedConnectionHelper` when:**
- Writing unit tests for SQL builders (SelectBuilder, InsertBuilder, etc.)
- Writing integration tests that verify DSL → Builder → Renderer flow with mocks
- You need to verify SQL generation without database I/O

**Do NOT use `MockedConnectionHelper` when:**
- Writing integration tests with real database (use `TestDatabaseUtil` instead)
- Writing E2E tests (use Testcontainers)

**Use `SqlAssert` when:**
- Asserting on generated SQL strings
- You want more readable assertions than plain `assertThat(...).contains(...)`
- You need to verify SQL structure or fragment ordering

### Step-by-Step Migration

1. **Add imports**:

   ```java
   import lan.tlab.r4j.jdsql.test.helper.MockedConnectionHelper;
   import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
   ```
2. **Replace field declarations**:

   ```java
   // Before
   private Connection connection;
   private PreparedStatement ps;
   private ArgumentCaptor<String> sqlCaptor;

   // After
   private MockedConnectionHelper mockHelper;
   ```
3. **Update @BeforeEach**:

   ```java
   @BeforeEach
   void setUp() throws SQLException {
       // All mocks are created automatically by constructor!
       mockHelper = new MockedConnectionHelper();

       // Remove all the old mock setup code:
       // connection = mock(Connection.class);
       // ps = mock(PreparedStatement.class);
       // sqlCaptor = ArgumentCaptor.forClass(String.class);
       // when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
   }
   ```
4. **Update test methods**:

   ```java
   // Before
   builder.buildPreparedStatement(connection);
   assertThat(sqlCaptor.getValue()).contains("SELECT");
   verify(ps).setObject(1, value);

   // After
   builder.buildPreparedStatement(mockHelper.getConnection());
   assertThatSql(mockHelper.getSql()).contains("SELECT");
   verify(mockHelper.getPreparedStatement()).setObject(1, value);
   ```

## Example: Complete Refactored Test

See `SelectBuilderRefactoredExampleTest` in `jdsql-core/src/test/java/lan/tlab/r4j/jdsql/dsl/select/` for a complete working example.

## Next Steps

To fully realize the benefits of these utilities, consider:

1. **Migrate existing tests gradually** - Start with one test class, validate it works, then proceed
2. **Extract more setup logic** - Consider creating specialized helpers for DSL tests or specific builder types
3. **Add more assertion methods** - Extend `SqlAssert` with domain-specific assertions as needed
4. **Document patterns** - Update team wiki or README with common testing patterns

## Design Decisions

### Why MockedConnectionHelper Creates Mocks in Constructor?

This approach eliminates all boilerplate setup code. Since JDBC mocking requirements are standardized in this project (always: Connection → mock, prepareStatement → capture SQL, return PreparedStatement mock), we encapsulate this in the constructor for maximum code reduction.

### Why Follow JsonAssert Pattern for SqlAssert?

`JsonAssert` already demonstrates the correct pattern for custom AssertJ assertions in this project. Consistency in code style makes the codebase easier to navigate and maintain. Both follow the same fluent API style and are located in the same `test-support` module.

### Why Not Full Inheritance?

We avoided base test classes (AbstractBuilderTest, etc.) because:
- Composition is more flexible than inheritance
- Allows tests to extend other base classes if needed
- Helper and util classes are easier to combine than rigid class hierarchies
- Aligns with the existing pattern in the codebase (TestDatabaseUtil is a util class, not a base class)

## Related Files

- `test-support/src/main/java/lan/tlab/r4j/jdsql/test/JsonAssert.java` - Similar custom assertion for JSON
- `test-support/src/main/java/lan/tlab/r4j/jdsql/test/util/TestDatabaseUtil.java` - Utility for real database connections
- `jdsql-core/src/test/java/lan/tlab/r4j/jdsql/dsl/select/SelectBuilderRefactoredExampleTest.java` - Complete example

