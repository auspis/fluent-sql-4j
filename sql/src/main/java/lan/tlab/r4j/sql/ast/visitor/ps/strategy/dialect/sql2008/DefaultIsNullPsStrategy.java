package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.predicate.IsNull;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IsNullPsStrategy;

public class DefaultIsNullPsStrategy implements IsNullPsStrategy {
    @Override
    public PsDto handle(IsNull isNull, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto inner = isNull.expression().accept(renderer, ctx);
        return new PsDto(inner.sql() + " IS NULL", inner.parameters());
    }
}
