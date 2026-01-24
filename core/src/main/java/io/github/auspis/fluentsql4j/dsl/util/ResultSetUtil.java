package io.github.auspis.fluentsql4j.dsl.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility methods to convert JDBC {@link ResultSet} to Java {@link Stream}.
 * <p>
 * This is a pure utility class: final, with a private constructor and only static methods.
 */
public final class ResultSetUtil {

    private ResultSetUtil() {
        // utility class
    }

    /**
     * Functional interface to map the current row of a {@link ResultSet} to a value of type T.
     */
    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    /**
     * Create a lazy {@link Stream} over the provided {@link ResultSet}.
     * <p>
     * IMPORTANT: this method does NOT close the provided {@link ResultSet}, {@link java.sql.Statement}
     * or {@link java.sql.Connection}. The caller that created those resources is responsible for closing
     * them (for example using try-with-resources). Any {@link SQLException} thrown while iterating is
     * wrapped in a {@link RuntimeException}.
     *
     * @param rs     the ResultSet to stream (must not be null)
     * @param mapper maps the current ResultSet row to an element
     * @param <T>    element type
     * @return a lazily-driven Stream of mapped rows; closing the stream does not close the ResultSet
     */
    public static <T> Stream<T> stream(ResultSet rs, RowMapper<T> mapper) {
        ResultSet notNullRs = requireNonNull(rs, "ResultSet must not be null");
        RowMapper<T> notNullMapper = requireNonNull(mapper, "RowMapper must not be null");

        return streamInternal(
                notNullRs,
                notNullMapper,
                () -> closeResultSetSafely(notNullRs),
                e -> closeResultSetSuppressed(notNullRs, e));
    }

    /**
     * Eagerly reads the provided ResultSet into a List and closes the ResultSet.
     * <p>
     * This convenience method consumes all rows, maps them with the provided mapper,
     * and ensures the ResultSet is closed before returning. It does NOT close the
     * associated Statement or Connection — closing those remains the caller's responsibility.
     *
     * @param rs     the ResultSet to read (must not be null)
     * @param mapper maps the current ResultSet row to an element
     * @param <T>    element type
     * @return list with all mapped rows
     * @throws RuntimeException in case of SQL errors while reading the ResultSet
     */
    public static <T> List<T> list(ResultSet rs, RowMapper<T> mapper) {
        ResultSet notNullRs = requireNonNull(rs, "ResultSet must not be null");
        RowMapper<T> notNullMapper = requireNonNull(mapper, "RowMapper must not be null");
        // Delegate to toStream which guarantees the ResultSet will be closed
        // either when the stream is fully consumed or when the stream is closed.
        try (Stream<T> stream = stream(notNullRs, notNullMapper)) {
            return stream.toList();
        }
    }

    /**
     * Execute the given {@link PreparedStatement} and return a Stream over the resulting {@link ResultSet}.
     * <p>
     * This method will execute the statement (calling {@code executeQuery}) and return a lazy Stream of rows
     * mapped via the provided mapper. The returned Stream will close both the ResultSet and the
     * PreparedStatement when the stream is fully consumed or when the stream is closed prematurely.
     * <p>
     * The associated {@link java.sql.Connection} is NOT closed by this method; the caller remains
     * responsible for closing the connection if necessary.
     *
     * @param ps     the prepared statement to execute (must not be null)
     * @param mapper maps the current ResultSet row to an element
     * @param <T>    element type
     * @return a lazily-driven Stream of mapped rows; closing the stream or exhausting it will close both
     *         ResultSet and PreparedStatement
     * @throws RuntimeException in case of SQL errors while executing or iterating the results
     */
    public static <T> Stream<T> stream(PreparedStatement ps, RowMapper<T> mapper) {
        PreparedStatement notNullPs = requireNonNull(ps, "PreparedStatement must not be null");
        RowMapper<T> notNullMapper = requireNonNull(mapper, "RowMapper must not be null");

        final ResultSet rs;
        try {
            rs = notNullPs.executeQuery();
        } catch (SQLException e) {
            try {
                notNullPs.close();
            } catch (SQLException ex) {
                e.addSuppressed(ex);
            }
            throw new RuntimeException(e);
        }

        return streamInternal(
                rs,
                notNullMapper,
                () -> closeResourcesSafely(rs, notNullPs),
                e -> closeResourcesSuppressed(rs, notNullPs, e));
    }

    /**
     * Execute the given {@link PreparedStatement} and eagerly read all rows into a List.
     * <p>
     * This method executes the provided PreparedStatement and returns an eagerly-built List with
     * all mapped rows. It closes both the ResultSet and the PreparedStatement before returning.
     * The associated {@link java.sql.Connection} is NOT closed by this method.
     *
     * @param ps     the prepared statement to execute (must not be null)
     * @param mapper maps the current ResultSet row to an element
     * @param <T>    element type
     * @return a List containing all mapped rows
     * @throws RuntimeException in case of SQL errors while executing or reading the results
     */
    public static <T> List<T> list(PreparedStatement ps, RowMapper<T> mapper) {
        PreparedStatement notNullPs = requireNonNull(ps, "PreparedStatement must not be null");
        RowMapper<T> notNullMapper = requireNonNull(mapper, "RowMapper must not be null");

        try (Stream<T> stream = stream(notNullPs, notNullMapper)) {
            return stream.toList();
        }
    }

    private static <T> Stream<T> streamInternal(
            ResultSet rs, RowMapper<T> mapper, Runnable closeSafely, Consumer<SQLException> closeSuppressed) {
        final AtomicBoolean closed = new AtomicBoolean(false);

        Spliterator<T> spliterator =
                new Spliterators.AbstractSpliterator<>(Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL) {
                    @Override
                    public boolean tryAdvance(Consumer<? super T> action) {
                        try {
                            boolean hasNext = rs.next();
                            if (!hasNext) {
                                closeOnce(closed, closeSafely);
                                return false;
                            }
                            action.accept(mapper.map(rs));
                            return true;
                        } catch (SQLException e) {
                            closeOnce(closed, () -> closeSuppressed.accept(e));
                            throw new RuntimeException(e);
                        }
                    }
                };

        Stream<T> stream = StreamSupport.stream(spliterator, false);
        return stream.onClose(() -> closeOnce(closed, closeSafely));
    }

    /**
     * Close a single ResultSet with proper exception handling.
     * If close fails, wraps the SQLException in a RuntimeException.
     */
    private static void closeResultSetSafely(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Close a single ResultSet with exception suppression.
     * If close fails, adds the SQLException as a suppressed exception to the provided original exception.
     */
    private static void closeResultSetSuppressed(ResultSet rs, SQLException originalException) {
        try {
            rs.close();
        } catch (SQLException ex) {
            originalException.addSuppressed(ex);
        }
    }

    /**
     * Close both ResultSet and PreparedStatement with proper exception handling.
     * Closes ResultSet first, then PreparedStatement. If either fails, exceptions are suppressed
     * and the original exception is propagated.
     */
    private static void closeResourcesSafely(ResultSet rs, PreparedStatement ps) {
        try {
            rs.close();
        } catch (SQLException e) {
            try {
                ps.close();
            } catch (SQLException ex) {
                e.addSuppressed(ex);
            }
            throw new RuntimeException(e);
        }
        try {
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Close both ResultSet and PreparedStatement with exception suppression.
     * If either fails, exceptions are added as suppressed to the original exception.
     */
    private static void closeResourcesSuppressed(ResultSet rs, PreparedStatement ps, SQLException originalException) {
        try {
            rs.close();
        } catch (SQLException ex) {
            originalException.addSuppressed(ex);
        }
        try {
            ps.close();
        } catch (SQLException ex) {
            originalException.addSuppressed(ex);
        }
    }

    /**
     * Execute a close action exactly once using an atomic gate-keeper.
     * Prevents duplicate close attempts when multiple paths (error/end/onClose) converge.
     */
    private static void closeOnce(AtomicBoolean closed, Runnable closeAction) {
        if (closed.compareAndSet(false, true)) {
            closeAction.run();
        }
    }

    private static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
