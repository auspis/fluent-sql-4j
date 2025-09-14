package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.pagination;

import lan.tlab.sqlbuilder.ast.clause.pagination.Pagination;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.dialect.mysql.strategy.clause.pagination.MySqlLimitOffsetRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.ClauseRenderStrategy;

public interface PaginationRenderStrategy extends ClauseRenderStrategy {

    String render(Pagination clause, SqlRenderer sqlRenderer, AstContext ctx);

    public static PaginationRenderStrategy standardSql2008() {
        return new OffsetRowsRenderStrategy();
    }

    public static PaginationRenderStrategy mysql() {
        return new MySqlLimitOffsetRenderStrategy();
    }
}
