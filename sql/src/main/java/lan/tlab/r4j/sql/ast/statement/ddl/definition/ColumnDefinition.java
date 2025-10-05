package lan.tlab.r4j.sql.ast.statement.ddl.definition;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class ColumnDefinition implements Visitable {

    private final String name;

    @Default
    private final DataType type = DataType.varchar(255);

    private final NotNullConstraintDefinition notNullConstraint;
    private final DefaultConstraintDefinition defaultConstraint;

    public static ColumnDefinition nullObject() {
        return builder().build();
    }

    public static class ColumnDefinitionBuilder {

        public static ColumnDefinitionBuilder integer(String name) {
            return builder(name, DataType.integer());
        }

        public static ColumnDefinitionBuilder varchar(String name) {
            return builder(name, DataType.varchar(255));
        }

        public static ColumnDefinitionBuilder date(String name) {
            return builder(name, DataType.date());
        }

        public static ColumnDefinitionBuilder bool(String name) {
            return builder(name, DataType.bool());
        }
    }

    @Tolerate
    public static ColumnDefinitionBuilder builder(String name, DataType type) {
        return builder().name(name).type(type);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
