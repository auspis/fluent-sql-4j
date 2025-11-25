# Plan: Implementare ContextPreparationVisitor per context-aware rendering con JOIN

Creare un `Visitor<AstContext>` che analizza l'AST prima del rendering e arricchisce il contesto con tutte le feature rilevate (JOIN, UNION, WHERE, HAVING, GROUP_BY, WINDOW_FUNCTION, SUBQUERY), permettendo la qualificazione automatica delle colonne quando necessario.

## Steps

### 1. Creare `ContextPreparationVisitor`

**File**: `lan.tlab.r4j.jdsql.ast.visitor.ContextPreparationVisitor`

- Implementa `Visitor<AstContext>`
- Implementare metodi chiave:
  - `visit(SelectStatement)` attraversa ricorsivamente tutte le clausole (`select`, `from`, `where`, `groupBy`, `having`, `orderBy`) accumulando feature da ognuna
  - `visit(From)` itera su `sources()` e accumula feature da ogni `FromSource`
  - `visit(OnJoin)` ritorna `ctx.withFeatures(Feature.JOIN_ON)` e visita ricorsivamente `left`, `right`, `onCondition`
  - `visit(TableIdentifier)` ritorna `ctx` immutato
  - `visit(UnionExpression/IntersectExpression/ExceptExpression)` ritorna `ctx.withFeatures(Feature.UNION)` e visita ricorsivamente left/right
  - `visit(FromSubquery)` propaga tutte le feature dalla subquery interna al contesto esterno
  - `visit(Where)` ritorna `ctx.withFeatures(Feature.WHERE)` e visita ricorsivamente la `condition`
  - `visit(Having)` ritorna `ctx.withFeatures(Feature.HAVING)` e visita ricorsivamente la `condition`
  - `visit(GroupBy)` ritorna `ctx.withFeatures(Feature.GROUP_BY)` e visita ricorsivamente le `groupingExpressions`
  - `visit(OverClause)` ritorna `ctx.withFeatures(Feature.WINDOW_FUNCTION)`
  - `visit(ScalarSubquery)` ritorna `ctx.withFeatures(Feature.SUBQUERY)` e visita ricorsivamente la subquery interna
- Implementazioni default per tutti gli altri metodi che ritornano `ctx` immutato

### 2. Modificare `DialectRenderer`

**File**: `DialectRenderer.renderPreparedStatement()` e `renderSql()`

- Creare field statico `private static final ContextPreparationVisitor CONTEXT_ANALYZER = new ContextPreparationVisitor()`
- In `renderPreparedStatement()`:
  - Invocare `AstContext enrichedCtx = statement.accept(CONTEXT_ANALYZER, new AstContext())`
  - Poi `statement.accept(psRenderer, enrichedCtx)`
- In `renderSql()`: stessa logica per consistenza

### 3. Rimuovere contesto hardcoded

**File**: `StandardSqlOnJoinPsStrategy.handle()` riga 35

- Sostituire `new AstContext(AstContext.Feature.JOIN_ON)` con `ctx`
- Il contesto viene già arricchito globalmente dal `ContextPreparationVisitor`

### 4. Scrivere test unitari per `ContextPreparationVisitor`

**File**: `ContextPreparationVisitorTest`

Test cases:
- SELECT con singola tabella ritorna contesto vuoto
- SELECT con INNER/LEFT/RIGHT JOIN ritorna `hasFeature(Feature.JOIN_ON)`
- JOIN nested accumula correttamente `Feature.JOIN_ON`
- UNION/INTERSECT/EXCEPT ritornano `hasFeature(Feature.UNION)`
- SELECT con WHERE ritorna `hasFeature(Feature.WHERE)`
- SELECT con GROUP BY ritorna `hasFeature(Feature.GROUP_BY)`
- SELECT con HAVING ritorna `hasFeature(Feature.HAVING)`
- SELECT con window function ritorna `hasFeature(Feature.WINDOW_FUNCTION)`
- SELECT con scalar subquery ritorna `hasFeature(Feature.SUBQUERY)`
- FromSubquery con JOIN interno propaga `Feature.JOIN_ON` al contesto esterno
- Combinazione complessa (JOIN + WHERE + GROUP BY + HAVING) accumula tutte le feature

### 5. Verificare test esistente

**File**: `StandardSqlSelectStatementPsStrategyTest.join()`

- Eseguire test per confermare che passa automaticamente dopo le modifiche
- SQL atteso: `SELECT "t1"."id", "t2"."name" FROM "t1" INNER JOIN "t2" ON "t1"."id" = "t2"."t1_id"`

## Further Considerations

### 1. Visitor pattern ricorsivo completo ✅

**Decisione**: Il `ContextPreparationVisitor` deve implementare tutti i metodi `visit()` dell'interfaccia `Visitor<AstContext>`. La maggior parte dei metodi ritorneranno semplicemente `ctx` immutato (implementazione default), mentre solo i metodi rilevanti per la feature detection effettueranno modifiche al contesto.

### 2. Ordine di accumulo delle feature ✅

**Decisione**: L'ordine di visita **non è importante**. Il `Set<Feature>` garantisce idempotenza indipendentemente dall'ordine in cui le feature vengono aggiunte. Questo semplifica l'implementazione perché non è necessario preoccuparsi della sequenza di attraversamento dell'AST.

### 3. Testing coverage ✅

**Decisione**: Il `ContextPreparationVisitor` avrà alta coverage dato che è critico per il funzionamento. Implementare test parametrizzati per combinazioni complesse di feature (es. JOIN + WHERE + GROUP BY + HAVING + WINDOW_FUNCTION). Ogni test case verificherà sia feature singole che combinazioni multiple per garantire che l'accumulo funzioni correttamente in scenari complessi.

## Context Files Reference

### AstContext.java

```java
public record AstContext(Set<Feature> features) {
    public enum Feature {
        WHERE,
        HAVING,
        GROUP_BY,
        JOIN_ON,
        UNION,
        SUBQUERY,
        WINDOW_FUNCTION
    }

    public AstContext(Feature... features) {
        this(Set.of(features));
    }

    public AstContext withFeatures(Feature... newFeatures) {
        Set<Feature> combined = new HashSet<>(features);
        combined.addAll(Arrays.asList(newFeatures));
        return new AstContext(Collections.unmodifiableSet(combined));
    }

    public boolean hasFeature(Feature feature) {
        return features.contains(feature);
    }
}
```

### Current behavior to fix

**StandardSqlOnJoinPsStrategy.java** (line 35):

```java
// PROBLEMA: crea contesto hardcoded invece di usare quello propagato
PsDto onResult = join.onCondition().accept(renderer, new AstContext(AstContext.Feature.JOIN_ON));
```

**StandardSqlColumnReferencePsStrategy.java**:

```java
// Già corretto: usa hasFeature per decidere se qualificare
boolean qualify = ctx.hasFeature(AstContext.Feature.JOIN_ON) || ctx.hasFeature(AstContext.Feature.UNION);
if (qualify && !col.table().isBlank()) {
    sql = escapeStrategy.apply(col.table()) + "." + escapeStrategy.apply(col.column());
}
```

**DialectRenderer.java** (punto di iniezione):

```java
public PsDto renderPreparedStatement(Statement statement) {
    // QUI: aggiungere pre-analisi con ContextPreparationVisitor
    return statement.accept(psRenderer, new AstContext());
}
```

## Design Decisions

1. **Feature collection senza isolamento**: Le feature vengono accumulate globalmente attraverso tutta la gerarchia dell'AST, incluse le subquery. Non c'è isolamento tra query esterne e subquery.

2. **Window functions detection**: Il visitor rileva anche window functions (`OverClause`, `RowNumber`, `Rank`, ecc.) e aggiunge `Feature.WINDOW_FUNCTION`.

3. **Doppio passaggio accettabile**: Per ora accettiamo l'overhead del doppio passaggio (analisi + rendering). Non sono necessarie ottimizzazioni premature.

4. **Field statico per il visitor**: `ContextPreparationVisitor` è stateless, quindi può essere un field statico final in `DialectRenderer` per evitare istanziazioni ripetute.

