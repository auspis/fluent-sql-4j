package lan.tlab.sqlbuilder.dsl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.fetch.Fetch;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.Sorting;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.bool.IsNotNull;
import lan.tlab.sqlbuilder.ast.expression.bool.IsNull;
import lan.tlab.sqlbuilder.ast.expression.bool.Like;
import lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class SelectBuilder {
    // Single source of truth
    private SelectStatement.SelectStatementBuilder statementBuilder = SelectStatement.builder();

    private enum LogicalOperator {
        AND,
        OR
    }

    public SelectBuilder(String... columns) {
        if (columns != null && columns.length > 0) {
            // Build projections for specified columns
            List<ScalarExpressionProjection> projections = new ArrayList<>();
            for (String column : columns) {
                projections.add(new ScalarExpressionProjection(ColumnReference.of("", column)));
            }
            this.statementBuilder =
                    this.statementBuilder.select(Select.of(projections.toArray(new ScalarExpressionProjection[0])));
        }
        // else: Default SELECT * behavior - no select clause (empty projections list renders as *)
    }

    public SelectBuilder from(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }

        Table table = new Table(tableName);
        this.statementBuilder = this.statementBuilder.from(From.of(table));

        // Update SELECT clause with table context
        updateSelectClauseWithTable(table);

        return this;
    }

    public SelectBuilder as(String alias) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }

        // Get current table from FROM clause
        From currentFrom = getCurrentStatement().getFrom();
        if (currentFrom == null || currentFrom.getSources().isEmpty()) {
            throw new IllegalStateException("Cannot set alias before specifying table with from()");
        }

        Table currentTable = (Table) currentFrom.getSources().get(0);
        Table tableWithAlias = new Table(currentTable.getName(), alias);

        this.statementBuilder = this.statementBuilder.from(From.of(tableWithAlias));
        updateSelectClauseWithTable(tableWithAlias);

        return this;
    }

    // Helper methods to work with current state
    private SelectStatement getCurrentStatement() {
        return statementBuilder.build();
    }

    private String getTableReference() {
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

    // Helper method to check if statementBuilder has WHERE clause
    private boolean hasWhereClause() {
        Where where = statementBuilder.build().getWhere();
        return where != null && !(where.getCondition() instanceof NullBooleanExpression);
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
                this.statementBuilder = this.statementBuilder.select(
                        Select.of(updatedProjections.toArray(new ScalarExpressionProjection[0])));
            }
        }
    }

    // Pagination using functional approach
    private SelectBuilder updateFetch(Function<Fetch, Fetch> updater) {
        Fetch currentFetch = getCurrentStatement().getFetch();
        Fetch newFetch = updater.apply(currentFetch);
        this.statementBuilder = this.statementBuilder.fetch(newFetch);
        return this;
    }

    // Direct where method with operator
    public SelectBuilder where(String column, String operator, Object value) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        ColumnReference columnRef = ColumnReference.of(getTableReference(), column);
        BooleanExpression condition = createCondition(columnRef, operator, value);
        return addWhereCondition(condition, LogicalOperator.AND);
    }

    // Fluent where method that returns WhereBuilder
    public WhereBuilder where(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        return new WhereBuilder(this, column, false); // false = AND
    }

    // Helper method to create conditions from operator string
    private BooleanExpression createCondition(ColumnReference columnRef, String operator, Object value) {
        Literal<?> literal = toLiteral(value);
        return switch (operator) {
            case "=" -> Comparison.eq(columnRef, literal);
            case ">" -> Comparison.gt(columnRef, literal);
            case ">=" -> Comparison.gte(columnRef, literal);
            case "<" -> Comparison.lt(columnRef, literal);
            case "<=" -> Comparison.lte(columnRef, literal);
            case "!=" -> Comparison.ne(columnRef, literal);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
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
        this.statementBuilder = this.statementBuilder.orderBy(OrderBy.of(sorting));
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

    public WhereBuilder and(String column) {
        return new WhereBuilder(this, column, false);
    }

    public WhereBuilder or(String column) {
        return new WhereBuilder(this, column, true);
    }

    SelectBuilder addWhereCondition(BooleanExpression condition, LogicalOperator operator) {
        // Get current WHERE clause from statementBuilder
        Where currentWhere = statementBuilder.build().getWhere();

        // Combine with new condition
        BooleanExpression combinedCondition;
        if (currentWhere == null || currentWhere.getCondition() instanceof NullBooleanExpression) {
            // First condition - use it directly regardless of operator
            combinedCondition = condition;
        } else {
            // Combine with existing WHERE clause using logical operator
            BooleanExpression existingCondition = currentWhere.getCondition();
            if (operator == LogicalOperator.OR) {
                combinedCondition = AndOr.or(existingCondition, condition);
            } else {
                // Default to AND if operator is null or AND
                combinedCondition = AndOr.and(existingCondition, condition);
            }
        }

        // Update statementBuilder with combined WHERE clause
        this.statementBuilder = this.statementBuilder.where(Where.of(combinedCondition));

        return this;
    } // Helper method to convert Object to Literal

    private Literal<?> toLiteral(Object value) {
        if (value instanceof String) {
            return Literal.of((String) value);
        } else if (value instanceof Number) {
            return Literal.of((Number) value);
        } else if (value instanceof Boolean) {
            return Literal.of((Boolean) value);
        } else if (value == null) {
            return Literal.ofNull();
        } else {
            return Literal.of(value.toString());
        }
    }

    public String build() {
        validateState();
        SelectStatement selectStatement = getCurrentStatement();
        SqlRenderer renderer = SqlRenderer.builder().build();
        return selectStatement.accept(renderer, new AstContext());
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

    // Builder class for WHERE conditions with fluent API
    public static class WhereBuilder {
        private final SelectBuilder selectBuilder;
        private final String column;
        private final LogicalOperator logicalOperator;

        public WhereBuilder(SelectBuilder selectBuilder, String column, boolean isOr) {
            this.selectBuilder = selectBuilder;
            this.column = column;
            this.logicalOperator = isOr ? LogicalOperator.OR : LogicalOperator.AND;
        }

        public SelectBuilder eq(Object value) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = Comparison.eq(columnRef, selectBuilder.toLiteral(value));
            LogicalOperator op = selectBuilder.hasWhereClause() ? logicalOperator : null;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder ne(Object value) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = Comparison.ne(columnRef, selectBuilder.toLiteral(value));
            LogicalOperator op = selectBuilder.hasWhereClause() ? logicalOperator : null;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder gt(Object value) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = Comparison.gt(columnRef, selectBuilder.toLiteral(value));
            LogicalOperator op = selectBuilder.hasWhereClause() ? logicalOperator : null;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder lt(Object value) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = Comparison.lt(columnRef, selectBuilder.toLiteral(value));
            LogicalOperator op = selectBuilder.hasWhereClause() ? logicalOperator : null;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder gte(Object value) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = Comparison.gte(columnRef, selectBuilder.toLiteral(value));
            LogicalOperator op = selectBuilder.hasWhereClause() ? logicalOperator : null;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder lte(Object value) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = Comparison.lte(columnRef, selectBuilder.toLiteral(value));
            LogicalOperator op = selectBuilder.hasWhereClause() ? logicalOperator : null;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder like(String pattern) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = new Like(columnRef, pattern);
            LogicalOperator op = selectBuilder.hasWhereClause() ? logicalOperator : null;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder isNull() {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = new IsNull(columnRef);
            LogicalOperator op = selectBuilder.hasWhereClause() ? logicalOperator : null;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder isNotNull() {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = new IsNotNull(columnRef);
            LogicalOperator op = selectBuilder.hasWhereClause() ? logicalOperator : null;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }
    }
}
