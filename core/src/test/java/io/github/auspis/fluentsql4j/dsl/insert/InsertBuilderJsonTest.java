package io.github.auspis.fluentsql4j.dsl.insert;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.mockito.Mockito.verify;

import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for InsertBuilder with JSON values.
 * Tests insertion of JSON data as string literals.
 */
class InsertBuilderJsonTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void insertJsonObjectLiteral() throws SQLException {
        String jsonValue = "{\"name\":\"John\",\"age\":30}";
        new InsertBuilder(specFactory, "users")
                .set("id", 1)
                .set("data", jsonValue)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                INSERT INTO "users" ("id", "data") VALUES (?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 1);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, jsonValue);
    }

    @Test
    void insertJsonArrayLiteral() throws SQLException {
        String jsonArray = "[\"item1\",\"item2\",\"item3\"]";
        new InsertBuilder(specFactory, "products")
                .set("id", 100)
                .set("tags", jsonArray)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                INSERT INTO "products" ("id", "tags") VALUES (?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, jsonArray);
    }

    @Test
    void insertNestedJsonObject() throws SQLException {
        String nestedJson = "{\"user\":{\"name\":\"Alice\",\"address\":{\"city\":\"NYC\"}}}";
        new InsertBuilder(specFactory, "documents")
                .set("doc_id", 42)
                .set("content", nestedJson)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                INSERT INTO "documents" ("doc_id", "content") VALUES (?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 42);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, nestedJson);
    }

    @Test
    void insertMultipleColumnsWithJson() throws SQLException {
        String metadata = "{\"version\":\"1.0\",\"author\":\"admin\"}";
        new InsertBuilder(specFactory, "articles")
                .set("title", "Test Article")
                .set("published", true)
                .set("metadata", metadata)
                .set("view_count", 0)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                INSERT INTO "articles" ("title", "published", "metadata", "view_count") VALUES (?, ?, ?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "Test Article");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, true);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, metadata);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 0);
    }

    @Test
    void insertJsonWithNullValue() throws SQLException {
        new InsertBuilder(specFactory, "settings")
                .set("key", "app_config")
                .set("value", (String) null)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                INSERT INTO "settings" ("key", "value") VALUES (?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "app_config");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, null);
    }

    @Test
    void insertEmptyJsonObject() throws SQLException {
        String emptyJson = "{}";
        new InsertBuilder(specFactory, "logs")
                .set("log_id", 999)
                .set("details", emptyJson)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                INSERT INTO "logs" ("log_id", "details") VALUES (?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 999);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, emptyJson);
    }

    @Test
    void insertEmptyJsonArray() throws SQLException {
        String emptyArray = "[]";
        new InsertBuilder(specFactory, "collections")
                .set("collection_id", 5)
                .set("items", emptyArray)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                INSERT INTO "collections" ("collection_id", "items") VALUES (?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 5);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, emptyArray);
    }
}
