package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.from.source.FromSource;
import lan.tlab.r4j.sql.ast.clause.from.source.FromSubquery;
import lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin.JoinType;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FromRenderStrategyTest {

    private FromRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new FromRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void table() {
        From from = From.fromTable("Customer");
        String sql = strategy.render(from, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("FROM \"Customer\"");
    }

    @Test
    void tableWithAlias() {
        From from = From.fromTable("Customer", "c");
        String sql = strategy.render(from, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("FROM \"Customer\" AS c");
    }

    @Test
    void subquery() {
        SelectStatement subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("Customer", "id"))))
                .from(From.fromTable("Customer"))
                .build();
        From from = From.of(FromSubquery.of(subquery, "c"));
        assertThat(strategy.render(from, sqlRenderer, new AstContext()))
                .isEqualTo("FROM (SELECT \"Customer\".\"id\" FROM \"Customer\") AS c");
    }

    @Test
    void innerJoin() {
        OnJoin join = new OnJoin(
                new TableIdentifier("users", "u"),
                JoinType.INNER,
                new TableIdentifier("orders", "o"),
                Comparison.eq(ColumnReference.of("u", "id"), ColumnReference.of("o", "user_id")));
        From from = From.of(join);
        String sql = strategy.render(from, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\"");
    }

    @Test
    void multipleTablesImplicitCrossJoin() {
        From from = From.of(new TableIdentifier("table1"), new TableIdentifier("table2"));
        String sql = strategy.render(from, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("FROM \"table1\", \"table2\"");
    }

    @Test
    void chainedJoin() {
        FromSource join1 = new OnJoin(
                new TableIdentifier("a"),
                JoinType.INNER,
                new TableIdentifier("b"),
                Comparison.eq(ColumnReference.of("a", "id"), ColumnReference.of("b", "a_id")));
        FromSource join2 = new OnJoin(
                join1,
                JoinType.LEFT,
                new TableIdentifier("c"),
                Comparison.eq(ColumnReference.of("b", "id"), ColumnReference.of("c", "b_id")));
        From from = From.of(join2);
        String sql = strategy.render(from, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "FROM \"a\" INNER JOIN \"b\" ON \"a\".\"id\" = \"b\".\"a_id\" LEFT JOIN \"c\" ON \"b\".\"id\" = \"c\".\"b_id\"");
    }
}
