package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Length;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LengthPsStrategy;

public class StandardSqlLengthPsStrategy implements LengthPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Length length, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var expressionResult = length.expression().accept(astToPsSpecVisitor, ctx);

        String sql = String.format("LENGTH(%s)", expressionResult.sql());
        return new PreparedStatementSpec(sql, expressionResult.parameters());
    }
}
