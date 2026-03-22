package io.github.auspis.fluentsql4j.test.util.database;

import java.util.List;

public final class DataUtil {

    private static final String WIDGET = "Widget";

    protected static final String BIRTHDATE_1990 = "1990-01-01";
    protected static final String BIRTHDATE_1995 = "1995-01-01";
    protected static final String CREATED_AT_2023 = "2023-01-01";

    public record UserRecord(
            long id,
            String name,
            String email,
            int age,
            boolean active,
            String birthdate,
            String createdAt,
            String address,
            String preferences) {}

    public record ProductRecord(int id, String name, double price, int quantity, String metadata) {}

    public record OrderRecord(int id, int userId, double total) {}

    public record CartItemRecord(long cartId, long productId, String productName, double unitPrice, int quantity) {}

    public record CustomerRecord(int id, String name, String country) {}

    // Shared data lists

    protected static final List<UserRecord> SAMPLE_USERS = List.of(
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

    protected static final List<UserRecord> SAMPLE_USERS_UPDATES = List.of(
            new UserRecord(
                    1, "John Doe", "john.newemail@example.com", 31, true, BIRTHDATE_1990, CREATED_AT_2023, null, null),
            new UserRecord(2, "Jane Smith", "jane@example.com", 25, true, BIRTHDATE_1995, CREATED_AT_2023, null, null),
            new UserRecord(11, "New User", "newuser@example.com", 28, true, "2000-01-01", "2023-01-08", null, null));

    protected static final List<ProductRecord> SAMPLE_PRODUCTS = List.of(
            new ProductRecord(1, WIDGET, 19.99, 100, null),
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

    protected static final List<OrderRecord> SAMPLE_ORDERS = List.of(
            new OrderRecord(1, 1, 10.99),
            new OrderRecord(2, 1, 29.99),
            new OrderRecord(3, 4, 39.99),
            new OrderRecord(4, 5, 49.99));

    protected static final List<CartItemRecord> SAMPLE_CART_ITEMS = List.of(
            new CartItemRecord(1L, 101L, WIDGET, 19.99, 2),
            new CartItemRecord(1L, 102L, "Gadget", 29.99, 1),
            new CartItemRecord(2L, 101L, WIDGET, 19.99, 3));

    protected static final List<CustomerRecord> SAMPLE_CUSTOMERS = List.of(
            new CustomerRecord(1, "Alice", "USA"),
            new CustomerRecord(2, "Bob", "UK"),
            new CustomerRecord(3, "Charlie", "USA"));

    private DataUtil() {}
}
