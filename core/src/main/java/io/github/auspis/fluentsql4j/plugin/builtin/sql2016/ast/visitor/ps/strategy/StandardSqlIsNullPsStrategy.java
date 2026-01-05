package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.predicate.IsNull;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.IsNullPsStrategy;

public class StandardSqlIsNullPsStrategy implements IsNullPsStrategy {
    @Override
    public PreparedStatementSpec handle(IsNull isNull, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec inner = isNull.expression().accept(renderer, ctx);
        return new PreparedStatementSpec(inner.sql() + " IS NULL", inner.parameters());
    }
}
