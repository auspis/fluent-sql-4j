package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.clause.Sorting;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.SortingPsStrategy;

public class StandardSqlSortingPsStrategy implements SortingPsStrategy {
    @Override
    public PreparedStatementSpec handle(Sorting sorting, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec exprResult = sorting.expression().accept(renderer, ctx);
        String sql = exprResult.sql();
        String order = sorting.sortOrder().getSqlKeyword();
        if (!order.isEmpty()) {
            sql += " " + order;
        }
        return new PreparedStatementSpec(sql, exprResult.parameters());
    }
}
