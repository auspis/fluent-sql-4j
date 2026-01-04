package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.CustomFunctionCallPsStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Standard SQL strategy for rendering custom function calls in prepared statements.
 * <p>
 * This strategy handles custom functions without any special options or dialect-specific
 * syntax. It simply renders the function name followed by parentheses containing
 * the comma-separated arguments.
 */
public class StandardSqlCustomFunctionCallPsStrategy implements CustomFunctionCallPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            CustomFunctionCall functionCall, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        List<Object> allParams = new ArrayList<>();

        String args = functionCall.arguments().stream()
                .map(arg -> {
                    PreparedStatementSpec argDto = arg.accept(astToPsSpecVisitor, ctx);
                    allParams.addAll(argDto.parameters());
                    return argDto.sql();
                })
                .collect(Collectors.joining(", "));

        String sql = functionCall.functionName() + "(" + args + ")";
        return new PreparedStatementSpec(sql, allParams);
    }
}
