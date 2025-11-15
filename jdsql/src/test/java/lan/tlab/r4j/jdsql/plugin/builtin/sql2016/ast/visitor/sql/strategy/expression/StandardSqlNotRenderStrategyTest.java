package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.predicate.Like;
import lan.tlab.r4j.jdsql.ast.common.predicate.logical.Not;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlNotRenderStrategyTest {

    private StandardSqlNotRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlNotRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void ok() {
        Not exp = new Not(new Like(ColumnReference.of("Customer", "name"), "A%"));
        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("NOT (\"Customer\".\"name\" LIKE 'A%')");
    }
}
