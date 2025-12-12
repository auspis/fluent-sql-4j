package lan.tlab.r4j.jdsql.ast.visitor.ps;

import lan.tlab.r4j.jdsql.ast.core.expression.aggregate.AggregateCall;
import lan.tlab.r4j.jdsql.ast.core.expression.function.CustomFunctionCall;
import lan.tlab.r4j.jdsql.ast.core.expression.function.datetime.CurrentDate;
import lan.tlab.r4j.jdsql.ast.core.expression.function.datetime.CurrentDateTime;
import lan.tlab.r4j.jdsql.ast.core.expression.function.datetime.DateArithmetic;
import lan.tlab.r4j.jdsql.ast.core.expression.function.datetime.ExtractDatePart;
import lan.tlab.r4j.jdsql.ast.core.expression.function.datetime.Interval;
import lan.tlab.r4j.jdsql.ast.core.expression.function.json.JsonExists;
import lan.tlab.r4j.jdsql.ast.core.expression.function.json.JsonQuery;
import lan.tlab.r4j.jdsql.ast.core.expression.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.core.expression.function.number.Mod;
import lan.tlab.r4j.jdsql.ast.core.expression.function.number.Power;
import lan.tlab.r4j.jdsql.ast.core.expression.function.number.Round;
import lan.tlab.r4j.jdsql.ast.core.expression.function.number.UnaryNumeric;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.CharLength;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.CharacterLength;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Concat;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Left;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Length;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Replace;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Substring;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Trim;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.UnaryString;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Cast;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.jdsql.ast.core.expression.set.AliasedTableExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.set.ExceptExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.set.IntersectExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.set.NullSetExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.set.UnionExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.window.DenseRank;
import lan.tlab.r4j.jdsql.ast.core.expression.window.Lag;
import lan.tlab.r4j.jdsql.ast.core.expression.window.Lead;
import lan.tlab.r4j.jdsql.ast.core.expression.window.Ntile;
import lan.tlab.r4j.jdsql.ast.core.expression.window.OverClause;
import lan.tlab.r4j.jdsql.ast.core.expression.window.Rank;
import lan.tlab.r4j.jdsql.ast.core.expression.window.RowNumber;
import lan.tlab.r4j.jdsql.ast.core.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.core.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.core.predicate.AndOr;
import lan.tlab.r4j.jdsql.ast.core.predicate.Between;
import lan.tlab.r4j.jdsql.ast.core.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.core.predicate.In;
import lan.tlab.r4j.jdsql.ast.core.predicate.IsNotNull;
import lan.tlab.r4j.jdsql.ast.core.predicate.IsNull;
import lan.tlab.r4j.jdsql.ast.core.predicate.Like;
import lan.tlab.r4j.jdsql.ast.core.predicate.Not;
import lan.tlab.r4j.jdsql.ast.core.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType.ParameterizedDataType;
import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.jdsql.ast.ddl.definition.IndexDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ReferencesItem;
import lan.tlab.r4j.jdsql.ast.ddl.definition.TableDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.statement.CreateTableStatement;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.DefaultValues;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertSource;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertValues;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenMatchedDelete;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeUsing;
import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.dml.statement.DeleteStatement;
import lan.tlab.r4j.jdsql.ast.dml.statement.InsertStatement;
import lan.tlab.r4j.jdsql.ast.dml.statement.MergeStatement;
import lan.tlab.r4j.jdsql.ast.dml.statement.UpdateStatement;
import lan.tlab.r4j.jdsql.ast.dql.clause.Fetch;
import lan.tlab.r4j.jdsql.ast.dql.clause.From;
import lan.tlab.r4j.jdsql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.jdsql.ast.dql.clause.Having;
import lan.tlab.r4j.jdsql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.clause.Sorting;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.dql.projection.AggregateCallProjection;
import lan.tlab.r4j.jdsql.ast.dql.projection.AggregateExpressionProjection;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.dql.source.FromSource;
import lan.tlab.r4j.jdsql.ast.dql.source.FromSubquery;
import lan.tlab.r4j.jdsql.ast.dql.source.join.OnJoin;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.AggregateCallPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.AggregationFunctionProjectionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.AliasedTableExpressionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.AndOrPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.AsPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.BetweenPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.BinaryArithmeticExpressionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CastPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CharLengthPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CharacterLengthPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CheckConstraintPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ColumnDefinitionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ColumnReferencePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ComparisonPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ConcatPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CreateTableStatementPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CurrentDatePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CurrentDateTimePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CustomFunctionCallPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.DateArithmeticPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.DefaultConstraintPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.DefaultValuesPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.DeleteStatementPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.DenseRankPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.EscapeStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ExceptExpressionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ExtractDatePartPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.FetchPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ForeignKeyConstraintPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.FromClausePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.FromSubqueryPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.GroupByClausePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.HavingClausePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.InPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IndexDefinitionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.InsertSourcePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.InsertStatementPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.InsertValuesPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IntersectExpressionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IntervalPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IsNotNullPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IsNullPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.JsonExistsPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.JsonQueryPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.JsonValuePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LagPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LeadPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LeftPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LengthPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LikePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LiteralPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.MergeStatementPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.MergeUsingPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ModPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.NotNullConstraintPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.NotPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.NtilePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.NullPredicatePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.NullScalarExpressionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.NullSetExpressionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.OnJoinPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.OrderByClausePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.OverClausePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ParameterizedDataTypePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.PowerPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.PrimaryKeyPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.RankPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ReferencesItemPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ReplacePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.RoundPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.RowNumberPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ScalarExpressionProjectionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ScalarSubqueryPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.SelectClausePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.SelectStatementPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.SimpleDataTypePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.SortingPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.SubstringPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.TableDefinitionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.TablePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.TrimPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UnaryArithmeticExpressionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UnaryNumericPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UnaryStringPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UnionExpressionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UniqueConstraintPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UpdateItemPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UpdateStatementPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.WhenMatchedDeletePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.WhenMatchedUpdatePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.WhenNotMatchedInsertPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.WhereClausePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlAggregateCallPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlAggregationFunctionProjectionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlAliasedTableExpressionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlAndOrPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlAsPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlBetweenPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlBinaryArithmeticExpressionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCastPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCharLengthPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCharacterLengthPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCheckConstraintPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlColumnDefinitionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlColumnReferencePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlComparisonPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlConcatPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCreateTableStatementPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCurrentDatePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCurrentDateTimePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCustomFunctionCallPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlDateArithmeticPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlDefaultConstraintPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlDefaultValuesPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlDeleteStatementPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlDenseRankPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlEscapeStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlExceptExpressionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlExtractDatePartPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlForeignKeyConstraintPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlFromClausePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlFromSubqueryPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlGroupByClausePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlHavingClausePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlInPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlIndexDefinitionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlInsertSourcePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlInsertStatementPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlInsertValuesPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlIntersectExpressionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlIntervalPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlIsNotNullPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlIsNullPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlJsonExistsPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlJsonQueryPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlJsonValuePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLagPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLeadPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLeftPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLengthPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLikePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLiteralPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlMergeStatementPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlMergeUsingPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlModPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNotNullConstraintPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNotPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNtilePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNullPredicatePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNullScalarExpressionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNullSetExpressionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlOnJoinPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlOrderByClausePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlOverClausePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlPaginationPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlParameterizedDataTypePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlPowerPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlPrimaryKeyPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlRankPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlReferencesItemPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlReplacePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlRoundPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlRowNumberPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlScalarExpressionProjectionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlScalarSubqueryPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlSelectClausePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlSelectStatementPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlSimpleDataTypePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlSortingPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlSubstringPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlTableDefinitionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlTablePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlTrimPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUnaryArithmeticExpressionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUnaryNumericPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUnaryStringPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUnionExpressionPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUniqueConstraintPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUpdateItemPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUpdateStatementPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlWhenMatchedDeletePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlWhenMatchedUpdatePsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlWhenNotMatchedInsertPsStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlWhereClausePsStrategy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PreparedStatementRenderer implements Visitor<PreparedStatementSpec> {
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
