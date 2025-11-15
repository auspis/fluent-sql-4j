package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Replace;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlReplaceRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlReplaceRenderStrategyTest {

    private StandardSqlReplaceRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlReplaceRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void ok() {
        Replace func = new Replace(ColumnReference.of("Customer", "address"), Literal.of("Street"), Literal.of("St."));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("REPLACE(\"Customer\".\"address\", 'Street', 'St.')");
    }

    @Test
    void withAllLiterals() {
        Replace func = new Replace(Literal.of("Hello World"), Literal.of("o"), Literal.of("x"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("REPLACE('Hello World', 'o', 'x')");
    }

    @Test
    void withColumnReferences() {
        Replace func = new Replace(
                ColumnReference.of("Customer", "description"),
                ColumnReference.of("Customer", "old_word"),
                ColumnReference.of("Customer", "new_word"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "REPLACE(\"Customer\".\"description\", \"Customer\".\"old_word\", \"Customer\".\"new_word\")");
    }
}
