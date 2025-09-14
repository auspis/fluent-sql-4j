package lan.tlab.sqlbuilder.ast.visitor;

import lan.tlab.sqlbuilder.ast.clause.conditional.having.Having;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.from.From;
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
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.bool.In;
import lan.tlab.sqlbuilder.ast.expression.bool.IsNotNull;
import lan.tlab.sqlbuilder.ast.expression.bool.IsNull;
import lan.tlab.sqlbuilder.ast.expression.bool.Like;
import lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.Not;
import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.DefaultValues;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertSource;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertValues;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.item.UpdateItem;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.CheckConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.DefaultConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.ForeignKeyConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.UniqueConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.ParameterizedDataType;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.SimpleDataType;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Index;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ReferencesItem;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.NullScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarSubquery;
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

public interface SqlVisitor<T> {

    // statements
    T visit(SelectStatement clause, AstContext ctx);

    T visit(InsertStatement insertStatement);

    T visit(UpdateStatement updateStatement);

    T visit(DeleteStatement deleteStatement);

    T visit(CreateTableStatement createTableStatement);

    // clause
    T visit(Select clause, AstContext ctx);

    T visit(AggregationFunctionProjection aggregationFunctionProjection, AstContext ctx);

    T visit(ScalarExpressionProjection scalarExpressionProjection, AstContext ctx);

    T visit(From clause, AstContext ctx);

    T visit(OnJoin onJoin, AstContext ctx);

    T visit(FromSubquery fromSubquery);

    T visit(Where clause, AstContext ctx);

    T visit(GroupBy clause, AstContext ctx);

    T visit(Having clause, AstContext ctx);

    T visit(OrderBy clause, AstContext ctx);

    T visit(Sorting sorting, AstContext ctx);

    T visit(Pagination clause, AstContext ctx);

    // boolean expressions
    T visit(NullBooleanExpression expression);

    T visit(Between expression);

    T visit(Comparison expression, AstContext ctx);

    T visit(In expression);

    T visit(IsNotNull expression, AstContext ctx);

    T visit(IsNull expression, AstContext ctx);

    T visit(Like expression);

    // boolean expressions - logical
    T visit(AndOr expression, AstContext ctx);

    T visit(Not expression, AstContext ctx);

    // ordering expressions
    // ...

    // scalar expressions
    T visit(AggregateCall expression, AstContext ctx);

    T visit(ColumnReference expression);

    T visit(Interval interval);

    T visit(Literal<?> expression);

    T visit(ScalarSubquery expression);

    // scalar expressions - arithmetic
    T visit(BinaryArithmeticExpression expression);

    T visit(UnaryArithmeticExpression expression);

    // scalar expressions - function calls
    T visit(Cast functionCall);

    T visit(Concat functionCall);

    T visit(CurrentDate functionCall);

    T visit(CurrentDateTime functionCall);

    T visit(DateArithmetic functionCall);

    T visit(ExtractDatePart functionCall);

    T visit(Left functionCall);

    T visit(Length functionCall);

    T visit(CharLength functionCall);

    T visit(CharacterLength functionCall);

    T visit(DataLength functionCall);

    T visit(Mod functionCall);

    T visit(NullScalarExpression expression);

    T visit(Power functionCall);

    T visit(Replace functionCall);

    T visit(Round functionCall);

    T visit(Substring functionCall);

    T visit(Trim functionCall);

    T visit(UnaryNumeric functionCall);

    T visit(UnaryString functionCall);

    // set expressions
    T visit(NullSetExpression expression);

    T visit(ExceptExpression expression);

    T visit(IntersectExpression expression);

    T visit(UnionExpression expression);

    // sql items
    T visit(Table item);

    T visit(As item);

    T visit(UpdateItem item);

    T visit(InsertValues item);

    T visit(InsertSource item, AstContext ctx);

    T visit(DefaultValues item);

    T visit(ReferencesItem item);

    T visit(TableDefinition item);

    T visit(ColumnDefinition item);

    T visit(SimpleDataType type);

    T visit(ParameterizedDataType type);

    T visit(PrimaryKey item);

    T visit(Index index);

    T visit(NotNullConstraint constraint);

    T visit(UniqueConstraint constraint);

    T visit(ForeignKeyConstraint constraint);

    T visit(CheckConstraint constraint);

    T visit(DefaultConstraint constraint);
}
