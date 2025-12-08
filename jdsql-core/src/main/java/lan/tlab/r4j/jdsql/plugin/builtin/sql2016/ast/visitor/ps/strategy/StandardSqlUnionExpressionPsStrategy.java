package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.set.UnionExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.set.UnionExpression.UnionType;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UnionExpressionPsStrategy;

public class StandardSqlUnionExpressionPsStrategy implements UnionExpressionPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            UnionExpression expression, PreparedStatementRenderer renderer, AstContext ctx) {

        PreparedStatementSpec leftPart = expression.left().accept(renderer, ctx);
        PreparedStatementSpec rightPart = expression.right().accept(renderer, ctx);

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
