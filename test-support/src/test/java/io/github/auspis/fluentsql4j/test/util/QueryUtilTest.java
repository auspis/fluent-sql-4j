package io.github.auspis.fluentsql4j.test.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class QueryUtilTest {

    @Test
    void countByColumnReturnsMatchingRows() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);

        long count = QueryUtil.countByColumn(connection, "users", "email", "john@example.com");

        assertThat(count).isEqualTo(1);
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void countByColumnInReturnsMatchingRows() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);

        long count = QueryUtil.countByColumnIn(
                connection, "users", "email", "john@example.com", "jane@example.com", "missing@example.com");

        assertThat(count).isEqualTo(2);
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void existsByColumnReturnsTrueWhenRowExists() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);

        boolean exists = QueryUtil.existsByColumn(connection, "users", "email", "jane@example.com");

        assertThat(exists).isTrue();
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void getSingleValueByColumnReturnsValue() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);

        var userId =
                QueryUtil.getSingleValueByColumn(connection, "users", "id", "email", "jane@example.com", Integer.class);

        assertThat(userId).contains(2);
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void getSingleValueByColumnConvertsNumericValue() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);

        var userId =
                QueryUtil.getSingleValueByColumn(connection, "users", "id", "email", "jane@example.com", Long.class);

        assertThat(userId).contains(2L);
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void getSingleValueByColumnReturnsEmptyWhenNoMatch() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);

        var userId = QueryUtil.getSingleValueByColumn(
                connection, "users", "id", "email", "missing@example.com", Integer.class);

        assertThat(userId).isEmpty();
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void countByColumnInWithoutValuesThrows() throws SQLException {
        Connection connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);

        assertThatThrownBy(() -> QueryUtil.countByColumnIn(connection, "users", "email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one value must be provided");

        TestDatabaseUtil.closeConnection(connection);
    }
}
