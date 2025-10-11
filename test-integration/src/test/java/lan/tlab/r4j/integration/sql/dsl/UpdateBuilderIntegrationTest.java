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
        TestDatabaseUtil.insertSampleUsers(connection);

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
            assertThat(rs.getString(1)).isEqualTo("Jane Smith");
        }
    }

    @Test
    void updateMultipleColumnsAndVerify() throws SQLException {
        TestDatabaseUtil.insertSampleUsers(connection);

        // Update using DSL
        PreparedStatement ps = DSL.update("users")
                .set("name", "Johnny")
                .set("age", 35)
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
            assertThat(rs.getInt(2)).isEqualTo(35);
        }
    }

    @Test
    void updateWithAndConditionAndVerify() throws SQLException {
        TestDatabaseUtil.insertSampleUsers(connection);

        // Update users with age greater than 18 and name equals 'Jane Smith'
        PreparedStatement ps = DSL.update("users")
                .set("email", "jane.updated@example.com")
                .where("age")
                .gt(18)
                .and("name")
                .eq("Jane Smith")
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the update
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT email FROM users WHERE name = 'Jane Smith'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("jane.updated@example.com");
        }

        // Verify others unchanged
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT email FROM users WHERE name = 'John Doe'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("john@example.com");
        }
    }

    @Test
    void updateWithOrConditionAndVerify() throws SQLException {
        TestDatabaseUtil.insertSampleUsers(connection);

        // Update users with name 'John Doe' or 'Jane Smith'
        PreparedStatement ps = DSL.update("users")
                .set("email", "updated@example.com")
                .where("name")
                .eq("John Doe")
                .or("name")
                .eq("Jane Smith")
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(2);

        // Verify the updates
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE email = 'updated@example.com'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(2);
        }
    }

    @Test
    void updateWithLikeConditionAndVerify() throws SQLException {
        TestDatabaseUtil.insertSampleUsers(connection);

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
    }

    @Test
    void updateAllRowsAndVerify() throws SQLException {
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
        TestDatabaseUtil.insertSampleUsers(connection);

        // Update active status
        PreparedStatement ps = DSL.update("users")
                .set("active", false)
                .where("name")
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
}
