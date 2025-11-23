package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.jdsql.ast.dql.clause.Fetch;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.FetchRenderStrategy;

public class MySqlFetchRenderStrategy implements FetchRenderStrategy {

    @Override
    public String render(Fetch clause, SqlRenderer sqlRenderer, AstContext ctx) {
        if (!clause.isActive()) {
            return "";
        }
        Integer offset = clause.offset();
        Integer rows = clause.rows();
        return String.format("LIMIT %s OFFSET %s", rows, offset);
    }
}
