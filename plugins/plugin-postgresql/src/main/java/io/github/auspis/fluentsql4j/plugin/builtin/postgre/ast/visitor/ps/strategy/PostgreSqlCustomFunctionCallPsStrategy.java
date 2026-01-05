package io.github.auspis.fluentsql4j.plugin.builtin.postgre.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CustomFunctionCallPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCustomFunctionCallPsStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PostgreSQL-aware rendering for custom function calls.
 */
public final class PostgreSqlCustomFunctionCallPsStrategy implements CustomFunctionCallPsStrategy {

    private static final String OPTION_ORDER_BY = "ORDER_BY";
    private static final String OPTION_SEPARATOR = "SEPARATOR";
    private static final String OPTION_DISTINCT = "DISTINCT";

    private final CustomFunctionCallPsStrategy fallback = new StandardSqlCustomFunctionCallPsStrategy();

    @Override
    public PreparedStatementSpec handle(
            CustomFunctionCall functionCall, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        String name = functionCall.functionName().toUpperCase();
        return switch (name) {
            case "STRING_AGG" -> renderStringAgg(functionCall, astToPsSpecVisitor, ctx);
            case "ARRAY_AGG", "JSONB_AGG" -> renderAggregateWithOrdering(functionCall, astToPsSpecVisitor, ctx);
            default -> fallback.handle(functionCall, astToPsSpecVisitor, ctx);
        };
    }

    private PreparedStatementSpec renderStringAgg(
            CustomFunctionCall functionCall, AstToPreparedStatementSpecVisitor visitor, AstContext ctx) {
        PreparedStatementSpec valueSpec = functionCall.arguments().getFirst().accept(visitor, ctx);
        List<Object> params = new ArrayList<>(valueSpec.parameters());

        Map<String, Object> options = functionCall.options();
        boolean distinct = Boolean.TRUE.equals(options.get(OPTION_DISTINCT));
        String orderBy = (String) options.get(OPTION_ORDER_BY);
        String separator = (String) options.getOrDefault(OPTION_SEPARATOR, ",");

        StringBuilder sql = new StringBuilder("STRING_AGG(");
        if (distinct) {
            sql.append("DISTINCT ");
        }
        sql.append(valueSpec.sql()).append(", ?");
        if (orderBy != null) {
            sql.append(" ORDER BY ").append(orderBy);
        }
        sql.append(")");

        params.add(separator);
        return new PreparedStatementSpec(sql.toString(), params);
    }

    private PreparedStatementSpec renderAggregateWithOrdering(
            CustomFunctionCall functionCall, AstToPreparedStatementSpecVisitor visitor, AstContext ctx) {
        PreparedStatementSpec valueSpec = functionCall.arguments().getFirst().accept(visitor, ctx);
        List<Object> params = new ArrayList<>(valueSpec.parameters());

        Map<String, Object> options = functionCall.options();
        boolean distinct = Boolean.TRUE.equals(options.get(OPTION_DISTINCT));
        String orderBy = (String) options.get(OPTION_ORDER_BY);

        StringBuilder sql = new StringBuilder(functionCall.functionName()).append("(");
        if (distinct) {
            sql.append("DISTINCT ");
        }
        sql.append(valueSpec.sql());
        if (orderBy != null) {
            sql.append(" ORDER BY ").append(orderBy);
        }
        sql.append(")");

        return new PreparedStatementSpec(sql.toString(), params);
    }
}
