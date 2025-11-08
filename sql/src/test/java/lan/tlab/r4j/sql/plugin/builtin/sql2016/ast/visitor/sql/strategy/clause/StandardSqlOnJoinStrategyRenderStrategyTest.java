package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.dql.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.dql.source.join.OnJoin.JoinType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlOnJoinStrategyRenderStrategyTest {

    private StandardSqlOnJoinStrategyRenderStrategy strategy = new StandardSqlOnJoinStrategyRenderStrategy();
    private SqlRenderer standardSql2008 = TestDialectRendererFactory.standardSql();

    @BeforeEach
    public void SsetUp() {
        strategy = new StandardSqlOnJoinStrategyRenderStrategy();
        standardSql2008 = TestDialectRendererFactory.standardSql();
    }

    @Test
    void innerJoin() {
        OnJoin onJoin = new OnJoin(
                new TableIdentifier("Customer", "c"),
                JoinType.INNER,
                new TableIdentifier("Address", "a"),
                Comparison.eq(ColumnReference.of("c", "id"), ColumnReference.of("a", "customer_id")));

        String sql = strategy.render(onJoin, standardSql2008, new AstContext());
        assertThat(sql)
                .isEqualTo("\"Customer\" AS c INNER JOIN \"Address\" AS a ON \"c\".\"id\" = \"a\".\"customer_id\"");
    }

    @Test
    void leftJoin() {
        OnJoin onJoin = new OnJoin(
                new TableIdentifier("Customer", "c"),
                JoinType.LEFT,
                new TableIdentifier("Address", "a"),
                Comparison.eq(ColumnReference.of("c", "id"), ColumnReference.of("a", "customer_id")));

        String sql = strategy.render(onJoin, standardSql2008, new AstContext());
        assertThat(sql)
                .isEqualTo("\"Customer\" AS c LEFT JOIN \"Address\" AS a ON \"c\".\"id\" = \"a\".\"customer_id\"");
    }

    @Test
    void fullJoin() {
        OnJoin onJoin = new OnJoin(
                new TableIdentifier("Customer", "c"),
                JoinType.FULL,
                new TableIdentifier("Address", "a"),
                Comparison.eq(ColumnReference.of("c", "id"), ColumnReference.of("a", "customer_id")));

        String sql = strategy.render(onJoin, standardSql2008, new AstContext());
        assertThat(sql)
                .isEqualTo("\"Customer\" AS c FULL JOIN \"Address\" AS a ON \"c\".\"id\" = \"a\".\"customer_id\"");
    }

    @Test
    void corssJoin() {
        OnJoin onJoin = new OnJoin(
                new TableIdentifier("Customer", "c"),
                JoinType.CROSS,
                new TableIdentifier("Address", "a"),
                Comparison.eq(ColumnReference.of("c", "id"), ColumnReference.of("a", "customer_id")));

        String sql = strategy.render(onJoin, standardSql2008, new AstContext());
        assertThat(sql)
                .isEqualTo("\"Customer\" AS c CROSS JOIN \"Address\" AS a ON \"c\".\"id\" = \"a\".\"customer_id\"");
    }

    @Test
    void crossJoinWithoutOnCondition() {
        OnJoin onJoin = new OnJoin(
                new TableIdentifier("Customer", "c"), JoinType.CROSS, new TableIdentifier("Address", "a"), null);

        String sql = strategy.render(onJoin, standardSql2008, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\" AS c CROSS JOIN \"Address\" AS a");
    }
}
