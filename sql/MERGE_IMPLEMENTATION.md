# MERGE Statement Implementation

This document describes the implementation of SQL:2008 MERGE functionality in r4j.

## Overview

The MERGE statement (also known as UPSERT) allows inserting a new record or updating an existing one based on a condition, eliminating the need for separate INSERT/UPDATE logic.

## Architecture

### AST Nodes

#### MergeStatement

Main statement node containing:
- `targetTable`: Target table expression
- `targetAlias`: Optional alias for target table
- `using`: Source specification (MergeUsing)
- `onCondition`: Join condition predicate
- `actions`: List of WHEN clauses (MergeAction)

#### MergeUsing

Represents the USING clause:
- `source`: Table expression (table or subquery)
- `sourceAlias`: Optional alias for source

#### MergeAction

Interface with three implementations:
- `WhenMatchedUpdate`: UPDATE when rows match
- `WhenMatchedDelete`: DELETE when rows match
- `WhenNotMatchedInsert`: INSERT when rows don't match

Each action can have an optional conditional predicate.

### DSL Builder

The `MergeBuilder` provides a fluent API:

```java
dsl.mergeInto("target_table")
    .as("tgt")
    .using("source_table", "src")
    .on("tgt.id", "src.id")
    .whenMatchedThenUpdate(updateItems)
    .whenNotMatchedThenInsert(columns, values)
    .build();
```

#### Builder Methods

- `as(String alias)`: Set target table alias
- `using(String table)`: Specify source table
- `using(String table, String alias)`: Specify source table with alias
- `using(SelectStatement subquery, String alias)`: Use subquery as source
- `on(String leftColumn, String rightColumn)`: Set join condition
- `on(Predicate condition)`: Set custom join condition
- `whenMatchedThenUpdate(List<UpdateItem>)`: Add UPDATE action
- `whenMatchedThenUpdate(Predicate, List<UpdateItem>)`: Add conditional UPDATE
- `whenMatchedThenDelete()`: Add DELETE action
- `whenMatchedThenDelete(Predicate)`: Add conditional DELETE
- `whenNotMatchedThenInsert(List<ColumnReference>, List<Expression>)`: Add INSERT action
- `whenNotMatchedThenInsert(Predicate, List<ColumnReference>, List<Expression>)`: Add conditional INSERT

### Render Strategies

#### MergeStatementRenderStrategy

Renders the complete MERGE statement.

#### MergeUsingRenderStrategy

Renders the USING clause, wrapping subqueries in parentheses.

#### WhenMatchedUpdateRenderStrategy

Renders UPDATE actions with optional conditions.

#### WhenMatchedDeleteRenderStrategy

Renders DELETE actions with optional conditions.

#### WhenNotMatchedInsertRenderStrategy

Renders INSERT actions with optional conditions.

## SQL:2008 Standard Syntax

```sql
MERGE INTO target_table [AS target_alias]
USING source_table|subquery [AS source_alias]
ON join_condition
[WHEN MATCHED [AND condition] THEN UPDATE SET ...]
[WHEN MATCHED [AND condition] THEN DELETE]
[WHEN NOT MATCHED [AND condition] THEN INSERT (...) VALUES (...)]
```

## Examples

### Basic Table-to-Table Merge

```java
String sql = dsl.mergeInto("products")
    .as("p")
    .using("new_products", "np")
    .on("p.product_id", "np.product_id")
    .whenMatchedThenUpdate(List.of(
        UpdateItem.of("name", ColumnReference.of("np", "name")),
        UpdateItem.of("price", ColumnReference.of("np", "price"))))
    .whenNotMatchedThenInsert(
        List.of(ColumnReference.of("p", "product_id")),
        List.of(ColumnReference.of("np", "product_id")))
    .build();
```

### Using Subquery as Source

```java
SelectStatement subquery = dsl.select("id", "name")
    .from("staging_products")
    .where("status").eq("active")
    .getCurrentStatement();

String sql = dsl.mergeInto("products")
    .using(subquery, "src")
    .on("products.id", "src.id")
    .whenMatchedThenUpdate(List.of(
        UpdateItem.of("name", ColumnReference.of("src", "name"))))
    .build();
```

### Conditional Actions

```java
String sql = dsl.mergeInto("inventory")
    .using("new_stock", "ns")
    .on("inventory.product_id", "ns.product_id")
    .whenMatchedThenUpdate(
        Comparison.gt(ColumnReference.of("ns", "quantity"), Literal.of(0)),
        List.of(UpdateItem.of("quantity", ColumnReference.of("ns", "quantity"))))
    .whenMatchedThenDelete(
        Comparison.eq(ColumnReference.of("ns", "quantity"), Literal.of(0)))
    .build();
```

## Use Cases

1. **Data Synchronization**: Sync data between tables or from external sources
2. **ETL Operations**: Load and update data in data warehouses
3. **Incremental Loads**: Update changed records and insert new ones
4. **Avoiding Race Conditions**: Single statement prevents concurrent update issues

## Testing

### Unit Tests (MergeBuilderTest)

- Basic MERGE operations
- Subquery sources
- Conditional actions
- Validation and error handling
- 11 tests covering all builder methods

### E2E Tests (MergeE2ETest)

- Real-world scenarios
- Complex multi-action merges
- Subquery integration
- 4 comprehensive integration tests

## Limitations and Future Work

### Current Limitations

1. **PreparedStatement Support**: Currently renders as direct SQL without parameterization
2. **No Dialect-Specific Optimizations**: Uses only standard SQL:2008 syntax

### Future Enhancements

1. Add full PreparedStatement parameterization
2. Implement dialect-specific syntax:
   - MySQL: `INSERT ... ON DUPLICATE KEY UPDATE`
   - PostgreSQL: `INSERT ... ON CONFLICT`
3. Add database integration tests
4. Support MERGE with VALUES clause
5. Add performance optimizations

## Integration with Existing Code

The MERGE implementation follows the existing patterns:
- AST nodes implement `Visitable`
- Builder follows fluent API pattern like InsertBuilder, UpdateBuilder
- Render strategies follow existing strategy pattern
- Tests use TestDialectRendererFactory

## References

- SQL:2008 Standard (ISO/IEC 9075-2:2008)
- [Issue #XX](link-to-issue): Original feature request
- Test files: `MergeBuilderTest.java`, `MergeE2ETest.java`

