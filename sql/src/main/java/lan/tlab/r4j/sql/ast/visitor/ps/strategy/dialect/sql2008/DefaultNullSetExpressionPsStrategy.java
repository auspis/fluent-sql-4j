package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.Collections;
import lan.tlab.r4j.sql.ast.expression.set.NullSetExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NullSetExpressionPsStrategy;

public class DefaultNullSetExpressionPsStrategy implements NullSetExpressionPsStrategy {

    @Override
    public PsDto handle(NullSetExpression expression, PreparedStatementRenderer renderer, AstContext ctx) {
        return new PsDto("", Collections.emptyList());
    }
}
