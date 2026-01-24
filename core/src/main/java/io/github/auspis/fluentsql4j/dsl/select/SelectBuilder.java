package io.github.auspis.fluentsql4j.dsl.select;

import io.github.auspis.fluentsql4j.ast.core.expression.aggregate.AggregateCall;
import io.github.auspis.fluentsql4j.ast.core.expression.aggregate.AggregateCallImpl;
import io.github.auspis.fluentsql4j.ast.core.expression.aggregate.CountDistinct;
import io.github.auspis.fluentsql4j.ast.core.expression.aggregate.CountStar;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.auspis.fluentsql4j.ast.dql.clause.Fetch;
import io.github.auspis.fluentsql4j.ast.dql.clause.From;
import io.github.auspis.fluentsql4j.ast.dql.clause.GroupBy;
import io.github.auspis.fluentsql4j.ast.dql.clause.Having;
import io.github.auspis.fluentsql4j.ast.dql.clause.OrderBy;
import io.github.auspis.fluentsql4j.ast.dql.clause.Select;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.dql.projection.AggregateCallProjection;
import io.github.auspis.fluentsql4j.ast.dql.projection.Projection;
import io.github.auspis.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.auspis.fluentsql4j.ast.dql.source.FromSource;
import io.github.auspis.fluentsql4j.ast.dql.source.FromSubquery;
import io.github.auspis.fluentsql4j.ast.dql.source.join.OnJoin;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.dsl.clause.HavingBuilder;
import io.github.auspis.fluentsql4j.dsl.clause.LogicalCombinator;
import io.github.auspis.fluentsql4j.dsl.clause.SupportsWhere;
import io.github.auspis.fluentsql4j.dsl.util.ColumnReferenceUtil;
import io.github.auspis.fluentsql4j.dsl.util.PsUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SelectBuilder implements SupportsWhere<SelectBuilder> {
    private SelectStatement.SelectStatementBuilder statementBuilder = SelectStatement.builder();
    private final PreparedStatementSpecFactory specFactory;
    private FromSource currentFromSource;
    private TableIdentifier baseTable;

    public SelectBuilder(PreparedStatementSpecFactory specFactory, String... columns) {
        this.specFactory = specFactory;
        if (columns != null && columns.length > 0) {
            statementBuilder = statementBuilder.select(Select.of(java.util.Arrays.stream(columns)
                    .map(column -> new ScalarExpressionProjection(ColumnReference.of("", column)))
                    .toArray(ScalarExpressionProjection[]::new)));
        }
    }

    public SelectBuilder(PreparedStatementSpecFactory specFactory, Select select) {
        this.specFactory = specFactory;
        if (select != null) {
            statementBuilder = statementBuilder.select(select);
        }
    }

    public SelectBuilder from(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("TableIdentifier name cannot be null or empty");
        }

        TableIdentifier table = new TableIdentifier(tableName);
        baseTable = table;
        currentFromSource = table;
        statementBuilder = statementBuilder.from(From.of(table));

        updateSelectClauseWithTable(table);
        return this;
    }

    public SelectBuilder from(SelectBuilder subquery, String alias) {
        if (subquery == null) {
            throw new IllegalArgumentException("Subquery cannot be null");
        }
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty for subquery");
        }

        FromSubquery fromSubquery = FromSubquery.of(subquery.getCurrentStatement(), alias);
        currentFromSource = fromSubquery;

        // Create a dummy TableIdentifier with the alias as the table reference
        // This allows WHERE clauses to properly reference columns from the subquery
        baseTable = new TableIdentifier(alias);

        statementBuilder = statementBuilder.from(From.of(fromSubquery));
        return this;
    }

    public SelectBuilder as(String alias) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }

        From currentFrom = getCurrentStatement().getFrom();
        if (currentFrom == null || currentFrom.sources().isEmpty()) {
            throw new IllegalStateException("Cannot set alias before specifying table with from()");
        }

        TableIdentifier currentTable = (TableIdentifier) currentFrom.sources().get(0);
        TableIdentifier tableWithAlias = new TableIdentifier(currentTable.name(), alias);

        baseTable = tableWithAlias;
        currentFromSource = tableWithAlias;
        statementBuilder = statementBuilder.from(From.of(tableWithAlias));
        updateSelectClauseWithTable(tableWithAlias);
        return this;
    }

    public JoinSpecBuilder innerJoin(String tableName) {
        validateFromExists();
        return new JoinSpecBuilder(this, currentFromSource, OnJoin.JoinType.INNER, tableName);
    }

    public JoinSpecBuilder leftJoin(String tableName) {
        validateFromExists();
        return new JoinSpecBuilder(this, currentFromSource, OnJoin.JoinType.LEFT, tableName);
    }

    public JoinSpecBuilder rightJoin(String tableName) {
        validateFromExists();
        return new JoinSpecBuilder(this, currentFromSource, OnJoin.JoinType.RIGHT, tableName);
    }

    public JoinSpecBuilder fullJoin(String tableName) {
        validateFromExists();
        return new JoinSpecBuilder(this, currentFromSource, OnJoin.JoinType.FULL, tableName);
    }

    public SelectBuilder crossJoin(String tableName) {
        validateFromExists();
        TableIdentifier rightTable = new TableIdentifier(tableName);
        OnJoin join = new OnJoin(currentFromSource, OnJoin.JoinType.CROSS, rightTable, null);
        return addJoin(join);
    }

    SelectBuilder addJoin(OnJoin join) {
        currentFromSource = join;
        statementBuilder = statementBuilder.from(From.of(join));
        return this;
    }

    private void validateFromExists() {
        if (currentFromSource == null) {
            throw new IllegalStateException("FROM table must be specified before adding a join");
        }
    }

    public SelectStatement getCurrentStatement() {
        return statementBuilder.build();
    }

    @Override
    public String getTableReference() {
        return baseTable != null ? baseTable.getTableReference() : "";
    }

    private void updateSelectClauseWithTable(TableIdentifier table) {
        Select currentSelect = getCurrentStatement().getSelect();
        if (currentSelect == null || currentSelect.projections().isEmpty()) {
            return;
        }

        List<Projection> updatedProjections = new ArrayList<>();
        for (var projection : currentSelect.projections()) {
            Projection updated =
                    switch (projection) {
                        case ScalarExpressionProjection scalarProj -> updateScalarProjection(scalarProj, table);
                        case AggregateCallProjection aggProj -> updateAggregateProjection(aggProj, table);
                        default -> projection;
                    };
            updatedProjections.add(updated);
        }

        if (!updatedProjections.isEmpty()) {
            statementBuilder = statementBuilder.select(Select.of(updatedProjections.toArray(new Projection[0])));
        }
    }

    private Projection updateScalarProjection(ScalarExpressionProjection scalarProj, TableIdentifier table) {
        if (!(scalarProj.expression() instanceof ColumnReference colRef)) {
            return scalarProj;
        }

        if (ColumnReferenceUtil.isWildcard(colRef) || !ColumnReferenceUtil.shouldRetarget(colRef, table.name())) {
            return scalarProj;
        }

        ColumnReference updatedCol =
                ColumnReferenceUtil.retargetIfApplicable(colRef, table.name(), table.getTableReference());

        // Preserve the alias if present
        if (scalarProj.as() != null && !scalarProj.as().name().isEmpty()) {
            return new ScalarExpressionProjection(updatedCol, scalarProj.as());
        }
        return new ScalarExpressionProjection(updatedCol);
    }

    private Projection updateAggregateProjection(AggregateCallProjection aggProj, TableIdentifier table) {
        AggregateCall aggCall = (AggregateCall) aggProj.expression();
        AggregateCall updatedAggCall = updateAggregateCallWithTable(aggCall, table);

        // Preserve the alias if present
        if (aggProj.as() != null && !aggProj.as().name().isEmpty()) {
            return new AggregateCallProjection(updatedAggCall, aggProj.as());
        }
        return new AggregateCallProjection(updatedAggCall);
    }

    private AggregateCall updateAggregateCallWithTable(AggregateCall aggCall, TableIdentifier table) {
        return switch (aggCall) {
            case AggregateCallImpl impl -> {
                if (impl.expression() instanceof ColumnReference colRef) {
                    ColumnReference targetCol =
                            ColumnReferenceUtil.retargetIfApplicable(colRef, table.name(), table.getTableReference());
                    yield switch (impl.operator()) {
                        case MAX -> AggregateCall.max(targetCol);
                        case MIN -> AggregateCall.min(targetCol);
                        case AVG -> AggregateCall.avg(targetCol);
                        case SUM -> AggregateCall.sum(targetCol);
                        case COUNT -> AggregateCall.count(targetCol);
                    };
                }
                yield impl;
            }
            case CountDistinct cd -> {
                if (cd.expression() instanceof ColumnReference colRef) {
                    ColumnReference targetCol =
                            ColumnReferenceUtil.retargetIfApplicable(colRef, table.name(), table.getTableReference());
                    yield AggregateCall.countDistinct(targetCol);
                }
                yield cd;
            }
            case CountStar cs -> cs; // CountStar doesn't need table reference
            default -> aggCall;
        };
    }

    private SelectBuilder updateFetch(Function<Fetch, Fetch> updater) {
        Fetch currentFetch = getCurrentStatement().getFetch();
        Fetch newFetch = updater.apply(currentFetch);
        statementBuilder = statementBuilder.fetch(newFetch);
        return this;
    }

    /**
     * Start building a WHERE clause with support for both regular columns and JSON functions.
     *
     * @return a WHERE builder
     */
    public io.github.auspis.fluentsql4j.dsl.clause.WhereBuilder<SelectBuilder> where() {
        return new io.github.auspis.fluentsql4j.dsl.clause.WhereBuilder<>(
                this, io.github.auspis.fluentsql4j.dsl.clause.LogicalCombinator.AND);
    }

    /**
     * Start a GROUP BY clause with explicit column references supporting aliases.
     * Use this method for multi-table queries where you need to group by columns from different tables.
     * <pre>
     * .groupBy()
     *     .column("country")
     *     .column("o", "year")
     *     .having()...
     * </pre>
     *
     * @return a GroupByBuilder to specify columns with optional aliases
     */
    public GroupByBuilder groupBy() {
        return new GroupByBuilder(this);
    }

    SelectBuilder updateGroupBy(GroupBy groupBy) {
        statementBuilder = statementBuilder.groupBy(groupBy);
        return this;
    }

    /**
     * Create a fluent ORDER BY builder for defining query ordering.
     *
     * <p>Use this method to define the ordering of query results with a fluent API that supports
     * both ascending and descending order for columns from single-table or multi-table contexts.
     *
     * <p>Example usage:
     * <pre>{@code
     * dsl.select("name", "age")
     *     .from("users")
     *     .orderBy()
     *         .asc("name")
     *         .desc("age")
     *     .build(connection);
     * }</pre>
     *
     * @return a new OrderByBuilder for defining ORDER BY clauses
     */
    public OrderByBuilder orderBy() {
        return new OrderByBuilder(this);
    }

    SelectBuilder updateOrderBy(OrderBy orderBy) {
        statementBuilder = statementBuilder.orderBy(orderBy);
        return this;
    }

    public SelectBuilder fetch(int rows) {
        if (rows <= 0) {
            throw new IllegalArgumentException("Fetch rows must be positive, got: " + rows);
        }

        return updateFetch(fetch -> {
            int currentOffset = fetch != null ? fetch.offset() : 0;
            return new Fetch(currentOffset, rows);
        });
    }

    public SelectBuilder offset(int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative, got: " + offset);
        }

        return updateFetch(fetch -> {
            Integer currentRows = fetch != null ? fetch.rows() : null;
            return new Fetch(offset, currentRows);
        });
    }

    /**
     * Continue building WHERE clause with AND combinator.
     *
     * @return a WHERE builder with AND combinator
     */
    public io.github.auspis.fluentsql4j.dsl.clause.WhereBuilder<SelectBuilder> and() {
        return new io.github.auspis.fluentsql4j.dsl.clause.WhereBuilder<>(
                this, io.github.auspis.fluentsql4j.dsl.clause.LogicalCombinator.AND);
    }

    /**
     * Continue building WHERE clause with OR combinator.
     *
     * @return a WHERE builder with OR combinator
     */
    public io.github.auspis.fluentsql4j.dsl.clause.WhereBuilder<SelectBuilder> or() {
        return new io.github.auspis.fluentsql4j.dsl.clause.WhereBuilder<>(
                this, io.github.auspis.fluentsql4j.dsl.clause.LogicalCombinator.OR);
    }

    // Functional WHERE updater
    @Override
    public SelectBuilder updateWhere(Function<Where, Where> updater) {
        Where currentWhere = getCurrentStatement().getWhere();
        Where newWhere = updater.apply(currentWhere);
        statementBuilder = statementBuilder.where(newWhere);
        return this;
    }

    // Helper to combine conditions with functional approach
    static Where combineConditions(Where currentWhere, Predicate newCondition, LogicalCombinator combinator) {
        return Optional.ofNullable(currentWhere)
                .filter(SelectBuilder::hasValidCondition)
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
    public SelectBuilder addWhereCondition(Predicate condition, LogicalCombinator combinator) {
        return updateWhere(where -> SelectBuilder.combineConditions(where, condition, combinator));
    }

    // HAVING clause support
    public HavingBuilder having() {
        return new HavingBuilder(this, LogicalCombinator.AND);
    }

    public HavingBuilder andHaving() {
        return new HavingBuilder(this, LogicalCombinator.AND);
    }

    public HavingBuilder orHaving() {
        return new HavingBuilder(this, LogicalCombinator.OR);
    }

    // Functional HAVING updater
    public SelectBuilder updateHaving(Function<Having, Having> updater) {
        Having currentHaving = getCurrentStatement().getHaving();
        Having newHaving = updater.apply(currentHaving);
        statementBuilder = statementBuilder.having(newHaving);
        return this;
    }

    // Helper to combine HAVING conditions with functional approach
    static Having combineHavingConditions(Having currentHaving, Predicate newCondition, LogicalCombinator combinator) {
        return Optional.ofNullable(currentHaving)
                .filter(SelectBuilder::hasValidHavingCondition)
                .map(having -> combineHavingWithExisting(having, newCondition, combinator))
                .orElse(Having.of(newCondition));
    }

    static boolean hasValidHavingCondition(Having having) {
        return !(having.condition() instanceof NullPredicate);
    }

    static Having combineHavingWithExisting(Having having, Predicate newCondition, LogicalCombinator combinator) {
        Predicate existingCondition = having.condition();
        Predicate combinedCondition = combinator.combine(existingCondition, newCondition);
        return Having.of(combinedCondition);
    }

    public SelectBuilder addHavingCondition(Predicate condition, LogicalCombinator combinator) {
        return updateHaving(having -> SelectBuilder.combineHavingConditions(having, condition, combinator));
    }

    public PreparedStatement build(Connection connection) throws SQLException {
        validateState();
        SelectStatement statement = getCurrentStatement();
        PreparedStatementSpec spec = specFactory.create(statement);
        return PsUtil.preparedStatement(spec, connection);
    }

    private void validateState() {
        From from = getCurrentStatement().getFrom();
        if (from == null || from.sources().isEmpty()) {
            throw new IllegalStateException("FROM table must be specified");
        }
    }
}
