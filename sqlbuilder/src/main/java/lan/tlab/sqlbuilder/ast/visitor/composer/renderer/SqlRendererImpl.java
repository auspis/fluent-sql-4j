package lan.tlab.sqlbuilder.ast.visitor.composer.renderer;

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
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.FromRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.FromSubqueryRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.GroupByRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.HavingRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.OnJoinStrategyRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.OrderByRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.SelectRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.SortingRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.WhereRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.pagination.PaginationRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.escape.EscapeStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.AggregateCallRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.AggregationFunctionProjectionRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.AndOrRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.BetweenRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.BinaryArithmeticExpressionRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.CastRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.CharLengthRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.CharacterLengthRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.ColumnReferenceRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.ComparisonRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.ConcatRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.CurrentDateRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.CurrentDateTimeRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.DataLengthRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.DateArithmeticRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.ExceptRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.ExtractDatePartRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.InRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.IntersectRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.IntervalRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.IsNotNullRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.IsNullRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.LeftRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.LegthRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.LikeRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.LiteralRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.ModRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.NotRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.NullScalarExpressionRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.NullSetExpressionRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.PowerRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.ReplaceRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.RoundRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.ScalarExpressionProjectionRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.ScalarSubqueryRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.SubstringRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.TrimRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.UnaryArithmeticExpressionRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.UnaryNumericRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.UnaryStringRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.UnionRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.AsRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.ColumnDefinitionRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.DefaultValuesRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.InsertSourceRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.InsertValueRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.ReferencesItemRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.TableDefinitionRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.TableRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.UpdateItemRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.constraint.ForeignKeyConstraintRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.constraint.NotNullConstraintRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.constraint.PrimaryKeyDefinitionRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.constraint.UniqueConstraintRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement.CreateTableStatementRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement.DeleteStatementRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement.InsertStatementRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement.SelectStatementRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement.UpdateStatementRenderStrategy;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
public class SqlRendererImpl implements SqlRenderer {

    @Getter
    @Default
    private final EscapeStrategy escapeStrategy = EscapeStrategy.standard();

    // statements
    @Default
    private final SelectStatementRenderStrategy selectStatementStrategy = new SelectStatementRenderStrategy();

    @Default
    private final InsertStatementRenderStrategy insertStatementStrategy = new InsertStatementRenderStrategy();

    @Default
    private final UpdateStatementRenderStrategy updateStatementStrategy = new UpdateStatementRenderStrategy();

    @Default
    private final DeleteStatementRenderStrategy deleteStatementStrategy = new DeleteStatementRenderStrategy();

    @Default
    private final CreateTableStatementRenderStrategy createTableStatementStrategy = new CreateTableStatementRenderStrategy();

    // clause
    @Default
    private final SelectRenderStrategy selectStrategy = new SelectRenderStrategy();

    @Default
    private final AggregationFunctionProjectionRenderStrategy aggregationFunctionProjectionStrategy = new AggregationFunctionProjectionRenderStrategy();

    @Default
    private final ScalarExpressionProjectionRenderStrategy scalarExpressionProjectionStrategy = new ScalarExpressionProjectionRenderStrategy();

    @Default
    private final FromRenderStrategy fromStrategy = new FromRenderStrategy();

    @Default
    private final OnJoinStrategyRenderStrategy onJoinStrategy = new OnJoinStrategyRenderStrategy();

    @Default
    private final FromSubqueryRenderStrategy fromSubqueryStrategy = new FromSubqueryRenderStrategy();

    @Default
    private final WhereRenderStrategy whereStrategy = new WhereRenderStrategy();

    @Default
    private final GroupByRenderStrategy groupByStrategy = new GroupByRenderStrategy();

    @Default
    private final HavingRenderStrategy havingStrategy = new HavingRenderStrategy();

    @Default
    private final OrderByRenderStrategy orderByStrategy = new OrderByRenderStrategy();

    @Default
    private final SortingRenderStrategy sortingStrategy = new SortingRenderStrategy();

    @Default
    private final PaginationRenderStrategy paginationStrategy = PaginationRenderStrategy.standardSql2008();

    // boolean expressions
    @Default
    private final BetweenRenderStrategy betweenStrategy = new BetweenRenderStrategy();

    @Default
    private final ComparisonRenderStrategy comparisonStrategy = new ComparisonRenderStrategy();

    @Default
    private final InRenderStrategy inStrategy = new InRenderStrategy();

    @Default
    private final IsNotNullRenderStrategy isNotNullStrategy = new IsNotNullRenderStrategy();

    @Default
    private final IsNullRenderStrategy isNullStrategy = new IsNullRenderStrategy();

    @Default
    private final LikeRenderStrategy likeStrategy = new LikeRenderStrategy();

    @Default
    private final AndOrRenderStrategy andOrStrategy = new AndOrRenderStrategy();

    @Default
    private final NotRenderStrategy notStrategy = new NotRenderStrategy();

    // scalar expressions
    @Default
    private final AggregateCallRenderStrategy aggregateCallStrategy = new AggregateCallRenderStrategy();

    @Default
    private final ColumnReferenceRenderStrategy columnReferenceStrategy = new ColumnReferenceRenderStrategy();

    @Default
    private final IntervalRenderStrategy intervalStrategy = new IntervalRenderStrategy();

    @Default
    private final LiteralRenderStrategy literalStrategy = new LiteralRenderStrategy();

    @Default
    private final ScalarSubqueryRenderStrategy scalarSubqueryStrategy = new ScalarSubqueryRenderStrategy();

    @Default
    private final BinaryArithmeticExpressionRenderStrategy binaryArithmeticExpressionStrategy =
            new BinaryArithmeticExpressionRenderStrategy();

    @Default
    private final UnaryArithmeticExpressionRenderStrategy unaryArithmeticExpressionStrategy =
            new UnaryArithmeticExpressionRenderStrategy();

    // scalar expressions - function calls
    @Default
    private final CastRenderStrategy castStrategy = CastRenderStrategy.standard();

    @Default
    private final ConcatRenderStrategy concatStrategy = ConcatRenderStrategy.standardSql2008();

    @Default
    private final CurrentDateRenderStrategy currentDateStrategy = CurrentDateRenderStrategy.standardSql2008();

    @Default
    private final CurrentDateTimeRenderStrategy currentDateTimeStrategy =
            CurrentDateTimeRenderStrategy.standardSql2008();

    @Default
    private final DateArithmeticRenderStrategy dateArithmeticStrategy = DateArithmeticRenderStrategy.standardSql2008();

    @Default
    private final ExtractDatePartRenderStrategy extractDatePartStrategy = new ExtractDatePartRenderStrategy();

    @Default
    private final LeftRenderStrategy leftStrategy = new LeftRenderStrategy();

    @Default
    private final LegthRenderStrategy lengthStrategy = LegthRenderStrategy.standardSql2008();

    @Default
    private final CharLengthRenderStrategy charLengthStrategy = CharLengthRenderStrategy.standardSql2008();

    @Default
    private final CharacterLengthRenderStrategy characterLengthStrategy =
            CharacterLengthRenderStrategy.standardSql2008();

    @Default
    private final DataLengthRenderStrategy dataLengthStrategy = DataLengthRenderStrategy.standardSql2008();

    @Default
    private final ModRenderStrategy modStrategy = new ModRenderStrategy();

    @Default
    private final NullScalarExpressionRenderStrategy nullScalarExpressionStrategy =
            new NullScalarExpressionRenderStrategy();

    @Default
    private final PowerRenderStrategy powerStrategy = new PowerRenderStrategy();

    @Default
    private final ReplaceRenderStrategy replaceStrategy = new ReplaceRenderStrategy();

    @Default
    private final RoundRenderStrategy roundStrategy = new RoundRenderStrategy();

    @Default
    private final SubstringRenderStrategy substringStrategy = new SubstringRenderStrategy();

    @Default
    private final TrimRenderStrategy trimStrategy = new TrimRenderStrategy();

    @Default
    private final UnaryNumericRenderStrategy unaryNumericStrategy = new UnaryNumericRenderStrategy();

    @Default
    private final UnaryStringRenderStrategy unaryStringStrategy = new UnaryStringRenderStrategy();

    // set expressions
    @Default
    private final NullSetExpressionRenderStrategy nullSetExpressionStrategy = new NullSetExpressionRenderStrategy();

    @Default
    private final ExceptRenderStrategy exceptStrategy = ExceptRenderStrategy.standardSql2008();

    @Default
    private final IntersectRenderStrategy intersectStrategy = new IntersectRenderStrategy();

    @Default
    private final UnionRenderStrategy unionStrategy = new UnionRenderStrategy();

    // sql items
    @Default
    private final TableRenderStrategy tableStrategy = new TableRenderStrategy();

    @Default
    private final AsRenderStrategy asStrategy = new AsRenderStrategy();

    @Default
    private final UpdateItemRenderStrategy updateItemStrategy = new UpdateItemRenderStrategy();

    @Default
    private final InsertValueRenderStrategy insertValueStrategy = new InsertValueRenderStrategy();

    @Default
    private final InsertSourceRenderStrategy insertSourceStrategy = new InsertSourceRenderStrategy();

    @Default
    private final DefaultValuesRenderStrategy defaultValuesStrategy = new DefaultValuesRenderStrategy();
    
    @Default
    private final ReferencesItemRenderStrategy referencesItemStrategy = new ReferencesItemRenderStrategy();

    @Default
    private final TableDefinitionRenderStrategy tableDefinitionStrategy = new TableDefinitionRenderStrategy();

    @Default
    private final ColumnDefinitionRenderStrategy columnDefinitionStrategy = new ColumnDefinitionRenderStrategy();

    @Default
    private final PrimaryKeyDefinitionRenderStrategy primaryKeyDefinitionStrategy =
            new PrimaryKeyDefinitionRenderStrategy();

    @Default
    private final NotNullConstraintRenderStrategy notNullConstraintStrategy = new NotNullConstraintRenderStrategy();

    @Default
    private final UniqueConstraintRenderStrategy uniqueConstraintStrategy = new UniqueConstraintRenderStrategy();

    @Default
    private final ForeignKeyConstraintRenderStrategy foreignKeyConstraintStrategy = new ForeignKeyConstraintRenderStrategy();

    // statements
    @Override
    public String visit(SelectStatement statement) {
        return selectStatementStrategy.render(statement, this);
    }

    @Override
    public String visit(InsertStatement statement) {
        return insertStatementStrategy.render(statement, this);
    }

    @Override
    public String visit(UpdateStatement statement) {
        return updateStatementStrategy.render(statement, this);
    }

    @Override
    public String visit(DeleteStatement statement) {
        return deleteStatementStrategy.render(statement, this);
    }

    @Override
    public String visit(CreateTableStatement statement) {
        return createTableStatementStrategy.render(statement, this);
    }

    // clause
    @Override
    public String visit(Select clause) {
        return selectStrategy.render(clause, this);
    }

    @Override
    public String visit(AggregationFunctionProjection projection) {
        return aggregationFunctionProjectionStrategy.render(projection, this);
    }

    @Override
    public String visit(ScalarExpressionProjection projection) {
        return scalarExpressionProjectionStrategy.render(projection, this);
    }

    @Override
    public String visit(From clause) {
        return fromStrategy.render(clause, this);
    }

    @Override
    public String visit(OnJoin onJoin) {
        return onJoinStrategy.render(onJoin, this);
    }

    @Override
    public String visit(FromSubquery fromSubquery) {
        return fromSubqueryStrategy.render(fromSubquery, this);
    }

    @Override
    public String visit(Where clause) {
        return whereStrategy.render(clause, this);
    }

    @Override
    public String visit(GroupBy clause) {
        return groupByStrategy.render(clause, this);
    }

    @Override
    public String visit(Having clause) {
        return havingStrategy.render(clause, this);
    }

    @Override
    public String visit(OrderBy clause) {
        return orderByStrategy.render(clause, this);
    }

    @Override
    public String visit(Sorting sorting) {
        return sortingStrategy.render(sorting, this);
    }

    @Override
    public String visit(Pagination clause) {
        return paginationStrategy.render(clause, this);
    }

    // boolean expressions
    @Override
    public String visit(NullBooleanExpression expression) {
        return nullScalarExpressionStrategy.render(expression, this);
    }

    @Override
    public String visit(Between expression) {
        return betweenStrategy.render(expression, this);
    }

    @Override
    public String visit(Comparison expression) {
        return comparisonStrategy.render(expression, this);
    }

    @Override
    public String visit(In expression) {
        return inStrategy.render(expression, this);
    }

    @Override
    public String visit(IsNotNull expression) {
        return isNotNullStrategy.render(expression, this);
    }

    @Override
    public String visit(IsNull expression) {
        return isNullStrategy.render(expression, this);
    }

    @Override
    public String visit(Like expression) {
        return likeStrategy.render(expression, this);
    }

    // boolean expressions - logical
    @Override
    public String visit(AndOr expression) {
        return andOrStrategy.render(expression, this);
    }

    @Override
    public String visit(Not expression) {
        return notStrategy.render(expression, this);
    }

    // scalar expressions
    @Override
    public String visit(AggregateCall expression) {
        return aggregateCallStrategy.render(expression, this);
    }

    @Override
    public String visit(ColumnReference expression) {
        return columnReferenceStrategy.render(expression, this);
    }

    @Override
    public String visit(Literal<?> expression) {
        return literalStrategy.render(expression, this);
    }

    @Override
    public String visit(ScalarSubquery expression) {
        return scalarSubqueryStrategy.render(expression, this);
    }

    @Override
    public String visit(Interval interval) {
        return intervalStrategy.render(interval, this);
    }

    @Override
    public String visit(BinaryArithmeticExpression expression) {
        return binaryArithmeticExpressionStrategy.render(expression, this);
    }

    @Override
    public String visit(UnaryArithmeticExpression expression) {
        return unaryArithmeticExpressionStrategy.render(expression, this);
    }

    // functionCall
    @Override
    public String visit(Cast functionCall) {
        return castStrategy.render(functionCall, this);
    }

    @Override
    public String visit(Concat functionCall) {
        return concatStrategy.render(functionCall, this);
    }

    @Override
    public String visit(CurrentDate functionCall) {
        return currentDateStrategy.render(functionCall, this);
    }

    @Override
    public String visit(CurrentDateTime functionCall) {
        return currentDateTimeStrategy.render(functionCall, this);
    }

    @Override
    public String visit(DateArithmetic functionCall) {
        return dateArithmeticStrategy.render(functionCall, this);
    }

    @Override
    public String visit(ExtractDatePart functionCall) {
        return extractDatePartStrategy.render(functionCall, this);
    }

    @Override
    public String visit(Left functionCall) {
        return leftStrategy.render(functionCall, this);
    }

    @Override
    public String visit(Length functionCall) {
        return lengthStrategy.render(functionCall, this);
    }

    @Override
    public String visit(CharLength functionCall) {
        return charLengthStrategy.render(functionCall, this);
    }

    @Override
    public String visit(CharacterLength functionCall) {
        return characterLengthStrategy.render(functionCall, this);
    }

    @Override
    public String visit(DataLength functionCall) {
        return dataLengthStrategy.render(functionCall, this);
    }

    @Override
    public String visit(Mod functionCall) {
        return modStrategy.render(functionCall, this);
    }

    @Override
    public String visit(NullScalarExpression interval) {
        return nullScalarExpressionStrategy.render(interval, this);
    }

    @Override
    public String visit(Power functionCall) {
        return powerStrategy.render(functionCall, this);
    }

    @Override
    public String visit(Replace functionCall) {
        return replaceStrategy.render(functionCall, this);
    }

    @Override
    public String visit(Round functionCall) {
        return roundStrategy.render(functionCall, this);
    }

    @Override
    public String visit(Substring functionCall) {
        return substringStrategy.render(functionCall, this);
    }

    @Override
    public String visit(Trim functionCall) {
        return trimStrategy.render(functionCall, this);
    }

    @Override
    public String visit(UnaryNumeric functionCall) {
        return unaryNumericStrategy.render(functionCall, this);
    }

    @Override
    public String visit(UnaryString functionCall) {
        return unaryStringStrategy.render(functionCall, this);
    }

    // set expressions
    @Override
    public String visit(NullSetExpression expression) {
        return nullSetExpressionStrategy.render(expression, this);
    }

    @Override
    public String visit(ExceptExpression expression) {
        return exceptStrategy.render(expression, this);
    }

    @Override
    public String visit(IntersectExpression expression) {
        return intersectStrategy.render(expression, this);
    }

    @Override
    public String visit(UnionExpression expression) {
        return unionStrategy.render(expression, this);
    }

    // sql items
    @Override
    public String visit(Table item) {
        return tableStrategy.render(item, this);
    }

    @Override
    public String visit(As item) {
        return asStrategy.render(item, this);
    }

    @Override
    public String visit(UpdateItem item) {
        return updateItemStrategy.render(item, this);
    }

    @Override
    public String visit(InsertValues item) {
        return insertValueStrategy.render(item, this);
    }

    @Override
    public String visit(InsertSource item) {
        return insertSourceStrategy.render(item, this);
    }

    @Override
    public String visit(DefaultValues item) {
        return defaultValuesStrategy.render(item, this);
    }
    
    @Override
    public String visit(ReferencesItem item) {
        return referencesItemStrategy.render(item, this);
    }

    @Override
    public String visit(TableDefinition item) {
        return tableDefinitionStrategy.render(item, this);
    }

    @Override
    public String visit(ColumnDefinition item) {
        return columnDefinitionStrategy.render(item, this);
    }

    @Override
    public String visit(PrimaryKey item) {
        return primaryKeyDefinitionStrategy.render(item, this);
    }

    @Override
    public String visit(NotNullConstraint constraint) {
        return notNullConstraintStrategy.render(constraint, this);
    }

    @Override
    public String visit(UniqueConstraint constraint) {
        return uniqueConstraintStrategy.render(constraint, this);
    }

    @Override
    public String visit(ForeignKeyConstraint constraint) {
        return foreignKeyConstraintStrategy.render(constraint, this);
    }

    @Override
    public String visit(CheckConstraint constraint) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String visit(DefaultConstraint constraint) {
        // TODO Auto-generated method stub
        return null;
    }

}
