package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.clause.pagination;

import lan.tlab.sqlbuilder.ast.clause.pagination.Pagination;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class OffsetRowsRenderStrategy implements PaginationRenderStrategy {

    @Override
    public String render(Pagination clause, SqlRenderer sqlRenderer, AstContext ctx) {
        if (!clause.isActive()) {
            return "";
        }

        Integer page = clause.getPage();
        Integer perPage = clause.getPerPage();
        int offset = Math.max(0, page - 1) * perPage;
        return String.format("OFFSET %s ROWS FETCH NEXT %s ROWS ONLY", offset, perPage);
    }
}
