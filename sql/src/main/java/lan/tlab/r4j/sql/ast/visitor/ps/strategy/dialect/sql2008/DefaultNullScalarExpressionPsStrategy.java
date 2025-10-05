package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NullScalarExpressionPsStrategy;

public class DefaultNullScalarExpressionPsStrategy implements NullScalarExpressionPsStrategy {
    @Override
    public PsDto handle(NullScalarExpression nullScalarExpression, Visitor<PsDto> visitor, AstContext ctx) {
        return new PsDto("NULL", List.of());
    }
}
