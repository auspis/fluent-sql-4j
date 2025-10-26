package lan.tlab.r4j.sql.ast.statement.dml;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record UpdateStatement(TableExpression table, List<UpdateItem> set, Where where)
        implements DataManipulationStatement {

    public UpdateStatement {
        if (where == null) where = Where.nullObject();
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public static UpdateStatementBuilder builder() {
        return new UpdateStatementBuilder();
    }

    public static class UpdateStatementBuilder {
        private TableExpression table;
        private List<UpdateItem> set;
        private Where where;

        public UpdateStatementBuilder table(TableExpression table) {
            this.table = table;
            return this;
        }

        public UpdateStatementBuilder set(List<UpdateItem> set) {
            this.set = set;
            return this;
        }

        public UpdateStatementBuilder where(Where where) {
            this.where = where;
            return this;
        }

        public UpdateStatement build() {
            return new UpdateStatement(table, set, where);
        }
    }
}
