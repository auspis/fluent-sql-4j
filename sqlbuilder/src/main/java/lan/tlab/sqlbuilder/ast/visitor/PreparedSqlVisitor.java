package lan.tlab.sqlbuilder.ast.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.from.source.FromSource;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.Projection;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.bool.IsNotNull;
import lan.tlab.sqlbuilder.ast.expression.bool.IsNull;
import lan.tlab.sqlbuilder.ast.expression.bool.Like;
import lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.DefaultValues;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertSource;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertValues;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.statement.CreateTableStatement;
import lan.tlab.sqlbuilder.ast.statement.DeleteStatement;
import lan.tlab.sqlbuilder.ast.statement.InsertStatement;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.statement.UpdateStatement;

public class PreparedSqlVisitor implements SqlVisitor<PreparedSqlResult> {
    private final List<Object> parameters = new ArrayList<>();

    @Override
    public PreparedSqlResult visit(InsertStatement stmt) {
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
        if (stmt.getData() instanceof InsertValues values) {
            placeholders = values.getValueExpressions().stream()
                    .map(val -> {
                        if (val instanceof Literal<?> literal) {
                            parameters.add(literal.getValue());
                        } else {
                            parameters.add(null); // fallback
                        }
                        return "?";
                    })
                    .collect(Collectors.joining(", "));
        }
        String sql = "INSERT INTO \"" + tableName + "\" (" + columnList + ") VALUES (" + placeholders + ")";
        return new PreparedSqlResult(sql, List.copyOf(parameters));
    }

    @Override
    public PreparedSqlResult visit(SelectStatement stmt) {
        parameters.clear();
        // SELECT ...
        PreparedSqlResult selectResult = stmt.getSelect().accept(this);
        // FROM ...
        PreparedSqlResult fromResult = stmt.getFrom().accept(this);
        // WHERE ... (optional)
        PreparedSqlResult whereResult = null;
        String whereClause = "";
        if (stmt.getWhere() != null
                && stmt.getWhere().getCondition() != null
                && !(stmt.getWhere().getCondition()
                        instanceof lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression)) {
            whereResult = visit(stmt.getWhere());
            whereClause = " WHERE " + whereResult.sql();
        }
        // GROUP BY ... (optional)
        PreparedSqlResult groupByResult = null;
        String groupByClause = "";
        if (stmt.getGroupBy() != null
                && !stmt.getGroupBy().getGroupingExpressions().isEmpty()) {
            groupByResult = stmt.getGroupBy().accept(this);
            groupByClause = " GROUP BY " + groupByResult.sql();
        }
        // HAVING ... (optional, after GROUP BY)
        PreparedSqlResult havingResult = null;
        String havingClause = "";
        if (stmt.getHaving() != null && stmt.getHaving().getCondition() != null) {
            havingResult = visit(stmt.getHaving());
            if (!havingResult.sql().isBlank()) {
                havingClause = " HAVING " + havingResult.sql();
            }
        }
        // ORDER BY ... (optional)
        PreparedSqlResult orderByResult = null;
        String orderByClause = "";
        if (stmt.getOrderBy() != null && !stmt.getOrderBy().getSortings().isEmpty()) {
            orderByResult = stmt.getOrderBy().accept(this);
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
        return new PreparedSqlResult(sql, allParams);
    }

    @Override
    public PreparedSqlResult visit(Select select) {
        List<String> cols = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (select.getProjections().isEmpty()) {
            // No projections: SELECT *
            return new PreparedSqlResult("*", List.of());
        }
        for (Projection p : select.getProjections()) {
            PreparedSqlResult res = p.accept(this);
            cols.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", cols);
        return new PreparedSqlResult(sql, params);
    }

    @Override
    public PreparedSqlResult visit(Table table) {
        String sql = "\"" + table.getName() + "\"";
        String alias = table.getAs() != null ? table.getAs().getName() : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS " + alias;
        }
        return new PreparedSqlResult(sql, List.of());
    }

    // RIMOSSO: private boolean forceQualification = false;

    // Overload privato per qualificazione colonne
    private PreparedSqlResult visit(ColumnReference col, boolean qualify) {
        String sql;
        if (qualify && col.getTable() != null && !col.getTable().isBlank()) {
            sql = "\"" + col.getTable() + "\".\"" + col.getColumn() + "\"";
        } else {
            sql = "\"" + col.getColumn() + "\"";
        }
        return new PreparedSqlResult(sql, List.of());
    }

    private PreparedSqlResult visit(BooleanExpression expr, boolean qualify) {
        switch (expr) {
            case Comparison cmp -> {
                return visitComparisonWithQualify(cmp, qualify);
            }
            case lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr andOr -> {
                List<String> sqlParts = new ArrayList<>();
                List<Object> params = new ArrayList<>();
                for (BooleanExpression op : andOr.getOperands()) {
                    PreparedSqlResult res = visit(op, qualify);
                    sqlParts.add("(" + res.sql() + ")");
                    params.addAll(res.parameters());
                }
                String operator =
                        andOr.getOperator() == lan.tlab.sqlbuilder.ast.expression.bool.logical.LogicalOperator.AND
                                ? "AND"
                                : "OR";
                return new PreparedSqlResult(String.join(" " + operator + " ", sqlParts), params);
            }
            case lan.tlab.sqlbuilder.ast.expression.bool.logical.Not not -> {
                PreparedSqlResult inner = visit(not.getExpression(), qualify);
                return new PreparedSqlResult("NOT (" + inner.sql() + ")", inner.parameters());
            }
            case null, default -> {
                // Fallback: visita normale
                return expr.accept(this);
            }
        }
    }

    private PreparedSqlResult visitComparisonWithQualify(Comparison cmp, boolean qualify) {
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
        String lhs;
        if (cmp.getLhs() instanceof ColumnReference colLhs) {
            lhs = visit(colLhs, qualify).sql();
        } else {
            lhs = cmp.getLhs().accept(this).sql();
        }
        String rhsSql;
        List<Object> params = new ArrayList<>();
        if (cmp.getRhs() instanceof ColumnReference colRhs) {
            rhsSql = visit(colRhs, qualify).sql();
        } else {
            PreparedSqlResult rhsResult = cmp.getRhs().accept(this);
            rhsSql = rhsResult.sql();
            params.addAll(rhsResult.parameters());
        }
        return new PreparedSqlResult(lhs + " " + operator + " " + rhsSql, params);
    }

    @Override
    public PreparedSqlResult visit(ColumnReference col) {
        return visit(col, false);
    }

    @Override
    public PreparedSqlResult visit(Comparison cmp) {
        // Default: non qualificare mai, tranne se richiesto esplicitamente
        return visitComparisonWithQualify(cmp, false);
    }

    @Override
    public PreparedSqlResult visit(Where where) {
        BooleanExpression cond = where.getCondition();
        if (cond instanceof NullBooleanExpression) {
            return new PreparedSqlResult("", List.of());
        }
        return cond.accept(this);
    }

    @Override
    public PreparedSqlResult visit(Literal<?> literal) {
        parameters.add(literal.getValue());
        return new PreparedSqlResult("?", List.of(literal.getValue()));
    }

    @Override
    public PreparedSqlResult visit(UpdateStatement updateStatement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(DeleteStatement deleteStatement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(CreateTableStatement createTableStatement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(AggregationFunctionProjection aggregationFunctionProjection) {
        // The AggregationFunctionProjection wraps an AggregateCall (e.g., COUNT, SUM, etc.)
        var expr = aggregationFunctionProjection.getExpression();
        PreparedSqlResult exprResult = expr.accept(this);
        String sql = exprResult.sql();
        // Handle alias if present
        String alias = aggregationFunctionProjection.getAs() != null
                ? aggregationFunctionProjection.getAs().getName()
                : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS \"" + alias + "\"";
        }
        return new PreparedSqlResult(sql, exprResult.parameters());
    }

    @Override
    public PreparedSqlResult visit(ScalarExpressionProjection scalarExpressionProjection) {
        // Visit the underlying scalar expression
        var expr = scalarExpressionProjection.getExpression();
        PreparedSqlResult exprResult = expr.accept(this);
        String sql = exprResult.sql();
        // Handle alias if present
        String alias = scalarExpressionProjection.getAs() != null
                ? scalarExpressionProjection.getAs().getName()
                : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS \"" + alias + "\"";
        }
        return new PreparedSqlResult(sql, exprResult.parameters());
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.from.From clause) {
        List<String> sqlParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var source : clause.getSources()) {
            PreparedSqlResult res = visit(source);
            sqlParts.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", sqlParts);
        return new PreparedSqlResult(sql, params);
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin join) {
        // Visit left and right sources
        PreparedSqlResult leftResult = visit(join.getLeft());
        PreparedSqlResult rightResult = visit(join.getRight());
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
                // Passa il flag di qualificazione alle Comparison e sotto-espressioni
                PreparedSqlResult onResult = visit(join.getOnCondition(), true);
                sql.append(" ON ").append(onResult.sql());
                params.addAll(onResult.parameters());
            }
        }
        return new PreparedSqlResult(sql.toString(), params);
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.from.source.FromSubquery fromSubquery) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy clause) {
        List<String> exprSqls = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var expr : clause.getGroupingExpressions()) {
            PreparedSqlResult res = expr.accept(this);
            exprSqls.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", exprSqls);
        return new PreparedSqlResult(sql, params);
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having clause) {
        if (clause.getCondition() == null || clause.getCondition() instanceof NullBooleanExpression) {
            return new PreparedSqlResult("", List.of());
        }
        return clause.getCondition().accept(this);
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy clause) {
        List<String> sqlParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var sorting : clause.getSortings()) {
            PreparedSqlResult res = visit(sorting);
            sqlParts.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", sqlParts);
        return new PreparedSqlResult(sql, params);
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.orderby.Sorting sorting) {
        PreparedSqlResult exprResult = sorting.getExpression().accept(this);
        String sql = exprResult.sql();
        String order = sorting.getSortOrder().getSqlKeyword();
        if (!order.isEmpty()) {
            sql += " " + order;
        }
        return new PreparedSqlResult(sql, exprResult.parameters());
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.pagination.Pagination clause) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.bool.Between expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.bool.In expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr expression) {
        String operator =
                expression.getOperator() == lan.tlab.sqlbuilder.ast.expression.bool.logical.LogicalOperator.AND
                        ? "AND"
                        : "OR";
        List<String> sqlParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (BooleanExpression expr : expression.getOperands()) {
            PreparedSqlResult res = expr.accept(this);
            sqlParts.add("(" + res.sql() + ")");
            params.addAll(res.parameters());
        }
        String sql = String.join(" " + operator + " ", sqlParts);
        return new PreparedSqlResult(sql, params);
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.bool.logical.Not expression) {
        PreparedSqlResult inner = expression.getExpression().accept(this);
        String sql = "NOT (" + inner.sql() + ")";
        return new PreparedSqlResult(sql, inner.parameters());
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.scalar.convert.Cast functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Concat functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDate functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDateTime functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.DateArithmetic functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.ExtractDatePart functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Left functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Length functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.CharLength functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.CharacterLength functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.DataLength functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Mod functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.scalar.NullScalarExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Power functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Replace functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Round functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Substring functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Trim functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.UnaryNumeric functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.UnaryString functionCall) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.set.NullSetExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.set.ExceptExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.set.IntersectExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.set.UnionExpression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.item.As item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.item.UpdateItem item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.ReferencesItem item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.SimpleDataType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.ParameterizedDataType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.Index index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint constraint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.UniqueConstraint constraint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.ForeignKeyConstraint constraint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.CheckConstraint constraint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.DefaultConstraint constraint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.scalar.ScalarSubquery expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.interval.Interval interval) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall expression) {
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
                PreparedSqlResult argResult =
                        ((lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression) argument).accept(this);
                argumentSql = argResult.sql();
                params.addAll(argResult.parameters());
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("AggregateCall reflection failed", e);
        }
        String sql = functionName + "(" + argumentSql + ")";
        return new PreparedSqlResult(sql, params);
    }

    @Override
    public PreparedSqlResult visit(InsertValues item) {
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
        return new PreparedSqlResult(sql, params);
    }

    @Override
    public PreparedSqlResult visit(InsertSource item) {
        // InsertSource is a parent type, delegate to the actual subtype
        return item.accept(this);
    }

    @Override
    public PreparedSqlResult visit(DefaultValues item) {
        // For SQL DEFAULT VALUES
        return new PreparedSqlResult("DEFAULT VALUES", List.of());
    }

    // Handle FromSource dispatch for FROM clause
    public PreparedSqlResult visit(FromSource source) {
        return source.accept(this);
    }

    @Override
    public PreparedSqlResult visit(Like expression) {
        throw new UnsupportedOperationException("Unsupported FromSource type: " + expression.getClass());
    }

    @Override
    public PreparedSqlResult visit(IsNull expr) {
        PreparedSqlResult inner = expr.getExpression().accept(this);
        return new PreparedSqlResult(inner.sql() + " IS NULL", inner.parameters());
    }

    @Override
    public PreparedSqlResult visit(IsNotNull expr) {
        PreparedSqlResult inner = expr.getExpression().accept(this);
        return new PreparedSqlResult(inner.sql() + " IS NOT NULL", inner.parameters());
    }
}
