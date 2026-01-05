package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.NullPredicatePsStrategy;

public class StandardSqlNullPredicatePsStrategy implements NullPredicatePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            NullPredicate expression, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        return new PreparedStatementSpec("NULL", List.of());
    }
}
