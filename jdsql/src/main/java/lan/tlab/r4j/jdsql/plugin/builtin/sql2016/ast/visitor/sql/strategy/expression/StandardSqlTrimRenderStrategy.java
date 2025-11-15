package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Trim;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Trim.TrimMode;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.TrimRenderStrategy;

public class StandardSqlTrimRenderStrategy implements TrimRenderStrategy {

    @Override
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
