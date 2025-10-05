package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.predicate.logical.Not;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NotPsStrategy;

public class DefaultNotPsStrategy implements NotPsStrategy {
    @Override
    public PsDto handle(Not expression, Visitor<PsDto> visitor, AstContext ctx) {
        PsDto inner = expression.getExpression().accept(visitor, ctx);
        String sql = "NOT (" + inner.sql() + ")";
        return new PsDto(sql, inner.parameters());
    }
}
