package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.AggregateCallImpl;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.CountDistinct;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.CountStar;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.AggregateCallPsStrategy;

public class StandardSqlAggregateCallPsStrategy implements AggregateCallPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            AggregateCall aggregateCall, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        return switch (aggregateCall) {
            case AggregateCallImpl e -> {
                String functionName = e.operator().name();
                PreparedStatementSpec argResult =
                        e.expression() == null ? null : e.expression().accept(renderer, ctx);
                String argumentSql = argResult == null ? "*" : argResult.sql();
                List<Object> params = argResult == null ? List.of() : argResult.parameters();
                String sql = functionName + "(" + argumentSql + ")";
                yield new PreparedStatementSpec(sql, params);
            }
            case CountDistinct e -> {
                PreparedStatementSpec argResult = e.expression().accept(renderer, ctx);
                String sql = "COUNT(DISTINCT " + argResult.sql() + ")";
                yield new PreparedStatementSpec(sql, argResult.parameters());
            }
            case CountStar e -> {
                yield new PreparedStatementSpec("COUNT(*)", List.of());
            }
            default -> throw new UnsupportedOperationException("Unknown aggregate function: " + aggregateCall);
        };
    }
}
