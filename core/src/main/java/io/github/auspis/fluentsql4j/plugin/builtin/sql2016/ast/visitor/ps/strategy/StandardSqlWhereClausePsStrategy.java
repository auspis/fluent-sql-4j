package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhereClausePsStrategy;

public class StandardSqlWhereClausePsStrategy implements WhereClausePsStrategy {
    @Override
    public PreparedStatementSpec handle(Where where, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        Predicate cond = where.condition();
        if (cond instanceof NullPredicate) {
            return new PreparedStatementSpec("", List.of());
        }
        return cond.accept(renderer, ctx);
    }
}
