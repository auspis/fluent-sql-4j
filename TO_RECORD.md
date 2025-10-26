# TO_RECORD.md

## Classi candidate per conversione a record

### CharacterLength

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/string/CharacterLength.java`
- Campi: 1 (expression: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor, @Getter
- Note: Implementa FunctionCall interface, costruttore pubblico

### Cast

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/convert/Cast.java`
- Campi: 2 (expression: ScalarExpression, dataType: String)
- Annotazioni Lombok: @AllArgsConstructor(access = AccessLevel.PRIVATE), @Getter
- Note: Implementa FunctionCall interface, costruttore privato con metodo factory statico

### Substring

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/string/Substring.java`
- Campi: 3 (expression: ScalarExpression, startPosition: ScalarExpression, length: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor(access = AccessLevel.PRIVATE), @Getter
- Note: Implementa FunctionCall interface, costruttore privato con metodi factory statici

### UnaryString

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/string/UnaryString.java`
- Campi: 2 (functionName: String, expression: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor(access = AccessLevel.PRIVATE), @Getter
- Note: Implementa FunctionCall interface, costruttore privato con metodi factory statici

### Replace

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/string/Replace.java`
- Campi: 3 (expression: ScalarExpression, oldSubstring: ScalarExpression, newSubstring: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor(access = AccessLevel.PRIVATE), @Getter
- Note: Implementa FunctionCall interface, costruttore privato con metodo factory statico

### Trim

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/string/Trim.java`
- Campi: 3 (stringExpression: ScalarExpression, mode: TrimMode, charactersToRemove: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor(access = AccessLevel.PRIVATE), @Getter
- Note: Implementa FunctionCall interface, costruttore privato con metodi factory statici

### ExtractDatePart

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/datetime/ExtractDatePart.java`
- Campi: 2 (functionName: String, dateExpression: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor(access = AccessLevel.PRIVATE), @Getter
- Note: Implementa FunctionCall interface, costruttore privato con metodi factory statici

### DataLength

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/string/DataLength.java`
- Campi: 1 (expression: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor, @Getter
- Note: Implementa FunctionCall interface, costruttore pubblico

### DateArithmetic

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/datetime/DateArithmetic.java`
- Campi: 3 (add: boolean, dateExpression: ScalarExpression, interval: Interval)
- Annotazioni Lombok: @AllArgsConstructor(access = AccessLevel.PRIVATE), @Getter
- Note: Implementa FunctionCall interface, costruttore privato con metodi factory statici

### Concat

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/string/Concat.java`
- Campi: 2 (separator: String, stringExpressions: List<ScalarExpression>)
- Annotazioni Lombok: @AllArgsConstructor(access = AccessLevel.PRIVATE), @Getter
- Note: Implementa FunctionCall interface, costruttore privato con metodi factory statici

### Left

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/string/Left.java`
- Campi: 2 (expression: ScalarExpression, length: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor(access = AccessLevel.PRIVATE), @Getter
- Note: Implementa FunctionCall interface, costruttore privato con metodi factory statici

### CharLength

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/string/CharLength.java`
- Campi: 1 (expression: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor, @Getter
- Note: Implementa FunctionCall interface, costruttore pubblico

### UnaryNumeric

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/number/UnaryNumeric.java`
- Campi: 2 (functionName: String, numericExpression: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor(access = AccessLevel.PRIVATE), @Getter
- Note: Implementa FunctionCall interface, costruttore privato con metodi factory statici

### Mod

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/number/Mod.java`
- Campi: 2 (dividend: ScalarExpression, divisor: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor, @Getter
- Note: Implementa FunctionCall interface, costruttore pubblico con metodi factory statici

### Round

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/number/Round.java`
- Campi: 2 (numericExpression: ScalarExpression, decimalPlaces: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor, @Getter
- Note: Implementa FunctionCall interface, costruttore pubblico con metodi factory statici

### Interval

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/datetime/interval/Interval.java`
- Campi: 2 (value: ScalarExpression, unit: IntervalUnit)
- Annotazioni Lombok: @AllArgsConstructor(access = AccessLevel.PRIVATE), @Getter
- Note: Implementa ScalarExpression interface, costruttore privato

### Length

- Percorso: `sql/src/main/java/lan/tlab/r4j/sql/ast/expression/scalar/call/function/string/Length.java`
- Campi: 1 (expression: ScalarExpression)
- Annotazioni Lombok: @AllArgsConstructor, @Getter
- Note: Implementa FunctionCall interface, costruttore pubblico

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

