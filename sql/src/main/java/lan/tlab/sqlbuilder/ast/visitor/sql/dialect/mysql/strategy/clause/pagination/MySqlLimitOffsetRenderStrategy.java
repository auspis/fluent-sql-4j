package lan.tlab.sqlbuilder.ast.visitor.sql.dialect.mysql.strategy.clause.pagination;

import lan.tlab.sqlbuilder.ast.clause.fetch.Fetch;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.clause.fetch.FetchRenderStrategy;

public class MySqlLimitOffsetRenderStrategy implements FetchRenderStrategy {

    @Override
    public String render(Fetch clause, SqlRenderer sqlRenderer, AstContext ctx) {
        if (!clause.isActive()) {
            return "";
        }

        Integer offset = clause.getOffset();
        Integer rows = clause.getRows();
        return String.format("LIMIT %s OFFSET %s", rows, offset);
    }
}
