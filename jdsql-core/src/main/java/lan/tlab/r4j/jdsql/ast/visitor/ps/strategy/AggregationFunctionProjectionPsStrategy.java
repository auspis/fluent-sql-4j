package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.projection.AggregateCallProjection;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface AggregationFunctionProjectionPsStrategy {
    PreparedStatementSpec handle(
            AggregateCallProjection projection, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
