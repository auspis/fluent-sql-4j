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

