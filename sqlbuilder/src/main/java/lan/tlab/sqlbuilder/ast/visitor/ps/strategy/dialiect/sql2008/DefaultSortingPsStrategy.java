package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.clause.orderby.Sorting;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.SortingPsStrategy;

public class DefaultSortingPsStrategy implements SortingPsStrategy {
    @Override
    public PsDto handle(Sorting sorting, Visitor<PsDto> visitor, AstContext ctx) {
        PsDto exprResult = sorting.getExpression().accept(visitor, ctx);
        String sql = exprResult.sql();
        String order = sorting.getSortOrder().getSqlKeyword();
        if (!order.isEmpty()) {
            sql += " " + order;
        }
        return new PsDto(sql, exprResult.parameters());
    }
}
