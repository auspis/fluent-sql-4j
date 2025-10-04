package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.clause.fetch;

import lan.tlab.sqlbuilder.ast.clause.fetch.Fetch;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class OffsetRowsRenderStrategy implements FetchRenderStrategy {

    @Override
    public String render(Fetch clause, SqlRenderer sqlRenderer, AstContext ctx) {
        if (!clause.isActive()) {
            return "";
        }

        Integer offset = clause.getOffset();
        Integer rows = clause.getRows();
        return String.format("OFFSET %s ROWS FETCH NEXT %s ROWS ONLY", offset, rows);
    }
}
