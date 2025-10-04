package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.bool.IsNotNull;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface IsNotNullPsStrategy {
    PsDto handle(IsNotNull isNotNull, Visitor<PsDto> visitor, AstContext ctx);
}
