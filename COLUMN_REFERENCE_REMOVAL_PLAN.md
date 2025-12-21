# Piano di Rimozione: Parsing Automatico "table.column"

## Obiettivo Generale

Rimuovere completamente il supporto per il parsing automatico delle stringhe "table.column" nella codebase, forzando l'uso esclusivo dell'approccio strutturato con parametri espliciti.

**Approccio Target**: Tutti i metodi devono richiedere parametri espliciti senza parsing automatico di dot notation.

**Priorità di Esecuzione**:
1. **WHERE/HAVING**: Abilitare riferimenti cross-table espliciti con overload `column(alias, column)` (prima attività)
2. **JOIN/MERGE**: Sostituire parsing con firma a 4 parametri espliciti (nessun overload da mantenere)
3. **GROUP BY**: Validare contro dot notation
4. **Window Functions**: Validare contro dot notation, rimuovere parsing interno duplicato
5. **ColumnReferenceUtil**: Deprecare completamente la classe utility

## Branch Git

**Nome branch**: `allign-column-handling`

**Commit strategy**: Un commit per ogni sottosezione della Fase 1, test inclusi

---

## Fase 1: Preparazione e Deprecazione

### Obiettivo
Deprecare i metodi che usano parsing automatico, introdurre alternative strutturate, mantenere backward compatibility temporanea.

### 1.1 WhereBuilder e HavingBuilder - Supporto Cross-Table Esplicito (PRIORITÀ ALTA)

**File**: 
- `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/clause/WhereBuilder.java`
- `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/clause/WhereConditionBuilder.java`
- `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/clause/HavingBuilder.java`
- `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/clause/HavingConditionBuilder.java`

**Stato Attuale**:
\`\`\`java
// WhereBuilder
public <R> WhereConditionBuilder<T> column(String column) {
    return new WhereConditionBuilder<>(parent, column, combinator);
}

// HavingBuilder
public HavingConditionBuilder column(String column) {
    return new HavingConditionBuilder(parent, column, combinator);
}
\`\`\`

**Problema**: 
- Non supportano riferimenti a colonne di altre tabelle in contesti multi-table (JOIN)
- Il `ColumnReference` viene costruito usando solo il table reference del parent builder
- Non è possibile specificare condizioni su alias diversi

**Azione**:
1. Aggiungere overload espliciti `column(String alias, String column)` a WhereBuilder e HavingBuilder
2. Modificare i ConditionBuilder per accettare `ColumnReference` espliciti
3. Mantenere i metodi esistenti `column(String)` per compatibilità con single-table context
4. Validare che l'alias sia non vuoto quando fornito
5. Validare contro dot notation in entrambe le firme

**Nuovo Comportamento**:

\`\`\`java
// WhereBuilder - nuovo overload per multi-table
public <R> WhereConditionBuilder<T> column(String alias, String column) {
    if (alias == null || alias.trim().isEmpty()) {
        throw new IllegalArgumentException("Alias cannot be null or empty");
    }
    if (alias.contains(".")) {
        throw new IllegalArgumentException("Alias must not contain dot: '" + alias + "'");
    }
    if (column == null || column.trim().isEmpty()) {
        throw new IllegalArgumentException("Column name cannot be null or empty");
    }
    if (column.contains(".")) {
        throw new IllegalArgumentException(
            "Column name must not contain dot. Use column(alias, column) with separate parameters"
        );
    }
    ColumnReference colRef = ColumnReference.of(alias, column);
    return new WhereConditionBuilder<>(parent, colRef, combinator);
}

// Mantenere per single-table context
public <R> WhereConditionBuilder<T> column(String column) {
    if (column == null || column.trim().isEmpty()) {
        throw new IllegalArgumentException("Column name cannot be null or empty");
    }
    if (column.contains(".")) {
        throw new IllegalArgumentException(
            "Dot notation not supported. Use column(alias, column) for qualified references"
        );
    }
    // Usa table reference del parent (single-table context)
    return new WhereConditionBuilder<>(parent, column, combinator);
}

// HavingBuilder - stesso pattern
public HavingConditionBuilder column(String alias, String column) {
    if (alias == null || alias.trim().isEmpty()) {
        throw new IllegalArgumentException("Alias cannot be null or empty");
    }
    if (alias.contains(".")) {
        throw new IllegalArgumentException("Alias must not contain dot: '" + alias + "'");
    }
    if (column == null || column.trim().isEmpty()) {
        throw new IllegalArgumentException("Column name cannot be null or empty");
    }
    if (column.contains(".")) {
        throw new IllegalArgumentException(
            "Column name must not contain dot. Use column(alias, column) with separate parameters"
        );
    }
    ColumnReference colRef = ColumnReference.of(alias, column);
    return new HavingConditionBuilder(parent, colRef, combinator);
}

public HavingConditionBuilder column(String column) {
    if (column == null || column.trim().isEmpty()) {
        throw new IllegalArgumentException("Column name cannot be null or empty");
    }
    if (column.contains(".")) {
        throw new IllegalArgumentException(
            "Dot notation not supported. Use column(alias, column) for qualified references"
        );
    }
    return new HavingConditionBuilder(parent, column, combinator);
}
\`\`\`

**Modifiche ai ConditionBuilder**:

\`\`\`java
// WhereConditionBuilder - aggiungere costruttore con ColumnReference
public WhereConditionBuilder(T parent, ColumnReference columnRef, LogicalCombinator combinator) {
    this.parent = parent;
    this.columnRef = columnRef;
    this.combinator = combinator;
}

// Mantenere costruttore esistente
public WhereConditionBuilder(T parent, String column, LogicalCombinator combinator) {
    this.parent = parent;
    this.column = column;
    this.combinator = combinator;
}

// Metodo helper per risolvere ColumnReference
private ColumnReference getColumnRef() {
    if (columnRef != null) {
        return columnRef;  // Usa quello esplicito
    }
    return ColumnReference.of(parent.getTableReference(), column);
}
\`\`\`

**Test da Aggiungere**:
- `WhereBuilderTest.java`: test per `where().column("u", "age").gt(18)` in contesto JOIN
- `HavingBuilderTest.java`: test per `having().column("o", "total").gt(100)` in contesto JOIN
- Test di validazione per alias vuoti, colonne con dot, alias con dot

**Documentazione da Aggiornare**:
- `DSL_USAGE_GUIDE.md`: aggiungere sezione "WHERE/HAVING with Multiple Tables"
- `README.md`: aggiornare esempi di query multi-table

**Rimozione (Fase 3)**:
- Nessuna rimozione (i metodi esistenti restano per single-table context)

---

### 1.2 JoinSpecBuilder.on() - Firma Esplicita a 4 Parametri

**File**: `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/clause/JoinSpecBuilder.java`

**Stato Attuale**:
\`\`\`java
public SelectBuilder on(String leftColumn, String rightColumn) {
    ColumnReference leftColRef = ColumnReferenceUtil.parseColumnReference(leftColumn, "");
    ColumnReference rightColRef = ColumnReferenceUtil.parseColumnReference(rightColumn, "");
    // ...
}
\`\`\`

**Problema**: 
- Parsing automatico di "alias.column" crea ambiguità
- Non validazione dell'esistenza degli alias
- Approccio non coerente con WHERE/HAVING

**Azione**:
1. Sostituire completamente con `on(String leftAlias, String leftColumn, String rightAlias, String rightColumn)`
2. Deprecare il metodo esistente `on(String, String)`
3. Validare alias e colonne non vuoti e senza dot

**Nuovo Comportamento**:

\`\`\`java
// Nuovo metodo a 4 parametri (unica firma supportata)
public SelectBuilder on(String leftAlias, String leftColumn, String rightAlias, String rightColumn) {
    validateJoinColumn(leftAlias, leftColumn, "left");
    validateJoinColumn(rightAlias, rightColumn, "right");
    
    ColumnReference leftColRef = ColumnReference.of(leftAlias, leftColumn);
    ColumnReference rightColRef = ColumnReference.of(rightAlias, rightColumn);
    
    return parent.addJoinCondition(leftColRef, rightColRef);
}

private void validateJoinColumn(String alias, String column, String side) {
    if (alias == null || alias.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "JOIN ON " + side + " alias cannot be null or empty"
        );
    }
    if (alias.contains(".")) {
        throw new IllegalArgumentException(
            "JOIN ON " + side + " alias must not contain dot: '" + alias + "'"
        );
    }
    if (column == null || column.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "JOIN ON " + side + " column cannot be null or empty"
        );
    }
    if (column.contains(".")) {
        throw new IllegalArgumentException(
            "JOIN ON " + side + " column must not contain dot: '" + column + "'"
        );
    }
}

// Deprecato
@Deprecated(forRemoval = true, since = "2.0.0")
public SelectBuilder on(String leftColumn, String rightColumn) {
    throw new UnsupportedOperationException(
        "on(String, String) is deprecated. " +
        "Use on(leftAlias, leftColumn, rightAlias, rightColumn) with explicit 4 parameters. " +
        "Example: .on(\"o\", \"customer_id\", \"c\", \"id\")"
    );
}
\`\`\`

**Esempio Utilizzo**:
\`\`\`java
// PRIMA
.on("o.customer_id", "c.id")  // Parsing automatico

// DOPO
.on("o", "customer_id", "c", "id")  // Esplicito, validato
\`\`\`

**Test da Modificare**:
- `JoinSpecBuilderTest.java`: aggiornare tutti i test per usare la firma a 4 parametri
- Aggiungere test di validazione per alias/colonne vuoti o con dot
- Test per self-join con stesso alias

**Documentazione da Aggiornare**:
- `DSL_USAGE_GUIDE.md`: aggiornare tutti gli esempi di JOIN con sintassi a 4 parametri
- `README.md`: esempio principale con la nuova sintassi

**Rimozione (Fase 3)**:
- Rimuovere `on(String, String)` completamente
- Rimuovere import di `ColumnReferenceUtil`

---

### 1.3 MergeBuilder - Firma Esplicita a 4 Parametri per on() e set()

**File**: `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/merge/MergeBuilder.java`

**Stato Attuale (on)**:
\`\`\`java
public MergeBuilder on(String sourceColumn, String targetColumn) {
    ColumnReference sourceColRef = ColumnReferenceUtil.parseColumnReference(sourceColumn, "");
    ColumnReference targetColRef = ColumnReferenceUtil.parseColumnReference(targetColumn, "");
    // ...
}
\`\`\`

**Stato Attuale (set)**:
\`\`\`java
public WhenMatchedUpdateBuilder set(String column, String value) {
    // Ambiguo: value è literal o column reference?
}
\`\`\`

**Problema**: 
- Parsing automatico in `on()` crea ambiguità
- `set(String, String)` non può distinguere tra literal e column reference
- Non validazione dell'esistenza degli alias

**Azione**:
1. Sostituire `on(String, String)` con `on(String targetAlias, String targetColumn, String sourceAlias, String sourceColumn)`
2. Sostituire `set(String, String)` con `set(String column, ColumnReference value)` per riferimenti a colonne
3. Deprecare i metodi esistenti
4. Validare alias e colonne non vuoti e senza dot

**Nuovo Comportamento (on)**:

\`\`\`java
// Nuovo metodo a 4 parametri (unica firma supportata)
public MergeBuilder on(String targetAlias, String targetColumn, 
                       String sourceAlias, String sourceColumn) {
    validateMergeColumn(targetAlias, targetColumn, "target");
    validateMergeColumn(sourceAlias, sourceColumn, "source");
    
    ColumnReference targetColRef = ColumnReference.of(targetAlias, targetColumn);
    ColumnReference sourceColRef = ColumnReference.of(sourceAlias, sourceColumn);
    
    return this.addOnCondition(targetColRef, sourceColRef);
}

private void validateMergeColumn(String alias, String column, String side) {
    if (alias == null || alias.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "MERGE ON " + side + " alias cannot be null or empty"
        );
    }
    if (alias.contains(".")) {
        throw new IllegalArgumentException(
            "MERGE ON " + side + " alias must not contain dot: '" + alias + "'"
        );
    }
    if (column == null || column.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "MERGE ON " + side + " column cannot be null or empty"
        );
    }
    if (column.contains(".")) {
        throw new IllegalArgumentException(
            "MERGE ON " + side + " column must not contain dot: '" + column + "'"
        );
    }
}

// Deprecato
@Deprecated(forRemoval = true, since = "2.0.0")
public MergeBuilder on(String sourceColumn, String targetColumn) {
    throw new UnsupportedOperationException(
        "on(String, String) is deprecated. " +
        "Use on(targetAlias, targetColumn, sourceAlias, sourceColumn) with explicit 4 parameters. " +
        "Example: .on(\"target\", \"id\", \"source\", \"id\")"
    );
}
\`\`\`

**Nuovo Comportamento (set)**:

\`\`\`java
// Per literal values (già esistente - mantiene firma)
public WhenMatchedUpdateBuilder set(String column, Object value) {
    // Literal value
}

// Nuovo metodo per column references
public WhenMatchedUpdateBuilder set(String column, ColumnReference valueColumn) {
    if (column == null || column.trim().isEmpty()) {
        throw new IllegalArgumentException("SET column cannot be null or empty");
    }
    if (column.contains(".")) {
        throw new IllegalArgumentException(
            "SET column must not contain dot: '" + column + "'"
        );
    }
    return this.addSetClause(column, valueColumn);
}

// Deprecato
@Deprecated(forRemoval = true, since = "2.0.0")
public WhenMatchedUpdateBuilder set(String column, String value) {
    throw new UnsupportedOperationException(
        "set(String, String) is ambiguous (literal vs column). " +
        "Use set(column, Object) for literals or set(column, ColumnReference) for column references. " +
        "Example: .set(\"price\", ColumnReference.of(\"source\", \"new_price\"))"
    );
}
\`\`\`

**Esempio Utilizzo**:
\`\`\`java
// PRIMA
.on("source.id", "target.id")  // Parsing automatico
.set("price", "source.new_price")  // Ambiguo!

// DOPO
.on("target", "id", "source", "id")  // Esplicito, validato
.set("price", ColumnReference.of("source", "new_price"))  // Disambiguato
// oppure per literal:
.set("price", 100.0)
\`\`\`

**Test da Modificare**:
- `MergeBuilderTest.java`: aggiornare tutti i test per usare firma a 4 parametri
- Aggiungere test di validazione per alias/colonne vuoti o con dot
- Test per `set()` con ColumnReference vs Object literal
- Test per MERGE con self-reference (stesso alias)

**Documentazione da Aggiornare**:
- `DSL_USAGE_GUIDE.md`: aggiornare tutti gli esempi di MERGE con sintassi a 4 parametri e set() disambiguato
- `README.md`: esempio principale con la nuova sintassi

**Rimozione (Fase 3)**:
- Rimuovere `on(String, String)` completamente
- Rimuovere `set(String, String)` completamente
- Rimuovere import di `ColumnReferenceUtil`

---

### 1.4 SelectBuilder.groupBy() - Validazione Against Dot Notation

**File**: `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/select/SelectBuilder.java`

**Stato Attuale**:
\`\`\`java
public SelectBuilder groupBy(String... columns) {
    ColumnReference[] groupingColumns = Arrays.stream(columns)
        .map(column -> ColumnReferenceUtil.parseColumnReference(column, getTableReference()))
        .toArray(ColumnReference[]::new);
    // ...
}
\`\`\`

**Problema**:
- GROUP BY opera solo sulla tabella principale (FROM clause)
- Il parsing automatico di "table.column" è fuorviante - non c'è ambiguità semantica
- Basta validare che l'utente non usi dot notation

**Azione**:
1. Rimuovere uso di `ColumnReferenceUtil.parseColumnReference()`
2. Validare che i column names NON contengano dot
3. Usare solo `getTableReference()` per qualificare le colonne

**Nuovo Comportamento**:
\`\`\`java
public SelectBuilder groupBy(String... columns) {
    ColumnReference[] groupingColumns = Arrays.stream(columns)
        .map(column -> {
            if (column == null || column.trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "GROUP BY column cannot be null or empty"
                );
            }
            if (column.contains(".")) {
                throw new IllegalArgumentException(
                    "GROUP BY column must be simple name (no table qualifier): '" + column + "'. " +
                    "Columns are automatically qualified with the main table."
                );
            }
            return ColumnReference.of(getTableReference(), column);
        })
        .toArray(ColumnReference[]::new);
    
    return this.addGroupByColumns(groupingColumns);
}
\`\`\`

**Esempio Utilizzo**:
\`\`\`java
// PRIMA
.groupBy("orders.customer_id", "orders.status")  // Parsing automatico

// DOPO
.groupBy("customer_id", "status")  // Validato, no dot allowed
\`\`\`

**Test da Modificare**:
- `SelectBuilderGroupByTest.java`: rimuovere tutti i test con "table.column"
- Aggiungere test di validazione per column con dot (deve fallire)
- Test per column null/empty (deve fallire)

**Documentazione da Aggiornare**:
- `DSL_USAGE_GUIDE.md`: aggiornare esempi GROUP BY senza table qualifier
- Aggiungere nota che GROUP BY opera sulla tabella principale

**Rimozione (Fase 3)**:
- Rimuovere import di `ColumnReferenceUtil`

---

### 1.5 WindowFunctionBuilder - Validazione Against Dot Notation

**File**: `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/select/WindowFunctionBuilder.java`

**Stato Attuale**: Ha un metodo interno `parseColumnReference()` duplicato

**Azione**:
1. Rimuovere il metodo interno `parseColumnReference()`
2. Modificare tutti i metodi per usare solo `defaultTableReference`
3. Validare che le colonne non contengano dot

**Metodi da Modificare**:

\`\`\`java
public WindowFunctionBuilder<PARENT> partitionBy(String column) {
    if (column == null || column.trim().isEmpty()) {
        throw new IllegalArgumentException("PARTITION BY column cannot be null or empty");
    }
    if (column.contains(".")) {
        throw new IllegalArgumentException(
            "Qualified column names not supported in partitionBy(). " +
            "Use partitionBy(String table, String column) or simple column name"
        );
    }
    partitionByColumns.add(ColumnReference.of(defaultTableReference, column));
    return this;
}

public WindowFunctionBuilder<PARENT> orderByAsc(String column) {
    if (column == null || column.trim().isEmpty()) {
        throw new IllegalArgumentException("ORDER BY column cannot be null or empty");
    }
    if (column.contains(".")) {
        throw new IllegalArgumentException(
            "Qualified column names not supported in orderByAsc(). " +
            "Use orderByAsc(String table, String column) or simple column name"
        );
    }
    // ...
}

public WindowFunctionBuilder<PARENT> orderByDesc(String column) {
    if (column == null || column.trim().isEmpty()) {
        throw new IllegalArgumentException("ORDER BY column cannot be null or empty");
    }
    if (column.contains(".")) {
        throw new IllegalArgumentException(
            "Qualified column names not supported in orderByDesc(). " +
            "Use orderByDesc(String table, String column) or simple column name"
        );
    }
    // ...
}

// Costruttori LAG/LEAD
WindowFunctionBuilder(/* ... */, String column, int offset) {
    if (column == null || column.trim().isEmpty()) {
        throw new IllegalArgumentException("Column cannot be null or empty");
    }
    if (column.contains(".")) {
        throw new IllegalArgumentException(
            "Qualified column names not supported. Use constructor with table parameter"
        );
    }
    this.valueExpression = ColumnReference.of(defaultTableReference, column);
    // ...
}
\`\`\`

**Test da Modificare**:
- `WindowFunctionsIntegrationTest.java`: rimuovere uso di "table.column"
- Aggiungere test per validazione errori (dot notation non permessa)

**Rimozione (Fase 3)**:
- Rimuovere metodo interno `parseColumnReference()`

---

### 1.6 ColumnReferenceUtil - Deprecazione Completa

**File**: `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/util/ColumnReferenceUtil.java`

**Azione**:
1. Deprecare l'intera classe
2. Documentare che sarà rimossa
3. Modificare implementazione per lanciare UnsupportedOperationException
4. Rimuovere tutti gli import da altre classi

**Nuovo Stato**:
\`\`\`java
/**
 * @deprecated This utility class will be removed in version 3.0.0.
 * Automatic parsing of "table.column" strings is no longer supported.
 * Use explicit ColumnReference.of(table, column) instead.
 */
@Deprecated(forRemoval = true, since = "2.0.0")
public final class ColumnReferenceUtil {
    
    private ColumnReferenceUtil() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    /**
     * @deprecated Use ColumnReference.of(table, column) directly
     */
    @Deprecated(forRemoval = true, since = "2.0.0")
    public static ColumnReference parseColumnReference(String column, String defaultTableReference) {
        throw new UnsupportedOperationException(
            "ColumnReferenceUtil is deprecated. Use ColumnReference.of(table, column) directly"
        );
    }
}
\`\`\`

**Test da Modificare**:
- `ColumnReferenceUtilTest.java`: deprecare o rimuovere completamente

---

## Fase 2: Migrazione Test e Documentazione

### 2.1 Migrazione Test

**Obiettivo**: Aggiornare tutti i test per usare solo approccio strutturato

#### Test da Modificare:

**JoinSpecBuilderTest.java**:
\`\`\`java
// PRIMA
.innerJoin("customers").as("c")
.on("o.customer_id", "c.id")

// DOPO
.innerJoin("customers").as("c")
.on("o", "customer_id", "c", "id")
\`\`\`

**SelectBuilderGroupByTest.java**:
\`\`\`java
// PRIMA
builder.groupBy("orders.customer_id", "orders.status")

// DOPO
builder.from("orders")
       .groupBy("customer_id", "status")
\`\`\`

**WindowFunctionsIntegrationTest.java**:
\`\`\`java
// PRIMA
dsl.select()
    .rowNumber()
        .partitionBy("employees.department")
        .orderByAsc("employees.salary")
        .as("rank")

// DOPO
dsl.select()
    .rowNumber()
        .partitionBy("department")
        .orderByAsc("salary")
        .as("rank")
    .from("employees")
\`\`\`

**MergeBuilderTest.java**:
\`\`\`java
// PRIMA
merge.on("target.id", "source.id")
    .whenMatched()
    .set("name", "source.name")
    .set("age", "source.age")

// DOPO
merge.on("target", "id", "source", "id")
    .whenMatched()
    .set("name", ColumnReference.of("source", "name"))
    .set("age", ColumnReference.of("source", "age"))
\`\`\`

**WhereBuilder/HavingBuilder Test (Nuovi)**:
\`\`\`java
// Test per multi-table WHERE
dsl.select()
    .column("u", "name")
    .column("o", "total")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u", "id", "o", "user_id")
    .where()
        .column("u", "age").gt(18)
        .and()
        .column("o", "status").eq("COMPLETED")
    .build(connection);
\`\`\`

### 2.2 Aggiornamento Documentazione

**File da Aggiornare**:

1. **DSL_USAGE_GUIDE.md**:
   - Rimuovere esempi con "table.column" parsing automatico
   - Aggiungere sezione "Column Reference Best Practices"
   - Documentare nuovi overload WHERE/HAVING per multi-table
   - Aggiornare esempi JOIN con 4 parametri
   - Aggiornare esempi MERGE con 4 parametri e ColumnReference

2. **DEVELOPER_GUIDE.md**:
   - Aggiungere nota sulla rimozione del parsing automatico
   - Documentare le convenzioni per metodi DSL
   - Linee guida per contributi futuri

3. **README.md**:
   - Aggiornare tutti gli esempi di codice
   - Rimuovere riferimenti a dot notation

**Esempio Sezione Documentazione**:
\`\`\`markdown
## Column References

### Single-Table Queries

Per query su una singola tabella, usa nomi di colonna semplici:

\`\`\`java
dsl.select("name", "age")
    .from("users")
    .where().column("age").gt(18)
    .groupBy("country")
    .build(connection);
\`\`\`

### Multi-Table Queries (JOIN)

Per query con JOIN, usa l'overload esplicito per WHERE/HAVING:

\`\`\`java
dsl.select()
    .column("u", "name")
    .column("o", "total")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u", "id", "o", "user_id")  // 4 parametri espliciti
    .where()
        .column("u", "age").gt(18)  // Alias esplicito
        .and()
        .column("o", "status").eq("COMPLETED")
    .build(connection);
\`\`\`

### MERGE Operations

Per MERGE, usa 4 parametri per ON e ColumnReference per SET:

\`\`\`java
dsl.merge()
    .into("target_table").as("target")
    .using("source_table").as("source")
    .on("target", "id", "source", "id")  // 4 parametri espliciti
    .whenMatched()
        .set("name", ColumnReference.of("source", "name"))  // ColumnReference esplicito
        .set("price", ColumnReference.of("source", "new_price"))
    .whenNotMatched()
        .set("id", ColumnReference.of("source", "id"))
        .set("name", ColumnReference.of("source", "name"))
    .build(connection);
\`\`\`

### ❌ Comportamenti NON Supportati

\`\`\`java
// ❌ ERRORE: dot notation non supportata in GROUP BY
.groupBy("users.country")  // IllegalArgumentException

// ❌ ERRORE: dot notation non supportata in WHERE single-table
.where().column("users.age").gt(18)  // IllegalArgumentException

// ❌ ERRORE: set() ambiguo con String value
.whenMatched().set("name", "source.name")  // Deprecato - usare ColumnReference
\`\`\`
\`\`\`

---

## Fase 3: Rimozione Completa

### 3.1 Rimozione Codice Deprecato

**Azioni**:
1. Rimuovere `ColumnReferenceUtil.java` completamente
2. Rimuovere metodi deprecati:
   - `JoinSpecBuilder.on(String, String)`
   - `MergeBuilder.on(String, String)`
   - `MergeBuilder.WhenMatchedUpdateBuilder.set(String, String)`
   - `MergeBuilder.WhenNotMatchedInsertBuilder.set(String, String)`
3. Rimuovere metodi interni di parsing:
   - `WindowFunctionBuilder.parseColumnReference()`
4. Rimuovere import di `ColumnReferenceUtil` da tutte le classi

### 3.2 Validazione Finale

**Checklist**:
- [ ] Nessun import di `ColumnReferenceUtil` nel codice produzione
- [ ] Nessun metodo `parseColumnReference()` custom nel codice
- [ ] Tutti i test passano (unit, component, integration, E2E)
- [ ] Build completa senza warning di deprecazione
- [ ] `./mvnw spotless:check` passa
- [ ] Documentazione aggiornata
- [ ] CHANGELOG aggiornato con breaking changes
- [ ] Migration guide disponibile

### 3.3 Release Notes

\`\`\`markdown
## [2.0.0] - Breaking Changes

### Removed: Automatic "table.column" Parsing

The DSL no longer supports automatic parsing of dot-separated column names.

**Migration Guide**:

#### WHERE/HAVING in Multi-Table Context
\`\`\`java
// Before (not supported)
.where().column("users.age").gt(18)

// After
.where().column("users", "age").gt(18)
\`\`\`

#### JOIN Operations
\`\`\`java
// Before
.on("o.customer_id", "c.id")

// After
.on("o", "customer_id", "c", "id")
\`\`\`

#### GROUP BY
\`\`\`java
// Before
.groupBy("orders.customer_id")

// After
.from("orders")
.groupBy("customer_id")
\`\`\`

#### Window Functions
\`\`\`java
// Before
.partitionBy("employees.department")

// After
.from("employees")
.partitionBy("department")
\`\`\`

#### MERGE Operations
\`\`\`java
// Before
.on("target.id", "source.id")
.whenMatched().set("name", "source.name")

// After
.on("target", "id", "source", "id")
.whenMatched().set("name", ColumnReference.of("source", "name"))
\`\`\`

**Rationale**: Removing implicit parsing eliminates ambiguity (especially in MERGE set operations), improves type safety, and makes the DSL more predictable and explicit.
\`\`\`

### 3.4 CHANGELOG Entry

\`\`\`markdown
## [2.0.0] - 2025-MM-DD

### Added
- `WhereBuilder.column(String alias, String column)` - Support for explicit cross-table references in WHERE clauses
- `HavingBuilder.column(String alias, String column)` - Support for explicit cross-table references in HAVING clauses
- `JoinSpecBuilder.on(String leftAlias, String leftColumn, String rightAlias, String rightColumn)` - Explicit 4-parameter JOIN ON syntax
- `MergeBuilder.on(String targetAlias, String targetColumn, String sourceAlias, String sourceColumn)` - Explicit 4-parameter MERGE ON syntax
- `MergeBuilder.WhenMatchedUpdateBuilder.set(String column, ColumnReference value)` - Explicit column reference in MERGE SET
- Validation for dot notation in column names (throws IllegalArgumentException)

### Changed
- `SelectBuilder.groupBy()` - Now validates against dot notation, requires simple column names only
- `WindowFunctionBuilder` - All methods now validate against dot notation

### Deprecated
- `JoinSpecBuilder.on(String, String)` - Use 4-parameter version instead
- `MergeBuilder.on(String, String)` - Use 4-parameter version instead
- `MergeBuilder.WhenMatchedUpdateBuilder.set(String, String)` - Ambiguous, use set(String, Object) for literals or set(String, ColumnReference) for columns
- `ColumnReferenceUtil` - Entire class deprecated, will be removed in 3.0.0

### Removed (Breaking Changes)
- Automatic parsing of "table.column" strings throughout the DSL
- `ColumnReferenceUtil.parseColumnReference()` method (throws UnsupportedOperationException)

### Migration
See [Migration Guide](docs/MIGRATION_2.0.md) for detailed migration instructions.
\`\`\`

---

## Criteri di Successo

### Fase 1
- [ ] Tutti i metodi con parsing automatico sono deprecati o sostituiti
- [ ] Validazione/errori chiari per input non validi
- [ ] Nuovi overload WHERE/HAVING implementati e testati
- [ ] JOIN/MERGE usano firma a 4 parametri espliciti
- [ ] Test esistenti passano ancora (con warning di deprecazione dove applicabile)
- [ ] Documentazione inline (Javadoc) aggiornata

### Fase 2
- [ ] Tutti i test usano solo approccio strutturato
- [ ] Nessun warning di deprecazione nei test
- [ ] Documentazione utente aggiornata (DSL_USAGE_GUIDE, README)
- [ ] Guide di migrazione disponibili
- [ ] Esempi di codice aggiornati

### Fase 3
- [ ] `ColumnReferenceUtil` rimosso completamente
- [ ] Nessun parsing automatico nel codice
- [ ] Metodi deprecati rimossi
- [ ] Build pulita senza errori o warning
- [ ] `./mvnw spotless:apply` eseguito
- [ ] Tutti i test passano (unit, component, integration, E2E)
- [ ] Release notes complete
- [ ] CHANGELOG aggiornato

---

## Rischi e Mitigazioni

### Rischio 1: Breaking Changes per Utenti Esistenti
**Mitigazione**: 
- Seguire processo di deprecazione in 3 fasi
- Fornire release notes dettagliate
- Creare migration guide con esempi concreti
- Mantenere backward compatibility in versione intermedia (2.0.0 con deprecation warnings)

### Rischio 2: Test che Falliscono
**Mitigazione**:
- Migrare test gradualmente, sezione per sezione
- Eseguire suite completa dopo ogni modifica
- Commit incrementali per facilitare rollback se necessario

### Rischio 3: Complessità API Aumentata
**Mitigazione**:
- Documentazione chiara con molti esempi
- Overload multipli per supportare diversi use case
- Messaggi di errore esplicativi che suggeriscono la sintassi corretta

### Rischio 4: Regressioni Funzionali
**Mitigazione**:
- Mantenere test coverage elevata (>80%)
- Eseguire integration ed E2E tests dopo ogni modifica
- Code review accurata per ogni fase

---

## Timeline Stimata

- **Fase 1.1 (WHERE/HAVING)**: 1 giorno (implementazione + test)
- **Fase 1.2 (JOIN)**: 1 giorno (implementazione + test)
- **Fase 1.3 (MERGE)**: 1 giorno (implementazione + test)
- **Fase 1.4 (GROUP BY)**: 0.5 giorni (implementazione + test)
- **Fase 1.5 (Window Functions)**: 0.5 giorni (implementazione + test)
- **Fase 1.6 (ColumnReferenceUtil deprecation)**: 0.5 giorni
- **Fase 2 (Migrazione test + documentazione)**: 2 giorni
- **Fase 3 (Rimozione finale + validazione)**: 1 giorno

**Totale**: ~7-8 giorni di lavoro

---

## Note Implementative

### Ordine di Implementazione

1. **WHERE/HAVING** (priorità assoluta): Richiesto per supportare query multi-table corrette
2. **JOIN**: Seconda priorità, uso frequente
3. **MERGE**: Terza priorità, ambiguità critica da risolvere
4. **GROUP BY**: Validazione semplice, bassa priorità
5. **Window Functions**: Validazione semplice, bassa priorità
6. **ColumnReferenceUtil**: Deprecazione finale

### Commit Strategy

Ogni sottosezione della Fase 1 deve essere un commit separato:

\`\`\`bash
git commit -m "feat(dsl): add WHERE/HAVING cross-table support with explicit alias

- Add WhereBuilder.column(alias, column) overload
- Add HavingBuilder.column(alias, column) overload
- Add validation for dot notation in column names
- Add tests for multi-table WHERE/HAVING conditions

BREAKING CHANGE: Dot notation in WHERE/HAVING single-table context now throws IllegalArgumentException"

git commit -m "feat(dsl): replace JOIN on() with 4-parameter explicit signature

- Replace JoinSpecBuilder.on(String, String) with on(leftAlias, leftColumn, rightAlias, rightColumn)
- Deprecate old on(String, String) method
- Add validation for alias/column parameters
- Update all JOIN tests to use new signature

BREAKING CHANGE: JoinSpecBuilder.on(String, String) is deprecated, use 4-parameter version"

# ...e così via per ogni sezione
\`\`\`

### Test Coverage Target

- **Unit tests**: >90% coverage
- **Component tests**: Copertura di tutti i builder con nuove firme
- **Integration tests**: Test end-to-end con H2 per WHERE/HAVING/JOIN multi-table
- **E2E tests**: Test con database reali (MySQL, PostgreSQL) per MERGE e scenari complessi

---

## Appendice: Esempi Completi

### Esempio 1: Query Multi-Table con WHERE Esplicito

\`\`\`java
// Scenario: Trovare utenti con ordini completati sopra un certo importo

// PRIMA (non supportato correttamente)
dsl.select("u.name", "o.total")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u.id", "o.user_id")
    .where().column("o.status").eq("COMPLETED")  // Problema: column si riferisce solo a "users"
    .and().column("o.total").gt(100.0)
    .build(connection);

// DOPO (con cross-table support)
dsl.select()
    .column("u", "name")
    .column("o", "total")
    .from("users").as("u")
    .innerJoin("orders").as("o")
    .on("u", "id", "o", "user_id")
    .where()
        .column("o", "status").eq("COMPLETED")  // Esplicito: alias "o"
        .and()
        .column("o", "total").gt(100.0)
    .build(connection);
\`\`\`

### Esempio 2: MERGE Completo

\`\`\`java
// Scenario: Merge di dati da staging a produzione

// PRIMA (ambiguo)
dsl.merge()
    .into("products").as("target")
    .using("staging_products").as("source")
    .on("target.product_id", "source.product_id")
    .whenMatched()
        .set("name", "source.name")  // Ambiguo: literal o column?
        .set("price", "source.price")
        .set("updated_at", "NOW()")  // Ancora più ambiguo!
    .whenNotMatched()
        .set("product_id", "source.product_id")
        .set("name", "source.name")
        .set("price", "source.price")
    .build(connection);

// DOPO (esplicito)
dsl.merge()
    .into("products").as("target")
    .using("staging_products").as("source")
    .on("target", "product_id", "source", "product_id")
    .whenMatched()
        .set("name", ColumnReference.of("source", "name"))
        .set("price", ColumnReference.of("source", "price"))
        .set("updated_at", ScalarFunction.of("NOW"))  // Funzione SQL
    .whenNotMatched()
        .set("product_id", ColumnReference.of("source", "product_id"))
        .set("name", ColumnReference.of("source", "name"))
        .set("price", ColumnReference.of("source", "price"))
    .build(connection);
\`\`\`

### Esempio 3: GROUP BY con Aggregazioni

\`\`\`java
// Scenario: Raggruppare ordini per customer e status

// PRIMA (con parsing automatico)
dsl.select("customer_id", "status")
    .count("order_id").as("total_orders")
    .from("orders")
    .groupBy("orders.customer_id", "orders.status")  // Parsing automatico
    .build(connection);

// DOPO (senza parsing)
dsl.select("customer_id", "status")
    .count("order_id").as("total_orders")
    .from("orders")
    .groupBy("customer_id", "status")  // Semplice, validato
    .build(connection);
\`\`\`

### Esempio 4: Window Functions

\`\`\`java
// Scenario: Ranking dipendenti per dipartimento

// PRIMA (con parsing automatico)
dsl.select()
    .column("employee_id")
    .column("department")
    .column("salary")
    .rowNumber()
        .partitionBy("employees.department")  // Parsing automatico
        .orderByDesc("employees.salary")
        .as("rank")
    .from("employees")
    .build(connection);

// DOPO (validato)
dsl.select()
    .column("employee_id")
    .column("department")
    .column("salary")
    .rowNumber()
        .partitionBy("department")  // Semplice, validato
        .orderByDesc("salary")
        .as("rank")
    .from("employees")
    .build(connection);
\`\`\`

---

## Fine del Piano