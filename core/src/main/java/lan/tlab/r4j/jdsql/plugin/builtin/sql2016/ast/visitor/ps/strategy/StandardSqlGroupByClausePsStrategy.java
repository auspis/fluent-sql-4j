package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.GroupByClausePsStrategy;

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
