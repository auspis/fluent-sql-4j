package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.set.IntersectExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.IntersectExpressionPsStrategy;
import java.util.ArrayList;
import java.util.List;

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
