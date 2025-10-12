package lan.tlab.r4j.sql.dsl.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ResultSetUtilExceptionTest {

    @Test
    void nextThrowsSQLException_closesResultSetAndPropagates() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenThrow(new SQLException("boom-next"));

        Stream<String> stream = ResultSetUtil.stream(rs, r -> r.getString(1));

        assertThatThrownBy(() -> stream.forEach(s -> {}))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class)
                .hasMessageContaining("boom-next");

        // verify close called
        verify(rs, atLeastOnce()).close();
    }

    @Test
    void closeThrowsSQLException_suppressedWhenNextThrows() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        SQLException nextEx = new SQLException("boom-next");
        SQLException closeEx = new SQLException("boom-close");
        when(rs.next()).thenThrow(nextEx);
        doThrow(closeEx).when(rs).close();

        Stream<String> stream = ResultSetUtil.stream(rs, r -> r.getString(1));

        try {
            stream.forEach(s -> {});
            // should not reach here
            throw new AssertionError("Expected RuntimeException");
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            assertThat(cause).isSameAs(nextEx);
            assertThat(cause.getSuppressed()).contains(closeEx);
        }

        verify(rs, atLeastOnce()).close();
    }
}
