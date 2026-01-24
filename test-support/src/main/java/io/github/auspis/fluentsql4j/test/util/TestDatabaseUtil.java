package io.github.auspis.fluentsql4j.test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Utility class for creating and managing test database connections and tables in integration tests.
 */
@SuppressWarnings("java:S2115")
public final class TestDatabaseUtil {

    /**
     * Database password used for test database connections.
     * <p>
     * The value is read from the {@code fluentsql4j.test.database.password} system property.
     * If the property is not set, it defaults to an empty string, which is suitable for H2
     * in-memory databases that typically do not require a password.
     */
    private static final String PASSWORD = System.getProperty("fluentsql4j.test.database.password", "");

    private TestDatabaseUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates an H2 in-memory database connection with standard SQL mode.
     * Each connection gets a unique database name to avoid conflicts between tests.
     *
     * @return a new H2 database connection
     * @throws SQLException if connection cannot be established
     */
    public static Connection createH2Connection() throws SQLException {
        String uniqueDbName = "testdb_" + UUID.randomUUID().toString().replace("-", "");
        String jdbcUrl =
                "jdbc:h2:mem:" + uniqueDbName + ";MODE=REGULAR;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
        return DriverManager.getConnection(jdbcUrl, "sa", PASSWORD);
    }

    /**
     * Creates an H2 in-memory database connection with MySQL compatibility mode.
     * This mode is useful for testing MySQL-specific features like JSON functions,
     * though H2 may not support all MySQL JSON functions.
     * Each connection gets a unique database name to avoid conflicts between tests.
     *
     * @return a new H2 database connection in MySQL mode
     * @throws SQLException if connection cannot be established
     */
    public static Connection createH2JsonConnection() throws SQLException {
        String uniqueDbName = "testdb_json_" + UUID.randomUUID().toString().replace("-", "");
        String jdbcUrl =
                "jdbc:h2:mem:" + uniqueDbName + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
        return DriverManager.getConnection(jdbcUrl, "sa", PASSWORD);
    }

    public static void dropUsersTable(Connection connection) throws SQLException {
        dropTable(connection, "users");
    }

    public static void dropOrdersTable(Connection connection) throws SQLException {
        dropTable(connection, "orders");
    }

    public static void dropUsersUpdatesTable(Connection connection) throws SQLException {
        dropTable(connection, "users_updates");
    }

    private static void dropTable(Connection connection, String table) throws SQLException {
        try (var stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS " + table);
        }
    }
    /**
     * Creates a standard users table with columns: id, name, email, age, active, birthdate, createdAt.
     *
     * @param connection the database connection
     * @throws SQLException if table creation fails
     */
    public static void createUsersTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE users (\
                    "id" INTEGER PRIMARY KEY, \
                    "name" VARCHAR(50), \
                    "email" VARCHAR(100), \
                    "age" INTEGER, \
                    "active" BOOLEAN, \
                    "birthdate" DATE, \
                    "createdAt" TIMESTAMP, \
                    "address" JSON, \
                    "preferences" JSON)
                    """);
        }
    }

    public static void createUsersTableWithBackTicks(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE users (\
                    `id` INTEGER PRIMARY KEY, \
                    `name` VARCHAR(50), \
                    `email` VARCHAR(100), \
                    `age` INTEGER, \
                    `active` BOOLEAN, \
                    `birthdate` DATE, \
                    `createdAt` TIMESTAMP, \
                    `address` JSON, \
                    `preferences` JSON)
                    """);
        }
    }

    /**
     * Creates a standard products table with columns: id, name, price, quantity.
     *
     * @param connection the database connection
     * @throws SQLException if table creation fails
     */
    public static void createProductsTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE products (\
                    "id" INTEGER PRIMARY KEY, \
                    "name" VARCHAR(50), \
                    "price" DECIMAL(10,2), \
                    "quantity" INTEGER, \
                    "metadata" JSON)
                    """);
        }
    }

    public static void createOrderTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE orders (\
                "id" INTEGER PRIMARY KEY, \
                "userId" INTEGER, \
                "total" DECIMAL(10,2))
                """);
        }
    }

    public static void createOrderTableWithBackTicks(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE orders (\
                    `id` INTEGER PRIMARY KEY, \
                    `userId` VARCHAR(50), \
                    `total` DECIMAL(10,2))
                    """);
        }
    }

    public static void truncateUsers(Connection connection) throws SQLException {
        truncateTable(connection, "users");
    }

    public static void truncateOrders(Connection connection) throws SQLException {
        truncateTable(connection, "orders");
    }

    public static void truncateUsersUpdates(Connection connection) throws SQLException {
        truncateTable(connection, "users_updates");
    }

    private static void truncateTable(Connection connection, String table) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE " + table);
        }
    }
    /**
     * Inserts sample data into the users table.
     * Some users have JSON data prepopulated for testing JSON operations.
     *
     * @param connection the database connection
     * @throws SQLException if insert fails
     */
    public static void insertSampleUsers(Connection connection) throws SQLException {
        // Use CAST for PostgreSQL compatibility; H2 also supports CAST
        String sql = "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS JSON), CAST(? AS JSON))";
        try (var pstmt = connection.prepareStatement(sql)) {
            // Users without JSON data
            insertUser(pstmt, 1, "John Doe", "john@example.com", 30, true, "1990-01-01", "2023-01-01", null, null);
            insertUser(pstmt, 2, "Jane Smith", "jane@example.com", 25, true, "1995-01-01", "2023-01-01", null, null);
            insertUser(pstmt, 3, "Bob", "bob@example.com", 15, false, "2005-01-01", "2023-01-01", null, null);
            insertUser(pstmt, 4, "Alice", "alice@example.com", 35, true, "1990-01-01", "2023-01-01", null, null);
            insertUser(pstmt, 5, "Charlie", "charlie@example.com", 30, true, "1991-01-01", "2023-01-02", null, null);
            insertUser(pstmt, 6, "Diana", "diana@example.com", 25, false, "1996-01-01", "2023-01-03", null, null);
            insertUser(pstmt, 7, "Eve", "eve@example.com", 40, true, "1985-01-01", "2023-01-04", null, null);

            // Users with JSON data prepopulated
            insertUser(
                    pstmt,
                    8,
                    "Frank",
                    "frank@example.com",
                    35,
                    true,
                    "1990-02-01",
                    "2023-01-05",
                    "{\"street\":\"Via Roma 123\",\"city\":\"Milan\",\"zip\":\"20100\",\"country\":\"Italy\"}",
                    "[\"email\",\"sms\"]");
            insertUser(
                    pstmt,
                    9,
                    "Grace",
                    "grace@example.com",
                    28,
                    false,
                    "1997-01-01",
                    "2023-01-06",
                    "{\"street\":\"Via Torino 45\",\"city\":\"Rome\",\"zip\":\"00100\",\"country\":\"Italy\"}",
                    "[\"email\",\"push\"]");
            insertUser(
                    pstmt,
                    10,
                    "Henry",
                    "henry@example.com",
                    30,
                    true,
                    "1995-01-01",
                    "2023-01-07",
                    "{\"street\":\"Corso Vittorio 78\",\"city\":\"Turin\",\"zip\":\"10100\",\"country\":\"Italy\"}",
                    "[\"sms\",\"push\",\"phone\"]");
        }
    }

    private static void insertUser(
            java.sql.PreparedStatement pstmt,
            int id,
            String name,
            String email,
            int age,
            boolean active,
            String birthdate,
            String createdAt,
            String address,
            String preferences)
            throws SQLException {
        pstmt.setInt(1, id);
        pstmt.setString(2, name);
        pstmt.setString(3, email);
        pstmt.setInt(4, age);
        pstmt.setBoolean(5, active);
        pstmt.setDate(6, java.sql.Date.valueOf(java.time.LocalDate.parse(birthdate)));
        pstmt.setDate(7, java.sql.Date.valueOf(java.time.LocalDate.parse(createdAt)));
        pstmt.setString(8, address);
        pstmt.setString(9, preferences);
        pstmt.executeUpdate();
    }

    /**
     * Inserts sample data into the products table.
     * Some products have JSON metadata prepopulated for testing JSON operations.
     *
     * @param connection the database connection
     * @throws SQLException if insert fails
     */
    public static void insertSampleProducts(Connection connection) throws SQLException {
        String sql = "INSERT INTO products VALUES (?, ?, ?, ?, ?)";
        try (var pstmt = connection.prepareStatement(sql)) {
            // Products without JSON metadata
            insertProduct(pstmt, 1, "Widget", 19.99, 100, null);
            insertProduct(pstmt, 2, "Gadget", 29.99, 50, null);

            // Products with JSON metadata prepopulated
            insertProduct(
                    pstmt,
                    3,
                    "Laptop",
                    999.99,
                    10,
                    "{\"tags\":[\"electronics\",\"computers\"],\"featured\":true,\"warranty\":24}");
            insertProduct(
                    pstmt,
                    4,
                    "Mouse",
                    15.99,
                    200,
                    "{\"tags\":[\"electronics\",\"accessories\"],\"featured\":false,\"color\":\"black\"}");
            insertProduct(
                    pstmt,
                    5,
                    "Keyboard",
                    49.99,
                    75,
                    "{\"tags\":[\"electronics\",\"accessories\"],\"featured\":true,\"backlit\":true}");
        }
    }

    private static void insertProduct(
            java.sql.PreparedStatement pstmt, int id, String name, double price, int quantity, String metadata)
            throws SQLException {
        pstmt.setInt(1, id);
        pstmt.setString(2, name);
        pstmt.setDouble(3, price);
        pstmt.setInt(4, quantity);
        pstmt.setString(5, metadata);
        pstmt.executeUpdate();
    }

    public static void insertSampleOrders(Connection connection) throws SQLException {
        String sql = "INSERT INTO orders VALUES (?, ?, ?)";
        try (var pstmt = connection.prepareStatement(sql)) {
            insertOrder(pstmt, 1, 1, 10.99);
            insertOrder(pstmt, 2, 1, 29.99);
            insertOrder(pstmt, 3, 4, 39.99);
            insertOrder(pstmt, 4, 5, 49.99);
        }
    }

    private static void insertOrder(java.sql.PreparedStatement pstmt, int id, int userId, double total)
            throws SQLException {
        pstmt.setInt(1, id);
        pstmt.setInt(2, userId);
        pstmt.setDouble(3, total);
        pstmt.executeUpdate();
    }

    public static void createUsersUpdatesTableWithRecords(Connection connection) throws SQLException {
        try (var stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS users_updates");
            stmt.execute("""
                    CREATE TABLE users_updates (
                        id INTEGER PRIMARY KEY,
                        name VARCHAR(50),
                        email VARCHAR(100),
                        age INTEGER,
                        active BOOLEAN,
                        birthdate DATE,
                        createdAt TIMESTAMP,
                        address JSON,
                        preferences JSON
                    )
                    """);
        }
        // Insert data using PreparedStatement
        String sql = "INSERT INTO users_updates VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS JSON), CAST(? AS JSON))";
        try (var pstmt = connection.prepareStatement(sql)) {
            insertUser(
                    pstmt,
                    1,
                    "John Doe",
                    "john.newemail@example.com",
                    31,
                    true,
                    "1990-01-01",
                    "2023-01-01",
                    null,
                    null);
            insertUser(pstmt, 2, "Jane Smith", "jane@example.com", 25, true, "1995-01-01", "2023-01-01", null, null);
            insertUser(pstmt, 11, "New User", "newuser@example.com", 28, true, "2000-01-01", "2023-01-08", null, null);
        }
    }

    /**
     * Closes the database connection if it is not null and not already closed.
     * For H2 in-memory databases, this ensures immediate shutdown and release of resources.
     *
     * @param connection the database connection to close
     * @throws SQLException if closing the connection fails
     */
    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            try {
                // Shutdown the H2 database to release all resources
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("SHUTDOWN");
                }
            } catch (SQLException e) {
                // SHUTDOWN may fail if connection is already closed
                // This is acceptable - we still want to close the connection
            } finally {
                connection.close();
            }
        }
    }
}
