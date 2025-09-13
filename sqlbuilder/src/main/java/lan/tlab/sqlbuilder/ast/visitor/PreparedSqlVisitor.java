package lan.tlab.sqlbuilder.ast.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.Projection;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
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
        String selectClause = stmt.getSelect().accept(this).sql();
        // FROM ...
        String fromClause = stmt.getFrom().accept(this).sql();
        // WHERE ... (optional)
        String whereClause = "";
        if (stmt.getWhere() != null
                && stmt.getWhere().getCondition() != null
                && !(stmt.getWhere().getCondition()
                        instanceof lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression)) {
            PreparedSqlResult whereResult = visit(stmt.getWhere());
            whereClause = " WHERE " + whereResult.sql();
        }
        String sql = "SELECT " + selectClause + " FROM " + fromClause + whereClause;
        return new PreparedSqlResult(sql, List.copyOf(parameters));
    }

    @Override
    public PreparedSqlResult visit(Select select) {
        // Only support ColumnReference projections for now
        List<String> cols = new ArrayList<>();
        for (Projection p : select.getProjections()) {
            cols.add(p.accept(this).sql());
        }
        String sql = String.join(", ", cols);
        return new PreparedSqlResult(sql, List.of());
    }

    @Override
    public PreparedSqlResult visit(Table table) {
        return new PreparedSqlResult("\"" + table.getName() + "\"", List.of());
    }

    @Override
    public PreparedSqlResult visit(ColumnReference col) {
        return new PreparedSqlResult("\"" + col.getColumn() + "\"", List.of());
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
    public PreparedSqlResult visit(Comparison cmp) {
        // Only support EQUALS for now
        if (cmp.getOperator() != Comparison.ComparisonOperator.EQUALS) {
            throw new UnsupportedOperationException("Only EQUALS operator is supported in WHERE");
        }
        String lhs = visit((ColumnReference) cmp.getLhs()).sql();
        PreparedSqlResult rhsResult = visit((Literal<?>) cmp.getRhs());
        return new PreparedSqlResult(lhs + " = ?", rhsResult.parameters());
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin onJoin) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.from.source.FromSubquery fromSubquery) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy clause) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having clause) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy clause) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.orderby.Sorting sorting) {
        throw new UnsupportedOperationException();
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
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.bool.IsNotNull expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.bool.IsNull expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.bool.Like expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.bool.logical.Not expression) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(InsertValues item) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PreparedSqlResult visit(InsertSource item) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PreparedSqlResult visit(DefaultValues item) {
        // TODO Auto-generated method stub
        return null;
    }
}
