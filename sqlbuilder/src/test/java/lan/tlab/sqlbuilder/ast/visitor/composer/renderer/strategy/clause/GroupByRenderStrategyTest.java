package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.ExtractDatePart;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GroupByRenderStrategyTest {

    private GroupByRenderStrategy strategy;
    private SqlRendererImpl renderer;

    @BeforeEach
    public void setUp() {
        strategy = new GroupByRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        GroupBy clause = GroupBy.of(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(clause, renderer);
        assertThat(sql).isEqualTo("GROUP BY \"Customer\".\"name\"");
    }

    @Test
    void multipleExpressions() {
        GroupBy clause = GroupBy.of(ColumnReference.of("Customer", "name"), ColumnReference.of("Customer", "email"));
        String sql = strategy.render(clause, renderer);
        assertThat(sql).isEqualTo("GROUP BY \"Customer\".\"name\", \"Customer\".\"email\"");
    }

    @Test
    void functionCall() {
        GroupBy clause = GroupBy.of(
                ColumnReference.of("Customer", "name"),
                ExtractDatePart.year(ColumnReference.of("Customer", "birthdate")));
        String sql = strategy.render(clause, renderer);
        assertThat(sql).isEqualTo("GROUP BY \"Customer\".\"name\", YEAR(\"Customer\".\"birthdate\")");
    }
}
