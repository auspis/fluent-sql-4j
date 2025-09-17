package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.clause.conditional.having.Having;
import lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public class DefaultHavingClausePsStrategy implements HavingClausePsStrategy {
    @Override
    public PsDto handle(Having clause, Visitor<PsDto> visitor, AstContext ctx) {
        if (clause.getCondition() == null || clause.getCondition() instanceof NullBooleanExpression) {
            return new PsDto("", java.util.List.of());
        }
        return clause.getCondition().accept(visitor, ctx);
    }
}
