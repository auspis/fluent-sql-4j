package integration.dsl;

import static lan.tlab.r4j.jdsql.test.JsonAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.plugin.util.StandardSqlUtil;
import lan.tlab.r4j.jdsql.test.util.TestDatabaseUtil;
import lan.tlab.r4j.jdsql.test.util.annotation.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for InsertBuilder with H2 in-memory database.
 * Tests the complete integration between DSL InsertBuilder, SQL rendering,
 * PreparedStatement creation, and actual database insert operations.
 */
@IntegrationTest
class InsertBuilderIntegrationTest {

    private Connection connection;
    private DSL dsl;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        dsl = StandardSqlUtil.dsl();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.createProductsTable(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void singleStringValue() throws SQLException {
        PreparedStatement ps =
                dsl.insertInto("users").set("id", 1).set("name", "John").build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name FROM users WHERE id = 1")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(1);
            assertThat(rs.getString("name")).isEqualTo("John");
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void multipleColumnsWithMixedTypes() throws SQLException {
        PreparedStatement ps = dsl.insertInto("users")
                .set("id", 2)
                .set("name", "Jane")
                .set("email", "jane@example.com")
                .set("age", 25)
                .set("active", true)
                .set("birthdate", LocalDate.of(1999, 5, 15))
                .set("createdAt", LocalDateTime.of(2023, 10, 10, 12, 0, 0))
                .build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id = 2")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(2);
            assertThat(rs.getString("name")).isEqualTo("Jane");
            assertThat(rs.getString("email")).isEqualTo("jane@example.com");
            assertThat(rs.getInt("age")).isEqualTo(25);
            assertThat(rs.getBoolean("active")).isTrue();
            assertThat(rs.getDate("birthdate").toLocalDate()).isEqualTo(LocalDate.of(1999, 5, 15));
            assertThat(rs.getTimestamp("createdAt").toLocalDateTime())
                    .isEqualTo(LocalDateTime.of(2023, 10, 10, 12, 0, 0));
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void nullValues() throws SQLException {
        PreparedStatement ps = dsl.insertInto("users")
                .set("id", 3)
                .set("name", "Bob")
                .set("email", (String) null)
                .set("age", (Integer) null)
                .build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name, email, age FROM users WHERE id = 3")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(3);
            assertThat(rs.getString("name")).isEqualTo("Bob");
            assertThat(rs.getString("email")).isNull();
            assertThat(rs.getObject("age")).isNull();
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void decimalValues() throws SQLException {
        PreparedStatement ps = dsl.insertInto("products")
                .set("id", 1)
                .set("name", "Widget")
                .set("price", 19.99)
                .set("quantity", 100)
                .build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM products WHERE id = 1")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(1);
            assertThat(rs.getString("name")).isEqualTo("Widget");
            assertThat(rs.getBigDecimal("price")).isEqualByComparingTo("19.99");
            assertThat(rs.getInt("quantity")).isEqualTo(100);
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void multipleRows() throws SQLException {
        PreparedStatement ps1 = dsl.insertInto("users")
                .set("id", 10)
                .set("name", "Alice")
                .set("active", true)
                .build(connection);
        assertThat(ps1.executeUpdate()).isEqualTo(1);

        // Insert second row
        PreparedStatement ps2 = dsl.insertInto("users")
                .set("id", 11)
                .set("name", "Charlie")
                .set("active", false)
                .build(connection);
        assertThat(ps2.executeUpdate()).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name, active FROM users WHERE id >= 10 ORDER BY id")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(10);
            assertThat(rs.getString("name")).isEqualTo("Alice");
            assertThat(rs.getBoolean("active")).isTrue();

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(11);
            assertThat(rs.getString("name")).isEqualTo("Charlie");
            assertThat(rs.getBoolean("active")).isFalse();

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void typeSpecificMethods() throws SQLException {
        PreparedStatement ps1 = dsl.insertInto("users")
                .set("id", 20)
                .set("name", "David")
                .set("email", "david@example.com")
                .build(connection);
        assertThat(ps1.executeUpdate()).isEqualTo(1);

        PreparedStatement ps2 = dsl.insertInto("products")
                .set("id", 20)
                .set("price", 29.99)
                .set("quantity", 50)
                .build(connection);
        assertThat(ps2.executeUpdate()).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name, email FROM users WHERE id = 20")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("David");
            assertThat(rs.getString("email")).isEqualTo("david@example.com");
        }

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT price, quantity FROM products WHERE id = 20")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getBigDecimal("price")).isEqualByComparingTo("29.99");
            assertThat(rs.getInt("quantity")).isEqualTo(50);
        }
    }

    @Test
    void setMethod() throws SQLException {
        PreparedStatement ps = dsl.insertInto("users")
                .set("id", 30)
                .set("name", "Emily")
                .set("email", "emily@example.com")
                .set("age", 28)
                .set("active", true)
                .set("birthdate", LocalDate.of(1996, 3, 20))
                .set("createdAt", LocalDateTime.of(2023, 10, 10, 14, 30, 0))
                .build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the insert
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id = 30")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(30);
            assertThat(rs.getString("name")).isEqualTo("Emily");
            assertThat(rs.getString("email")).isEqualTo("emily@example.com");
            assertThat(rs.getInt("age")).isEqualTo(28);
            assertThat(rs.getBoolean("active")).isTrue();
            assertThat(rs.getDate("birthdate").toLocalDate()).isEqualTo(LocalDate.of(1996, 3, 20));
            assertThat(rs.getTimestamp("createdAt").toLocalDateTime())
                    .isEqualTo(LocalDateTime.of(2023, 10, 10, 14, 30, 0));
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void insertJsonObjectValue() throws SQLException {
        String addressJson = "{\"street\":\"Via Roma 123\",\"city\":\"Milan\",\"zip\":\"20100\",\"country\":\"Italy\"}";

        PreparedStatement ps = dsl.insertInto("users")
                .set("id", 100)
                .set("name", "Marco")
                .set("email", "marco@example.com")
                .set("age", 35)
                .set("address", addressJson)
                .build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the JSON was inserted correctly
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name, address FROM users WHERE id = 100")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(100);
            assertThat(rs.getString("name")).isEqualTo("Marco");
            String retrievedAddress = rs.getString("address");
            assertThatJson(retrievedAddress).isEqualToJson("""
                    {
                        "street": "Via Roma 123",
                        "city": "Milan",
                        "zip": "20100",
                        "country": "Italy"
                    }
                    """);
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void insertJsonArrayValue() throws SQLException {
        String preferencesJson = "[\"email\",\"sms\",\"push\"]";
        String metadataJson = "{\"tags\":[\"electronics\",\"new\"],\"featured\":true}";

        PreparedStatement ps = dsl.insertInto("users")
                .set("id", 101)
                .set("name", "Laura")
                .set("email", "laura@example.com")
                .set("preferences", preferencesJson)
                .build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the JSON array was inserted correctly
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name, preferences FROM users WHERE id = 101")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(101);
            assertThat(rs.getString("name")).isEqualTo("Laura");
            String retrievedPreferences = rs.getString("preferences");
            assertThatJson(retrievedPreferences).isEqualToJson("""
                    ["email", "sms", "push"]
                    """);
            assertThat(rs.next()).isFalse();
        }

        // Also test with products table
        PreparedStatement ps2 = dsl.insertInto("products")
                .set("id", 200)
                .set("name", "Laptop")
                .set("price", 999.99)
                .set("metadata", metadataJson)
                .build(connection);

        int rowsAffected2 = ps2.executeUpdate();
        assertThat(rowsAffected2).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name, metadata FROM products WHERE id = 200")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(200);
            assertThat(rs.getString("name")).isEqualTo("Laptop");
            String retrievedMetadata = rs.getString("metadata");
            assertThat(retrievedMetadata)
                    .contains("electronics")
                    .contains("new")
                    .contains("featured");
            assertThat(rs.next()).isFalse();
        }
    }
}
