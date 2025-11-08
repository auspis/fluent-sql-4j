package lan.tlab.r4j.integration.sql.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.dsl.DSLRegistry;

/**
 * Utility class for creating and managing test database connections and tables in integration tests.
 */
public final class TestDatabaseUtil {

    private static final DSLRegistry registry = DSLRegistry.createWithServiceLoader();
    private static final DSL dsl = registry.dslFor("standardsql", "2008").orElseThrow();

    private TestDatabaseUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Returns a DSL instance configured for Standard SQL 2008 dialect.
     * This is the dialect used by H2 in integration tests.
     *
     * @return DSL instance for building SQL queries
     */
    public static DSL getDSL() {
        return dsl;
    }

    /**
     * Creates an H2 in-memory database connection with standard SQL mode.
     *
     * @return a new H2 database connection
     * @throws SQLException if connection cannot be established
     */
    public static Connection createH2Connection() throws SQLException {
        String jdbcUrl = "jdbc:h2:mem:testdb;MODE=REGULAR;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
        return DriverManager.getConnection(jdbcUrl, "sa", "");
    }

    /**
     * Creates an H2 in-memory database connection with MySQL compatibility mode.
     * This mode is useful for testing MySQL-specific features like JSON functions,
     * though H2 may not support all MySQL JSON functions.
     *
     * @return a new H2 database connection in MySQL mode
     * @throws SQLException if connection cannot be established
     */
    public static Connection createH2JsonConnection() throws SQLException {
        String jdbcUrl = "jdbc:h2:mem:testdb_json;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
        return DriverManager.getConnection(jdbcUrl, "sa", "");
    }

    /**
     * Creates a standard users table with columns: id, name, email, age, active, birthdate, createdAt.
     *
     * @param connection the database connection
     * @throws SQLException if table creation fails
     */
    public static void createUsersTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    """
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

    /**
     * Creates a standard products table with columns: id, name, price, quantity.
     *
     * @param connection the database connection
     * @throws SQLException if table creation fails
     */
    public static void createProductsTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    """
                    CREATE TABLE products (\
                    "id" INTEGER PRIMARY KEY, \
                    "name" VARCHAR(50), \
                    "price" DECIMAL(10,2), \
                    "quantity" INTEGER, \
                    "metadata" JSON)
                    """);
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
        try (Statement stmt = connection.createStatement()) {
            // Users without JSON data
            stmt.execute(
                    "INSERT INTO users VALUES (1, 'John Doe', 'john@example.com', 30, true, '1990-01-01', '2023-01-01', NULL, NULL)");
            stmt.execute(
                    "INSERT INTO users VALUES (2, 'Jane Smith', 'jane@example.com', 25, true, '1995-01-01', '2023-01-01', NULL, NULL)");
            stmt.execute(
                    "INSERT INTO users VALUES (3, 'Bob', 'bob@example.com', 15, false, '2005-01-01', '2023-01-01', NULL, NULL)");
            stmt.execute(
                    "INSERT INTO users VALUES (4, 'Alice', 'alice@example.com', 35, true, '1990-01-01', '2023-01-01', NULL, NULL)");
            stmt.execute(
                    "INSERT INTO users VALUES (5, 'Charlie', 'charlie@example.com', 30, true, '1991-01-01', '2023-01-02', NULL, NULL)");
            stmt.execute(
                    "INSERT INTO users VALUES (6, 'Diana', 'diana@example.com', 25, false, '1996-01-01', '2023-01-03', NULL, NULL)");
            stmt.execute(
                    "INSERT INTO users VALUES (7, 'Eve', 'eve@example.com', 40, true, '1985-01-01', '2023-01-04', NULL, NULL)");

            // Users with JSON data prepopulated
            stmt.execute(
                    """
                    INSERT INTO users VALUES (8, 'Frank', 'frank@example.com', 35, true, '1990-02-01', '2023-01-05', \
                    '{"street":"Via Roma 123","city":"Milan","zip":"20100","country":"Italy"}', \
                    '["email","sms"]')
                    """);
            stmt.execute(
                    """
                    INSERT INTO users VALUES (9, 'Grace', 'grace@example.com', 28, false, '1997-01-01', '2023-01-06', \
                    '{"street":"Via Torino 45","city":"Rome","zip":"00100","country":"Italy"}', \
                    '["email","push"]')
                    """);
            stmt.execute(
                    """
                    INSERT INTO users VALUES (10, 'Henry', 'henry@example.com', 30, true, '1995-01-01', '2023-01-07', \
                    '{"street":"Corso Vittorio 78","city":"Turin","zip":"10100","country":"Italy"}', \
                    '["sms","push","phone"]')
                    """);
        }
    }

    /**
     * Inserts sample data into the products table.
     * Some products have JSON metadata prepopulated for testing JSON operations.
     *
     * @param connection the database connection
     * @throws SQLException if insert fails
     */
    public static void insertSampleProducts(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Products without JSON metadata
            stmt.execute("INSERT INTO products VALUES (1, 'Widget', 19.99, 100, NULL)");
            stmt.execute("INSERT INTO products VALUES (2, 'Gadget', 29.99, 50, NULL)");

            // Products with JSON metadata prepopulated
            stmt.execute(
                    """
                    INSERT INTO products VALUES (3, 'Laptop', 999.99, 10, \
                    '{"tags":["electronics","computers"],"featured":true,"warranty":24}')
                    """);
            stmt.execute(
                    """
                    INSERT INTO products VALUES (4, 'Mouse', 15.99, 200, \
                    '{"tags":["electronics","accessories"],"featured":false,"color":"black"}')
                    """);
            stmt.execute(
                    """
                    INSERT INTO products VALUES (5, 'Keyboard', 49.99, 75, \
                    '{"tags":["electronics","accessories"],"featured":true,"backlit":true}')
                    """);
        }
    }

    /**
     * Closes the database connection if it is not null and not already closed.
     *
     * @param connection the database connection to close
     * @throws SQLException if closing the connection fails
     */
    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
