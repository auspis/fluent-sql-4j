package lan.tlab.r4j.integration.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.dsl.DSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsertBuilderIntegrationTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // H2 in-memory database with standard SQL mode
        String jdbcUrl = "jdbc:h2:mem:testdb;MODE=REGULAR;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
        connection = DriverManager.getConnection(jdbcUrl, "sa", "");

        createTestTables();
    }

    private void createTestTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "CREATE TABLE users (id INTEGER PRIMARY KEY, name VARCHAR(50), email VARCHAR(100), age INTEGER, active BOOLEAN)");
            stmt.execute(
                    "CREATE TABLE products (id INTEGER PRIMARY KEY, name VARCHAR(50), price DECIMAL(10,2), quantity INTEGER)");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void insertSingleStringValueAndVerify() throws SQLException {
        // Insert using DSL
        PreparedStatement ps = DSL.insertInto("users")
                .columns("id", "name")
                .values(Literal.of(1), Literal.of("John"))
                .buildPrepared(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the insert
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name FROM users WHERE id = 1")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(1);
            assertThat(rs.getString("name")).isEqualTo("John");
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void insertMultipleColumnsWithMixedTypesAndVerify() throws SQLException {
        // Insert using DSL with mixed types
        PreparedStatement ps = DSL.insertInto("users")
                .columns("id", "name", "email", "age", "active")
                .values(
                        Literal.of(2),
                        Literal.of("Jane"),
                        Literal.of("jane@example.com"),
                        Literal.of(25),
                        Literal.of(true))
                .buildPrepared(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the insert
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id = 2")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(2);
            assertThat(rs.getString("name")).isEqualTo("Jane");
            assertThat(rs.getString("email")).isEqualTo("jane@example.com");
            assertThat(rs.getInt("age")).isEqualTo(25);
            assertThat(rs.getBoolean("active")).isTrue();
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void insertWithNullValuesAndVerify() throws SQLException {
        // Insert with null values
        PreparedStatement ps = DSL.insertInto("users")
                .columns("id", "name", "email", "age")
                .values(Literal.of(3), Literal.of("Bob"), Literal.ofNull(), Literal.ofNull())
                .buildPrepared(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the insert
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
    void insertWithDecimalValuesAndVerify() throws SQLException {
        // Insert numeric values including decimals
        PreparedStatement ps = DSL.insertInto("products")
                .columns("id", "name", "price", "quantity")
                .values(Literal.of(1), Literal.of("Widget"), Literal.of(19.99), Literal.of(100))
                .buildPrepared(connection);

        int rowsAffected = ps.executeUpdate();
        assertThat(rowsAffected).isEqualTo(1);

        // Verify the insert
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
    void insertMultipleRowsAndVerify() throws SQLException {
        // Insert first row
        PreparedStatement ps1 = DSL.insertInto("users")
                .columns("id", "name", "active")
                .values(Literal.of(10), Literal.of("Alice"), Literal.of(true))
                .buildPrepared(connection);
        assertThat(ps1.executeUpdate()).isEqualTo(1);

        // Insert second row
        PreparedStatement ps2 = DSL.insertInto("users")
                .columns("id", "name", "active")
                .values(Literal.of(11), Literal.of("Charlie"), Literal.of(false))
                .buildPrepared(connection);
        assertThat(ps2.executeUpdate()).isEqualTo(1);

        // Verify both inserts
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
    void insertWithTypeSpecificMethodsAndVerify() throws SQLException {
        // Test string-only values method
        PreparedStatement ps1 = DSL.insertInto("users")
                .columns("id", "name", "email")
                .values("20", "David", "david@example.com")
                .buildPrepared(connection);
        assertThat(ps1.executeUpdate()).isEqualTo(1);

        // Test numeric-only values method
        PreparedStatement ps2 = DSL.insertInto("products")
                .columns("id", "price", "quantity")
                .values(20, 29.99, 50)
                .buildPrepared(connection);
        assertThat(ps2.executeUpdate()).isEqualTo(1);

        // Verify user insert
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name, email FROM users WHERE id = 20")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("David");
            assertThat(rs.getString("email")).isEqualTo("david@example.com");
        }

        // Verify product insert
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT price, quantity FROM products WHERE id = 20")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getBigDecimal("price")).isEqualByComparingTo("29.99");
            assertThat(rs.getInt("quantity")).isEqualTo(50);
        }
    }

    @Test
    void insertWithSetMethodAndVerify() throws SQLException {
        // Test new set() method with fluent API
        PreparedStatement ps = DSL.insertInto("users")
                .set("id", 30)
                .set("name", "Emily")
                .set("email", "emily@example.com")
                .set("age", 28)
                .set("active", true)
                .buildPrepared(connection);

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
            assertThat(rs.next()).isFalse();
        }
    }
}
