package lan.tlab.r4j.jdsql.dsl.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import lan.tlab.r4j.jdsql.ast.core.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.core.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.core.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.dml.statement.DeleteStatement;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.dsl.clause.LogicalCombinator;
import lan.tlab.r4j.jdsql.dsl.clause.SupportsWhere;

public class DeleteBuilder implements SupportsWhere<DeleteBuilder> {
    private DeleteStatement.DeleteStatementBuilder statementBuilder = DeleteStatement.builder();
    private final PreparedStatementSpecFactory specFactory;
    private TableIdentifier table;

    public DeleteBuilder(PreparedStatementSpecFactory specFactory, String tableName) {
        this.specFactory = specFactory;
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }
        this.table = new TableIdentifier(tableName);
        statementBuilder = statementBuilder.table(table);
    }

    /**
     * Start building a WHERE clause with support for both regular columns and JSON functions.
     *
     * @return a WHERE builder
     */
    public lan.tlab.r4j.jdsql.dsl.clause.WhereBuilder<DeleteBuilder> where() {
        return new lan.tlab.r4j.jdsql.dsl.clause.WhereBuilder<>(this, LogicalCombinator.AND);
    }

    /**
     * Continue building WHERE clause with AND combinator.
     *
     * @return a WHERE builder with AND combinator
     */
    public lan.tlab.r4j.jdsql.dsl.clause.WhereBuilder<DeleteBuilder> and() {
        return new lan.tlab.r4j.jdsql.dsl.clause.WhereBuilder<>(this, LogicalCombinator.AND);
    }

    /**
     * Continue building WHERE clause with OR combinator.
     *
     * @return a WHERE builder with OR combinator
     */
    public lan.tlab.r4j.jdsql.dsl.clause.WhereBuilder<DeleteBuilder> or() {
        return new lan.tlab.r4j.jdsql.dsl.clause.WhereBuilder<>(this, LogicalCombinator.OR);
    }

    @Override
    public String getTableReference() {
        return table != null ? table.getTableReference() : "";
    }

    @Override
    public DeleteBuilder updateWhere(Function<Where, Where> updater) {
        Where currentWhere = getCurrentStatement().where();
        Where newWhere = updater.apply(currentWhere);
        statementBuilder = statementBuilder.where(newWhere);
        return this;
    }

    static Where combineConditions(Where currentWhere, Predicate newCondition, LogicalCombinator combinator) {
        return Optional.ofNullable(currentWhere)
                .filter(DeleteBuilder::hasValidCondition)
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
    public DeleteBuilder addWhereCondition(Predicate condition, LogicalCombinator combinator) {
        return updateWhere(where -> DeleteBuilder.combineConditions(where, condition, combinator));
    }

    private DeleteStatement getCurrentStatement() {
        return statementBuilder.build();
    }

    public PreparedStatement build(Connection connection) throws SQLException {
        DeleteStatement statement = getCurrentStatement();
        PreparedStatementSpec result = specFactory.create(statement);

        PreparedStatement ps = connection.prepareStatement(result.sql());
        for (int i = 0; i < result.parameters().size(); i++) {
            ps.setObject(i + 1, result.parameters().get(i));
        }
        return ps;
    }
}
