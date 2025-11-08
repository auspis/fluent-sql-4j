package lan.tlab.r4j.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.OnEmptyBehavior;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import lan.tlab.r4j.sql.util.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class SelectDSLIntegrationTest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void createsSelectBuilderWithRenderer() {
        String result = dsl.select("name", "email").from("users").build();

        assertThat(result).isEqualTo("""
                SELECT "users"."name", "users"."email" FROM "users\"""");
    }

    @Test
    void appliesRendererQuoting() {
        String result = dsl.select("id", "value").from("temp_table").build();

        assertThat(result)
                .isEqualTo("""
                SELECT "temp_table"."id", "temp_table"."value" FROM "temp_table\"""");
    }

    @Test
    void fluentApiWithComplexQuery() {
        String result = dsl.select("name", "age")
                .from("users")
                .where("age")
                .gt(18)
                .and("active")
                .eq(true)
                .orderBy("name")
                .fetch(10)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "users"."name", "users"."age" \
                FROM "users" \
                WHERE ("users"."age" > 18) AND ("users"."active" = true) \
                ORDER BY "users"."name" ASC \
                OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY""");
    }

    @Test
    void jsonFunctionsWithFluentApi() {
        String result = dsl.select()
                .column("products", "name")
                .jsonExists("products", "metadata", "$.tags")
                .as("has_tags")
                .jsonValue("products", "metadata", "$.price")
                .returning("DECIMAL(10,2)")
                .as("price")
                .jsonQuery("products", "metadata", "$.details")
                .as("details")
                .from("products")
                .where("category")
                .eq("Electronics")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."name", \
                JSON_EXISTS("products"."metadata", '$.tags') AS has_tags, \
                JSON_VALUE("products"."metadata", '$.price' RETURNING DECIMAL(10,2)) AS price, \
                JSON_QUERY("products"."metadata", '$.details') AS details \
                FROM "products" \
                WHERE "products"."category" = 'Electronics'\
                """);
    }

    @Test
    void jsonValueWithDefaultBehaviorFluentApi() {
        String result = dsl.select()
                .column("products", "id")
                .column("products", "name")
                .jsonValue("products", "data", "$.discount")
                .returning("DECIMAL(5,2)")
                .defaultOnEmpty("0.00")
                .as("discount")
                .from("products")
                .orderBy("name")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."id", "products"."name", \
                JSON_VALUE("products"."data", '$.discount' RETURNING DECIMAL(5,2) DEFAULT 0.00 ON EMPTY) AS discount \
                FROM "products" \
                ORDER BY "products"."name" ASC\
                """);
    }

    @Test
    void jsonFunctionsUsingExpressionApi() {
        // Test the low-level expression API for custom JSON configurations
        JsonValue jsonDiscount = new JsonValue(
                ColumnReference.of("products", "data"),
                Literal.of("$.discount"),
                "DECIMAL(5,2)",
                OnEmptyBehavior.defaultValue("0.00"),
                null);

        String result = dsl.select()
                .column("products", "name")
                .expression(jsonDiscount, "discount")
                .from("products")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."name", \
                JSON_VALUE("products"."data", '$.discount' RETURNING DECIMAL(5,2) DEFAULT 0.00 ON EMPTY) AS discount \
                FROM "products"\
                """);
    }
}
