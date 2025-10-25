# MERGE Statement Implementation

This document describes the technical implementation of SQL:2008 MERGE functionality in r4j.

## Architecture

### AST Nodes

#### MergeStatement

Main statement node representing the complete MERGE statement:

```java
@Builder
public class MergeStatement implements Statement {
    private final TableExpression targetTable;
    private final Alias targetAlias;
    private final MergeUsing using;
    private final Predicate onCondition;
    private final List<MergeAction> actions;
}
```

**Fields:**
- `targetTable`: Target table expression (where data will be merged into)
- `targetAlias`: Optional alias for the target table
- `using`: Source specification (table or subquery with optional alias)
- `onCondition`: Join condition predicate (how to match source and target rows)
- `actions`: List of WHEN clauses defining what to do when rows match or don't match

#### MergeUsing

Represents the USING clause, specifying the source of data:

```java
public class MergeUsing implements Visitable {
    private final TableExpression source;
    private final Alias sourceAlias;
}
```

**Supports:**
- Table references: `USING source_table`
- Subqueries: `USING (SELECT ...) AS alias`
- Optional aliasing for both cases

#### MergeAction

Abstract base class with three concrete implementations:

**WhenMatchedUpdate**: Executes UPDATE when source and target rows match

```java
public static class WhenMatchedUpdate extends MergeAction {
    private final List<UpdateItem> updateItems;
}
```

**WhenMatchedDelete**: Executes DELETE when source and target rows match

```java
public static class WhenMatchedDelete extends MergeAction {
    // No additional fields needed
}
```

**WhenNotMatchedInsert**: Executes INSERT when source row has no match in target

```java
public static class WhenNotMatchedInsert extends MergeAction {
    private final List<ColumnReference> columns;
    private final InsertData data;
}
```

All actions support optional conditional predicates via `searchCondition` field.

### DSL Builder Architecture

The `MergeBuilder` uses a **nested builder pattern** with two inner classes:

#### WhenMatchedUpdateBuilder

Handles WHEN MATCHED THEN UPDATE actions:
- Provides fluent `set()` methods for different data types
- Auto-commits the action when transitioning to next clause or calling `build()`
- Validates that at least one SET clause is specified
- Supports conditional updates via constructor parameter

#### WhenNotMatchedInsertBuilder

Handles WHEN NOT MATCHED THEN INSERT actions:
- Provides fluent `set()` methods that build column/value pairs
- Auto-commits the action when transitioning to next clause or calling `build()`
- Validates that at least one column is specified
- Validates column count matches value count
- Supports conditional inserts via constructor parameter

#### Auto-Commit Mechanism

Both nested builders implement an **auto-commit pattern**:
1. User calls fluent `set()` methods to accumulate data
2. When user calls `whenMatched()`, `whenNotMatched()`, or `build()`, the builder automatically:
- Validates accumulated data
- Creates the appropriate `MergeAction` instance
- Adds it to the parent `MergeBuilder.actions` list
- Marks itself as committed to prevent duplicate actions

This eliminates the need for explicit `then*()` methods, making the API more intuitive.

### Render Strategies

#### MergeStatementRenderStrategy

**Location**: `lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement.MergeStatementRenderStrategy`

**Responsibilities:**
- Orchestrates rendering of complete MERGE statement
- Delegates to specialized strategies for each clause
- Handles SQL:2008 standard syntax

**Output Format:**

```sql
MERGE INTO target_table [AS alias]
USING source
ON condition
WHEN MATCHED [AND condition] THEN UPDATE SET ...
WHEN MATCHED [AND condition] THEN DELETE
WHEN NOT MATCHED [AND condition] THEN INSERT (...) VALUES (...)
```

#### MergeUsingRenderStrategy

**Handles:**
- Table references: `source_table [AS alias]`
- Subqueries: `(SELECT ...) AS alias`
- Wraps subqueries in parentheses

#### WhenMatchedUpdateRenderStrategy

**Renders:**
- `WHEN MATCHED` keyword
- Optional `AND condition` clause
- `THEN UPDATE SET` clause
- Comma-separated update items

#### WhenMatchedDeleteRenderStrategy

**Renders:**
- `WHEN MATCHED` keyword
- Optional `AND condition` clause
- `THEN DELETE` keyword

#### WhenNotMatchedInsertRenderStrategy

**Renders:**
- `WHEN NOT MATCHED` keyword
- Optional `AND condition` clause
- `THEN INSERT` keyword
- Column list in parentheses
- `VALUES` keyword
- Value list in parentheses

### Prepared Statement Support

Both `MergeBuilder` and nested builders support prepared statements:

```java
PreparedStatement buildPreparedStatement(Connection connection) throws SQLException
```

**Process:**
1. Validates the builder state
2. Constructs the `MergeStatement` AST node
3. Renders to SQL with parameter placeholders (`?`)
4. Extracts parameter values in order
5. Creates `PreparedStatement` and binds parameters

## SQL:2008 Standard Compliance

The implementation follows SQL:2008 standard syntax:

```sql
<merge statement> ::=
  MERGE INTO <target table> [ [ AS ] <merge correlation name> ]
  USING <table reference>
  ON <search condition>
  <merge operation specification>

<merge operation specification> ::=
  <when clause>...

<when clause> ::=
  <when matched clause> | <when not matched clause>

<when matched clause> ::=
  WHEN MATCHED [ AND <search condition> ]
  THEN <merge update or delete specification>

<when not matched clause> ::=
  WHEN NOT MATCHED [ AND <search condition> ]
  THEN <merge insert specification>
```

### Standard Features Supported

✅ Target table with optional alias  
✅ Table or subquery as source with alias  
✅ Complex join conditions  
✅ Multiple WHEN clauses  
✅ Conditional WHEN clauses (AND condition)  
✅ UPDATE action  
✅ DELETE action  
✅ INSERT action  
✅ Prepared statement support

### Limitations

⚠️ The implementation currently supports:
- One UPDATE action per MERGE (standard allows multiple with different conditions)
- One DELETE action per MERGE (standard allows multiple with different conditions)
- One INSERT action per MERGE (standard allows multiple with different conditions)

This is a design choice to keep the DSL API simple. For complex scenarios, use multiple MERGE statements.

## Testing Strategy

### Unit Tests

**Location**: `lan.tlab.r4j.sql.dsl.merge.MergeBuilderTest`

**Coverage:**
- Basic merge with table source
- Merge with subquery source
- Conditional actions (WHEN ... AND condition)
- DELETE actions
- Multiple actions in same MERGE
- Fluent API with multiple SET clauses
- Mixed data types (String, Number, Boolean, Expression)
- Column reference expressions
- Validation (missing clauses, mismatched columns/values)
- Error handling

### Integration Tests

**Location**: `test-integration` module

Tests actual SQL execution against real databases using Testcontainers.

## Implementation Notes

### Why Nested Builders?

1. **Type Safety**: Compile-time guarantee that only valid method sequences are possible
2. **Discoverability**: IDE auto-completion guides users through valid API calls
3. **Consistency**: Matches `UpdateBuilder` and `InsertBuilder` patterns
4. **Simplicity**: No need to manually construct AST nodes

### Why Auto-Commit?

Traditional approach required explicit `then*()` methods:

```java
.whenMatched().set(...).thenUpdate()  // Explicit commit
```

Auto-commit approach is more intuitive:

```java
.whenMatched().set(...)  // Implicit commit on transition
.whenNotMatched().set(...)
```

The builder detects when you're moving to the next clause and automatically commits the current action.

### Delete Action Design

The `delete()` method is part of `WhenMatchedUpdateBuilder` because:
1. DELETE is only valid for WHEN MATCHED (not WHEN NOT MATCHED)
2. DELETE and UPDATE are mutually exclusive
3. Validation ensures `delete()` cannot be called after `set()`

## Future Enhancements

Potential improvements:
- Support for MERGE...RETURNING clause
- Support for multiple conditional actions of same type
- Default values for INSERT when source column is NULL
- Bulk MERGE operations
- Diagnostic output for complex MERGE statements

## References

- SQL:2008 Standard, Foundation (SQL/Foundation), Section 14.12: MERGE statement
- [README.md](./README.md) - Usage examples and quick start guide

