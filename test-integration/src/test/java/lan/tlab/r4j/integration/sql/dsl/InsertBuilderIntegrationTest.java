package lan.tlab.r4j.integration.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.integration.sql.util.TestDatabaseUtil;
import lan.tlab.r4j.sql.dsl.DSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsertBuilderIntegrationTest {

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
    void singleStringValue() throws SQLException {
        PreparedStatement ps =
                DSL.insertInto("users").set("id", 1).set("name", "John").buildPreparedStatement(connection);

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
        PreparedStatement ps = DSL.insertInto("users")
                .set("id", 2)
                .set("name", "Jane")
                .set("email", "jane@example.com")
                .set("age", 25)
                .set("active", true)
                .set("birthdate", LocalDate.of(1999, 5, 15))
                .set("createdAt", LocalDateTime.of(2023, 10, 10, 12, 0, 0))
                .buildPreparedStatement(connection);

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
        PreparedStatement ps = DSL.insertInto("users")
                .set("id", 3)
                .set("name", "Bob")
                .set("email", (String) null)
                .set("age", (Integer) null)
                .buildPreparedStatement(connection);

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
        PreparedStatement ps = DSL.insertInto("products")
                .set("id", 1)
                .set("name", "Widget")
                .set("price", 19.99)
                .set("quantity", 100)
                .buildPreparedStatement(connection);

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
        PreparedStatement ps1 = DSL.insertInto("users")
                .set("id", 10)
                .set("name", "Alice")
                .set("active", true)
                .buildPreparedStatement(connection);
        assertThat(ps1.executeUpdate()).isEqualTo(1);

        // Insert second row
        PreparedStatement ps2 = DSL.insertInto("users")
                .set("id", 11)
                .set("name", "Charlie")
                .set("active", false)
                .buildPreparedStatement(connection);
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
        PreparedStatement ps1 = DSL.insertInto("users")
                .set("id", 20)
                .set("name", "David")
                .set("email", "david@example.com")
                .buildPreparedStatement(connection);
        assertThat(ps1.executeUpdate()).isEqualTo(1);

        PreparedStatement ps2 = DSL.insertInto("products")
                .set("id", 20)
                .set("price", 29.99)
                .set("quantity", 50)
                .buildPreparedStatement(connection);
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
        PreparedStatement ps = DSL.insertInto("users")
                .set("id", 30)
                .set("name", "Emily")
                .set("email", "emily@example.com")
                .set("age", 28)
                .set("active", true)
                .set("birthdate", LocalDate.of(1996, 3, 20))
                .set("createdAt", LocalDateTime.of(2023, 10, 10, 14, 30, 0))
                .buildPreparedStatement(connection);

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
}
