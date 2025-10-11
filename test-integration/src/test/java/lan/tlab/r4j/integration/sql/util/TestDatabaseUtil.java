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
            stmt.execute(
                    "INSERT INTO users VALUES (3, 'Bob', 'bob@example.com', 15, false, '2005-01-01', '2023-01-01')");
            stmt.execute(
                    "INSERT INTO users VALUES (4, 'Alice', 'alice@example.com', 35, true, '1990-01-01', '2023-01-01')");
        }
    }

    /**
     * Creates an orders table with columns: id, customer_id, amount.
     *
     * @param connection the database connection
     * @throws SQLException if table creation fails
     */
    public static void createOrdersTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE orders (\"id\" INTEGER, \"customer_id\" INTEGER, \"amount\" INTEGER)");
        }
    }

    /**
     * Creates a sales table with columns: customer_id, amount.
     *
     * @param connection the database connection
     * @throws SQLException if table creation fails
     */
    public static void createSalesTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE sales (\"customer_id\" INTEGER, \"amount\" INTEGER)");
        }
    }

    /**
     * Creates an employees table with columns: department, salary.
     *
     * @param connection the database connection
     * @throws SQLException if table creation fails
     */
    public static void createEmployeesTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE employees (\"department\" VARCHAR(50), \"salary\" INTEGER)");
        }
    }

    /**
     * Creates a scores table with columns: student_id, score.
     *
     * @param connection the database connection
     * @throws SQLException if table creation fails
     */
    public static void createScoresTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE scores (\"student_id\" INTEGER, \"score\" INTEGER)");
        }
    }

    /**
     * Inserts sample data into the orders table.
     *
     * @param connection the database connection
     * @throws SQLException if insert fails
     */
    public static void insertSampleOrders(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO orders VALUES (1, 100, 50)");
            stmt.execute("INSERT INTO orders VALUES (2, 100, 150)");
            stmt.execute("INSERT INTO orders VALUES (3, 200, 75)");
            stmt.execute("INSERT INTO orders VALUES (4, 200, 25)");
            stmt.execute("INSERT INTO orders VALUES (5, 300, 300)");
        }
    }

    /**
     * Inserts sample data into the sales table.
     *
     * @param connection the database connection
     * @throws SQLException if insert fails
     */
    public static void insertSampleSales(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO sales VALUES (1, 100)");
            stmt.execute("INSERT INTO sales VALUES (1, 150)");
            stmt.execute("INSERT INTO sales VALUES (2, 200)");
            stmt.execute("INSERT INTO sales VALUES (2, 50)");
        }
    }

    /**
     * Inserts sample data into the employees table.
     *
     * @param connection the database connection
     * @throws SQLException if insert fails
     */
    public static void insertSampleEmployees(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO employees VALUES ('IT', 60000)");
            stmt.execute("INSERT INTO employees VALUES ('IT', 80000)");
            stmt.execute("INSERT INTO employees VALUES ('HR', 50000)");
            stmt.execute("INSERT INTO employees VALUES ('Sales', 70000)");
        }
    }

    /**
     * Inserts sample data into the scores table.
     *
     * @param connection the database connection
     * @throws SQLException if insert fails
     */
    public static void insertSampleScores(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO scores VALUES (1, 85)");
            stmt.execute("INSERT INTO scores VALUES (2, 92)");
            stmt.execute("INSERT INTO scores VALUES (3, 78)");
            stmt.execute("INSERT INTO scores VALUES (4, 95)");
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
