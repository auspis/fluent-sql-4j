package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.function.string.CharLength;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CharLengthPsStrategy;

public class StandardSqlCharLengthPsStrategy implements CharLengthPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            CharLength charLength, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var expressionResult = charLength.expression().accept(astToPsSpecVisitor, ctx);

        String sql = String.format("CHAR_LENGTH(%s)", expressionResult.sql());
        return new PreparedStatementSpec(sql, expressionResult.parameters());
    }
}
