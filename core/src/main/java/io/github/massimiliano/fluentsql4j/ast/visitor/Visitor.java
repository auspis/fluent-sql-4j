package io.github.massimiliano.fluentsql4j.ast.visitor;

import io.github.massimiliano.fluentsql4j.ast.core.expression.aggregate.AggregateCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.CurrentDate;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.CurrentDateTime;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.DateArithmetic;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.ExtractDatePart;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.Interval;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.JsonExists;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.JsonQuery;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.JsonValue;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.number.Mod;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.number.Power;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.number.Round;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.number.UnaryNumeric;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.CharLength;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.CharacterLength;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.Concat;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.Left;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.Length;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.Replace;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.Substring;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.Trim;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.UnaryString;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Cast;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.NullScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarSubquery;
import io.github.massimiliano.fluentsql4j.ast.core.expression.set.AliasedTableExpression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.set.ExceptExpression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.set.IntersectExpression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.set.NullSetExpression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.set.UnionExpression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.window.DenseRank;
import io.github.massimiliano.fluentsql4j.ast.core.expression.window.Lag;
import io.github.massimiliano.fluentsql4j.ast.core.expression.window.Lead;
import io.github.massimiliano.fluentsql4j.ast.core.expression.window.Ntile;
import io.github.massimiliano.fluentsql4j.ast.core.expression.window.OverClause;
import io.github.massimiliano.fluentsql4j.ast.core.expression.window.Rank;
import io.github.massimiliano.fluentsql4j.ast.core.expression.window.RowNumber;
import io.github.massimiliano.fluentsql4j.ast.core.identifier.Alias;
import io.github.massimiliano.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.AndOr;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Between;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Comparison;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.In;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.IsNotNull;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.IsNull;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Like;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Not;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ColumnDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.DataType.ParameterizedDataType;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.DataType.SimpleDataType;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.IndexDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ReferencesItem;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.TableDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.statement.CreateTableStatement;
import io.github.massimiliano.fluentsql4j.ast.dml.component.InsertData.DefaultValues;
import io.github.massimiliano.fluentsql4j.ast.dml.component.InsertData.InsertSource;
import io.github.massimiliano.fluentsql4j.ast.dml.component.InsertData.InsertValues;
import io.github.massimiliano.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedDelete;
import io.github.massimiliano.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedUpdate;
import io.github.massimiliano.fluentsql4j.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import io.github.massimiliano.fluentsql4j.ast.dml.component.MergeUsing;
import io.github.massimiliano.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.massimiliano.fluentsql4j.ast.dml.statement.DeleteStatement;
import io.github.massimiliano.fluentsql4j.ast.dml.statement.InsertStatement;
import io.github.massimiliano.fluentsql4j.ast.dml.statement.MergeStatement;
import io.github.massimiliano.fluentsql4j.ast.dml.statement.UpdateStatement;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Fetch;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.From;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.GroupBy;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Having;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.OrderBy;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Select;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Sorting;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Where;
import io.github.massimiliano.fluentsql4j.ast.dql.projection.AggregateCallProjection;
import io.github.massimiliano.fluentsql4j.ast.dql.projection.AggregateExpressionProjection;
import io.github.massimiliano.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.massimiliano.fluentsql4j.ast.dql.source.FromSubquery;
import io.github.massimiliano.fluentsql4j.ast.dql.source.join.OnJoin;
import io.github.massimiliano.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.EscapeStrategy;

public interface Visitor<T> {

    EscapeStrategy getEscapeStrategy();
    // statements
    T visit(SelectStatement clause, AstContext ctx);

    T visit(InsertStatement insertStatement, AstContext ctx);

    T visit(UpdateStatement updateStatement, AstContext ctx);

    T visit(DeleteStatement deleteStatement, AstContext ctx);

    T visit(MergeStatement mergeStatement, AstContext ctx);

    T visit(CreateTableStatement createTableStatement, AstContext ctx);

    // clause
    T visit(Select clause, AstContext ctx);

    T visit(AggregateExpressionProjection aggregateExpressionProjection, AstContext ctx);

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

    T visit(Mod functionCall, AstContext ctx);

    T visit(NullScalarExpression expression, AstContext ctx);

    T visit(Power functionCall, AstContext ctx);

    T visit(Replace functionCall, AstContext ctx);

    T visit(Round functionCall, AstContext ctx);

    T visit(Substring functionCall, AstContext ctx);

    T visit(Trim functionCall, AstContext ctx);

    T visit(UnaryNumeric functionCall, AstContext ctx);

    T visit(UnaryString functionCall, AstContext ctx);

    T visit(JsonExists functionCall, AstContext ctx);

    T visit(JsonQuery functionCall, AstContext ctx);

    T visit(JsonValue functionCall, AstContext ctx);

    // set expressions
    T visit(NullSetExpression expression, AstContext ctx);

    T visit(ExceptExpression expression, AstContext ctx);

    T visit(IntersectExpression expression, AstContext ctx);

    T visit(UnionExpression expression, AstContext ctx);

    // sql items
    T visit(TableIdentifier item, AstContext ctx);

    T visit(AliasedTableExpression item, AstContext ctx);

    T visit(Alias item, AstContext ctx);

    T visit(UpdateItem item, AstContext ctx);

    T visit(InsertValues item, AstContext ctx);

    T visit(InsertSource item, AstContext ctx);

    T visit(DefaultValues item, AstContext ctx);

    T visit(MergeUsing item, AstContext ctx);

    T visit(WhenMatchedUpdate item, AstContext ctx);

    T visit(WhenMatchedDelete item, AstContext ctx);

    T visit(WhenNotMatchedInsert item, AstContext ctx);

    T visit(ReferencesItem item, AstContext ctx);

    T visit(TableDefinition item, AstContext ctx);

    T visit(ColumnDefinition item, AstContext ctx);

    T visit(SimpleDataType type, AstContext ctx);

    T visit(ParameterizedDataType type, AstContext ctx);

    T visit(PrimaryKeyDefinition constraintDefinition, AstContext ctx);

    T visit(IndexDefinition constraintDefinition, AstContext ctx);

    T visit(NotNullConstraintDefinition constraintDefinition, AstContext ctx);

    T visit(UniqueConstraintDefinition constraintDefinition, AstContext ctx);

    T visit(ForeignKeyConstraintDefinition constraintDefinition, AstContext ctx);

    T visit(CheckConstraintDefinition constraintDefinition, AstContext ctx);

    T visit(DefaultConstraintDefinition constraintDefinition, AstContext ctx);

    // window functions
    T visit(RowNumber functionCall, AstContext ctx);

    T visit(Rank functionCall, AstContext ctx);

    T visit(DenseRank functionCall, AstContext ctx);

    T visit(Ntile functionCall, AstContext ctx);

    T visit(Lag functionCall, AstContext ctx);

    T visit(Lead functionCall, AstContext ctx);

    T visit(OverClause overClause, AstContext ctx);

    T visit(CustomFunctionCall functionCall, AstContext ctx);
}
