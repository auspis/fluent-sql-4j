package io.github.massimiliano.fluentsql4j.ast.core.expression.function.string;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.FunctionCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public record Concat(String separator, List<ScalarExpression> stringExpressions) implements FunctionCall {

    public Concat(List<ScalarExpression> list) {
        this("", Collections.unmodifiableList(list));
    }

    public static Concat concat(ScalarExpression... expressions) {
        return new Concat(Stream.of(expressions).toList());
    }

    public static Concat concatWithSeparator(String separator, ScalarExpression... expressions) {
        return new Concat(separator, Stream.of(expressions).toList());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
