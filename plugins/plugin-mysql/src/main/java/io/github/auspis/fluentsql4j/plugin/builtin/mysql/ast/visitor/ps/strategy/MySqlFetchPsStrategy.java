package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dql.clause.Fetch;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.FetchPsStrategy;
import java.util.List;

public class MySqlFetchPsStrategy implements FetchPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Fetch clause, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        // Inlined from MySqlFetchRenderStrategy
        if (!clause.isActive()) {
            return new PreparedStatementSpec("", List.of());
        }
        Integer offset = clause.offset();
        Integer rows = clause.rows();
        String sql = String.format("LIMIT %s OFFSET %s", rows, offset);
        return new PreparedStatementSpec(sql, List.of());
    }
}
