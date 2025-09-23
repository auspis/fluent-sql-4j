package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.Collections;
import lan.tlab.sqlbuilder.ast.expression.set.NullSetExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.NullSetExpressionPsStrategy;

public class DefaultNullSetExpressionPsStrategy implements NullSetExpressionPsStrategy {

    @Override
    public PsDto handle(NullSetExpression expression, PreparedStatementVisitor visitor, AstContext ctx) {
        return new PsDto("", Collections.emptyList());
    }
}
