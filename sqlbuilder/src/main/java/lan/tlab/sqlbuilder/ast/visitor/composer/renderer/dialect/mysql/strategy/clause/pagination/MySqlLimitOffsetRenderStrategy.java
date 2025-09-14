package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.dialect.mysql.strategy.clause.pagination;

import lan.tlab.sqlbuilder.ast.clause.pagination.Pagination;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.pagination.PaginationRenderStrategy;

public class MySqlLimitOffsetRenderStrategy implements PaginationRenderStrategy {

    @Override
    public String render(Pagination clause, SqlRenderer sqlRenderer, AstContext ctx) {
        if (clause.isActive()) {
            return "";
        }

        Integer page = clause.getPage();
        Integer perPage = clause.getPerPage();
        int offset = Math.max(0, page - 1) * perPage;
        return String.format("LIMIT %s OFFSET %s", perPage, offset);
    }
}
