package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Left;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Length;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlLeftRenderStrategyTest {

    private StandardSqlLeftRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlLeftRenderStrategy();
        sqlRenderer = StandardSqlRendererFactory.standardSql();
    }

    @Test
    void ok() {
        Left func = Left.of(ColumnReference.of("Customer", "name"), 3);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LEFT(\"Customer\".\"name\", 3)");
    }

    @Test
    void withLiteralStringAndColumnLength() {
        Left func = new Left(Literal.of("Some String"), ColumnReference.of("Customer", "prefix_length"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LEFT('Some String', \"Customer\".\"prefix_length\")");
    }

    @Test
    void withArithmeticExpressionAsLength() {
        Left func = new Left(
                ColumnReference.of("Customer", "full_name"),
                ArithmeticExpression.subtraction(
                        new Length(ColumnReference.of("Customer", "full_name")), Literal.of(5)));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LEFT(\"Customer\".\"full_name\", (LENGTH(\"Customer\".\"full_name\") - 5))");
    }
}
