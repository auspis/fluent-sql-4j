package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface AggregationFunctionProjectionPsStrategy {
    PsDto handle(AggregateCallProjection projection, Visitor<PsDto> visitor, AstContext ctx);
}
