package io.github.auspis.fluentsql4j.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.auspis.fluentsql4j.test.util.annotation.ComponentTest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@ComponentTest
class UpdateDSLComponentTest {

    private DSL dsl;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        dsl = StandardSqlUtil.dsl();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
    }

    @Test
    void createsUpdateBuilderWithPreparedStatementSpecFactory() throws SQLException {
        dsl.update("users").set("name", "John").where().column("id").eq(1).build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                UPDATE "users" SET "name" = ? WHERE "id" = ?""");
        verify(ps).setObject(1, "John");
        verify(ps).setObject(2, 1);
    }

    @Test
    void appliesPreparedStatementSpecFactoryQuoting() throws SQLException {
        dsl.update("temp_table").set("status", "active").build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                UPDATE "temp_table" SET "status" = ?""");
        verify(ps).setObject(1, "active");
    }

    @Test
    void fluentApiWithComplexConditions() throws SQLException {
        dsl.update("products")
                .set("stock", 0)
                .set("discontinued", true)
                .where()
                .column("last_order_date")
                .lt("2023-01-01")
                .and()
                .column("quantity")
                .eq(0)
                .build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                UPDATE "products" SET "stock" = ?, "discontinued" = ? WHERE ("last_order_date" < ?) AND ("quantity" = ?)""");
        verify(ps).setObject(1, 0);
        verify(ps).setObject(2, true);
        verify(ps).setObject(3, "2023-01-01");
        verify(ps).setObject(4, 0);
    }

    @Test
    void updateJsonColumn() throws SQLException {
        String settings = "{\"theme\":\"dark\",\"language\":\"it\",\"notifications\":true}";

        dsl.update("user_preferences")
                .set("settings", settings)
                .where()
                .column("user_id")
                .eq(42)
                .build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                UPDATE "user_preferences" SET "settings" = ? WHERE "user_id" = ?""");
        verify(ps).setObject(1, settings);
        verify(ps).setObject(2, 42);
    }

    @Test
    void updateMultipleColumnsIncludingJson() throws SQLException {
        String profile = "{\"bio\":\"Software Engineer\",\"skills\":[\"Java\",\"SQL\",\"Git\"]}";
        String contacts = "{\"email\":\"dev@example.com\",\"linkedin\":\"linkedin.com/in/dev\"}";

        dsl.update("developers")
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
                .build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                UPDATE "developers" SET "full_name" = ?, "profile_json" = ?, "contact_info" = ?, "is_available" = ?, "updated_at" = ? WHERE ("developer_id" = ?) AND ("status" = ?)""");
        verify(ps).setObject(1, "Marco Bianchi");
        verify(ps).setObject(2, profile);
        verify(ps).setObject(3, contacts);
        verify(ps).setObject(4, true);
        verify(ps).setObject(5, "2025-11-08T12:00:00");
        verify(ps).setObject(6, 100);
        verify(ps).setObject(7, "active");
    }
}
