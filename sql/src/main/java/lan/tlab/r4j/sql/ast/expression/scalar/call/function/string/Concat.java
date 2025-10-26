package lan.tlab.r4j.sql.ast.expression.scalar.call.function.string;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record Concat(String separator, List<ScalarExpression> stringExpressions) implements FunctionCall {

    public static Concat concat(ScalarExpression... expressions) {
        return new Concat("", Stream.of(expressions).toList());
    }

    public static Concat concatWithSeparator(String separator, ScalarExpression... expressions) {
        return new Concat(separator, Stream.of(expressions).toList());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
