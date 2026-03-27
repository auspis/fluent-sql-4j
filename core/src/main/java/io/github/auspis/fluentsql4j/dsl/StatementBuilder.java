package io.github.auspis.fluentsql4j.dsl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Common contract for all DSL builders that produce a {@link PreparedStatement}.
 *
 * <p>Every concrete builder ({@code SelectBuilder}, {@code DeleteBuilder},
 * {@code InsertBuilder}, {@code UpdateBuilder}, {@code TruncateBuilder},
 * {@code MergeBuilder}, {@code OrderByBuilder}) implements this interface by
 * virtue of already exposing a {@code build(Connection)} method with this
 * exact signature.
 */
@FunctionalInterface
public interface StatementBuilder {

    /**
     * Builds and returns a {@link PreparedStatement} ready for execution.
     *
     * @param connection an open JDBC connection; the caller is responsible for
     *                   lifecycle management
     * @return a prepared statement
     * @throws SQLException if the statement cannot be prepared
     */
    PreparedStatement build(Connection connection) throws SQLException;
}
