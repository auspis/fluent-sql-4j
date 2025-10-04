package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.scalar.NullScalarExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.NullScalarExpressionPsStrategy;

public class DefaultNullScalarExpressionPsStrategy implements NullScalarExpressionPsStrategy {
    @Override
    public PsDto handle(NullScalarExpression nullScalarExpression, Visitor<PsDto> visitor, AstContext ctx) {
        return new PsDto("NULL", List.of());
    }
}
