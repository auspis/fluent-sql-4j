package io.github.auspis.fluentsql4j.dsl.util;

import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PsUtil {
    private PsUtil() {}

    /**
     * Creates and returns a PreparedStatement with bound parameters from a PreparedStatementSpec.
     * <p>
     * <strong>Note:</strong> The caller is responsible for closing the returned PreparedStatement.
     * Consider using try-with-resources:
     * <pre>
     * try (PreparedStatement ps = PsUtil.preparedStatement(spec, connection)) {
     *     // use ps
     * }
     * </pre>
     *
     * @param spec the PreparedStatementSpec containing SQL and parameters
     * @param connection the database connection
     * @return a PreparedStatement ready for execution
     * @throws SQLException if a database error occurs
     */
    public static PreparedStatement preparedStatement(PreparedStatementSpec spec, Connection connection)
            throws SQLException {
        //noinspection resource
        PreparedStatement ps = connection.prepareStatement(spec.sql());
        for (int i = 0; i < spec.parameters().size(); i++) {
            ps.setObject(i + 1, spec.parameters().get(i));
        }
        return ps;
    }
}
