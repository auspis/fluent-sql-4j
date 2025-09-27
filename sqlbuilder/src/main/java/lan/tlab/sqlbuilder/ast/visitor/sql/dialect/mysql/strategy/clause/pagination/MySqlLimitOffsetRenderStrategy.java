package lan.tlab.sqlbuilder.ast.visitor.sql.dialect.mysql.strategy.clause.pagination;

import lan.tlab.sqlbuilder.ast.clause.pagination.Pagination;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.clause.pagination.PaginationRenderStrategy;

public class MySqlLimitOffsetRenderStrategy implements PaginationRenderStrategy {

    @Override
    public String render(Pagination clause, SqlRenderer sqlRenderer, AstContext ctx) {
        if (!clause.isActive()) {
            return "";
        }

        Integer offset = clause.getOffset();
        Integer rows = clause.getRows();
        return String.format("LIMIT %s OFFSET %s", rows, offset);
    }
}
