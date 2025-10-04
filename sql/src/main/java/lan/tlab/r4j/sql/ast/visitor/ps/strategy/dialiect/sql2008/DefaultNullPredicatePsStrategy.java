package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NullPredicatePsStrategy;

public class DefaultNullPredicatePsStrategy implements NullPredicatePsStrategy {

    @Override
    public PsDto handle(NullPredicate expression, Visitor<PsDto> visitor, AstContext ctx) {
        return new PsDto("NULL", List.of());
    }
}
