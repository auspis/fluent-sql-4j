package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NullScalarExpressionPsStrategy;

public class StandardSqlNullScalarExpressionPsStrategy implements NullScalarExpressionPsStrategy {
    @Override
    public PsDto handle(NullScalarExpression nullScalarExpression, Visitor<PsDto> renderer, AstContext ctx) {
        return new PsDto("NULL", List.of());
    }
}
