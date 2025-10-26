package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Trim;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Trim.TrimMode;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class TrimRenderStrategy implements ExpressionRenderStrategy {

    public String render(Trim functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        TrimMode mode = functionCall.mode();
        ScalarExpression charactersToRemove = functionCall.charactersToRemove();
        ScalarExpression stringExpression = functionCall.stringExpression();

        StringBuilder sb = new StringBuilder();
        sb.append("TRIM(");

        if (mode != null) {
            sb.append(mode.name()).append(" ");
        }

        if (charactersToRemove != null) {
            sb.append(charactersToRemove.accept(sqlRenderer, ctx)).append(" FROM ");
        }

        sb.append(stringExpression.accept(sqlRenderer, ctx));
        sb.append(")");
        return sb.toString();
    }
}
