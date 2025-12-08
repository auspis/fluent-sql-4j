package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.predicate.AndOr;
import lan.tlab.r4j.jdsql.ast.core.predicate.LogicalOperator;
import lan.tlab.r4j.jdsql.ast.core.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.AndOrPsStrategy;

public class StandardSqlAndOrPsStrategy implements AndOrPsStrategy {
    @Override
    public PreparedStatementSpec handle(AndOr expression, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        String operator = expression.operator() == LogicalOperator.AND ? "AND" : "OR";
        List<String> sqlParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (Predicate expr : expression.operands()) {
            PreparedStatementSpec res = expr.accept(renderer, ctx);
            sqlParts.add("(" + res.sql() + ")");
            params.addAll(res.parameters());
        }
        String sql = String.join(" " + operator + " ", sqlParts);
        return new PreparedStatementSpec(sql, params);
    }
}
