package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.groupby.GroupBy;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.ExtractDatePart;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GroupByRenderStrategyTest {

    private GroupByRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new GroupByRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        GroupBy clause = GroupBy.of(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(clause, renderer, new AstContext());
        assertThat(sql).isEqualTo("GROUP BY \"Customer\".\"name\"");
    }

    @Test
    void multipleExpressions() {
        GroupBy clause = GroupBy.of(ColumnReference.of("Customer", "name"), ColumnReference.of("Customer", "email"));
        String sql = strategy.render(clause, renderer, new AstContext());
        assertThat(sql).isEqualTo("GROUP BY \"Customer\".\"name\", \"Customer\".\"email\"");
    }

    @Test
    void functionCall() {
        GroupBy clause = GroupBy.of(
                ColumnReference.of("Customer", "name"),
                ExtractDatePart.year(ColumnReference.of("Customer", "birthdate")));
        String sql = strategy.render(clause, renderer, new AstContext());
        assertThat(sql).isEqualTo("GROUP BY \"Customer\".\"name\", YEAR(\"Customer\".\"birthdate\")");
    }
}
