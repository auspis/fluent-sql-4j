package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.clause.conditional.having.Having;
import lan.tlab.r4j.sql.ast.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.HavingClausePsStrategy;

public class DefaultHavingClausePsStrategy implements HavingClausePsStrategy {
    @Override
    public PsDto handle(Having clause, Visitor<PsDto> visitor, AstContext ctx) {
        if (clause.getCondition() == null || clause.getCondition() instanceof NullPredicate) {
            return new PsDto("", java.util.List.of());
        }
        return clause.getCondition().accept(visitor, ctx);
    }
}
