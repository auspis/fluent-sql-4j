package lan.tlab.r4j.jdsql.ast.visitor;

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
import lan.tlab.r4j.jdsql.ast.dql.source.FromSource;
import lan.tlab.r4j.jdsql.ast.dql.source.FromSubquery;
import lan.tlab.r4j.jdsql.ast.dql.source.join.OnJoin;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.escape.EscapeStrategy;

/**
 * A visitor that traverses an AST and enriches the context with detected features (JOIN, UNION,
 * WHERE, HAVING, GROUP_BY, WINDOW_FUNCTION, SUBQUERY). This visitor performs a pre-analysis pass
 * before rendering to enable context-aware rendering decisions.
 *
 * <p>This visitor is stateless and can be reused across multiple statements.
 */
public class ContextPreparationVisitor implements Visitor<AstContext> {

    @Override
    public EscapeStrategy getEscapeStrategy() {
        throw new UnsupportedOperationException("ContextPreparationVisitor does not support escape strategy");
    }

    @Override
    public AstContext visit(SelectStatement statement, AstContext ctx) {
        AstContext enriched = ctx;

        // Visit all clauses and accumulate features
        // Note: null objects won't add features since they have empty conditions/expressions
        if (statement.getSelect() != null) {
            enriched = statement.getSelect().accept(this, enriched);
        }
        if (statement.getFrom() != null) {
            enriched = statement.getFrom().accept(this, enriched);
        }
        if (statement.getWhere() != null) {
            enriched = statement.getWhere().accept(this, enriched);
        }
        if (statement.getGroupBy() != null) {
            enriched = statement.getGroupBy().accept(this, enriched);
        }
        if (statement.getHaving() != null) {
            enriched = statement.getHaving().accept(this, enriched);
        }
        if (statement.getOrderBy() != null) {
            enriched = statement.getOrderBy().accept(this, enriched);
        }
        if (statement.getFetch() != null) {
            enriched = statement.getFetch().accept(this, enriched);
        }

        return enriched;
    }

    @Override
    public AstContext visit(Select clause, AstContext ctx) {
        AstContext enriched = ctx;
        for (var projection : clause.projections()) {
            enriched = projection.accept(this, enriched);
        }
        return enriched;
    }

    @Override
    public AstContext visit(From clause, AstContext ctx) {
        AstContext enriched = ctx;
        for (FromSource source : clause.sources()) {
            enriched = source.accept(this, enriched);
        }
        return enriched;
    }

    @Override
    public AstContext visit(OnJoin onJoin, AstContext ctx) {
        AstContext enriched = ctx.withFeatures(AstContext.Feature.JOIN_ON);

        // Visit left, right, and condition recursively
        enriched = onJoin.left().accept(this, enriched);
        enriched = onJoin.right().accept(this, enriched);

        if (onJoin.onCondition() != null) {
            enriched = onJoin.onCondition().accept(this, enriched);
        }

        return enriched;
    }

    @Override
    public AstContext visit(FromSubquery fromSubquery, AstContext ctx) {
        // Propagate features from inner subquery to outer context
        return fromSubquery.getExpression().accept(this, ctx);
    }

    @Override
    public AstContext visit(Where clause, AstContext ctx) {
        if (clause.condition() == null
                || clause.condition() instanceof lan.tlab.r4j.jdsql.ast.common.predicate.NullPredicate) {
            return ctx;
        }
        AstContext enriched = ctx.withFeatures(AstContext.Feature.WHERE);
        enriched = clause.condition().accept(this, enriched);
        return enriched;
    }

    @Override
    public AstContext visit(GroupBy clause, AstContext ctx) {
        if (clause.groupingExpressions() == null || clause.groupingExpressions().isEmpty()) {
            return ctx;
        }
        AstContext enriched = ctx.withFeatures(AstContext.Feature.GROUP_BY);
        for (var groupingExpression : clause.groupingExpressions()) {
            enriched = groupingExpression.accept(this, enriched);
        }
        return enriched;
    }

    @Override
    public AstContext visit(Having clause, AstContext ctx) {
        if (clause.condition() == null
                || clause.condition() instanceof lan.tlab.r4j.jdsql.ast.common.predicate.NullPredicate) {
            return ctx;
        }
        AstContext enriched = ctx.withFeatures(AstContext.Feature.HAVING);
        enriched = clause.condition().accept(this, enriched);
        return enriched;
    }

    @Override
    public AstContext visit(ScalarSubquery expression, AstContext ctx) {
        AstContext enriched = ctx.withFeatures(AstContext.Feature.SUBQUERY);
        if (expression.tableExpression() != null) {
            enriched = expression.tableExpression().accept(this, enriched);
        }
        return enriched;
    }

    @Override
    public AstContext visit(UnionExpression expression, AstContext ctx) {
        AstContext enriched = ctx.withFeatures(AstContext.Feature.UNION);
        enriched = expression.left().accept(this, enriched);
        enriched = expression.right().accept(this, enriched);
        return enriched;
    }

    @Override
    public AstContext visit(IntersectExpression expression, AstContext ctx) {
        AstContext enriched = ctx.withFeatures(AstContext.Feature.UNION);
        enriched = expression.leftSetExpression().accept(this, enriched);
        enriched = expression.rightSetExpression().accept(this, enriched);
        return enriched;
    }

    @Override
    public AstContext visit(ExceptExpression expression, AstContext ctx) {
        AstContext enriched = ctx.withFeatures(AstContext.Feature.UNION);
        enriched = expression.left().accept(this, enriched);
        enriched = expression.right().accept(this, enriched);
        return enriched;
    }

    @Override
    public AstContext visit(OverClause overClause, AstContext ctx) {
        return ctx.withFeatures(AstContext.Feature.WINDOW_FUNCTION);
    }

    @Override
    public AstContext visit(RowNumber functionCall, AstContext ctx) {
        AstContext enriched = ctx.withFeatures(AstContext.Feature.WINDOW_FUNCTION);
        if (functionCall.overClause() != null) {
            enriched = functionCall.overClause().accept(this, enriched);
        }
        return enriched;
    }

    @Override
    public AstContext visit(Rank functionCall, AstContext ctx) {
        AstContext enriched = ctx.withFeatures(AstContext.Feature.WINDOW_FUNCTION);
        if (functionCall.overClause() != null) {
            enriched = functionCall.overClause().accept(this, enriched);
        }
        return enriched;
    }

    @Override
    public AstContext visit(DenseRank functionCall, AstContext ctx) {
        AstContext enriched = ctx.withFeatures(AstContext.Feature.WINDOW_FUNCTION);
        if (functionCall.overClause() != null) {
            enriched = functionCall.overClause().accept(this, enriched);
        }
        return enriched;
    }

    @Override
    public AstContext visit(Ntile functionCall, AstContext ctx) {
        AstContext enriched = ctx.withFeatures(AstContext.Feature.WINDOW_FUNCTION);
        if (functionCall.overClause() != null) {
            enriched = functionCall.overClause().accept(this, enriched);
        }
        return enriched;
    }

    @Override
    public AstContext visit(Lag functionCall, AstContext ctx) {
        AstContext enriched = ctx.withFeatures(AstContext.Feature.WINDOW_FUNCTION);
        if (functionCall.overClause() != null) {
            enriched = functionCall.overClause().accept(this, enriched);
        }
        return enriched;
    }

    @Override
    public AstContext visit(Lead functionCall, AstContext ctx) {
        AstContext enriched = ctx.withFeatures(AstContext.Feature.WINDOW_FUNCTION);
        if (functionCall.overClause() != null) {
            enriched = functionCall.overClause().accept(this, enriched);
        }
        return enriched;
    }

    // Default implementations for all other visit methods - return context unchanged
    @Override
    public AstContext visit(InsertStatement insertStatement, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(UpdateStatement updateStatement, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(DeleteStatement deleteStatement, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(MergeStatement mergeStatement, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(CreateTableStatement createTableStatement, AstContext ctx) {
        return ctx.withFeatures(AstContext.Feature.DDL);
    }

    @Override
    public AstContext visit(AggregateCallProjection aggregationFunctionProjection, AstContext ctx) {
        // Visit the aggregate call to detect window functions
        if (aggregationFunctionProjection.expression() != null) {
            return aggregationFunctionProjection.expression().accept(this, ctx);
        }
        return ctx;
    }

    @Override
    public AstContext visit(ScalarExpressionProjection scalarExpressionProjection, AstContext ctx) {
        // Visit the expression to detect window functions and subqueries
        if (scalarExpressionProjection.expression() != null) {
            return scalarExpressionProjection.expression().accept(this, ctx);
        }
        return ctx;
    }

    @Override
    public AstContext visit(OrderBy clause, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Sorting sorting, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Fetch clause, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(NullPredicate expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Between expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Comparison expression, AstContext ctx) {
        AstContext enriched = ctx;
        // Visit left and right expressions to detect subqueries
        if (expression.lhs() != null) {
            enriched = expression.lhs().accept(this, enriched);
        }
        if (expression.rhs() != null) {
            enriched = expression.rhs().accept(this, enriched);
        }
        return enriched;
    }

    @Override
    public AstContext visit(In expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(IsNotNull expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(IsNull expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Like expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(AndOr expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Not expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(AggregateCall expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(ColumnReference expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Interval interval, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Literal<?> expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(BinaryArithmeticExpression expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(UnaryArithmeticExpression expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Cast functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Concat functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(CurrentDate functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(CurrentDateTime functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(DateArithmetic functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(ExtractDatePart functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Left functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Length functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(CharLength functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(CharacterLength functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Mod functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(NullScalarExpression expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Power functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Replace functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Round functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Substring functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Trim functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(UnaryNumeric functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(UnaryString functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(JsonExists functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(JsonQuery functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(JsonValue functionCall, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(NullSetExpression expression, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(TableIdentifier item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(AliasedTableExpression item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(Alias item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(UpdateItem item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(InsertValues item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(InsertSource item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(DefaultValues item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(MergeUsing item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(WhenMatchedUpdate item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(WhenMatchedDelete item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(WhenNotMatchedInsert item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(ReferencesItem item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(TableDefinition item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(ColumnDefinition item, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(SimpleDataType type, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(ParameterizedDataType type, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(PrimaryKeyDefinition constraintDefinition, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(IndexDefinition constraintDefinition, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(NotNullConstraintDefinition constraintDefinition, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(UniqueConstraintDefinition constraintDefinition, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(ForeignKeyConstraintDefinition constraintDefinition, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(CheckConstraintDefinition constraintDefinition, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(DefaultConstraintDefinition constraintDefinition, AstContext ctx) {
        return ctx;
    }

    @Override
    public AstContext visit(CustomFunctionCall functionCall, AstContext ctx) {
        return ctx;
    }
}
