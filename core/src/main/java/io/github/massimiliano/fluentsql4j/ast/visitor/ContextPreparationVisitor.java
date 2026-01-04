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
import io.github.massimiliano.fluentsql4j.ast.dql.source.FromSource;
import io.github.massimiliano.fluentsql4j.ast.dql.source.FromSubquery;
import io.github.massimiliano.fluentsql4j.ast.dql.source.join.OnJoin;
import io.github.massimiliano.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.EscapeStrategy;

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
        AstContext enriched = ctx.withFeatures(AstContext.Feature.SUBQUERY);
        // Propagate features from inner subquery to outer context
        return fromSubquery.getExpression().accept(this, enriched);
    }

    @Override
    public AstContext visit(Where clause, AstContext ctx) {
        if (clause.condition() == null
                || clause.condition() instanceof io.github.massimiliano.fluentsql4j.ast.core.predicate.NullPredicate) {
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
                || clause.condition() instanceof io.github.massimiliano.fluentsql4j.ast.core.predicate.NullPredicate) {
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
    public AstContext visit(AggregateExpressionProjection aggregateExpressionProjection, AstContext ctx) {
        // Visit the aggregate expression to detect window functions
        if (aggregateExpressionProjection.expression() != null) {
            return aggregateExpressionProjection.expression().accept(this, ctx);
        }
        return ctx;
    }

    @Override
    public AstContext visit(AggregateCallProjection aggregationFunctionProjection, AstContext ctx) {
        // Delegate to parent implementation since AggregateCallProjection extends AggregateExpressionProjection
        return visit((AggregateExpressionProjection) aggregationFunctionProjection, ctx);
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
