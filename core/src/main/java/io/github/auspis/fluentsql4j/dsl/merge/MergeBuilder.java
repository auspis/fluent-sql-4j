package io.github.auspis.fluentsql4j.dsl.merge;

import io.github.auspis.fluentsql4j.ast.core.expression.Expression;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.set.AliasedTableExpression;
import io.github.auspis.fluentsql4j.ast.core.identifier.Alias;
import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.InsertValues;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedDelete;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedUpdate;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeUsing;
import io.github.auspis.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.auspis.fluentsql4j.ast.dml.statement.MergeStatement;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MergeBuilder {
    private final PreparedStatementSpecFactory specFactory;
    private TableIdentifier targetTable;
    private MergeUsing using;
    private Predicate onCondition;
    private final List<MergeAction> actions = new ArrayList<>();

    public MergeBuilder(PreparedStatementSpecFactory specFactory, String targetTableName) {
        if (targetTableName == null || targetTableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Target table name cannot be null or empty");
        }
        this.specFactory = specFactory;
        targetTable = new TableIdentifier(targetTableName);
    }

    public MergeBuilder as(String alias) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        targetTable = new TableIdentifier(targetTable.name(), alias);
        return this;
    }

    public MergeBuilder using(String sourceTableName) {
        if (sourceTableName == null || sourceTableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Source table name cannot be null or empty");
        }
        using = new MergeUsing(new TableIdentifier(sourceTableName));
        return this;
    }

    public MergeBuilder using(String sourceTableName, String alias) {
        if (sourceTableName == null || sourceTableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Source table name cannot be null or empty");
        }
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        using = new MergeUsing(new TableIdentifier(sourceTableName, alias));
        return this;
    }

    public MergeBuilder using(SelectStatement subquery, String alias) {
        if (subquery == null) {
            throw new IllegalArgumentException("Subquery cannot be null");
        }
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        using = new MergeUsing(new AliasedTableExpression(subquery, new Alias(alias)));
        return this;
    }

    public MergeBuilder on(
            String leftTableReference, String leftColumn, String rightTableReference, String rightColumn) {
        if (leftTableReference == null || leftTableReference.trim().isEmpty()) {
            throw new IllegalArgumentException("Left table reference cannot be null or empty");
        }
        if (leftTableReference.contains(".")) {
            throw new IllegalArgumentException(
                    "Left table reference must not contain dot: '" + leftTableReference + "'");
        }
        if (leftColumn == null || leftColumn.trim().isEmpty()) {
            throw new IllegalArgumentException("Left column cannot be null or empty");
        }
        if (leftColumn.contains(".")) {
            throw new IllegalArgumentException(
                    "Left column must not contain dot. Use on(table, column, table, column) with separate parameters");
        }
        if (rightTableReference == null || rightTableReference.trim().isEmpty()) {
            throw new IllegalArgumentException("Right table reference cannot be null or empty");
        }
        if (rightTableReference.contains(".")) {
            throw new IllegalArgumentException(
                    "Right table reference must not contain dot: '" + rightTableReference + "'");
        }
        if (rightColumn == null || rightColumn.trim().isEmpty()) {
            throw new IllegalArgumentException("Right column cannot be null or empty");
        }
        if (rightColumn.contains(".")) {
            throw new IllegalArgumentException(
                    "Right column must not contain dot. Use on(table, column, table, column) with separate parameters");
        }

        ColumnReference targetColRef = ColumnReference.of(leftTableReference, leftColumn);
        ColumnReference sourceColRef = ColumnReference.of(rightTableReference, rightColumn);
        onCondition = Comparison.eq(targetColRef, sourceColRef);
        return this;
    }

    public MergeBuilder on(Predicate condition) {
        if (condition == null) {
            throw new IllegalArgumentException("Condition cannot be null");
        }
        onCondition = condition;
        return this;
    }

    public MergeBuilder whenMatchedThenUpdate(List<UpdateItem> updateItems) {
        if (updateItems == null || updateItems.isEmpty()) {
            throw new IllegalArgumentException("Update items cannot be null or empty");
        }
        actions.add(new WhenMatchedUpdate(updateItems));
        return this;
    }

    public MergeBuilder whenMatchedThenUpdate(Predicate condition, List<UpdateItem> updateItems) {
        if (updateItems == null || updateItems.isEmpty()) {
            throw new IllegalArgumentException("Update items cannot be null or empty");
        }
        actions.add(new WhenMatchedUpdate(condition, updateItems));
        return this;
    }

    public WhenMatchedUpdateBuilder whenMatched() {
        return new WhenMatchedUpdateBuilder(this, null);
    }

    public WhenMatchedUpdateBuilder whenMatched(Predicate condition) {
        if (condition == null) {
            throw new IllegalArgumentException("Condition cannot be null");
        }
        return new WhenMatchedUpdateBuilder(this, condition);
    }

    public MergeBuilder whenMatchedThenDelete(Predicate condition) {
        actions.add(new WhenMatchedDelete(condition));
        return this;
    }

    public MergeBuilder whenNotMatchedThenInsert(List<ColumnReference> columns, List<Expression> values) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("Columns cannot be null or empty");
        }
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values cannot be null or empty");
        }
        if (columns.size() != values.size()) {
            throw new IllegalArgumentException("Number of columns must match number of values");
        }
        InsertData insertData = new InsertValues(values);
        actions.add(new WhenNotMatchedInsert(columns, insertData));
        return this;
    }

    public MergeBuilder whenNotMatchedThenInsert(
            Predicate condition, List<ColumnReference> columns, List<Expression> values) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("Columns cannot be null or empty");
        }
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values cannot be null or empty");
        }
        if (columns.size() != values.size()) {
            throw new IllegalArgumentException("Number of columns must match number of values");
        }
        InsertData insertData = new InsertValues(values);
        actions.add(new WhenNotMatchedInsert(condition, columns, insertData));
        return this;
    }

    public WhenNotMatchedInsertBuilder whenNotMatched() {
        return new WhenNotMatchedInsertBuilder(this, null);
    }

    public WhenNotMatchedInsertBuilder whenNotMatched(Predicate condition) {
        if (condition == null) {
            throw new IllegalArgumentException("Condition cannot be null");
        }
        return new WhenNotMatchedInsertBuilder(this, condition);
    }

    public PreparedStatement build(Connection connection) throws SQLException {
        validateState();
        MergeStatement statement = getCurrentStatement();
        PreparedStatementSpec result = specFactory.create(statement);

        PreparedStatement ps = connection.prepareStatement(result.sql());
        for (int i = 0; i < result.parameters().size(); i++) {
            ps.setObject(i + 1, result.parameters().get(i));
        }
        return ps;
    }

    private void validateState() {
        if (targetTable == null) {
            throw new IllegalStateException("Target table must be specified");
        }
        if (using == null) {
            throw new IllegalStateException("USING clause must be specified");
        }
        if (onCondition == null) {
            throw new IllegalStateException("ON condition must be specified");
        }
        if (actions.isEmpty()) {
            throw new IllegalStateException("At least one WHEN clause must be specified");
        }
    }

    private MergeStatement getCurrentStatement() {
        return MergeStatement.builder()
                .targetTable(targetTable)
                .using(using)
                .onCondition(onCondition)
                .actions(actions)
                .build();
    }

    public static class WhenMatchedUpdateBuilder {
        private final MergeBuilder parent;
        private final Predicate condition;
        private final List<UpdateItem> updateItems = new ArrayList<>();
        private boolean actionCommitted = false;

        WhenMatchedUpdateBuilder(MergeBuilder parent, Predicate condition) {
            this.parent = parent;
            this.condition = condition;
        }

        private ColumnReference validateAndCreateColumnReference(String column) {
            if (column == null || column.trim().isEmpty()) {
                throw new IllegalArgumentException("Column name cannot be null or empty");
            }
            if (column.contains(".")) {
                throw new IllegalArgumentException("Column name must not contain dot notation: '" + column + "'");
            }
            return ColumnReference.of("", column);
        }

        public WhenMatchedUpdateBuilder set(String column, String value) {
            ColumnReference colRef = validateAndCreateColumnReference(column);
            ScalarExpression expr = value == null ? Literal.ofNull() : Literal.of(value);
            updateItems.add(new UpdateItem(colRef, expr));
            return this;
        }

        public WhenMatchedUpdateBuilder set(String column, Number value) {
            ColumnReference colRef = validateAndCreateColumnReference(column);
            ScalarExpression expr = value == null ? Literal.ofNull() : Literal.of(value);
            updateItems.add(new UpdateItem(colRef, expr));
            return this;
        }

        public WhenMatchedUpdateBuilder set(String column, Boolean value) {
            ColumnReference colRef = validateAndCreateColumnReference(column);
            ScalarExpression expr = value == null ? Literal.ofNull() : Literal.of(value);
            updateItems.add(new UpdateItem(colRef, expr));
            return this;
        }

        public WhenMatchedUpdateBuilder set(String column, ColumnReference value) {
            ColumnReference colRef = validateAndCreateColumnReference(column);
            ScalarExpression expr = value == null ? Literal.ofNull() : value;
            updateItems.add(new UpdateItem(colRef, expr));
            return this;
        }

        public WhenMatchedUpdateBuilder set(String column, Expression value) {
            ColumnReference colRef = validateAndCreateColumnReference(column);
            if (value != null && !(value instanceof ScalarExpression)) {
                throw new IllegalArgumentException("Value must be a ScalarExpression");
            }
            ScalarExpression expr = value == null ? Literal.ofNull() : (ScalarExpression) value;
            updateItems.add(new UpdateItem(colRef, expr));
            return this;
        }

        private void commitAction() {
            if (actionCommitted) {
                return;
            }
            if (updateItems.isEmpty()) {
                throw new IllegalStateException(
                        "At least one SET clause must be specified for WHEN MATCHED THEN UPDATE");
            }
            if (condition == null) {
                parent.actions.add(new WhenMatchedUpdate(updateItems));
            } else {
                parent.actions.add(new WhenMatchedUpdate(condition, updateItems));
            }
            actionCommitted = true;
        }

        public WhenMatchedUpdateBuilder whenMatched() {
            commitAction();
            return parent.whenMatched();
        }

        public WhenMatchedUpdateBuilder whenMatched(Predicate condition) {
            commitAction();
            return parent.whenMatched(condition);
        }

        public WhenNotMatchedInsertBuilder whenNotMatched() {
            commitAction();
            return parent.whenNotMatched();
        }

        public WhenNotMatchedInsertBuilder whenNotMatched(Predicate condition) {
            commitAction();
            return parent.whenNotMatched(condition);
        }

        public WhenMatchedUpdateBuilder delete() {
            if (!updateItems.isEmpty()) {
                throw new IllegalStateException("Cannot use delete() with SET clauses");
            }
            if (actionCommitted) {
                throw new IllegalStateException("Action already committed");
            }
            parent.actions.add(new WhenMatchedDelete(condition));
            actionCommitted = true;
            return this;
        }

        public PreparedStatement build(Connection connection) throws SQLException {
            commitAction();
            return parent.build(connection);
        }
    }

    public static class WhenNotMatchedInsertBuilder {
        private final MergeBuilder parent;
        private final Predicate condition;
        private final List<ColumnReference> columns = new ArrayList<>();
        private final List<Expression> values = new ArrayList<>();
        private boolean actionCommitted = false;

        WhenNotMatchedInsertBuilder(MergeBuilder parent, Predicate condition) {
            this.parent = parent;
            this.condition = condition;
        }

        private ColumnReference validateAndCreateColumnReference(String column) {
            if (column == null || column.trim().isEmpty()) {
                throw new IllegalArgumentException("Column name cannot be null or empty");
            }
            if (column.contains(".")) {
                throw new IllegalArgumentException("Column name must not contain dot notation: '" + column + "'");
            }
            return ColumnReference.of("", column);
        }

        public WhenNotMatchedInsertBuilder set(String column, String value) {
            ColumnReference colRef = validateAndCreateColumnReference(column);
            Expression expr = value == null ? Literal.ofNull() : Literal.of(value);
            columns.add(colRef);
            values.add(expr);
            return this;
        }

        public WhenNotMatchedInsertBuilder set(String column, Number value) {
            ColumnReference colRef = validateAndCreateColumnReference(column);
            Expression expr = value == null ? Literal.ofNull() : Literal.of(value);
            columns.add(colRef);
            values.add(expr);
            return this;
        }

        public WhenNotMatchedInsertBuilder set(String column, Boolean value) {
            ColumnReference colRef = validateAndCreateColumnReference(column);
            Expression expr = value == null ? Literal.ofNull() : Literal.of(value);
            columns.add(colRef);
            values.add(expr);
            return this;
        }

        public WhenNotMatchedInsertBuilder set(String column, ColumnReference value) {
            ColumnReference colRef = validateAndCreateColumnReference(column);
            Expression expr = value == null ? Literal.ofNull() : value;
            columns.add(colRef);
            values.add(expr);
            return this;
        }

        public WhenNotMatchedInsertBuilder set(String column, Expression value) {
            ColumnReference colRef = validateAndCreateColumnReference(column);
            Expression expr = value == null ? Literal.ofNull() : value;
            columns.add(colRef);
            values.add(expr);
            return this;
        }

        private void commitAction() {
            if (actionCommitted) {
                return;
            }
            if (columns.isEmpty()) {
                throw new IllegalStateException(
                        "At least one column must be specified for WHEN NOT MATCHED THEN INSERT");
            }
            InsertData insertData = new InsertValues(values);
            if (condition == null) {
                parent.actions.add(new WhenNotMatchedInsert(columns, insertData));
            } else {
                parent.actions.add(new WhenNotMatchedInsert(condition, columns, insertData));
            }
            actionCommitted = true;
        }

        public WhenMatchedUpdateBuilder whenMatched() {
            commitAction();
            return parent.whenMatched();
        }

        public WhenMatchedUpdateBuilder whenMatched(Predicate condition) {
            commitAction();
            return parent.whenMatched(condition);
        }

        public WhenNotMatchedInsertBuilder whenNotMatched() {
            commitAction();
            return parent.whenNotMatched();
        }

        public WhenNotMatchedInsertBuilder whenNotMatched(Predicate condition) {
            commitAction();
            return parent.whenNotMatched(condition);
        }

        public PreparedStatement build(Connection connection) throws SQLException {
            commitAction();
            return parent.build(connection);
        }
    }
}
