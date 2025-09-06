package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Replace;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReplaceRenderStrategyTest {

    private ReplaceRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new ReplaceRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        Replace func = Replace.of(ColumnReference.of("Customer", "address"), Literal.of("Street"), Literal.of("St."));
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql).isEqualTo("REPLACE(\"Customer\".\"address\", 'Street', 'St.')");
    }

    @Test
    void withAllLiterals() {
        Replace func = Replace.of(Literal.of("Hello World"), Literal.of("o"), Literal.of("x"));
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql).isEqualTo("REPLACE('Hello World', 'o', 'x')");
    }

    @Test
    void withColumnReferences() {
        Replace func = Replace.of(
                ColumnReference.of("Customer", "description"),
                ColumnReference.of("Customer", "old_word"),
                ColumnReference.of("Customer", "new_word"));
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql)
                .isEqualTo(
                        "REPLACE(\"Customer\".\"description\", \"Customer\".\"old_word\", \"Customer\".\"new_word\")");
    }
}
