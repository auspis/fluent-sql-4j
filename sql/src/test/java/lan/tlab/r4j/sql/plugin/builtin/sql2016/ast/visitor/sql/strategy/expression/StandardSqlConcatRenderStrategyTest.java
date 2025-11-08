package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Concat;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Substring;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ConcatRenderStrategy;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlConcatRenderStrategyTest {

    private ConcatRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlConcatRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void literal() {
        Concat concat = Concat.concat(Literal.of("first"), Literal.of("second"), Literal.of("third"));
        String sql = strategy.render(concat, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("('first' || 'second' || 'third')");
    }

    @Test
    void columnReference() {
        Concat concat = Concat.concat(Literal.of("Mr."), ColumnReference.of("Customer", "name"));
        String sql = strategy.render(concat, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("('Mr.' || \"Customer\".\"name\")");
    }

    @Test
    void functionCall() {
        Concat concat = Concat.concat(Literal.of("Mr."), Substring.of(ColumnReference.of("Customer", "name"), 1, 5));
        String sql = strategy.render(concat, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("('Mr.' || SUBSTRING(\"Customer\".\"name\", 1, 5))");
    }
}
