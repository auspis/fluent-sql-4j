package lan.tlab.r4j.sql.dsl.insert;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
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
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void insertJsonObjectLiteral() {
        String jsonValue = "{\"name\":\"John\",\"age\":30}";
        String sql = new InsertBuilder(renderer, "users")
                .set("id", 1)
                .set("data", jsonValue)
                .build();

        assertThat(sql)
                .contains("INSERT INTO \"users\"")
                .contains("\"users\".\"id\", \"users\".\"data\"")
                .contains("VALUES (1, '{\"name\":\"John\",\"age\":30}')");
    }

    @Test
    void insertJsonArrayLiteral() {
        String jsonArray = "[\"item1\",\"item2\",\"item3\"]";
        String sql = new InsertBuilder(renderer, "products")
                .set("id", 100)
                .set("tags", jsonArray)
                .build();

        assertThat(sql)
                .contains("INSERT INTO \"products\"")
                .contains("\"products\".\"id\", \"products\".\"tags\"")
                .contains("VALUES (100, '[\"item1\",\"item2\",\"item3\"]')");
    }

    @Test
    void insertNestedJsonObject() {
        String nestedJson = "{\"user\":{\"name\":\"Alice\",\"address\":{\"city\":\"NYC\"}}}";
        String sql = new InsertBuilder(renderer, "documents")
                .set("doc_id", 42)
                .set("content", nestedJson)
                .build();

        assertThat(sql)
                .contains("INSERT INTO \"documents\"")
                .contains("\"documents\".\"doc_id\", \"documents\".\"content\"")
                .contains("VALUES (42,")
                .contains("{\"user\":{\"name\":\"Alice\",\"address\":{\"city\":\"NYC\"}}}");
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
                .contains("INSERT INTO \"articles\"")
                .contains("\"articles\".\"title\"")
                .contains("\"articles\".\"published\"")
                .contains("\"articles\".\"metadata\"")
                .contains("\"articles\".\"view_count\"")
                .contains("VALUES ('Test Article', true, '{\"version\":\"1.0\",\"author\":\"admin\"}', 0)");
    }

    @Test
    void insertJsonWithNullValue() {
        String sql = new InsertBuilder(renderer, "settings")
                .set("key", "app_config")
                .set("value", (String) null)
                .build();

        assertThat(sql)
                .contains("INSERT INTO \"settings\"")
                .contains("\"settings\".\"key\", \"settings\".\"value\"")
                .contains("VALUES ('app_config', null)");
    }

    @Test
    void insertEmptyJsonObject() {
        String emptyJson = "{}";
        String sql = new InsertBuilder(renderer, "logs")
                .set("log_id", 999)
                .set("details", emptyJson)
                .build();

        assertThat(sql)
                .contains("INSERT INTO \"logs\"")
                .contains("\"logs\".\"log_id\", \"logs\".\"details\"")
                .contains("VALUES (999, '{}')");
    }

    @Test
    void insertEmptyJsonArray() {
        String emptyArray = "[]";
        String sql = new InsertBuilder(renderer, "collections")
                .set("collection_id", 5)
                .set("items", emptyArray)
                .build();

        assertThat(sql)
                .contains("INSERT INTO \"collections\"")
                .contains("\"collections\".\"collection_id\", \"collections\".\"items\"")
                .contains("VALUES (5, '[]')");
    }
}
