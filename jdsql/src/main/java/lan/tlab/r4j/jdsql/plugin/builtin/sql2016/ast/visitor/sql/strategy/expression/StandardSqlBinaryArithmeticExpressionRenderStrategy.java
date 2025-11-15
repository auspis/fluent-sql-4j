package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.BinaryArithmeticExpressionRenderStrategy;

public class StandardSqlBinaryArithmeticExpressionRenderStrategy implements BinaryArithmeticExpressionRenderStrategy {

    @Override
    public String render(BinaryArithmeticExpression expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "(%s %s %s)",
                expression.lhs().accept(sqlRenderer, ctx),
                expression.operator(),
                expression.rhs().accept(sqlRenderer, ctx));
    }
}
