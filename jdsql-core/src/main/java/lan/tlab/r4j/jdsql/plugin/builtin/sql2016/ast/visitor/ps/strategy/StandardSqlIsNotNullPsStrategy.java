package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.predicate.IsNotNull;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IsNotNullPsStrategy;

public class StandardSqlIsNotNullPsStrategy implements IsNotNullPsStrategy {
    @Override
    public PsDto handle(IsNotNull isNotNull, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto inner = isNotNull.expression().accept(renderer, ctx);
        return new PsDto(inner.sql() + " IS NOT NULL", inner.parameters());
    }
}
