package lan.tlab.sqlbuilder.ast.visitor.ps;

import lan.tlab.sqlbuilder.ast.clause.conditional.having.Having;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.from.source.FromSource;
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
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.AggregateCallPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.AggregationFunctionProjectionPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.AndOrPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.AsPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.BetweenPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.ColumnReferencePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.ComparisonPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.DefaultValuesPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.FromClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.FromSubqueryPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.GroupByClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.HavingClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.InPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.InsertSourcePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.InsertStatementPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.InsertValuesPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.IsNotNullPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.IsNullPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.LikePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.LiteralPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.NotPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.NullBooleanExpressionPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.OnJoinPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.OrderByClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.PaginationPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.ScalarExpressionProjectionPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.SelectClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.SelectStatementPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.SortingPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.TablePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.UnionExpressionPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.WhereClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultAggregateCallPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultAggregationFunctionProjectionPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultAndOrPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultAsPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultBetweenPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultColumnReferencePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultComparisonPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultDefaultValuesPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultFromClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultFromSubqueryPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultGroupByClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultHavingClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultInPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultInsertSourcePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultInsertStatementPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultInsertValuesPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultIsNotNullPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultIsNullPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultLikePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultLiteralPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultNotPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultNullBooleanExpressionPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultOnJoinPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultOrderByClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultPaginationPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultScalarExpressionProjectionPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultSelectClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultSelectStatementPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultSortingPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultTablePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultUnionExpressionPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008.DefaultWhereClausePsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PreparedStatementVisitor implements Visitor<PsDto> {
    @Getter
    @Default
    private final EscapeStrategy escapeStrategy = EscapeStrategy.standard();

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
    private final TablePsStrategy tablePsStrategy = new DefaultTablePsStrategy();

    @Default
    private final ColumnReferencePsStrategy columnReferencePsStrategy = new DefaultColumnReferencePsStrategy();

    @Default
    private final ComparisonPsStrategy comparisonPsStrategy = new DefaultComparisonPsStrategy();

    @Default
    private final LiteralPsStrategy literalPsStrategy = new DefaultLiteralPsStrategy();

    @Default
    private final SortingPsStrategy sortingPsStrategy = new DefaultSortingPsStrategy();

    @Default
    private final OnJoinPsStrategy onJoinPsStrategy = new DefaultOnJoinPsStrategy();

    @Default
    private final AndOrPsStrategy andOrPsStrategy = new DefaultAndOrPsStrategy();

    @Default
    private final NotPsStrategy notPsStrategy = new DefaultNotPsStrategy();

    @Default
    private final AggregationFunctionProjectionPsStrategy aggregationFunctionProjectionPsStrategy =
            new DefaultAggregationFunctionProjectionPsStrategy();

    @Default
    private final ScalarExpressionProjectionPsStrategy scalarExpressionProjectionPsStrategy =
            new DefaultScalarExpressionProjectionPsStrategy();

    @Default
    private final AggregateCallPsStrategy aggregateCallPsStrategy = new DefaultAggregateCallPsStrategy();

    @Default
    private final InsertValuesPsStrategy insertValuesPsStrategy = new DefaultInsertValuesPsStrategy();

    @Default
    private final InsertSourcePsStrategy insertSourcePsStrategy = new DefaultInsertSourcePsStrategy();

    @Default
    private final DefaultValuesPsStrategy defaultValuesPsStrategy = new DefaultDefaultValuesPsStrategy();

    @Default
    private final IsNullPsStrategy isNullPsStrategy = new DefaultIsNullPsStrategy();

    @Default
    private final IsNotNullPsStrategy isNotNullPsStrategy = new DefaultIsNotNullPsStrategy();

    @Default
    private final InsertStatementPsStrategy insertStatementPsStrategy = new DefaultInsertStatementPsStrategy();

    @Default
    private final SelectStatementPsStrategy selectStatementPsStrategy = new DefaultSelectStatementPsStrategy();

    @Default
    private final PaginationPsStrategy paginationPsStrategy = new DefaultPaginationPsStrategy();

    @Default
    private final LikePsStrategy likePsStrategy = new DefaultLikePsStrategy();

    @Default
    private final UnionExpressionPsStrategy unionExpressionPsStrategy = new DefaultUnionExpressionPsStrategy();

    @Default
    private final BetweenPsStrategy betweenPsStrategy = new DefaultBetweenPsStrategy();

    @Default
    private final InPsStrategy inPsStrategy = new DefaultInPsStrategy();

    @Default
    private final AsPsStrategy asPsStrategy = new DefaultAsPsStrategy();

    @Default
    private final FromSubqueryPsStrategy fromSubqueryPsStrategy = new DefaultFromSubqueryPsStrategy();

    @Default
    private final NullBooleanExpressionPsStrategy nullBooleanExpressionPsStrategy =
            new DefaultNullBooleanExpressionPsStrategy();

    @Override
    public PsDto visit(InsertStatement stmt, AstContext ctx) {
        return insertStatementPsStrategy.handle(stmt, this, ctx);
    }

    @Override
    public PsDto visit(SelectStatement stmt, AstContext ctx) {
        return selectStatementPsStrategy.handle(stmt, this, ctx);
    }

    @Override
    public PsDto visit(Select select, AstContext ctx) {
        return selectClauseStrategy.handle(select, this, ctx);
    }

    @Override
    public PsDto visit(Table table, AstContext ctx) {
        return tablePsStrategy.handle(table, this, ctx);
    }

    @Override
    public PsDto visit(ColumnReference col, AstContext ctx) {
        return columnReferencePsStrategy.handle(col, this, ctx);
    }

    @Override
    public PsDto visit(Comparison cmp, AstContext ctx) {
        return comparisonPsStrategy.handle(cmp, this, ctx);
    }

    @Override
    public PsDto visit(Where where, AstContext ctx) {
        return whereClauseStrategy.handle(where, this, ctx);
    }

    @Override
    public PsDto visit(Literal<?> literal, AstContext ctx) {
        return literalPsStrategy.handle(literal, this, ctx);
    }

    @Override
    public PsDto visit(UpdateStatement updateStatement, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(DeleteStatement deleteStatement, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(CreateTableStatement createTableStatement, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(AggregationFunctionProjection aggregationFunctionProjection, AstContext ctx) {
        return aggregationFunctionProjectionPsStrategy.handle(aggregationFunctionProjection, this, ctx);
    }

    @Override
    public PsDto visit(ScalarExpressionProjection scalarExpressionProjection, AstContext ctx) {
        return scalarExpressionProjectionPsStrategy.handle(scalarExpressionProjection, this, ctx);
    }

    @Override
    public PsDto visit(From clause, AstContext ctx) {
        return fromClauseStrategy.handle(clause, this, ctx);
    }

    @Override
    public PsDto visit(OnJoin join, AstContext ctx) {
        return onJoinPsStrategy.handle(join, this, ctx);
    }

    @Override
    public PsDto visit(FromSubquery fromSubquery, AstContext ctx) {
        return fromSubqueryPsStrategy.handle(fromSubquery, this, ctx);
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
        return sortingPsStrategy.handle(sorting, this, ctx);
    }

    @Override
    public PsDto visit(Pagination clause, AstContext ctx) {
        return paginationPsStrategy.handle(clause, this, ctx);
    }

    @Override
    public PsDto visit(NullBooleanExpression expression, AstContext ctx) {
        return nullBooleanExpressionPsStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(Between expression, AstContext ctx) {
        return betweenPsStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(In expression, AstContext ctx) {
        return inPsStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(AndOr expression, AstContext ctx) {
        return andOrPsStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(Not expression, AstContext ctx) {
        return notPsStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(BinaryArithmeticExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(UnaryArithmeticExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Cast functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Concat functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(CurrentDate functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(CurrentDateTime functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(DateArithmetic functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(ExtractDatePart functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Left functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Length functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(CharLength functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(CharacterLength functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(DataLength functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Mod functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(NullScalarExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Power functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Replace functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Round functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Substring functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Trim functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(UnaryNumeric functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(UnaryString functionCall, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(NullSetExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(ExceptExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(IntersectExpression expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(UnionExpression expression, AstContext ctx) {
        return unionExpressionPsStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(As item, AstContext ctx) {
        return asPsStrategy.handle(item, this, ctx);
    }

    @Override
    public PsDto visit(UpdateItem item, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(ReferencesItem item, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(TableDefinition item, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(ColumnDefinition item, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(SimpleDataType type, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(ParameterizedDataType type, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(PrimaryKey item, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Index index, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(NotNullConstraint constraint, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(UniqueConstraint constraint, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(ForeignKeyConstraint constraint, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(CheckConstraint constraint, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(DefaultConstraint constraint, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(ScalarSubquery expression, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(Interval interval, AstContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsDto visit(AggregateCall expression, AstContext ctx) {
        return aggregateCallPsStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(InsertValues item, AstContext ctx) {
        return insertValuesPsStrategy.handle(item, this, ctx);
    }

    @Override
    public PsDto visit(InsertSource item, AstContext ctx) {
        return insertSourcePsStrategy.handle(item, this, ctx);
    }

    @Override
    public PsDto visit(DefaultValues item, AstContext ctx) {
        return defaultValuesPsStrategy.handle(item, this, ctx);
    }

    // Handle FromSource dispatch for FROM clause
    public PsDto visit(FromSource source, AstContext ctx) {
        return source.accept(this, ctx);
    }

    @Override
    public PsDto visit(Like expression, AstContext ctx) {
        return likePsStrategy.handle(expression, this, ctx);
    }

    @Override
    public PsDto visit(IsNull expr, AstContext ctx) {
        return isNullPsStrategy.handle(expr, this, ctx);
    }

    @Override
    public PsDto visit(IsNotNull expr, AstContext ctx) {
        return isNotNullPsStrategy.handle(expr, this, ctx);
    }
}
