package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlLiteralRenderStrategyTest {

    private StandardSqlLiteralRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlLiteralRenderStrategy();
        sqlRenderer = StandardSqlRendererFactory.standardSql();
    }

    @Test
    void string() {
        String sql = strategy.render(Literal.of("string"), sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("'string'");
    }

    @Test
    void number() {
        String sql = strategy.render(Literal.of(23), sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("23");

        sql = strategy.render(Literal.of(23.023), sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("23.023");
    }

    @Test
    void booleanValue() {
        String sql = strategy.render(Literal.of(true), sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("true");

        sql = strategy.render(Literal.of(false), sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("false");
    }

    @Test
    void nullValue() {
        String sql = strategy.render(Literal.ofNull(), sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("null");
    }
}
