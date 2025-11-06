package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCallImpl;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.CountDistinct;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.CountStar;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.AggregateCallPsStrategy;

public class StandardSqlAggregateCallPsStrategy implements AggregateCallPsStrategy {
    @Override
    public PsDto handle(AggregateCall aggregateCall, Visitor<PsDto> renderer, AstContext ctx) {
        return switch (aggregateCall) {
            case AggregateCallImpl e -> {
                String functionName = e.operator().name();
                PsDto argResult = e.expression() == null ? null : e.expression().accept(renderer, ctx);
                String argumentSql = argResult == null ? "*" : argResult.sql();
                List<Object> params = argResult == null ? List.of() : argResult.parameters();
                String sql = functionName + "(" + argumentSql + ")";
                yield new PsDto(sql, params);
            }
            case CountDistinct e -> {
                PsDto argResult = e.expression().accept(renderer, ctx);
                String sql = "COUNT(DISTINCT " + argResult.sql() + ")";
                yield new PsDto(sql, argResult.parameters());
            }
            case CountStar e -> {
                yield new PsDto("COUNT(*)", List.of());
            }
            default -> throw new UnsupportedOperationException("Unknown aggregate function: " + aggregateCall);
        };
    }
}
