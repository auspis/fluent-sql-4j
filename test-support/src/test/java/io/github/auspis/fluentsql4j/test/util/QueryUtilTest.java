package io.github.auspis.fluentsql4j.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueryUtilTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
    }

    @Test
    void countByColumnReturnsMatchingRows() throws SQLException {
        long count = QueryUtil.countByColumn(connection, "users", "email", "john@example.com");

        assertThat(count).isEqualTo(1);
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void countByColumnInReturnsMatchingRows() throws SQLException {
        long count = QueryUtil.countByColumnIn(
                connection, "users", "email", "john@example.com", "jane@example.com", "missing@example.com");

        assertThat(count).isEqualTo(2);
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void existsByColumnReturnsTrueWhenRowExists() throws SQLException {
        boolean exists = QueryUtil.existsByColumn(connection, "users", "email", "jane@example.com");

        assertThat(exists).isTrue();
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void getSingleValueByColumnReturnsValue() throws SQLException {
        var userId =
                QueryUtil.getSingleValueByColumn(connection, "users", "id", "email", "jane@example.com", Integer.class);

        assertThat(userId).contains(2);
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void getSingleValueByColumnConvertsNumericValue() throws SQLException {
        var userId =
                QueryUtil.getSingleValueByColumn(connection, "users", "id", "email", "jane@example.com", Long.class);

        assertThat(userId).contains(2L);
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void getSingleValueByColumnReturnsEmptyWhenNoMatch() throws SQLException {
        var userId = QueryUtil.getSingleValueByColumn(
                connection, "users", "id", "email", "missing@example.com", Integer.class);

        assertThat(userId).isEmpty();
        TestDatabaseUtil.closeConnection(connection);
    }
}
