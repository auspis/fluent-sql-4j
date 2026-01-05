package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.NullScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.NullScalarExpressionPsStrategy;

public class StandardSqlNullScalarExpressionPsStrategy implements NullScalarExpressionPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            NullScalarExpression nullScalarExpression, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        return new PreparedStatementSpec("NULL", List.of());
    }
}
