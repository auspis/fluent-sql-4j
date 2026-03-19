package io.github.auspis.fluentsql4j.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.test.util.annotation.E2ETest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
