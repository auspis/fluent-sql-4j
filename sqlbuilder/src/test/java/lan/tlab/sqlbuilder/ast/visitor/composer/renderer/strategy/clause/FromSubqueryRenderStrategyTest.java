package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.from.source.FromSubquery;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.item.factory.TestTableFactory;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FromSubqueryRenderStrategyTest {

    private FromSubqueryRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

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
        String sql = strategy.render(fromSubquery, sqlRenderer);
        assertThat(sql).isEqualTo("(SELECT * FROM \"Customer\")");
    }

    @Test
    void alias() {
        SelectStatement subquery = SelectStatement.builder()
                .select(Select.allColumns(TestTableFactory.customer()))
                .from(From.builder().source(new Table("Customer")).build())
                .build();
        FromSubquery fromSubquery = FromSubquery.of(subquery, "tmp");
        String sql = strategy.render(fromSubquery, sqlRenderer);
        assertThat(sql)
                .isEqualTo(
                        """
			(SELECT \"Customer\".\"db_name\" AS name, \
			\"Customer\".\"db_surname\" AS surname, \
			\"Customer\".\"score\" AS score, \
			\"Customer\".\"rating\" AS rating, \
			\"Customer\".\"birthdate\" AS birthdate \
			FROM \"Customer\") AS tmp""");
    }
}
