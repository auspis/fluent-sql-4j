package io.github.massimiliano.fluentsql4j.ast.ddl.definition;

import io.github.massimiliano.fluentsql4j.ast.core.expression.set.TableExpression;
import io.github.massimiliano.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitable;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import java.util.List;

public record TableDefinition(
        TableExpression table,
        List<ColumnDefinition> columns,
        PrimaryKeyDefinition primaryKey,
        List<ConstraintDefinition> constraints,
        List<IndexDefinition> indexes)
        implements Visitable {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public static TableDefinitionBuilder builder() {
        return new TableDefinitionBuilder();
    }

    public static TableDefinition nullObject() {
        return new TableDefinition(null, List.of(), null, List.of(), List.of());
    }

    public static class TableDefinitionBuilder {
        private TableExpression table;
        private List<ColumnDefinition> columns = List.of();
        private PrimaryKeyDefinition primaryKey;
        private List<ConstraintDefinition> constraints = List.of();
        private List<IndexDefinition> indexes = List.of();

        public TableDefinitionBuilder table(TableExpression table) {
            this.table = table;
            return this;
        }

        public TableDefinitionBuilder columns(List<ColumnDefinition> columns) {
            this.columns = columns != null ? List.copyOf(columns) : List.of();
            return this;
        }

        public TableDefinitionBuilder column(ColumnDefinition column) {
            List<ColumnDefinition> newColumns = new java.util.ArrayList<>(this.columns);
            newColumns.add(column);
            this.columns = List.copyOf(newColumns);
            return this;
        }

        public TableDefinitionBuilder primaryKey(PrimaryKeyDefinition primaryKey) {
            this.primaryKey = primaryKey;
            return this;
        }

        public TableDefinitionBuilder primaryKey(List<String> columns) {
            this.primaryKey = new PrimaryKeyDefinition(columns.toArray(new String[0]));
            return this;
        }

        public TableDefinitionBuilder constraints(List<ConstraintDefinition> constraints) {
            this.constraints = constraints != null ? List.copyOf(constraints) : List.of();
            return this;
        }

        public TableDefinitionBuilder constraint(ConstraintDefinition constraint) {
            List<ConstraintDefinition> newConstraints = new java.util.ArrayList<>(this.constraints);
            newConstraints.add(constraint);
            this.constraints = List.copyOf(newConstraints);
            return this;
        }

        public TableDefinitionBuilder unique(List<String> columns) {
            UniqueConstraintDefinition unique = new UniqueConstraintDefinition(columns.toArray(new String[0]));
            return constraint(unique);
        }

        public TableDefinitionBuilder indexes(List<IndexDefinition> indexes) {
            this.indexes = indexes != null ? List.copyOf(indexes) : List.of();
            return this;
        }

        public TableDefinitionBuilder index(IndexDefinition index) {
            List<IndexDefinition> newIndexes = new java.util.ArrayList<>(this.indexes);
            newIndexes.add(index);
            this.indexes = List.copyOf(newIndexes);
            return this;
        }

        public TableDefinition build() {
            return new TableDefinition(table, columns, primaryKey, constraints, indexes);
        }

        public TableDefinitionBuilder name(String value) {
            return table(new TableIdentifier(value));
        }
    }
}
