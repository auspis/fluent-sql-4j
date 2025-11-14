package lan.tlab.r4j.sql.ast.dml.component;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record UpdateItem(ColumnReference column, ScalarExpression value) implements Visitable {

    public static UpdateItem of(String column, ScalarExpression value) {
        return new UpdateItem(ColumnReference.of("", column), value);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
