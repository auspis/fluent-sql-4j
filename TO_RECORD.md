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

## Migrazione Completata ✅

La migrazione sistematica delle classi Lombok a Java records è stata completata con successo.

### Classi Convertite (17 totali)

1. **Trim** ✅ - Convertito a record con campo `expression`
2. **Substring** ✅ - Convertito a record con campi `expression`, `start`, `length`
3. **Replace** ✅ - Convertito a record con campi `expression`, `pattern`, `replacement`
4. **UnaryString** ✅ - Convertito a record con campi `functionName`, `expression`
5. **Cast** ✅ - Convertito a record con campi `expression`, `dataType`
6. **ExtractDatePart** ✅ - Convertito a record con campi `functionName`, `dateExpression`
7. **DataLength** ✅ - Convertito a record con campo `expression`
8. **DateArithmetic** ✅ - Convertito a record con campi `add`, `dateExpression`, `interval`
9. **Concat** ✅ - Convertito a record con campi `separator`, `stringExpressions`
10. **Left** ✅ - Convertito a record con campi `expression`, `length`
11. **CharLength** ✅ - Convertito a record con campo `expression`
12. **UnaryNumeric** ✅ - Convertito a record con campi `functionName`, `numericExpression`
13. **Mod** ✅ - Convertito a record con campi `dividend`, `divisor`
14. **Round** ✅ - Convertito a record con campi `numericExpression`, `decimalPlaces`
15. **Interval** ✅ - Convertito a record con campi `value`, `unit`
16. **Length** ✅ - Convertito a record con campo `expression`

### Risultati della Migrazione

- **Test passati**: 1093 unit tests + 50 integration tests (0 failures, 0 errors)
- **Codice formattato**: Tutti i file formattati con spotless
- **Commit creato**: `51ef41c` - "Migrate remaining Lombok classes to Java records"
- **API preservata**: Tutte le factory methods e comportamenti esistenti mantenuti
- **Strategie aggiornate**: Tutti i render strategies e prepared statement strategies aggiornati per usare accessor record

### Note Tecniche

- Criteri di conversione: ≤3 campi, nessuna ereditarietà, immutabilità
- Pattern applicato: Conversione a record + aggiornamento sistematico client code
- Fix speciali: MySQL DateArithmetic strategy modificata per costruzione diretta INTERVAL string
- Validazione: Compilazione e test completi dopo ogni conversione
- **Correzione post-migrazione**: UnaryString e DateArithmetic convertiti da record a classi finali tradizionali per forzare l'uso esclusivo dei factory methods

### Classi Convertite a Classi Finali Tradizionali

Dopo la migrazione iniziale, è stato deciso di convertire **UnaryString** e **DateArithmetic** da record a classi finali tradizionali perché:

- **Problema**: I record espongono costruttori canonici pubblici che permettono istanziazione diretta
- **Requisito**: Queste classi dovrebbero essere istanziate solo attraverso factory methods controllati
- **Soluzione**: Classi finali con costruttori privati + factory methods pubblici
- **Vantaggio**: Controllo completo sull'istanziazione mantenendo immutabilità e API identica

// TODO: evaluate if the record is the correct solution: the creation should go only throght factory methods. see DateArithmetic.java

