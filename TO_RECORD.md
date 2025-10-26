# TO_RECORD.md

## Stato Conversioni (✅, 🔄, ⏳)

## Classi candidate per conversione a record

## Classi saltate per conversione a record

### MergeStatement (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dml/MergeStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.
- Nota: Ha 4 campi, oltre il limite di 3 campi per costruttore record. Saltare per ora.

### SelectStatement (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/statement/dql/SelectStatement.java`
- Motivo: Campi final, builder pattern, nessun comportamento mutabile.
- Nota: Ha 7 campi, oltre il limite di 3 campi per costruttore record. Saltare per ora.

### OnJoin (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/clause/from/source/join/OnJoin.java`
- Motivo: Campi final, costruttori, nessun comportamento mutabile.
- Nota: Ha 4 campi, oltre il limite di 3 campi per costruttore record. Saltare per ora.

### CountStar (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/aggregate/CountStar.java`
- Motivo: Classe marker senza campi.
- Nota: Non ha campi, i record devono avere almeno un campo.

### CurrentDate (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/datetime/CurrentDate.java`
- Motivo: Classe marker senza campi.
- Nota: Non ha campi, i record devono avere almeno un campo.

### CurrentDateTime (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/datetime/CurrentDateTime.java`
- Motivo: Classe marker senza campi.
- Nota: Non ha campi, i record devono avere almeno un campo.

### PreparedStatementRenderer (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/visitor/ps/PreparedStatementRenderer.java`
- Motivo: Usa builder pattern con @Builder.
- Nota: Ha 2 campi ma usa builder pattern, non adatto per record.

### SqlRenderer (saltare)

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/visitor/sql/SqlRenderer.java`
- Motivo: Usa builder pattern con @Builder.
- Nota: Classe complessa con molti campi, usa builder pattern.

