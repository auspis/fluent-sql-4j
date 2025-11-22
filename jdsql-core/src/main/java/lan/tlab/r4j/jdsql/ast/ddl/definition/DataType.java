package lan.tlab.r4j.jdsql.ast.ddl.definition;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.Expression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

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
