package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.UnaryArithmeticExpressionRenderStrategy;

public class StandardSqlUnaryArithmeticExpressionRenderStrategy implements UnaryArithmeticExpressionRenderStrategy {

    @Override
    public String render(UnaryArithmeticExpression expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "(%s%s)", expression.operator(), expression.expression().accept(sqlRenderer, ctx));
    }
}
