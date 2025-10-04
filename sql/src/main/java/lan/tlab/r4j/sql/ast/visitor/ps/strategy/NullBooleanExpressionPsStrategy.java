package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.bool.NullBooleanExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface NullBooleanExpressionPsStrategy {
    PsDto handle(NullBooleanExpression expression, Visitor<PsDto> visitor, AstContext ctx);
}
