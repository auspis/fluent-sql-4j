package lan.tlab.r4j.sql.dsl.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.statement.dml.UpdateStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.dsl.LogicalCombinator;
import lan.tlab.r4j.sql.dsl.SupportsWhere;
import lan.tlab.r4j.sql.dsl.WhereConditionBuilder;
import lan.tlab.r4j.sql.dsl.util.LiteralUtil;

public class UpdateBuilder implements SupportsWhere<UpdateBuilder> {
    private UpdateStatement.UpdateStatementBuilder statementBuilder = UpdateStatement.builder();
    private final SqlRenderer sqlRenderer;
    private TableIdentifier table;
    private final List<UpdateItem> setItems = new ArrayList<>();

    public UpdateBuilder(SqlRenderer sqlRenderer, String tableName) {
        this.sqlRenderer = sqlRenderer;
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }
        this.table = new TableIdentifier(tableName);
        statementBuilder = statementBuilder.table(table);
    }

    public UpdateBuilder set(String columnName, String value) {
        return addSetItem(columnName, value);
    }

    public UpdateBuilder set(String columnName, Number value) {
        return addSetItem(columnName, value);
    }

    public UpdateBuilder set(String columnName, Boolean value) {
        return addSetItem(columnName, value);
    }

    public UpdateBuilder set(String columnName, LocalDate value) {
        return addSetItem(columnName, value);
    }

    public UpdateBuilder set(String columnName, LocalDateTime value) {
        return addSetItem(columnName, value);
    }

    public UpdateBuilder set(String columnName, ScalarExpression expression) {
        if (columnName == null || columnName.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }
        setItems.add(UpdateItem.of(columnName, expression));
        return this;
    }

    private UpdateBuilder addSetItem(String columnName, Object value) {
        if (columnName == null || columnName.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        ScalarExpression literal = value == null ? Literal.ofNull() : LiteralUtil.createLiteral(value);
        setItems.add(UpdateItem.of(columnName, literal));
        return this;
    }

    public WhereConditionBuilder<UpdateBuilder> where(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        return new WhereConditionBuilder<>(this, column, LogicalCombinator.AND);
    }

    public WhereConditionBuilder<UpdateBuilder> and(String column) {
        return new WhereConditionBuilder<>(this, column, LogicalCombinator.AND);
    }

    public WhereConditionBuilder<UpdateBuilder> or(String column) {
        return new WhereConditionBuilder<>(this, column, LogicalCombinator.OR);
    }

    @Override
    public String getTableReference() {
        return table != null ? table.getTableReference() : "";
    }

    @Override
    public UpdateBuilder updateWhere(Function<Where, Where> updater) {
        Where currentWhere = getCurrentStatement().getWhere();
        Where newWhere = updater.apply(currentWhere);
        statementBuilder = statementBuilder.where(newWhere);
        return this;
    }

    static Where combineConditions(Where currentWhere, Predicate newCondition, LogicalCombinator combinator) {
        return Optional.ofNullable(currentWhere)
                .filter(UpdateBuilder::hasValidCondition)
                .map(where -> combineWithExisting(where, newCondition, combinator))
                .orElse(Where.of(newCondition));
    }

    static boolean hasValidCondition(Where where) {
        return !(where.getCondition() instanceof NullPredicate);
    }

    static Where combineWithExisting(Where where, Predicate newCondition, LogicalCombinator combinator) {
        Predicate existingCondition = where.getCondition();
        Predicate combinedCondition = combinator.combine(existingCondition, newCondition);
        return Where.of(combinedCondition);
    }

    @Override
    public UpdateBuilder addWhereCondition(Predicate condition, LogicalCombinator combinator) {
        return updateWhere(where -> UpdateBuilder.combineConditions(where, condition, combinator));
    }

    private UpdateStatement getCurrentStatement() {
        return statementBuilder.set(setItems).build();
    }

    public String build() {
        validateState();
        UpdateStatement updateStatement = getCurrentStatement();
        return updateStatement.accept(sqlRenderer, new AstContext());
    }

    public PreparedStatement buildPreparedStatement(Connection connection) throws SQLException {
        validateState();
        UpdateStatement stmt = getCurrentStatement();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = stmt.accept(visitor, new AstContext());

        PreparedStatement ps = connection.prepareStatement(result.sql());
        for (int i = 0; i < result.parameters().size(); i++) {
            ps.setObject(i + 1, result.parameters().get(i));
        }
        return ps;
    }

    private void validateState() {
        if (table == null) {
            throw new IllegalStateException("Table must be specified");
        }
        if (setItems.isEmpty()) {
            throw new IllegalStateException("At least one SET clause must be specified");
        }
    }
}
