package integration.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static util.JsonAssert.assertThatJson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.util.annotation.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestDatabaseUtil;

/**
 * Integration tests for UpdateBuilder with H2 in-memory database.
 * Tests the complete integration between DSL UpdateBuilder, SQL rendering,
 * PreparedStatement creation, and actual database update operations.
 */
@IntegrationTest
class UpdateBuilderIntegrationTest {

    private Connection connection;
    private DSL dsl;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        dsl = TestDatabaseUtil.getDSL();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.createProductsTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
        TestDatabaseUtil.insertSampleProducts(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void singleColumnWithWhereCondition() throws SQLException {
        PreparedStatement ps = dsl.update("users")
                .set("name", "Johnny")
                .where()
                .column("id")
                .eq(1)
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM users WHERE id = 1")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Johnny");
        }

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM users WHERE id = 2")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Jane Smith");
        }
    }

    @Test
    void multipleColumns() throws SQLException {
        PreparedStatement ps = dsl.update("users")
                .set("name", "Johnny")
                .set("age", 35)
                .where()
                .column("id")
                .eq(1)
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name, age FROM users WHERE id = 1")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Johnny");
            assertThat(rs.getInt(2)).isEqualTo(35);
        }
    }

    @Test
    void andCondition() throws SQLException {
        PreparedStatement ps = dsl.update("users")
                .set("email", "jane.updated@example.com")
                .where()
                .column("age")
                .gt(18)
                .and()
                .column("name")
                .eq("Jane Smith")
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT email FROM users WHERE name = 'Jane Smith'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("jane.updated@example.com");
        }

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT email FROM users WHERE name = 'John Doe'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("john@example.com");
        }
    }

    @Test
    void orCondition() throws SQLException {
        PreparedStatement ps = dsl.update("users")
                .set("email", "updated@example.com")
                .where()
                .column("name")
                .eq("John Doe")
                .or()
                .column("name")
                .eq("Jane Smith")
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(2);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE email = 'updated@example.com'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(2);
        }
    }

    @Test
    void likeCondition() throws SQLException {
        PreparedStatement ps = dsl.update("users")
                .set("email", "verified@example.com")
                .where()
                .column("email")
                .like("%example.com")
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(10);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE email = 'verified@example.com'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(10);
        }
    }

    @Test
    void allRows() throws SQLException {
        PreparedStatement ps =
                dsl.update("users").set("email", "updated@example.com").buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(10);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE email = 'updated@example.com'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(10);
        }
    }

    @Test
    void numberComparison() throws SQLException {
        PreparedStatement ps = dsl.update("users")
                .set("name", "Older User")
                .where()
                .column("age")
                .gt(30)
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(3); // Alice (35), Frank (35), Eve (40)

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE name = 'Older User'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(3);
        }

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM users WHERE name = 'Jane Smith'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Jane Smith");
        }
    }

    @Test
    void booleanUpdate() throws SQLException {
        PreparedStatement ps = dsl.update("users")
                .set("active", false)
                .where()
                .column("name")
                .eq("John Doe")
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the update
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT active FROM users WHERE name = 'John Doe'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getBoolean(1)).isFalse();
        }

        // Verify Jane unchanged
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT active FROM users WHERE name = 'Jane Smith'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getBoolean(1)).isTrue();
        }
    }

    @Test
    void updateJsonObjectValue() throws SQLException {
        // Frank (id=8) has initial Milan address prepopulated
        // Update his address to a Turin address
        String updatedAddress =
                """
                {"street":"Via Milano 25","city":"Turin","zip":"10100","country":"Italy"}""";

        PreparedStatement updatePs = dsl.update("users")
                .set("address", updatedAddress)
                .where()
                .column("id")
                .eq(8)
                .buildPreparedStatement(connection);

        int rowsAffected = updatePs.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the JSON was updated correctly
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name, address FROM users WHERE id = 8")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(8);
            assertThat(rs.getString("name")).isEqualTo("Frank");
            String retrievedAddress = rs.getString("address");
            assertThatJson(retrievedAddress)
                    .isEqualToJson(
                            """
                    {
                        "street": "Via Milano 25",
                        "city": "Turin",
                        "zip": "10100",
                        "country": "Italy"
                    }
                    """);
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void updateJsonArrayValue() throws SQLException {
        // Frank (id=8) has initial preferences ["email","sms"] prepopulated
        // Update his preferences to a different array
        String updatedPreferences = """
                ["email","push","phone"]""";

        PreparedStatement updatePs = dsl.update("users")
                .set("preferences", updatedPreferences)
                .where()
                .column("id")
                .eq(8)
                .buildPreparedStatement(connection);

        int rowsAffected = updatePs.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the JSON array was updated correctly
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name, preferences FROM users WHERE id = 8")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(8);
            assertThat(rs.getString("name")).isEqualTo("Frank");
            String retrievedPreferences = rs.getString("preferences");
            assertThatJson(retrievedPreferences)
                    .isEqualToJson("""
                    ["email", "push", "phone"]
                    """);
            assertThat(rs.next()).isFalse();
        }

        // Also test with products table - Laptop (id=3) has metadata prepopulated
        // Update its metadata with different tags
        String productMetadata = """
                {"tags":["electronics","sale"],"featured":false,"discount":15}""";

        PreparedStatement updateProductPs = dsl.update("products")
                .set("metadata", productMetadata)
                .where()
                .column("id")
                .eq(3)
                .buildPreparedStatement(connection);

        int productRowsAffected = updateProductPs.executeUpdate();
        assertThat(productRowsAffected).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name, metadata FROM products WHERE id = 3")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(3);
            assertThat(rs.getString("name")).isEqualTo("Laptop");
            String retrievedMetadata = rs.getString("metadata");
            assertThatJson(retrievedMetadata)
                    .isEqualToJson(
                            """
                    {
                        "tags": ["electronics", "sale"],
                        "featured": false,
                        "discount": 15
                    }
                    """);
            assertThat(rs.next()).isFalse();
        }
    }
}
