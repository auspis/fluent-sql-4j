package lan.tlab.r4j.sql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhereJsonFunctionBuilderTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void jsonValueStringComparison() {
        // SELECT * FROM users WHERE JSON_VALUE(info, '$.city') = 'Rome'
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .jsonValue("info", "$.city")
                .eq("Rome")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "users" WHERE JSON_VALUE("users"."info", '$.city') = 'Rome'""");
    }

    @Test
    void jsonValueNumberComparison() {
        // SELECT * FROM users WHERE JSON_VALUE(info, '$.age') > 30
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .jsonValue("info", "$.age")
                .gt(30)
                .build();

        assertThat(sql)
                .isEqualTo("""
                SELECT * FROM "users" WHERE JSON_VALUE("users"."info", '$.age') > 30""");
    }

    @Test
    void jsonExistsCondition() {
        // SELECT * FROM users WHERE JSON_EXISTS(info, '$.email') = true
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .jsonExists("info", "$.email")
                .exists()
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "users" WHERE JSON_EXISTS("users"."info", '$.email') = true""");
    }

    @Test
    void jsonNotExistsCondition() {
        // SELECT * FROM users WHERE JSON_EXISTS(info, '$.phone') = false
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .jsonExists("info", "$.phone")
                .notExists()
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "users" WHERE JSON_EXISTS("users"."info", '$.phone') = false""");
    }

    @Test
    void jsonQueryIsNotNull() {
        // SELECT * FROM products WHERE JSON_QUERY(data, '$.tags') IS NOT NULL
        String sql = new SelectBuilder(renderer, "*")
                .from("products")
                .where()
                .jsonQuery("data", "$.tags")
                .isNotNull()
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "products" WHERE JSON_QUERY("products"."data", '$.tags') IS NOT NULL""");
    }

    @Test
    void jsonValueWithMultipleConditions() {
        // SELECT * FROM users WHERE JSON_VALUE(info, '$.city') = 'Rome' AND active = true
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .jsonValue("info", "$.city")
                .eq("Rome")
                .and()
                .column("active")
                .eq(true)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "users" WHERE (JSON_VALUE("users"."info", '$.city') = 'Rome') AND ("users"."active" = true)""");
    }

    @Test
    void jsonValueWithOrCondition() {
        // SELECT * FROM users WHERE JSON_VALUE(info, '$.status') = 'vip' OR JSON_VALUE(info, '$.status') = 'premium'
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .jsonValue("info", "$.status")
                .eq("vip")
                .or()
                .jsonValue("info", "$.status")
                .eq("premium")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "users" WHERE (JSON_VALUE("users"."info", '$.status') = 'vip') OR (JSON_VALUE("users"."info", '$.status') = 'premium')""");
    }

    @Test
    void jsonMixedConditionsNormalAndJson() {
        // Mix normal columns and JSON functions
        String sql = new SelectBuilder(renderer, "*")
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
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "orders" WHERE (("orders"."status" = 'completed') AND (JSON_VALUE("orders"."data", '$.amount') >= 100)) AND (JSON_EXISTS("orders"."data", '$.customer.email') = true)""");
    }

    @Test
    void jsonValueIsNull() {
        // SELECT * FROM products WHERE JSON_VALUE(data, '$.discount') IS NULL
        String sql = new SelectBuilder(renderer, "*")
                .from("products")
                .where()
                .jsonValue("data", "$.discount")
                .isNull()
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "products" WHERE JSON_VALUE("products"."data", '$.discount') IS NULL""");
    }

    @Test
    void jsonValueWithTableAlias() {
        // SELECT * FROM users AS u WHERE JSON_VALUE(u.info, '$.city') = 'Milan'
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .as("u")
                .where()
                .jsonValue("u", "info", "$.city")
                .eq("Milan")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "users" AS u WHERE JSON_VALUE("u"."info", '$.city') = 'Milan'""");
    }
}
