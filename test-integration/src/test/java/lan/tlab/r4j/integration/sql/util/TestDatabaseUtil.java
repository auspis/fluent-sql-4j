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
                    "createdAt" TIMESTAMP)
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
                    "quantity" INTEGER)
                    """);
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
            stmt.execute(
                    "INSERT INTO users VALUES (5, 'Charlie', 'charlie@example.com', 30, true, '1991-01-01', '2023-01-02')");
            stmt.execute(
                    "INSERT INTO users VALUES (6, 'Diana', 'diana@example.com', 25, false, '1996-01-01', '2023-01-03')");
            stmt.execute(
                    "INSERT INTO users VALUES (7, 'Eve', 'eve@example.com', 40, true, '1985-01-01', '2023-01-04')");
            stmt.execute(
                    "INSERT INTO users VALUES (8, 'Frank', 'frank@example.com', 35, true, '1990-02-01', '2023-01-05')");
            stmt.execute(
                    "INSERT INTO users VALUES (9, 'Grace', 'grace@example.com', 28, false, '1997-01-01', '2023-01-06')");
            stmt.execute(
                    "INSERT INTO users VALUES (10, 'Henry', 'henry@example.com', 30, true, '1995-01-01', '2023-01-07')");
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
