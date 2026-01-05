package io.github.auspis.fluentsql4j.ast.visitor.ps;

import io.github.auspis.fluentsql4j.ast.core.expression.aggregate.AggregateCall;
import io.github.auspis.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.CurrentDate;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.CurrentDateTime;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.DateArithmetic;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.ExtractDatePart;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.Interval;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.JsonExists;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.JsonQuery;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.JsonValue;
import io.github.auspis.fluentsql4j.ast.core.expression.function.number.Mod;
import io.github.auspis.fluentsql4j.ast.core.expression.function.number.Power;
import io.github.auspis.fluentsql4j.ast.core.expression.function.number.Round;
import io.github.auspis.fluentsql4j.ast.core.expression.function.number.UnaryNumeric;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.CharLength;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.CharacterLength;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.Concat;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.Left;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.Length;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.Replace;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.Substring;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.Trim;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.UnaryString;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Cast;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.NullScalarExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarSubquery;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.set.AliasedTableExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.set.ExceptExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.set.IntersectExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.set.NullSetExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.set.UnionExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.window.DenseRank;
import io.github.auspis.fluentsql4j.ast.core.expression.window.Lag;
import io.github.auspis.fluentsql4j.ast.core.expression.window.Lead;
import io.github.auspis.fluentsql4j.ast.core.expression.window.Ntile;
import io.github.auspis.fluentsql4j.ast.core.expression.window.OverClause;
import io.github.auspis.fluentsql4j.ast.core.expression.window.Rank;
import io.github.auspis.fluentsql4j.ast.core.expression.window.RowNumber;
import io.github.auspis.fluentsql4j.ast.core.identifier.Alias;
import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.core.predicate.AndOr;
import io.github.auspis.fluentsql4j.ast.core.predicate.Between;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.core.predicate.In;
import io.github.auspis.fluentsql4j.ast.core.predicate.IsNotNull;
import io.github.auspis.fluentsql4j.ast.core.predicate.IsNull;
import io.github.auspis.fluentsql4j.ast.core.predicate.Like;
import io.github.auspis.fluentsql4j.ast.core.predicate.Not;
import io.github.auspis.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ColumnDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.IndexDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ReferencesItem;
import io.github.auspis.fluentsql4j.ast.ddl.definition.TableDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.DataType.ParameterizedDataType;
import io.github.auspis.fluentsql4j.ast.ddl.definition.DataType.SimpleDataType;
import io.github.auspis.fluentsql4j.ast.ddl.statement.CreateTableStatement;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeUsing;
import io.github.auspis.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.DefaultValues;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.InsertSource;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.InsertValues;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedDelete;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedUpdate;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import io.github.auspis.fluentsql4j.ast.dml.statement.DeleteStatement;
import io.github.auspis.fluentsql4j.ast.dml.statement.InsertStatement;
import io.github.auspis.fluentsql4j.ast.dml.statement.MergeStatement;
import io.github.auspis.fluentsql4j.ast.dml.statement.UpdateStatement;
import io.github.auspis.fluentsql4j.ast.dql.clause.Fetch;
import io.github.auspis.fluentsql4j.ast.dql.clause.From;
import io.github.auspis.fluentsql4j.ast.dql.clause.GroupBy;
import io.github.auspis.fluentsql4j.ast.dql.clause.Having;
import io.github.auspis.fluentsql4j.ast.dql.clause.OrderBy;
import io.github.auspis.fluentsql4j.ast.dql.clause.Select;
import io.github.auspis.fluentsql4j.ast.dql.clause.Sorting;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.dql.projection.AggregateCallProjection;
import io.github.auspis.fluentsql4j.ast.dql.projection.AggregateExpressionProjection;
import io.github.auspis.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.auspis.fluentsql4j.ast.dql.source.FromSource;
import io.github.auspis.fluentsql4j.ast.dql.source.FromSubquery;
import io.github.auspis.fluentsql4j.ast.dql.source.join.OnJoin;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.AggregateCallPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.AggregationFunctionProjectionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.AliasedTableExpressionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.AndOrPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.AsPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.BetweenPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.BinaryArithmeticExpressionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CastPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CharLengthPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CharacterLengthPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CheckConstraintPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ColumnDefinitionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ColumnReferencePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ComparisonPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ConcatPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CreateTableStatementPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CurrentDatePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CurrentDateTimePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CustomFunctionCallPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.DateArithmeticPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.DefaultConstraintPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.DefaultValuesPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.DeleteStatementPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.DenseRankPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.EscapeStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ExceptExpressionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ExtractDatePartPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.FetchPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ForeignKeyConstraintPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.FromClausePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.FromSubqueryPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.GroupByClausePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.HavingClausePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.InPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.IndexDefinitionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.InsertSourcePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.InsertStatementPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.InsertValuesPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.IntersectExpressionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.IntervalPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.IsNotNullPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.IsNullPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.JsonExistsPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.JsonQueryPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.JsonValuePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.LagPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.LeadPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.LeftPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.LengthPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.LikePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.LiteralPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.MergeStatementPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.MergeUsingPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ModPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.NotNullConstraintPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.NotPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.NtilePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.NullPredicatePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.NullScalarExpressionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.NullSetExpressionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.OnJoinPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.OrderByClausePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.OverClausePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ParameterizedDataTypePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.PowerPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.PrimaryKeyPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.RankPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ReferencesItemPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ReplacePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.RoundPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.RowNumberPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ScalarExpressionProjectionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ScalarSubqueryPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.SelectClausePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.SelectStatementPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.SimpleDataTypePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.SortingPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.SubstringPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.TableDefinitionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.TablePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.TrimPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UnaryArithmeticExpressionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UnaryNumericPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UnaryStringPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UnionExpressionPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UniqueConstraintPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UpdateItemPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UpdateStatementPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhenMatchedDeletePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhenMatchedUpdatePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhenNotMatchedInsertPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhereClausePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlAggregateCallPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlAggregationFunctionProjectionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlAliasedTableExpressionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlAndOrPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlAsPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlBetweenPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlBinaryArithmeticExpressionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCastPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCharLengthPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCharacterLengthPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCheckConstraintPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlColumnDefinitionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlColumnReferencePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlComparisonPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlConcatPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCreateTableStatementPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCurrentDatePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCurrentDateTimePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCustomFunctionCallPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlDateArithmeticPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlDefaultConstraintPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlDefaultValuesPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlDeleteStatementPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlDenseRankPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlEscapeStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlExceptExpressionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlExtractDatePartPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlForeignKeyConstraintPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlFromClausePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlFromSubqueryPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlGroupByClausePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlHavingClausePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlInPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlIndexDefinitionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlInsertSourcePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlInsertStatementPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlInsertValuesPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlIntersectExpressionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlIntervalPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlIsNotNullPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlIsNullPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlJsonExistsPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlJsonQueryPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlJsonValuePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLagPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLeadPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLeftPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLengthPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLikePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLiteralPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlMergeStatementPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlMergeUsingPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlModPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNotNullConstraintPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNotPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNtilePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNullPredicatePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNullScalarExpressionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNullSetExpressionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlOnJoinPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlOrderByClausePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlOverClausePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlPaginationPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlParameterizedDataTypePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlPowerPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlPrimaryKeyPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlRankPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlReferencesItemPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlReplacePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlRoundPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlRowNumberPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlScalarExpressionProjectionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlScalarSubqueryPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlSelectClausePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlSelectStatementPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlSimpleDataTypePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlSortingPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlSubstringPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlTableDefinitionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlTablePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlTrimPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUnaryArithmeticExpressionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUnaryNumericPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUnaryStringPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUnionExpressionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUniqueConstraintPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUpdateItemPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUpdateStatementPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlWhenMatchedDeletePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlWhenMatchedUpdatePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlWhenNotMatchedInsertPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlWhereClausePsStrategy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AstToPreparedStatementSpecVisitor implements Visitor<PreparedStatementSpec> {
    @Default
    private final EscapeStrategy escapeStrategy = new StandardSqlEscapeStrategy();

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
    private final CustomFunctionCallPsStrategy customFunctionCallStrategy =
            new StandardSqlCustomFunctionCallPsStrategy();

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
    private final MergeUsingPsStrategy mergeUsingStrategy = new StandardSqlMergeUsingPsStrategy();

    @Default
    private final WhenMatchedUpdatePsStrategy whenMatchedUpdateStrategy = new StandardSqlWhenMatchedUpdatePsStrategy();

    @Default
    private final WhenMatchedDeletePsStrategy whenMatchedDeleteStrategy = new StandardSqlWhenMatchedDeletePsStrategy();

    @Default
    private final WhenNotMatchedInsertPsStrategy whenNotMatchedInsertStrategy =
            new StandardSqlWhenNotMatchedInsertPsStrategy();

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

    @Default
    private final AliasedTableExpressionPsStrategy aliasedTableExpressionStrategy =
            new StandardSqlAliasedTableExpressionPsStrategy();

    @Override
    public EscapeStrategy getEscapeStrategy() {
        return escapeStrategy;
    }

    @Override
    public PreparedStatementSpec visit(InsertStatement stmt, AstContext ctx) {
        return insertStatementStrategy.handle(stmt, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(SelectStatement stmt, AstContext ctx) {
        return selectStatementStrategy.handle(stmt, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Select select, AstContext ctx) {
        return selectClauseStrategy.handle(select, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(TableIdentifier table, AstContext ctx) {
        return tableStrategy.handle(table, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(ColumnReference col, AstContext ctx) {
        return columnReferenceStrategy.handle(col, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Comparison cmp, AstContext ctx) {
        return comparisonStrategy.handle(cmp, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Where where, AstContext ctx) {
        return whereClauseStrategy.handle(where, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Literal<?> literal, AstContext ctx) {
        return literalStrategy.handle(literal, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(UpdateStatement updateStatement, AstContext ctx) {
        return updateStatementStrategy.handle(updateStatement, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(DeleteStatement deleteStatement, AstContext ctx) {
        return deleteStatementStrategy.handle(deleteStatement, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(MergeStatement mergeStatement, AstContext ctx) {
        return mergeStatementStrategy.handle(mergeStatement, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(CreateTableStatement createTableStatement, AstContext ctx) {
        return createTableStatementStrategy.handle(createTableStatement, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(AggregateExpressionProjection aggregateExpressionProjection, AstContext ctx) {
        return aggregationFunctionProjectionStrategy.handle(aggregateExpressionProjection, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(AggregateCallProjection aggregationFunctionProjection, AstContext ctx) {
        return aggregationFunctionProjectionStrategy.handle(aggregationFunctionProjection, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(ScalarExpressionProjection scalarExpressionProjection, AstContext ctx) {
        return scalarExpressionProjectionStrategy.handle(scalarExpressionProjection, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(From clause, AstContext ctx) {
        return fromClauseStrategy.handle(clause, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(OnJoin join, AstContext ctx) {
        return onJoinStrategy.handle(join, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(FromSubquery fromSubquery, AstContext ctx) {
        return fromSubqueryStrategy.handle(fromSubquery, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(GroupBy clause, AstContext ctx) {
        return groupByClauseStrategy.handle(clause, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Having clause, AstContext ctx) {
        return havingClauseStrategy.handle(clause, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(OrderBy clause, AstContext ctx) {
        return orderByClauseStrategy.handle(clause, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Sorting sorting, AstContext ctx) {
        return sortingStrategy.handle(sorting, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Fetch clause, AstContext ctx) {
        return paginationStrategy.handle(clause, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(NullPredicate expression, AstContext ctx) {
        return nullPredicateStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Between expression, AstContext ctx) {
        return betweenStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(In expression, AstContext ctx) {
        return inStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(AndOr expression, AstContext ctx) {
        return andOrStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Not expression, AstContext ctx) {
        return notStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(BinaryArithmeticExpression expression, AstContext ctx) {
        return binaryArithmeticExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(UnaryArithmeticExpression expression, AstContext ctx) {
        return unaryArithmeticExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Cast functionCall, AstContext ctx) {
        return castStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Concat functionCall, AstContext ctx) {
        return concatStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(CurrentDate functionCall, AstContext ctx) {
        return currentDateStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(CurrentDateTime functionCall, AstContext ctx) {
        return currentDateTimeStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(DateArithmetic functionCall, AstContext ctx) {
        return dateArithmeticStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(ExtractDatePart functionCall, AstContext ctx) {
        return extractDatePartStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Left functionCall, AstContext ctx) {
        return leftStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Length functionCall, AstContext ctx) {
        return lengthStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(CharLength functionCall, AstContext ctx) {
        return charLengthStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(CharacterLength functionCall, AstContext ctx) {
        return characterLengthStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Mod functionCall, AstContext ctx) {
        return modStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(NullScalarExpression expression, AstContext ctx) {
        return nullScalarExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Power functionCall, AstContext ctx) {
        return powerStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Replace functionCall, AstContext ctx) {
        return replaceStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Round functionCall, AstContext ctx) {
        return roundStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Substring functionCall, AstContext ctx) {
        return substringStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Trim functionCall, AstContext ctx) {
        return trimStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(UnaryNumeric functionCall, AstContext ctx) {
        return unaryNumericStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(UnaryString functionCall, AstContext ctx) {
        return unaryStringStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(JsonExists functionCall, AstContext ctx) {
        return jsonExistsStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(JsonQuery functionCall, AstContext ctx) {
        return jsonQueryStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(JsonValue functionCall, AstContext ctx) {
        return jsonValueStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(NullSetExpression expression, AstContext ctx) {
        return nullSetExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(ExceptExpression expression, AstContext ctx) {
        return exceptExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(IntersectExpression expression, AstContext ctx) {
        return intersectExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(UnionExpression expression, AstContext ctx) {
        return unionExpressionStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Alias item, AstContext ctx) {
        return asStrategy.handle(item, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(UpdateItem item, AstContext ctx) {
        return updateItemStrategy.handle(item, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(ReferencesItem item, AstContext ctx) {
        return referencesItemStrategy.handle(item, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(TableDefinition item, AstContext ctx) {
        return tableDefinitionStrategy.handle(item, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(ColumnDefinition item, AstContext ctx) {
        return columnDefinitionStrategy.handle(item, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(SimpleDataType type, AstContext ctx) {
        return simpleDataTypeStrategy.handle(type, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(ParameterizedDataType type, AstContext ctx) {
        return parameterizedDataTypeStrategy.handle(type, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(PrimaryKeyDefinition constraintDefinition, AstContext ctx) {
        return primaryKeyDefinitionStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(IndexDefinition constraintDefinition, AstContext ctx) {
        return indexDefinitionStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(NotNullConstraintDefinition constraintDefinition, AstContext ctx) {
        return notNullConstraintDefinitionStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(UniqueConstraintDefinition constraintDefinition, AstContext ctx) {
        return uniqueConstraintStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(ForeignKeyConstraintDefinition constraintDefinition, AstContext ctx) {
        return foreignKeyConstraintStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(CheckConstraintDefinition constraintDefinition, AstContext ctx) {
        return checkConstraintStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(DefaultConstraintDefinition constraintDefinition, AstContext ctx) {
        return defaultConstraintStrategy.handle(constraintDefinition, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(ScalarSubquery expression, AstContext ctx) {
        return scalarSubqueryStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Interval interval, AstContext ctx) {
        return intervalStrategy.handle(interval, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(AggregateCall expression, AstContext ctx) {
        return aggregateCallStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(InsertValues item, AstContext ctx) {
        return insertValuesStrategy.handle(item, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(InsertSource item, AstContext ctx) {
        return insertSourceStrategy.handle(item, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(DefaultValues item, AstContext ctx) {
        return defaultValuesStrategy.handle(item, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(MergeUsing item, AstContext ctx) {
        return mergeUsingStrategy.handle(item, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(AliasedTableExpression item, AstContext ctx) {
        return aliasedTableExpressionStrategy.handle(item, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(WhenMatchedUpdate item, AstContext ctx) {
        return whenMatchedUpdateStrategy.handle(item, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(WhenMatchedDelete item, AstContext ctx) {
        return whenMatchedDeleteStrategy.handle(item, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(WhenNotMatchedInsert item, AstContext ctx) {
        return whenNotMatchedInsertStrategy.handle(item, this, ctx);
    }

    // Handle FromSource dispatch for FROM clause
    public PreparedStatementSpec visit(FromSource source, AstContext ctx) {
        return source.accept(this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Like expression, AstContext ctx) {
        return likeStrategy.handle(expression, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(IsNull expr, AstContext ctx) {
        return isNullStrategy.handle(expr, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(IsNotNull expr, AstContext ctx) {
        return isNotNullStrategy.handle(expr, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(RowNumber functionCall, AstContext ctx) {
        return rowNumberStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Rank functionCall, AstContext ctx) {
        return rankStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(DenseRank functionCall, AstContext ctx) {
        return denseRankStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Ntile functionCall, AstContext ctx) {
        return ntileStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Lag functionCall, AstContext ctx) {
        return lagStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(Lead functionCall, AstContext ctx) {
        return leadStrategy.handle(functionCall, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(OverClause overClause, AstContext ctx) {
        return overClauseStrategy.handle(overClause, this, ctx);
    }

    @Override
    public PreparedStatementSpec visit(CustomFunctionCall functionCall, AstContext ctx) {
        return customFunctionCallStrategy.handle(functionCall, this, ctx);
    }
}
