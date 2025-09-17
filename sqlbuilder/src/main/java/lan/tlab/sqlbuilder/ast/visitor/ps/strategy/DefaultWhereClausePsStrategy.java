package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public class DefaultWhereClausePsStrategy implements WhereClausePsStrategy {
    @Override
    public PsDto handle(Where where, Visitor<PsDto> visitor, AstContext ctx) {
        BooleanExpression cond = where.getCondition();
        if (cond instanceof NullBooleanExpression) {
            return new PsDto("", List.of());
        }
        return cond.accept(visitor, ctx);
    }
}
