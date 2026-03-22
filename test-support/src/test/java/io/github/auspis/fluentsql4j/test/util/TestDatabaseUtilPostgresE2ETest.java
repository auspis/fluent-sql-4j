package io.github.auspis.fluentsql4j.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.test.util.annotation.E2ETest;
import io.github.auspis.fluentsql4j.test.util.database.TestDatabaseUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@E2ETest
@Testcontainers
class TestDatabaseUtilPostgresE2ETest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15");

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = POSTGRES.createConnection("");
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void postgres_createAndInsertUsers() throws SQLException {
        TestDatabaseUtil.PostgreSQL.createUsersTable(connection);
        TestDatabaseUtil.PostgreSQL.insertSampleUsers(connection);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(10);
        }

        TestDatabaseUtil.PostgreSQL.truncateUsers(connection);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.PostgreSQL.dropUsersTable(connection);
    }

    @Test
    void postgres_insertUserInsertsSingleRecord() throws SQLException {
        TestDatabaseUtil.PostgreSQL.createUsersTable(connection);

        TestDatabaseUtil.PostgreSQL.insertUser(
                connection,
                99L,
                "Single User",
                "single.user@example.com",
                42,
                true,
                LocalDate.of(1982, 3, 14),
                LocalDate.of(2024, 1, 2),
                "{\"street\":\"Test Street\"}",
                "{\"theme\":\"dark\"}");

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name, email, age, active FROM users WHERE id = 99")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getLong("id")).isEqualTo(99L);
            assertThat(rs.getString("name")).isEqualTo("Single User");
            assertThat(rs.getString("email")).isEqualTo("single.user@example.com");
            assertThat(rs.getInt("age")).isEqualTo(42);
            assertThat(rs.getBoolean("active")).isTrue();
        }

        TestDatabaseUtil.PostgreSQL.dropUsersTable(connection);
    }

    @Test
    void postgres_createAndInsertProducts() throws SQLException {
        TestDatabaseUtil.PostgreSQL.createProductsTable(connection);
        TestDatabaseUtil.PostgreSQL.insertSampleProducts(connection);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(5);
        }

        TestDatabaseUtil.PostgreSQL.dropProductsTable(connection);
    }

    @Test
    void postgres_createAndInsertOrders() throws SQLException {
        TestDatabaseUtil.PostgreSQL.createOrdersTable(connection);
        TestDatabaseUtil.PostgreSQL.insertSampleOrders(connection);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM orders")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(4);
        }

        TestDatabaseUtil.PostgreSQL.dropOrdersTable(connection);
    }

    @Test
    void postgres_createAndInsertUsersUpdates() throws SQLException {
        TestDatabaseUtil.PostgreSQL.createUsersUpdatesTable(connection);
        TestDatabaseUtil.PostgreSQL.insertSampleUsersUpdates(connection);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users_updates")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(3);
        }

        TestDatabaseUtil.PostgreSQL.dropUsersUpdatesTable(connection);
    }

    @Test
    void postgres_createAndInsertCartItems() throws SQLException {
        TestDatabaseUtil.PostgreSQL.createCartItemsTable(connection);
        TestDatabaseUtil.PostgreSQL.insertSampleCartItems(connection);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM cart_items")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(3);
        }

        TestDatabaseUtil.PostgreSQL.dropCartItemsTable(connection);
    }
}
