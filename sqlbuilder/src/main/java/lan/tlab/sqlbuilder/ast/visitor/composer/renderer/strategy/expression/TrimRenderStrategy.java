package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Trim;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Trim.TrimMode;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class TrimRenderStrategy implements ExpressionRenderStrategy {

    public String render(Trim functionCall, SqlRenderer sqlRenderer) {
        TrimMode mode = functionCall.getMode();
        ScalarExpression charactersToRemove = functionCall.getCharactersToRemove();
        ScalarExpression stringExpression = functionCall.getStringExpression();

        StringBuilder sb = new StringBuilder();
        sb.append("TRIM(");

        if (mode != null) {
            sb.append(mode.name()).append(" ");
        }

        if (charactersToRemove != null) {
            sb.append(charactersToRemove.accept(sqlRenderer)).append(" FROM ");
        }

        sb.append(stringExpression.accept(sqlRenderer));
        sb.append(")");
        return sb.toString();
    }
}
