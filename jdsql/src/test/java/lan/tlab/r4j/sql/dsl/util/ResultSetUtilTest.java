package lan.tlab.r4j.sql.dsl.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ResultSetUtilTest {

    @Test
    void stream_withNullResultSet_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ResultSetUtil.stream((ResultSet) null, (ResultSetUtil.RowMapper<String>) rs -> "test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ResultSet must not be null");
    }

    @Test
    void stream_withNullMapper_throwsIllegalArgumentException() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        assertThatThrownBy(() -> ResultSetUtil.stream(rs, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("RowMapper must not be null");
    }

    @Test
    void stream_withEmptyResultSet_returnsEmptyStream() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false);

        Stream<String> stream = ResultSetUtil.stream(rs, r -> r.getString(1));

        assertThat(stream).isEmpty();
        verify(rs).close();
    }

    @Test
    void stream_withData_returnsMappedValues() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getString(1)).thenReturn("Alice", "Bob");

        Stream<String> stream = ResultSetUtil.stream(rs, r -> r.getString(1));

        assertThat(stream).containsExactly("Alice", "Bob");
        verify(rs).close();
    }

    @Test
    void stream_closesResultSet_whenFullyConsumed() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true, true, false); // two rows then end
        when(rs.getString(1)).thenReturn("Alice", "Bob");

        Stream<String> stream = ResultSetUtil.stream(rs, r -> r.getString(1));

        // Consume all elements
        assertThat(stream).containsExactly("Alice", "Bob");

        verify(rs).close();
    }

    @Test
    void stream_propagatesMapperException() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getString(1)).thenThrow(new SQLException("mapper error"));

        Stream<String> stream = ResultSetUtil.stream(rs, r -> r.getString(1));

        assertThatThrownBy(() -> stream.findFirst())
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class);

        verify(rs).close();
    }

    @Test
    void list_withNullResultSet_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ResultSetUtil.list((ResultSet) null, (ResultSetUtil.RowMapper<String>) rs -> "test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ResultSet must not be null");
    }

    @Test
    void list_withNullMapper_throwsIllegalArgumentException() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        assertThatThrownBy(() -> ResultSetUtil.list(rs, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("RowMapper must not be null");
    }

    @Test
    void list_withData_returnsMappedList() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getString(1)).thenReturn("Alice", "Bob");

        List<String> result = ResultSetUtil.list(rs, r -> r.getString(1));

        assertThat(result).containsExactly("Alice", "Bob");
        verify(rs).close();
    }

    @Test
    void list_withEmptyResultSet_returnsEmptyList() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false);

        List<String> result = ResultSetUtil.list(rs, r -> r.getString(1));

        assertThat(result).isEmpty();
        verify(rs).close();
    }

    @Test
    void stream_withNullPreparedStatement_throwsIllegalArgumentException() {
        assertThatThrownBy(() ->
                        ResultSetUtil.stream((PreparedStatement) null, (ResultSetUtil.RowMapper<String>) rs -> "test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("PreparedStatement must not be null");
    }

    @Test
    void stream_withPreparedStatement_nullMapper_throwsIllegalArgumentException() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);

        assertThatThrownBy(() -> ResultSetUtil.stream(ps, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("RowMapper must not be null");
    }

    @Test
    void stream_withPreparedStatement_executesAndStreams() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getString(1)).thenReturn("Alice", "Bob");

        Stream<String> stream = ResultSetUtil.stream(ps, r -> r.getString(1));

        assertThat(stream).containsExactly("Alice", "Bob");
        verify(ps).executeQuery();
        verify(rs).close();
        verify(ps).close();
    }

    @Test
    void stream_withPreparedStatement_executeQueryFails_closesStatement() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        when(ps.executeQuery()).thenThrow(new SQLException("execute failed"));

        assertThatThrownBy(() -> ResultSetUtil.stream(ps, r -> r.getString(1)))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class);

        verify(ps).close();
    }

    @Test
    void stream_withPreparedStatement_closesBothResources_whenFullyConsumed() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false); // two rows then end
        when(rs.getString(1)).thenReturn("Alice", "Bob");

        Stream<String> stream = ResultSetUtil.stream(ps, r -> r.getString(1));

        // Consume all elements
        assertThat(stream).containsExactly("Alice", "Bob");

        verify(ps).executeQuery();
        verify(rs).close();
        verify(ps).close();
    }

    @Test
    void list_withNullPreparedStatement_throwsIllegalArgumentException() {
        assertThatThrownBy(() ->
                        ResultSetUtil.list((PreparedStatement) null, (ResultSetUtil.RowMapper<String>) rs -> "test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("PreparedStatement must not be null");
    }

    @Test
    void list_withPreparedStatement_nullMapper_throwsIllegalArgumentException() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);

        assertThatThrownBy(() -> ResultSetUtil.list(ps, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("RowMapper must not be null");
    }

    @Test
    void list_withPreparedStatement_executesAndReturnsList() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getString(1)).thenReturn("Alice", "Bob");

        List<String> result = ResultSetUtil.list(ps, r -> r.getString(1));

        assertThat(result).containsExactly("Alice", "Bob");
        verify(ps).executeQuery();
        verify(rs).close();
        verify(ps).close();
    }

    @Test
    void list_withPreparedStatement_emptyResult_returnsEmptyList() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        List<String> result = ResultSetUtil.list(ps, r -> r.getString(1));

        assertThat(result).isEmpty();
        verify(ps).executeQuery();
        verify(rs).close();
        verify(ps).close();
    }
}
