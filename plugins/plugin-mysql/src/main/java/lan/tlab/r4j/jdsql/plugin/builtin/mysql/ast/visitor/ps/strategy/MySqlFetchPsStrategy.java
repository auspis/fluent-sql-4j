package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.dql.clause.Fetch;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.FetchPsStrategy;

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
