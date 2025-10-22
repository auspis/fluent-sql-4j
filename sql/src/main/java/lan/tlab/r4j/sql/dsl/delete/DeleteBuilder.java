package lan.tlab.r4j.sql.dsl.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.statement.dml.DeleteStatement;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.dsl.LogicalCombinator;
import lan.tlab.r4j.sql.dsl.SupportsWhere;
import lan.tlab.r4j.sql.dsl.WhereConditionBuilder;

public class DeleteBuilder implements SupportsWhere<DeleteBuilder> {
    private DeleteStatement.DeleteStatementBuilder statementBuilder = DeleteStatement.builder();
    private final DialectRenderer renderer;
    private TableIdentifier table;

    public DeleteBuilder(DialectRenderer renderer, String tableName) {
        this.renderer = renderer;
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }
        this.table = new TableIdentifier(tableName);
        statementBuilder = statementBuilder.table(table);
    }

    public WhereConditionBuilder<DeleteBuilder> where(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        return new WhereConditionBuilder<>(this, column, LogicalCombinator.AND);
    }

    public WhereConditionBuilder<DeleteBuilder> and(String column) {
        return new WhereConditionBuilder<>(this, column, LogicalCombinator.AND);
    }

    public WhereConditionBuilder<DeleteBuilder> or(String column) {
        return new WhereConditionBuilder<>(this, column, LogicalCombinator.OR);
    }

    @Override
    public String getTableReference() {
        return table != null ? table.getTableReference() : "";
    }

    @Override
    public DeleteBuilder updateWhere(Function<Where, Where> updater) {
        Where currentWhere = getCurrentStatement().getWhere();
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
        return !(where.getCondition() instanceof NullPredicate);
    }

    static Where combineWithExisting(Where where, Predicate newCondition, LogicalCombinator combinator) {
        Predicate existingCondition = where.getCondition();
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

    public String build() {
        DeleteStatement deleteStatement = getCurrentStatement();
        return renderer.renderSql(deleteStatement);
    }

    public PreparedStatement buildPreparedStatement(Connection connection) throws SQLException {
        DeleteStatement statement = getCurrentStatement();
        PsDto result = renderer.renderPreparedStatement(statement);

        PreparedStatement ps = connection.prepareStatement(result.sql());
        for (int i = 0; i < result.parameters().size(); i++) {
            ps.setObject(i + 1, result.parameters().get(i));
        }
        return ps;
    }
}
