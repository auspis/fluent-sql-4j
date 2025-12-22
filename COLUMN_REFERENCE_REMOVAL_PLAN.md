# Piano di Rimozione: Parsing Automatico "table.column"

## Obiettivo Generale

Rimuovere completamente il supporto per il parsing automatico delle stringhe "table.column" nella codebase, forzando l'uso esclusivo dell'approccio strutturato con parametri espliciti.

**Approccio Target**: Tutti i metodi devono richiedere parametri espliciti senza parsing automatico di dot notation.

**Priorità di Esecuzione**:
1. **WHERE/HAVING**: Abilitare riferimenti cross-table espliciti con overload `column(alias, column)` (prima attività)
2. **JOIN/MERGE**: Sostituire parsing con firma a 4 parametri espliciti (nessun overload da mantenere)
3. **GROUP BY**: Validare contro dot notation
4. **ORDER BY**: Supportare alias espliciti in `SelectBuilder.orderBy/orderByDesc`
5. **Window Functions**: Validare contro dot notation, rimuovere parsing interno duplicato
6. **ColumnReferenceUtil**: Deprecare completamente la classe utility

## Branch Git

**Nome branch**: `allign-column-handling`

**Commit strategy**: Un commit per ogni sottosezione della Fase 1, test inclusi

---

## Fase 1: Preparazione e Deprecazione

### Obiettivo
Deprecare i metodi che usano parsing automatico, introdurre alternative strutturate, mantenere backward compatibility temporanea.

### 1.1 WhereBuilder e HavingBuilder - Supporto Cross-Table Esplicito ✅ COMPLETATO

**File**: 
- `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/clause/WhereBuilder.java`
- `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/clause/WhereConditionBuilder.java`
- `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/clause/HavingBuilder.java`
- `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/clause/HavingConditionBuilder.java`

**Stato**: ✅ Implementato e testato completamente

**Stato Precedente**:
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

#### ✅ Stato Implementazione Fase 1.1 (Completata)

**Implementato**:
1. ✅ Overload `WhereBuilder.column(String alias, String column)` con validazione completa
2. ✅ Overload `HavingBuilder.column(String alias, String column)` con validazione completa
3. ✅ Costruttori `WhereConditionBuilder` e `HavingConditionBuilder` con supporto `ColumnReference` esplicito
4. ✅ Validazione per alias/colonne null, empty, e dot notation
5. ✅ **Feature Aggiuntiva**: Column-to-Column Comparison Support
   - Metodi zero-argument `eq()`, `ne()`, `gt()`, `lt()`, `gte()`, `lte()` che ritornano `ColumnComparator`
   - Inner class `ColumnComparator` per comparazioni fluent: `.where().column("u", "age").gt().column("e", "age")`
   - Supporto sia per WHERE che HAVING clauses
   - Riuso di `Comparison.ComparisonOperator` (eliminata duplicazione con enum custom)

**Test Aggiunti**:
- ✅ WhereConditionBuilderTest: 20 test base cross-table + 12 test column-to-column (totale 32 nuovi test)
- ✅ HavingConditionBuilderTest: 21 test base cross-table + 16 test column-to-column (totale 37 nuovi test)
- ✅ Coverage completa: tutti gli operatori (eq, ne, gt, lt, gte, lte), validazione, AND/OR chaining

**Documentazione Aggiornata**:
- ✅ DSL_USAGE_GUIDE.md: esempi cross-table integrati nelle sezioni WHERE e HAVING
- ✅ README.md: esempio principale con multi-table JOIN aggiornato

**Build Status**:
- ✅ Tutti i 1341 test passano (1313 esistenti + 28 nuovi)
- ✅ Build SUCCESS
- ✅ Codice formattato con spotless

**Refactoring Aggiuntivo**:
- ✅ Eliminata duplicazione `WhereConditionBuilder.ComparisonOperator` enum
- ✅ Riuso completo di `Comparison.ComparisonOperator` in tutti i builder

---

### 1.2 JoinSpecBuilder.on() - Firma Esplicita a 4 Parametri ✅ COMPLETATO

**File**: `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/clause/JoinSpecBuilder.java`

**Stato**: completato.

**Implementato**:
- Sostituita la firma con `on(String leftAlias, String leftColumn, String rightAlias, String rightColumn)` con validazione (alias/colonne non vuoti, niente dot).
- Rimosso parsing dot-notation e deprecato il vecchio `on(String, String)`.
- Fix aggiuntivo in `SelectBuilder`: le proiezioni/aggregati già qualificati (es. `o.total`) non vengono più riscritti quando si applicano alias, evitando regressioni nei JOIN con SUM.

**Test**:
- `JoinSpecBuilderTest` aggiornato; nuovo test regressione su aggregate qualificato in `SelectBuilderJoinTest`.
- Suite `SelectBuilderJoinTest` verde (18 test).

**Documentazione**:
- Esempi JOIN da aggiornare in `DSL_USAGE_GUIDE.md` e `README.md` (TODO Fase 2).

**Rimozione (Fase 3)**:
- Eliminare definitivamente `on(String, String)` e import di `ColumnReferenceUtil` (quando si passerà alla rimozione totale).

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

### 1.5 SelectBuilder.orderBy() - Supporto Alias Esplicito

**Obiettivo**: estendere `orderBy`/`orderByDesc` per accettare alias espliciti, allineando il comportamento a WHERE/HAVING e JOIN.

**Azione**:
- Aggiungere overload `orderBy(String alias, String column)` e `orderByDesc(String alias, String column)` con validazione (alias/colonna non vuoti, senza dot).
- Mantenere le versioni a singolo parametro per single-table, ma senza parsing della dot notation.
- Aggiornare i test `SelectBuilderJoinTest`/`SelectBuilderOrderByTest` (o equivalenti) e l’esempio ORDER BY in `DSL_USAGE_GUIDE.md`.

**Rimozione (Fase 3)**:
- Nessuna API da rimuovere, solo evitare parsing implicito; documentare la preferenza per gli overload con alias in contesti multi-table.

### 1.6 WindowFunctionBuilder - Validazione Against Dot Notation

**Stato**: ✅ Completato

**File**: `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/select/WindowFunctionBuilder.java`

**Azione Implementata**:
1. Rimosso il parsing implicito `parseColumnReference()`
2. Validazione per colonne/alias null o vuoti e contro la dot notation
3. Guidance esplicita sugli overload con alias (`partitionBy(table, column)`, `orderByAsc/Desc(table, column)`, `lag/lead(table, column, offset)`)

**Test Aggiornati**:
- `SelectDSLComponentTest`: aggiunti test di validazione per dot notation in `partitionBy()` e `orderByAsc()`

**Note**:
- Le chiamate senza alias usano il `defaultTableReference` senza parsing; le versioni con alias convalidano alias/colonne senza dot

---

### 1.7 ColumnReferenceUtil - Deprecazione Completa

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
### 1.3 MergeBuilder - Firma Esplicita a 4 Parametri per on() e set() ✅ COMPLETATO

**File**: `jdsql-core/src/main/java/lan/tlab/r4j/jdsql/dsl/merge/MergeBuilder.java`

**Stato**: completato.

**Implementato**:
- `on(targetAlias, targetColumn, sourceAlias, sourceColumn)` con validazione, rimozione parsing dot-notation; metodi a 2 parametri deprecati.
- `set(String, ColumnReference)` introdotto per riferimenti a colonne; `set(String, String)` deprecato; `set(String, Object)` resta per i literal.
- Allineate le catene WhenMatched/WhenNotMatched.

**Test**:
- `MergeBuilderTest`, `MergeBuilderWhenSequencingTest` e `StandardSQLDialectPluginE2E` aggiornati alle nuove firme; suite verdi.

**Documentazione**:
- Esempi MERGE da aggiornare in `DSL_USAGE_GUIDE.md` e `README.md` (TODO Fase 2).

**Rimozione (Fase 3)**:
- Eliminare definitivamente `on(String, String)` e `set(String, String)` e gli import di `ColumnReferenceUtil` residui.
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
// PRIMA (ambiguo)
dsl.merge()
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

// DOPO (esplicito)
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
---

## Riepilogo Stato Fase 1

### ✅ Completate
- **1.1**: WHERE/HAVING - Supporto Cross-Table Esplicito + Column-to-Column Comparison
- **1.2**: JoinSpecBuilder.on() - Firma Esplicita a 4 Parametri
- **1.3**: MergeBuilder - Firma Esplicita a 4 Parametri per on() e set()
- **1.4**: SelectBuilder.groupBy() - Fluent Builder con Supporto Alias
- **1.5**: SelectBuilder.orderBy() - Fluent Builder Pattern (OrderByBuilder) con Supporto Alias
- **1.6**: WindowFunctionBuilder - Validazione Against Dot Notation

### 🔄 In Progress
- **1.7**: ColumnReferenceUtil - Deprecazione Completa

---

## Note Implementazione Fase 1.5 (OrderByBuilder)

### Decisione API Design
- **Pattern Scelto**: Option A (direction-first) con delega implicita: `.orderBy().asc("col").desc("col").fetch(10).build(connection)` senza doppio `.build()` intermedio
- **Motivazione**: Preserva l'ordine semantico SQL, chiaro e flessibile, ed evita `.build().build(connection)` poco fluente
- **Consistenza**: Allineato con GroupByBuilder e Where/Having condition builders che ritornano il parent tramite metodi di delega

### Files Modificati
- **Creati**: `OrderByBuilder.java` (nuovo builder con metodi asc/desc)
- **Modificati**: `SelectBuilder.java` (rimossi metodi diretti, aggiunto orderBy() che restituisce OrderByBuilder)
- **Modificati**: `GroupByBuilder.java` (chaining orderBy()/fetch()/offset() con build(Connection) delegato)
- **Test Aggiornati**: SelectBuilderOrderByTest, SelectBuilderGroupByTest, SelectDSLComponentTest allineati al chaining senza doppio `.build()`

### Test Coverage
- ✅ Tutti i 1363 test passano
- ✅ Validazione completa: null/empty/dot notation per alias e column
- ✅ Empty ORDER BY validation (deve contenere almeno un sorting)
- ✅ Single-table e multi-table contexts testati
- ✅ Chaining da GroupByBuilder testato

### Benefici
- API consistente con GroupByBuilder e condition builders (delega implicita, niente doppio build)
- Ordine di inserzione preservato (semanticamente significativo in SQL)
- Validazione preventiva contro errori comuni
- Fluent API più espressiva e leggibile

---
