package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.predicate.IsNull;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IsNullPsStrategy;

public class StandardSqlIsNullPsStrategy implements IsNullPsStrategy {
    @Override
    public PsDto handle(IsNull isNull, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto inner = isNull.expression().accept(renderer, ctx);
        return new PsDto(inner.sql() + " IS NULL", inner.parameters());
    }
}
