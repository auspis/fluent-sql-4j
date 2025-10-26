# TO_RECORD.md

## Classi candidabili per conversione a record

### 1. Alias

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/identifier/Alias.java`
- Motivo: Solo campo `name`, costruttore, nessun comportamento mutabile.

### 2. TableIdentifier

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/identifier/TableIdentifier.java`
- Motivo: Campi `name` e `alias`, costruttori e getter, nessuno stato mutabile.

### 3. Sorting

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/orderby/Sorting.java`
- Motivo: Campi final, factory statici, enum interna, nessun comportamento mutabile.

### 4. AggregateCallProjection

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/selection/projection/AggregateCallProjection.java`
- Motivo: Estende una classe astratta immutabile, solo costruttori e metodi di accesso.

### 5. Projection

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/selection/projection/Projection.java`
- Motivo: Campi final, costruttori, nessun comportamento mutabile.

### 6. ScalarExpressionProjection

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/selection/projection/ScalarExpressionProjection.java`
- Motivo: Come sopra, solo costruttori e metodi di accesso.

### 7. ColumnReference

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/ColumnReference.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### 8. Literal

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/Literal.java`
- Motivo: Campo final, factory statici, nessun comportamento mutabile.

---

## Nuove classi candidabili per record

### 9. NullPredicate

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/NullPredicate.java`
- Motivo: Nessun campo, solo implementazione di un’interfaccia e metodo di visita.

### 10. IsNull

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/IsNull.java`
- Motivo: Campo final, costruttore, nessun comportamento mutabile.

### 11. IsNotNull

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/IsNotNull.java`
- Motivo: Come sopra.

### 12. Between

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/Between.java`
- Motivo: Solo campi final, costruttore, nessun comportamento mutabile.

### 13. Not

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/logical/Not.java`
- Motivo: Campo final, costruttore, nessun comportamento mutabile.

### 14. AndOr

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/logical/AndOr.java`
- Motivo: Campi final, factory statici, nessun comportamento mutabile.

### 15. Like

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/Like.java`
- Motivo: Campi final, costruttore, nessun comportamento mutabile.

### 16. Comparison

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/Comparison.java`
- Motivo: Campi final, factory statici, enum interna, nessun comportamento mutabile.

### 17. In

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/In.java`
- Motivo: Campi final, costruttore, nessun comportamento mutabile.

---

Queste classi ampliano la lista dei record candidati, favorendo la chiarezza e l’immutabilità nel modulo SQL.

## Candidati record dal package visitor

### 41. AstContext

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/visitor/AstContext.java`
- Motivo: Campo final, costruttore, nessun comportamento mutabile.

## Candidati record dal package statement/dml/item

### 40. UpdateItem

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/item/UpdateItem.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

## Candidati record dal package statement/dql

### 39. SelectStatement

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dql/SelectStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

## Candidati record dai package statement/dml

### 35. UpdateStatement

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/UpdateStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### 36. InsertStatement

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/InsertStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### 37. DeleteStatement

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/DeleteStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### 38. MergeStatement

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/MergeStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

## Candidati record dai package clause

### 28. Fetch

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/fetch/Fetch.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### 29. OrderBy

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/orderby/OrderBy.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### 30. GroupBy

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/groupby/GroupBy.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### 31. Where

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/conditional/where/Where.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### 32. From

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/from/From.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### 33. Having

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/conditional/having/Having.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### 34. Select

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/selection/Select.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

## Altri candidati record da expression/scalar e set

### 22. NullScalarExpression

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/NullScalarExpression.java`
- Motivo: Nessun campo, solo implementazione e metodo di visita.

### 23. ScalarSubquery

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/ScalarSubquery.java`
- Motivo: Campo final, builder pattern, nessun comportamento mutabile.

### 24. NullSetExpression

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/set/NullSetExpression.java`
- Motivo: Nessun campo, solo implementazione e metodo di visita.

### 25. ExceptExpression

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/set/ExceptExpression.java`
- Motivo: Campi final, factory statici, nessun comportamento mutabile.

### 26. IntersectExpression

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/set/IntersectExpression.java`
- Motivo: Campi final, factory statici, enum interna, nessun comportamento mutabile.

### 27. UnionExpression

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/set/UnionExpression.java`
- Motivo: Campi final, factory statici, enum interna, nessun comportamento mutabile.

## Altre classi candidabili per record

### 18. IndexDefinition

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/IndexDefinition.java`
- Motivo: Campi final, costruttore, nessun comportamento mutabile.

### 19. ColumnDefinition

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/ColumnDefinition.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### 20. TableDefinition

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/TableDefinition.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### 21. ReferencesItem

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/ReferencesItem.java`
- Motivo: Campi final, costruttore, nessun comportamento mutabile.

