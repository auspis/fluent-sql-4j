package io.github.massimiliano.fluentsql4j.dsl.clause;

import static io.github.massimiliano.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.mockito.Mockito.verify;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.BehaviorKind;
import io.github.massimiliano.fluentsql4j.dsl.DSL;
import io.github.massimiliano.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.massimiliano.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhereJsonFunctionBuilderTest {

    private SqlCaptureHelper sqlCaptureHelper;
    private DSL dsl;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        dsl = StandardSqlUtil.dsl();
    }

    @Test
    void jsonValueBasicComparison() throws SQLException {
        dsl.select("id", "name")
                .from("users")
                .where()
                .jsonValue("metadata", "$.age")
                .eq(25)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_VALUE");
        assertThatSql(sqlCaptureHelper).contains("= ?");
        // First param is JSON path, second is comparison value
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.age");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 25);
    }

    @Test
    void jsonValueNumberEqComparison() throws SQLException {
        dsl.select("id")
                .from("products")
                .where()
                .jsonValue("attributes", "$.quantity")
                .eq(50)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_VALUE");
        assertThatSql(sqlCaptureHelper).contains("= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.quantity");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 50);
    }

    @Test
    void jsonValueNumberNeComparison() throws SQLException {
        dsl.select("id")
                .from("inventory")
                .where()
                .jsonValue("stock_info", "$.reorder_point")
                .ne(0)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_VALUE");
        assertThatSql(sqlCaptureHelper).contains("<> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.reorder_point");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 0);
    }

    @Test
    void jsonValueNumberGtComparison() throws SQLException {
        dsl.select("name")
                .from("employees")
                .where()
                .jsonValue("performance", "$.rating")
                .gt(4.5)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_VALUE");
        assertThatSql(sqlCaptureHelper).contains("> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.rating");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 4.5);
    }

    @Test
    void jsonValueNumberLtComparison() throws SQLException {
        dsl.select("product_id")
                .from("sales")
                .where()
                .jsonValue("details", "$.price")
                .lt(99.99)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_VALUE");
        assertThatSql(sqlCaptureHelper).contains("< ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.price");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 99.99);
    }

    @Test
    void jsonValueNumberGteComparison() throws SQLException {
        dsl.select("order_id")
                .from("orders")
                .where()
                .jsonValue("totals", "$.amount")
                .gte(1000.0)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_VALUE");
        assertThatSql(sqlCaptureHelper).contains(">= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.amount");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 1000.0);
    }

    @Test
    void jsonValueNumberLteComparison() throws SQLException {
        dsl.select("customer_id")
                .from("transactions")
                .where()
                .jsonValue("payment", "$.fee")
                .lte(5.0)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_VALUE");
        assertThatSql(sqlCaptureHelper).contains("<= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.fee");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 5.0);
    }

    @Test
    void jsonValueIsNull() throws SQLException {
        dsl.select("id")
                .from("users")
                .where()
                .jsonValue("preferences", "$.newsletter")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_VALUE");
        assertThatSql(sqlCaptureHelper).contains("IS NULL");
    }

    @Test
    void jsonValueIsNotNull() throws SQLException {
        dsl.select("id")
                .from("customers")
                .where()
                .jsonValue("contact", "$.phone")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_VALUE");
        assertThatSql(sqlCaptureHelper).contains("IS NOT NULL");
    }

    @Test
    void jsonExistsWithExists() throws SQLException {
        dsl.select("id")
                .from("documents")
                .where()
                .jsonExists("metadata", "$.tags[0]")
                .exists()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_EXISTS");
        assertThatSql(sqlCaptureHelper).contains("= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.tags[0]");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, true);
    }

    @Test
    void jsonExistsWithNotExists() throws SQLException {
        dsl.select("id")
                .from("profiles")
                .where()
                .jsonExists("settings", "$.advanced")
                .notExists()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_EXISTS");
        assertThatSql(sqlCaptureHelper).contains("= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.advanced");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, false);
    }

    @Test
    void jsonExistsWithOnErrorBehavior() throws SQLException {
        dsl.select("id")
                .from("data")
                .where()
                .jsonExists("json_col", "$.path")
                .onError(BehaviorKind.NONE)
                .exists()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_EXISTS");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.path");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, true);
    }

    @Test
    void jsonQueryStringComparison() throws SQLException {
        dsl.select("id")
                .from("records")
                .where()
                .jsonQuery("data", "$.results")
                .eq("[1, 2, 3]")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_QUERY");
        assertThatSql(sqlCaptureHelper).contains("= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.results");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "[1, 2, 3]");
    }

    @Test
    void jsonQueryWithReturningType() throws SQLException {
        dsl.select("name")
                .from("collections")
                .where()
                .jsonQuery("items", "$.list")
                .returning("VARCHAR(500)")
                .ne("[]")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_QUERY");
        assertThatSql(sqlCaptureHelper).contains("<> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.list");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "[]");
    }

    @Test
    void jsonQueryIsNull() throws SQLException {
        dsl.select("id")
                .from("logs")
                .where()
                .jsonQuery("payload", "$.errors")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_QUERY");
        assertThatSql(sqlCaptureHelper).contains("IS NULL");
    }

    @Test
    void jsonQueryIsNotNull() throws SQLException {
        dsl.select("id")
                .from("events")
                .where()
                .jsonQuery("details", "$.participants")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("JSON_QUERY");
        assertThatSql(sqlCaptureHelper).contains("IS NOT NULL");
    }

    @Test
    void jsonValueCombinedWithAndLogicalOperator() throws SQLException {
        dsl.select("id")
                .from("users")
                .where()
                .jsonValue("profile", "$.age")
                .gt(18)
                .and()
                .jsonValue("profile", "$.verified")
                .eq("true")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).containsInOrder("JSON_VALUE", "> ?", "AND", "JSON_VALUE", "= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.age");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "$.verified");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "true");
    }

    @Test
    void jsonExistsCombinedWithOrLogicalOperator() throws SQLException {
        dsl.select("id")
                .from("documents")
                .where()
                .jsonExists("metadata", "$.author")
                .exists()
                .or()
                .jsonExists("metadata", "$.editor")
                .exists()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).containsInOrder("JSON_EXISTS", "= ?", "OR", "JSON_EXISTS", "= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.author");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, true);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "$.editor");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, true);
    }
}
