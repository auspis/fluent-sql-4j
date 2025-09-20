package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public class DefaultAggregateCallPsStrategy implements AggregateCallPsStrategy {
    @Override
    public PsDto handle(AggregateCall aggregateCall, Visitor<PsDto> visitor, AstContext ctx) {
        String functionName = null;
        String argumentSql = null;
        List<Object> params = new ArrayList<>();
        try {
            var operatorField = aggregateCall.getClass().getDeclaredField("operator");
            operatorField.setAccessible(true);
            var operator = operatorField.get(aggregateCall);
            functionName = operator.toString();
            // Normalize to SQL function names
            functionName = switch (functionName) {
                case "COUNT" -> "COUNT";
                case "SUM" -> "SUM";
                case "AVG" -> "AVG";
                case "MIN" -> "MIN";
                case "MAX" -> "MAX";
                default -> throw new UnsupportedOperationException("Unknown aggregate function: " + functionName);
            };
            var argumentField = aggregateCall.getClass().getDeclaredField("expression");
            argumentField.setAccessible(true);
            var argument = argumentField.get(aggregateCall);
            if (argument == null) {
                argumentSql = "*";
            } else {
                PsDto argResult = ((ScalarExpression) argument).accept(visitor, ctx);
                argumentSql = argResult.sql();
                params.addAll(argResult.parameters());
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("AggregateCall reflection failed", e);
        }
        String sql = functionName + "(" + argumentSql + ")";
        return new PsDto(sql, params);
    }
}
