package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.WhereClausePsStrategy;

public class StandardSqlWhereClausePsStrategy implements WhereClausePsStrategy {
    @Override
    public PsDto handle(Where where, Visitor<PsDto> renderer, AstContext ctx) {
        Predicate cond = where.condition();
        if (cond instanceof NullPredicate) {
            return new PsDto("", List.of());
        }
        return cond.accept(renderer, ctx);
    }
}
