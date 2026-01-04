package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.LiteralPsStrategy;
import java.util.Arrays;
import java.util.List;

public class StandardSqlLiteralPsStrategy implements LiteralPsStrategy {
    @Override
    public PreparedStatementSpec handle(Literal<?> literal, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        // In DDL context, render literals inline instead of as placeholders
        if (ctx.hasFeature(AstContext.Feature.DDL)) {
            return new PreparedStatementSpec(formatLiteralValue(literal.value()), List.of());
        }
        return new PreparedStatementSpec("?", Arrays.asList(literal.value()));
    }

    private String formatLiteralValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof String) {
            return "'" + value.toString().replace("'", "''") + "'";
        }
        return value.toString();
    }
}
