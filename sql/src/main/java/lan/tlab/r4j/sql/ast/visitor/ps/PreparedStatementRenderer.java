package lan.tlab.r4j.sql.ast.visitor.ps;

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
import lan.tlab.r4j.sql.ast.statement.dml.UpdateStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertSource;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertValues;
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
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LeftPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LikePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LiteralPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ModPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NotNullConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NotPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NullPredicatePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NullScalarExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NullSetExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.OnJoinPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.OrderByClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ParameterizedDataTypePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.PowerPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.PrimaryKeyPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ReferencesItemPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ReplacePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.RoundPsStrategy;
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
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultAggregateCallPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultAggregationFunctionProjectionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultAndOrPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultAsPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultBetweenPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultBinaryArithmeticExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultCastPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultCharLengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultCharacterLengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultCheckConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultColumnDefinitionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultColumnReferencePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultComparisonPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultConcatPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultCreateTableStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultCurrentDatePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultCurrentDateTimePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultDataLengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultDateArithmeticPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultDefaultConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultDefaultValuesPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultDeleteStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultExceptExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultExtractDatePartPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultForeignKeyConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultFromClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultFromSubqueryPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultGroupByClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultHavingClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultInPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultIndexDefinitionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultInsertSourcePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultInsertStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultInsertValuesPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultIntersectExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultIntervalPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultIsNotNullPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultIsNullPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultLeftPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultLengthPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultLikePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultLiteralPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultModPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultNotNullConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultNotPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultNullPredicatePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultNullScalarExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultNullSetExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultOnJoinPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultOrderByClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultPaginationPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultParameterizedDataTypePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultPowerPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultPrimaryKeyPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultReferencesItemPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultReplacePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultRoundPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultScalarExpressionProjectionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultScalarSubqueryPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultSelectClausePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultSelectStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultSimpleDataTypePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultSortingPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultSubstringPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultTableDefinitionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultTablePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultTrimPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultUnaryArithmeticExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultUnaryNumericPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultUnaryStringPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultUnionExpressionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultUniqueConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultUpdateItemPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultUpdateStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008.DefaultWhereClausePsStrategy;
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
    private final SelectClausePsStrategy selectClauseStrategy = new DefaultSelectClausePsStrategy();

    @Default
    private final FromClausePsStrategy fromClauseStrategy = new DefaultFromClausePsStrategy();

    @Default
    private final WhereClausePsStrategy whereClauseStrategy = new DefaultWhereClausePsStrategy();

    @Default
    private final GroupByClausePsStrategy groupByClauseStrategy = new DefaultGroupByClausePsStrategy();

    @Default
    private final HavingClausePsStrategy havingClauseStrategy = new DefaultHavingClausePsStrategy();

    @Default
    private final OrderByClausePsStrategy orderByClauseStrategy = new DefaultOrderByClausePsStrategy();

    @Default
    private final TablePsStrategy tableStrategy = new DefaultTablePsStrategy();

    @Default
    private final ColumnReferencePsStrategy columnReferenceStrategy = new DefaultColumnReferencePsStrategy();

    @Default
    private final ComparisonPsStrategy comparisonStrategy = new DefaultComparisonPsStrategy();

    @Default
    private final LiteralPsStrategy literalStrategy = new DefaultLiteralPsStrategy();

    @Default
    private final SortingPsStrategy sortingStrategy = new DefaultSortingPsStrategy();

    @Default
    private final OnJoinPsStrategy onJoinStrategy = new DefaultOnJoinPsStrategy();

    @Default
    private final AndOrPsStrategy andOrStrategy = new DefaultAndOrPsStrategy();

    @Default
    private final NotPsStrategy notStrategy = new DefaultNotPsStrategy();

    @Default
    private final AggregationFunctionProjectionPsStrategy aggregationFunctionProjectionStrategy =
            new DefaultAggregationFunctionProjectionPsStrategy();

    @Default
    private final ScalarExpressionProjectionPsStrategy scalarExpressionProjectionStrategy =
            new DefaultScalarExpressionProjectionPsStrategy();

    @Default
    private final AggregateCallPsStrategy aggregateCallStrategy = new DefaultAggregateCallPsStrategy();

    @Default
    private final InsertValuesPsStrategy insertValuesStrategy = new DefaultInsertValuesPsStrategy();

    @Default
    private final InsertSourcePsStrategy insertSourceStrategy = new DefaultInsertSourcePsStrategy();

    @Default
    private final DefaultValuesPsStrategy defaultValuesStrategy = new DefaultDefaultValuesPsStrategy();

    @Default
    private final IsNullPsStrategy isNullStrategy = new DefaultIsNullPsStrategy();

    @Default
    private final IsNotNullPsStrategy isNotNullStrategy = new DefaultIsNotNullPsStrategy();

    @Default
    private final InsertStatementPsStrategy insertStatementStrategy = new DefaultInsertStatementPsStrategy();

    @Default
    private final SelectStatementPsStrategy selectStatementStrategy = new DefaultSelectStatementPsStrategy();

    @Default
    private final FetchPsStrategy paginationStrategy = new DefaultPaginationPsStrategy();

    @Default
    private final LikePsStrategy likeStrategy = new DefaultLikePsStrategy();

    @Default
    private final UnionExpressionPsStrategy unionExpressionStrategy = new DefaultUnionExpressionPsStrategy();

    @Default
    private final BetweenPsStrategy betweenStrategy = new DefaultBetweenPsStrategy();

    @Default
    private final BinaryArithmeticExpressionPsStrategy binaryArithmeticExpressionStrategy =
            new DefaultBinaryArithmeticExpressionPsStrategy();

    @Default
    private final UnaryArithmeticExpressionPsStrategy unaryArithmeticExpressionStrategy =
            new DefaultUnaryArithmeticExpressionPsStrategy();

    @Default
    private final CastPsStrategy castStrategy = new DefaultCastPsStrategy();

    @Default
    private final ConcatPsStrategy concatStrategy = new DefaultConcatPsStrategy();

    @Default
    private final CurrentDatePsStrategy currentDateStrategy = new DefaultCurrentDatePsStrategy();

    @Default
    private final CurrentDateTimePsStrategy currentDateTimeStrategy = new DefaultCurrentDateTimePsStrategy();

    @Default
    private final DateArithmeticPsStrategy dateArithmeticStrategy = new DefaultDateArithmeticPsStrategy();

    @Default
    private final ExceptExpressionPsStrategy exceptExpressionStrategy = new DefaultExceptExpressionPsStrategy();

    @Default
    private final ExtractDatePartPsStrategy extractDatePartStrategy = new DefaultExtractDatePartPsStrategy();

    @Default
    private final IntersectExpressionPsStrategy intersectExpressionStrategy =
            new DefaultIntersectExpressionPsStrategy();

    @Default
    private final IntervalPsStrategy intervalStrategy = new DefaultIntervalPsStrategy();

    @Default
    private final LeftPsStrategy leftStrategy = new DefaultLeftPsStrategy();

    @Default
    private final LengthPsStrategy lengthStrategy = new DefaultLengthPsStrategy();

    @Default
    private final ModPsStrategy modStrategy = new DefaultModPsStrategy();

    @Default
    private final NullScalarExpressionPsStrategy nullScalarExpressionStrategy =
            new DefaultNullScalarExpressionPsStrategy();

    @Default
    private final NullSetExpressionPsStrategy nullSetExpressionStrategy = new DefaultNullSetExpressionPsStrategy();

    @Default
    private final PowerPsStrategy powerStrategy = new DefaultPowerPsStrategy();

    @Default
    private final ReplacePsStrategy replaceStrategy = new DefaultReplacePsStrategy();

    @Default
    private final ReferencesItemPsStrategy referencesItemStrategy = new DefaultReferencesItemPsStrategy();

    @Default
    private final TableDefinitionPsStrategy tableDefinitionStrategy = new DefaultTableDefinitionPsStrategy();

    @Default
    private final ColumnDefinitionPsStrategy columnDefinitionStrategy = new DefaultColumnDefinitionPsStrategy();

    @Default
    private final SimpleDataTypePsStrategy simpleDataTypeStrategy = new DefaultSimpleDataTypePsStrategy();

    @Default
    private final ParameterizedDataTypePsStrategy parameterizedDataTypeStrategy =
            new DefaultParameterizedDataTypePsStrategy();

    @Default
    private final PrimaryKeyPsStrategy primaryKeyDefinitionStrategy = new DefaultPrimaryKeyPsStrategy();

    @Default
    private final IndexDefinitionPsStrategy indexDefinitionStrategy = new DefaultIndexDefinitionPsStrategy();

    @Default
    private final NotNullConstraintPsStrategy notNullConstraintDefinitionStrategy =
            new DefaultNotNullConstraintPsStrategy();

    @Default
    private final UniqueConstraintPsStrategy uniqueConstraintStrategy = new DefaultUniqueConstraintPsStrategy();

    @Default
    private final ForeignKeyConstraintPsStrategy foreignKeyConstraintStrategy =
            new DefaultForeignKeyConstraintPsStrategy();

    @Default
    private final CheckConstraintPsStrategy checkConstraintStrategy = new DefaultCheckConstraintPsStrategy();

    @Default
    private final DefaultConstraintPsStrategy defaultConstraintStrategy = new DefaultDefaultConstraintPsStrategy();

    @Default
    private final RoundPsStrategy roundStrategy = new DefaultRoundPsStrategy();

    @Default
    private final SubstringPsStrategy substringStrategy = new DefaultSubstringPsStrategy();

    @Default
    private final TrimPsStrategy trimStrategy = new DefaultTrimPsStrategy();

    @Default
    private final UnaryNumericPsStrategy unaryNumericStrategy = new DefaultUnaryNumericPsStrategy();

    @Default
    private final UnaryStringPsStrategy unaryStringStrategy = new DefaultUnaryStringPsStrategy();

    @Default
    private final ScalarSubqueryPsStrategy scalarSubqueryStrategy = new DefaultScalarSubqueryPsStrategy();

    @Default
    private final CreateTableStatementPsStrategy createTableStatementStrategy =
            new DefaultCreateTableStatementPsStrategy();

    @Default
    private final CharLengthPsStrategy charLengthStrategy = new DefaultCharLengthPsStrategy();

    @Default
    private final CharacterLengthPsStrategy characterLengthStrategy = new DefaultCharacterLengthPsStrategy();

    @Default
    private final DataLengthPsStrategy dataLengthStrategy = new DefaultDataLengthPsStrategy();

    @Default
    private final InPsStrategy inStrategy = new DefaultInPsStrategy();

    @Default
    private final AsPsStrategy asStrategy = new DefaultAsPsStrategy();

    @Default
    private final FromSubqueryPsStrategy fromSubqueryStrategy = new DefaultFromSubqueryPsStrategy();

    @Default
    private final NullPredicatePsStrategy nullPredicateStrategy = new DefaultNullPredicatePsStrategy();

    @Default
    private final UpdateItemPsStrategy updateItemStrategy = new DefaultUpdateItemPsStrategy();

    @Default
    private final UpdateStatementPsStrategy updateStatementStrategy = new DefaultUpdateStatementPsStrategy();

    @Default
    private final DeleteStatementPsStrategy deleteStatementStrategy = new DefaultDeleteStatementPsStrategy();

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
}
