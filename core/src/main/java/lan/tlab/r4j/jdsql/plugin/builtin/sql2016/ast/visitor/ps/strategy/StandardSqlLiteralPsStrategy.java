package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.Arrays;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LiteralPsStrategy;

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
