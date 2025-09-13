package lan.tlab.sqlbuilder.ast.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
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

    // All other methods throw UnsupportedOperationException
    @Override
    public PreparedSqlResult visit(Table item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(ColumnReference expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(Literal<?> expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(InsertValues item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(DefaultValues item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(InsertSource item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(SelectStatement clause) {
        throw new UnsupportedOperationException();
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
    public PreparedSqlResult visit(Select clause) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(AggregationFunctionProjection aggregationFunctionProjection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedSqlResult visit(
            lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection scalarExpressionProjection) {
        throw new UnsupportedOperationException();
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
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.clause.conditional.where.Where clause) {
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
    public PreparedSqlResult visit(lan.tlab.sqlbuilder.ast.expression.bool.Comparison expression) {
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
}
