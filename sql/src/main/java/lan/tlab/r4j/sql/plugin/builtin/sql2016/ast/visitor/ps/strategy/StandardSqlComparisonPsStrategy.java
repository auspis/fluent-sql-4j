package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ComparisonPsStrategy;

public class StandardSqlComparisonPsStrategy implements ComparisonPsStrategy {
    @Override
    public PsDto handle(Comparison cmp, Visitor<PsDto> renderer, AstContext ctx) {
        String operator;
        switch (cmp.operator()) {
            case EQUALS -> operator = "=";
            case NOT_EQUALS -> operator = "<>";
            case GREATER_THAN -> operator = ">";
            case GREATER_THAN_OR_EQUALS -> operator = ">=";
            case LESS_THAN -> operator = "<";
            case LESS_THAN_OR_EQUALS -> operator = "<=";
            default -> throw new UnsupportedOperationException("Operator not supported: " + cmp.operator());
        }

        List<Object> params = new ArrayList<>();

        // Handle LHS (Left Hand Side)
        PsDto lhsResult = cmp.lhs().accept(renderer, ctx.copy());
        String lhs = lhsResult.sql();
        params.addAll(lhsResult.parameters());

        // Handle RHS (Right Hand Side)
        PsDto rhsResult = cmp.rhs().accept(renderer, ctx);
        String rhsSql = rhsResult.sql();
        params.addAll(rhsResult.parameters());

        return new PsDto(lhs + " " + operator + " " + rhsSql, params);
    }
}
