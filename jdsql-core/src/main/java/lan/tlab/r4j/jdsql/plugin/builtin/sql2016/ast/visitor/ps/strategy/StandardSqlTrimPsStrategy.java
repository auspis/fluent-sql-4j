package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Trim;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Trim.TrimMode;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.TrimPsStrategy;

public class StandardSqlTrimPsStrategy implements TrimPsStrategy {

    @Override
    public PreparedStatementSpec handle(Trim functionCall, PreparedStatementRenderer renderer, AstContext ctx) {
        TrimMode mode = functionCall.mode();
        ScalarExpression charactersToRemove = functionCall.charactersToRemove();
        ScalarExpression stringExpression = functionCall.stringExpression();

        StringBuilder sb = new StringBuilder();
        sb.append("TRIM(");

        if (mode != null) {
            sb.append(mode.name()).append(" ");
        }

        if (charactersToRemove != null) {
            PreparedStatementSpec charactersDto = charactersToRemove.accept(renderer, ctx);
            sb.append(charactersDto.sql()).append(" FROM ");
            PreparedStatementSpec stringDto = stringExpression.accept(renderer, ctx);
            sb.append(stringDto.sql()).append(")");

            List<Object> allParameters = new ArrayList<>(charactersDto.parameters());
            allParameters.addAll(stringDto.parameters());
            return new PreparedStatementSpec(sb.toString(), allParameters);
        } else {
            PreparedStatementSpec stringDto = stringExpression.accept(renderer, ctx);
            sb.append(stringDto.sql()).append(")");
            return new PreparedStatementSpec(sb.toString(), stringDto.parameters());
        }
    }
}
