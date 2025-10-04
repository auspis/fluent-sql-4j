package lan.tlab.r4j.sql.ast.visitor;

import lan.tlab.r4j.sql.ast.clause.conditional.having.Having;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.from.source.FromSubquery;
import lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.clause.groupby.GroupBy;
import lan.tlab.r4j.sql.ast.clause.orderby.OrderBy;
import lan.tlab.r4j.sql.ast.clause.orderby.Sorting;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.ExtractDatePart;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Mod;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Power;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Round;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.UnaryNumeric;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.CharLength;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.CharacterLength;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Concat;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.DataLength;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Left;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Length;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Replace;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Substring;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Trim;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.UnaryString;
import lan.tlab.r4j.sql.ast.expression.scalar.convert.Cast;
import lan.tlab.r4j.sql.ast.expression.set.ExceptExpression;
import lan.tlab.r4j.sql.ast.expression.set.IntersectExpression;
import lan.tlab.r4j.sql.ast.expression.set.NullSetExpression;
import lan.tlab.r4j.sql.ast.expression.set.UnionExpression;
import lan.tlab.r4j.sql.ast.identifier.Alias;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.Between;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.predicate.In;
import lan.tlab.r4j.sql.ast.predicate.IsNotNull;
import lan.tlab.r4j.sql.ast.predicate.IsNull;
import lan.tlab.r4j.sql.ast.predicate.Like;
import lan.tlab.r4j.sql.ast.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.predicate.logical.AndOr;
import lan.tlab.r4j.sql.ast.predicate.logical.Not;
import lan.tlab.r4j.sql.ast.statement.ddl.CreateTableStatement;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.Constraint.CheckConstraint;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.Constraint.DefaultConstraint;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.Constraint.ForeignKeyConstraint;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.Constraint.NotNullConstraint;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.Constraint.PrimaryKey;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.Constraint.UniqueConstraint;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.ParameterizedDataType;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.IndexDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ReferencesItem;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.statement.dml.DeleteStatement;
import lan.tlab.r4j.sql.ast.statement.dml.InsertStatement;
import lan.tlab.r4j.sql.ast.statement.dml.UpdateStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertSource;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertValues;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;

public interface Visitor<T> {

    // statements
    T visit(SelectStatement clause, AstContext ctx);

    T visit(InsertStatement insertStatement, AstContext ctx);

    T visit(UpdateStatement updateStatement, AstContext ctx);

    T visit(DeleteStatement deleteStatement, AstContext ctx);

    T visit(CreateTableStatement createTableStatement, AstContext ctx);

    // clause
    T visit(Select clause, AstContext ctx);

    T visit(AggregateCallProjection aggregationFunctionProjection, AstContext ctx);

    T visit(ScalarExpressionProjection scalarExpressionProjection, AstContext ctx);

    T visit(From clause, AstContext ctx);

    T visit(OnJoin onJoin, AstContext ctx);

    T visit(FromSubquery fromSubquery, AstContext ctx);

    T visit(Where clause, AstContext ctx);

    T visit(GroupBy clause, AstContext ctx);

    T visit(Having clause, AstContext ctx);

    T visit(OrderBy clause, AstContext ctx);

    T visit(Sorting sorting, AstContext ctx);

    T visit(Fetch clause, AstContext ctx);

    // boolean expressions
    T visit(NullPredicate expression, AstContext ctx);

    T visit(Between expression, AstContext ctx);

    T visit(Comparison expression, AstContext ctx);

    T visit(In expression, AstContext ctx);

    T visit(IsNotNull expression, AstContext ctx);

    T visit(IsNull expression, AstContext ctx);

    T visit(Like expression, AstContext ctx);

    // boolean expressions - logical
    T visit(AndOr expression, AstContext ctx);

    T visit(Not expression, AstContext ctx);

    // ordering expressions
    // ...

    // scalar expressions
    T visit(AggregateCall expression, AstContext ctx);

    T visit(ColumnReference expression, AstContext ctx);

    T visit(Interval interval, AstContext ctx);

    T visit(Literal<?> expression, AstContext ctx);

    T visit(ScalarSubquery expression, AstContext ctx);

    // scalar expressions - arithmetic
    T visit(BinaryArithmeticExpression expression, AstContext ctx);

    T visit(UnaryArithmeticExpression expression, AstContext ctx);

    // scalar expressions - function calls
    T visit(Cast functionCall, AstContext ctx);

    T visit(Concat functionCall, AstContext ctx);

    T visit(CurrentDate functionCall, AstContext ctx);

    T visit(CurrentDateTime functionCall, AstContext ctx);

    T visit(DateArithmetic functionCall, AstContext ctx);

    T visit(ExtractDatePart functionCall, AstContext ctx);

    T visit(Left functionCall, AstContext ctx);

    T visit(Length functionCall, AstContext ctx);

    T visit(CharLength functionCall, AstContext ctx);

    T visit(CharacterLength functionCall, AstContext ctx);

    T visit(DataLength functionCall, AstContext ctx);

    T visit(Mod functionCall, AstContext ctx);

    T visit(NullScalarExpression expression, AstContext ctx);

    T visit(Power functionCall, AstContext ctx);

    T visit(Replace functionCall, AstContext ctx);

    T visit(Round functionCall, AstContext ctx);

    T visit(Substring functionCall, AstContext ctx);

    T visit(Trim functionCall, AstContext ctx);

    T visit(UnaryNumeric functionCall, AstContext ctx);

    T visit(UnaryString functionCall, AstContext ctx);

    // set expressions
    T visit(NullSetExpression expression, AstContext ctx);

    T visit(ExceptExpression expression, AstContext ctx);

    T visit(IntersectExpression expression, AstContext ctx);

    T visit(UnionExpression expression, AstContext ctx);

    // sql items
    T visit(TableIdentifier item, AstContext ctx);

    T visit(Alias item, AstContext ctx);

    T visit(UpdateItem item, AstContext ctx);

    T visit(InsertValues item, AstContext ctx);

    T visit(InsertSource item, AstContext ctx);

    T visit(DefaultValues item, AstContext ctx);

    T visit(ReferencesItem item, AstContext ctx);

    T visit(TableDefinition item, AstContext ctx);

    T visit(ColumnDefinition item, AstContext ctx);

    T visit(SimpleDataType type, AstContext ctx);

    T visit(ParameterizedDataType type, AstContext ctx);

    T visit(PrimaryKey item, AstContext ctx);

    T visit(IndexDefinition indexDefinition, AstContext ctx);

    T visit(NotNullConstraint constraint, AstContext ctx);

    T visit(UniqueConstraint constraint, AstContext ctx);

    T visit(ForeignKeyConstraint constraint, AstContext ctx);

    T visit(CheckConstraint constraint, AstContext ctx);

    T visit(DefaultConstraint constraint, AstContext ctx);
}
