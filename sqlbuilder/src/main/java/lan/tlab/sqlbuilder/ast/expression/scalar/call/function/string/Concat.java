package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Concat implements FunctionCall {

    private final String separator;
    private final List<ScalarExpression> stringExpressions;

    public static Concat concat(ScalarExpression... expressions) {
        return new Concat("", Stream.of(expressions).toList());
    }

    public static Concat concatWithSeparator(String separator, ScalarExpression... expressions) {
        return new Concat(separator, Stream.of(expressions).toList());
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this);
    }
}
