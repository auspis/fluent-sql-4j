package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dql.projection.AggregateCallProjection;
import io.github.massimiliano.fluentsql4j.ast.dql.projection.AggregateExpressionProjection;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface AggregationFunctionProjectionPsStrategy {

    PreparedStatementSpec handle(
            AggregateExpressionProjection projection, Visitor<PreparedStatementSpec> visitor, AstContext ctx);

    default PreparedStatementSpec handle(
            AggregateCallProjection projection, Visitor<PreparedStatementSpec> visitor, AstContext ctx) {
        return handle((AggregateExpressionProjection) projection, visitor, ctx);
    }
}
