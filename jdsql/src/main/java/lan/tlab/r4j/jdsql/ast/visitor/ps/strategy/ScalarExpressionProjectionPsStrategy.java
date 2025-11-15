package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface ScalarExpressionProjectionPsStrategy {
    PsDto handle(ScalarExpressionProjection projection, Visitor<PsDto> visitor, AstContext ctx);
}
