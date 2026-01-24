package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CustomFunctionCallPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction.CustomFunctionCallOptions;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction.GenericCustomFunctionCallOptions;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction.GroupConcatCustomFunctionCallOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MySQL strategy for rendering custom function calls in prepared statements.
 * <p>
 * This strategy handles rendering of custom functions with options, such as:
 * <ul>
 *   <li>GROUP_CONCAT with SEPARATOR option</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * GROUP_CONCAT(name SEPARATOR ', ')
 * </pre>
 */
public class MysqlCustomFunctionCallPsStrategy implements CustomFunctionCallPsStrategy {

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

        StringBuilder result = new StringBuilder();
        result.append(functionCall.functionName()).append("(").append(args);

        // Delegate options rendering to function-specific strategy
        PreparedStatementSpec optionsSpec =
                selectOptionsStrategy(functionCall.functionName()).renderOptions(functionCall.options());
        result.append(optionsSpec.sql());
        allParams.addAll(optionsSpec.parameters());

        result.append(")");
        return new PreparedStatementSpec(result.toString(), allParams);
    }

    private CustomFunctionCallOptions selectOptionsStrategy(String functionName) {
        if (functionName != null && functionName.equalsIgnoreCase("GROUP_CONCAT")) {
            return new GroupConcatCustomFunctionCallOptions();
        }
        return new GenericCustomFunctionCallOptions();
    }
}
