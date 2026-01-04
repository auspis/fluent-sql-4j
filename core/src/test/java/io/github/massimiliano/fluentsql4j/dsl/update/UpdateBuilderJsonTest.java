package io.github.massimiliano.fluentsql4j.dsl.update;

import static io.github.massimiliano.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.mockito.Mockito.verify;

import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.massimiliano.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateBuilderJsonTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void updateSingleJsonColumn() throws SQLException {
        String jsonValue = "{\"status\":\"active\",\"lastLogin\":\"2025-11-08\"}";

        new UpdateBuilder(specFactory, "users")
                .set("metadata", jsonValue)
                .where()
                .column("id")
                .eq(1)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                UPDATE "users" SET "metadata" = ? WHERE "id" = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, jsonValue);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 1);
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
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                UPDATE "user_settings" SET "username" = ?, "preferences" = ?, "updated_at" = ? \
                WHERE "user_id" = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "alice");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, preferences);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "2025-11-08T10:30:00");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 42);
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
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                UPDATE "profiles" SET "profile_data" = ? WHERE "profile_id" = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, profile);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 100);
    }

    @Test
    void updateJsonToNull() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("metadata", (String) null)
                .where()
                .column("id")
                .eq(5)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                UPDATE "users" SET "metadata" = ? WHERE "id" = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, null);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 5);
    }

    @Test
    void updateEmptyJsonObject() throws SQLException {
        String emptyJson = "{}";

        new UpdateBuilder(specFactory, "documents")
                .set("properties", emptyJson)
                .where()
                .column("doc_id")
                .eq(7)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                UPDATE "documents" SET "properties" = ? WHERE "doc_id" = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, emptyJson);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 7);
    }

    @Test
    void updateEmptyJsonArray() throws SQLException {
        String emptyArray = "[]";

        new UpdateBuilder(specFactory, "tags")
                .set("tag_list", emptyArray)
                .where()
                .column("item_id")
                .eq(15)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                UPDATE "tags" SET "tag_list" = ? WHERE "item_id" = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, emptyArray);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 15);
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
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                UPDATE "api_configs" SET "configuration" = ?, "last_modified" = ? WHERE ("api_name" = ?) AND ("version" >= ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, config);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "2025-11-08");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "payment-service");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "2.0");
    }
}
