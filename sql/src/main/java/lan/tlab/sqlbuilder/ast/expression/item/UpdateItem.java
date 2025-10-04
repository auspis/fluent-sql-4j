package lan.tlab.sqlbuilder.ast.expression.item;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitable;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
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
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
