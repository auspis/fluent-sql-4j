package lan.tlab.sqlbuilder.ast.visitor.ps;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.clause.conditional.having.Having;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.from.source.FromSource;
import lan.tlab.sqlbuilder.ast.clause.from.source.FromSubquery;
import lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin;
import lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.Sorting;
import lan.tlab.sqlbuilder.ast.clause.pagination.Pagination;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.bool.Between;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.bool.In;
import lan.tlab.sqlbuilder.ast.expression.bool.IsNotNull;
import lan.tlab.sqlbuilder.ast.expression.bool.IsNull;
import lan.tlab.sqlbuilder.ast.expression.bool.Like;
import lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.LogicalOperator;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.Not;
import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.DefaultValues;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertSource;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertValues;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.item.UpdateItem;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ReferencesItem;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.NullScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.DateArithmetic;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.ExtractDatePart;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Mod;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Power;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Round;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.UnaryNumeric;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.CharLength;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.CharacterLength;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Concat;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.DataLength;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Left;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Length;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Replace;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Substring;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Trim;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.UnaryString;
import lan.tlab.sqlbuilder.ast.expression.scalar.convert.Cast;
import lan.tlab.sqlbuilder.ast.expression.set.ExceptExpression;
import lan.tlab.sqlbuilder.ast.expression.set.IntersectExpression;
import lan.tlab.sqlbuilder.ast.expression.set.NullSetExpression;
import lan.tlab.sqlbuilder.ast.expression.set.UnionExpression;
import lan.tlab.sqlbuilder.ast.statement.CreateTableStatement;
import lan.tlab.sqlbuilder.ast.statement.DeleteStatement;
import lan.tlab.sqlbuilder.ast.statement.InsertStatement;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.statement.UpdateStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.DefaultFromClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.DefaultGroupByClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.DefaultHavingClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.DefaultOrderByClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.DefaultSelectClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.DefaultWhereClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.FromClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.GroupByClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.HavingClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.OrderByClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.SelectClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.WhereClausePsStrategy;

public class PsVisitor implements Visitor<PsDto> {
    private final SelectClausePsStrategy selectClauseStrategy;
    private final FromClausePsStrategy fromClauseStrategy;
    private final WhereClausePsStrategy whereClauseStrategy;
    private final GroupByClausePsStrategy groupByClauseStrategy;
    private final HavingClausePsStrategy havingClauseStrategy;
    private final OrderByClausePsStrategy orderByClauseStrategy;

    public PsVisitor() {
        this(
                new DefaultSelectClausePsStrategy(),
                new DefaultFromClausePsStrategy(),
                new DefaultWhereClausePsStrategy(),
                new DefaultGroupByClausePsStrategy(),
                new DefaultHavingClausePsStrategy(),
                new DefaultOrderByClausePsStrategy());
    }

    public PsVisitor(SelectClausePsStrategy selectClauseStrategy) {
        this(
                selectClauseStrategy,
                new DefaultFromClausePsStrategy(),
                new DefaultWhereClausePsStrategy(),
                new DefaultGroupByClausePsStrategy(),
                new DefaultHavingClausePsStrategy(),
                new DefaultOrderByClausePsStrategy());
    }

    public PsVisitor(SelectClausePsStrategy selectClauseStrategy, FromClausePsStrategy fromClauseStrategy) {
        this(
                selectClauseStrategy,
                fromClauseStrategy,
                new DefaultWhereClausePsStrategy(),
                new DefaultGroupByClausePsStrategy(),
                new DefaultHavingClausePsStrategy(),
                new DefaultOrderByClausePsStrategy());
    }

    public PsVisitor(
            SelectClausePsStrategy selectClauseStrategy,
            FromClausePsStrategy fromClauseStrategy,
            WhereClausePsStrategy whereClauseStrategy) {
        this(
                selectClauseStrategy,
                fromClauseStrategy,
                whereClauseStrategy,
                new DefaultGroupByClausePsStrategy(),
                new DefaultHavingClausePsStrategy(),
                new DefaultOrderByClausePsStrategy());
    }

    public PsVisitor(
            SelectClausePsStrategy selectClauseStrategy,
            FromClausePsStrategy fromClauseStrategy,
            WhereClausePsStrategy whereClauseStrategy,
            GroupByClausePsStrategy groupByClauseStrategy) {
        this(
                selectClauseStrategy,
                fromClauseStrategy,
                whereClauseStrategy,
                groupByClauseStrategy,
                new DefaultHavingClausePsStrategy(),
                new DefaultOrderByClausePsStrategy());
    }

    public PsVisitor(
            SelectClausePsStrategy selectClauseStrategy,
            FromClausePsStrategy fromClauseStrategy,
            WhereClausePsStrategy whereClauseStrategy,
            GroupByClausePsStrategy groupByClauseStrategy,
            HavingClausePsStrategy havingClauseStrategy) {
        this(
                selectClauseStrategy,
                fromClauseStrategy,
                whereClauseStrategy,
                groupByClauseStrategy,
                havingClauseStrategy,
                new DefaultOrderByClausePsStrategy());
    }

    public PsVisitor(
            SelectClausePsStrategy selectClauseStrategy,
            FromClausePsStrategy fromClauseStrategy,
            WhereClausePsStrategy whereClauseStrategy,
            GroupByClausePsStrategy groupByClauseStrategy,
            HavingClausePsStrategy havingClauseStrategy,
            OrderByClausePsStrategy orderByClauseStrategy) {
        this.selectClauseStrategy = selectClauseStrategy;
        this.fromClauseStrategy = fromClauseStrategy;
        this.whereClauseStrategy = whereClauseStrategy;
        this.groupByClauseStrategy = groupByClauseStrategy;
        this.havingClauseStrategy = havingClauseStrategy;
        this.orderByClauseStrategy = orderByClauseStrategy;
    }

    @Override
    public PsDto visit(InsertStatement stmt, AstContext ctx) {
        // Table name
        Table table = (Table) stmt.getTable();
        String tableName = table.getName();
        // Column names
        List<String> columns = stmt.getColumns().stream()
                .map(ColumnReference::getColumn)
                .map(name -> "\"" + name + "\"")
                .collect(Collectors.toList());
        String columnList = String.join(", ", columns);
        // Placeholders and parameters
        String placeholders = "";
        List<Object> params = new ArrayList<>();
        if (stmt.getData() instanceof InsertValues values) {
            placeholders = values.getValueExpressions().stream()
                    .map(val -> {
                        if (val instanceof Literal<?> literal) {
                            params.add(literal.getValue());
                        } else {
                            params.add(null); // fallback
                        }
                        return "?";
                    })
                    .collect(Collectors.joining(", "));
        }
        String sql = "INSERT INTO \"" + tableName + "\" (" + columnList + ") VALUES (" + placeholders + ")";
        return new PsDto(sql, params);
    }

    @Override
    public PsDto visit(SelectStatement stmt, AstContext ctx) {
        // SELECT ...
        PsDto selectResult = stmt.getSelect().accept(this, ctx);
        // FROM ...
        PsDto fromResult = stmt.getFrom().accept(this, ctx);
        // WHERE ... (optional)
        PsDto whereResult = null;
        String whereClause = "";
        if (stmt.getWhere() != null
                && stmt.getWhere().getCondition() != null
                && !(stmt.getWhere().getCondition()
                        instanceof lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression)) {
            whereResult = visit(stmt.getWhere(), ctx);
            whereClause = " WHERE " + whereResult.sql();
        }
        // GROUP BY ... (optional)
        PsDto groupByResult = null;
        String groupByClause = "";
        if (stmt.getGroupBy() != null
                && !stmt.getGroupBy().getGroupingExpressions().isEmpty()) {
            groupByResult = stmt.getGroupBy().accept(this, ctx);
            groupByClause = " GROUP BY " + groupByResult.sql();
        }
        // HAVING ... (optional, after GROUP BY)
        PsDto havingResult = null;
        String havingClause = "";
        if (stmt.getHaving() != null && stmt.getHaving().getCondition() != null) {
            havingResult = visit(stmt.getHaving(), ctx);
            if (!havingResult.sql().isBlank()) {
                havingClause = " HAVING " + havingResult.sql();
            }
        }
        // ORDER BY ... (optional)
        PsDto orderByResult = null;
        String orderByClause = "";
        if (stmt.getOrderBy() != null && !stmt.getOrderBy().getSortings().isEmpty()) {
            orderByResult = stmt.getOrderBy().accept(this, ctx);
            orderByClause = " ORDER BY " + orderByResult.sql();
        }
        // PAGINATION (LIMIT/OFFSET)
        String paginationClause = "";
        if (stmt.getPagination() != null && stmt.getPagination().getPerPage() != null) {
            Integer perPage = stmt.getPagination().getPerPage();
            Integer page = stmt.getPagination().getPage();
            if (perPage != null) {
                paginationClause = " LIMIT " + perPage;
                if (page != null && page > 0) {
                    int offset = page * perPage;
                    paginationClause += " OFFSET " + offset;
                }
            }
        }
        String sql = "SELECT " + selectResult.sql() + " FROM " + fromResult.sql() + whereClause + groupByClause
                + havingClause + orderByClause + paginationClause;
        List<Object> allParams = new ArrayList<>();
        allParams.addAll(selectResult.parameters());
        allParams.addAll(fromResult.parameters());
        if (whereResult != null) {
            allParams.addAll(whereResult.parameters());
        }
        if (groupByResult != null) {
            allParams.addAll(groupByResult.parameters());
        }
        if (havingResult != null) {
            allParams.addAll(havingResult.parameters());
        }
        if (orderByResult != null) {
            allParams.addAll(orderByResult.parameters());
        }
        return new PsDto(sql, allParams);
    }

    @Override
    public PsDto visit(Select select, AstContext ctx) {
        return selectClauseStrategy.handle(select, this, ctx);
    }

    @Override
    public PsDto visit(Table table, AstContext ctx) {
        String sql = "\"" + table.getName() + "\"";
        String alias = table.getAs() != null ? table.getAs().getName() : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS " + alias;
        }
        return new PsDto(sql, List.of());
    }

    @Override
    public PsDto visit(ColumnReference col, AstContext ctx) {
        boolean qualify = ctx != null && ctx.getScope() == AstContext.Scope.JOIN_ON;
        String sql;
        if (qualify && col.getTable() != null && !col.getTable().isBlank()) {
            sql = "\"" + col.getTable() + "\".\"" + col.getColumn() + "\"";
        } else {
            sql = "\"" + col.getColumn() + "\"";
        }
        return new PsDto(sql, List.of());
    }

    @Override
    public PsDto visit(Comparison cmp, AstContext ctx) {
        String operator;
        switch (cmp.getOperator()) {
            case EQUALS -> operator = "=";
            case NOT_EQUALS -> operator = "<>";
            case GREATER_THAN -> operator = ">";
            case GREATER_THAN_OR_EQUALS -> operator = ">=";
            case LESS_THAN -> operator = "<";
            case LESS_THAN_OR_EQUALS -> operator = "<=";
            default -> throw new UnsupportedOperationException("Operator not supported: " + cmp.getOperator());
        }
        boolean qualify = ctx != null && ctx.getScope() == AstContext.Scope.JOIN_ON;
        String lhs;
        if (cmp.getLhs() instanceof ColumnReference colLhs) {
            PsDto lhsResult = visit(colLhs, ctx.copy());
            lhs = lhsResult.sql();
        } else {
            lhs = cmp.getLhs().accept(this, ctx).sql();
        }
        String rhsSql;
        List<Object> params = new ArrayList<>();
        if (cmp.getRhs() instanceof ColumnReference colRhs) {
            rhsSql = visit(colRhs, ctx.copy()).sql();
        } else {
            PsDto rhsResult = cmp.getRhs().accept(this, ctx);
            rhsSql = rhsResult.sql();
            params.addAll(rhsResult.parameters());
        }
        return new PsDto(lhs + " " + operator + " " + rhsSql, params);
    }

    @Override
    public PsDto visit(Where where, AstContext ctx) {
        return whereClauseStrategy.handle(where, this, ctx);
    }

    @Override
    public PsDto visit(Literal<?> literal, AstContext ctx) {
        return new PsDto("?", List.of(literal.getValue()));
    }

    @Override
    public PsDto visit(UpdateStatement updateStatement, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(DeleteStatement deleteStatement, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(CreateTableStatement createTableStatement, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(AggregationFunctionProjection aggregationFunctionProjection, AstContext ctx) {
        // The AggregationFunctionProjection wraps an AggregateCall (e.g., COUNT, SUM, etc.)
        var expr = aggregationFunctionProjection.getExpression();
        PsDto exprResult = expr.accept(this, ctx);
        String sql = exprResult.sql();
        // Handle alias if present
        String alias = aggregationFunctionProjection.getAs() != null
                ? aggregationFunctionProjection.getAs().getName()
                : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS \"" + alias + "\"";
        }
        return new PsDto(sql, exprResult.parameters());
    }

    @Override
    public PsDto visit(ScalarExpressionProjection scalarExpressionProjection, AstContext ctx) {
        // Visit the underlying scalar expression
        var expr = scalarExpressionProjection.getExpression();
        PsDto exprResult = expr.accept(this, ctx);
        String sql = exprResult.sql();
        // Handle alias if present
        String alias = scalarExpressionProjection.getAs() != null
                ? scalarExpressionProjection.getAs().getName()
                : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS \"" + alias + "\"";
        }
        return new PsDto(sql, exprResult.parameters());
    }

    @Override
    public PsDto visit(From clause, AstContext ctx) {
        return fromClauseStrategy.handle(clause, this, ctx);
    }

    @Override
    public PsDto visit(OnJoin join, AstContext ctx) {
        // Visit left and right sources
        PsDto leftResult = visit(join.getLeft(), ctx);
        PsDto rightResult = visit(join.getRight(), ctx);
        String joinType;
        switch (join.getType()) {
            case INNER -> joinType = "INNER JOIN";
            case LEFT -> joinType = "LEFT JOIN";
            case RIGHT -> joinType = "RIGHT JOIN";
            case FULL -> joinType = "FULL JOIN";
            case CROSS -> joinType = "CROSS JOIN";
            default -> throw new UnsupportedOperationException("Unknown join type: " + join.getType());
        }
        StringBuilder sql = new StringBuilder();
        sql.append(leftResult.sql()).append(" ").append(joinType).append(" ").append(rightResult.sql());
        List<Object> params = new ArrayList<>();
        params.addAll(leftResult.parameters());
        params.addAll(rightResult.parameters());
        // ON condition (not for CROSS JOIN)
        if (join.getType() != lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin.JoinType.CROSS) {
            if (join.getOnCondition() != null) {
                // Passa il contesto con scope JOIN_ON
                PsDto onResult = join.getOnCondition().accept(this, new AstContext(AstContext.Scope.JOIN_ON));
                sql.append(" ON ").append(onResult.sql());
                params.addAll(onResult.parameters());
            }
        }
        return new PsDto(sql.toString(), params);
    }

    @Override
    public PsDto visit(FromSubquery fromSubquery, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(GroupBy clause, AstContext ctx) {
        return groupByClauseStrategy.handle(clause, this, ctx);
    }

    @Override
    public PsDto visit(Having clause, AstContext ctx) {
        return havingClauseStrategy.handle(clause, this, ctx);
    }

    @Override
    public PsDto visit(OrderBy clause, AstContext ctx) {
        return orderByClauseStrategy.handle(clause, this, ctx);
    }

    @Override
    public PsDto visit(Sorting sorting, AstContext ctx) {
        PsDto exprResult = sorting.getExpression().accept(this, ctx);
        String sql = exprResult.sql();
        String order = sorting.getSortOrder().getSqlKeyword();
        if (!order.isEmpty()) {
            sql += " " + order;
        }
        return new PsDto(sql, exprResult.parameters());
    }

    @Override
    public PsDto visit(Pagination clause, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(NullBooleanExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Between expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(In expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(AndOr expression, AstContext ctx) {
        String operator = expression.getOperator() == LogicalOperator.AND ? "AND" : "OR";
        List<String> sqlParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (BooleanExpression expr : expression.getOperands()) {
            PsDto res = expr.accept(this, ctx);
            sqlParts.add("(" + res.sql() + ")");
            params.addAll(res.parameters());
        }
        String sql = String.join(" " + operator + " ", sqlParts);
        return new PsDto(sql, params);
    }

    @Override
    public PsDto visit(Not expression, AstContext ctx) {
        PsDto inner = expression.getExpression().accept(this, ctx);
        String sql = "NOT (" + inner.sql() + ")";
        return new PsDto(sql, inner.parameters());
    }

    @Override
    public PsDto visit(BinaryArithmeticExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(UnaryArithmeticExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Cast functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Concat functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(CurrentDate functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(CurrentDateTime functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(DateArithmetic functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(ExtractDatePart functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Left functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Length functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(CharLength functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(CharacterLength functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(DataLength functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Mod functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(NullScalarExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Power functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Replace functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Round functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Substring functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Trim functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(UnaryNumeric functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(UnaryString functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(NullSetExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(ExceptExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(IntersectExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(UnionExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(As item, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(UpdateItem item, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(ReferencesItem item, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(TableDefinition item, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition item, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.SimpleDataType type, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(
            lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.ParameterizedDataType type, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey item, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.Index index, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(
            lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint constraint, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(
            lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.UniqueConstraint constraint, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(
            lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.ForeignKeyConstraint constraint, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(
            lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.CheckConstraint constraint, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(
            lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.DefaultConstraint constraint, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(lan.tlab.sqlbuilder.ast.expression.scalar.ScalarSubquery expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Interval interval, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(AggregateCall expression, AstContext ctx) {
        // Handle SQL generation for aggregate functions (COUNT, SUM, AVG, MIN, MAX)
        // The AggregateCallImpl should expose the operator and argument
        // We'll assume the interface provides getOperator() and getArgument()
        String functionName = null;
        String argumentSql = null;
        List<Object> params = new ArrayList<>();
        try {
            var operatorField = expression.getClass().getDeclaredField("operator");
            operatorField.setAccessible(true);
            var operator = operatorField.get(expression);
            functionName = operator.toString();
            // Normalize to SQL function names
            functionName = switch (functionName) {
                case "COUNT" -> "COUNT";
                case "SUM" -> "SUM";
                case "AVG" -> "AVG";
                case "MIN" -> "MIN";
                case "MAX" -> "MAX";
                default -> throw new UnsupportedOperationException("Unknown aggregate function: " + functionName);
            };
            var argumentField = expression.getClass().getDeclaredField("expression");
            argumentField.setAccessible(true);
            var argument = argumentField.get(expression);
            if (argument == null) {
                argumentSql = "*";
            } else {
                PsDto argResult = ((ScalarExpression) argument).accept(this, ctx);
                argumentSql = argResult.sql();
                params.addAll(argResult.parameters());
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("AggregateCall reflection failed", e);
        }
        String sql = functionName + "(" + argumentSql + ")";
        return new PsDto(sql, params);
    }

    @Override
    public PsDto visit(InsertValues item, AstContext ctx) {
        // InsertValues holds a list of value expressions (e.g., literals)
        List<String> placeholders = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var expr : item.getValueExpressions()) {
            if (expr instanceof Literal<?> literal) {
                placeholders.add("?");
                params.add(literal.getValue());
            } else {
                // Fallback for non-literal expressions
                placeholders.add("?");
                params.add(null);
            }
        }
        String sql = String.join(", ", placeholders);
        return new PsDto(sql, params);
    }

    @Override
    public PsDto visit(InsertSource item, AstContext ctx) {
        // InsertSource is a parent type, delegate to the actual subtype
        return item.accept(this, ctx);
    }

    @Override
    public PsDto visit(DefaultValues item, AstContext ctx) {
        // For SQL DEFAULT VALUES
        return new PsDto("DEFAULT VALUES", List.of());
    }

    // Handle FromSource dispatch for FROM clause
    public PsDto visit(FromSource source, AstContext ctx) {
        return source.accept(this, ctx);
    }

    @Override
    public PsDto visit(Like expression, AstContext ctx) {
        throw new UnsupportedOperationException("Unsupported FromSource type: " + expression.getClass());
    }

    @Override
    public PsDto visit(IsNull expr, AstContext ctx) {
        PsDto inner = expr.getExpression().accept(this, ctx);
        return new PsDto(inner.sql() + " IS NULL", inner.parameters());
    }

    @Override
    public PsDto visit(IsNotNull expr, AstContext ctx) {
        PsDto inner = expr.getExpression().accept(this, ctx);
        return new PsDto(inner.sql() + " IS NOT NULL", inner.parameters());
    }
}
