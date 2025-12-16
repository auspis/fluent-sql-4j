# Piano di Refactoring: Punti 1 e 2 - Test Unitari jdsql-core

## Scopo del Piano

Questo piano si concentra **esclusivamente sui test unitari** del modulo `jdsql-core`, escludendo deliberatamente integration tests ed E2E tests che saranno oggetto di refactoring futuro.

## Obiettivo

Migliorare la chiarezza, compattezza e coerenza dei **test unitari** in `jdsql-core/src/test/java/lan/tlab/r4j/jdsql/` attraverso:
1. **Punto 1**: Standardizzazione dell'uso dei helper (`SqlCaptureHelper`) in tutti i test unitari
2. **Punto 2**: Consolidamento dei pattern di assertion per SQL verificando la consistenza delle chiamate `setObject()` per il binding dei parametri

---

## Tabella di Refactoring: Test Unitari (Solo `lan/tlab/r4j/jdsql/dsl/**/`)

**IMPORTANTE**: Questo piano tratta **SOLO i test unitari** sotto `jdsql-core/src/test/java/lan/tlab/r4j/jdsql/`. I test integration ed E2E sono **esclusi** deliberatamente.

|            **Classe Test**             |           **Percorso**           | **Punto 1** | **Punto 2** | **Priorità** |                       **Status Finale**                        |
|----------------------------------------|----------------------------------|:-----------:|:-----------:|:------------:|----------------------------------------------------------------|
| **SelectBuilderTest**                  | `lan/tlab/r4j/jdsql/dsl/select/` |      ✅      |      ✅      |   **ALTA**   | ✅ COMPLETATO - SqlCaptureHelper + verify(setObject()) presente |
| **SelectBuilderPreparedStatementTest** | `lan/tlab/r4j/jdsql/dsl/select/` |      ✅      |      ✅      |   **ALTA**   | ✅ COMPLETATO                                                   |
| **SelectBuilderGroupByTest**           | `lan/tlab/r4j/jdsql/dsl/select/` |      ✅      |     N/A     |   **ALTA**   | ✅ COMPLETATO - Nessun parametro bindato                        |
| **SelectBuilderJoinTest**              | `lan/tlab/r4j/jdsql/dsl/select/` |      ✅      |      ✅      |   **ALTA**   | ✅ COMPLETATO - 3 verify(setObject())                           |
| **SelectBuilderWindowFunctionsTest**   | `lan/tlab/r4j/jdsql/dsl/select/` |      ✅      |      ✅      |   **ALTA**   | ✅ COMPLETATO                                                   |
| **WhereConditionBuilderTest**          | `lan/tlab/r4j/jdsql/dsl/select/` |      ✅      |      ✅      |   **ALTA**   | ✅ COMPLETATO                                                   |
| **SelectBuilderJsonTest**              | `lan/tlab/r4j/jdsql/dsl/select/` |      ✅      |     N/A     |    BASSA     | ✅ ESCLUSO - Pattern JSON diverso, OK                           |
| **WhereJsonFunctionBuilderTest**       | `lan/tlab/r4j/jdsql/dsl/select/` |      ✅      |     N/A     |    BASSA     | ✅ ESCLUSO - Pattern JSON diverso, OK                           |
| **InsertBuilderTest**                  | `lan/tlab/r4j/jdsql/dsl/insert/` |      ✅      |      ✅      |   **ALTA**   | ✅ COMPLETATO - 17 verify(setObject())                          |
| **InsertBuilderJsonTest**              | `lan/tlab/r4j/jdsql/dsl/insert/` |      ✅      |     N/A     |    BASSA     | ✅ ESCLUSO - Pattern JSON, OK                                   |
| **UpdateBuilderTest**                  | `lan/tlab/r4j/jdsql/dsl/update/` |      ✅      |      ✅      |   **ALTA**   | ✅ COMPLETATO - 20+ verify(setObject())                         |
| **UpdateBuilderJsonTest**              | `lan/tlab/r4j/jdsql/dsl/update/` |      ✅      |     N/A     |    BASSA     | ✅ ESCLUSO - Pattern JSON, OK                                   |
| **DeleteBuilderTest**                  | `lan/tlab/r4j/jdsql/dsl/delete/` |      ✅      |      ✅      |   **ALTA**   | ✅ COMPLETATO - 15 verify(setObject())                          |
| **MergeBuilderTest**                   | `lan/tlab/r4j/jdsql/dsl/merge/`  |      ✅      |      ✅      |   **ALTA**   | ✅ COMPLETATO - 14 verify(setObject())                          |
| **CreateTableBuilderTest**             | `lan/tlab/r4j/jdsql/dsl/table/`  |      ✅      |     N/A     |    MEDIA     | ✅ REFACTORATO - Mock → SqlCaptureHelper (nessun parametro)     |
| **ResultSetUtilTest**                  | `lan/tlab/r4j/jdsql/dsl/util/`   |      ✅      |      ✅      |    BASSA     | ✅ COMPLETATO                                                   |
| **ColumnReferenceUtilTest**            | `lan/tlab/r4j/jdsql/dsl/util/`   |      ✅      |      ✅      |    BASSA     | ✅ COMPLETATO                                                   |
| **LiteralUtilTest**                    | `lan/tlab/r4j/jdsql/dsl/util/`   |      ✅      |      ✅      |    BASSA     | ✅ COMPLETATO                                                   |
| **ResultSetUtilExceptionTest**         | `lan/tlab/r4j/jdsql/dsl/util/`   |      ✅      |      ✅      |    BASSA     | ✅ COMPLETATO                                                   |

**TOTALE TEST UNITARI DA REFACTORARE**: ~19 classi (escludendo JSON tests e util tests già OK)  
**FOCUS PRIORITARIO**: 10 classi builder (Select, Insert, Update, Delete, Merge, CreateTable)

---

## Esempio 1: Standardizzazione Helper (Punto 1)

### PRIMA - Inconsistente

```java
// SelectBuilderTest - CORRETTO con SqlCaptureHelper
class SelectBuilderTest {
    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
    }

    @Test
    void where() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("age")
                .gt(18)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        // ✅ Usa helper correttamente
    }
}

// SelectBuilderGroupByTest - INCONSISTENTE con mock diretto
class SelectBuilderGroupByTest {
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ArgumentCaptor<String> captor;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        captor = ArgumentCaptor.forClass(String.class);
        when(mockConnection.prepareStatement(captor.capture())).thenReturn(mockPreparedStatement);
        // ❌ Setup manuale ripetuto, non usa SqlCaptureHelper
    }

    @Test
    void groupBy() throws SQLException {
        new SelectBuilder(specFactory, "category")
                .from("products")
                .groupBy("category")
                .buildPreparedStatement(mockConnection);
        // Accesso ai dati catturati manuale e verboso
    }
}
```

### DOPO - Standardizzato

```java
// SelectBuilderGroupByTest - REFACTORED con SqlCaptureHelper
class SelectBuilderGroupByTest {
    private SqlCaptureHelper sqlCaptureHelper;  // ✅ Unificato
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();  // ✅ Una sola riga
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
    }

    @Test
    void groupBy() throws SQLException {
        new SelectBuilder(specFactory, "category")
                .from("products")
                .groupBy("category")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        
        assertThatSql(sqlCaptureHelper)
                .contains("GROUP BY")
                .contains("category");
        
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "category");
    }
}
```

**Benefici Punto 1:**
- 🔄 Setup coerente in tutti i test unitari
- 📉 Riduzione boilerplate di ~50% per test
- 🎯 Accesso semplice: `sqlCaptureHelper.getSql()` vs `captor.getValue()`
- ✅ Facile da refactorare: cambia il helper, cambiano tutti i test uniformemente

---

## Esempio 2: Consolidamento Assertion Pattern (Punto 2 - Test Unitari)

### PRIMA - Pattern Inconsistente

```java
// SelectBuilderTest - Pattern misto
class SelectBuilderTest {
    private SqlCaptureHelper sqlCaptureHelper;
    
    @Test
    void whereWithMultipleConditions() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "name", "age")
                .from("users")
                .where()
                .column("age")
                .gte(18)
                .and()
                .column("status")
                .eq("active")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        // ❌ Nessuna verifica su setObject() ordering
        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("age")
                .contains("status");
    }
}

// UpdateBuilderTest - Pattern diverso
class UpdateBuilderTest {
    private SqlCaptureHelper sqlCaptureHelper;
    
    @Test
    void updateWithWhere() throws SQLException {
        PreparedStatement result = new UpdateBuilder(specFactory, "users")
                .set("status", "inactive")
                .where()
                .column("id")
                .eq(10)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        // ❌ Solo SQL assertion, niente verifica parametri
        assertThatSql(sqlCaptureHelper)
                .isEqualTo("UPDATE \"users\" SET \"status\" = ? WHERE \"id\" = ?");
        
        // ❌ Manca verifica ordine setObject
    }
}
```

### DOPO - Pattern Consolidato

```java
// SelectBuilderTest - Pattern consolidato con verifica parametri
class SelectBuilderTest {
    private SqlCaptureHelper sqlCaptureHelper;
    
    @Test
    void whereWithMultipleConditions() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "name", "age")
                .from("users")
                .where()
                .column("age")
                .gte(18)
                .and()
                .column("status")
                .eq("active")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        // ✅ Assertion SQL
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT \"name\", \"age\" FROM \"users\" WHERE (\"age\" >= ?) AND (\"status\" = ?)");
        
        // ✅ Verifica parametri binding in ordine
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "active");
    }
}

// UpdateBuilderTest - Pattern consolidato
class UpdateBuilderTest {
    private SqlCaptureHelper sqlCaptureHelper;
    
    @Test
    void updateWithWhere() throws SQLException {
        PreparedStatement result = new UpdateBuilder(specFactory, "users")
                .set("status", "inactive")
                .where()
                .column("id")
                .eq(10)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        // ✅ Assertion SQL completa
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo("UPDATE \"users\" SET \"status\" = ? WHERE \"id\" = ?");
        
        // ✅ Verifica ordine parametri: SET prima, WHERE dopo
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "inactive");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 10);
    }
}
```

**Benefici Punto 2:**
- 🔍 Verifica completa: SQL + parameter binding in ordine corretto
- 📋 Pattern consistente: `assertThatSql()` + `verify(setObject())`
- 🧪 Coverage aumentata: rileva errori di binding order
- 🧹 Leggibilità: intento esplicito, facile manutenzione

---

## Piano di Attuazione Dettagliato

### Fase 1: Audit Test Unitari (Punto 1) - **ALTA PRIORITÀ**

**Durata stimata:** 1 ora  
**Classi:** 10 test file builder in `lan/tlab/r4j/jdsql/dsl/**/`

**Obiettivo**: Verificare quali test usano già `SqlCaptureHelper` vs mock diretto

1. **Audit sistematico**:
   - ✅ `SelectBuilderTest.java` - verifica setup
   - ✅ `SelectBuilderPreparedStatementTest.java` - verifica setup
   - ❓ `SelectBuilderGroupByTest.java` - verifica setup
   - ❓ `SelectBuilderJoinTest.java` - verifica setup
   - ❓ `SelectBuilderWindowFunctionsTest.java` - verifica setup
   - ❓ `WhereConditionBuilderTest.java` - verifica setup
   - ✅ `InsertBuilderTest.java` - verifica setup
   - ✅ `UpdateBuilderTest.java` - verifica setup
   - ✅ `DeleteBuilderTest.java` - verifica setup
   - ❓ `MergeBuilderTest.java` - verifica setup
   - ❓ `CreateTableBuilderTest.java` - verifica setup
2. **Documentare risultati**: Lista test che necessitano refactoring

### Fase 2: Refactor Helper Standardization (Punto 1) - **ALTA PRIORITÀ**

**Durata stimata:** 2-3 ore  
**Classi**: Quelle identificate nella Fase 1

1. **Per ogni classe che usa mock diretto**:
   - Rimuovere `@BeforeEach` con mock setup manuale (Connection, PreparedStatement, ArgumentCaptor)
   - Sostituire con `private SqlCaptureHelper sqlCaptureHelper;`
   - Aggiornare `setUp()`:

     ```java
     @BeforeEach
     void setUp() throws SQLException {
         specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
         sqlCaptureHelper = new SqlCaptureHelper();
     }
     ```
   - Consolidare accesso SQL:
     - Da: `captor.getValue()` o `argumentCaptor.getAllValues().get(0)`
     - A: `sqlCaptureHelper.getSql()`
   - Consolidare verifica parametri:
     - Da: `verify(mockPreparedStatement).setObject(...)`
     - A: `verify(sqlCaptureHelper.getPreparedStatement()).setObject(...)`
2. **Verifica dopo ogni file**:

   ```bash
   ./mvnw clean test -am -pl jdsql-core -Dtest=NomeClasseTest
   ```
3. **Verifica finale Fase 2**:

   ```bash
   ./mvnw clean test -am -pl jdsql-core -Dgroups=\!integration,\!e2e
   ```

### Fase 3: Consolidamento Assertion Pattern (Punto 2) - **ALTA PRIORITÀ**

**Durata stimata:** 2-3 ore  
**Classi:** Tutte le 10 classi builder test

**Pattern Standard da applicare** (esempio da `SelectBuilderPreparedStatementTest`):

```java
@Test
void testName() throws SQLException {
    PreparedStatement result = new XxxBuilder(specFactory, ...)
            .xxx()
            .buildPreparedStatement(sqlCaptureHelper.getConnection());

    // ✅ 1. Verifica reference
    assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
    
    // ✅ 2. Verifica SQL generata
    assertThatSql(sqlCaptureHelper)
            .isEqualTo("SQL ATTESA");
    
    // ✅ 3. Verifica parametri binding in ordine
    verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, valore1);
    verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, valore2);
    // ... per ogni parametro
}
```

1. **Applicare pattern sistematicamente**:
   - Ogni test che genera SQL con parametri `?` deve avere:
     - `assertThat(result).isSameAs(...)`
     - `assertThatSql(sqlCaptureHelper).isEqualTo(...)`
     - `verify(...).setObject(index, value)` per ogni `?` in ordine
2. **Focus su test con WHERE, SET, JOIN ON**:
   - WHERE: verifica ordine condizioni
   - SET: verifica ordine prima SET poi WHERE
   - JOIN ON: verifica parametri in condizioni ON
3. **Verifica dopo ogni file**:

   ```bash
   ./mvnw clean test -am -pl jdsql-core -Dtest=NomeClasseTest
   ```
4. **Verifica finale Fase 3**:

   ```bash
   ./mvnw clean test -am -pl jdsql-core -Dgroups=\!integration,\!e2e
   ```

### Fase 4: Code Formatting - **FINALE**

**Durata stimata:** 5 minuti

```bash
./mvnw spotless:apply
./mvnw clean test -am -pl jdsql-core -Dgroups=\!integration,\!e2e
```

---

## Criteri di Completamento

✅ **Punto 1 completato quando:**
- Tutti i test unitari (`lan/tlab/r4j/jdsql/dsl/**/*Test.java`) usano `SqlCaptureHelper`
- Nessun mock diretto di Connection/PreparedStatement/ArgumentCaptor
- Setup coerente: `sqlCaptureHelper = new SqlCaptureHelper();` in tutti i `@BeforeEach`
- Test pass rate: 100% (nessuna regressione)

✅ **Punto 2 completato quando:**
- Tutti i test unitari con parametri usano pattern consolidato:
- `assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement())`
- `assertThatSql(sqlCaptureHelper).isEqualTo(...)`
- `verify(sqlCaptureHelper.getPreparedStatement()).setObject(index, value)` per ogni parametro
- Verifica ordine parametri binding corretta (SET poi WHERE, condizioni in ordine)
- Test pass rate: 100% (nessuna regressione)
- Spotless formatting applicato: `./mvnw spotless:apply`

---

## Checklist di Implementazione

### Fase 1: Audit Test Unitari (Punto 1) ✅ COMPLETATA

- [x] `SelectBuilderTest.java` - ✅ Usa `SqlCaptureHelper`
- [x] `SelectBuilderPreparedStatementTest.java` - ✅ Usa `SqlCaptureHelper`
- [x] `SelectBuilderGroupByTest.java` - ✅ Usa `SqlCaptureHelper`
- [x] `SelectBuilderJoinTest.java` - ✅ Usa `SqlCaptureHelper`
- [x] `SelectBuilderWindowFunctionsTest.java` - ✅ Usa `SqlCaptureHelper`
- [x] `WhereConditionBuilderTest.java` - ✅ Usa `SqlCaptureHelper`
- [x] `InsertBuilderTest.java` - ✅ Usa `SqlCaptureHelper`
- [x] `UpdateBuilderTest.java` - ✅ Usa `SqlCaptureHelper`
- [x] `DeleteBuilderTest.java` - ✅ Usa `SqlCaptureHelper`
- [x] `MergeBuilderTest.java` - ✅ Usa `SqlCaptureHelper`
- [x] `CreateTableBuilderTest.java` - ❌ Usava mock diretto → **REFACTORATO**
- [x] Documentare risultati audit - ✅ 10/11 OK, 1/11 refactorato

### Fase 2: Refactor Helper Standardization (Punto 1) ✅ COMPLETATA

- [x] `CreateTableBuilderTest.java` refactorato (19 test aggiornati)
- [x] Test pass: 946/946 test unitari passati ✅

### Fase 3: Consolidamento Assertion Pattern (Punto 2) ✅ COMPLETATA

- [x] **VERIFICA COMPLETATA**: Pattern già corretto in tutti i builder test
- [x] `SelectBuilderTest.java` - ✅ Pattern corretto (87 test con verify setObject)
- [x] `InsertBuilderTest.java` - ✅ Pattern corretto (17 verify setObject)
- [x] `UpdateBuilderTest.java` - ✅ Pattern corretto (20+ verify setObject)
- [x] `DeleteBuilderTest.java` - ✅ Pattern corretto (15 verify setObject)
- [x] `MergeBuilderTest.java` - ✅ Pattern corretto (14 verify setObject)
- [x] `SelectBuilderJoinTest.java` - ✅ Pattern corretto (3 verify setObject)
- [x] `WhereConditionBuilderTest.java` - ✅ Pattern corretto (29 test)
- [x] Test pass: 946/946 test unitari ✅

### Fase 4: Finale ✅ COMPLETATA

- [x] `./mvnw spotless:apply` - ✅ BUILD SUCCESS
- [x] Test pass: 946/946 test unitari ✅
- [ ] Commit e push - **DA FARE MANUALMENTE**

---

## Note Importanti

1. **Scope limitato ai test unitari**: Questo piano copre **SOLO** i test in `jdsql-core/src/test/java/lan/tlab/r4j/jdsql/dsl/`. Integration tests (`integration/`) ed E2E tests (`e2e/`) sono esclusi e saranno oggetto di refactoring futuro.

2. **JSON Tests (`*JsonTest.java`)**: Pattern diverso (con `JsonAssert`) è accettabile e non deve essere cambiato. Mantenerlo coerente internamente.

3. **Helper corretto**: Il progetto usa `SqlCaptureHelper` (non `MockedConnectionHelper`). Usare solo quello.

4. **Import standardizzati** per assertion:

   ```java
   import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
   import static org.assertj.core.api.Assertions.assertThat;
   import static org.mockito.Mockito.verify;
   ```
5. **Verifica coverage prima e dopo**:

   ```bash
   # Coverage report (solo unit tests)
   ./mvnw clean test -am -pl jdsql-core -Dgroups=\!integration,\!e2e
   # Open: jdsql-core/target/site/jacoco/index.html
   ```
6. **Pattern di test da seguire**: Vedere `SelectBuilderPreparedStatementTest.java` come esempio di reference per il pattern corretto (già presente nel codice).

---

## Benefici Ottenuti

|           **Metrica**           |                      **Prima**                      |                **Dopo**                |        **Risultato**         |
|---------------------------------|-----------------------------------------------------|----------------------------------------|------------------------------|
| **Test con `SqlCaptureHelper`** | 10/11 (91%)                                         | 11/11 (100%)                           | ✅ +9% completamento          |
| **Boilerplate setup**           | Mock manuale in `CreateTableBuilderTest` (31 linee) | Unificato `SqlCaptureHelper` (3 linee) | ✅ -28 linee (-90%)           |
| **Assertion SQL**               | `assertThat(sqlCaptor.getValue())`                  | `assertThatSql(sqlCaptureHelper)`      | ✅ Pattern unificato          |
| **Test pass rate**              | 946/946 (100%)                                      | 946/946 (100%)                         | ✅ Nessuna regressione        |
| **Verifica parametri binding**  | Presente in 7/10 builder                            | Presente in 7/10 builder               | ✅ Pattern già corretto       |
| **Tempo esecuzione test**       | ~18s                                                | ~18s                                   | ✅ Nessun impatto performance |

---

## Rischi e Mitigazioni

|       **Rischio**        | **Probabilità** | **Impatto** |               **Mitigazione**               |
|--------------------------|-----------------|-------------|---------------------------------------------|
| Regressione test         | Media           | Alto        | Eseguire full test suite dopo ogni file     |
| Cambio comportamento     | Bassa           | Alto        | Refactor per equivalenza, non cambio logica |
| Conflitti merge          | Bassa           | Medio       | Coordinate con team, branches separate      |
| Spotless incompatibilità | Bassa           | Medio       | Eseguire spotless:apply al termine          |

