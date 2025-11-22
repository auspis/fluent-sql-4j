package lan.tlab.r4j.jdsql.dsl.insert;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for InsertBuilder with JSON values.
 * Tests insertion of JSON data as string literals.
 */
class InsertBuilderJsonTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = StandardSqlRendererFactory.dialectRendererStandardSql();
    }

    @Test
    void insertJsonObjectLiteral() {
        String jsonValue = "{\"name\":\"John\",\"age\":30}";
        String sql = new InsertBuilder(renderer, "users")
                .set("id", 1)
                .set("data", jsonValue)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                INSERT INTO "users" ("users"."id", "users"."data") VALUES (1, '{"name":"John","age":30}')""");
    }

    @Test
    void insertJsonArrayLiteral() {
        String jsonArray = "[\"item1\",\"item2\",\"item3\"]";
        String sql = new InsertBuilder(renderer, "products")
                .set("id", 100)
                .set("tags", jsonArray)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                INSERT INTO "products" ("products"."id", "products"."tags") VALUES (100, '["item1","item2","item3"]')""");
    }

    @Test
    void insertNestedJsonObject() {
        String nestedJson = "{\"user\":{\"name\":\"Alice\",\"address\":{\"city\":\"NYC\"}}}";
        String sql = new InsertBuilder(renderer, "documents")
                .set("doc_id", 42)
                .set("content", nestedJson)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                INSERT INTO "documents" ("documents"."doc_id", "documents"."content") VALUES (42, '{"user":{"name":"Alice","address":{"city":"NYC"}}}')""");
    }

    @Test
    void insertMultipleColumnsWithJson() {
        String metadata = "{\"version\":\"1.0\",\"author\":\"admin\"}";
        String sql = new InsertBuilder(renderer, "articles")
                .set("title", "Test Article")
                .set("published", true)
                .set("metadata", metadata)
                .set("view_count", 0)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                INSERT INTO "articles" ("articles"."title", "articles"."published", "articles"."metadata", "articles"."view_count") VALUES ('Test Article', true, '{"version":"1.0","author":"admin"}', 0)""");
    }

    @Test
    void insertJsonWithNullValue() {
        String sql = new InsertBuilder(renderer, "settings")
                .set("key", "app_config")
                .set("value", (String) null)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                INSERT INTO "settings" ("settings"."key", "settings"."value") VALUES ('app_config', null)""");
    }

    @Test
    void insertEmptyJsonObject() {
        String emptyJson = "{}";
        String sql = new InsertBuilder(renderer, "logs")
                .set("log_id", 999)
                .set("details", emptyJson)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                INSERT INTO "logs" ("logs"."log_id", "logs"."details") VALUES (999, '{}')""");
    }

    @Test
    void insertEmptyJsonArray() {
        String emptyArray = "[]";
        String sql = new InsertBuilder(renderer, "collections")
                .set("collection_id", 5)
                .set("items", emptyArray)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                INSERT INTO "collections" ("collections"."collection_id", "collections"."items") VALUES (5, '[]')""");
    }
}
