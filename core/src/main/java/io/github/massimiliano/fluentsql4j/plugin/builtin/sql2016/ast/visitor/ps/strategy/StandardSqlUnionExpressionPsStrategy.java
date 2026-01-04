package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.set.UnionExpression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.set.UnionExpression.UnionType;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.UnionExpressionPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlUnionExpressionPsStrategy implements UnionExpressionPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            UnionExpression expression, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {

        PreparedStatementSpec leftPart = expression.left().accept(astToPsSpecVisitor, ctx);
        PreparedStatementSpec rightPart = expression.right().accept(astToPsSpecVisitor, ctx);

        String sql = String.format(
                "((%s) %s (%s))",
                leftPart.sql(),
                (expression.type() == UnionType.UNION_DISTINCT ? "UNION" : "UNION ALL"),
                rightPart.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(leftPart.parameters());
        parameters.addAll(rightPart.parameters());

        return new PreparedStatementSpec(sql, parameters);
    }
}
