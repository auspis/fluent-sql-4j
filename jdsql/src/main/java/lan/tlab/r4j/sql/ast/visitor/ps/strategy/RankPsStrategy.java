package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.common.expression.scalar.window.Rank;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface RankPsStrategy {
    PsDto handle(Rank rank, Visitor<PsDto> visitor, AstContext ctx);
}
