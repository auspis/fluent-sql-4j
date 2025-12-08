package lan.tlab.r4j.jdsql.dsl.update;

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

class UpdateBuilderJsonTest {

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
    void updateSingleJsonColumn() throws SQLException {
        String jsonValue = "{\"status\":\"active\",\"lastLogin\":\"2025-11-08\"}";

        new UpdateBuilder(specFactory, "users")
                .set("metadata", jsonValue)
                .where()
                .column("id")
                .eq(1)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                UPDATE "users" SET "metadata" = ? WHERE "id" = ?""");
        verify(ps).setObject(1, jsonValue);
        verify(ps).setObject(2, 1);
    }

    @Test
    void updateMultipleColumnsWithJson() throws SQLException {
        String preferences = "[\"notifications\",\"theme-dark\",\"auto-save\"]";

        new UpdateBuilder(specFactory, "user_settings")
                .set("username", "alice")
                .set("preferences", preferences)
                .set("updated_at", "2025-11-08T10:30:00")
                .where()
                .column("user_id")
                .eq(42)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                UPDATE "user_settings" SET "username" = ?, "preferences" = ?, "updated_at" = ? \
                WHERE "user_id" = ?""");
        verify(ps).setObject(1, "alice");
        verify(ps).setObject(2, preferences);
        verify(ps).setObject(3, "2025-11-08T10:30:00");
        verify(ps).setObject(4, 42);
    }

    @Test
    void updateNestedJsonObject() throws SQLException {
        String profile =
                "{\"personal\":{\"firstName\":\"Mario\",\"lastName\":\"Rossi\"},\"contact\":{\"email\":\"mario@example.com\",\"phone\":\"+39123456789\"}}";

        new UpdateBuilder(specFactory, "profiles")
                .set("profile_data", profile)
                .where()
                .column("profile_id")
                .eq(100)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                UPDATE "profiles" SET "profile_data" = ? WHERE "profile_id" = ?""");
        verify(ps).setObject(1, profile);
        verify(ps).setObject(2, 100);
    }

    @Test
    void updateJsonToNull() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("metadata", (String) null)
                .where()
                .column("id")
                .eq(5)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                UPDATE "users" SET "metadata" = ? WHERE "id" = ?""");
        verify(ps).setObject(1, null);
        verify(ps).setObject(2, 5);
    }

    @Test
    void updateEmptyJsonObject() throws SQLException {
        String emptyJson = "{}";

        new UpdateBuilder(specFactory, "documents")
                .set("properties", emptyJson)
                .where()
                .column("doc_id")
                .eq(7)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                UPDATE "documents" SET "properties" = ? WHERE "doc_id" = ?""");
        verify(ps).setObject(1, emptyJson);
        verify(ps).setObject(2, 7);
    }

    @Test
    void updateEmptyJsonArray() throws SQLException {
        String emptyArray = "[]";

        new UpdateBuilder(specFactory, "tags")
                .set("tag_list", emptyArray)
                .where()
                .column("item_id")
                .eq(15)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                UPDATE "tags" SET "tag_list" = ? WHERE "item_id" = ?""");
        verify(ps).setObject(1, emptyArray);
        verify(ps).setObject(2, 15);
    }

    @Test
    void updateJsonWithComplexWhereConditions() throws SQLException {
        String config = "{\"timeout\":30,\"retries\":3,\"enabled\":true}";

        new UpdateBuilder(specFactory, "api_configs")
                .set("configuration", config)
                .set("last_modified", "2025-11-08")
                .where()
                .column("api_name")
                .eq("payment-service")
                .and()
                .column("version")
                .gte("2.0")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                UPDATE "api_configs" SET "configuration" = ?, "last_modified" = ? WHERE ("api_name" = ?) AND ("version" >= ?)""");
        verify(ps).setObject(1, config);
        verify(ps).setObject(2, "2025-11-08");
        verify(ps).setObject(3, "payment-service");
        verify(ps).setObject(4, "2.0");
    }
}
