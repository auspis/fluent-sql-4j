package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dql.clause.Sorting;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.SortingPsStrategy;

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
