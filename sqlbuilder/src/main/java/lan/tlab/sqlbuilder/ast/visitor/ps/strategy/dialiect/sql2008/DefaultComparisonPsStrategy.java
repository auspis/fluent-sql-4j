package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.ComparisonPsStrategy;

public class DefaultComparisonPsStrategy implements ComparisonPsStrategy {
    @Override
    public PsDto handle(Comparison cmp, Visitor<PsDto> visitor, AstContext ctx) {
        String operator;
        switch (cmp.getOperator()) {
            case EQUALS -> operator = "=";
            case NOT_EQUALS -> operator = "<>";
            case GREATER_THAN -> operator = ">";
            case GREATER_THAN_OR_EQUALS -> operator = ">=";
            case LESS_THAN -> operator = "<";
            case LESS_THAN_OR_EQUALS -> operator = "<=";
            default -> throw new UnsupportedOperationException("Operator not supported: " + cmp.getOperator());
        }

        String lhs;
        if (cmp.getLhs() instanceof ColumnReference colLhs) {
            PsDto lhsResult = colLhs.accept(visitor, ctx.copy());
            lhs = lhsResult.sql();
        } else {
            lhs = cmp.getLhs().accept(visitor, ctx).sql();
        }

        String rhsSql;
        List<Object> params = new ArrayList<>();
        if (cmp.getRhs() instanceof ColumnReference colRhs) {
            rhsSql = colRhs.accept(visitor, ctx.copy()).sql();
        } else {
            PsDto rhsResult = cmp.getRhs().accept(visitor, ctx);
            rhsSql = rhsResult.sql();
            params.addAll(rhsResult.parameters());
        }

        return new PsDto(lhs + " " + operator + " " + rhsSql, params);
    }
}
