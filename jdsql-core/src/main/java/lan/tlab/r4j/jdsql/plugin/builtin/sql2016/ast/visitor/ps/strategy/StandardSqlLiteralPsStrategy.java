package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.Arrays;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LiteralPsStrategy;

public class StandardSqlLiteralPsStrategy implements LiteralPsStrategy {
    @Override
    public PsDto handle(Literal<?> literal, Visitor<PsDto> renderer, AstContext ctx) {
        // In DDL context, render literals inline instead of as placeholders
        if (ctx.hasFeature(AstContext.Feature.DDL)) {
            return new PsDto(formatLiteralValue(literal.value()), List.of());
        }
        return new PsDto("?", Arrays.asList(literal.value()));
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
