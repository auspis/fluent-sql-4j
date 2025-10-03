package lan.tlab.sqlbuilder.dsl.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.fetch.Fetch;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.Sorting;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class SelectBuilder {
    private SelectStatement.SelectStatementBuilder statementBuilder = SelectStatement.builder();
    private final SqlRenderer sqlRenderer;

    public SelectBuilder(SqlRenderer sqlRenderer, String... columns) {
        this.sqlRenderer = sqlRenderer;
        if (columns != null && columns.length > 0) {
            statementBuilder = statementBuilder.select(Select.of(java.util.Arrays.stream(columns)
                    .map(column -> new ScalarExpressionProjection(ColumnReference.of("", column)))
                    .toArray(ScalarExpressionProjection[]::new)));
        }
    }

    public SelectBuilder from(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }

        Table table = new Table(tableName);
        statementBuilder = statementBuilder.from(From.of(table));

        updateSelectClauseWithTable(table);
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

        Table currentTable = (Table) currentFrom.getSources().get(0);
        Table tableWithAlias = new Table(currentTable.getName(), alias);

        statementBuilder = statementBuilder.from(From.of(tableWithAlias));
        updateSelectClauseWithTable(tableWithAlias);

        return this;
    }

    private SelectStatement getCurrentStatement() {
        return statementBuilder.build();
    }

    String getTableReference() {
        From from = getCurrentStatement().getFrom();
        if (from == null || from.getSources().isEmpty()) {
            return "";
        }

        Table table = (Table) from.getSources().get(0);
        if (table.getAs() != null && !table.getAs().getName().isEmpty()) {
            return table.getAs().getName();
        }
        return table.getName();
    }

    private void updateSelectClauseWithTable(Table table) {
        Select currentSelect = getCurrentStatement().getSelect();
        if (currentSelect != null && !currentSelect.getProjections().isEmpty()) {
            String tableReference =
                    table.getAs() != null && !table.getAs().getName().isEmpty()
                            ? table.getAs().getName()
                            : table.getName();

            List<ScalarExpressionProjection> updatedProjections = new ArrayList<>();

            for (var projection : currentSelect.getProjections()) {
                if (projection instanceof ScalarExpressionProjection scalarProj
                        && scalarProj.getExpression() instanceof ColumnReference colRef) {

                    if (!"*".equals(colRef.getColumn())) {
                        updatedProjections.add(
                                new ScalarExpressionProjection(ColumnReference.of(tableReference, colRef.getColumn())));
                    } else {
                        updatedProjections.add(scalarProj);
                    }
                } else {
                    updatedProjections.add((ScalarExpressionProjection) projection);
                }
            }

            if (!updatedProjections.isEmpty()) {
                statementBuilder = statementBuilder.select(
                        Select.of(updatedProjections.toArray(new ScalarExpressionProjection[0])));
            }
        }
    }

    private SelectBuilder updateFetch(Function<Fetch, Fetch> updater) {
        Fetch currentFetch = getCurrentStatement().getFetch();
        Fetch newFetch = updater.apply(currentFetch);
        statementBuilder = statementBuilder.fetch(newFetch);
        return this;
    }

    public WhereConditionBuilder where(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        return new WhereConditionBuilder(this, column, LogicalCombinator.AND);
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

    public WhereConditionBuilder and(String column) {
        return new WhereConditionBuilder(this, column, LogicalCombinator.AND);
    }

    public WhereConditionBuilder or(String column) {
        return new WhereConditionBuilder(this, column, LogicalCombinator.OR);
    }

    // Functional WHERE updater
    SelectBuilder updateWhere(Function<Where, Where> updater) {
        Where currentWhere = getCurrentStatement().getWhere();
        Where newWhere = updater.apply(currentWhere);
        statementBuilder = statementBuilder.where(newWhere);
        return this;
    }

    // Helper to combine conditions with functional approach
    static Where combineConditions(Where currentWhere, BooleanExpression newCondition, LogicalCombinator combinator) {
        return Optional.ofNullable(currentWhere)
                .filter(SelectBuilder::hasValidCondition)
                .map(where -> combineWithExisting(where, newCondition, combinator))
                .orElse(Where.of(newCondition));
    }

    static boolean hasValidCondition(Where where) {
        return !(where.getCondition() instanceof NullBooleanExpression);
    }

    static Where combineWithExisting(Where where, BooleanExpression newCondition, LogicalCombinator combinator) {
        BooleanExpression existingCondition = where.getCondition();
        BooleanExpression combinedCondition = combinator.combine(existingCondition, newCondition);
        return Where.of(combinedCondition);
    }

    SelectBuilder addWhereCondition(BooleanExpression condition, LogicalCombinator combinator) {
        return updateWhere(where -> SelectBuilder.combineConditions(where, condition, combinator));
    }

    public String build() {
        validateState();
        SelectStatement selectStatement = getCurrentStatement();
        return selectStatement.accept(sqlRenderer, new AstContext());
    }

    public PreparedStatement buildPrepared(Connection connection) throws SQLException {
        validateState();
        SelectStatement stmt = getCurrentStatement();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = stmt.accept(visitor, new AstContext());

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
