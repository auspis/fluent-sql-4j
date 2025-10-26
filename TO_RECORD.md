# TO_RECORD.md

## Classi saltate per conversione a record

### AggregateCallProjection (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/selection/projection/AggregateCallProjection.java`
- Motivo: Estende una classe astratta immutabile, solo costruttori e metodi di accesso.
- Nota: Estende la classe `Projection`, quindi non può essere convertita a record (i record non possono ereditare da classi).

### Projection (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/selection/projection/Projection.java`
- Motivo: Campi final, costruttori, nessun comportamento mutabile.
- Nota: Classe astratta, quindi non può essere convertita a record.

### ScalarExpressionProjection (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/selection/projection/ScalarExpressionProjection.java`
- Motivo: Come sopra, solo costruttori e metodi di accesso.
- Nota: Estende la classe `Projection`, quindi non può essere convertita a record (i record non possono ereditare da classi).

### SelectStatement (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dql/SelectStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.
- Nota: Ha 7 campi, oltre il limite di 3 campi per costruttore record. Saltare per ora.

### MergeStatement (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/MergeStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.
- Nota: Ha 4 campi, oltre il limite di 3 campi per costruttore record. Saltare per ora.

### OnJoin (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/from/source/join/OnJoin.java`
- Motivo: Campi final, costruttori, nessun comportamento mutabile.
- Nota: Ha 4 campi, oltre il limite di 3 campi per costruttore record. Saltare per ora.

## Nuove classi candidabili per record

### AggregateCallImpl

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/aggregate/AggregateCallImpl.java`
- Motivo: Campi final, costruttore, nessun comportamento mutabile.

### CountDistinct

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/aggregate/CountDistinct.java`
- Motivo: Campo final, costruttore, nessun comportamento mutabile.

### InsertData.DefaultValues

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/item/InsertData.java`
- Motivo: Nessun campo, solo marker per valori di default.

### CreateTableStatement

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/CreateTableStatement.java`
- Motivo: Campo final, costruttore, nessun comportamento mutabile.

### ConstraintDefinition.PrimaryKeyDefinition

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/ConstraintDefinition.java`
- Motivo: Campo final, costruttore, nessun comportamento mutabile.

### ConstraintDefinition.NotNullConstraintDefinition

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/ConstraintDefinition.java`
- Motivo: Nessun campo, solo marker per constraint not null.

### ConstraintDefinition.UniqueConstraintDefinition

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/ConstraintDefinition.java`
- Motivo: Campo final, costruttore, nessun comportamento mutabile.

### ConstraintDefinition.ForeignKeyConstraintDefinition

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/ConstraintDefinition.java`
- Motivo: Campi final, costruttore, nessun comportamento mutabile.

### ConstraintDefinition.CheckConstraintDefinition

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/ConstraintDefinition.java`
- Motivo: Campo final, costruttore, nessun comportamento mutabile.

### ConstraintDefinition.DefaultConstraintDefinition

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/ddl/definition/ConstraintDefinition.java`
- Motivo: Campo final, costruttore, nessun comportamento mutabile.

