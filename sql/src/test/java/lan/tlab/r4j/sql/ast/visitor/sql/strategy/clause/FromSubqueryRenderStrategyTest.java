package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.from.source.FromSubquery;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.item.As;
import lan.tlab.r4j.sql.ast.expression.item.Table;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FromSubqueryRenderStrategyTest {

    private FromSubqueryRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new FromSubqueryRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        SelectStatement subquery = SelectStatement.builder()
                .select(new Select())
                .from(From.builder().source(new Table("Customer")).build())
                .build();
        FromSubquery fromSubquery = FromSubquery.of(subquery, As.nullObject());
        String sql = strategy.render(fromSubquery, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(SELECT * FROM \"Customer\")");
    }

    @Test
    void alias() {
        SelectStatement subquery = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("Customer", "db_name"), new As("name")),
                        new ScalarExpressionProjection(ColumnReference.of("Customer", "score"), new As("score"))))
                .from(From.builder().source(new Table("Customer")).build())
                .build();
        FromSubquery fromSubquery = FromSubquery.of(subquery, "tmp");
        String sql = strategy.render(fromSubquery, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
			(SELECT \"Customer\".\"db_name\" AS name, \
			\"Customer\".\"score\" AS score \
			FROM \"Customer\") AS tmp""");
    }
}
