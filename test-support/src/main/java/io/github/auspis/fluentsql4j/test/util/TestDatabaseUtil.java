package io.github.auspis.fluentsql4j.test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
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

    // Shared data records

    private record UserRecord(
            int id,
            String name,
            String email,
            int age,
            boolean active,
            String birthdate,
            String createdAt,
            String address,
            String preferences) {}

    private record ProductRecord(int id, String name, double price, int quantity, String metadata) {}

    private record OrderRecord(int id, int userId, double total) {}

    private record CartItemRecord(long cartId, long productId, String productName, double unitPrice, int quantity) {}

    private record CustomerRecord(int id, String name, String country) {}

    // Shared date constants

    private static final String BIRTHDATE_1990 = "1990-01-01";
    private static final String BIRTHDATE_1995 = "1995-01-01";
    private static final String CREATED_AT_2023 = "2023-01-01";

    // Shared data lists

    private static final List<UserRecord> SAMPLE_USERS = List.of(
            new UserRecord(1, "John Doe", "john@example.com", 30, true, BIRTHDATE_1990, CREATED_AT_2023, null, null),
            new UserRecord(2, "Jane Smith", "jane@example.com", 25, true, BIRTHDATE_1995, CREATED_AT_2023, null, null),
            new UserRecord(3, "Bob", "bob@example.com", 15, false, "2005-01-01", CREATED_AT_2023, null, null),
            new UserRecord(4, "Alice", "alice@example.com", 35, true, BIRTHDATE_1990, CREATED_AT_2023, null, null),
            new UserRecord(5, "Charlie", "charlie@example.com", 30, true, "1991-01-01", "2023-01-02", null, null),
            new UserRecord(6, "Diana", "diana@example.com", 25, false, "1996-01-01", "2023-01-03", null, null),
            new UserRecord(7, "Eve", "eve@example.com", 40, true, "1985-01-01", "2023-01-04", null, null),
            new UserRecord(
                    8,
                    "Frank",
                    "frank@example.com",
                    35,
                    true,
                    "1990-02-01",
                    "2023-01-05",
                    "{\"street\":\"Via Roma 123\",\"city\":\"Milan\",\"zip\":\"20100\",\"country\":\"Italy\"}",
                    "[\"email\",\"sms\"]"),
            new UserRecord(
                    9,
                    "Grace",
                    "grace@example.com",
                    28,
                    false,
                    "1997-01-01",
                    "2023-01-06",
                    "{\"street\":\"Via Torino 45\",\"city\":\"Rome\",\"zip\":\"00100\",\"country\":\"Italy\"}",
                    "[\"email\",\"push\"]"),
            new UserRecord(
                    10,
                    "Henry",
                    "henry@example.com",
                    30,
                    true,
                    BIRTHDATE_1995,
                    "2023-01-07",
                    "{\"street\":\"Corso Vittorio 78\",\"city\":\"Turin\",\"zip\":\"10100\",\"country\":\"Italy\"}",
                    "[\"sms\",\"push\",\"phone\"]"));

    private static final List<UserRecord> SAMPLE_USERS_UPDATES = List.of(
            new UserRecord(
                    1, "John Doe", "john.newemail@example.com", 31, true, BIRTHDATE_1990, CREATED_AT_2023, null, null),
            new UserRecord(2, "Jane Smith", "jane@example.com", 25, true, BIRTHDATE_1995, CREATED_AT_2023, null, null),
            new UserRecord(11, "New User", "newuser@example.com", 28, true, "2000-01-01", "2023-01-08", null, null));

    private static final List<ProductRecord> SAMPLE_PRODUCTS = List.of(
            new ProductRecord(1, "Widget", 19.99, 100, null),
            new ProductRecord(2, "Gadget", 29.99, 50, null),
            new ProductRecord(
                    3,
                    "Laptop",
                    999.99,
                    10,
                    "{\"tags\":[\"electronics\",\"computers\"],\"featured\":true,\"warranty\":24}"),
            new ProductRecord(
                    4,
                    "Mouse",
                    15.99,
                    200,
                    "{\"tags\":[\"electronics\",\"accessories\"],\"featured\":false,\"color\":\"black\"}"),
            new ProductRecord(
                    5,
                    "Keyboard",
                    49.99,
                    75,
                    "{\"tags\":[\"electronics\",\"accessories\"],\"featured\":true,\"backlit\":true}"));

    private static final List<OrderRecord> SAMPLE_ORDERS = List.of(
            new OrderRecord(1, 1, 10.99),
            new OrderRecord(2, 1, 29.99),
            new OrderRecord(3, 4, 39.99),
            new OrderRecord(4, 5, 49.99));

    private static final List<CartItemRecord> SAMPLE_CART_ITEMS = List.of(
            new CartItemRecord(1L, 101L, "Widget", 19.99, 2),
            new CartItemRecord(1L, 102L, "Gadget", 29.99, 1),
            new CartItemRecord(2L, 101L, "Widget", 19.99, 3));

    private static final List<CustomerRecord> SAMPLE_CUSTOMERS = List.of(
            new CustomerRecord(1, "Alice", "USA"),
            new CustomerRecord(2, "Bob", "UK"),
            new CustomerRecord(3, "Charlie", "USA"));

    private TestDatabaseUtil() {}

    // Shared binding helpers

    private static void bindUser(PreparedStatement pstmt, UserRecord user) throws SQLException {
        pstmt.setInt(1, user.id());
        pstmt.setString(2, user.name());
        pstmt.setString(3, user.email());
        pstmt.setInt(4, user.age());
        pstmt.setBoolean(5, user.active());
        pstmt.setDate(6, java.sql.Date.valueOf(LocalDate.parse(user.birthdate())));
        pstmt.setTimestamp(
                7, Timestamp.valueOf(LocalDate.parse(user.createdAt()).atStartOfDay()));
        pstmt.setString(8, user.address());
        pstmt.setString(9, user.preferences());
        pstmt.executeUpdate();
    }

    private static void bindProduct(PreparedStatement pstmt, ProductRecord product) throws SQLException {
        pstmt.setInt(1, product.id());
        pstmt.setString(2, product.name());
        pstmt.setDouble(3, product.price());
        pstmt.setInt(4, product.quantity());
        pstmt.setString(5, product.metadata());
        pstmt.executeUpdate();
    }

    private static void bindOrder(PreparedStatement pstmt, OrderRecord order) throws SQLException {
        pstmt.setInt(1, order.id());
        pstmt.setInt(2, order.userId());
        pstmt.setDouble(3, order.total());
        pstmt.executeUpdate();
    }

    private static void bindCartItem(PreparedStatement pstmt, CartItemRecord item) throws SQLException {
        pstmt.setLong(1, item.cartId());
        pstmt.setLong(2, item.productId());
        pstmt.setString(3, item.productName());
        pstmt.setDouble(4, item.unitPrice());
        pstmt.setInt(5, item.quantity());
        pstmt.executeUpdate();
    }

    private static void executeSql(Connection connection, String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void insertUsers(Connection connection, String sql, List<UserRecord> users) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (UserRecord user : users) {
                bindUser(pstmt, user);
            }
        }
    }

    private static void insertProducts(Connection connection, String sql) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (ProductRecord product : SAMPLE_PRODUCTS) {
                bindProduct(pstmt, product);
            }
        }
    }

    private static void insertOrders(Connection connection, String sql) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (OrderRecord order : SAMPLE_ORDERS) {
                bindOrder(pstmt, order);
            }
        }
    }

    private static void insertCartItems(Connection connection, String sql) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (CartItemRecord item : SAMPLE_CART_ITEMS) {
                bindCartItem(pstmt, item);
            }
        }
    }

    private static void bindCustomer(PreparedStatement pstmt, CustomerRecord customer) throws SQLException {
        pstmt.setInt(1, customer.id());
        pstmt.setString(2, customer.name());
        pstmt.setString(3, customer.country());
        pstmt.executeUpdate();
    }

    private static void insertCustomers(Connection connection, String sql) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (CustomerRecord customer : SAMPLE_CUSTOMERS) {
                bindCustomer(pstmt, customer);
            }
        }
    }

    // =====================================================================
    // H2
    // =====================================================================

    /** H2 dialect-specific test database operations. */
    @SuppressWarnings("java:S2115")
    public static final class H2 {

        private static final String PASSWORD = System.getProperty("fluentsql4j.test.database.password", "");

        private H2() {}

        // Connection lifecycle

        /**
         * Creates an H2 in-memory database connection with standard SQL mode.
         * Each call gets a unique database name to avoid conflicts between tests.
         */
        public static Connection createConnection() throws SQLException {
            String uniqueDbName = "testdb_" + UUID.randomUUID().toString().replace("-", "");
            String jdbcUrl =
                    "jdbc:h2:mem:" + uniqueDbName + ";MODE=REGULAR;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
            return DriverManager.getConnection(jdbcUrl, "sa", PASSWORD);
        }

        /**
         * Creates an H2 in-memory database connection with MySQL compatibility mode.
         * Each call gets a unique database name to avoid conflicts between tests.
         */
        public static Connection createJsonConnection() throws SQLException {
            String uniqueDbName = "testdb_json_" + UUID.randomUUID().toString().replace("-", "");
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
                } catch (SQLException e) {
                    // SHUTDOWN may fail if connection is already closed
                } finally {
                    connection.close();
                }
            }
        }

        // users

        public static void createUsersTable(Connection connection) throws SQLException {
            executeSql(connection, """
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
            executeSql(connection, "DROP TABLE IF EXISTS users");
        }

        public static void truncateUsers(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE users");
        }

        public static void insertSampleUsers(Connection connection) throws SQLException {
            insertUsers(
                    connection,
                    "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS JSON), CAST(? AS JSON))",
                    SAMPLE_USERS);
        }

        // products

        public static void createProductsTable(Connection connection) throws SQLException {
            executeSql(connection, """
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
            executeSql(connection, "DROP TABLE IF EXISTS products");
        }

        public static void truncateProducts(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE products");
        }

        public static void insertSampleProducts(Connection connection) throws SQLException {
            insertProducts(connection, "INSERT INTO products VALUES (?, ?, ?, ?, ?)");
        }

        // orders

        public static void createOrdersTable(Connection connection) throws SQLException {
            executeSql(connection, """
                    CREATE TABLE orders (
                        "id" INTEGER PRIMARY KEY,
                        "userId" INTEGER,
                        "total" DECIMAL(10,2)
                    )
                    """);
        }

        public static void dropOrdersTable(Connection connection) throws SQLException {
            executeSql(connection, "DROP TABLE IF EXISTS orders");
        }

        public static void truncateOrders(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE orders");
        }

        public static void insertSampleOrders(Connection connection) throws SQLException {
            insertOrders(connection, "INSERT INTO orders VALUES (?, ?, ?)");
        }

        // users_updates

        public static void createUsersUpdatesTable(Connection connection) throws SQLException {
            executeSql(connection, """
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
            executeSql(connection, "DROP TABLE IF EXISTS users_updates");
        }

        public static void truncateUsersUpdates(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE users_updates");
        }

        public static void insertSampleUsersUpdates(Connection connection) throws SQLException {
            insertUsers(
                    connection,
                    "INSERT INTO users_updates VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS JSON), CAST(? AS JSON))",
                    SAMPLE_USERS_UPDATES);
        }

        // cart_items

        public static void createCartItemsTable(Connection connection) throws SQLException {
            executeSql(connection, """
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
            executeSql(connection, "DROP TABLE IF EXISTS cart_items");
        }

        public static void truncateCartItems(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE cart_items");
        }

        public static void insertSampleCartItems(Connection connection) throws SQLException {
            insertCartItems(
                    connection,
                    "INSERT INTO cart_items (cart_id, product_id, product_name, unit_price, quantity) VALUES (?, ?, ?, ?, ?)");
        }

        // customers

        public static void createCustomersTable(Connection connection) throws SQLException {
            executeSql(connection, """
                    CREATE TABLE customers (
                        "id" INTEGER PRIMARY KEY,
                        "name" VARCHAR(100) NOT NULL,
                        "country" VARCHAR(50) NOT NULL
                    )
                    """);
        }

        public static void dropCustomersTable(Connection connection) throws SQLException {
            executeSql(connection, "DROP TABLE IF EXISTS customers");
        }

        public static void truncateCustomers(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE customers");
        }

        public static void insertSampleCustomers(Connection connection) throws SQLException {
            insertCustomers(connection, "INSERT INTO customers VALUES (?, ?, ?)");
        }
    }

    // =====================================================================
    // MySQL
    // =====================================================================
    public static final class MySQL {

        private MySQL() {}

        // users

        public static void createUsersTable(Connection connection) throws SQLException {
            executeSql(connection, """
                    CREATE TABLE `users` (
                        `id` BIGINT PRIMARY KEY,
                        `name` VARCHAR(50),
                        `email` VARCHAR(100),
                        `age` INT,
                        `active` BOOLEAN,
                        `birthdate` DATE,
                        `createdAt` TIMESTAMP NULL,
                        `address` TEXT,
                        `preferences` TEXT
                    )
                    """);
        }

        public static void dropUsersTable(Connection connection) throws SQLException {
            executeSql(connection, "DROP TABLE IF EXISTS users");
        }

        public static void truncateUsers(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE users");
        }

        public static void insertSampleUsers(Connection connection) throws SQLException {
            insertUsers(connection, "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", SAMPLE_USERS);
        }

        // products

        public static void createProductsTable(Connection connection) throws SQLException {
            executeSql(connection, """
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
            executeSql(connection, "DROP TABLE IF EXISTS products");
        }

        public static void truncateProducts(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE products");
        }

        public static void insertSampleProducts(Connection connection) throws SQLException {
            insertProducts(connection, "INSERT INTO products VALUES (?, ?, ?, ?, ?)");
        }

        // orders

        public static void createOrdersTable(Connection connection) throws SQLException {
            executeSql(connection, """
                    CREATE TABLE `orders` (
                        `id` INT PRIMARY KEY,
                        `userId` INT,
                        `total` DECIMAL(10,2)
                    )
                    """);
        }

        public static void dropOrdersTable(Connection connection) throws SQLException {
            executeSql(connection, "DROP TABLE IF EXISTS orders");
        }

        public static void truncateOrders(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE orders");
        }

        public static void insertSampleOrders(Connection connection) throws SQLException {
            insertOrders(connection, "INSERT INTO orders VALUES (?, ?, ?)");
        }

        // users_updates

        public static void createUsersUpdatesTable(Connection connection) throws SQLException {
            executeSql(connection, """
                    CREATE TABLE `users_updates` (
                        `id` BIGINT PRIMARY KEY,
                        `name` VARCHAR(50),
                        `email` VARCHAR(100),
                        `age` INT,
                        `active` BOOLEAN,
                        `birthdate` DATE,
                        `createdAt` TIMESTAMP NULL,
                        `address` TEXT,
                        `preferences` TEXT
                    )
                    """);
        }

        public static void dropUsersUpdatesTable(Connection connection) throws SQLException {
            executeSql(connection, "DROP TABLE IF EXISTS users_updates");
        }

        public static void truncateUsersUpdates(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE users_updates");
        }

        public static void insertSampleUsersUpdates(Connection connection) throws SQLException {
            insertUsers(
                    connection, "INSERT INTO users_updates VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", SAMPLE_USERS_UPDATES);
        }

        // cart_items

        public static void createCartItemsTable(Connection connection) throws SQLException {
            executeSql(connection, """
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
            executeSql(connection, "DROP TABLE IF EXISTS cart_items");
        }

        public static void truncateCartItems(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE cart_items");
        }

        public static void insertSampleCartItems(Connection connection) throws SQLException {
            insertCartItems(
                    connection,
                    "INSERT INTO cart_items (cart_id, product_id, product_name, unit_price, quantity) VALUES (?, ?, ?, ?, ?)");
        }
    }

    // =====================================================================
    // PostgreSQL
    // =====================================================================

    /** PostgreSQL dialect-specific test database operations. */
    public static final class PostgreSQL {

        private PostgreSQL() {}

        // users

        public static void createUsersTable(Connection connection) throws SQLException {
            executeSql(connection, """
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
            executeSql(connection, "DROP TABLE IF EXISTS users");
        }

        public static void truncateUsers(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE users");
        }

        public static void insertSampleUsers(Connection connection) throws SQLException {
            insertUsers(connection, "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", SAMPLE_USERS);
        }

        // products

        public static void createProductsTable(Connection connection) throws SQLException {
            executeSql(connection, """
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
            executeSql(connection, "DROP TABLE IF EXISTS products");
        }

        public static void truncateProducts(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE products");
        }

        public static void insertSampleProducts(Connection connection) throws SQLException {
            insertProducts(connection, "INSERT INTO products VALUES (?, ?, ?, ?, ?)");
        }

        // orders

        public static void createOrdersTable(Connection connection) throws SQLException {
            executeSql(connection, """
                    CREATE TABLE "orders" (
                        "id" INT PRIMARY KEY,
                        "userId" INT,
                        "total" DECIMAL(10,2)
                    )
                    """);
        }

        public static void dropOrdersTable(Connection connection) throws SQLException {
            executeSql(connection, "DROP TABLE IF EXISTS orders");
        }

        public static void truncateOrders(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE orders");
        }

        public static void insertSampleOrders(Connection connection) throws SQLException {
            insertOrders(connection, "INSERT INTO orders VALUES (?, ?, ?)");
        }

        // users_updates

        public static void createUsersUpdatesTable(Connection connection) throws SQLException {
            executeSql(connection, """
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
            executeSql(connection, "DROP TABLE IF EXISTS users_updates");
        }

        public static void truncateUsersUpdates(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE users_updates");
        }

        public static void insertSampleUsersUpdates(Connection connection) throws SQLException {
            insertUsers(
                    connection, "INSERT INTO users_updates VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", SAMPLE_USERS_UPDATES);
        }

        // cart_items

        public static void createCartItemsTable(Connection connection) throws SQLException {
            executeSql(connection, """
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
            executeSql(connection, "DROP TABLE IF EXISTS cart_items");
        }

        public static void truncateCartItems(Connection connection) throws SQLException {
            executeSql(connection, "TRUNCATE TABLE cart_items");
        }

        public static void insertSampleCartItems(Connection connection) throws SQLException {
            insertCartItems(
                    connection,
                    "INSERT INTO cart_items (cart_id, product_id, product_name, unit_price, quantity) VALUES (?, ?, ?, ?, ?)");
        }
    }
}
