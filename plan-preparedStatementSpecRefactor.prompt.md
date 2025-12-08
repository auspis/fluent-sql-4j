## Refactoring Plan: PreparedStatementSpec & Factory Naming

### 1. Rename PsDto → PreparedStatementSpec

- Update all usages, imports, and documentation
- Ensure the new name reflects its role: SQL + parameters for PreparedStatement

### 2. Rename DialectRenderer → PreparedStatementSpecFactory

- Update all usages, imports, and documentation
- Responsibility: given a Statement, produce a PreparedStatementSpec (prepares context and delegates rendering)

### 3. Update variable, parameter, and field names for consistency

- All `renderer` → `specFactory`
- All `psDto` → `spec` (or `preparedStatementSpec` for clarity)
- Update constructors, method signatures, and usages

### 4. Update documentation and comments

- Javadoc, README, guides, and code comments
- Ensure all examples use the new names

### 5. Update tests and examples

- Refactor test code to use new names
- Validate that all tests pass after renaming

### 6. Validate and clean up

- Search for any remaining old names
- Ensure no obsolete references remain

---

#### Example after refactor

```java
PreparedStatementSpecFactory specFactory = ...;
PreparedStatementSpec spec = specFactory.create(statement);
PreparedStatement ps = PsUtil.preparedStatement(spec, connection);
```

---

#### Steps

1. Rename classes and files
2. Update all references in codebase
3. Update documentation
4. Run and validate all tests

