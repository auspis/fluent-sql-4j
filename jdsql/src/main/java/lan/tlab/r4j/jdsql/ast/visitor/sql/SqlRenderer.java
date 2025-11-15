package lan.tlab.r4j.jdsql.ast.visitor.sql;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Cast;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDateTime;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.DateArithmetic;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.ExtractDatePart;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.interval.Interval;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonExists;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.Mod;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.Power;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.Round;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.UnaryNumeric;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.CharLength;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.CharacterLength;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Concat;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Left;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Length;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Replace;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Substring;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Trim;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.UnaryString;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.DenseRank;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Lag;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Lead;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Ntile;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.OverClause;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Rank;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.RowNumber;
import lan.tlab.r4j.jdsql.ast.common.expression.set.AliasedTableExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.set.ExceptExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.set.IntersectExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.set.NullSetExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.set.UnionExpression;
import lan.tlab.r4j.jdsql.ast.common.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.common.predicate.Between;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.common.predicate.In;
import lan.tlab.r4j.jdsql.ast.common.predicate.IsNotNull;
import lan.tlab.r4j.jdsql.ast.common.predicate.IsNull;
import lan.tlab.r4j.jdsql.ast.common.predicate.Like;
import lan.tlab.r4j.jdsql.ast.common.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.common.predicate.logical.AndOr;
import lan.tlab.r4j.jdsql.ast.common.predicate.logical.Not;
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
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.dql.source.FromSubquery;
import lan.tlab.r4j.jdsql.ast.dql.source.join.OnJoin;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.FetchRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.FromRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.FromSubqueryRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.GroupByRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.HavingRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.OnJoinStrategyRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.OrderByRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.SelectRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.SortingRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.WhereRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.AggregateCallProjectionRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.AggregateCallRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.AndOrRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.BetweenRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.BinaryArithmeticExpressionRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.CastRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.CharLengthRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.CharacterLengthRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ColumnReferenceRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ComparisonRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ConcatRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.CurrentDateRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.CurrentDateTimeRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.CustomFunctionCallRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.DateArithmeticRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ExceptRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ExtractDatePartRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.InRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.IntersectRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.IntervalRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.IsNotNullRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.IsNullRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.JsonExistsRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.JsonQueryRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.JsonValueRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.LeftRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.LengthRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.LikeRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.LiteralRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ModRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.NotRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.NullScalarExpressionRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.NullSetExpressionRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.PowerRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ReplaceRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.RoundRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ScalarExpressionProjectionRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ScalarSubqueryRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.SubstringRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.TrimRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.UnaryArithmeticExpressionRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.UnaryNumericRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.UnaryStringRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.UnionRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.set.AliasedTableExpressionRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window.DenseRankRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window.LagRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window.LeadRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window.NtileRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window.OverClauseRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window.RankRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window.RowNumberRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.AsRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.DefaultValuesRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.InsertSourceRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.InsertValueRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.MergeUsingRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.TableRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.UpdateItemRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.WhenMatchedDeleteRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.WhenMatchedUpdateRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.WhenNotMatchedInsertRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.ColumnDefinitionRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.IndexDefinitionRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.ParameterizedDataTypeRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.ReferencesItemRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.SimpleDataTypeRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.TableDefinitionRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint.CheckConstraintRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint.DefaultConstraintRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint.ForeignKeyConstraintRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint.NotNullConstraintRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint.PrimaryKeyRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint.UniqueConstraintRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement.CreateTableStatementRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement.DeleteStatementRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement.InsertStatementRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement.MergeStatementRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement.SelectStatementRenderStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement.UpdateStatementRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlFetchRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlFromRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlFromSubqueryRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlGroupByRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlHavingRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlOnJoinStrategyRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlOrderByRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlSelectRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlSortingRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlWhereRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.escape.StandardSqlEscapeStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlAggregateCallProjectionRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlAggregateCallRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlAndOrRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlBetweenRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlBinaryArithmeticExpressionRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlCastRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlCharLengthRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlCharacterLengthRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlColumnReferenceRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlComparisonRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlConcatRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlCurrentDateRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlCurrentDateTimeRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlCustomFunctionCallRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlDateArithmeticRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlExceptRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlExtractDatePartRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlInRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlIntersectRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlIntervalRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlIsNotNullRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlIsNullRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlJsonExistsRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlJsonQueryRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlJsonValueRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlLeftRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlLengthRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlLikeRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlLiteralRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlModRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlNotRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlNullScalarExpressionRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlNullSetExpressionRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlPowerRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlReplaceRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlRoundRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlScalarExpressionProjectionRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlScalarSubqueryRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlSubstringRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlTrimRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlUnaryArithmeticExpressionRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlUnaryNumericRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlUnaryStringRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlUnionRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.set.StandardSqlAliasedTableExpressionRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window.StandardSqlDenseRankRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window.StandardSqlLagRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window.StandardSqlLeadRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window.StandardSqlNtileRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window.StandardSqlOverClauseRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window.StandardSqlRankRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window.StandardSqlRowNumberRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.StandardSqlAsRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.StandardSqlDefaultValuesRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.StandardSqlInsertSourceRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.StandardSqlInsertValueRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.StandardSqlMergeUsingRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.StandardSqlTableRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.StandardSqlUpdateItemRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.StandardSqlWhenMatchedDeleteRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.StandardSqlWhenMatchedUpdateRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.StandardSqlWhenNotMatchedInsertRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.StandardSqlColumnDefinitionRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.StandardSqlIndexDefinitionRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.StandardSqlParameterizedDataTypeRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.StandardSqlReferencesItemRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.StandardSqlSimpleDataTypeRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.StandardSqlTableDefinitionRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint.StandardSqlCheckConstraintRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint.StandardSqlDefaultConstraintRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint.StandardSqlForeignKeyConstraintRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint.StandardSqlNotNullConstraintRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint.StandardSqlPrimaryKeyRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint.StandardSqlUniqueConstraintRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement.StandardSqlCreateTableStatementRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement.StandardSqlDeleteStatementRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement.StandardSqlInsertStatementRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement.StandardSqlMergeStatementRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement.StandardSqlSelectStatementRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement.StandardSqlUpdateStatementRenderStrategy;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
public class SqlRenderer implements Visitor<String> {

    @Getter
    @Default
    private final EscapeStrategy escapeStrategy = new StandardSqlEscapeStrategy();

    @Default
    private final SelectStatementRenderStrategy selectStatementStrategy =
            new StandardSqlSelectStatementRenderStrategy();

    @Default
    private final InsertStatementRenderStrategy insertStatementStrategy =
            new StandardSqlInsertStatementRenderStrategy();

    @Default
    private final UpdateStatementRenderStrategy updateStatementStrategy =
            new StandardSqlUpdateStatementRenderStrategy();

    @Default
    private final DeleteStatementRenderStrategy deleteStatementStrategy =
            new StandardSqlDeleteStatementRenderStrategy();

    @Default
    private final MergeStatementRenderStrategy mergeStatementStrategy = new StandardSqlMergeStatementRenderStrategy();

    @Default
    private final CreateTableStatementRenderStrategy createTableStatementStrategy =
            new StandardSqlCreateTableStatementRenderStrategy();

    // clause
    @Default
    private final SelectRenderStrategy selectStrategy = new StandardSqlSelectRenderStrategy();

    @Default
    private final AggregateCallProjectionRenderStrategy aggregateCallProjectionStrategy =
            new StandardSqlAggregateCallProjectionRenderStrategy();

    @Default
    private final ScalarExpressionProjectionRenderStrategy scalarExpressionProjectionStrategy =
            new StandardSqlScalarExpressionProjectionRenderStrategy();

    @Default
    private final FromRenderStrategy fromStrategy = new StandardSqlFromRenderStrategy();

    @Default
    private final OnJoinStrategyRenderStrategy onJoinStrategy = new StandardSqlOnJoinStrategyRenderStrategy();

    @Default
    private final FromSubqueryRenderStrategy fromSubqueryStrategy = new StandardSqlFromSubqueryRenderStrategy();

    @Default
    private final WhereRenderStrategy whereStrategy = new StandardSqlWhereRenderStrategy();

    @Default
    private final GroupByRenderStrategy groupByStrategy = new StandardSqlGroupByRenderStrategy();

    @Default
    private final HavingRenderStrategy havingStrategy = new StandardSqlHavingRenderStrategy();

    @Default
    private final OrderByRenderStrategy orderByStrategy = new StandardSqlOrderByRenderStrategy();

    @Default
    private final SortingRenderStrategy sortingStrategy = new StandardSqlSortingRenderStrategy();

    @Default
    private final FetchRenderStrategy paginationStrategy = new StandardSqlFetchRenderStrategy();

    // boolean expressions
    @Default
    private final BetweenRenderStrategy betweenStrategy = new StandardSqlBetweenRenderStrategy();

    @Default
    private final ComparisonRenderStrategy comparisonStrategy = new StandardSqlComparisonRenderStrategy();

    @Default
    private final InRenderStrategy inStrategy = new StandardSqlInRenderStrategy();

    @Default
    private final IsNotNullRenderStrategy isNotNullStrategy = new StandardSqlIsNotNullRenderStrategy();

    @Default
    private final IsNullRenderStrategy isNullStrategy = new StandardSqlIsNullRenderStrategy();

    @Default
    private final LikeRenderStrategy likeStrategy = new StandardSqlLikeRenderStrategy();

    @Default
    private final AndOrRenderStrategy andOrStrategy = new StandardSqlAndOrRenderStrategy();

    @Default
    private final NotRenderStrategy notStrategy = new StandardSqlNotRenderStrategy();

    // scalar expressions
    @Default
    private final AggregateCallRenderStrategy aggregateCallStrategy = new StandardSqlAggregateCallRenderStrategy();

    @Default
    private final ColumnReferenceRenderStrategy columnReferenceStrategy =
            new StandardSqlColumnReferenceRenderStrategy();

    @Default
    private final IntervalRenderStrategy intervalStrategy = new StandardSqlIntervalRenderStrategy();

    @Default
    private final LiteralRenderStrategy literalStrategy = new StandardSqlLiteralRenderStrategy();

    @Default
    private final ScalarSubqueryRenderStrategy scalarSubqueryStrategy = new StandardSqlScalarSubqueryRenderStrategy();

    @Default
    private final BinaryArithmeticExpressionRenderStrategy binaryArithmeticExpressionStrategy =
            new StandardSqlBinaryArithmeticExpressionRenderStrategy();

    @Default
    private final UnaryArithmeticExpressionRenderStrategy unaryArithmeticExpressionStrategy =
            new StandardSqlUnaryArithmeticExpressionRenderStrategy();

    // scalar expressions - function calls
    @Default
    private final CastRenderStrategy castStrategy = new StandardSqlCastRenderStrategy();

    @Default
    private final ConcatRenderStrategy concatStrategy = new StandardSqlConcatRenderStrategy();

    @Default
    private final CurrentDateRenderStrategy currentDateStrategy = new StandardSqlCurrentDateRenderStrategy();

    @Default
    private final CurrentDateTimeRenderStrategy currentDateTimeStrategy =
            new StandardSqlCurrentDateTimeRenderStrategy();

    @Default
    private final DateArithmeticRenderStrategy dateArithmeticStrategy = new StandardSqlDateArithmeticRenderStrategy();

    @Default
    private final ExtractDatePartRenderStrategy extractDatePartStrategy =
            new StandardSqlExtractDatePartRenderStrategy();

    @Default
    private final LeftRenderStrategy leftStrategy = new StandardSqlLeftRenderStrategy();

    @Default
    private final LengthRenderStrategy lengthStrategy = new StandardSqlLengthRenderStrategy();

    @Default
    private final CharLengthRenderStrategy charLengthStrategy = new StandardSqlCharLengthRenderStrategy();

    @Default
    private final CharacterLengthRenderStrategy characterLengthStrategy =
            new StandardSqlCharacterLengthRenderStrategy();

    @Default
    private final ModRenderStrategy modStrategy = new StandardSqlModRenderStrategy();

    @Default
    private final NullScalarExpressionRenderStrategy nullScalarExpressionStrategy =
            new StandardSqlNullScalarExpressionRenderStrategy();

    @Default
    private final PowerRenderStrategy powerStrategy = new StandardSqlPowerRenderStrategy();

    @Default
    private final ReplaceRenderStrategy replaceStrategy = new StandardSqlReplaceRenderStrategy();

    @Default
    private final RoundRenderStrategy roundStrategy = new StandardSqlRoundRenderStrategy();

    @Default
    private final SubstringRenderStrategy substringStrategy = new StandardSqlSubstringRenderStrategy();

    @Default
    private final TrimRenderStrategy trimStrategy = new StandardSqlTrimRenderStrategy();

    @Default
    private final CustomFunctionCallRenderStrategy customFunctionCallStrategy =
            new StandardSqlCustomFunctionCallRenderStrategy();

    @Default
    private final UnaryNumericRenderStrategy unaryNumericStrategy = new StandardSqlUnaryNumericRenderStrategy();

    @Default
    private final UnaryStringRenderStrategy unaryStringStrategy = new StandardSqlUnaryStringRenderStrategy();

    @Default
    private final JsonExistsRenderStrategy jsonExistsStrategy = new StandardSqlJsonExistsRenderStrategy();

    @Default
    private final JsonQueryRenderStrategy jsonQueryStrategy = new StandardSqlJsonQueryRenderStrategy();

    @Default
    private final JsonValueRenderStrategy jsonValueStrategy = new StandardSqlJsonValueRenderStrategy();

    // set expressions
    @Default
    private final NullSetExpressionRenderStrategy nullSetExpressionStrategy =
            new StandardSqlNullSetExpressionRenderStrategy();

    @Default
    private final ExceptRenderStrategy exceptStrategy = new StandardSqlExceptRenderStrategy();

    @Default
    private final IntersectRenderStrategy intersectStrategy = new StandardSqlIntersectRenderStrategy();

    @Default
    private final UnionRenderStrategy unionStrategy = new StandardSqlUnionRenderStrategy();

    // sql items
    @Default
    private final TableRenderStrategy tableStrategy = new StandardSqlTableRenderStrategy();

    @Default
    private final AsRenderStrategy asStrategy = new StandardSqlAsRenderStrategy();

    @Default
    private final UpdateItemRenderStrategy updateItemStrategy = new StandardSqlUpdateItemRenderStrategy();

    @Default
    private final InsertValueRenderStrategy insertValueStrategy = new StandardSqlInsertValueRenderStrategy();

    @Default
    private final InsertSourceRenderStrategy insertSourceStrategy = new StandardSqlInsertSourceRenderStrategy();

    @Default
    private final DefaultValuesRenderStrategy defaultValuesStrategy = new StandardSqlDefaultValuesRenderStrategy();

    @Default
    private final AliasedTableExpressionRenderStrategy aliasedTableExpressionStrategy =
            new StandardSqlAliasedTableExpressionRenderStrategy();

    @Default
    private final MergeUsingRenderStrategy mergeUsingStrategy = new StandardSqlMergeUsingRenderStrategy();

    @Default
    private final WhenMatchedUpdateRenderStrategy whenMatchedUpdateStrategy =
            new StandardSqlWhenMatchedUpdateRenderStrategy();

    @Default
    private final WhenMatchedDeleteRenderStrategy whenMatchedDeleteStrategy =
            new StandardSqlWhenMatchedDeleteRenderStrategy();

    @Default
    private final WhenNotMatchedInsertRenderStrategy whenNotMatchedInsertStrategy =
            new StandardSqlWhenNotMatchedInsertRenderStrategy();

    @Default
    private final ReferencesItemRenderStrategy referencesItemStrategy = new StandardSqlReferencesItemRenderStrategy();

    @Default
    private final TableDefinitionRenderStrategy tableDefinitionStrategy =
            new StandardSqlTableDefinitionRenderStrategy();

    @Default
    private final ColumnDefinitionRenderStrategy columnDefinitionStrategy =
            new StandardSqlColumnDefinitionRenderStrategy();

    @Default
    private final SimpleDataTypeRenderStrategy simpleDataTypeStrategy = new StandardSqlSimpleDataTypeRenderStrategy();

    @Default
    private final ParameterizedDataTypeRenderStrategy parameterizedDataTypeStrategy =
            new StandardSqlParameterizedDataTypeRenderStrategy();

    @Default
    private final PrimaryKeyRenderStrategy primaryKeyStrategy = new StandardSqlPrimaryKeyRenderStrategy();

    @Default
    private final IndexDefinitionRenderStrategy indexDefinitionStrategy =
            new StandardSqlIndexDefinitionRenderStrategy();

    @Default
    private final NotNullConstraintRenderStrategy notNullConstraintStrategy =
            new StandardSqlNotNullConstraintRenderStrategy();

    @Default
    private final UniqueConstraintRenderStrategy uniqueConstraintStrategy =
            new StandardSqlUniqueConstraintRenderStrategy();

    @Default
    private final ForeignKeyConstraintRenderStrategy foreignKeyConstraintStrategy =
            new StandardSqlForeignKeyConstraintRenderStrategy();

    @Default
    private final CheckConstraintRenderStrategy checkConstraintStrategy =
            new StandardSqlCheckConstraintRenderStrategy();

    @Default
    private final DefaultConstraintRenderStrategy defaultConstraintStrategy =
            new StandardSqlDefaultConstraintRenderStrategy();

    @Default
    private final RowNumberRenderStrategy rowNumberStrategy = new StandardSqlRowNumberRenderStrategy();

    @Default
    private final RankRenderStrategy rankStrategy = new StandardSqlRankRenderStrategy();

    @Default
    private final DenseRankRenderStrategy denseRankStrategy = new StandardSqlDenseRankRenderStrategy();

    @Default
    private final NtileRenderStrategy ntileStrategy = new StandardSqlNtileRenderStrategy();

    @Default
    private final LagRenderStrategy lagStrategy = new StandardSqlLagRenderStrategy();

    @Default
    private final LeadRenderStrategy leadStrategy = new StandardSqlLeadRenderStrategy();

    @Default
    private final OverClauseRenderStrategy overClauseStrategy = new StandardSqlOverClauseRenderStrategy();

    // statements
    @Override
    public String visit(SelectStatement statement, AstContext ctx) {
        return selectStatementStrategy.render(statement, this, ctx);
    }

    @Override
    public String visit(InsertStatement statement, AstContext ctx) {
        return insertStatementStrategy.render(statement, this, ctx);
    }

    @Override
    public String visit(UpdateStatement statement, AstContext ctx) {
        return updateStatementStrategy.render(statement, this, ctx);
    }

    @Override
    public String visit(DeleteStatement statement, AstContext ctx) {
        return deleteStatementStrategy.render(statement, this, ctx);
    }

    @Override
    public String visit(MergeStatement statement, AstContext ctx) {
        return mergeStatementStrategy.render(statement, this, ctx);
    }

    @Override
    public String visit(CreateTableStatement statement, AstContext ctx) {
        return createTableStatementStrategy.render(statement, this, ctx);
    }

    // clause
    @Override
    public String visit(Select clause, AstContext ctx) {
        return selectStrategy.render(clause, this, ctx);
    }

    @Override
    public String visit(AggregateCallProjection projection, AstContext ctx) {
        return aggregateCallProjectionStrategy.render(projection, this, ctx);
    }

    @Override
    public String visit(ScalarExpressionProjection projection, AstContext ctx) {
        return scalarExpressionProjectionStrategy.render(projection, this, ctx);
    }

    @Override
    public String visit(From clause, AstContext ctx) {
        return fromStrategy.render(clause, this, ctx);
    }

    @Override
    public String visit(OnJoin onJoin, AstContext ctx) {
        return onJoinStrategy.render(onJoin, this, ctx);
    }

    @Override
    public String visit(FromSubquery fromSubquery, AstContext ctx) {
        return fromSubqueryStrategy.render(fromSubquery, this, ctx);
    }

    @Override
    public String visit(Where clause, AstContext ctx) {
        return whereStrategy.render(clause, this, ctx);
    }

    @Override
    public String visit(GroupBy clause, AstContext ctx) {
        return groupByStrategy.render(clause, this, ctx);
    }

    @Override
    public String visit(Having clause, AstContext ctx) {
        return havingStrategy.render(clause, this, ctx);
    }

    @Override
    public String visit(OrderBy clause, AstContext ctx) {
        return orderByStrategy.render(clause, this, ctx);
    }

    @Override
    public String visit(Sorting sorting, AstContext ctx) {
        return sortingStrategy.render(sorting, this, ctx);
    }

    @Override
    public String visit(Fetch clause, AstContext ctx) {
        return paginationStrategy.render(clause, this, ctx);
    }

    // boolean expressions
    @Override
    public String visit(NullPredicate expression, AstContext ctx) {
        return nullScalarExpressionStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(Between expression, AstContext ctx) {
        return betweenStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(Comparison expression, AstContext ctx) {
        return comparisonStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(In expression, AstContext ctx) {
        return inStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(IsNotNull expression, AstContext ctx) {
        return isNotNullStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(IsNull expression, AstContext ctx) {
        return isNullStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(Like expression, AstContext ctx) {
        return likeStrategy.render(expression, this, ctx);
    }

    // boolean expressions - logical
    @Override
    public String visit(AndOr expression, AstContext ctx) {
        return andOrStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(Not expression, AstContext ctx) {
        return notStrategy.render(expression, this, ctx);
    }

    // scalar expressions
    @Override
    public String visit(AggregateCall expression, AstContext ctx) {
        return aggregateCallStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(ColumnReference expression, AstContext ctx) {
        return columnReferenceStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(Literal<?> expression, AstContext ctx) {
        return literalStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(ScalarSubquery expression, AstContext ctx) {
        return scalarSubqueryStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(Interval interval, AstContext ctx) {
        return intervalStrategy.render(interval, this, ctx);
    }

    @Override
    public String visit(BinaryArithmeticExpression expression, AstContext ctx) {
        return binaryArithmeticExpressionStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(UnaryArithmeticExpression expression, AstContext ctx) {
        return unaryArithmeticExpressionStrategy.render(expression, this, ctx);
    }

    // functionCall
    @Override
    public String visit(Cast functionCall, AstContext ctx) {
        return castStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(Concat functionCall, AstContext ctx) {
        return concatStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(CurrentDate functionCall, AstContext ctx) {
        return currentDateStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(CurrentDateTime functionCall, AstContext ctx) {
        return currentDateTimeStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(DateArithmetic functionCall, AstContext ctx) {
        return dateArithmeticStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(ExtractDatePart functionCall, AstContext ctx) {
        return extractDatePartStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(Left functionCall, AstContext ctx) {
        return leftStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(Length functionCall, AstContext ctx) {
        return lengthStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(CharLength functionCall, AstContext ctx) {
        return charLengthStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(CharacterLength functionCall, AstContext ctx) {
        return characterLengthStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(Mod functionCall, AstContext ctx) {
        return modStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(NullScalarExpression interval, AstContext ctx) {
        return nullScalarExpressionStrategy.render(interval, this, ctx);
    }

    @Override
    public String visit(Power functionCall, AstContext ctx) {
        return powerStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(Replace functionCall, AstContext ctx) {
        return replaceStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(Round functionCall, AstContext ctx) {
        return roundStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(Substring functionCall, AstContext ctx) {
        return substringStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(Trim functionCall, AstContext ctx) {
        return trimStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(UnaryNumeric functionCall, AstContext ctx) {
        return unaryNumericStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(UnaryString functionCall, AstContext ctx) {
        return unaryStringStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(JsonExists functionCall, AstContext ctx) {
        return jsonExistsStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(JsonQuery functionCall, AstContext ctx) {
        return jsonQueryStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(JsonValue functionCall, AstContext ctx) {
        return jsonValueStrategy.render(functionCall, this, ctx);
    }

    // set expressions
    @Override
    public String visit(NullSetExpression expression, AstContext ctx) {
        return nullSetExpressionStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(ExceptExpression expression, AstContext ctx) {
        return exceptStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(IntersectExpression expression, AstContext ctx) {
        return intersectStrategy.render(expression, this, ctx);
    }

    @Override
    public String visit(UnionExpression expression, AstContext ctx) {
        return unionStrategy.render(expression, this, ctx);
    }

    // sql items
    @Override
    public String visit(TableIdentifier item, AstContext ctx) {
        return tableStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(Alias item, AstContext ctx) {
        return asStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(UpdateItem item, AstContext ctx) {
        return updateItemStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(InsertValues item, AstContext ctx) {
        return insertValueStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(InsertSource item, AstContext ctx) {
        return insertSourceStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(DefaultValues item, AstContext ctx) {
        return defaultValuesStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(MergeUsing item, AstContext ctx) {
        return mergeUsingStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(AliasedTableExpression item, AstContext ctx) {
        return aliasedTableExpressionStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(WhenMatchedUpdate item, AstContext ctx) {
        return whenMatchedUpdateStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(WhenMatchedDelete item, AstContext ctx) {
        return whenMatchedDeleteStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(WhenNotMatchedInsert item, AstContext ctx) {
        return whenNotMatchedInsertStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(ReferencesItem item, AstContext ctx) {
        return referencesItemStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(TableDefinition item, AstContext ctx) {
        return tableDefinitionStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(ColumnDefinition item, AstContext ctx) {
        return columnDefinitionStrategy.render(item, this, ctx);
    }

    @Override
    public String visit(SimpleDataType type, AstContext ctx) {
        return simpleDataTypeStrategy.render(type, this, ctx);
    }

    @Override
    public String visit(ParameterizedDataType type, AstContext ctx) {
        return parameterizedDataTypeStrategy.render(type, this, ctx);
    }

    @Override
    public String visit(PrimaryKeyDefinition constraintDefinition, AstContext ctx) {
        return primaryKeyStrategy.render(constraintDefinition, this, ctx);
    }

    @Override
    public String visit(IndexDefinition constraintDefinition, AstContext ctx) {
        return indexDefinitionStrategy.render(constraintDefinition, this, ctx);
    }

    @Override
    public String visit(NotNullConstraintDefinition constraintDefinition, AstContext ctx) {
        return notNullConstraintStrategy.render(constraintDefinition, this, ctx);
    }

    @Override
    public String visit(UniqueConstraintDefinition constraintDefinition, AstContext ctx) {
        return uniqueConstraintStrategy.render(constraintDefinition, this, ctx);
    }

    @Override
    public String visit(ForeignKeyConstraintDefinition constraintDefinition, AstContext ctx) {
        return foreignKeyConstraintStrategy.render(constraintDefinition, this, ctx);
    }

    @Override
    public String visit(CheckConstraintDefinition constraintDefinition, AstContext ctx) {
        return checkConstraintStrategy.render(constraintDefinition, this, ctx);
    }

    @Override
    public String visit(DefaultConstraintDefinition constraintDefinition, AstContext ctx) {
        return defaultConstraintStrategy.render(constraintDefinition, this, ctx);
    }

    @Override
    public String visit(RowNumber functionCall, AstContext ctx) {
        return rowNumberStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(Rank functionCall, AstContext ctx) {
        return rankStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(DenseRank functionCall, AstContext ctx) {
        return denseRankStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(Ntile functionCall, AstContext ctx) {
        return ntileStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(Lag functionCall, AstContext ctx) {
        return lagStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(Lead functionCall, AstContext ctx) {
        return leadStrategy.render(functionCall, this, ctx);
    }

    @Override
    public String visit(OverClause overClause, AstContext ctx) {
        return overClauseStrategy.render(overClause, this, ctx);
    }

    @Override
    public String visit(CustomFunctionCall functionCall, AstContext ctx) {
        return customFunctionCallStrategy.render(functionCall, this, ctx);
    }
}
