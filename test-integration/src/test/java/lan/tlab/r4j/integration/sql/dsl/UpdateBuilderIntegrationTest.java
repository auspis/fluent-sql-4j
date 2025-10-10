package lan.tlab.r4j.integration.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lan.tlab.r4j.integration.sql.util.TestDatabaseUtil;
import lan.tlab.r4j.sql.dsl.DSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateBuilderIntegrationTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.createProductsTable(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void updateSingleColumnWithWhereConditionAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (1, 'John', 'john@example.com')");
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (2, 'Jane', 'jane@example.com')");
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (3, 'Bob', 'bob@example.com')");
        }

        // Update using DSL
        PreparedStatement ps =
                DSL.update("users").set("name", "Johnny").where("id").eq(1).buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the update
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM users WHERE id = 1")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Johnny");
        }

        // Verify other records unchanged
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM users WHERE id = 2")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Jane");
        }
    }

    @Test
    void updateMultipleColumnsAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO users (id, name, email, age) VALUES (1, 'John', 'john@example.com', 25)");
        }

        // Update using DSL
        PreparedStatement ps = DSL.update("users")
                .set("name", "Johnny")
                .set("age", 30)
                .where("id")
                .eq(1)
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the update
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name, age FROM users WHERE id = 1")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Johnny");
            assertThat(rs.getInt(2)).isEqualTo(30);
        }
    }

    @Test
    void updateWithAndConditionAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO users (id, name, email, age) VALUES (1, 'John', 'john@example.com', 20)");
            stmt.executeUpdate("INSERT INTO users (id, name, email, age) VALUES (2, 'Jane', 'jane@example.com', 25)");
            stmt.executeUpdate("INSERT INTO users (id, name, email, age) VALUES (3, 'Bob', 'bob@example.com', 30)");
        }

        // Update users with age greater than 18 and name equals 'Bob'
        PreparedStatement ps = DSL.update("users")
                .set("email", "bob.updated@example.com")
                .where("age")
                .gt(18)
                .and("name")
                .eq("Bob")
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the update
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT email FROM users WHERE name = 'Bob'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("bob.updated@example.com");
        }

        // Verify others unchanged
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT email FROM users WHERE name = 'Jane'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("jane@example.com");
        }
    }

    @Test
    void updateWithOrConditionAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (1, 'John', 'john@example.com')");
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (2, 'Jane', 'jane@example.com')");
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (3, 'Bob', 'bob@example.com')");
        }

        // Update users with name 'John' or 'Jane'
        PreparedStatement ps = DSL.update("users")
                .set("email", "updated@example.com")
                .where("name")
                .eq("John")
                .or("name")
                .eq("Jane")
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(2);

        // Verify the updates
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE email = 'updated@example.com'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(2);
        }

        // Verify Bob unchanged
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT email FROM users WHERE name = 'Bob'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("bob@example.com");
        }
    }

    @Test
    void updateWithLikeConditionAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (1, 'John', 'john@example.com')");
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (2, 'Jane', 'jane@example.com')");
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (3, 'Bob', 'bob@test.com')");
        }

        // Update users with email like '%example.com'
        PreparedStatement ps = DSL.update("users")
                .set("email", "verified@example.com")
                .where("email")
                .like("%example.com")
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(2);

        // Verify the updates
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE email = 'verified@example.com'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(2);
        }

        // Verify Bob unchanged
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT email FROM users WHERE name = 'Bob'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("bob@test.com");
        }
    }

    @Test
    void updateAllRowsAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO products (id, name, price) VALUES (1, 'Product A', 10.00)");
            stmt.executeUpdate("INSERT INTO products (id, name, price) VALUES (2, 'Product B', 20.00)");
            stmt.executeUpdate("INSERT INTO products (id, name, price) VALUES (3, 'Product C', 30.00)");
        }

        // Update all products
        PreparedStatement ps = DSL.update("products").set("price", 99.99).buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(3);

        // Verify all updated
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products WHERE price = 99.99")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(3);
        }
    }

    @Test
    void updateWithNumberComparisonAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO products (id, name, price) VALUES (1, 'Product A', 10.00)");
            stmt.executeUpdate("INSERT INTO products (id, name, price) VALUES (2, 'Product B', 20.00)");
            stmt.executeUpdate("INSERT INTO products (id, name, price) VALUES (3, 'Product C', 30.00)");
        }

        // Update products with price > 15
        PreparedStatement ps = DSL.update("products")
                .set("name", "Expensive Product")
                .where("price")
                .gt(15.00)
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(2);

        // Verify the updates
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products WHERE name = 'Expensive Product'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(2);
        }

        // Verify Product A unchanged
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM products WHERE id = 1")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Product A");
        }
    }

    @Test
    void updateWithBooleanAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO users (id, name, active) VALUES (1, 'John', false)");
            stmt.executeUpdate("INSERT INTO users (id, name, active) VALUES (2, 'Jane', false)");
        }

        // Update active status
        PreparedStatement ps =
                DSL.update("users").set("active", true).where("name").eq("John").buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the update
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT active FROM users WHERE name = 'John'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getBoolean(1)).isTrue();
        }

        // Verify Jane unchanged
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT active FROM users WHERE name = 'Jane'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getBoolean(1)).isFalse();
        }
    }
}
