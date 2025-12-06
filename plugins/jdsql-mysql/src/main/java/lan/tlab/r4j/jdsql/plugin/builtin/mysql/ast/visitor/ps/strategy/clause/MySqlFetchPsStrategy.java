package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy.clause;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.dql.clause.Fetch;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.FetchPsStrategy;

public class MySqlFetchPsStrategy implements FetchPsStrategy {

    @Override
    public PsDto handle(Fetch clause, PreparedStatementRenderer renderer, AstContext ctx) {
        // Inlined from MySqlFetchRenderStrategy
        if (!clause.isActive()) {
            return new PsDto("", List.of());
        }
        Integer offset = clause.offset();
        Integer rows = clause.rows();
        String sql = String.format("LIMIT %s OFFSET %s", rows, offset);
        return new PsDto(sql, List.of());
    }
}
