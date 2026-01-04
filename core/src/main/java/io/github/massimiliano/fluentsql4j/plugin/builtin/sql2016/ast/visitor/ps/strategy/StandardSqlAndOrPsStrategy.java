package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.predicate.AndOr;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.LogicalOperator;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Predicate;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.AndOrPsStrategy;
import java.util.ArrayList;
import java.util.List;

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
