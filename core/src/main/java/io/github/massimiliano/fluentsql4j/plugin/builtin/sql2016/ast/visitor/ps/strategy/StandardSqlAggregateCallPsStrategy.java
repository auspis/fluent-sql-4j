package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.aggregate.AggregateCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.aggregate.AggregateCallImpl;
import io.github.massimiliano.fluentsql4j.ast.core.expression.aggregate.CountDistinct;
import io.github.massimiliano.fluentsql4j.ast.core.expression.aggregate.CountStar;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.AggregateCallPsStrategy;
import java.util.List;

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
