package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.r4j.sql.ast.predicate.IsNotNull;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IsNotNullPsStrategy;

public class DefaultIsNotNullPsStrategy implements IsNotNullPsStrategy {
    @Override
    public PsDto handle(IsNotNull isNotNull, Visitor<PsDto> visitor, AstContext ctx) {
        PsDto inner = isNotNull.getExpression().accept(visitor, ctx);
        return new PsDto(inner.sql() + " IS NOT NULL", inner.parameters());
    }
}
