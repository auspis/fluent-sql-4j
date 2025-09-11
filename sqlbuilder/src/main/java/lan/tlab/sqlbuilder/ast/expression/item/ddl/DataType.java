package lan.tlab.sqlbuilder.ast.expression.item.ddl;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.Expression;
import lan.tlab.sqlbuilder.ast.expression.item.SqlItem;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public interface DataType extends SqlItem {

    ParameterizedDataType VARCHAR_255 = new ParameterizedDataType("VARCHAR", List.of(Literal.of(255)));
    SimpleDataType INTEGER = new SimpleDataType("INTEGER");
    SimpleDataType DATE = new SimpleDataType("DATE");
    SimpleDataType TIMESTAMP = new SimpleDataType("TIMESTAMP");
    SimpleDataType BOOLEAN = new SimpleDataType("BOOLEAN");

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class SimpleDataType implements DataType {
        private final String name;

        @Override
        public <T> T accept(SqlVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class ParameterizedDataType implements DataType {
        private final String name;
        private final List<Expression> parameters;

        @Override
        public <T> T accept(SqlVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }
}
