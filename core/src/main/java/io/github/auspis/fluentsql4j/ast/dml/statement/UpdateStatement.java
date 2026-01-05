package io.github.auspis.fluentsql4j.ast.dml.statement;

import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.expression.set.TableExpression;
import io.github.auspis.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

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
