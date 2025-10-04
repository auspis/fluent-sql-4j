package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.item.As;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface AsPsStrategy {
    PsDto handle(As as, Visitor<PsDto> visitor, AstContext ctx);
}
