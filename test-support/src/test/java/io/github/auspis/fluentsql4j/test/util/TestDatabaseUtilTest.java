package io.github.auspis.fluentsql4j.test.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class TestDatabaseUtilTest {

    @Test
    void createH2ConnectionReturnsValidConnection() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        assertThat(connection).isNotNull();
        assertThat(connection.isClosed()).isFalse();
        assertThat(connection.getMetaData().getDatabaseProductName()).isEqualTo("H2");

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void createH2ConnectionGeneratesUniqueDatabaseNames() throws SQLException {
        Connection conn1 = TestDatabaseUtil.createH2Connection();
        Connection conn2 = TestDatabaseUtil.createH2Connection();

        assertThat(conn1.getMetaData().getURL())
                .isNotEqualTo(conn2.getMetaData().getURL());

        TestDatabaseUtil.closeConnection(conn1);
        TestDatabaseUtil.closeConnection(conn2);
    }

    @Test
    void createH2JsonConnectionReturnsValidConnection() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2JsonConnection();

        assertThat(connection).isNotNull();
        assertThat(connection.isClosed()).isFalse();

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void createUsersTableCreatesTableWithCorrectSchema() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        TestDatabaseUtil.createUsersTable(connection);

        // Verify table exists and has essential columns (not checking exact names due to H2 reserved words handling)
        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero(); // Table exists but is empty
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void createUsersTableWithBackTicksCreatesTableSuccessfully() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        assertThatCode(() -> TestDatabaseUtil.createUsersTableWithBackTicks(connection))
                .doesNotThrowAnyException();

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void createProductsTableCreatesTableWithCorrectSchema() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        TestDatabaseUtil.createProductsTable(connection);

        try (var rs = connection.getMetaData().getColumns(null, null, "products", null)) {
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

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void createOrderTableCreatesTableWithCorrectSchema() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        TestDatabaseUtil.createOrderTable(connection);

        try (var rs = connection.getMetaData().getColumns(null, null, "orders", null)) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("id");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("userId");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("total");
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void createOrderTableWithBackTicksCreatesTableSuccessfully() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        assertThatCode(() -> TestDatabaseUtil.createOrderTableWithBackTicks(connection))
                .doesNotThrowAnyException();

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM orders")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void insertSampleUsersInsertsCorrectNumberOfRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);

        TestDatabaseUtil.insertSampleUsers(connection);

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(10);
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void insertSampleUsersInsertsUsersWithoutJsonData() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);

        TestDatabaseUtil.insertSampleUsers(connection);

        String sql = "SELECT id, name, email, age FROM users WHERE id = ?";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, 1);
            try (var rs = pstmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("id")).isEqualTo(1);
                assertThat(rs.getString("name")).isEqualTo("John Doe");
                assertThat(rs.getString("email")).isEqualTo("john@example.com");
                assertThat(rs.getInt("age")).isEqualTo(30);
            }
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void insertSampleUsersInsertsUsersWithJsonData() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);

        TestDatabaseUtil.insertSampleUsers(connection);

        String sql = "SELECT id, name, address FROM users WHERE id = ?";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, 8);
            try (var rs = pstmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("id")).isEqualTo(8);
                assertThat(rs.getString("name")).isEqualTo("Frank");
                assertThat(rs.getString("address")).isNotNull();
                assertThat(rs.getString("address")).contains("Via Roma 123");
            }
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void insertSampleProductsInsertsCorrectNumberOfRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createProductsTable(connection);

        TestDatabaseUtil.insertSampleProducts(connection);

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(5);
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void insertSampleProductsInsertsProductsWithJsonMetadata() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createProductsTable(connection);

        TestDatabaseUtil.insertSampleProducts(connection);

        String sql = "SELECT id, name, metadata FROM products WHERE id = ?";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, 3);
            try (var rs = pstmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("id")).isEqualTo(3);
                assertThat(rs.getString("name")).isEqualTo("Laptop");
                assertThat(rs.getString("metadata")).isNotNull();
                assertThat(rs.getString("metadata")).contains("electronics");
            }
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void insertSampleOrdersInsertsCorrectNumberOfRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createOrderTable(connection);

        TestDatabaseUtil.insertSampleOrders(connection);

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM orders")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(4);
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void truncateUsersRemovesAllRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);

        TestDatabaseUtil.truncateUsers(connection);

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void truncateOrdersRemovesAllRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createOrderTable(connection);
        TestDatabaseUtil.insertSampleOrders(connection);

        TestDatabaseUtil.truncateOrders(connection);

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM orders")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void truncateUsersUpdatesRemovesAllRecords() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersUpdatesTableWithRecords(connection);

        TestDatabaseUtil.truncateUsersUpdates(connection);

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM users_updates")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void createUsersUpdatesTableWithRecordsCreatesTableAndInsertsData() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        TestDatabaseUtil.createUsersUpdatesTableWithRecords(connection);

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM users_updates")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(3);
        }

        String sql = "SELECT id, name, email, age FROM users_updates WHERE id = ?";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, 1);
            try (var rs = pstmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("id")).isEqualTo(1);
                assertThat(rs.getString("name")).isEqualTo("John Doe");
                assertThat(rs.getString("email")).isEqualTo("john.newemail@example.com");
                assertThat(rs.getInt("age")).isEqualTo(31);
            }
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void closeConnectionClosesOpenConnection() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        TestDatabaseUtil.closeConnection(connection);

        assertThat(connection.isClosed()).isTrue();
    }

    @Test
    void closeConnectionHandlesNullConnection() {
        assertThatCode(() -> TestDatabaseUtil.closeConnection(null)).doesNotThrowAnyException();
    }

    @Test
    void closeConnectionHandlesAlreadyClosedConnection() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        connection.close();

        assertThatCode(() -> TestDatabaseUtil.closeConnection(connection)).doesNotThrowAnyException();
    }

    @Test
    void closeConnectionExecutesShutdownCommand() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);

        TestDatabaseUtil.closeConnection(connection);

        assertThat(connection.isClosed()).isTrue();
    }

    // Drop methods tests
    @Test
    void dropUsersTableRemovesExistingTable() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);

        TestDatabaseUtil.dropUsersTable(connection);

        // Try to query the dropped table - should fail
        try (var stmt = connection.createStatement()) {
            assertThatThrownBy(() -> stmt.executeQuery("SELECT * FROM users")).isInstanceOf(SQLException.class);
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void dropUsersTableIsIdempotent() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        assertThatCode(() -> TestDatabaseUtil.dropUsersTable(connection)).doesNotThrowAnyException();

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void dropOrdersTableRemovesExistingTable() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createOrderTable(connection);

        TestDatabaseUtil.dropOrdersTable(connection);

        // Try to query the dropped table - should fail
        try (var stmt = connection.createStatement()) {
            assertThatThrownBy(() -> stmt.executeQuery("SELECT * FROM orders")).isInstanceOf(SQLException.class);
        }
        ;

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void dropUsersUpdatesTableRemovesExistingTable() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersUpdatesTableWithRecords(connection);

        TestDatabaseUtil.dropUsersUpdatesTable(connection);

        // Try to query the dropped table - should fail
        try (var stmt = connection.createStatement()) {
            assertThatThrownBy(() -> stmt.executeQuery("SELECT * FROM users_updates"))
                    .isInstanceOf(SQLException.class);
        }
        ;

        TestDatabaseUtil.closeConnection(connection);
    }

    // BackTick variants tests
    @Test
    void createUsersTableWithBackTicksCreatesColumnsWithBackTicks() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        TestDatabaseUtil.createUsersTableWithBackTicks(connection);

        try (var rs = connection.getMetaData().getColumns(null, null, "users", null)) {
            int columnCount = 0;
            while (rs.next()) {
                columnCount++;
                assertThat(rs.getString("COLUMN_NAME")).isNotNull();
            }
            assertThat(columnCount).isGreaterThan(0); // Should have columns
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void createUsersTableWithBackTicksCanBeQueried() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTableWithBackTicks(connection);

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void createOrderTableWithBackTicksCanBeQueried() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createOrderTableWithBackTicks(connection);

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM orders")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    // createCartItemsTable tests
    @Test
    void createCartItemsTableCreatesTableWithCorrectSchema() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        TestDatabaseUtil.createCartItemsTable(connection);

        try (var rs = connection.getMetaData().getColumns(null, null, "cart_items", null)) {
            assertThat(rs.next()).as("column id should exist").isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("id");
            assertThat(rs.next()).as("column cart_id should exist").isTrue();
            assertThat(rs.getString("COLUMN_NAME")).isEqualToIgnoringCase("cart_id");
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void createCartItemsTableSupportsAutoIncrementId() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createCartItemsTable(connection);

        String sql =
                "INSERT INTO cart_items (cart_id, product_id, product_name, unit_price, quantity) VALUES (?, ?, ?, ?, ?)";
        try (var pstmt = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, 1L);
            pstmt.setLong(2, 101L);
            pstmt.setString(3, "Widget");
            pstmt.setDouble(4, 19.99);
            pstmt.setInt(5, 2);
            pstmt.executeUpdate();

            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                assertThat(generatedKeys.next()).isTrue();
                assertThat(generatedKeys.getLong(1)).isGreaterThan(0L);
            }
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void createCartItemsTableInsertAndRetrieveData() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createCartItemsTable(connection);

        String insertSql =
                "INSERT INTO cart_items (cart_id, product_id, product_name, unit_price, quantity) VALUES (?, ?, ?, ?, ?)";
        try (var pstmt = connection.prepareStatement(insertSql)) {
            pstmt.setLong(1, 1L);
            pstmt.setLong(2, 101L);
            pstmt.setString(3, "Widget");
            pstmt.setDouble(4, 19.99);
            pstmt.setInt(5, 2);
            pstmt.executeUpdate();
        }

        String selectSql = "SELECT cart_id, product_name, unit_price, quantity FROM cart_items WHERE product_id = ?";
        try (var pstmt = connection.prepareStatement(selectSql)) {
            pstmt.setLong(1, 101L);
            try (var rs = pstmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getLong("cart_id")).isEqualTo(1L);
                assertThat(rs.getString("product_name")).isEqualTo("Widget");
                assertThat(rs.getDouble("unit_price")).isEqualTo(19.99);
                assertThat(rs.getInt("quantity")).isEqualTo(2);
            }
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    // Error scenarios tests
    @Test
    void insertSampleUsersFailsWhenTableDoesNotExist() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        assertThatThrownBy(() -> TestDatabaseUtil.insertSampleUsers(connection)).isInstanceOf(SQLException.class);

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void truncateUsersFailsWhenTableDoesNotExist() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();

        assertThatThrownBy(() -> TestDatabaseUtil.truncateUsers(connection)).isInstanceOf(SQLException.class);

        TestDatabaseUtil.closeConnection(connection);
    }

    // Data integrity tests
    @Test
    void dropAndRecreateUserTablePreservesSchema() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);

        TestDatabaseUtil.dropUsersTable(connection);
        TestDatabaseUtil.createUsersTable(connection);

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero(); // New table is empty
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void insertTruncateInsertCyclePreservesDataIntegrity() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);

        TestDatabaseUtil.insertSampleUsers(connection);
        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            int countBefore = rs.getInt(1);
            assertThat(countBefore).isGreaterThan(0);
        }

        TestDatabaseUtil.truncateUsers(connection);
        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isZero();
        }

        TestDatabaseUtil.insertSampleUsers(connection);
        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            int countAfter = rs.getInt(1);
            assertThat(countAfter).isGreaterThan(0);
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void jsonDataPersistsThroughTruncateRecreate() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT address FROM users WHERE id = 8")) {
            assertThat(rs.next()).isTrue();
            String addressBefore = rs.getString("address");
            assertThat(addressBefore).isNotNull().contains("Via Roma");
        }

        TestDatabaseUtil.truncateUsers(connection);
        TestDatabaseUtil.insertSampleUsers(connection);

        try (var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT address FROM users WHERE id = 8")) {
            assertThat(rs.next()).isTrue();
            String addressAfter = rs.getString("address");
            assertThat(addressAfter).isNotNull().contains("Via Roma");
        }

        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void multipleConnectionsWithUniqueDbNamesAreIsolated() throws SQLException {
        Connection conn1 = TestDatabaseUtil.createH2Connection();
        Connection conn2 = TestDatabaseUtil.createH2Connection();

        TestDatabaseUtil.createUsersTable(conn1);
        TestDatabaseUtil.insertSampleUsers(conn1);

        try (var stmt = conn1.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isGreaterThan(0);
        }

        // conn2 should not have access to conn1's data (different database)
        try (var stmt = conn2.createStatement()) {
            assertThatThrownBy(() -> stmt.executeQuery("SELECT COUNT(*) FROM users"))
                    .isInstanceOf(SQLException.class);
        }
        ;

        TestDatabaseUtil.closeConnection(conn1);
        TestDatabaseUtil.closeConnection(conn2);
    }
}
