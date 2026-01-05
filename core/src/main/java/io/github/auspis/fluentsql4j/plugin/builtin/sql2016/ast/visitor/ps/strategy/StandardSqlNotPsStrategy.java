package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.predicate.Not;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.NotPsStrategy;

public class StandardSqlNotPsStrategy implements NotPsStrategy {
    @Override
    public PreparedStatementSpec handle(Not expression, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec inner = expression.expression().accept(renderer, ctx);
        String sql = "NOT (" + inner.sql() + ")";
        return new PreparedStatementSpec(sql, inner.parameters());
    }
}
