package io.github.auspis.fluentsql4j.test.util.database;

import io.github.auspis.fluentsql4j.test.util.database.DataUtil.CartItemRecord;
import io.github.auspis.fluentsql4j.test.util.database.DataUtil.CustomerRecord;
import io.github.auspis.fluentsql4j.test.util.database.DataUtil.OrderRecord;
import io.github.auspis.fluentsql4j.test.util.database.DataUtil.ProductRecord;
import io.github.auspis.fluentsql4j.test.util.database.DataUtil.UserRecord;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

final class StatementUtil {
    private StatementUtil() {}

    static void dropUsersTable(Connection connection) throws SQLException {
        dropTable(connection, "users");
    }

    static void dropProductsTable(Connection connection) throws SQLException {
        dropTable(connection, "products");
    }

    static void dropOrdersTable(Connection connection) throws SQLException {
        dropTable(connection, "orders");
    }

    static void dropUsersUpdatesTable(Connection connection) throws SQLException {
        dropTable(connection, "users_updates");
    }

    static void dropCartItemsTable(Connection connection) throws SQLException {
        dropTable(connection, "cart_items");
    }

    static void dropCustomersTable(Connection connection) throws SQLException {
        dropTable(connection, "customers");
    }

    private static void dropTable(Connection connection, String tableName) throws SQLException {
        JdbcUtil.executeSql(connection, "DROP TABLE IF EXISTS " + tableName);
    }

    static void truncateUsersTable(Connection connection) throws SQLException {
        truncateTable(connection, "users");
    }

    static void truncateProductsTable(Connection connection) throws SQLException {
        truncateTable(connection, "products");
    }

    static void truncateOrdersTable(Connection connection) throws SQLException {
        truncateTable(connection, "orders");
    }

    static void truncateUsersUpdatesTable(Connection connection) throws SQLException {
        truncateTable(connection, "users_updates");
    }

    static void truncateCartItemsTable(Connection connection) throws SQLException {
        truncateTable(connection, "cart_items");
    }

    static void truncateCustomersTable(Connection connection) throws SQLException {
        truncateTable(connection, "customers");
    }

    private static void truncateTable(Connection connection, String tableName) throws SQLException {
        JdbcUtil.executeSql(connection, "TRUNCATE TABLE " + tableName);
    }

    static void insertUsers(Connection connection, String sql, List<UserRecord> users) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (UserRecord user : users) {
                JdbcUtil.bind(ps, user);
            }
        }
    }

    static void insertProducts(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO products VALUES (?, ?, ?, ?, ?)")) {
            for (ProductRecord product : DataUtil.SAMPLE_PRODUCTS) {
                JdbcUtil.bind(ps, product);
            }
        }
    }

    static void insertOrders(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO orders VALUES (?, ?, ?)")) {
            for (OrderRecord order : DataUtil.SAMPLE_ORDERS) {
                JdbcUtil.bind(ps, order);
            }
        }
    }

    static void insertCartItems(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO cart_items (cart_id, product_id, product_name, unit_price, quantity) VALUES (?, ?, ?, ?, ?)")) {
            for (CartItemRecord item : DataUtil.SAMPLE_CART_ITEMS) {
                JdbcUtil.bind(ps, item);
            }
        }
    }

    static void insertCustomers(Connection connection, String sql) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (CustomerRecord customer : DataUtil.SAMPLE_CUSTOMERS) {
                JdbcUtil.bind(ps, customer);
            }
        }
    }
}
