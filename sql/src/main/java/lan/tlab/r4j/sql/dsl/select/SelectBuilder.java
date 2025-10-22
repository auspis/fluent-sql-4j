package lan.tlab.r4j.sql.dsl.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lan.tlab.r4j.sql.ast.clause.conditional.having.Having;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.from.source.FromSource;
import lan.tlab.r4j.sql.ast.clause.from.source.FromSubquery;
import lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.clause.groupby.GroupBy;
import lan.tlab.r4j.sql.ast.clause.orderby.OrderBy;
import lan.tlab.r4j.sql.ast.clause.orderby.Sorting;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.clause.selection.projection.Projection;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCallImpl;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.CountDistinct;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.CountStar;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.dsl.HavingConditionBuilder;
import lan.tlab.r4j.sql.dsl.LogicalCombinator;
import lan.tlab.r4j.sql.dsl.SupportsWhere;
import lan.tlab.r4j.sql.dsl.WhereConditionBuilder;
import lan.tlab.r4j.sql.dsl.util.ColumnReferenceUtil;

// TODO: Add support for SELECT AggregateCalls, subqueries, and other SQL features as needed.
public class SelectBuilder implements SupportsWhere<SelectBuilder> {
    private SelectStatement.SelectStatementBuilder statementBuilder = SelectStatement.builder();
    private final DialectRenderer renderer;
    private FromSource currentFromSource;
    private TableIdentifier baseTable;

    public SelectBuilder(DialectRenderer renderer, String... columns) {
        this.renderer = renderer;
        if (columns != null && columns.length > 0) {
            statementBuilder = statementBuilder.select(Select.of(java.util.Arrays.stream(columns)
                    .map(column -> new ScalarExpressionProjection(ColumnReference.of("", column)))
                    .toArray(ScalarExpressionProjection[]::new)));
        }
    }

    public SelectBuilder(DialectRenderer renderer, Select select) {
        this.renderer = renderer;
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
        if (currentFrom == null || currentFrom.getSources().isEmpty()) {
            throw new IllegalStateException("Cannot set alias before specifying table with from()");
        }

        TableIdentifier currentTable =
                (TableIdentifier) currentFrom.getSources().get(0);
        TableIdentifier tableWithAlias = new TableIdentifier(currentTable.getName(), alias);

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
        if (currentSelect != null && !currentSelect.getProjections().isEmpty()) {
            List<Projection> updatedProjections = new ArrayList<>();

            for (var projection : currentSelect.getProjections()) {
                if (projection instanceof ScalarExpressionProjection scalarProj
                        && scalarProj.getExpression() instanceof ColumnReference colRef) {

                    if (!"*".equals(colRef.getColumn())) {
                        // Preserve the alias if present
                        if (scalarProj.getAs() != null
                                && !scalarProj.getAs().getName().isEmpty()) {
                            updatedProjections.add(new ScalarExpressionProjection(
                                    ColumnReference.of(table.getTableReference(), colRef.getColumn()),
                                    scalarProj.getAs()));
                        } else {
                            updatedProjections.add(new ScalarExpressionProjection(
                                    ColumnReference.of(table.getTableReference(), colRef.getColumn())));
                        }
                    } else {
                        updatedProjections.add(scalarProj);
                    }
                } else if (projection instanceof AggregateCallProjection aggProj) {
                    // Update aggregate call projections with table reference
                    AggregateCall aggCall = (AggregateCall) aggProj.getExpression();
                    AggregateCall updatedAggCall = updateAggregateCallWithTable(aggCall, table);
                    if (aggProj.getAs() != null && !aggProj.getAs().getName().isEmpty()) {
                        updatedProjections.add(new AggregateCallProjection(updatedAggCall, aggProj.getAs()));
                    } else {
                        updatedProjections.add(new AggregateCallProjection(updatedAggCall));
                    }
                } else {
                    updatedProjections.add(projection);
                }
            }

            if (!updatedProjections.isEmpty()) {
                statementBuilder = statementBuilder.select(Select.of(updatedProjections.toArray(new Projection[0])));
            }
        }
    }

    private AggregateCall updateAggregateCallWithTable(AggregateCall aggCall, TableIdentifier table) {
        return switch (aggCall) {
            case AggregateCallImpl impl -> {
                if (impl.getExpression() instanceof ColumnReference colRef) {
                    ColumnReference updatedColRef = ColumnReference.of(table.getTableReference(), colRef.getColumn());
                    yield switch (impl.getOperator()) {
                        case MAX -> AggregateCall.max(updatedColRef);
                        case MIN -> AggregateCall.min(updatedColRef);
                        case AVG -> AggregateCall.avg(updatedColRef);
                        case SUM -> AggregateCall.sum(updatedColRef);
                        case COUNT -> AggregateCall.count(updatedColRef);
                    };
                }
                yield impl;
            }
            case CountDistinct cd -> {
                if (cd.getExpression() instanceof ColumnReference colRef) {
                    ColumnReference updatedColRef = ColumnReference.of(table.getTableReference(), colRef.getColumn());
                    yield AggregateCall.countDistinct(updatedColRef);
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

    public WhereConditionBuilder<SelectBuilder> where(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        return new WhereConditionBuilder<>(this, column, LogicalCombinator.AND);
    }

    public SelectBuilder groupBy(String... columns) {
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException("At least one column must be specified for GROUP BY");
        }

        ColumnReference[] groupingColumns = java.util.Arrays.stream(columns)
                .filter(column -> {
                    if (column == null || column.trim().isEmpty()) {
                        throw new IllegalArgumentException("Column name cannot be null or empty");
                    }
                    return true;
                })
                .map(column -> ColumnReferenceUtil.parseColumnReference(column, getTableReference()))
                .toArray(ColumnReference[]::new);

        statementBuilder = statementBuilder.groupBy(GroupBy.of(groupingColumns));
        return this;
    }

    public SelectBuilder orderBy(String column) {
        return orderBy(column, Sorting::asc);
    }

    public SelectBuilder orderByDesc(String column) {
        return orderBy(column, Sorting::desc);
    }

    private SelectBuilder orderBy(String column, Function<ColumnReference, Sorting> sortingFactory) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        ColumnReference columnRef = ColumnReference.of(getTableReference(), column);
        Sorting sorting = sortingFactory.apply(columnRef);
        statementBuilder = statementBuilder.orderBy(OrderBy.of(sorting));
        return this;
    }

    public SelectBuilder fetch(int rows) {
        if (rows <= 0) {
            throw new IllegalArgumentException("Fetch rows must be positive, got: " + rows);
        }

        return updateFetch(fetch -> {
            int currentOffset = fetch != null ? fetch.getOffset() : 0;
            return Fetch.builder().offset(currentOffset).rows(rows).build();
        });
    }

    public SelectBuilder offset(int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative, got: " + offset);
        }

        return updateFetch(fetch -> {
            Integer currentRows = fetch != null ? fetch.getRows() : null;
            return Fetch.builder().offset(offset).rows(currentRows).build();
        });
    }

    public WhereConditionBuilder<SelectBuilder> and(String column) {
        return new WhereConditionBuilder<>(this, column, LogicalCombinator.AND);
    }

    public WhereConditionBuilder<SelectBuilder> or(String column) {
        return new WhereConditionBuilder<>(this, column, LogicalCombinator.OR);
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
        return !(where.getCondition() instanceof NullPredicate);
    }

    static Where combineWithExisting(Where where, Predicate newCondition, LogicalCombinator combinator) {
        Predicate existingCondition = where.getCondition();
        Predicate combinedCondition = combinator.combine(existingCondition, newCondition);
        return Where.of(combinedCondition);
    }

    @Override
    public SelectBuilder addWhereCondition(Predicate condition, LogicalCombinator combinator) {
        return updateWhere(where -> SelectBuilder.combineConditions(where, condition, combinator));
    }

    // HAVING clause support
    public HavingConditionBuilder having(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        return new HavingConditionBuilder(this, column, LogicalCombinator.AND);
    }

    public HavingConditionBuilder andHaving(String column) {
        return new HavingConditionBuilder(this, column, LogicalCombinator.AND);
    }

    public HavingConditionBuilder orHaving(String column) {
        return new HavingConditionBuilder(this, column, LogicalCombinator.OR);
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
        return !(having.getCondition() instanceof NullPredicate);
    }

    static Having combineHavingWithExisting(Having having, Predicate newCondition, LogicalCombinator combinator) {
        Predicate existingCondition = having.getCondition();
        Predicate combinedCondition = combinator.combine(existingCondition, newCondition);
        return Having.of(combinedCondition);
    }

    public SelectBuilder addHavingCondition(Predicate condition, LogicalCombinator combinator) {
        return updateHaving(having -> SelectBuilder.combineHavingConditions(having, condition, combinator));
    }

    public String build() {
        validateState();
        SelectStatement selectStatement = getCurrentStatement();
        return renderer.renderSql(selectStatement);
    }

    public PreparedStatement buildPreparedStatement(Connection connection) throws SQLException {
        validateState();
        SelectStatement statement = getCurrentStatement();
        PsDto result = renderer.renderPreparedStatement(statement);

        PreparedStatement ps = connection.prepareStatement(result.sql());
        for (int i = 0; i < result.parameters().size(); i++) {
            ps.setObject(i + 1, result.parameters().get(i));
        }
        return ps;
    }

    private void validateState() {
        From from = getCurrentStatement().getFrom();
        if (from == null || from.getSources().isEmpty()) {
            throw new IllegalStateException("FROM table must be specified");
        }
    }
}
