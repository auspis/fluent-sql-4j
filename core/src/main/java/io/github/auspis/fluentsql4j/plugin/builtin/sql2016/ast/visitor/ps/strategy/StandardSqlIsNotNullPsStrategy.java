package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.predicate.IsNotNull;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.IsNotNullPsStrategy;

public class StandardSqlIsNotNullPsStrategy implements IsNotNullPsStrategy {
    @Override
    public PreparedStatementSpec handle(IsNotNull isNotNull, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec inner = isNotNull.expression().accept(renderer, ctx);
        return new PreparedStatementSpec(inner.sql() + " IS NOT NULL", inner.parameters());
    }
}
