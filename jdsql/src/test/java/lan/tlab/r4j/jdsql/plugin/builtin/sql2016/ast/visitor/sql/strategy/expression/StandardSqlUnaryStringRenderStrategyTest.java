package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Substring;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.UnaryString;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlUnaryStringRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlUnaryStringRenderStrategyTest {

    private StandardSqlUnaryStringRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlUnaryStringRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void lower_columnReference() {
        UnaryString func = UnaryString.lower(ColumnReference.of("Customer", "value"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LOWER(\"Customer\".\"value\")");
    }

    @Test
    void lower_literal() {
        UnaryString func = UnaryString.lower(Literal.of("ABC"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LOWER('ABC')");
    }

    @Test
    void lower_arithmeticExpression() {
        UnaryString func = UnaryString.lower(Substring.of(ColumnReference.of("Customer", "name"), 2));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LOWER(SUBSTRING(\"Customer\".\"name\", 2))");
    }
}
