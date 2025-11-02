package lan.tlab.r4j.sql.ast.visitor.ps;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.conditional.having.Having;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.from.source.FromSource;
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
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonExists;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonQuery;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonValue;
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
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.DenseRank;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Lag;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Lead;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Ntile;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.OverClause;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Rank;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.RowNumber;
import lan.tlab.r4j.sql.ast.expression.scalar.convert.Cast;
import lan.tlab.r4j.sql.ast.expression.set.AliasedTableExpression;
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
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.ParameterizedDataType;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.IndexDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ReferencesItem;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.statement.dml.DeleteStatement;
import lan.tlab.r4j.sql.ast.statement.dml.InsertStatement;
import lan.tlab.r4j.sql.ast.statement.dml.MergeStatement;
import lan.tlab.r4j.sql.ast.statement.dml.UpdateStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertSource;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertValues;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenMatchedDelete;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeUsing;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.AggregateCallPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.AggregationFunctionProjectionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.AndOrPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.AsPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.BetweenPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.BinaryArithmeticExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CastPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CharLengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CharacterLengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CheckConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ColumnDefinitionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ColumnReferencePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ComparisonPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ConcatPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CreateTableStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CurrentDatePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CurrentDateTimePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DataLengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DateArithmeticPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DefaultConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DefaultValuesPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DeleteStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DenseRankPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ExceptExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ExtractDatePartPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.FetchPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ForeignKeyConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.FromClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.FromSubqueryPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.GroupByClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.HavingClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.InPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IndexDefinitionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.InsertSourcePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.InsertStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.InsertValuesPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IntersectExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IntervalPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IsNotNullPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IsNullPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.JsonExistsPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.JsonQueryPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.JsonValuePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LagPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LeadPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LeftPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LikePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LiteralPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.MergeStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ModPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NotNullConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NotPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NtilePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NullPredicatePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NullScalarExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NullSetExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.OnJoinPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.OrderByClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.OverClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ParameterizedDataTypePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.PowerPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.PrimaryKeyPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.RankPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ReferencesItemPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ReplacePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.RoundPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.RowNumberPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ScalarExpressionProjectionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ScalarSubqueryPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SelectClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SelectStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SimpleDataTypePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SortingPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SubstringPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.TableDefinitionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.TablePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.TrimPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UnaryArithmeticExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UnaryNumericPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UnaryStringPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UnionExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UniqueConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UpdateItemPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UpdateStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.WhereClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlAggregateCallPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlAggregationFunctionProjectionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlAndOrPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlAsPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlBetweenPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlBinaryArithmeticExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlCastPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlCharLengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlCharacterLengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlCheckConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlColumnDefinitionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlColumnReferencePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlComparisonPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlConcatPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlCreateTableStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlCurrentDatePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlCurrentDateTimePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlDataLengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlDateArithmeticPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlDefaultConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlDefaultValuesPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlDeleteStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlDenseRankPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlExceptExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlExtractDatePartPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlForeignKeyConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlFromClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlFromSubqueryPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlGroupByClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlHavingClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlInPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlIndexDefinitionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlInsertSourcePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlInsertStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlInsertValuesPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlIntersectExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlIntervalPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlIsNotNullPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlIsNullPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlJsonExistsPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlJsonQueryPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlJsonValuePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlLagPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlLeadPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlLeftPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlLengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlLikePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlLiteralPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlMergeStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlModPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlNotNullConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlNotPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlNtilePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlNullPredicatePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlNullScalarExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlNullSetExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlOnJoinPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlOrderByClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlOverClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlPaginationPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlParameterizedDataTypePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlPowerPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlPrimaryKeyPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlRankPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlReferencesItemPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlReplacePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlRoundPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlRowNumberPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlScalarExpressionProjectionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlScalarSubqueryPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlSelectClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlSelectStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlSimpleDataTypePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlSortingPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlSubstringPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlTableDefinitionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlTablePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlTrimPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlUnaryArithmeticExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlUnaryNumericPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlUnaryStringPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlUnionExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlUniqueConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlUpdateItemPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlUpdateStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.StandardSqlWhereClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PreparedStatementRenderer implements Visitor<PsDto> {
    @Getter
    @Default
    private final EscapeStrategy escapeStrategy = EscapeStrategy.standard();

    @Getter
    @Default
    private final SqlRenderer sqlRenderer = SqlRenderer.builder().build();

    @Default
    private final SelectClausePsStrategy selectClauseStrategy = new StandardSqlSelectClausePsStrategy();

    @Default
    private final FromClausePsStrategy fromClauseStrategy = new StandardSqlFromClausePsStrategy();

    @Default
    private final WhereClausePsStrategy whereClauseStrategy = new StandardSqlWhereClausePsStrategy();

    @Default
    private final GroupByClausePsStrategy groupByClauseStrategy = new StandardSqlGroupByClausePsStrategy();

    @Default
    private final HavingClausePsStrategy havingClauseStrategy = new StandardSqlHavingClausePsStrategy();

    @Default
    private final OrderByClausePsStrategy orderByClauseStrategy = new StandardSqlOrderByClausePsStrategy();

    @Default
    private final TablePsStrategy tableStrategy = new StandardSqlTablePsStrategy();

    @Default
    private final ColumnReferencePsStrategy columnReferenceStrategy = new StandardSqlColumnReferencePsStrategy();

    @Default
    private final ComparisonPsStrategy comparisonStrategy = new StandardSqlComparisonPsStrategy();

    @Default
    private final LiteralPsStrategy literalStrategy = new StandardSqlLiteralPsStrategy();

    @Default
    private final SortingPsStrategy sortingStrategy = new StandardSqlSortingPsStrategy();

    @Default
    private final OnJoinPsStrategy onJoinStrategy = new StandardSqlOnJoinPsStrategy();

    @Default
    private final AndOrPsStrategy andOrStrategy = new StandardSqlAndOrPsStrategy();

    @Default
    private final NotPsStrategy notStrategy = new StandardSqlNotPsStrategy();

    @Default
    private final AggregationFunctionProjectionPsStrategy aggregationFunctionProjectionStrategy =
            new StandardSqlAggregationFunctionProjectionPsStrategy();

    @Default
    private final ScalarExpressionProjectionPsStrategy scalarExpressionProjectionStrategy =
            new StandardSqlScalarExpressionProjectionPsStrategy();

    @Default
    private final AggregateCallPsStrategy aggregateCallStrategy = new StandardSqlAggregateCallPsStrategy();

    @Default
    private final InsertValuesPsStrategy insertValuesStrategy = new StandardSqlInsertValuesPsStrategy();

    @Default
    private final InsertSourcePsStrategy insertSourceStrategy = new StandardSqlInsertSourcePsStrategy();

    @Default
    private final DefaultValuesPsStrategy defaultValuesStrategy = new StandardSqlDefaultValuesPsStrategy();

    @Default
    private final IsNullPsStrategy isNullStrategy = new StandardSqlIsNullPsStrategy();

    @Default
    private final IsNotNullPsStrategy isNotNullStrategy = new StandardSqlIsNotNullPsStrategy();

    @Default
    private final InsertStatementPsStrategy insertStatementStrategy = new StandardSqlInsertStatementPsStrategy();

    @Default
    private final SelectStatementPsStrategy selectStatementStrategy = new StandardSqlSelectStatementPsStrategy();

    @Default
    private final FetchPsStrategy paginationStrategy = new StandardSqlPaginationPsStrategy();

    @Default
    private final LikePsStrategy likeStrategy = new StandardSqlLikePsStrategy();

    @Default
    private final UnionExpressionPsStrategy unionExpressionStrategy = new StandardSqlUnionExpressionPsStrategy();

    @Default
    private final BetweenPsStrategy betweenStrategy = new StandardSqlBetweenPsStrategy();

    @Default
    private final BinaryArithmeticExpressionPsStrategy binaryArithmeticExpressionStrategy =
            new StandardSqlBinaryArithmeticExpressionPsStrategy();

    @Default
    private final UnaryArithmeticExpressionPsStrategy unaryArithmeticExpressionStrategy =
            new StandardSqlUnaryArithmeticExpressionPsStrategy();

    @Default
    private final CastPsStrategy castStrategy = new StandardSqlCastPsStrategy();

    @Default
    private final ConcatPsStrategy concatStrategy = new StandardSqlConcatPsStrategy();

    @Default
    private final CurrentDatePsStrategy currentDateStrategy = new StandardSqlCurrentDatePsStrategy();

    @Default
    private final CurrentDateTimePsStrategy currentDateTimeStrategy = new StandardSqlCurrentDateTimePsStrategy();

    @Default
    private final DateArithmeticPsStrategy dateArithmeticStrategy = new StandardSqlDateArithmeticPsStrategy();

    @Default
    private final ExceptExpressionPsStrategy exceptExpressionStrategy = new StandardSqlExceptExpressionPsStrategy();

    @Default
    private final ExtractDatePartPsStrategy extractDatePartStrategy = new StandardSqlExtractDatePartPsStrategy();

    @Default
    private final IntersectExpressionPsStrategy intersectExpressionStrategy =
            new StandardSqlIntersectExpressionPsStrategy();

    @Default
    private final IntervalPsStrategy intervalStrategy = new StandardSqlIntervalPsStrategy();

    @Default
    private final LeftPsStrategy leftStrategy = new StandardSqlLeftPsStrategy();

    @Default
    private final LengthPsStrategy lengthStrategy = new StandardSqlLengthPsStrategy();

    @Default
    private final ModPsStrategy modStrategy = new StandardSqlModPsStrategy();

    @Default
    private final NullScalarExpressionPsStrategy nullScalarExpressionStrategy =
            new StandardSqlNullScalarExpressionPsStrategy();

    @Default
    private final NullSetExpressionPsStrategy nullSetExpressionStrategy = new StandardSqlNullSetExpressionPsStrategy();

    @Default
    private final PowerPsStrategy powerStrategy = new StandardSqlPowerPsStrategy();

    @Default
    private final ReplacePsStrategy replaceStrategy = new StandardSqlReplacePsStrategy();

    @Default
    private final ReferencesItemPsStrategy referencesItemStrategy = new StandardSqlReferencesItemPsStrategy();

    @Default
    private final TableDefinitionPsStrategy tableDefinitionStrategy = new StandardSqlTableDefinitionPsStrategy();

    @Default
    private final ColumnDefinitionPsStrategy columnDefinitionStrategy = new StandardSqlColumnDefinitionPsStrategy();

    @Default
    private final SimpleDataTypePsStrategy simpleDataTypeStrategy = new StandardSqlSimpleDataTypePsStrategy();

    @Default
    private final ParameterizedDataTypePsStrategy parameterizedDataTypeStrategy =
            new StandardSqlParameterizedDataTypePsStrategy();

    @Default
    private final PrimaryKeyPsStrategy primaryKeyDefinitionStrategy = new StandardSqlPrimaryKeyPsStrategy();

    @Default
    private final IndexDefinitionPsStrategy indexDefinitionStrategy = new StandardSqlIndexDefinitionPsStrategy();

    @Default
    private final NotNullConstraintPsStrategy notNullConstraintDefinitionStrategy =
            new StandardSqlNotNullConstraintPsStrategy();

    @Default
    private final UniqueConstraintPsStrategy uniqueConstraintStrategy = new StandardSqlUniqueConstraintPsStrategy();

    @Default
    private final ForeignKeyConstraintPsStrategy foreignKeyConstraintStrategy =
            new StandardSqlForeignKeyConstraintPsStrategy();

    @Default
    private final CheckConstraintPsStrategy checkConstraintStrategy = new StandardSqlCheckConstraintPsStrategy();

    @Default
    private final DefaultConstraintPsStrategy defaultConstraintStrategy = new StandardSqlDefaultConstraintPsStrategy();

    @Default
    private final RoundPsStrategy roundStrategy = new StandardSqlRoundPsStrategy();

    @Default
    private final SubstringPsStrategy substringStrategy = new StandardSqlSubstringPsStrategy();

    @Default
    private final TrimPsStrategy trimStrategy = new StandardSqlTrimPsStrategy();

    @Default
    private final UnaryNumericPsStrategy unaryNumericStrategy = new StandardSqlUnaryNumericPsStrategy();

    @Default
    private final UnaryStringPsStrategy unaryStringStrategy = new StandardSqlUnaryStringPsStrategy();

    @Default
    private final JsonExistsPsStrategy jsonExistsStrategy = new StandardSqlJsonExistsPsStrategy();

    @Default
    private final JsonQueryPsStrategy jsonQueryStrategy = new StandardSqlJsonQueryPsStrategy();

    @Default
    private final JsonValuePsStrategy jsonValueStrategy = new StandardSqlJsonValuePsStrategy();

    @Default
    private final ScalarSubqueryPsStrategy scalarSubqueryStrategy = new StandardSqlScalarSubqueryPsStrategy();

    @Default
    private final CreateTableStatementPsStrategy createTableStatementStrategy =
            new StandardSqlCreateTableStatementPsStrategy();

    @Default
    private final CharLengthPsStrategy charLengthStrategy = new StandardSqlCharLengthPsStrategy();

    @Default
    private final CharacterLengthPsStrategy characterLengthStrategy = new StandardSqlCharacterLengthPsStrategy();

    @Default
    private final DataLengthPsStrategy dataLengthStrategy = new StandardSqlDataLengthPsStrategy();

    @Default
    private final InPsStrategy inStrategy = new StandardSqlInPsStrategy();

    @Default
    private final AsPsStrategy asStrategy = new StandardSqlAsPsStrategy();

    @Default
    private final FromSubqueryPsStrategy fromSubqueryStrategy = new StandardSqlFromSubqueryPsStrategy();

    @Default
    private final NullPredicatePsStrategy nullPredicateStrategy = new StandardSqlNullPredicatePsStrategy();

    @Default
    private final UpdateItemPsStrategy updateItemStrategy = new StandardSqlUpdateItemPsStrategy();

    @Default
    private final UpdateStatementPsStrategy updateStatementStrategy = new StandardSqlUpdateStatementPsStrategy();

    @Default
    private final DeleteStatementPsStrategy deleteStatementStrategy = new StandardSqlDeleteStatementPsStrategy();

    @Default
    private final MergeStatementPsStrategy mergeStatementStrategy = new StandardSqlMergeStatementPsStrategy();

    @Default
    private final RowNumberPsStrategy rowNumberStrategy = new StandardSqlRowNumberPsStrategy();

    @Default
    private final RankPsStrategy rankStrategy = new StandardSqlRankPsStrategy();

    @Default
    private final DenseRankPsStrategy denseRankStrategy = new StandardSqlDenseRankPsStrategy();

    @Default
    private final NtilePsStrategy ntileStrategy = new StandardSqlNtilePsStrategy();

    @Default
    private final LagPsStrategy lagStrategy = new StandardSqlLagPsStrategy();

    @Default
    private final LeadPsStrategy leadStrategy = new StandardSqlLeadPsStrategy();

    @Default
    private final OverClausePsStrategy overClauseStrategy = new StandardSqlOverClausePsStrategy();

    @Override
    public PsDto visit(InsertStatement stmt, AstContext ctx) {
        return insertStatementStrategy.handle(stmt, this, ctx);
    }

    @Override
    public PsDto visit(SelectStatement stmt, AstContext ctx) {
        return selectStatementStrategy.handle(stmt, this, ctx);
    }

    @Override
    public PsDto visit(Select select, AstContext ctx) {
        return selectClauseStrategy.handle(select, this, ctx);
    }

    @Override
    public PsDto visit(TableIdentifier table, AstContext ctx) {
        return tableStrategy.handle(table, this, ctx);
    }

    @Override
    public PsDto visit(ColumnReference col, AstContext ctx) {
        return columnReferenceStrategy.handle(col, this, ctx);
    }

    @Override
    public PsDto visit(Comparison cmp, AstContext ctx) {
        return comparisonStrategy.handle(cmp, this, ctx);
    }

    @Override
    public PsDto visit(Where where, AstContext ctx) {
        return whereClauseStrategy.handle(where, this, ctx);
    }

    @Override
    public PsDto visit(Literal<?> literal, AstContext ctx) {
        return literalStrategy.handle(literal, this, ctx);
    }

    @Override
    public PsDto visit(UpdateStatement updateStatement, AstContext ctx) {
        return updateStatementStrategy.handle(updateStatement, this, ctx);
    }

    @Override
    public PsDto visit(DeleteStatement deleteStatement, AstContext ctx) {
        return deleteStatementStrategy.handle(deleteStatement, this, ctx);
    }

    @Override
    public PsDto visit(MergeStatement mergeStatement, AstContext ctx) {
        return mergeStatementStrategy.handle(mergeStatement, this, ctx);
    }

    @Override
    public PsDto visit(CreateTableStatement createTableStatement, AstContext ctx) {
        return createTableStatementStrategy.handle(createTableStatement, this, ctx);
    }

    @Override
    public PsDto visit(AggregateCallProjection aggregationFunctionProjection, AstContext ctx) {
        return aggregationFunctionProjectionStrategy.handle(aggregationFunctionProjection, this, ctx);
    }

    @Override
    public PsDto visit(ScalarExpressionProjection scalarExpressionProjection, AstContext ctx) {
        return scalarExpressionProjectionStrategy.handle(scalarExpressionProjection, this, ctx);
    }

    @Override
    public PsDto visit(From clause, AstContext ctx) {
        return fromClauseStrategy.handle(clause, this, ctx);
    }

    @Override
    public PsDto visit(OnJoin join, AstContext ctx) {
        return onJoinStrategy.handle(join, this, ctx);
    }

    @Override
    public PsDto visit(FromSubquery fromSubquery, AstContext ctx) {
        return fromSubqueryStrategy.handle(fromSubquery, this, ctx);
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
        return sortingStrategy.handle(sorting, this, ctx);
    }

    @Override
    public PsDto visit(Fetch clause, AstContext ctx) {
        return paginationStrategy.handle(clause, this, ctx);
    }

    @Override
    public PsDto visit(NullPredicate expression, AstContext ctx) {
        return nullPredicateStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(Between expression, AstContext ctx) {
        return betweenStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(In expression, AstContext ctx) {
        return inStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(AndOr expression, AstContext ctx) {
        return andOrStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(Not expression, AstContext ctx) {
        return notStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(BinaryArithmeticExpression expression, AstContext ctx) {
        return binaryArithmeticExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(UnaryArithmeticExpression expression, AstContext ctx) {
        return unaryArithmeticExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(Cast functionCall, AstContext ctx) {
        return castStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(Concat functionCall, AstContext ctx) {
        return concatStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(CurrentDate functionCall, AstContext ctx) {
        return currentDateStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(CurrentDateTime functionCall, AstContext ctx) {
        return currentDateTimeStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(DateArithmetic functionCall, AstContext ctx) {
        return dateArithmeticStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(ExtractDatePart functionCall, AstContext ctx) {
        return extractDatePartStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(Left functionCall, AstContext ctx) {
        return leftStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(Length functionCall, AstContext ctx) {
        return lengthStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(CharLength functionCall, AstContext ctx) {
        return charLengthStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(CharacterLength functionCall, AstContext ctx) {
        return characterLengthStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(DataLength functionCall, AstContext ctx) {
        return dataLengthStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(Mod functionCall, AstContext ctx) {
        return modStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(NullScalarExpression expression, AstContext ctx) {
        return nullScalarExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(Power functionCall, AstContext ctx) {
        return powerStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(Replace functionCall, AstContext ctx) {
        return replaceStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(Round functionCall, AstContext ctx) {
        return roundStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(Substring functionCall, AstContext ctx) {
        return substringStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(Trim functionCall, AstContext ctx) {
        return trimStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(UnaryNumeric functionCall, AstContext ctx) {
        return unaryNumericStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(UnaryString functionCall, AstContext ctx) {
        return unaryStringStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(JsonExists functionCall, AstContext ctx) {
        return jsonExistsStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(JsonQuery functionCall, AstContext ctx) {
        return jsonQueryStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(JsonValue functionCall, AstContext ctx) {
        return jsonValueStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(NullSetExpression expression, AstContext ctx) {
        return nullSetExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(ExceptExpression expression, AstContext ctx) {
        return exceptExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(IntersectExpression expression, AstContext ctx) {
        return intersectExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(UnionExpression expression, AstContext ctx) {
        return unionExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(Alias item, AstContext ctx) {
        return asStrategy.handle(item, this, ctx);
    }

    @Override
    public PsDto visit(UpdateItem item, AstContext ctx) {
        return updateItemStrategy.handle(item, this, ctx);
    }

    @Override
    public PsDto visit(ReferencesItem item, AstContext ctx) {
        return referencesItemStrategy.handle(item, this, ctx);
    }

    @Override
    public PsDto visit(TableDefinition item, AstContext ctx) {
        return tableDefinitionStrategy.handle(item, this, ctx);
    }

    @Override
    public PsDto visit(ColumnDefinition item, AstContext ctx) {
        return columnDefinitionStrategy.handle(item, this, ctx);
    }

    @Override
    public PsDto visit(SimpleDataType type, AstContext ctx) {
        return simpleDataTypeStrategy.handle(type, this, ctx);
    }

    @Override
    public PsDto visit(ParameterizedDataType type, AstContext ctx) {
        return parameterizedDataTypeStrategy.handle(type, this, ctx);
    }

    @Override
    public PsDto visit(PrimaryKeyDefinition constraintDefinition, AstContext ctx) {
        return primaryKeyDefinitionStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PsDto visit(IndexDefinition constraintDefinition, AstContext ctx) {
        return indexDefinitionStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PsDto visit(NotNullConstraintDefinition constraintDefinition, AstContext ctx) {
        return notNullConstraintDefinitionStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PsDto visit(UniqueConstraintDefinition constraintDefinition, AstContext ctx) {
        return uniqueConstraintStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PsDto visit(ForeignKeyConstraintDefinition constraintDefinition, AstContext ctx) {
        return foreignKeyConstraintStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PsDto visit(CheckConstraintDefinition constraintDefinition, AstContext ctx) {
        return checkConstraintStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PsDto visit(DefaultConstraintDefinition constraintDefinition, AstContext ctx) {
        return defaultConstraintStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PsDto visit(ScalarSubquery expression, AstContext ctx) {
        return scalarSubqueryStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(Interval interval, AstContext ctx) {
        return intervalStrategy.handle(interval, this, ctx);
    }

    @Override
    public PsDto visit(AggregateCall expression, AstContext ctx) {
        return aggregateCallStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(InsertValues item, AstContext ctx) {
        return insertValuesStrategy.handle(item, this, ctx);
    }

    @Override
    public PsDto visit(InsertSource item, AstContext ctx) {
        return insertSourceStrategy.handle(item, this, ctx);
    }

    @Override
    public PsDto visit(DefaultValues item, AstContext ctx) {
        return defaultValuesStrategy.handle(item, this, ctx);
    }

    @Override
    public PsDto visit(MergeUsing item, AstContext ctx) {
        // Render as SQL for now
        String sql = sqlRenderer.visit(item, ctx);
        return new PsDto(sql, List.of());
    }

    @Override
    public PsDto visit(AliasedTableExpression item, AstContext ctx) {
        // Render as SQL for now
        String sql = sqlRenderer.visit(item, ctx);
        return new PsDto(sql, List.of());
    }

    @Override
    public PsDto visit(WhenMatchedUpdate item, AstContext ctx) {
        // Render as SQL for now
        String sql = sqlRenderer.visit(item, ctx);
        return new PsDto(sql, List.of());
    }

    @Override
    public PsDto visit(WhenMatchedDelete item, AstContext ctx) {
        // Render as SQL for now
        String sql = sqlRenderer.visit(item, ctx);
        return new PsDto(sql, List.of());
    }

    @Override
    public PsDto visit(WhenNotMatchedInsert item, AstContext ctx) {
        // Render as SQL for now
        String sql = sqlRenderer.visit(item, ctx);
        return new PsDto(sql, List.of());
    }

    // Handle FromSource dispatch for FROM clause
    public PsDto visit(FromSource source, AstContext ctx) {
        return source.accept(this, ctx);
    }

    @Override
    public PsDto visit(Like expression, AstContext ctx) {
        return likeStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(IsNull expr, AstContext ctx) {
        return isNullStrategy.handle(expr, this, ctx);
    }

    @Override
    public PsDto visit(IsNotNull expr, AstContext ctx) {
        return isNotNullStrategy.handle(expr, this, ctx);
    }

    @Override
    public PsDto visit(RowNumber functionCall, AstContext ctx) {
        return rowNumberStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(Rank functionCall, AstContext ctx) {
        return rankStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(DenseRank functionCall, AstContext ctx) {
        return denseRankStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(Ntile functionCall, AstContext ctx) {
        return ntileStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(Lag functionCall, AstContext ctx) {
        return lagStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(Lead functionCall, AstContext ctx) {
        return leadStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PsDto visit(OverClause overClause, AstContext ctx) {
        return overClauseStrategy.handle(overClause, this, ctx);
    }
}
