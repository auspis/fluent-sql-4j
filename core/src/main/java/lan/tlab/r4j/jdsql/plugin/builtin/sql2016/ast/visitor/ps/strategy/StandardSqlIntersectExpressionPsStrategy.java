package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.set.IntersectExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IntersectExpressionPsStrategy;

public class StandardSqlIntersectExpressionPsStrategy implements IntersectExpressionPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            IntersectExpression expression, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        PreparedStatementSpec leftDto = expression.leftSetExpression().accept(astToPsSpecVisitor, ctx);
        PreparedStatementSpec rightDto = expression.rightSetExpression().accept(astToPsSpecVisitor, ctx);

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

        return new PreparedStatementSpec(sql, parameters);
    }
}
