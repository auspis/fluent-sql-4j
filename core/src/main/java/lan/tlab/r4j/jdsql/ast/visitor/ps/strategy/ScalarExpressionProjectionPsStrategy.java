package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface ScalarExpressionProjectionPsStrategy {
    PreparedStatementSpec handle(
            ScalarExpressionProjection projection, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
