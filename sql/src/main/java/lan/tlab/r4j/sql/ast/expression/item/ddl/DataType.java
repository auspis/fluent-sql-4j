package lan.tlab.r4j.sql.ast.expression.item.ddl;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.expression.item.SqlItem;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface DataType extends SqlItem {

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

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class SimpleDataType implements DataType {
        private final String name;

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class ParameterizedDataType implements DataType {
        private final String name;
        private final List<Expression> parameters;

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }
}
