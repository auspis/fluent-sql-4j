package lan.tlab.r4j.jdsql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class WhereJsonFunctionBuilderTest {

    private PreparedStatementSpecFactory specFactory;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
    }

    @Test
    void jsonValueStringComparison() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonValue("info", "$.city")
                .eq("Rome")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "users" WHERE JSON_VALUE("info", ?) = ?""");
        verify(ps).setObject(1, "$.city");
        verify(ps).setObject(2, "Rome");
    }

    @Test
    void jsonValueNumberComparison() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonValue("info", "$.age")
                .gt(30)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "users" WHERE JSON_VALUE("info", ?) > ?""");
        verify(ps).setObject(1, "$.age");
        verify(ps).setObject(2, 30);
    }

    @Test
    void jsonExistsCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonExists("info", "$.email")
                .exists()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "users" WHERE JSON_EXISTS("info", ?) = ?""");
        verify(ps).setObject(1, "$.email");
        verify(ps).setObject(2, true);
    }

    @Test
    void jsonNotExistsCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonExists("info", "$.phone")
                .notExists()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "users" WHERE JSON_EXISTS("info", ?) = ?""");
        verify(ps).setObject(1, "$.phone");
        verify(ps).setObject(2, false);
    }

    @Test
    void jsonQueryIsNotNull() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .jsonQuery("data", "$.tags")
                .isNotNull()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "products" WHERE JSON_QUERY("data", ?) IS NOT NULL""");
        verify(ps).setObject(1, "$.tags");
    }

    @Test
    void jsonValueWithMultipleConditions() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonValue("info", "$.city")
                .eq("Rome")
                .and()
                .column("active")
                .eq(true)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT * FROM "users" WHERE (JSON_VALUE("info", ?) = ?) AND ("active" = ?)""");
        verify(ps).setObject(1, "$.city");
        verify(ps).setObject(2, "Rome");
        verify(ps).setObject(3, true);
    }

    @Test
    void jsonValueWithOrCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonValue("info", "$.status")
                .eq("vip")
                .or()
                .jsonValue("info", "$.status")
                .eq("premium")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT * FROM "users" WHERE (JSON_VALUE("info", ?) = ?) OR (JSON_VALUE("info", ?) = ?)""");
        verify(ps).setObject(1, "$.status");
        verify(ps).setObject(2, "vip");
        verify(ps).setObject(3, "$.status");
        verify(ps).setObject(4, "premium");
    }

    @Test
    void jsonMixedConditionsNormalAndJson() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .where()
                .column("status")
                .eq("completed")
                .and()
                .jsonValue("data", "$.amount")
                .gte(100)
                .and()
                .jsonExists("data", "$.customer.email")
                .exists()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT * FROM "orders" \
                WHERE (("status" = ?) \
                AND (JSON_VALUE("data", ?) >= ?)) \
                AND (JSON_EXISTS("data", ?) = ?)""");
        verify(ps).setObject(1, "completed");
        verify(ps).setObject(2, "$.amount");
        verify(ps).setObject(3, 100);
        verify(ps).setObject(4, "$.customer.email");
        verify(ps).setObject(5, true);
    }

    @Test
    void jsonValueIsNull() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .jsonValue("data", "$.discount")
                .isNull()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "products" WHERE JSON_VALUE("data", ?) IS NULL""");
        verify(ps).setObject(1, "$.discount");
    }

    @Test
    void jsonValueWithTableAlias() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .as("u")
                .where()
                .jsonValue("u", "info", "$.city")
                .eq("Milan")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "users" AS u WHERE JSON_VALUE("info", ?) = ?""");
        verify(ps).setObject(1, "$.city");
        verify(ps).setObject(2, "Milan");
    }
}
