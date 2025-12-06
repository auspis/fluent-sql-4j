package lan.tlab.r4j.jdsql.dsl.insert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class InsertBuilderTest {

    private DialectRenderer renderer;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        renderer = StandardSqlRendererFactory.dialectRendererStandardSql();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
    }

    @Test
    void insertWithDefaultValues() throws SQLException {
        new InsertBuilder(renderer, "users").defaultValues().buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
            INSERT INTO "users" DEFAULT VALUES\
            """);
    }

    @Test
    void insertWithSingleColumnAndValue() throws SQLException {
        new InsertBuilder(renderer, "users").set("name", "John").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
            INSERT INTO "users" ("name") VALUES (?)\
            """);
        verify(ps).setObject(1, "John");
    }

    @Test
    void insertWithMultipleColumnsAndValues() throws SQLException {
        new InsertBuilder(renderer, "users")
                .set("id", 1)
                .set("name", "John")
                .set("email", "john@example.com")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
            INSERT INTO "users" ("id", "name", "email") VALUES (?, ?, ?)\
            """);
        verify(ps).setObject(1, 1);
        verify(ps).setObject(2, "John");
        verify(ps).setObject(3, "john@example.com");
    }

    @Test
    void insertWithNullValue() throws SQLException {
        new InsertBuilder(renderer, "users")
                .set("name", "John")
                .set("email", (String) null)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
            INSERT INTO "users" ("name", "email") VALUES (?, ?)\
            """);
        verify(ps).setObject(1, "John");
        verify(ps).setObject(2, null);
    }

    @Test
    void insertWithBooleanValue() throws SQLException {
        new InsertBuilder(renderer, "users")
                .set("name", "John")
                .set("active", true)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
            INSERT INTO "users" ("name", "active") VALUES (?, ?)\
            """);
        verify(ps).setObject(1, "John");
        verify(ps).setObject(2, true);
    }

    @Test
    void insertWithNumericValues() throws SQLException {
        new InsertBuilder(renderer, "products")
                .set("id", 1)
                .set("price", 19.99)
                .set("quantity", 100)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                INSERT INTO "products" ("id", "price", "quantity") VALUES (?, ?, ?)""");
        verify(ps).setObject(1, 1);
        verify(ps).setObject(2, 19.99);
        verify(ps).setObject(3, 100);
    }

    @Test
    void invalidTableName() {
        assertThatThrownBy(() -> new InsertBuilder(renderer, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidNullTableName() {
        assertThatThrownBy(() -> new InsertBuilder(renderer, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidEmptyColumnName() {
        assertThatThrownBy(() -> new InsertBuilder(renderer, "users").set("", "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void invalidNullColumnName() {
        assertThatThrownBy(() -> new InsertBuilder(renderer, "users").set(null, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void insertWithMixedDataTypes() throws SQLException {
        new InsertBuilder(renderer, "mixed_table")
                .set("text_col", "test")
                .set("int_col", 42)
                .set("bool_col", false)
                .set("null_col", (String) null)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                INSERT INTO "mixed_table" ("text_col", "int_col", "bool_col", "null_col") VALUES (?, ?, ?, ?)""");
        verify(ps).setObject(1, "test");
        verify(ps).setObject(2, 42);
        verify(ps).setObject(3, false);
        verify(ps).setObject(4, null);
    }

    @Test
    void insertWithDateValue() throws SQLException {
        LocalDate birthdate = LocalDate.of(1999, 1, 23);
        new InsertBuilder(renderer, "users")
                .set("name", "John")
                .set("birthdate", birthdate)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("INSERT INTO \"users\" (\"name\", \"birthdate\") VALUES (?, ?)");
        verify(ps).setObject(1, "John");
        verify(ps).setObject(2, birthdate);
    }

    @Test
    void buildPreparedStatementRequiresConnection() {
        InsertBuilder builder = new InsertBuilder(renderer, "users").set("name", "John");

        assertThatThrownBy(() -> builder.buildPreparedStatement(null)).isInstanceOf(NullPointerException.class);
    }
}
