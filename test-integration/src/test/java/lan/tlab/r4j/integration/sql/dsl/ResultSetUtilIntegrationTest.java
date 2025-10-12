package lan.tlab.r4j.integration.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.integration.sql.util.TestDatabaseUtil;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.dsl.util.ResultSetUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResultSetUtilIntegrationTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void streamOfResultSet() throws SQLException {
        try (PreparedStatement ps = DSL.select("name").from("users").buildPreparedStatement(connection);
                ResultSet rs = ps.executeQuery();
                Stream<String> names = ResultSetUtil.stream(rs, r -> r.getString("name"))) {

            List<String> list = names.toList();
            assertThat(list).hasSize(10);
            assertThat(list).contains("John Doe", "Jane Smith", "Bob", "Alice");
        }
    }

    @Test
    void streamOfResultSetClosesResultSetWhenMapperThrows() throws SQLException {
        PreparedStatement ps = DSL.select("name").from("users").buildPreparedStatement(connection);

        ResultSet rs = ps.executeQuery();
        Stream<String> names = ResultSetUtil.stream(rs, r -> r.getString("nonexistent_column"));

        assertThatThrownBy(() -> names.forEach(s -> {})).isInstanceOf(RuntimeException.class);
        // ResultSet must be closed; PreparedStatement remains open and should be closed by us
        assertThat(rs.isClosed()).isTrue();
        ps.close();
    }

    @Test
    void streamOfResultSetFilter() throws SQLException {
        try (PreparedStatement ps = DSL.select("age").from("users").buildPreparedStatement(connection);
                ResultSet rs = ps.executeQuery();
                Stream<Integer> ages = ResultSetUtil.stream(rs, r -> r.getInt("age"))) {

            List<Integer> filtered = ages.filter(a -> a >= 30).toList();

            // From fixture: users with age >= 30 are 6 (John, Charlie, Henry, Alice, Frank, Eve)
            assertThat(filtered).hasSize(6);
            assertThat(filtered).contains(30, 35, 40);
        }
    }

    @Test
    void streamOfPreparedStatementClosesPreparedStatementWhenExhausted() throws SQLException {
        PreparedStatement ps = DSL.select("name").from("users").buildPreparedStatement(connection);

        try (Stream<String> names = ResultSetUtil.stream(ps, r -> r.getString("name"))) {
            // consume all rows
            List<String> list = names.toList();
            assertThat(list).hasSize(10);
            assertThat(list).contains("Alice", "Bob", "John Doe");
        }

        // after the stream is exhausted (and closed by try-with-resources) the PreparedStatement must be closed
        assertThat(ps.isClosed()).isTrue();
    }

    @Test
    void streamOfPreparedStatementClosesPreparedStatementWhenClosedPrematurely() throws SQLException {
        PreparedStatement ps = DSL.select("name").from("users").buildPreparedStatement(connection);

        Stream<String> names = ResultSetUtil.stream(ps, r -> r.getString("name"));

        // consume only the first element
        String first = names.findFirst().orElse(null);
        assertThat(first).isNotNull();

        // close the stream explicitly
        names.close();

        // the PreparedStatement must be closed when the stream is closed
        assertThat(ps.isClosed()).isTrue();
    }

    @Test
    void streamOfPreparedStatementClosesPreparedStatementWhenMapperThrows() throws SQLException {
        PreparedStatement ps = DSL.select("name").from("users").buildPreparedStatement(connection);

        Stream<String> names = ResultSetUtil.stream(ps, r -> r.getString("nonexistent_column"));

        // mapper will throw SQLException, wrapped in RuntimeException; PreparedStatement must be closed
        assertThatThrownBy(() -> names.forEach(s -> {})).isInstanceOf(RuntimeException.class);
        assertThat(ps.isClosed()).isTrue();
    }

    @Test
    void listOfResultSet() throws SQLException {
        try (PreparedStatement ps = DSL.select("name", "age").from("users").buildPreparedStatement(connection);
                ResultSet rs = ps.executeQuery()) {

            List<List<Object>> list = ResultSetUtil.list(rs, r -> List.of(r.getString("name"), r.getInt("age")));

            assertThat(list)
                    .hasSize(10)
                    .extracting(r -> (String) r.get(0), r -> (Integer) r.get(1))
                    .contains(tuple("John Doe", 30), tuple("Jane Smith", 25), tuple("Bob", 15), tuple("Alice", 35));
        }
    }

    @Test
    void listOfPreparedStatement() throws SQLException {
        try (PreparedStatement ps = DSL.select("name", "age").from("users").buildPreparedStatement(connection)) {

            List<List<Object>> list = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age")));

            assertThat(list)
                    .hasSize(10)
                    .extracting(r -> (String) r.get(0), r -> (Integer) r.get(1))
                    .contains(tuple("John Doe", 30), tuple("Jane Smith", 25), tuple("Bob", 15), tuple("Alice", 35));
        }
    }

    @Test
    void listOfResultSetClosesResultSet() throws SQLException {
        PreparedStatement ps = DSL.select("name").from("users").buildPreparedStatement(connection);

        ResultSet rs = ps.executeQuery();
        try {
            List<String> list = ResultSetUtil.list(rs, r -> r.getString("name"));
            assertThat(list).hasSize(10);
            assertThat(list).contains("John Doe", "Jane Smith");
            // list(...) must close the ResultSet
            assertThat(rs.isClosed()).isTrue();
            // but it must NOT close the PreparedStatement
            assertThat(ps.isClosed()).isFalse();
        } finally {
            ps.close();
        }
    }

    @Test
    void listOfPreparedStatementReturnsContentAndClosesPs() throws SQLException {
        PreparedStatement ps = DSL.select("name").from("users").buildPreparedStatement(connection);

        List<String> list = ResultSetUtil.list(ps, r -> r.getString("name"));
        assertThat(list).hasSize(10);
        assertThat(ps.isClosed()).isTrue();
    }

    @Test
    void listOfPreparedStatementClosesPsWhenMapperThrows() throws SQLException {
        PreparedStatement ps = DSL.select("name").from("users").buildPreparedStatement(connection);

        assertThatThrownBy(() -> ResultSetUtil.list(ps, r -> r.getString("nonexistent_column")))
                .isInstanceOf(RuntimeException.class);
        assertThat(ps.isClosed()).isTrue();
    }
}
