package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.clause.orderby.Sorting;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SortingPsStrategy;

public class DefaultSortingPsStrategy implements SortingPsStrategy {
    @Override
    public PsDto handle(Sorting sorting, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto exprResult = sorting.getExpression().accept(renderer, ctx);
        String sql = exprResult.sql();
        String order = sorting.getSortOrder().getSqlKeyword();
        if (!order.isEmpty()) {
            sql += " " + order;
        }
        return new PsDto(sql, exprResult.parameters());
    }
}
