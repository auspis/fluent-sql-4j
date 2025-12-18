package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.Collections;
import lan.tlab.r4j.jdsql.ast.core.expression.set.NullSetExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.NullSetExpressionPsStrategy;

public class StandardSqlNullSetExpressionPsStrategy implements NullSetExpressionPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            NullSetExpression expression, AstToPreparedStatementSpecVisitor renderer, AstContext ctx) {
        return new PreparedStatementSpec("", Collections.emptyList());
    }
}
