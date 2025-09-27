package lan.tlab.sqlbuilder.dsl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.Sorting;
import lan.tlab.sqlbuilder.ast.clause.pagination.Pagination;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.bool.IsNotNull;
import lan.tlab.sqlbuilder.ast.expression.bool.IsNull;
import lan.tlab.sqlbuilder.ast.expression.bool.Like;
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
    private final List<String> columns = new ArrayList<>();
    private Optional<Table> fromTable = Optional.empty();
    private final List<WhereConditionEntry> whereConditions = new ArrayList<>();
    private Optional<OrderBy> orderBy = Optional.empty();
    private Integer limit;
    private Integer offset;

    // Inner class to track conditions with their logical operators
    private static class WhereConditionEntry {
        final BooleanExpression condition;
        final LogicalOperator operator; // AND or OR (null for first condition)

        WhereConditionEntry(BooleanExpression condition, LogicalOperator operator) {
            this.condition = condition;
            this.operator = operator;
        }
    }

    private enum LogicalOperator {
        AND,
        OR
    }

    public SelectBuilder(String... columns) {
        if (columns != null && columns.length > 0) {
            this.columns.addAll(Arrays.asList(columns));
        } else {
            this.columns.add("*");
        }
    }

    public SelectBuilder from(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }
        this.fromTable = Optional.of(new Table(tableName));
        return this;
    }

    public SelectBuilder as(String alias) {
        if (fromTable.isEmpty()) {
            throw new IllegalStateException("Cannot set alias before specifying table with from()");
        }
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        this.fromTable = Optional.of(new Table(fromTable.get().getName(), alias));
        return this;
    }

    // Helper method to get the table reference name (alias if available, otherwise table name)
    private String getTableReference() {
        return fromTable
                .map(table -> {
                    if (table.getAs() != null && !table.getAs().getName().isEmpty()) {
                        return table.getAs().getName();
                    }
                    return table.getName();
                })
                .orElse("");
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
        this.orderBy = Optional.of(OrderBy.of(sorting));
        return this;
    }

    public SelectBuilder limit(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive, got: " + limit);
        }
        this.limit = limit;
        return this;
    }

    public SelectBuilder offset(int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative, got: " + offset);
        }
        this.offset = offset;
        return this;
    }

    public WhereBuilder and(String column) {
        return new WhereBuilder(this, column, false);
    }

    public WhereBuilder or(String column) {
        return new WhereBuilder(this, column, true);
    }

    SelectBuilder addWhereCondition(BooleanExpression condition, LogicalOperator operator) {
        whereConditions.add(new WhereConditionEntry(condition, operator));
        return this;
    }

    // Helper method to convert Object to Literal
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
        SelectStatement selectStatement = buildSelectStatement();

        // Use the SqlRenderer to render the statement with standard SQL strategy
        SqlRenderer renderer = SqlRenderer.builder().build(); // Uses standard() strategy by default
        return selectStatement.accept(renderer, new AstContext());
    }

    private void validateState() {
        if (fromTable.isEmpty()) {
            throw new IllegalStateException("FROM table must be specified");
        }
    }

    public PreparedStatement buildPrepared(Connection connection) throws SQLException {
        SelectStatement stmt = buildSelectStatement();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = stmt.accept(visitor, new AstContext());

        PreparedStatement ps = connection.prepareStatement(result.sql());
        for (int i = 0; i < result.parameters().size(); i++) {
            ps.setObject(i + 1, result.parameters().get(i));
        }
        return ps;
    }

    private SelectStatement buildSelectStatement() {
        if (fromTable.isEmpty()) {
            throw new IllegalStateException("FROM table must be specified");
        }

        SelectStatement.SelectStatementBuilder builder = SelectStatement.builder();

        // Build SELECT clause
        if (columns.isEmpty() || (columns.size() == 1 && "*".equals(columns.get(0)))) {
            // SELECT * (use default empty projections which renders as *)
            builder.select(Select.builder().build());
        } else {
            List<ScalarExpressionProjection> projections = new ArrayList<>();
            for (String column : columns) {
                projections.add(new ScalarExpressionProjection(ColumnReference.of(getTableReference(), column)));
            }
            builder.select(Select.of(projections.toArray(new ScalarExpressionProjection[0])));
        }

        // Build FROM clause
        fromTable.ifPresent(table -> builder.from(From.of(table)));

        // Build WHERE clause
        if (!whereConditions.isEmpty()) {
            BooleanExpression combinedCondition = whereConditions.get(0).condition;
            for (int i = 1; i < whereConditions.size(); i++) {
                WhereConditionEntry entry = whereConditions.get(i);
                if (entry.operator == LogicalOperator.OR) {
                    combinedCondition = AndOr.or(combinedCondition, entry.condition);
                } else {
                    combinedCondition = AndOr.and(combinedCondition, entry.condition);
                }
            }
            builder.where(Where.of(combinedCondition));
        }

        // Build ORDER BY clause
        orderBy.ifPresent(builder::orderBy);

        // Build LIMIT/OFFSET clause (combined in Pagination)
        if (limit != null || offset != null) {
            Pagination.PaginationBuilder paginationBuilder = Pagination.builder();

            if (limit != null) {
                paginationBuilder.perPage(limit);
            }

            if (offset != null && limit != null) {
                // Calculate page from offset and limit
                int page = (offset / limit) + 1;
                paginationBuilder.page(page);
            } else if (offset == null) {
                // Only limit, start from page 1
                paginationBuilder.page(1);
            }

            builder.pagination(paginationBuilder.build());
        }

        return builder.build();
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
            LogicalOperator op = selectBuilder.whereConditions.isEmpty() ? null : logicalOperator;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder ne(Object value) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = Comparison.ne(columnRef, selectBuilder.toLiteral(value));
            LogicalOperator op = selectBuilder.whereConditions.isEmpty() ? null : logicalOperator;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder gt(Object value) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = Comparison.gt(columnRef, selectBuilder.toLiteral(value));
            LogicalOperator op = selectBuilder.whereConditions.isEmpty() ? null : logicalOperator;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder lt(Object value) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = Comparison.lt(columnRef, selectBuilder.toLiteral(value));
            LogicalOperator op = selectBuilder.whereConditions.isEmpty() ? null : logicalOperator;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder gte(Object value) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = Comparison.gte(columnRef, selectBuilder.toLiteral(value));
            LogicalOperator op = selectBuilder.whereConditions.isEmpty() ? null : logicalOperator;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder lte(Object value) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = Comparison.lte(columnRef, selectBuilder.toLiteral(value));
            LogicalOperator op = selectBuilder.whereConditions.isEmpty() ? null : logicalOperator;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder like(String pattern) {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = new Like(columnRef, pattern);
            LogicalOperator op = selectBuilder.whereConditions.isEmpty() ? null : logicalOperator;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder isNull() {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = new IsNull(columnRef);
            LogicalOperator op = selectBuilder.whereConditions.isEmpty() ? null : logicalOperator;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }

        public SelectBuilder isNotNull() {
            ColumnReference columnRef = ColumnReference.of(selectBuilder.getTableReference(), column);
            BooleanExpression condition = new IsNotNull(columnRef);
            LogicalOperator op = selectBuilder.whereConditions.isEmpty() ? null : logicalOperator;
            selectBuilder.addWhereCondition(condition, op);
            return selectBuilder;
        }
    }
}
