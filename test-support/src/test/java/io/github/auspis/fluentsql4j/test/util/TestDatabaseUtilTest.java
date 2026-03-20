package io.github.auspis.fluentsql4j.test.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.auspis.fluentsql4j.test.util.database.TestDatabaseUtil;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class TestDatabaseUtilTest {

    // =====================================================================
    // H2 — Connection lifecycle
    // =====================================================================

    @Test
    void h2_createConnectionReturnsValidConnection() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();

        assertThat(connection).isNotNull();
        assertThat(connection.isClosed()).isFalse();
        assertThat(connection.getMetaData().getDatabaseProductName()).isEqualTo("H2");

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_createConnectionGeneratesUniqueDatabaseNames() throws SQLException {
        Connection conn1 = TestDatabaseUtil.H2.createConnection();
        Connection conn2 = TestDatabaseUtil.H2.createConnection();

        assertThat(conn1.getMetaData().getURL())
                .isNotEqualTo(conn2.getMetaData().getURL());

        TestDatabaseUtil.H2.closeConnection(conn1);
        TestDatabaseUtil.H2.closeConnection(conn2);
    }

    @Test
    void h2_createJsonConnectionReturnsValidConnection() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createMySQLConnection();

        assertThat(connection).isNotNull();
        assertThat(connection.isClosed()).isFalse();

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_closeConnectionClosesOpenConnection() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();

        TestDatabaseUtil.H2.closeConnection(connection);

        assertThat(connection.isClosed()).isTrue();
    }

    @Test
    void h2_closeConnectionHandlesNullConnection() {
        assertThatCode(() -> TestDatabaseUtil.H2.closeConnection(null)).doesNotThrowAnyException();
    }

    @Test
    void h2_closeConnectionHandlesAlreadyClosedConnection() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        connection.close();

        assertThatCode(() -> TestDatabaseUtil.H2.closeConnection(connection)).doesNotThrowAnyException();
    }

    // =====================================================================
    // H2 — users
    // =====================================================================

    @Test
    void h2_createUsersTableCreatesTableWithCorrectSchema() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();

        TestDatabaseUtil.H2.createUsersTable(connection);

        try (java.sql.Statement stmt = connection.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_insertSampleUsersInsertsCorrectNumberOfRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createUsersTable(connection);

        TestDatabaseUtil.H2.insertSampleUsers(connection);

        try (java.sql.Statement stmt = connection.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(10);
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_insertSampleUsersInsertsUsersWithJsonData() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createUsersTable(connection);
        TestDatabaseUtil.H2.insertSampleUsers(connection);

        try (java.sql.PreparedStatement pstmt =
                connection.prepareStatement("SELECT name, address FROM users WHERE id = ?")) {
            pstmt.setInt(1, 8);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString("name")).isEqualTo("Frank");
                assertThat(rs.getString("address")).contains("Via Roma 123");
            }
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_truncateUsersRemovesAllRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createUsersTable(connection);
        TestDatabaseUtil.H2.insertSampleUsers(connection);

        TestDatabaseUtil.H2.truncateUsers(connection);

        try (java.sql.Statement stmt = connection.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_dropUsersTableRemovesTable() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createUsersTable(connection);

        TestDatabaseUtil.H2.dropUsersTable(connection);

        try (java.sql.Statement stmt = connection.createStatement()) {
            assertThatThrownBy(() -> stmt.executeQuery("SELECT * FROM users")).isInstanceOf(SQLException.class);
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_dropUsersTableIsIdempotent() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();

        assertThatCode(() -> TestDatabaseUtil.H2.dropUsersTable(connection)).doesNotThrowAnyException();

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    // =====================================================================
    // H2 — products
    // =====================================================================

    @Test
    void h2_createProductsTableCreatesTableWithCorrectSchema() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();

        TestDatabaseUtil.H2.createProductsTable(connection);

        try (java.sql.ResultSet rs = connection.getMetaData().getColumns(null, null, "products", null)) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("id");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("name");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("price");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("quantity");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("metadata");
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_insertSampleProductsInsertsCorrectNumberOfRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createProductsTable(connection);

        TestDatabaseUtil.H2.insertSampleProducts(connection);

        try (java.sql.Statement stmt = connection.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(5);
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_insertSampleProductsInsertsProductsWithJsonMetadata() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createProductsTable(connection);
        TestDatabaseUtil.H2.insertSampleProducts(connection);

        try (java.sql.PreparedStatement pstmt =
                connection.prepareStatement("SELECT name, metadata FROM products WHERE id = ?")) {
            pstmt.setInt(1, 3);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString("name")).isEqualTo("Laptop");
                assertThat(rs.getString("metadata")).contains("electronics");
            }
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_dropProductsTableRemovesTable() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createProductsTable(connection);

        TestDatabaseUtil.H2.dropProductsTable(connection);

        try (java.sql.Statement stmt = connection.createStatement()) {
            assertThatThrownBy(() -> stmt.executeQuery("SELECT * FROM products"))
                    .isInstanceOf(SQLException.class);
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_truncateProductsRemovesAllRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createProductsTable(connection);
        TestDatabaseUtil.H2.insertSampleProducts(connection);

        TestDatabaseUtil.H2.truncateProducts(connection);

        try (java.sql.Statement stmt = connection.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    // =====================================================================
    // H2 — orders
    // =====================================================================

    @Test
    void h2_createOrdersTableCreatesTableWithCorrectSchema() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();

        TestDatabaseUtil.H2.createOrdersTable(connection);

        try (java.sql.ResultSet rs = connection.getMetaData().getColumns(null, null, "orders", null)) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("id");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("userId");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("total");
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_insertSampleOrdersInsertsCorrectNumberOfRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createOrdersTable(connection);

        TestDatabaseUtil.H2.insertSampleOrders(connection);

        try (java.sql.Statement stmt = connection.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM orders")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(4);
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_dropOrdersTableRemovesTable() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createOrdersTable(connection);

        TestDatabaseUtil.H2.dropOrdersTable(connection);

        try (java.sql.Statement stmt = connection.createStatement()) {
            assertThatThrownBy(() -> stmt.executeQuery("SELECT * FROM orders")).isInstanceOf(SQLException.class);
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_truncateOrdersRemovesAllRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createOrdersTable(connection);
        TestDatabaseUtil.H2.insertSampleOrders(connection);

        TestDatabaseUtil.H2.truncateOrders(connection);

        try (java.sql.Statement stmt = connection.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM orders")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    // =====================================================================
    // H2 — users_updates
    // =====================================================================

    @Test
    void h2_createUsersUpdatesTableAndInsertSample() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();

        TestDatabaseUtil.H2.createUsersUpdatesTable(connection);
        TestDatabaseUtil.H2.insertSampleUsersUpdates(connection);

        try (java.sql.Statement stmt = connection.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users_updates")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(3);
        }

        try (java.sql.PreparedStatement pstmt =
                connection.prepareStatement("SELECT name, email, age FROM users_updates WHERE id = ?")) {
            pstmt.setInt(1, 1);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString("name")).isEqualTo("John Doe");
                assertThat(rs.getString("email")).isEqualTo("john.newemail@example.com");
                assertThat(rs.getInt("age")).isEqualTo(31);
            }
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_dropUsersUpdatesTableRemovesTable() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createUsersUpdatesTable(connection);

        TestDatabaseUtil.H2.dropUsersUpdatesTable(connection);

        try (java.sql.Statement stmt = connection.createStatement()) {
            assertThatThrownBy(() -> stmt.executeQuery("SELECT * FROM users_updates"))
                    .isInstanceOf(SQLException.class);
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_truncateUsersUpdatesRemovesAllRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createUsersUpdatesTable(connection);
        TestDatabaseUtil.H2.insertSampleUsersUpdates(connection);

        TestDatabaseUtil.H2.truncateUsersUpdates(connection);

        try (java.sql.Statement stmt = connection.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users_updates")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    // =====================================================================
    // H2 — cart_items
    // =====================================================================

    @Test
    void h2_createCartItemsTableCreatesTableWithCorrectSchema() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();

        TestDatabaseUtil.H2.createCartItemsTable(connection);

        try (java.sql.ResultSet rs = connection.getMetaData().getColumns(null, null, "cart_items", null)) {
            assertThat(rs.next()).as("column id should exist").isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("id");
            assertThat(rs.next()).as("column cart_id should exist").isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("cart_id");
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_createCartItemsTableSupportsAutoIncrementId() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createCartItemsTable(connection);

        String sql =
                "INSERT INTO cart_items (cart_id, product_id, product_name, unit_price, quantity) VALUES (?, ?, ?, ?, ?)";
        try (java.sql.PreparedStatement pstmt =
                connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, 1L);
            pstmt.setLong(2, 101L);
            pstmt.setString(3, "Widget");
            pstmt.setDouble(4, 19.99);
            pstmt.setInt(5, 2);
            pstmt.executeUpdate();

            try (java.sql.ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                assertThat(generatedKeys.next()).isTrue();
                assertThat(generatedKeys.getLong(1)).isGreaterThan(0L);
            }
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_insertSampleCartItemsInsertsCorrectNumberOfRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createCartItemsTable(connection);

        TestDatabaseUtil.H2.insertSampleCartItems(connection);

        try (java.sql.Statement stmt = connection.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM cart_items")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(3);
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_dropCartItemsTableRemovesTable() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createCartItemsTable(connection);

        TestDatabaseUtil.H2.dropCartItemsTable(connection);

        try (java.sql.Statement stmt = connection.createStatement()) {
            assertThatThrownBy(() -> stmt.executeQuery("SELECT * FROM cart_items"))
                    .isInstanceOf(SQLException.class);
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void h2_truncateCartItemsRemovesAllRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createCartItemsTable(connection);
        TestDatabaseUtil.H2.insertSampleCartItems(connection);

        TestDatabaseUtil.H2.truncateCartItems(connection);

        try (java.sql.Statement stmt = connection.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM cart_items")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.H2.closeConnection(connection);
    }
}
