package integration.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.dsl.DSL;
import io.github.massimiliano.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.massimiliano.fluentsql4j.test.util.TestDatabaseUtil;
import io.github.massimiliano.fluentsql4j.test.util.annotation.IntegrationTest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for DeleteBuilder with H2 in-memory database.
 * Tests the complete integration between DSL DeleteBuilder, SQL rendering,
 * PreparedStatement creation, and actual database delete operations.
 */
@IntegrationTest
class DeleteBuilderIntegrationTest {

    private Connection connection;
    private DSL dsl;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        dsl = StandardSqlUtil.dsl();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.createProductsTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void whereCondition() throws SQLException {
        PreparedStatement ps =
                dsl.deleteFrom("users").where().column("id").eq(2).build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(9);
        }

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id FROM users WHERE id = 2")) {
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void multipleConditions() throws SQLException {
        PreparedStatement ps =
                dsl.deleteFrom("users").where().column("age").lt(18).build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(9);
        }
    }

    @Test
    void andCondition() throws SQLException {
        PreparedStatement ps = dsl.deleteFrom("users")
                .where()
                .column("age")
                .gt(18)
                .and()
                .column("name")
                .eq("Alice")
                .build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(9);
        }
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM users WHERE name = 'Alice'")) {
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void orCondition() throws SQLException {
        PreparedStatement ps = dsl.deleteFrom("users")
                .where()
                .column("name")
                .eq("John Doe")
                .or()
                .column("name")
                .eq("Jane Smith")
                .build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(2);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(8);
        }

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM users WHERE name = 'Bob'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Bob");
        }
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM users WHERE name = 'Alice'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Alice");
        }
    }

    @Test
    void stringLike() throws SQLException {
        PreparedStatement ps = dsl.deleteFrom("users")
                .where()
                .column("email")
                .like("%example.com")
                .build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(10);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(0);
        }
    }

    @Test
    void allRows() throws SQLException {
        PreparedStatement ps = dsl.deleteFrom("users").build(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(10);

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(0);
        }
    }
}
