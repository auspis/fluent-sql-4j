package lan.tlab.r4j.sql.dsl.update;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateBuilderJsonTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void updateSingleJsonColumn() {
        String jsonValue = "{\"status\":\"active\",\"lastLogin\":\"2025-11-08\"}";

        String sql = new UpdateBuilder(renderer, "users")
                .set("metadata", jsonValue)
                .where()
                .column("id")
                .eq(1)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                UPDATE "users" SET "metadata" = '{"status":"active","lastLogin":"2025-11-08"}' WHERE "users"."id" = 1""");
    }

    @Test
    void updateMultipleColumnsWithJson() {
        String preferences = "[\"notifications\",\"theme-dark\",\"auto-save\"]";

        String sql = new UpdateBuilder(renderer, "user_settings")
                .set("username", "alice")
                .set("preferences", preferences)
                .set("updated_at", "2025-11-08T10:30:00")
                .where()
                .column("user_id")
                .eq(42)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                UPDATE "user_settings" SET "username" = 'alice', "preferences" = '["notifications","theme-dark","auto-save"]', "updated_at" = '2025-11-08T10:30:00' WHERE "user_settings"."user_id" = 42""");
    }

    @Test
    void updateNestedJsonObject() {
        String profile =
                "{\"personal\":{\"firstName\":\"Mario\",\"lastName\":\"Rossi\"},\"contact\":{\"email\":\"mario@example.com\",\"phone\":\"+39123456789\"}}";

        String sql = new UpdateBuilder(renderer, "profiles")
                .set("profile_data", profile)
                .where()
                .column("profile_id")
                .eq(100)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                UPDATE "profiles" SET "profile_data" = '{"personal":{"firstName":"Mario","lastName":"Rossi"},"contact":{"email":"mario@example.com","phone":"+39123456789"}}' WHERE "profiles"."profile_id" = 100""");
    }

    @Test
    void updateJsonToNull() {
        String sql = new UpdateBuilder(renderer, "users")
                .set("metadata", (String) null)
                .where()
                .column("id")
                .eq(5)
                .build();

        assertThat(sql).isEqualTo("""
                UPDATE "users" SET "metadata" = null WHERE "users"."id" = 5""");
    }

    @Test
    void updateEmptyJsonObject() {
        String emptyJson = "{}";

        String sql = new UpdateBuilder(renderer, "documents")
                .set("properties", emptyJson)
                .where()
                .column("doc_id")
                .eq(7)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                UPDATE "documents" SET "properties" = '{}' WHERE "documents"."doc_id" = 7""");
    }

    @Test
    void updateEmptyJsonArray() {
        String emptyArray = "[]";

        String sql = new UpdateBuilder(renderer, "tags")
                .set("tag_list", emptyArray)
                .where()
                .column("item_id")
                .eq(15)
                .build();

        assertThat(sql)
                .isEqualTo("""
                UPDATE "tags" SET "tag_list" = '[]' WHERE "tags"."item_id" = 15""");
    }

    @Test
    void updateJsonWithComplexWhereConditions() {
        String config = "{\"timeout\":30,\"retries\":3,\"enabled\":true}";

        String sql = new UpdateBuilder(renderer, "api_configs")
                .set("configuration", config)
                .set("last_modified", "2025-11-08")
                .where()
                .column("api_name")
                .eq("payment-service")
                .and()
                .column("version")
                .gte("2.0")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                UPDATE "api_configs" SET "configuration" = '{"timeout":30,"retries":3,"enabled":true}', "last_modified" = '2025-11-08' WHERE ("api_configs"."api_name" = 'payment-service') AND ("api_configs"."version" >= '2.0')""");
    }
}
