package lan.tlab.r4j.jdsql.dsl.insert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Unit tests for InsertBuilder with JSON values.
 * Tests insertion of JSON data as string literals.
 */
class InsertBuilderJsonTest {

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
    void insertJsonObjectLiteral() throws SQLException {
        String jsonValue = "{\"name\":\"John\",\"age\":30}";
        new InsertBuilder(specFactory, "users")
                .set("id", 1)
                .set("data", jsonValue)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                INSERT INTO "users" ("id", "data") VALUES (?, ?)""");
        verify(ps).setObject(1, 1);
        verify(ps).setObject(2, jsonValue);
    }

    @Test
    void insertJsonArrayLiteral() throws SQLException {
        String jsonArray = "[\"item1\",\"item2\",\"item3\"]";
        new InsertBuilder(specFactory, "products")
                .set("id", 100)
                .set("tags", jsonArray)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                INSERT INTO "products" ("id", "tags") VALUES (?, ?)""");
        verify(ps).setObject(1, 100);
        verify(ps).setObject(2, jsonArray);
    }

    @Test
    void insertNestedJsonObject() throws SQLException {
        String nestedJson = "{\"user\":{\"name\":\"Alice\",\"address\":{\"city\":\"NYC\"}}}";
        new InsertBuilder(specFactory, "documents")
                .set("doc_id", 42)
                .set("content", nestedJson)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                INSERT INTO "documents" ("doc_id", "content") VALUES (?, ?)""");
        verify(ps).setObject(1, 42);
        verify(ps).setObject(2, nestedJson);
    }

    @Test
    void insertMultipleColumnsWithJson() throws SQLException {
        String metadata = "{\"version\":\"1.0\",\"author\":\"admin\"}";
        new InsertBuilder(specFactory, "articles")
                .set("title", "Test Article")
                .set("published", true)
                .set("metadata", metadata)
                .set("view_count", 0)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                INSERT INTO "articles" ("title", "published", "metadata", "view_count") VALUES (?, ?, ?, ?)""");
        verify(ps).setObject(1, "Test Article");
        verify(ps).setObject(2, true);
        verify(ps).setObject(3, metadata);
        verify(ps).setObject(4, 0);
    }

    @Test
    void insertJsonWithNullValue() throws SQLException {
        new InsertBuilder(specFactory, "settings")
                .set("key", "app_config")
                .set("value", (String) null)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                INSERT INTO "settings" ("key", "value") VALUES (?, ?)""");
        verify(ps).setObject(1, "app_config");
        verify(ps).setObject(2, null);
    }

    @Test
    void insertEmptyJsonObject() throws SQLException {
        String emptyJson = "{}";
        new InsertBuilder(specFactory, "logs")
                .set("log_id", 999)
                .set("details", emptyJson)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                INSERT INTO "logs" ("log_id", "details") VALUES (?, ?)""");
        verify(ps).setObject(1, 999);
        verify(ps).setObject(2, emptyJson);
    }

    @Test
    void insertEmptyJsonArray() throws SQLException {
        String emptyArray = "[]";
        new InsertBuilder(specFactory, "collections")
                .set("collection_id", 5)
                .set("items", emptyArray)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                INSERT INTO "collections" ("collection_id", "items") VALUES (?, ?)""");
        verify(ps).setObject(1, 5);
        verify(ps).setObject(2, emptyArray);
    }
}
