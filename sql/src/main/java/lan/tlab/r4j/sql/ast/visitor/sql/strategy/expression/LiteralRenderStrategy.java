package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import java.util.Objects;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class LiteralRenderStrategy implements ExpressionRenderStrategy {

    public String render(Literal<?> literal, SqlRenderer sqlRenderer, AstContext ctx) {
        Object value = literal.value();
        if (Objects.isNull(value)) {
            return "null";
        }

        return switch (value) {
            case String s -> String.format("'%s'", s);
            case Number i -> String.valueOf(i);
            case Boolean b -> String.valueOf(b);
            default -> String.format("'%s'", value);
        };
    }
}
