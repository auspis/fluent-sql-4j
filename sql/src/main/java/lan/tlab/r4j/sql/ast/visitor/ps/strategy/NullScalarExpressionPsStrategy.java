package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface NullScalarExpressionPsStrategy {
    PsDto handle(NullScalarExpression nullScalarExpression, Visitor<PsDto> visitor, AstContext ctx);
}
