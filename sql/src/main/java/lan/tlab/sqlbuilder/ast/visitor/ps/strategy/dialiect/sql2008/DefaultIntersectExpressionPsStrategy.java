package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.set.IntersectExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.IntersectExpressionPsStrategy;

public class DefaultIntersectExpressionPsStrategy implements IntersectExpressionPsStrategy {

    @Override
    public PsDto handle(IntersectExpression expression, PreparedStatementVisitor visitor, AstContext ctx) {
        PsDto leftDto = expression.getLeftSetExpression().accept(visitor, ctx);
        PsDto rightDto = expression.getRightSetExpression().accept(visitor, ctx);

        String sql = String.format(
                "((%s) %s (%s))",
                leftDto.sql(),
                expression.getType().equals(IntersectExpression.IntersectType.INTERSECT_ALL)
                        ? "INTERSECT ALL"
                        : "INTERSECT",
                rightDto.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(leftDto.parameters());
        parameters.addAll(rightDto.parameters());

        return new PsDto(sql, parameters);
    }
}
