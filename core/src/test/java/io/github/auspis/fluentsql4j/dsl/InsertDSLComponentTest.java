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
class InsertDSLComponentTest {

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
    void createsInsertBuilderWithPreparedStatementSpecFactory() throws SQLException {
        dsl.insertInto("users").set("name", "John").build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                INSERT INTO "users" ("name") VALUES (?)""");
        verify(ps).setObject(1, "John");
    }

    @Test
    void appliesPreparedStatementSpecFactoryQuoting() throws SQLException {
        dsl.insertInto("temp_table").defaultValues().build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                INSERT INTO "temp_table" DEFAULT VALUES""");
    }

    @Test
    void fluentApiWithMultipleColumns() throws SQLException {
        dsl.insertInto("products")
                .set("id", 1)
                .set("name", "Widget")
                .set("price", 19.99)
                .build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                INSERT INTO "products" ("id", "name", "price") VALUES (?, ?, ?)""");
        verify(ps).setObject(1, 1);
        verify(ps).setObject(2, "Widget");
        verify(ps).setObject(3, 19.99);
    }

    @Test
    void insertJsonObject() throws SQLException {
        String jsonData = "{\"name\":\"Alice\",\"email\":\"alice@example.com\",\"age\":28}";

        dsl.insertInto("users").set("id", 42).set("metadata", jsonData).build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                INSERT INTO "users" ("id", "metadata") VALUES (?, ?)""");
        verify(ps).setObject(1, 42);
        verify(ps).setObject(2, jsonData);
    }

    @Test
    void insertComplexJsonWithMultipleColumns() throws SQLException {
        String preferences = "[\"email\",\"sms\",\"push\"]";
        String profile = "{\"bio\":\"Developer\",\"location\":{\"city\":\"Rome\",\"country\":\"Italy\"}}";

        dsl.insertInto("user_profiles")
                .set("user_id", 100)
                .set("username", "dev_user")
                .set("preferences", preferences)
                .set("profile_data", profile)
                .set("is_active", true)
                .build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                INSERT INTO "user_profiles" ("user_id", "username", "preferences", "profile_data", "is_active") VALUES (?, ?, ?, ?, ?)""");
        verify(ps).setObject(1, 100);
        verify(ps).setObject(2, "dev_user");
        verify(ps).setObject(3, preferences);
        verify(ps).setObject(4, profile);
        verify(ps).setObject(5, true);
    }
}
