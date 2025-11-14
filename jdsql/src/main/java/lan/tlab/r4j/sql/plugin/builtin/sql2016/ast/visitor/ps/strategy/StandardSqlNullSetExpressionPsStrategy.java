package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.Collections;
import lan.tlab.r4j.sql.ast.common.expression.set.NullSetExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NullSetExpressionPsStrategy;

public class StandardSqlNullSetExpressionPsStrategy implements NullSetExpressionPsStrategy {

    @Override
    public PsDto handle(NullSetExpression expression, PreparedStatementRenderer renderer, AstContext ctx) {
        return new PsDto("", Collections.emptyList());
    }
}
