# TO_RECORD.md

## Classi candidabili per conversione a record

### TableIdentifier

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/identifier/TableIdentifier.java`
- Motivo: Campi `name` e `alias`, costruttori e getter, nessuno stato mutabile.

### Sorting

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/orderby/Sorting.java`
- Motivo: Campi final, factory statici, enum interna, nessun comportamento mutabile.

### AggregateCallProjection

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/selection/projection/AggregateCallProjection.java`
- Motivo: Estende una classe astratta immutabile, solo costruttori e metodi di accesso.

### Projection

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/selection/projection/Projection.java`
- Motivo: Campi final, costruttori, nessun comportamento mutabile.

### ScalarExpressionProjection

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/selection/projection/ScalarExpressionProjection.java`
- Motivo: Come sopra, solo costruttori e metodi di accesso.

### ColumnReference

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/ColumnReference.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### Literal

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/Literal.java`
- Motivo: Campo final, factory statici, nessun comportamento mutabile.

---

## Nuove classi candidabili per record

### NullPredicate

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/NullPredicate.java`
- Motivo: Nessun campo, solo implementazione di un’interfaccia e metodo di visita.

### IsNull

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/IsNull.java`
- Motivo: Campo final, costruttore, nessun comportamento mutabile.

### IsNotNull

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/IsNotNull.java`
- Motivo: Come sopra.

### Between

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/Between.java`
- Motivo: Solo campi final, costruttore, nessun comportamento mutabile.

### Not

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/logical/Not.java`
- Motivo: Campo final, costruttore, nessun comportamento mutabile.

### AndOr

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/logical/AndOr.java`
- Motivo: Campi final, factory statici, nessun comportamento mutabile.

### Like

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/Like.java`
- Motivo: Campi final, costruttore, nessun comportamento mutabile.

### Comparison

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/Comparison.java`
- Motivo: Campi final, factory statici, enum interna, nessun comportamento mutabile.

### In

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/predicate/In.java`
- Motivo: Campi final, costruttore, nessun comportamento mutabile.

---

Queste classi ampliano la lista dei record candidati, favorendo la chiarezza e l’immutabilità nel modulo SQL.

## Candidati record dal package visitor

### AstContext

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/visitor/AstContext.java`
- Motivo: Campo final, costruttore, nessun comportamento mutabile.

## Candidati record dal package statement/dml/item

### UpdateItem

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/item/UpdateItem.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

## Candidati record dal package statement/dql

### SelectStatement

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dql/SelectStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

## Candidati record dai package statement/dml

### UpdateStatement

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/UpdateStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### InsertStatement

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/InsertStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### DeleteStatement

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/DeleteStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### MergeStatement

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/MergeStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

## Candidati record dai package clause

### Fetch

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/fetch/Fetch.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### OrderBy

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/orderby/OrderBy.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### GroupBy

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/groupby/GroupBy.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### Where

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/conditional/where/Where.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### From

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/from/From.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### Having

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/conditional/having/Having.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### Select

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/selection/Select.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

## Altri candidati record da expression/scalar e set

### NullScalarExpression

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/NullScalarExpression.java`
- Motivo: Nessun campo, solo implementazione e metodo di visita.

### ScalarSubquery

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/ScalarSubquery.java`
- Motivo: Campo final, builder pattern, nessun comportamento mutabile.

### NullSetExpression

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/set/NullSetExpression.java`
- Motivo: Nessun campo, solo implementazione e metodo di visita.

### ExceptExpression

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/set/ExceptExpression.java`
- Motivo: Campi final, factory statici, nessun comportamento mutabile.

### IntersectExpression

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/set/IntersectExpression.java`
- Motivo: Campi final, factory statici, enum interna, nessun comportamento mutabile.

### UnionExpression

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/set/UnionExpression.java`
- Motivo: Campi final, factory statici, enum interna, nessun comportamento mutabile.

## Altre classi candidabili per record

### IndexDefinition

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/IndexDefinition.java`
- Motivo: Campi final, costruttore, nessun comportamento mutabile.

### ColumnDefinition

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/ColumnDefinition.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### TableDefinition

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/TableDefinition.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.

### ReferencesItem

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/ReferencesItem.java`
- Motivo: Campi final, costruttore, nessun comportamento mutabile.

