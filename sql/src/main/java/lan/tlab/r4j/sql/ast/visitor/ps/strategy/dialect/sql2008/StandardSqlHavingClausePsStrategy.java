package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.clause.conditional.having.Having;
import lan.tlab.r4j.sql.ast.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.HavingClausePsStrategy;

public class StandardSqlHavingClausePsStrategy implements HavingClausePsStrategy {
    @Override
    public PsDto handle(Having clause, Visitor<PsDto> renderer, AstContext ctx) {
        if (clause.condition() == null || clause.condition() instanceof NullPredicate) {
            return new PsDto("", java.util.List.of());
        }
        return clause.condition().accept(renderer, ctx);
    }
}
