package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.NullBooleanExpressionPsStrategy;

public class DefaultNullBooleanExpressionPsStrategy implements NullBooleanExpressionPsStrategy {

    @Override
    public PsDto handle(NullBooleanExpression expression, Visitor<PsDto> visitor, AstContext ctx) {
        return new PsDto("NULL", List.of());
    }
}
