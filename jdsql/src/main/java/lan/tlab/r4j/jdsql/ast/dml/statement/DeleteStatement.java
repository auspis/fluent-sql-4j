package lan.tlab.r4j.jdsql.ast.dml.statement;

import lan.tlab.r4j.jdsql.ast.common.expression.set.TableExpression;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record DeleteStatement(TableExpression table, Where where) implements DataManipulationStatement {

    public DeleteStatement {
        if (where == null) where = Where.nullObject();
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public static DeleteStatementBuilder builder() {
        return new DeleteStatementBuilder();
    }

    public static class DeleteStatementBuilder {
        private TableExpression table;
        private Where where;

        public DeleteStatementBuilder table(TableExpression table) {
            this.table = table;
            return this;
        }

        public DeleteStatementBuilder where(Where where) {
            this.where = where;
            return this;
        }

        public DeleteStatement build() {
            return new DeleteStatement(table, where);
        }
    }
}
