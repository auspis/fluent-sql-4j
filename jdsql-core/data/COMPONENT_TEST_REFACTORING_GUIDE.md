# Component Test Refactoring Guide: SqlCaptureHelper

## Obiettivo

Standardizzare i component test DSL per usare `SqlCaptureHelper` e ridurre il boilerplate delle mock.

## Situazione Attuale (Manual Mock Setup)

```java
@ComponentTest
class DeleteDSLComponentTest {

    private DSL dsl;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        dsl = StandardSQLDialectPlugin.instance().createDSL();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
    }

    @Test
    void createsDeleteBuilderWithRenderer() throws SQLException {
        dsl.deleteFrom("users").where().column("id").eq(1)
            .build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                DELETE FROM "users" WHERE "id" = ?""");
        verify(ps).setObject(1, 1);
    }
}
```

**Boilerplate**: 5 fields + 6 lines di setup

---

## Refactored (Con SqlCaptureHelper)

```java
@ComponentTest
class DeleteDSLComponentTest {

    private DSL dsl;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        dsl = StandardSQLDialectPlugin.instance().createDSL();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void createsDeleteBuilderWithRenderer() throws SQLException {
        dsl.deleteFrom("users").where().column("id").eq(1)
            .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                DELETE FROM "users" WHERE "id" = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 1);
    }
}
```

**Miglioramenti**:
- ✅ 2 fields instead of 5 (60% riduzione)
- ✅ 2 lines di setup instead of 6 (67% riduzione)
- ✅ Setup è self-contained (non richiede spiegazione di mock)
- ✅ Usa `assertThatSql()` per SQL assertions (più leggibile)

---

## Component Test Files da Refactorare

|                File                | Linee Setup |                Stato                |
|------------------------------------|-------------|-------------------------------------|
| `InsertDSLComponentTest.java`      | 6           | ⏳ Da refactorare                    |
| `DeleteDSLComponentTest.java`      | 6           | ⏳ Da refactorare                    |
| `SelectDSLComponentTest.java`      | 6           | ⏳ Da refactorare (660 linee totali) |
| `UpdateDSLComponentTest.java`      | 6           | ⏳ Da refactorare                    |
| `CreateTableDSLComponentTest.java` | 6           | ⏳ Da refactorare                    |
| `MergeDSLComponentTest.java`       | 6           | ⏳ Da refactorare                    |
| `DSLRegistryComponentTest.java`    | ?           | ⏳ Da verificare                     |

---

## Checklist per Refactorare un Component Test

1. **Aggiungi import**:

   ```java
   import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
   import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
   ```
2. **Sostituisci 5 fields con 2**:

   ```java
   // DA:
   private Connection connection;
   private PreparedStatement ps;
   private ArgumentCaptor<String> sqlCaptor;

   // A:
   private SqlCaptureHelper sqlCaptureHelper;
   ```
3. **Semplifica setUp()**:

   ```java
   // DA:
   connection = mock(Connection.class);
   ps = mock(PreparedStatement.class);
   sqlCaptor = ArgumentCaptor.forClass(String.class);
   when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);

   // A:
   sqlCaptureHelper = new SqlCaptureHelper();
   ```
4. **Aggiorna le test calls**:

   ```java
   // DA:
   .build(connection);

   // A:
   .build(sqlCaptureHelper.getConnection());
   ```
5. **Aggiorna SQL assertions**:

   ```java
   // DA:
   assertThat(sqlCaptor.getValue()).isEqualTo("...");

   // A:
   assertThatSql(sqlCaptureHelper).isEqualTo("...");
   ```
6. **Aggiorna verify calls**:

   ```java
   // DA:
   verify(ps).setObject(1, value);

   // A:
   verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, value);
   ```

---

## Impatto

- **Riduzione boilerplate**: ~60-70% del setup code
- **Coerenza**: Tutti i builder test e component test usano lo stesso helper
- **Manutenibilità**: Meno codice, meno test setup duplicati
- **Leggibilità**: `assertThatSql()` è più intuitivo di `assertThat(sqlCaptor.getValue())`

---

## Note

- `SqlCaptureHelper` è la fonte di verità per mock setup nei test
- I builder test (InsertBuilderTest, DeleteBuilderTest, etc.) usano già SqlCaptureHelper ✅
- I component test dovrebbero essere migrati gradualmente (non è urgente)
- Non usare SqlCaptureHelper per test di integrazione con real database (H2/Testcontainers)

