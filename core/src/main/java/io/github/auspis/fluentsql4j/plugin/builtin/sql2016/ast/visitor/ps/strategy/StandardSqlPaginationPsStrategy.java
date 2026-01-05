package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dql.clause.Fetch;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.FetchPsStrategy;
import java.util.List;

public class StandardSqlPaginationPsStrategy implements FetchPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Fetch pagination, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder();

        // SQL 2008 standard uses OFFSET ... ROWS FETCH NEXT ... ROWS ONLY
        // Add OFFSET clause if offset > 0
        if (pagination.offset() > 0) {
            sql.append("OFFSET ").append(pagination.offset()).append(" ROWS ");
        }

        // Add FETCH clause (SQL 2008 standard)
        sql.append("FETCH NEXT ").append(pagination.rows()).append(" ROWS ONLY");

        return new PreparedStatementSpec(sql.toString(), List.of());
    }
}
