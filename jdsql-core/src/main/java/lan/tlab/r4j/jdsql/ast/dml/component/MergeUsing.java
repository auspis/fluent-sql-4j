package lan.tlab.r4j.jdsql.ast.dml.component;

import lan.tlab.r4j.jdsql.ast.common.expression.set.TableExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitable;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record MergeUsing(TableExpression source) implements Visitable {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
