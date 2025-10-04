package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin.JoinType;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OnJoinStrategyRenderStrategyTest {

    private OnJoinStrategyRenderStrategy strategy = new OnJoinStrategyRenderStrategy();
    private SqlRenderer standardSql2008 = SqlRendererFactory.standardSql2008();

    @BeforeEach
    public void SsetUp() {
        strategy = new OnJoinStrategyRenderStrategy();
        standardSql2008 = SqlRendererFactory.standardSql2008();
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
}
