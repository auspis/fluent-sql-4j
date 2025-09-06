package lan.tlab.sqlbuilder.ast.expression.item;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lan.tlab.sqlbuilder.ast.visitor.Visitable;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateItem implements Visitable {

    private final ColumnReference column;
    private final ScalarExpression value;

    public static UpdateItem of(String column, ScalarExpression value) {
        return UpdateItem.builder()
                .column(ColumnReference.of("", column))
                .value(value)
                .build();
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
