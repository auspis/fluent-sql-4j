package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface AggregateCallPsStrategy {
    PsDto handle(AggregateCall aggregateCall, Visitor<PsDto> visitor, AstContext ctx);
}
