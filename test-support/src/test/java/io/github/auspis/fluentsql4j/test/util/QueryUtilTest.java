package io.github.auspis.fluentsql4j.test.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.auspis.fluentsql4j.test.util.database.TestDatabaseUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueryUtilTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.H2.createConnection();
        TestDatabaseUtil.H2.createUsersTable(connection);
        TestDatabaseUtil.H2.insertSampleUsers(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void countByColumnReturnsMatchingRows() throws SQLException {
        long count = QueryUtil.countByColumn(connection, "users", "email", "john@example.com");

        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByColumnInReturnsMatchingRows() throws SQLException {
        long count = QueryUtil.countByColumnIn(
                connection, "users", "email", "john@example.com", "jane@example.com", "missing@example.com");

        assertThat(count).isEqualTo(2);
    }

    @Test
    void countByColumnInReturnsZeroWhenNothingMatches() throws SQLException {
        long count =
                QueryUtil.countByColumnIn(connection, "users", "email", "missing1@example.com", "missing2@example.com");

        assertThat(count).isZero();
    }

    @Test
    void existsByColumnReturnsTrueWhenRowExists() throws SQLException {
        boolean exists = QueryUtil.existsByColumn(connection, "users", "email", "jane@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByColumnReturnsFalseWhenRowDoesNotExist() throws SQLException {
        boolean exists = QueryUtil.existsByColumn(connection, "users", "email", "missing@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    void getSingleValueByColumnReturnsValue() throws SQLException {
        var userId =
                QueryUtil.getSingleValueByColumn(connection, "users", "id", "email", "jane@example.com", Integer.class);

        assertThat(userId).contains(2);
    }

    @Test
    void getSingleValueByColumnConvertsNumericValue() throws SQLException {
        var userId =
                QueryUtil.getSingleValueByColumn(connection, "users", "id", "email", "jane@example.com", Long.class);

        assertThat(userId).contains(2L);
    }

    @Test
    void getSingleValueByColumnConvertsAllSupportedNumericTypes() throws SQLException {
        var userIdAsShort =
                QueryUtil.getSingleValueByColumn(connection, "users", "id", "email", "jane@example.com", Short.class);
        var userIdAsByte =
                QueryUtil.getSingleValueByColumn(connection, "users", "id", "email", "jane@example.com", Byte.class);
        var userIdAsDouble =
                QueryUtil.getSingleValueByColumn(connection, "users", "id", "email", "jane@example.com", Double.class);
        var userIdAsFloat =
                QueryUtil.getSingleValueByColumn(connection, "users", "id", "email", "jane@example.com", Float.class);

        assertThat(userIdAsShort).contains((short) 2);
        assertThat(userIdAsByte).contains((byte) 2);
        assertThat(userIdAsDouble).contains(2.0d);
        assertThat(userIdAsFloat).contains(2.0f);
    }

    @Test
    void getSingleValueByColumnReturnsExactTypeWithoutConversion() throws SQLException {
        var userName = QueryUtil.getSingleValueByColumn(
                connection, "users", "name", "email", "jane@example.com", String.class);

        assertThat(userName).contains("Jane Smith");
    }

    @Test
    void getSingleValueByColumnReturnsEmptyWhenValueIsNull() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("UPDATE users SET email = NULL WHERE id = 1");
        }

        var userEmail = QueryUtil.getSingleValueByColumn(connection, "users", "email", "id", 1, String.class);

        assertThat(userEmail).isEmpty();
    }

    @Test
    void getSingleValueByColumnThrowsWhenTypeIsNotSupported() {
        assertThatThrownBy(() -> QueryUtil.getSingleValueByColumn(
                        connection, "users", "name", "email", "jane@example.com", Integer.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cast value of type");
    }

    @Test
    void getSingleValueByColumnReturnsEmptyWhenNoMatch() throws SQLException {
        var userId = QueryUtil.getSingleValueByColumn(
                connection, "users", "id", "email", "missing@example.com", Integer.class);

        assertThat(userId).isEmpty();
    }
}
