package lan.tlab.r4j.jdsql.ast.visitor;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.RowNumber;
import lan.tlab.r4j.jdsql.ast.common.expression.set.UnionExpression;
import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.dql.clause.From;
import lan.tlab.r4j.jdsql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.jdsql.ast.dql.clause.Having;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.dql.projection.AggregateCallProjection;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.dql.source.FromSubquery;
import lan.tlab.r4j.jdsql.ast.dql.source.join.OnJoin;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextPreparationVisitorTest {

    private ContextPreparationVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new ContextPreparationVisitor();
    }

    @Test
    void singleTableReturnsEmptyContext() {
        // SELECT * FROM users
        SelectStatement statement =
                SelectStatement.builder().from(From.fromTable("users")).build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.features()).isEmpty();
    }

    @Test
    void innerJoinDetectsJoinFeature() {
        // SELECT * FROM t1 INNER JOIN t2 ON t1.id = t2.t1_id
        OnJoin join = new OnJoin(
                new TableIdentifier("t1"),
                OnJoin.JoinType.INNER,
                new TableIdentifier("t2"),
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));

        SelectStatement statement =
                SelectStatement.builder().from(From.of(join)).build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.JOIN_ON)).isTrue();
    }

    @Test
    void leftJoinDetectsJoinFeature() {
        // SELECT * FROM t1 LEFT JOIN t2 ON t1.id = t2.t1_id
        OnJoin join = new OnJoin(
                new TableIdentifier("t1"),
                OnJoin.JoinType.LEFT,
                new TableIdentifier("t2"),
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));

        SelectStatement statement =
                SelectStatement.builder().from(From.of(join)).build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.JOIN_ON)).isTrue();
    }

    @Test
    void rightJoinDetectsJoinFeature() {
        // SELECT * FROM t1 RIGHT JOIN t2 ON t1.id = t2.t1_id
        OnJoin join = new OnJoin(
                new TableIdentifier("t1"),
                OnJoin.JoinType.RIGHT,
                new TableIdentifier("t2"),
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));

        SelectStatement statement =
                SelectStatement.builder().from(From.of(join)).build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.JOIN_ON)).isTrue();
    }

    @Test
    void nestedJoinAccumulatesJoinFeature() {
        // SELECT * FROM t1 INNER JOIN (t2 INNER JOIN t3 ON t2.id = t3.t2_id) ON t1.id = t2.t1_id
        OnJoin innerJoin = new OnJoin(
                new TableIdentifier("t2"),
                OnJoin.JoinType.INNER,
                new TableIdentifier("t3"),
                Comparison.eq(ColumnReference.of("t2", "id"), ColumnReference.of("t3", "t2_id")));

        OnJoin outerJoin = new OnJoin(
                new TableIdentifier("t1"),
                OnJoin.JoinType.INNER,
                innerJoin,
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));

        SelectStatement statement =
                SelectStatement.builder().from(From.of(outerJoin)).build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.JOIN_ON)).isTrue();
    }

    @Test
    void unionDetectsUnionFeature() {
        // SELECT * FROM t1 UNION SELECT * FROM t2
        SelectStatement left =
                SelectStatement.builder().from(From.fromTable("t1")).build();
        SelectStatement right =
                SelectStatement.builder().from(From.fromTable("t2")).build();

        UnionExpression union = UnionExpression.union(left, right);

        AstContext result = union.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.UNION)).isTrue();
    }

    @Test
    void whereDetectsWhereFeature() {
        // SELECT * FROM users WHERE age > 18
        SelectStatement statement = SelectStatement.builder()
                .from(From.fromTable("users"))
                .where(Where.of(Comparison.gt(ColumnReference.of("users", "age"), Literal.of(18))))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.WHERE)).isTrue();
    }

    @Test
    void groupByDetectsGroupByFeature() {
        // SELECT age FROM users GROUP BY age
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "age"))))
                .from(From.fromTable("users"))
                .groupBy(GroupBy.of(ColumnReference.of("users", "age")))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.GROUP_BY)).isTrue();
    }

    @Test
    void havingDetectsHavingFeature() {
        // SELECT COUNT(*) FROM users GROUP BY age HAVING age > 18
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.countStar())))
                .from(From.fromTable("users"))
                .groupBy(GroupBy.of(ColumnReference.of("users", "age")))
                .having(Having.of(Comparison.gt(ColumnReference.of("users", "age"), Literal.of(18))))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.GROUP_BY)).isTrue();
        assertThat(result.hasFeature(AstContext.Feature.HAVING)).isTrue();
    }

    @Test
    void windowFunctionDetectsWindowFeature() {
        // SELECT ROW_NUMBER() OVER () FROM users
        RowNumber rowNumber = new RowNumber(null);

        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(rowNumber)))
                .from(From.fromTable("users"))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.WINDOW_FUNCTION)).isTrue();
    }

    @Test
    void scalarSubqueryDetectsSubqueryFeature() {
        // SELECT * FROM users WHERE age > (SELECT AVG(age) FROM users)
        SelectStatement subquery = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.avg(ColumnReference.of("users", "age")))))
                .from(From.fromTable("users"))
                .build();

        ScalarSubquery scalarSubquery = new ScalarSubquery(subquery);

        SelectStatement statement = SelectStatement.builder()
                .from(From.fromTable("users"))
                .where(Where.of(Comparison.gt(ColumnReference.of("users", "age"), scalarSubquery)))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.WHERE)).isTrue();
        assertThat(result.hasFeature(AstContext.Feature.SUBQUERY)).isTrue();
    }

    @Test
    void fromSubqueryWithJoinPropagatesJoinFeature() {
        // SELECT * FROM (SELECT * FROM t1 INNER JOIN t2 ON t1.id = t2.t1_id) AS subq
        OnJoin innerJoin = new OnJoin(
                new TableIdentifier("t1"),
                OnJoin.JoinType.INNER,
                new TableIdentifier("t2"),
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));

        SelectStatement innerQuery =
                SelectStatement.builder().from(From.of(innerJoin)).build();

        FromSubquery fromSubquery = FromSubquery.of(innerQuery, "subq");

        SelectStatement statement =
                SelectStatement.builder().from(From.of(fromSubquery)).build();

        AstContext result = statement.accept(visitor, new AstContext());

        // Feature propagated from inner subquery
        assertThat(result.hasFeature(AstContext.Feature.JOIN_ON)).isTrue();
    }

    @Test
    void complexQueryAccumulatesAllFeatures() {
        // SELECT t1.id, COUNT(*)
        // FROM t1 INNER JOIN t2 ON t1.id = t2.t1_id
        // WHERE t1.active = true
        // GROUP BY t1.id
        // HAVING COUNT(*) > 5
        OnJoin join = new OnJoin(
                new TableIdentifier("t1"),
                OnJoin.JoinType.INNER,
                new TableIdentifier("t2"),
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));

        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new AggregateCallProjection(AggregateCall.countStar())))
                .from(From.of(join))
                .where(Where.of(Comparison.eq(ColumnReference.of("t1", "active"), Literal.of(true))))
                .groupBy(GroupBy.of(ColumnReference.of("t1", "id")))
                .having(Having.of(Comparison.gt(AggregateCall.countStar(), Literal.of(5))))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.JOIN_ON)).isTrue();
        assertThat(result.hasFeature(AstContext.Feature.WHERE)).isTrue();
        assertThat(result.hasFeature(AstContext.Feature.GROUP_BY)).isTrue();
        assertThat(result.hasFeature(AstContext.Feature.HAVING)).isTrue();
        assertThat(result.features()).hasSize(4);
    }

    @Test
    void joinPlusWhereAccumulatesBothFeatures() {
        // SELECT * FROM t1 INNER JOIN t2 ON t1.id = t2.t1_id WHERE t1.active = true
        OnJoin join = new OnJoin(
                new TableIdentifier("t1"),
                OnJoin.JoinType.INNER,
                new TableIdentifier("t2"),
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));

        SelectStatement statement = SelectStatement.builder()
                .from(From.of(join))
                .where(Where.of(Comparison.eq(ColumnReference.of("t1", "active"), Literal.of(true))))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.JOIN_ON)).isTrue();
        assertThat(result.hasFeature(AstContext.Feature.WHERE)).isTrue();
        assertThat(result.features()).hasSize(2);
    }

    @Test
    void groupByPlusHavingAccumulatesBothFeatures() {
        // SELECT age FROM users GROUP BY age HAVING age > 18
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "age"))))
                .from(From.fromTable("users"))
                .groupBy(GroupBy.of(ColumnReference.of("users", "age")))
                .having(Having.of(Comparison.gt(ColumnReference.of("users", "age"), Literal.of(18))))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.GROUP_BY)).isTrue();
        assertThat(result.hasFeature(AstContext.Feature.HAVING)).isTrue();
        assertThat(result.features()).hasSize(2);
    }

    @Test
    void whereWithSubqueryAccumulatesBothFeatures() {
        // SELECT * FROM users WHERE age > (SELECT AVG(age) FROM users)
        SelectStatement subquery = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.avg(ColumnReference.of("users", "age")))))
                .from(From.fromTable("users"))
                .build();

        ScalarSubquery scalarSubquery = new ScalarSubquery(subquery);

        SelectStatement statement = SelectStatement.builder()
                .from(From.fromTable("users"))
                .where(Where.of(Comparison.gt(ColumnReference.of("users", "age"), scalarSubquery)))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.WHERE)).isTrue();
        assertThat(result.hasFeature(AstContext.Feature.SUBQUERY)).isTrue();
        assertThat(result.features()).hasSize(2);
    }
}
