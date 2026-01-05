package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import io.github.auspis.fluentsql4j.ast.dql.clause.GroupBy;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.GroupByClausePsStrategy;

public class StandardSqlGroupByClausePsStrategy implements GroupByClausePsStrategy {
    @Override
    public PreparedStatementSpec handle(GroupBy clause, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        List<String> exprSqls = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var expr : clause.groupingExpressions()) {
            PreparedStatementSpec res = expr.accept(renderer, ctx);
            exprSqls.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", exprSqls);
        return new PreparedStatementSpec(sql, params);
    }
}
