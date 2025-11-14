package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface AggregateCallPsStrategy {
    PsDto handle(AggregateCall aggregateCall, Visitor<PsDto> visitor, AstContext ctx);
}
