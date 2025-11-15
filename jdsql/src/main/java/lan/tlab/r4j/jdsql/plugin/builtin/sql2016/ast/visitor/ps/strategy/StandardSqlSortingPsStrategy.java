package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.clause.Sorting;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.SortingPsStrategy;

public class StandardSqlSortingPsStrategy implements SortingPsStrategy {
    @Override
    public PsDto handle(Sorting sorting, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto exprResult = sorting.expression().accept(renderer, ctx);
        String sql = exprResult.sql();
        String order = sorting.sortOrder().getSqlKeyword();
        if (!order.isEmpty()) {
            sql += " " + order;
        }
        return new PsDto(sql, exprResult.parameters());
    }
}
