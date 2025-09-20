package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.bool.logical.Not;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.NotPsStrategy;

public class DefaultNotPsStrategy implements NotPsStrategy {
    @Override
    public PsDto handle(Not expression, Visitor<PsDto> visitor, AstContext ctx) {
        PsDto inner = expression.getExpression().accept(visitor, ctx);
        String sql = "NOT (" + inner.sql() + ")";
        return new PsDto(sql, inner.parameters());
    }
}
