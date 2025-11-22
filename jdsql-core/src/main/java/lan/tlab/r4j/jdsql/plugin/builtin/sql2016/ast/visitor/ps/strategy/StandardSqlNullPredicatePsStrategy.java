package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.NullPredicatePsStrategy;

public class StandardSqlNullPredicatePsStrategy implements NullPredicatePsStrategy {

    @Override
    public PsDto handle(NullPredicate expression, Visitor<PsDto> renderer, AstContext ctx) {
        return new PsDto("NULL", List.of());
    }
}
