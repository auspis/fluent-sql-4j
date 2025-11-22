package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.predicate.IsNull;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IsNullPsStrategy;

public class StandardSqlIsNullPsStrategy implements IsNullPsStrategy {
    @Override
    public PsDto handle(IsNull isNull, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto inner = isNull.expression().accept(renderer, ctx);
        return new PsDto(inner.sql() + " IS NULL", inner.parameters());
    }
}
