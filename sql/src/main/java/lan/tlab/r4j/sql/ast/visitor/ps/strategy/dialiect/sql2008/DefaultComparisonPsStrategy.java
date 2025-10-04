package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.bool.Comparison;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ComparisonPsStrategy;

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

        List<Object> params = new ArrayList<>();

        // Handle LHS (Left Hand Side)
        PsDto lhsResult = cmp.getLhs().accept(visitor, ctx.copy());
        String lhs = lhsResult.sql();
        params.addAll(lhsResult.parameters());

        // Handle RHS (Right Hand Side)
        PsDto rhsResult = cmp.getRhs().accept(visitor, ctx);
        String rhsSql = rhsResult.sql();
        params.addAll(rhsResult.parameters());

        return new PsDto(lhs + " " + operator + " " + rhsSql, params);
    }
}
