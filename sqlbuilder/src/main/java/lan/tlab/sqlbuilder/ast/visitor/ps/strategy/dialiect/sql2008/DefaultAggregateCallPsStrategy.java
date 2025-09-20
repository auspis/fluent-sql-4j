package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.AggregateCallPsStrategy;

public class DefaultAggregateCallPsStrategy implements AggregateCallPsStrategy {
    @Override
    public PsDto handle(AggregateCall aggregateCall, Visitor<PsDto> visitor, AstContext ctx) {
        String functionName = null;
        String argumentSql = null;
        List<Object> params = new ArrayList<>();
        var operator = aggregateCall.getOperator();
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
        var argument = aggregateCall.getExpression();
        if (argument == null) {
            argumentSql = "*";
        } else {
            PsDto argResult = ((ScalarExpression) argument).accept(visitor, ctx);
            argumentSql = argResult.sql();
            params.addAll(argResult.parameters());
        }
        String sql = functionName + "(" + argumentSql + ")";
        return new PsDto(sql, params);
    }
}
