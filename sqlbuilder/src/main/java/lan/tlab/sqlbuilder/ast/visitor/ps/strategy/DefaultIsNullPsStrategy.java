package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.expression.bool.IsNull;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public class DefaultIsNullPsStrategy implements IsNullPsStrategy {
    @Override
    public PsDto handle(IsNull isNull, Visitor<PsDto> visitor, AstContext ctx) {
        PsDto inner = isNull.getExpression().accept(visitor, ctx);
        return new PsDto(inner.sql() + " IS NULL", inner.parameters());
    }
}
