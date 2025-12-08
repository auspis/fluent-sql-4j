package lan.tlab.r4j.jdsql.dsl.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.common.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.common.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.dml.statement.UpdateStatement;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.dsl.clause.LogicalCombinator;
import lan.tlab.r4j.jdsql.dsl.clause.SupportsWhere;
import lan.tlab.r4j.jdsql.dsl.util.LiteralUtil;

public class UpdateBuilder implements SupportsWhere<UpdateBuilder> {
    private UpdateStatement.UpdateStatementBuilder statementBuilder = UpdateStatement.builder();
    private final PreparedStatementSpecFactory specFactory;
    private TableIdentifier table;
    private final List<UpdateItem> setItems = new ArrayList<>();

    public UpdateBuilder(PreparedStatementSpecFactory specFactory, String tableName) {
        this.specFactory = specFactory;
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

    /**
     * Start building a WHERE clause with support for both regular columns and JSON functions.
     *
     * @return a WHERE builder
     */
    public lan.tlab.r4j.jdsql.dsl.clause.WhereBuilder<UpdateBuilder> where() {
        return new lan.tlab.r4j.jdsql.dsl.clause.WhereBuilder<>(this, LogicalCombinator.AND);
    }

    /**
     * Continue building WHERE clause with AND combinator.
     *
     * @return a WHERE builder with AND combinator
     */
    public lan.tlab.r4j.jdsql.dsl.clause.WhereBuilder<UpdateBuilder> and() {
        return new lan.tlab.r4j.jdsql.dsl.clause.WhereBuilder<>(this, LogicalCombinator.AND);
    }

    /**
     * Continue building WHERE clause with OR combinator.
     *
     * @return a WHERE builder with OR combinator
     */
    public lan.tlab.r4j.jdsql.dsl.clause.WhereBuilder<UpdateBuilder> or() {
        return new lan.tlab.r4j.jdsql.dsl.clause.WhereBuilder<>(this, LogicalCombinator.OR);
    }

    @Override
    public String getTableReference() {
        return table != null ? table.getTableReference() : "";
    }

    @Override
    public UpdateBuilder updateWhere(Function<Where, Where> updater) {
        Where currentWhere = getCurrentStatement().where();
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
        return !(where.condition() instanceof NullPredicate);
    }

    static Where combineWithExisting(Where where, Predicate newCondition, LogicalCombinator combinator) {
        Predicate existingCondition = where.condition();
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

    public PreparedStatement buildPreparedStatement(Connection connection) throws SQLException {
        validateState();
        UpdateStatement statement = getCurrentStatement();
        PreparedStatementSpec result = specFactory.create(statement);

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
