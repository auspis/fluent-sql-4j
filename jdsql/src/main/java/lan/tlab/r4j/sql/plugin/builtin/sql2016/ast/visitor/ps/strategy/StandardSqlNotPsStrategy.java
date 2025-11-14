package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.common.predicate.logical.Not;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NotPsStrategy;

public class StandardSqlNotPsStrategy implements NotPsStrategy {
    @Override
    public PsDto handle(Not expression, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto inner = expression.expression().accept(renderer, ctx);
        String sql = "NOT (" + inner.sql() + ")";
        return new PsDto(sql, inner.parameters());
    }
}
