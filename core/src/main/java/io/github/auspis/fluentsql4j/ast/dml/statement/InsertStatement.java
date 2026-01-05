package io.github.auspis.fluentsql4j.ast.dml.statement;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.set.TableExpression;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.DefaultValues;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import java.util.ArrayList;
import java.util.List;

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
