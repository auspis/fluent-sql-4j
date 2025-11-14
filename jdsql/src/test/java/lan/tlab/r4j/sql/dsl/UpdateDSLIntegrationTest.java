package lan.tlab.r4j.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import lan.tlab.r4j.sql.util.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class UpdateDSLIntegrationTest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void createsUpdateBuilderWithRenderer() {
        String result = dsl.update("users")
                .set("name", "John")
                .where()
                .column("id")
                .eq(1)
                .build();

        assertThat(result).isEqualTo("""
                UPDATE "users" SET "name" = 'John' WHERE "users"."id" = 1""");
    }

    @Test
    void appliesRendererQuoting() {
        String result = dsl.update("temp_table").set("status", "active").build();

        assertThat(result).isEqualTo("""
                UPDATE "temp_table" SET "status" = 'active'""");
    }

    @Test
    void fluentApiWithComplexConditions() {
        String result = dsl.update("products")
                .set("stock", 0)
                .set("discontinued", true)
                .where()
                .column("last_order_date")
                .lt("2023-01-01")
                .and()
                .column("quantity")
                .eq(0)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                UPDATE "products" SET "stock" = 0, "discontinued" = true WHERE ("products"."last_order_date" < '2023-01-01') AND ("products"."quantity" = 0)""");
    }

    @Test
    void updateJsonColumn() {
        String settings = "{\"theme\":\"dark\",\"language\":\"it\",\"notifications\":true}";

        String result = dsl.update("user_preferences")
                .set("settings", settings)
                .where()
                .column("user_id")
                .eq(42)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                UPDATE "user_preferences" SET "settings" = '{"theme":"dark","language":"it","notifications":true}' WHERE "user_preferences"."user_id" = 42""");
    }

    @Test
    void updateMultipleColumnsIncludingJson() {
        String profile = "{\"bio\":\"Software Engineer\",\"skills\":[\"Java\",\"SQL\",\"Git\"]}";
        String contacts = "{\"email\":\"dev@example.com\",\"linkedin\":\"linkedin.com/in/dev\"}";

        String result = dsl.update("developers")
                .set("full_name", "Marco Bianchi")
                .set("profile_json", profile)
                .set("contact_info", contacts)
                .set("is_available", true)
                .set("updated_at", "2025-11-08T12:00:00")
                .where()
                .column("developer_id")
                .eq(100)
                .and()
                .column("status")
                .eq("active")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                UPDATE "developers" SET "full_name" = 'Marco Bianchi', "profile_json" = '{"bio":"Software Engineer","skills":["Java","SQL","Git"]}', "contact_info" = '{"email":"dev@example.com","linkedin":"linkedin.com/in/dev"}', "is_available" = true, "updated_at" = '2025-11-08T12:00:00' WHERE ("developers"."developer_id" = 100) AND ("developers"."status" = 'active')""");
    }
}
