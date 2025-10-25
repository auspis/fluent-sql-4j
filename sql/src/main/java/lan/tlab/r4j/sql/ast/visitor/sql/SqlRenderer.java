package lan.tlab.r4j.sql.ast.visitor.sql;

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
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.FromRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.FromSubqueryRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.GroupByRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.HavingRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.OnJoinStrategyRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.OrderByRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.SelectRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.SortingRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.WhereRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.fetch.FetchRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.AggregateCallProjectionRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.AggregateCallRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.AndOrRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.BetweenRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.BinaryArithmeticExpressionRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CastRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CharLengthRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CharacterLengthRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ColumnReferenceRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ComparisonRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ConcatRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CurrentDateRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CurrentDateTimeRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.DataLengthRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.DateArithmeticRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExceptRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExtractDatePartRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.InRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.IntersectRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.IntervalRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.IsNotNullRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.IsNullRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.LeftRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.LegthRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.LikeRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.LiteralRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ModRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.NotRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.NullScalarExpressionRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.NullSetExpressionRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.PowerRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ReplaceRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.RoundRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ScalarExpressionProjectionRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ScalarSubqueryRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.SubstringRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.TrimRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.UnaryArithmeticExpressionRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.UnaryNumericRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.UnaryStringRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.UnionRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.set.AliasedTableExpressionRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.AsRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.DefaultValuesRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.InsertSourceRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.InsertValueRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.MergeUsingRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.TableRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.UpdateItemRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.WhenMatchedDeleteRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.WhenMatchedUpdateRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.WhenNotMatchedInsertRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.ColumnDefinitionRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.IndexDefinitionRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.ParameterizedDataTypeRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.ReferencesItemRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.SimpleDataTypeRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.TableDefinitionRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint.CheckConstraintRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint.DefaultConstraintRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint.ForeignKeyConstraintRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint.NotNullConstraintRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint.PrimaryKeyRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint.UniqueConstraintRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement.CreateTableStatementRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement.DeleteStatementRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement.InsertStatementRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement.MergeStatementRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement.SelectStatementRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement.UpdateStatementRenderStrategy;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
public class SqlRenderer implements Visitor<String> {

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
    private final MergeStatementRenderStrategy mergeStatementStrategy = new MergeStatementRenderStrategy();

    @Default
    private final CreateTableStatementRenderStrategy createTableStatementStrategy =
            new CreateTableStatementRenderStrategy();

    // clause
    @Default
    private final SelectRenderStrategy selectStrategy = new SelectRenderStrategy();

    @Default
    private final AggregateCallProjectionRenderStrategy aggregateCallProjectionStrategy =
            new AggregateCallProjectionRenderStrategy();

    @Default
    private final ScalarExpressionProjectionRenderStrategy scalarExpressionProjectionStrategy =
            new ScalarExpressionProjectionRenderStrategy();

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
    private final FetchRenderStrategy paginationStrategy = FetchRenderStrategy.standardSql2008();

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
    private final AliasedTableExpressionRenderStrategy aliasedTableExpressionStrategy =
            new AliasedTableExpressionRenderStrategy();

    @Default
    private final MergeUsingRenderStrategy mergeUsingStrategy = new MergeUsingRenderStrategy();

    @Default
    private final WhenMatchedUpdateRenderStrategy whenMatchedUpdateStrategy = new WhenMatchedUpdateRenderStrategy();

    @Default
    private final WhenMatchedDeleteRenderStrategy whenMatchedDeleteStrategy = new WhenMatchedDeleteRenderStrategy();

    @Default
    private final WhenNotMatchedInsertRenderStrategy whenNotMatchedInsertStrategy =
            new WhenNotMatchedInsertRenderStrategy();

    @Default
    private final ReferencesItemRenderStrategy referencesItemStrategy = new ReferencesItemRenderStrategy();

    @Default
    private final TableDefinitionRenderStrategy tableDefinitionStrategy = new TableDefinitionRenderStrategy();

    @Default
    private final ColumnDefinitionRenderStrategy columnDefinitionStrategy = new ColumnDefinitionRenderStrategy();

    @Default
    private final SimpleDataTypeRenderStrategy simpleDataTypeStrategy = new SimpleDataTypeRenderStrategy();

    @Default
    private final ParameterizedDataTypeRenderStrategy parameterizedDataTypeStrategy =
            new ParameterizedDataTypeRenderStrategy();

    @Default
    private final PrimaryKeyRenderStrategy primaryKeyStrategy = new PrimaryKeyRenderStrategy();

    @Default
    private final IndexDefinitionRenderStrategy indexDefinitionStrategy = new IndexDefinitionRenderStrategy();

    @Default
    private final NotNullConstraintRenderStrategy notNullConstraintStrategy = new NotNullConstraintRenderStrategy();

    @Default
    private final UniqueConstraintRenderStrategy uniqueConstraintStrategy = new UniqueConstraintRenderStrategy();

    @Default
    private final ForeignKeyConstraintRenderStrategy foreignKeyConstraintStrategy =
            new ForeignKeyConstraintRenderStrategy();

    @Default
    private final CheckConstraintRenderStrategy checkConstraintStrategy = new CheckConstraintRenderStrategy();

    @Default
    private final DefaultConstraintRenderStrategy defaultConstraintStrategy = new DefaultConstraintRenderStrategy();

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
    public String visit(DataLength functionCall, AstContext ctx) {
        return dataLengthStrategy.render(functionCall, this, ctx);
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
}
