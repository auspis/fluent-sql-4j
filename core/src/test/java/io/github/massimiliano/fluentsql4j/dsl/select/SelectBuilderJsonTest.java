package io.github.massimiliano.fluentsql4j.dsl.select;

import static io.github.massimiliano.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.mockito.Mockito.verify;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.BehaviorKind;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.JsonExists;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.JsonQuery;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.JsonValue;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.OnEmptyBehavior;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.WrapperBehavior;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Select;
import io.github.massimiliano.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.massimiliano.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderJsonTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void jsonExistsBasicUsage() throws SQLException {
        JsonExists jsonExists = new JsonExists(ColumnReference.of("users", "profile"), Literal.of("$.email"));

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                new ScalarExpressionProjection(jsonExists, "has_email"));

        new SelectBuilder(specFactory, select).from("users").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", JSON_EXISTS("profile", ?) AS "has_email" FROM "users"\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.email");
    }

    @Test
    void jsonExistsWithErrorBehavior() throws SQLException {
        JsonExists jsonExists =
                new JsonExists(ColumnReference.of("users", "profile"), Literal.of("$.email"), BehaviorKind.ERROR);

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                new ScalarExpressionProjection(jsonExists, "has_email"));

        new SelectBuilder(specFactory, select).from("users").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", JSON_EXISTS("profile", ? ERROR ON ERROR) AS "has_email" \
                FROM "users"\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.email");
    }

    @Test
    void jsonValueBasicUsage() throws SQLException {
        JsonValue jsonValue = new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"));

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "price"));

        new SelectBuilder(specFactory, select).from("products").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", JSON_VALUE("data", ?) AS "price" FROM "products"\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.price");
    }

    @Test
    void jsonValueWithReturningType() throws SQLException {
        JsonValue jsonValue =
                new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"), "DECIMAL(10,2)");

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "price"));

        new SelectBuilder(specFactory, select).from("products").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", JSON_VALUE("data", ? RETURNING DECIMAL(10,2)) AS "price" FROM "products"\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.price");
    }

    @Test
    void jsonValueWithDefaultBehavior() throws SQLException {
        JsonValue jsonValue = new JsonValue(
                ColumnReference.of("products", "data"),
                Literal.of("$.discount"),
                "DECIMAL(10,2)",
                OnEmptyBehavior.defaultValue("0.00"),
                BehaviorKind.NONE);

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "discount"));

        new SelectBuilder(specFactory, select).from("products").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", JSON_VALUE("data", ? RETURNING DECIMAL(10,2) DEFAULT 0.00 ON EMPTY) AS "discount" FROM "products"\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.discount");
    }

    @Test
    void jsonQueryBasicUsage() throws SQLException {
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("users", "profile"), Literal.of("$.addresses"));

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                new ScalarExpressionProjection(jsonQuery, "addresses"));

        new SelectBuilder(specFactory, select).from("users").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", JSON_QUERY("profile", ?) AS "addresses" FROM "users"\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.addresses");
    }

    @Test
    void jsonQueryWithWrapperBehavior() throws SQLException {
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("products", "data"), Literal.of("$.tags"), null, WrapperBehavior.WITH_WRAPPER);

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonQuery, "tags"));

        new SelectBuilder(specFactory, select).from("products").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", JSON_QUERY("data", ? WITH WRAPPER) AS "tags" FROM "products"\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.tags");
    }

    @Test
    void jsonQueryWithAllOptions() throws SQLException {
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("products", "data"),
                Literal.of("$.reviews"),
                "JSON",
                WrapperBehavior.WITH_CONDITIONAL_WRAPPER,
                OnEmptyBehavior.defaultValue("[]"),
                BehaviorKind.NONE);

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "id")),
                new ScalarExpressionProjection(jsonQuery, "reviews"));

        new SelectBuilder(specFactory, select).from("products").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "id", JSON_QUERY("data", ? RETURNING JSON WITH CONDITIONAL WRAPPER DEFAULT [] ON EMPTY) AS "reviews" FROM "products"\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.reviews");
    }

    @Test
    void multipleJsonFunctionsInSelect() throws SQLException {
        JsonExists jsonExists = new JsonExists(ColumnReference.of("users", "profile"), Literal.of("$.email"));

        JsonValue jsonValue = new JsonValue(ColumnReference.of("users", "profile"), Literal.of("$.age"), "INT");

        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("users", "profile"), Literal.of("$.addresses"));

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                new ScalarExpressionProjection(jsonExists, "has_email"),
                new ScalarExpressionProjection(jsonValue, "age"),
                new ScalarExpressionProjection(jsonQuery, "addresses"));

        new SelectBuilder(specFactory, select).from("users").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", JSON_EXISTS("profile", ?) AS "has_email", \
                JSON_VALUE("profile", ? RETURNING INT) AS "age", \
                JSON_QUERY("profile", ?) AS "addresses" \
                FROM "users"\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.email");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "$.age");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "$.addresses");
    }

    @Test
    void jsonFunctionWithWhereClause() throws SQLException {
        JsonValue jsonValue =
                new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"), "DECIMAL(10,2)");

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "price"));

        new SelectBuilder(specFactory, select)
                .from("products")
                .where()
                .column("category")
                .eq("Electronics")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", JSON_VALUE("data", ? RETURNING DECIMAL(10,2)) AS "price" \
                FROM "products" WHERE "category" = ?\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.price");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "Electronics");
    }

    @Test
    void jsonFunctionWithOrderBy() throws SQLException {
        JsonValue jsonValue =
                new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.rating"), "DECIMAL(3,1)");

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "rating"));

        new SelectBuilder(specFactory, select)
                .from("products")
                .orderBy()
                .asc("name")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", JSON_VALUE("data", ? RETURNING DECIMAL(3,1)) AS "rating" FROM "products" ORDER BY "name" ASC\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.rating");
    }
}
