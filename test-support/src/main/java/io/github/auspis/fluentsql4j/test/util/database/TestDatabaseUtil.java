package io.github.auspis.fluentsql4j.test.util.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Utility class for creating and managing test database tables and sample data.
 *
 * <p>Provides dialect-specific inner classes {@link H2}, {@link MySQL}, and {@link PostgreSQL},
 * each exposing the same surface API: {@code createXxxTable}, {@code dropXxxTable},
 * {@code truncateXxx}, and {@code insertSampleXxx} for all supported tables.
 *
 * <p>Sample data is defined once as shared lists at the {@code TestDatabaseUtil} level.
 * Each dialect class reads from the same lists and produces dialect-specific SQL.
 * Adding a record to a shared list makes it available for all dialects automatically.
 */
public final class TestDatabaseUtil {

    private TestDatabaseUtil() {}

    public static final class H2 {

        private static final String PASSWORD = System.getProperty("fluentsql4j.test.database.password", "");

        private H2() {}

        /**
         * Creates an H2 in-memory database connection with standard SQL mode.
         */
        public static Connection createConnection() throws SQLException {
            String uniqueDbName = "testdb_" + UUID.randomUUID().toString().replace("-", "");
            String jdbcUrl =
                    "jdbc:h2:mem:" + uniqueDbName + ";MODE=REGULAR;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
            return DriverManager.getConnection(jdbcUrl, "sa", PASSWORD);
        }

        /**
         * Creates an H2 in-memory database connection with MySQL compatibility mode.
         */
        public static Connection createMySQLConnection() throws SQLException {
            String uniqueDbName = "testdb_mysql_" + UUID.randomUUID().toString().replace("-", "");
            String jdbcUrl =
                    "jdbc:h2:mem:" + uniqueDbName + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
            return DriverManager.getConnection(jdbcUrl, "sa", PASSWORD);
        }

        /**
         * Closes the H2 connection after executing SHUTDOWN to release all resources.
         */
        public static void closeConnection(Connection connection) throws SQLException {
            if (connection != null && !connection.isClosed()) {
                try {
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute("SHUTDOWN");
                    }
                } finally {
                    connection.close();
                }
            }
        }

        // users

        public static void createUsersTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE users (
                        "id" INTEGER PRIMARY KEY,
                        "name" VARCHAR(50),
                        "email" VARCHAR(100),
                        "age" INTEGER,
                        "active" BOOLEAN,
                        "birthdate" DATE,
                        "createdAt" TIMESTAMP,
                        "address" JSON,
                        "preferences" JSON
                    )
                    """);
        }

        public static void dropUsersTable(Connection connection) throws SQLException {
            StatementUtil.dropUsersTable(connection);
        }

        public static void truncateUsers(Connection connection) throws SQLException {
            StatementUtil.truncateUsersTable(connection);
        }

        public static void insertSampleUsers(Connection connection) throws SQLException {
            StatementUtil.insertUsers(
                    connection,
                    "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS JSON), CAST(? AS JSON))",
                    TestDataUtil.SAMPLE_USERS);
        }

        // products

        public static void createProductsTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE products (
                        "id" INTEGER PRIMARY KEY,
                        "name" VARCHAR(50),
                        "price" DECIMAL(10,2),
                        "quantity" INTEGER,
                        "metadata" JSON
                    )
                    """);
        }

        public static void dropProductsTable(Connection connection) throws SQLException {
            StatementUtil.dropProductsTable(connection);
        }

        public static void truncateProducts(Connection connection) throws SQLException {
            StatementUtil.truncateProductsTable(connection);
        }

        public static void insertSampleProducts(Connection connection) throws SQLException {
            StatementUtil.insertProducts(connection);
        }

        // orders

        public static void createOrdersTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE orders (
                        "id" INTEGER PRIMARY KEY,
                        "userId" INTEGER,
                        "total" DECIMAL(10,2)
                    )
                    """);
        }

        public static void dropOrdersTable(Connection connection) throws SQLException {
            StatementUtil.dropOrdersTable(connection);
        }

        public static void truncateOrders(Connection connection) throws SQLException {
            StatementUtil.truncateOrdersTable(connection);
        }

        public static void insertSampleOrders(Connection connection) throws SQLException {
            StatementUtil.insertOrders(connection);
        }

        // users_updates

        public static void createUsersUpdatesTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE users_updates (
                        "id" INTEGER PRIMARY KEY,
                        "name" VARCHAR(50),
                        "email" VARCHAR(100),
                        "age" INTEGER,
                        "active" BOOLEAN,
                        "birthdate" DATE,
                        "createdAt" TIMESTAMP,
                        "address" JSON,
                        "preferences" JSON
                    )
                    """);
        }

        public static void dropUsersUpdatesTable(Connection connection) throws SQLException {
            StatementUtil.dropUsersUpdatesTable(connection);
        }

        public static void truncateUsersUpdates(Connection connection) throws SQLException {
            StatementUtil.truncateUsersUpdatesTable(connection);
        }

        public static void insertSampleUsersUpdates(Connection connection) throws SQLException {
            StatementUtil.insertUsers(
                    connection,
                    "INSERT INTO users_updates VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS JSON), CAST(? AS JSON))",
                    TestDataUtil.SAMPLE_USERS_UPDATES);
        }

        // cart_items

        public static void createCartItemsTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE cart_items (
                        "id" BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                        "cart_id" BIGINT NOT NULL,
                        "product_id" BIGINT,
                        "product_name" VARCHAR(255) NOT NULL,
                        "unit_price" DOUBLE PRECISION NOT NULL,
                        "quantity" INTEGER NOT NULL DEFAULT 1
                    )
                    """);
        }

        public static void dropCartItemsTable(Connection connection) throws SQLException {
            StatementUtil.dropCartItemsTable(connection);
        }

        public static void truncateCartItems(Connection connection) throws SQLException {
            StatementUtil.truncateCartItemsTable(connection);
        }

        public static void insertSampleCartItems(Connection connection) throws SQLException {
            StatementUtil.insertCartItems(connection);
        }

        // customers

        public static void createCustomersTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE customers (
                        "id" INTEGER PRIMARY KEY,
                        "name" VARCHAR(100) NOT NULL,
                        "country" VARCHAR(50) NOT NULL
                    )
                    """);
        }

        public static void dropCustomersTable(Connection connection) throws SQLException {
            StatementUtil.dropCustomersTable(connection);
        }

        public static void truncateCustomers(Connection connection) throws SQLException {
            StatementUtil.truncateCustomersTable(connection);
        }

        public static void insertSampleCustomers(Connection connection) throws SQLException {
            StatementUtil.insertCustomers(connection, "INSERT INTO customers VALUES (?, ?, ?)");
        }
    }

    public static final class MySQL {

        private MySQL() {}

        // users

        public static void createUsersTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE `users` (
                        `id` BIGINT PRIMARY KEY,
                        `name` VARCHAR(50),
                        `email` VARCHAR(100),
                        `age` INT,
                        `active` BOOLEAN,
                        `birthdate` DATE,
                        `createdAt` TIMESTAMP NULL,
                        `address` JSON,
                        `preferences` JSON
                    )
                    """);
        }

        public static void dropUsersTable(Connection connection) throws SQLException {
            StatementUtil.dropUsersTable(connection);
        }

        public static void truncateUsers(Connection connection) throws SQLException {
            StatementUtil.truncateUsersTable(connection);
        }

        public static void insertSampleUsers(Connection connection) throws SQLException {
            StatementUtil.insertUsers(
                    connection, "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", TestDataUtil.SAMPLE_USERS);
        }

        // products

        public static void createProductsTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE `products` (
                        `id` INT PRIMARY KEY,
                        `name` VARCHAR(50),
                        `price` DECIMAL(10,2),
                        `quantity` INT,
                        `metadata` TEXT
                    )
                    """);
        }

        public static void dropProductsTable(Connection connection) throws SQLException {
            StatementUtil.dropProductsTable(connection);
        }

        public static void truncateProducts(Connection connection) throws SQLException {
            StatementUtil.truncateProductsTable(connection);
        }

        public static void insertSampleProducts(Connection connection) throws SQLException {
            StatementUtil.insertProducts(connection);
        }

        // orders

        public static void createOrdersTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE `orders` (
                        `id` INT PRIMARY KEY,
                        `userId` INT,
                        `total` DECIMAL(10,2)
                    )
                    """);
        }

        public static void dropOrdersTable(Connection connection) throws SQLException {
            StatementUtil.dropOrdersTable(connection);
        }

        public static void truncateOrders(Connection connection) throws SQLException {
            StatementUtil.truncateOrdersTable(connection);
        }

        public static void insertSampleOrders(Connection connection) throws SQLException {
            StatementUtil.insertOrders(connection);
        }

        // users_updates

        public static void createUsersUpdatesTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE `users_updates` (
                        `id` BIGINT PRIMARY KEY,
                        `name` VARCHAR(50),
                        `email` VARCHAR(100),
                        `age` INT,
                        `active` BOOLEAN,
                        `birthdate` DATE,
                        `createdAt` TIMESTAMP NULL,
                        `address` JSON,
                        `preferences` JSON
                    )
                    """);
        }

        public static void dropUsersUpdatesTable(Connection connection) throws SQLException {
            StatementUtil.dropUsersUpdatesTable(connection);
        }

        public static void truncateUsersUpdates(Connection connection) throws SQLException {
            StatementUtil.truncateUsersUpdatesTable(connection);
        }

        public static void insertSampleUsersUpdates(Connection connection) throws SQLException {
            StatementUtil.insertUsers(
                    connection,
                    "INSERT INTO users_updates VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    TestDataUtil.SAMPLE_USERS_UPDATES);
        }

        // cart_items

        public static void createCartItemsTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE `cart_items` (
                        `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                        `cart_id` BIGINT NOT NULL,
                        `product_id` BIGINT,
                        `product_name` VARCHAR(255) NOT NULL,
                        `unit_price` DOUBLE PRECISION NOT NULL,
                        `quantity` INT NOT NULL DEFAULT 1
                    )
                    """);
        }

        public static void dropCartItemsTable(Connection connection) throws SQLException {
            StatementUtil.dropCartItemsTable(connection);
        }

        public static void truncateCartItems(Connection connection) throws SQLException {
            StatementUtil.truncateCartItemsTable(connection);
        }

        public static void insertSampleCartItems(Connection connection) throws SQLException {
            StatementUtil.insertCartItems(connection);
        }
    }

    public static final class PostgreSQL {

        private PostgreSQL() {}

        // users

        public static void createUsersTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE "users" (
                        "id" BIGINT PRIMARY KEY,
                        "name" VARCHAR(50),
                        "email" VARCHAR(100),
                        "age" INT,
                        "active" BOOLEAN,
                        "birthdate" DATE,
                        "createdAt" TIMESTAMP,
                        "address" TEXT,
                        "preferences" TEXT
                    )
                    """);
        }

        public static void dropUsersTable(Connection connection) throws SQLException {
            StatementUtil.dropUsersTable(connection);
        }

        public static void truncateUsers(Connection connection) throws SQLException {
            StatementUtil.truncateUsersTable(connection);
        }

        public static void insertSampleUsers(Connection connection) throws SQLException {
            StatementUtil.insertUsers(
                    connection, "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", TestDataUtil.SAMPLE_USERS);
        }

        // products

        public static void createProductsTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE "products" (
                        "id" INT PRIMARY KEY,
                        "name" VARCHAR(50),
                        "price" DECIMAL(10,2),
                        "quantity" INT,
                        "metadata" TEXT
                    )
                    """);
        }

        public static void dropProductsTable(Connection connection) throws SQLException {
            StatementUtil.dropProductsTable(connection);
        }

        public static void truncateProducts(Connection connection) throws SQLException {
            StatementUtil.truncateProductsTable(connection);
        }

        public static void insertSampleProducts(Connection connection) throws SQLException {
            StatementUtil.insertProducts(connection);
        }

        // orders

        public static void createOrdersTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE "orders" (
                        "id" INT PRIMARY KEY,
                        "userId" INT,
                        "total" DECIMAL(10,2)
                    )
                    """);
        }

        public static void dropOrdersTable(Connection connection) throws SQLException {
            StatementUtil.dropOrdersTable(connection);
        }

        public static void truncateOrders(Connection connection) throws SQLException {
            StatementUtil.truncateOrdersTable(connection);
        }

        public static void insertSampleOrders(Connection connection) throws SQLException {
            StatementUtil.insertOrders(connection);
        }

        // users_updates

        public static void createUsersUpdatesTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE "users_updates" (
                        "id" BIGINT PRIMARY KEY,
                        "name" VARCHAR(50),
                        "email" VARCHAR(100),
                        "age" INT,
                        "active" BOOLEAN,
                        "birthdate" DATE,
                        "createdAt" TIMESTAMP,
                        "address" TEXT,
                        "preferences" TEXT
                    )
                    """);
        }

        public static void dropUsersUpdatesTable(Connection connection) throws SQLException {
            StatementUtil.dropUsersUpdatesTable(connection);
        }

        public static void truncateUsersUpdates(Connection connection) throws SQLException {
            StatementUtil.truncateUsersUpdatesTable(connection);
        }

        public static void insertSampleUsersUpdates(Connection connection) throws SQLException {
            StatementUtil.insertUsers(
                    connection,
                    "INSERT INTO users_updates VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    TestDataUtil.SAMPLE_USERS_UPDATES);
        }

        // cart_items

        public static void createCartItemsTable(Connection connection) throws SQLException {
            JdbcUtil.executeSql(connection, """
                    CREATE TABLE "cart_items" (
                        "id" BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                        "cart_id" BIGINT NOT NULL,
                        "product_id" BIGINT,
                        "product_name" VARCHAR(255) NOT NULL,
                        "unit_price" DOUBLE PRECISION NOT NULL,
                        "quantity" INTEGER NOT NULL DEFAULT 1
                    )
                    """);
        }

        public static void dropCartItemsTable(Connection connection) throws SQLException {
            StatementUtil.dropCartItemsTable(connection);
        }

        public static void truncateCartItems(Connection connection) throws SQLException {
            StatementUtil.truncateCartItemsTable(connection);
        }

        public static void insertSampleCartItems(Connection connection) throws SQLException {
            StatementUtil.insertCartItems(connection);
        }
    }
}
