package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Having;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.HavingClausePsStrategy;

public class StandardSqlHavingClausePsStrategy implements HavingClausePsStrategy {
    @Override
    public PreparedStatementSpec handle(Having clause, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        if (clause.condition() == null || clause.condition() instanceof NullPredicate) {
            return new PreparedStatementSpec("", java.util.List.of());
        }
        return clause.condition().accept(renderer, ctx);
    }
}
