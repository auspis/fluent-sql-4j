package lan.tlab.r4j.jdsql.dsl.util;

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
        if (rs == null) {
            throw new IllegalArgumentException("ResultSet must not be null");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("RowMapper must not be null");
        }

        final AtomicBoolean closed = new AtomicBoolean(false);

        Spliterator<T> spliterator =
                new Spliterators.AbstractSpliterator<>(Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL) {
                    @Override
                    public boolean tryAdvance(Consumer<? super T> action) {
                        try {
                            boolean hasNext = rs.next();
                            if (!hasNext) {
                                // reached end: close the ResultSet once
                                if (closed.compareAndSet(false, true)) {
                                    try {
                                        rs.close();
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                return false;
                            }
                            action.accept(mapper.map(rs));
                            return true;
                        } catch (SQLException e) {
                            // on error, attempt to close ResultSet and propagate
                            if (closed.compareAndSet(false, true)) {
                                try {
                                    rs.close();
                                } catch (SQLException ex) {
                                    e.addSuppressed(ex);
                                }
                            }
                            throw new RuntimeException(e);
                        }
                    }
                };

        Stream<T> stream = StreamSupport.stream(spliterator, false);

        // Ensure that if the stream is closed prematurely (try-with-resources) the ResultSet is closed too.
        return stream.onClose(() -> {
            if (closed.compareAndSet(false, true)) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
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
        if (rs == null) {
            throw new IllegalArgumentException("ResultSet must not be null");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("RowMapper must not be null");
        }
        // Delegate to toStream which guarantees the ResultSet will be closed
        // either when the stream is fully consumed or when the stream is closed.
        try (Stream<T> stream = stream(rs, mapper)) {
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
        if (ps == null) {
            throw new IllegalArgumentException("PreparedStatement must not be null");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("RowMapper must not be null");
        }

        final ResultSet rs;
        try {
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            // on execute failure, attempt to close the PreparedStatement and propagate
            try {
                ps.close();
            } catch (SQLException ex) {
                e.addSuppressed(ex);
            }
            throw new RuntimeException(e);
        }

        final java.util.concurrent.atomic.AtomicBoolean closed = new AtomicBoolean(false);

        Spliterator<T> spliterator =
                new Spliterators.AbstractSpliterator<>(Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL) {
                    @Override
                    public boolean tryAdvance(Consumer<? super T> action) {
                        try {
                            boolean hasNext = rs.next();
                            if (!hasNext) {
                                // reached end: close ResultSet and PreparedStatement once
                                if (closed.compareAndSet(false, true)) {
                                    try {
                                        rs.close();
                                    } catch (SQLException e) {
                                        // try to close ps even if rs.close fails
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
                                return false;
                            }
                            action.accept(mapper.map(rs));
                            return true;
                        } catch (SQLException e) {
                            // on error, attempt to close both resources and propagate
                            if (closed.compareAndSet(false, true)) {
                                try {
                                    rs.close();
                                } catch (SQLException ex) {
                                    e.addSuppressed(ex);
                                }
                                try {
                                    ps.close();
                                } catch (SQLException ex) {
                                    e.addSuppressed(ex);
                                }
                            }
                            throw new RuntimeException(e);
                        }
                    }
                };

        Stream<T> stream = StreamSupport.stream(spliterator, false);

        // Ensure that if the stream is closed prematurely the ResultSet AND PreparedStatement are closed too.
        return stream.onClose(() -> {
            if (closed.compareAndSet(false, true)) {
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
        });
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
        if (ps == null) {
            throw new IllegalArgumentException("PreparedStatement must not be null");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("RowMapper must not be null");
        }

        try (Stream<T> stream = stream(ps, mapper)) {
            return stream.toList();
        }
    }
}
