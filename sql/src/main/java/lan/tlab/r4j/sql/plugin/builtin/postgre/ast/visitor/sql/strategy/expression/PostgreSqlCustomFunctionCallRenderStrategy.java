package lan.tlab.r4j.sql.plugin.builtin.postgre.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CustomFunctionCallRenderStrategy;

/**
 * PostgreSQL-specific rendering strategy for custom functions.
 * <p>
 * Handles PostgreSQL-specific syntax for functions like STRING_AGG, ARRAY_AGG,
 * JSONB_AGG, TO_CHAR, DATE_TRUNC, AGE, etc.
 */
public class PostgreSqlCustomFunctionCallRenderStrategy implements CustomFunctionCallRenderStrategy {

    @Override
    public String render(CustomFunctionCall functionCall, SqlRenderer renderer, AstContext ctx) {
        return switch (functionCall.functionName()) {
            case "STRING_AGG" -> renderStringAgg(functionCall, renderer, ctx);
            case "ARRAY_AGG" -> renderArrayAgg(functionCall, renderer, ctx);
            case "JSONB_AGG" -> renderJsonbAgg(functionCall, renderer, ctx);
            case "TO_CHAR" -> renderToChar(functionCall, renderer, ctx);
            case "DATE_TRUNC" -> renderDateTrunc(functionCall, renderer, ctx);
            case "AGE" -> renderAge(functionCall, renderer, ctx);
            case "COALESCE" -> renderCoalesce(functionCall, renderer, ctx);
            case "NULLIF" -> renderNullIf(functionCall, renderer, ctx);
            default -> renderGeneric(functionCall, renderer, ctx);
        };
    }

    /**
     * Renders STRING_AGG with PostgreSQL syntax.
     * <p>
     * Syntax: {@code STRING_AGG([DISTINCT] expression, delimiter [ORDER BY sort_expression])}
     */
    private String renderStringAgg(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("STRING_AGG(");

        // DISTINCT (PostgreSQL supports DISTINCT in STRING_AGG)
        boolean distinct = (Boolean) call.options().getOrDefault("DISTINCT", false);
        if (distinct) {
            sql.append("DISTINCT ");
        }

        // Column expression
        sql.append(call.arguments().get(0).accept(renderer, ctx));

        // SEPARATOR (required in PostgreSQL)
        String separator = (String) call.options().getOrDefault("SEPARATOR", ",");
        sql.append(", '").append(escapeSingleQuotes(separator)).append("'");

        // ORDER BY (inside the function call in PostgreSQL)
        if (call.options().containsKey("ORDER_BY")) {
            sql.append(" ORDER BY ").append(call.options().get("ORDER_BY"));
        }

        sql.append(")");
        return sql.toString();
    }

    /**
     * Renders ARRAY_AGG with PostgreSQL syntax.
     * <p>
     * Syntax: {@code ARRAY_AGG([DISTINCT] expression [ORDER BY sort_expression])}
     */
    private String renderArrayAgg(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("ARRAY_AGG(");

        // DISTINCT
        boolean distinct = (Boolean) call.options().getOrDefault("DISTINCT", false);
        if (distinct) {
            sql.append("DISTINCT ");
        }

        // Column expression
        sql.append(call.arguments().get(0).accept(renderer, ctx));

        // ORDER BY
        if (call.options().containsKey("ORDER_BY")) {
            sql.append(" ORDER BY ").append(call.options().get("ORDER_BY"));
        }

        sql.append(")");
        return sql.toString();
    }

    /**
     * Renders JSONB_AGG with PostgreSQL syntax.
     * <p>
     * Syntax: {@code JSONB_AGG(expression [ORDER BY sort_expression])}
     */
    private String renderJsonbAgg(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("JSONB_AGG(");

        // Column expression
        sql.append(call.arguments().get(0).accept(renderer, ctx));

        // ORDER BY
        if (call.options().containsKey("ORDER_BY")) {
            sql.append(" ORDER BY ").append(call.options().get("ORDER_BY"));
        }

        sql.append(")");
        return sql.toString();
    }

    /**
     * Renders TO_CHAR function.
     * <p>
     * Syntax: {@code TO_CHAR(timestamp, format)}
     */
    private String renderToChar(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String timestamp = call.arguments().get(0).accept(renderer, ctx);
        String format = call.arguments().get(1).accept(renderer, ctx);
        return "TO_CHAR(" + timestamp + ", " + format + ")";
    }

    /**
     * Renders DATE_TRUNC function.
     * <p>
     * Syntax: {@code DATE_TRUNC(field, timestamp)}
     */
    private String renderDateTrunc(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String field = call.arguments().get(0).accept(renderer, ctx);
        String timestamp = call.arguments().get(1).accept(renderer, ctx);
        return "DATE_TRUNC(" + field + ", " + timestamp + ")";
    }

    /**
     * Renders AGE function.
     * <p>
     * Syntax: {@code AGE(timestamp)} or {@code AGE(timestamp1, timestamp2)}
     */
    private String renderAge(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String args =
                call.arguments().stream().map(arg -> arg.accept(renderer, ctx)).collect(Collectors.joining(", "));
        return "AGE(" + args + ")";
    }

    /**
     * Renders COALESCE function.
     * <p>
     * Syntax: {@code COALESCE(expression1, expression2, ...)}
     */
    private String renderCoalesce(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String args =
                call.arguments().stream().map(arg -> arg.accept(renderer, ctx)).collect(Collectors.joining(", "));
        return "COALESCE(" + args + ")";
    }

    /**
     * Renders NULLIF function.
     * <p>
     * Syntax: {@code NULLIF(expression1, expression2)}
     */
    private String renderNullIf(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String expr1 = call.arguments().get(0).accept(renderer, ctx);
        String expr2 = call.arguments().get(1).accept(renderer, ctx);
        return "NULLIF(" + expr1 + ", " + expr2 + ")";
    }

    /**
     * Generic fallback rendering for unknown functions.
     * <p>
     * Renders as: {@code FUNCTION_NAME(arg1, arg2, ...)}
     */
    private String renderGeneric(CustomFunctionCall call, SqlRenderer renderer, AstContext ctx) {
        String args =
                call.arguments().stream().map(arg -> arg.accept(renderer, ctx)).collect(Collectors.joining(", "));
        return call.functionName() + "(" + args + ")";
    }

    /**
     * Escapes single quotes in strings for SQL.
     */
    private String escapeSingleQuotes(String str) {
        return str.replace("'", "''");
    }
}
