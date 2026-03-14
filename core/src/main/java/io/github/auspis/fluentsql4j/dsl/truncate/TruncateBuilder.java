package io.github.auspis.fluentsql4j.dsl.truncate;

import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.dml.statement.TruncateStatement;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.dsl.util.PsUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Builder for TRUNCATE TABLE statements.
 */
public class TruncateBuilder {

    private final PreparedStatementSpecFactory specFactory;
    private final TableIdentifier table;

    public TruncateBuilder(PreparedStatementSpecFactory specFactory, String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }
        this.specFactory = specFactory;
        this.table = new TableIdentifier(tableName);
    }

    /**
     * Builds and returns a PreparedStatement for TRUNCATE TABLE.
     *
     * <p><strong>Note:</strong> The caller is responsible for closing the returned PreparedStatement.
     *
     * @param connection the database connection
     * @return a PreparedStatement ready for execution
     * @throws SQLException if a database error occurs
     */
    public PreparedStatement build(Connection connection) throws SQLException {
        TruncateStatement statement = TruncateStatement.builder().table(table).build();
        PreparedStatementSpec result = specFactory.create(statement);
        return PsUtil.preparedStatement(result, connection);
    }
}
