package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface ScalarExpressionProjectionPsStrategy {
    PreparedStatementSpec handle(
            ScalarExpressionProjection projection, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
