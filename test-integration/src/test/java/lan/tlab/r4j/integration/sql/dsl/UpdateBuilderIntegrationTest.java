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
        TestDatabaseUtil.insertSampleUsers(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void singleColumnWithWhereCondition() throws SQLException {
        PreparedStatement ps =
                DSL.update("users").set("name", "Johnny").where("id").eq(1).buildPreparedStatement(connection);

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
        PreparedStatement ps = DSL.update("users")
                .set("name", "Johnny")
                .set("age", 35)
                .where("id")
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
        PreparedStatement ps = DSL.update("users")
                .set("email", "jane.updated@example.com")
                .where("age")
                .gt(18)
                .and("name")
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
        PreparedStatement ps = DSL.update("users")
                .set("email", "updated@example.com")
                .where("name")
                .eq("John Doe")
                .or("name")
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
        PreparedStatement ps = DSL.update("users")
                .set("email", "verified@example.com")
                .where("email")
                .like("%example.com")
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(4);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE email = 'verified@example.com'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(4);
        }
    }

    @Test
    void allRows() throws SQLException {
        PreparedStatement ps =
                DSL.update("users").set("email", "updated@example.com").buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(4);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE email = 'updated@example.com'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(4);
        }
    }

    @Test
    void numberComparison() throws SQLException {
        PreparedStatement ps = DSL.update("users")
                .set("name", "Older User")
                .where("age")
                .gt(30)
                .buildPreparedStatement(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE name = 'Older User'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(1);
        }

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM users WHERE name = 'Jane Smith'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Jane Smith");
        }
    }

    @Test
    void booleanUpdate() throws SQLException {
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
