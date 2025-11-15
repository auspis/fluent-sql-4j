package lan.tlab.r4j.jdsql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.jdsql.test.util.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class InsertDSLIntegrationTest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void createsInsertBuilderWithRenderer() {
        String result = dsl.insertInto("users").set("name", "John").build();

        assertThat(result).isEqualTo("""
                INSERT INTO "users" ("users"."name") VALUES ('John')""");
    }

    @Test
    void appliesRendererQuoting() {
        String result = dsl.insertInto("temp_table").defaultValues().build();

        assertThat(result).isEqualTo("""
                INSERT INTO "temp_table" DEFAULT VALUES""");
    }

    @Test
    void fluentApiWithMultipleColumns() {
        String result = dsl.insertInto("products")
                .set("id", 1)
                .set("name", "Widget")
                .set("price", 19.99)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                INSERT INTO "products" ("products"."id", "products"."name", "products"."price") VALUES (1, 'Widget', 19.99)""");
    }

    @Test
    void insertJsonObject() {
        String jsonData = "{\"name\":\"Alice\",\"email\":\"alice@example.com\",\"age\":28}";

        String result =
                dsl.insertInto("users").set("id", 42).set("metadata", jsonData).build();

        assertThat(result)
                .isEqualTo(
                        """
                INSERT INTO "users" ("users"."id", "users"."metadata") VALUES (42, '{"name":"Alice","email":"alice@example.com","age":28}')""");
    }

    @Test
    void insertComplexJsonWithMultipleColumns() {
        String preferences = "[\"email\",\"sms\",\"push\"]";
        String profile = "{\"bio\":\"Developer\",\"location\":{\"city\":\"Rome\",\"country\":\"Italy\"}}";

        String result = dsl.insertInto("user_profiles")
                .set("user_id", 100)
                .set("username", "dev_user")
                .set("preferences", preferences)
                .set("profile_data", profile)
                .set("is_active", true)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                INSERT INTO "user_profiles" ("user_profiles"."user_id", "user_profiles"."username", "user_profiles"."preferences", "user_profiles"."profile_data", "user_profiles"."is_active") VALUES (100, 'dev_user', '["email","sms","push"]', '{"bio":"Developer","location":{"city":"Rome","country":"Italy"}}', true)""");
    }
}
