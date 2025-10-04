package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface OnJoinPsStrategy {
    PsDto handle(OnJoin onJoin, Visitor<PsDto> visitor, AstContext ctx);
}
