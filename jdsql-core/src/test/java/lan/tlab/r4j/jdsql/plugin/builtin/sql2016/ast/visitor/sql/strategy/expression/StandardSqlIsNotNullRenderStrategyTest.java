package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.predicate.IsNotNull;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlIsNotNullRenderStrategyTest {

    private StandardSqlIsNotNullRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlIsNotNullRenderStrategy();
        sqlRenderer = StandardSqlRendererFactory.standardSql();
    }

    @Test
    void ok() {
        IsNotNull exp = new IsNotNull(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\".\"name\" IS NOT NULL");
    }
}
