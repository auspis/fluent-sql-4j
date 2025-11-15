package lan.tlab.r4j.jdsql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonExists;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.OnEmptyBehavior;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.WrapperBehavior;
import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.select.SelectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderJsonTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void jsonExistsBasicUsage() {
        JsonExists jsonExists = new JsonExists(ColumnReference.of("users", "profile"), Literal.of("$.email"));

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                new ScalarExpressionProjection(jsonExists, "has_email"));

        String result = new SelectBuilder(renderer, select).from("users").build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "users"."name", JSON_EXISTS("users"."profile", '$.email') AS has_email FROM "users"\
                """);
    }

    @Test
    void jsonExistsWithErrorBehavior() {
        JsonExists jsonExists =
                new JsonExists(ColumnReference.of("users", "profile"), Literal.of("$.email"), BehaviorKind.ERROR);

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                new ScalarExpressionProjection(jsonExists, "has_email"));

        String result = new SelectBuilder(renderer, select).from("users").build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "users"."name", JSON_EXISTS("users"."profile", '$.email' ERROR ON ERROR) AS has_email FROM "users"\
                """);
    }

    @Test
    void jsonValueBasicUsage() {
        JsonValue jsonValue = new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"));

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "price"));

        String result = new SelectBuilder(renderer, select).from("products").build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."name", JSON_VALUE("products"."data", '$.price') AS price FROM "products"\
                """);
    }

    @Test
    void jsonValueWithReturningType() {
        JsonValue jsonValue =
                new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"), "DECIMAL(10,2)");

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "price"));

        String result = new SelectBuilder(renderer, select).from("products").build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."name", JSON_VALUE("products"."data", '$.price' RETURNING DECIMAL(10,2)) AS price FROM "products"\
                """);
    }

    @Test
    void jsonValueWithDefaultBehavior() {
        JsonValue jsonValue = new JsonValue(
                ColumnReference.of("products", "data"),
                Literal.of("$.discount"),
                "DECIMAL(10,2)",
                OnEmptyBehavior.defaultValue("0.00"),
                BehaviorKind.NONE);

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "discount"));

        String result = new SelectBuilder(renderer, select).from("products").build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."name", JSON_VALUE("products"."data", '$.discount' RETURNING DECIMAL(10,2) DEFAULT 0.00 ON EMPTY) AS discount FROM "products"\
                """);
    }

    @Test
    void jsonQueryBasicUsage() {
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("users", "profile"), Literal.of("$.addresses"));

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                new ScalarExpressionProjection(jsonQuery, "addresses"));

        String result = new SelectBuilder(renderer, select).from("users").build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "users"."name", JSON_QUERY("users"."profile", '$.addresses') AS addresses FROM "users"\
                """);
    }

    @Test
    void jsonQueryWithWrapperBehavior() {
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("products", "data"), Literal.of("$.tags"), null, WrapperBehavior.WITH_WRAPPER);

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonQuery, "tags"));

        String result = new SelectBuilder(renderer, select).from("products").build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."name", JSON_QUERY("products"."data", '$.tags' WITH WRAPPER) AS tags FROM "products"\
                """);
    }

    @Test
    void jsonQueryWithAllOptions() {
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

        String result = new SelectBuilder(renderer, select).from("products").build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."id", JSON_QUERY("products"."data", '$.reviews' RETURNING JSON WITH CONDITIONAL WRAPPER DEFAULT [] ON EMPTY) AS reviews FROM "products"\
                """);
    }

    @Test
    void multipleJsonFunctionsInSelect() {
        JsonExists jsonExists = new JsonExists(ColumnReference.of("users", "profile"), Literal.of("$.email"));

        JsonValue jsonValue = new JsonValue(ColumnReference.of("users", "profile"), Literal.of("$.age"), "INT");

        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("users", "profile"), Literal.of("$.addresses"));

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                new ScalarExpressionProjection(jsonExists, "has_email"),
                new ScalarExpressionProjection(jsonValue, "age"),
                new ScalarExpressionProjection(jsonQuery, "addresses"));

        String result = new SelectBuilder(renderer, select).from("users").build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "users"."name", JSON_EXISTS("users"."profile", '$.email') AS has_email, JSON_VALUE("users"."profile", '$.age' RETURNING INT) AS age, JSON_QUERY("users"."profile", '$.addresses') AS addresses FROM "users"\
                """);
    }

    @Test
    void jsonFunctionWithWhereClause() {
        JsonValue jsonValue =
                new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"), "DECIMAL(10,2)");

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "price"));

        String result = new SelectBuilder(renderer, select)
                .from("products")
                .where()
                .column("category")
                .eq("Electronics")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."name", JSON_VALUE("products"."data", '$.price' RETURNING DECIMAL(10,2)) AS price FROM "products" WHERE "products"."category" = 'Electronics'\
                """);
    }

    @Test
    void jsonFunctionWithOrderBy() {
        JsonValue jsonValue =
                new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.rating"), "DECIMAL(3,1)");

        Select select = Select.of(
                new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                new ScalarExpressionProjection(jsonValue, "rating"));

        String result = new SelectBuilder(renderer, select)
                .from("products")
                .orderBy("name")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."name", JSON_VALUE("products"."data", '$.rating' RETURNING DECIMAL(3,1)) AS rating FROM "products" ORDER BY "products"."name" ASC\
                """);
    }
}
