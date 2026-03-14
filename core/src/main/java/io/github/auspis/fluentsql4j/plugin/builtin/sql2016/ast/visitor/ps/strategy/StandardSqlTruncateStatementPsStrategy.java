package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.statement.TruncateStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.TruncateStatementPsStrategy;
import java.util.List;

public class StandardSqlTruncateStatementPsStrategy implements TruncateStatementPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            TruncateStatement truncateStatement, Visitor<PreparedStatementSpec> visitor, AstContext ctx) {
        String tableSql = truncateStatement.table().accept(visitor, ctx).sql();
        return new PreparedStatementSpec("TRUNCATE TABLE " + tableSql, List.of());
    }
}
