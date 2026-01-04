package lan.tlab.r4j.jdsql.ast.dml.component;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitable;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public interface MergeAction extends Visitable {

    record WhenMatchedUpdate(Predicate condition, List<UpdateItem> updateItems) implements MergeAction {
        public WhenMatchedUpdate(List<UpdateItem> updateItems) {
            this(null, updateItems);
        }

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    record WhenMatchedDelete(Predicate condition) implements MergeAction {
        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    record WhenNotMatchedInsert(Predicate condition, List<ColumnReference> columns, InsertData insertData)
            implements MergeAction {
        public WhenNotMatchedInsert(List<ColumnReference> columns, InsertData insertData) {
            this(null, columns, insertData);
        }

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }
}
