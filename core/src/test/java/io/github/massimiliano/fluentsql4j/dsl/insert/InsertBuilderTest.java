package io.github.massimiliano.fluentsql4j.dsl.insert;

import static io.github.massimiliano.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.massimiliano.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsertBuilderTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void insertWithDefaultValues() throws SQLException {
        new InsertBuilder(specFactory, "users").defaultValues().build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            INSERT INTO "users" DEFAULT VALUES\
            """);
    }

    @Test
    void insertWithSingleColumnAndValue() throws SQLException {
        new InsertBuilder(specFactory, "users").set("name", "John").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            INSERT INTO "users" ("name") VALUES (?)\
            """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John");
    }

    @Test
    void insertWithMultipleColumnsAndValues() throws SQLException {
        new InsertBuilder(specFactory, "users")
                .set("id", 1)
                .set("name", "John")
                .set("email", "john@example.com")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            INSERT INTO "users" ("id", "name", "email") VALUES (?, ?, ?)\
            """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 1);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "John");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "john@example.com");
    }

    @Test
    void insertWithNullValue() throws SQLException {
        new InsertBuilder(specFactory, "users")
                .set("name", "John")
                .set("email", (String) null)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            INSERT INTO "users" ("name", "email") VALUES (?, ?)\
            """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, null);
    }

    @Test
    void insertWithBooleanValue() throws SQLException {
        new InsertBuilder(specFactory, "users")
                .set("name", "John")
                .set("active", true)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            INSERT INTO "users" ("name", "active") VALUES (?, ?)\
            """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, true);
    }

    @Test
    void insertWithNumericValues() throws SQLException {
        new InsertBuilder(specFactory, "products")
                .set("id", 1)
                .set("price", 19.99)
                .set("quantity", 100)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                INSERT INTO "products" ("id", "price", "quantity") VALUES (?, ?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 1);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 19.99);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 100);
    }

    @Test
    void invalidTableName() {
        assertThatThrownBy(() -> new InsertBuilder(specFactory, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidNullTableName() {
        assertThatThrownBy(() -> new InsertBuilder(specFactory, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidEmptyColumnName() {
        assertThatThrownBy(() -> new InsertBuilder(specFactory, "users").set("", "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void invalidNullColumnName() {
        assertThatThrownBy(() -> new InsertBuilder(specFactory, "users").set(null, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void insertWithMixedDataTypes() throws SQLException {
        new InsertBuilder(specFactory, "mixed_table")
                .set("text_col", "test")
                .set("int_col", 42)
                .set("bool_col", false)
                .set("null_col", (String) null)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                INSERT INTO "mixed_table" ("text_col", "int_col", "bool_col", "null_col") VALUES (?, ?, ?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "test");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 42);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, false);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, null);
    }

    @Test
    void insertWithDateValue() throws SQLException {
        LocalDate birthdate = LocalDate.of(1999, 1, 23);
        new InsertBuilder(specFactory, "users")
                .set("name", "John")
                .set("birthdate", birthdate)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("INSERT INTO \"users\" (\"name\", \"birthdate\") VALUES (?, ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, birthdate);
    }

    @Test
    void buildRequiresConnection() {
        InsertBuilder builder = new InsertBuilder(specFactory, "users").set("name", "John");

        assertThatThrownBy(() -> builder.build(null)).isInstanceOf(NullPointerException.class);
    }
}
