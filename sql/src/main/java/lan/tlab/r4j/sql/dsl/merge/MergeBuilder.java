package lan.tlab.r4j.sql.dsl.merge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.identifier.Alias;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.statement.dml.MergeStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertValues;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenMatchedDelete;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeUsing;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public class MergeBuilder {
    private final DialectRenderer renderer;
    private TableExpression targetTable;
    private Alias targetAlias = Alias.nullObject();
    private MergeUsing using;
    private Predicate onCondition;
    private final List<MergeAction> actions = new ArrayList<>();

    public MergeBuilder(DialectRenderer renderer, String targetTableName) {
        if (targetTableName == null || targetTableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Target table name cannot be null or empty");
        }
        this.renderer = renderer;
        this.targetTable = new TableIdentifier(targetTableName);
    }

    public MergeBuilder as(String alias) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        this.targetAlias = new Alias(alias);
        return this;
    }

    public MergeBuilder using(String sourceTableName) {
        if (sourceTableName == null || sourceTableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Source table name cannot be null or empty");
        }
        this.using = new MergeUsing(new TableIdentifier(sourceTableName));
        return this;
    }

    public MergeBuilder using(String sourceTableName, String alias) {
        if (sourceTableName == null || sourceTableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Source table name cannot be null or empty");
        }
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        this.using = new MergeUsing(new TableIdentifier(sourceTableName), new Alias(alias));
        return this;
    }

    public MergeBuilder using(SelectStatement subquery, String alias) {
        if (subquery == null) {
            throw new IllegalArgumentException("Subquery cannot be null");
        }
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        this.using = new MergeUsing(subquery, new Alias(alias));
        return this;
    }

    public MergeBuilder on(String leftColumn, String rightColumn) {
        if (leftColumn == null || leftColumn.trim().isEmpty()) {
            throw new IllegalArgumentException("Left column cannot be null or empty");
        }
        if (rightColumn == null || rightColumn.trim().isEmpty()) {
            throw new IllegalArgumentException("Right column cannot be null or empty");
        }

        ColumnReference left = parseColumnReference(leftColumn);
        ColumnReference right = parseColumnReference(rightColumn);
        this.onCondition = Comparison.eq(left, right);
        return this;
    }

    public MergeBuilder on(Predicate condition) {
        if (condition == null) {
            throw new IllegalArgumentException("Condition cannot be null");
        }
        this.onCondition = condition;
        return this;
    }

    public MergeBuilder whenMatchedThenUpdate(List<UpdateItem> updateItems) {
        if (updateItems == null || updateItems.isEmpty()) {
            throw new IllegalArgumentException("Update items cannot be null or empty");
        }
        this.actions.add(new WhenMatchedUpdate(updateItems));
        return this;
    }

    public MergeBuilder whenMatchedThenUpdate(Predicate condition, List<UpdateItem> updateItems) {
        if (updateItems == null || updateItems.isEmpty()) {
            throw new IllegalArgumentException("Update items cannot be null or empty");
        }
        this.actions.add(new WhenMatchedUpdate(condition, updateItems));
        return this;
    }

    public MergeBuilder whenMatchedThenDelete() {
        this.actions.add(new WhenMatchedDelete(null));
        return this;
    }

    public MergeBuilder whenMatchedThenDelete(Predicate condition) {
        this.actions.add(new WhenMatchedDelete(condition));
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
        this.actions.add(new WhenNotMatchedInsert(columns, insertData));
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
        this.actions.add(new WhenNotMatchedInsert(condition, columns, insertData));
        return this;
    }

    public String build() {
        validateState();
        MergeStatement statement = getCurrentStatement();
        return renderer.renderSql(statement);
    }

    public PreparedStatement buildPreparedStatement(Connection connection) throws SQLException {
        validateState();
        MergeStatement statement = getCurrentStatement();
        PsDto result = renderer.renderPreparedStatement(statement);

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
                .targetAlias(targetAlias)
                .using(using)
                .onCondition(onCondition)
                .actions(actions)
                .build();
    }

    private ColumnReference parseColumnReference(String column) {
        if (column.contains(".")) {
            String[] parts = column.split("\\.", 2);
            return ColumnReference.of(parts[0], parts[1]);
        }
        return ColumnReference.of("", column);
    }
}
