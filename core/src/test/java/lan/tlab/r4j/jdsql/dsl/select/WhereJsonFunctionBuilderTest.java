package lan.tlab.r4j.jdsql.dsl.select;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.util.StandardSqlUtil;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhereJsonFunctionBuilderTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void jsonValueStringComparison() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonValue("info", "$.city")
                .eq("Rome")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE JSON_VALUE("info", ?) = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.city");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "Rome");
    }

    @Test
    void jsonValueNumberComparison() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonValue("info", "$.age")
                .gt(30)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE JSON_VALUE("info", ?) > ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.age");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 30);
    }

    @Test
    void jsonExistsCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonExists("info", "$.email")
                .exists()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE JSON_EXISTS("info", ?) = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.email");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, true);
    }

    @Test
    void jsonNotExistsCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonExists("info", "$.phone")
                .notExists()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE JSON_EXISTS("info", ?) = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.phone");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, false);
    }

    @Test
    void jsonQueryIsNotNull() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .jsonQuery("data", "$.tags")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "products" WHERE JSON_QUERY("data", ?) IS NOT NULL""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.tags");
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
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE (JSON_VALUE("info", ?) = ?) AND ("active" = ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.city");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "Rome");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, true);
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
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE (JSON_VALUE("info", ?) = ?) OR (JSON_VALUE("info", ?) = ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.status");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "vip");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "$.status");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "premium");
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
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "orders" \
                WHERE (("status" = ?) \
                AND (JSON_VALUE("data", ?) >= ?)) \
                AND (JSON_EXISTS("data", ?) = ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "completed");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "$.amount");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "$.customer.email");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(5, true);
    }

    @Test
    void jsonValueIsNull() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .jsonValue("data", "$.discount")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "products" WHERE JSON_VALUE("data", ?) IS NULL""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.discount");
    }

    @Test
    void jsonValueWithTableAlias() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .as("u")
                .where()
                .jsonValue("u", "info", "$.city")
                .eq("Milan")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" AS u WHERE JSON_VALUE("info", ?) = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.city");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "Milan");
    }
}
