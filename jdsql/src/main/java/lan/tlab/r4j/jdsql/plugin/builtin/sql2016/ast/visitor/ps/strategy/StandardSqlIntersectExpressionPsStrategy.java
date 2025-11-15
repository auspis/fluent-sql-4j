package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.set.IntersectExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IntersectExpressionPsStrategy;

public class StandardSqlIntersectExpressionPsStrategy implements IntersectExpressionPsStrategy {

    @Override
    public PsDto handle(IntersectExpression expression, PreparedStatementRenderer renderer, AstContext ctx) {
        PsDto leftDto = expression.leftSetExpression().accept(renderer, ctx);
        PsDto rightDto = expression.rightSetExpression().accept(renderer, ctx);

        String sql = String.format(
                "((%s) %s (%s))",
                leftDto.sql(),
                expression.type().equals(IntersectExpression.IntersectType.INTERSECT_ALL)
                        ? "INTERSECT ALL"
                        : "INTERSECT",
                rightDto.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(leftDto.parameters());
        parameters.addAll(rightDto.parameters());

        return new PsDto(sql, parameters);
    }
}
