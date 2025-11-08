package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlColumnReferenceRenderStrategyTest {

    private StandardSqlColumnReferenceRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlColumnReferenceRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void ok() {
        ColumnReference columnReference = ColumnReference.of("Customer", "name");
        String sql = strategy.render(columnReference, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\".\"name\"");
    }

    @Test
    void star() {
        ColumnReference columnReference = ColumnReference.star();
        String sql = strategy.render(columnReference, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("*");
    }
}
