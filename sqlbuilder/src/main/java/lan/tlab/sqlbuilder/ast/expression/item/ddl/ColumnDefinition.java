package lan.tlab.sqlbuilder.ast.expression.item.ddl;

import java.util.List;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lan.tlab.sqlbuilder.ast.visitor.Visitable;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Builder
@Getter
@ToString
public class ColumnDefinition implements Visitable {

    private final String name;

    @Default
    private final DataType type = DataType.VARCHAR_255;

    @Singular
    private final List<Constraint> constraints;

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
