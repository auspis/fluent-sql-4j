package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Length;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LengthPsStrategy;

public class StandardSqlLengthPsStrategy implements LengthPsStrategy {

    @Override
    public PreparedStatementSpec handle(Length length, PreparedStatementRenderer renderer, AstContext ctx) {
        var expressionResult = length.expression().accept(renderer, ctx);

        String sql = String.format("LENGTH(%s)", expressionResult.sql());
        return new PreparedStatementSpec(sql, expressionResult.parameters());
    }
}
