package lan.tlab.r4j.jdsql.ast.dml.statement;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.set.TableExpression;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.DefaultValues;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record InsertStatement(TableExpression table, List<ColumnReference> columns, InsertData data)
        implements DataManipulationStatement {
    // TODO: prevent table null
    public InsertStatement {
        if (columns == null) columns = new ArrayList<>();
        if (data == null) data = new DefaultValues();
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public static InsertStatementBuilder builder() {
        return new InsertStatementBuilder();
    }

    public static class InsertStatementBuilder {
        private TableExpression table;
        private List<ColumnReference> columns;
        private InsertData data;

        public InsertStatementBuilder table(TableExpression table) {
            this.table = table;
            return this;
        }

        public InsertStatementBuilder columns(List<ColumnReference> columns) {
            this.columns = columns;
            return this;
        }

        public InsertStatementBuilder data(InsertData data) {
            this.data = data;
            return this;
        }

        public InsertStatement build() {
            return new InsertStatement(table, columns, data);
        }
    }
}
