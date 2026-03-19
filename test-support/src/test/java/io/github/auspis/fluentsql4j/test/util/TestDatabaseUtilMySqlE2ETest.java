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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@E2ETest
@Testcontainers
class TestDatabaseUtilMySqlE2ETest {

    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = MYSQL.createConnection("");
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void mysql_createAndInsertUsers() throws SQLException {
        TestDatabaseUtil.MySQL.createUsersTable(connection);
        TestDatabaseUtil.MySQL.insertSampleUsers(connection);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(10);
        }

        TestDatabaseUtil.MySQL.truncateUsers(connection);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.MySQL.dropUsersTable(connection);
    }

    @Test
    void mysql_createAndInsertProducts() throws SQLException {
        TestDatabaseUtil.MySQL.createProductsTable(connection);
        TestDatabaseUtil.MySQL.insertSampleProducts(connection);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(5);
        }

        TestDatabaseUtil.MySQL.dropProductsTable(connection);
    }

    @Test
    void mysql_createAndInsertOrders() throws SQLException {
        TestDatabaseUtil.MySQL.createOrdersTable(connection);
        TestDatabaseUtil.MySQL.insertSampleOrders(connection);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM orders")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(4);
        }

        TestDatabaseUtil.MySQL.dropOrdersTable(connection);
    }

    @Test
    void mysql_createAndInsertUsersUpdates() throws SQLException {
        TestDatabaseUtil.MySQL.createUsersUpdatesTable(connection);
        TestDatabaseUtil.MySQL.insertSampleUsersUpdates(connection);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users_updates")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(3);
        }

        TestDatabaseUtil.MySQL.dropUsersUpdatesTable(connection);
    }

    @Test
    void mysql_createAndInsertCartItems() throws SQLException {
        TestDatabaseUtil.MySQL.createCartItemsTable(connection);
        TestDatabaseUtil.MySQL.insertSampleCartItems(connection);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM cart_items")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(3);
        }

        TestDatabaseUtil.MySQL.dropCartItemsTable(connection);
    }
}
