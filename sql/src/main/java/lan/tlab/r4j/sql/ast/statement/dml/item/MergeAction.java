package lan.tlab.r4j.sql.ast.statement.dml.item;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.visitor.Visitable;

public interface MergeAction extends Visitable {

    record WhenMatchedUpdate(Predicate condition, List<UpdateItem> updateItems) implements MergeAction {
        public WhenMatchedUpdate(List<UpdateItem> updateItems) {
            this(null, updateItems);
        }

        @Override
        public <T> T accept(
                lan.tlab.r4j.sql.ast.visitor.Visitor<T> visitor, lan.tlab.r4j.sql.ast.visitor.AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    record WhenMatchedDelete(Predicate condition) implements MergeAction {
        @Override
        public <T> T accept(
                lan.tlab.r4j.sql.ast.visitor.Visitor<T> visitor, lan.tlab.r4j.sql.ast.visitor.AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    record WhenNotMatchedInsert(Predicate condition, List<ColumnReference> columns, InsertData insertData)
            implements MergeAction {
        public WhenNotMatchedInsert(List<ColumnReference> columns, InsertData insertData) {
            this(null, columns, insertData);
        }

        @Override
        public <T> T accept(
                lan.tlab.r4j.sql.ast.visitor.Visitor<T> visitor, lan.tlab.r4j.sql.ast.visitor.AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }
}
