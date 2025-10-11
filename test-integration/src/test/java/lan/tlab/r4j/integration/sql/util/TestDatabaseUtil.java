package lan.tlab.r4j.integration.sql.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for creating and managing test database connections and tables in integration tests.
 */
public final class TestDatabaseUtil {

    private TestDatabaseUtil() {
        // Utility class - prevent instantiation
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
     * Creates a standard users table with columns: id, name, email, age, active, birthdate, createdAt.
     *
     * @param connection the database connection
     * @throws SQLException if table creation fails
     */
    public static void createUsersTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "CREATE TABLE users (\"id\" INTEGER PRIMARY KEY, \"name\" VARCHAR(50), \"email\" VARCHAR(100), \"age\" INTEGER, \"active\" BOOLEAN, \"birthdate\" DATE, \"createdAt\" TIMESTAMP)");
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
                    "CREATE TABLE products (\"id\" INTEGER PRIMARY KEY, \"name\" VARCHAR(50), \"price\" DECIMAL(10,2), \"quantity\" INTEGER)");
        }
    }

    /**
     * Inserts sample data into the users table.
     *
     * @param connection the database connection
     * @throws SQLException if insert fails
     */
    public static void insertSampleUsers(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "INSERT INTO users VALUES (1, 'John Doe', 'john@example.com', 30, true, '1990-01-01', '2023-01-01')");
            stmt.execute(
                    "INSERT INTO users VALUES (2, 'Jane Smith', 'jane@example.com', 25, true, '1995-01-01', '2023-01-01')");
        }
    }

    /**
     * Inserts basic test users for update/delete tests.
     *
     * @param connection the database connection
     * @throws SQLException if insert fails
     */
    public static void insertBasicTestUsers(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO users (id, name, email) VALUES (1, 'John', 'john@example.com')");
            stmt.execute("INSERT INTO users (id, name, email) VALUES (2, 'Jane', 'jane@example.com')");
            stmt.execute("INSERT INTO users (id, name, email) VALUES (3, 'Bob', 'bob@example.com')");
        }
    }

    /**
     * Inserts test users with age for conditional tests.
     *
     * @param connection the database connection
     * @throws SQLException if insert fails
     */
    public static void insertTestUsersWithAge(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO users (id, name, email, age) VALUES (1, 'John', 'john@example.com', 20)");
            stmt.execute("INSERT INTO users (id, name, email, age) VALUES (2, 'Jane', 'jane@example.com', 25)");
            stmt.execute("INSERT INTO users (id, name, email, age) VALUES (3, 'Bob', 'bob@example.com', 30)");
        }
    }

    /**
     * Inserts test products with price.
     *
     * @param connection the database connection
     * @throws SQLException if insert fails
     */
    public static void insertTestProducts(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO products (id, name, price) VALUES (1, 'Product A', 10.00)");
            stmt.execute("INSERT INTO products (id, name, price) VALUES (2, 'Product B', 20.00)");
            stmt.execute("INSERT INTO products (id, name, price) VALUES (3, 'Product C', 30.00)");
        }
    }
}
