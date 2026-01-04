package io.github.massimiliano.fluentsql4j.ast.ddl.definition;

import io.github.massimiliano.fluentsql4j.ast.core.expression.Expression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import java.util.List;

public interface DataType extends Expression {

    static ParameterizedDataType varchar(int length) {
        return new ParameterizedDataType("VARCHAR", List.of(Literal.of(length)));
    }

    static ParameterizedDataType decimal(int precision, int scale) {
        return new ParameterizedDataType("DECIMAL", List.of(Literal.of(precision), Literal.of(scale)));
    }

    static SimpleDataType integer() {
        return new SimpleDataType("INTEGER");
    }

    static SimpleDataType date() {
        return new SimpleDataType("DATE");
    }

    static SimpleDataType timestamp() {
        return new SimpleDataType("TIMESTAMP");
    }

    static SimpleDataType bool() {
        return new SimpleDataType("BOOLEAN");
    }

    public static record SimpleDataType(String name) implements DataType {

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    public record ParameterizedDataType(String name, List<Expression> parameters) implements DataType {

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }
}
