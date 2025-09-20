package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.bool.IsNotNull;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.IsNotNullPsStrategy;

public class DefaultIsNotNullPsStrategy implements IsNotNullPsStrategy {
    @Override
    public PsDto handle(IsNotNull isNotNull, Visitor<PsDto> visitor, AstContext ctx) {
        PsDto inner = isNotNull.getExpression().accept(visitor, ctx);
        return new PsDto(inner.sql() + " IS NOT NULL", inner.parameters());
    }
}
