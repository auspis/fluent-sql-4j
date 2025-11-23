package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy.clause;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.dql.clause.Fetch;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.FetchPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public class MySqlFetchPsStrategy implements FetchPsStrategy {

    private final SqlRenderer sqlRenderer;

    public MySqlFetchPsStrategy(SqlRenderer sqlRenderer) {
        this.sqlRenderer = sqlRenderer;
    }

    @Override
    public PsDto handle(Fetch clause, PreparedStatementRenderer renderer, AstContext ctx) {
        String sql = sqlRenderer.visit(clause, ctx);
        return new PsDto(sql, List.of());
    }
}
