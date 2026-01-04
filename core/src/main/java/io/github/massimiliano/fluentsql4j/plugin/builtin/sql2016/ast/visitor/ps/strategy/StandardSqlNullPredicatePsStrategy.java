package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.NullPredicatePsStrategy;
import java.util.List;

public class StandardSqlNullPredicatePsStrategy implements NullPredicatePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            NullPredicate expression, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        return new PreparedStatementSpec("NULL", List.of());
    }
}
