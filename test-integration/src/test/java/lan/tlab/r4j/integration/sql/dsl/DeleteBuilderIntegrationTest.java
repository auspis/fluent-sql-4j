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

class DeleteBuilderIntegrationTest {

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
    void deleteWithWhereConditionAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (1, 'John', 'john@example.com')");
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (2, 'Jane', 'jane@example.com')");
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (3, 'Bob', 'bob@example.com')");
        }

        // Delete using DSL
        PreparedStatement ps = DSL.deleteFrom("users").where("id").eq(2).buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the delete
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(2);
        }

        // Verify the deleted record is gone
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id FROM users WHERE id = 2")) {
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void deleteWithMultipleConditionsAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO users (id, name, email, age) VALUES (1, 'John', 'john@example.com', 20)");
            stmt.executeUpdate("INSERT INTO users (id, name, email, age) VALUES (2, 'Jane', 'jane@example.com', 25)");
            stmt.executeUpdate("INSERT INTO users (id, name, email, age) VALUES (3, 'Bob', 'bob@example.com', 15)");
            stmt.executeUpdate("INSERT INTO users (id, name, email, age) VALUES (4, 'Alice', 'alice@example.com', 30)");
        }

        // Delete users with age less than 18
        PreparedStatement ps = DSL.deleteFrom("users").where("age").lt(18).buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the delete
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(3);
        }
    }

    @Test
    void deleteWithAndConditionAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO users (id, name, email, age) VALUES (1, 'John', 'john@example.com', 20)");
            stmt.executeUpdate("INSERT INTO users (id, name, email, age) VALUES (2, 'Jane', 'jane@example.com', 25)");
            stmt.executeUpdate("INSERT INTO users (id, name, email, age) VALUES (3, 'Bob', 'bob@example.com', 30)");
        }

        // Delete users with age greater than 18 and name equals 'Bob'
        PreparedStatement ps = DSL.deleteFrom("users")
                .where("age")
                .gt(18)
                .and("name")
                .eq("Bob")
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the delete
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(2);
        }

        // Verify Bob is gone
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM users WHERE name = 'Bob'")) {
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void deleteWithOrConditionAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (1, 'John', 'john@example.com')");
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (2, 'Jane', 'jane@example.com')");
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (3, 'Bob', 'bob@example.com')");
        }

        // Delete users with name 'John' or 'Jane'
        PreparedStatement ps = DSL.deleteFrom("users")
                .where("name")
                .eq("John")
                .or("name")
                .eq("Jane")
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(2);

        // Verify the delete
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(1);
        }

        // Verify only Bob remains
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM users WHERE name = 'Bob'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Bob");
        }
    }

    @Test
    void deleteWithStringLikeAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (1, 'John', 'john@example.com')");
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (2, 'Jane', 'jane@example.com')");
            stmt.executeUpdate("INSERT INTO users (id, name, email) VALUES (3, 'Bob', 'bob@test.com')");
        }

        // Delete users with email like '%example.com'
        PreparedStatement ps =
                DSL.deleteFrom("users").where("email").like("%example.com").buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(2);

        // Verify the delete
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(1);
        }

        // Verify only Bob remains
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT email FROM users WHERE name = 'Bob'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("bob@test.com");
        }
    }

    @Test
    void deleteAllRowsAndVerify() throws SQLException {
        // Insert test data
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO products (id, name) VALUES (1, 'Product A')");
            stmt.executeUpdate("INSERT INTO products (id, name) VALUES (2, 'Product B')");
            stmt.executeUpdate("INSERT INTO products (id, name) VALUES (3, 'Product C')");
        }

        // Delete all products
        PreparedStatement ps = DSL.deleteFrom("products").buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(3);

        // Verify all deleted
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(0);
        }
    }
}
