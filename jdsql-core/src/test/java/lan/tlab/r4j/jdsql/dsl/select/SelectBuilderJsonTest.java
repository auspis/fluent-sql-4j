package lan.tlab.r4j.jdsql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonExists;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.OnEmptyBehavior;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.WrapperBehavior;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SelectBuilderJsonTest {

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
    void jsonExistsBasicUsage() throws SQLException {
        JsonExists jsonExists = new JsonExists(ColumnReference.of("users", "profile"), Literal.of("$.email"));

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                new ScalarExpressionProjection(jsonExists, "has_email"));

        new SelectBuilder(specFactory, select).from("users").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "name", JSON_EXISTS("profile", ?) AS "has_email" FROM "users"\
                """);
        verify(ps).setObject(1, "$.email");
    }

    @Test
    void jsonExistsWithErrorBehavior() throws SQLException {
        JsonExists jsonExists =
                new JsonExists(ColumnReference.of("users", "profile"), Literal.of("$.email"), BehaviorKind.ERROR);

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                new ScalarExpressionProjection(jsonExists, "has_email"));

        new SelectBuilder(specFactory, select).from("users").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "name", JSON_EXISTS("profile", ? ERROR ON ERROR) AS "has_email" \
                FROM "users"\
                """);
        verify(ps).setObject(1, "$.email");
    }

    @Test
    void jsonValueBasicUsage() throws SQLException {
        JsonValue jsonValue = new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"));

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "price"));

        new SelectBuilder(specFactory, select).from("products").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "name", JSON_VALUE("data", ?) AS "price" FROM "products"\
                """);
        verify(ps).setObject(1, "$.price");
    }

    @Test
    void jsonValueWithReturningType() throws SQLException {
        JsonValue jsonValue =
                new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"), "DECIMAL(10,2)");

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "price"));

        new SelectBuilder(specFactory, select).from("products").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "name", JSON_VALUE("data", ? RETURNING DECIMAL(10,2)) AS "price" FROM "products"\
                """);
        verify(ps).setObject(1, "$.price");
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

        new SelectBuilder(specFactory, select).from("products").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "name", JSON_VALUE("data", ? RETURNING DECIMAL(10,2) DEFAULT 0.00 ON EMPTY) AS "discount" FROM "products"\
                """);
        verify(ps).setObject(1, "$.discount");
    }

    @Test
    void jsonQueryBasicUsage() throws SQLException {
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("users", "profile"), Literal.of("$.addresses"));

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                new ScalarExpressionProjection(jsonQuery, "addresses"));

        new SelectBuilder(specFactory, select).from("users").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "name", JSON_QUERY("profile", ?) AS "addresses" FROM "users"\
                """);
        verify(ps).setObject(1, "$.addresses");
    }

    @Test
    void jsonQueryWithWrapperBehavior() throws SQLException {
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("products", "data"), Literal.of("$.tags"), null, WrapperBehavior.WITH_WRAPPER);

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonQuery, "tags"));

        new SelectBuilder(specFactory, select).from("products").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "name", JSON_QUERY("data", ? WITH WRAPPER) AS "tags" FROM "products"\
                """);
        verify(ps).setObject(1, "$.tags");
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

        new SelectBuilder(specFactory, select).from("products").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "id", JSON_QUERY("data", ? RETURNING JSON WITH CONDITIONAL WRAPPER DEFAULT [] ON EMPTY) AS "reviews" FROM "products"\
                """);
        verify(ps).setObject(1, "$.reviews");
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

        new SelectBuilder(specFactory, select).from("users").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "name", JSON_EXISTS("profile", ?) AS "has_email", \
                JSON_VALUE("profile", ? RETURNING INT) AS "age", \
                JSON_QUERY("profile", ?) AS "addresses" \
                FROM "users"\
                """);
        verify(ps).setObject(1, "$.email");
        verify(ps).setObject(2, "$.age");
        verify(ps).setObject(3, "$.addresses");
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
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "name", JSON_VALUE("data", ? RETURNING DECIMAL(10,2)) AS "price" \
                FROM "products" WHERE "category" = ?\
                """);
        verify(ps).setObject(1, "$.price");
        verify(ps).setObject(2, "Electronics");
    }

    @Test
    void jsonFunctionWithOrderBy() throws SQLException {
        JsonValue jsonValue =
                new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.rating"), "DECIMAL(3,1)");

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "rating"));

        new SelectBuilder(specFactory, select).from("products").orderBy("name").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "name", JSON_VALUE("data", ? RETURNING DECIMAL(3,1)) AS "rating" FROM "products" ORDER BY "name" ASC\
                """);
        verify(ps).setObject(1, "$.rating");
    }
}
