package lan.tlab.sqlbuilder.ast.expression.item.ddl;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.DefaultConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lan.tlab.sqlbuilder.ast.visitor.Visitable;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Tolerate;

@Builder
@Getter
@EqualsAndHashCode
public class ColumnDefinition implements Visitable {

    private final String name;

    @Default
    private final DataType type = DataType.VARCHAR_255;

    private final NotNullConstraint notNullConstraint;
    private final DefaultConstraint defaultConstraint;

    public static ColumnDefinition nullObject() {
        return builder().build();
    }

    public static class ColumnDefinitionBuilder {

        public static ColumnDefinitionBuilder integer(String name) {
            return builder(name, DataType.INTEGER);
        }

        public static ColumnDefinitionBuilder varchar(String name) {
            return builder(name, DataType.VARCHAR_255);
        }

        public static ColumnDefinitionBuilder date(String name) {
            return builder(name, DataType.DATE);
        }

        public static ColumnDefinitionBuilder bool(String name) {
            return builder(name, DataType.BOOLEAN);
        }
    }

    @Tolerate
    public static ColumnDefinitionBuilder builder(String name, DataType type) {
        return builder().name(name).type(type);
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
