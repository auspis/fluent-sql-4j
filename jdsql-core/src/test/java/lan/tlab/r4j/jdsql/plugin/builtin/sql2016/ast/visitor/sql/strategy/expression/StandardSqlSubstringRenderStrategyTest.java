package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Length;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Substring;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.UnaryString;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlSubstringRenderStrategyTest {

    private StandardSqlSubstringRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlSubstringRenderStrategy();
        sqlRenderer = StandardSqlRendererFactory.standardSql();
    }

    @Test
    void ok() {
        Substring func = Substring.of(ColumnReference.of("Customer", "name"), 5);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SUBSTRING(\"Customer\".\"name\", 5)");
    }

    @Test
    void columnReference() {
        StandardSqlSubstringRenderStrategy strategy = new StandardSqlSubstringRenderStrategy();
        Substring substring = Substring.of(ColumnReference.of("Customer", "name"), 0, 3);
        String sql = strategy.render(substring, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SUBSTRING(\"Customer\".\"name\", 0, 3)");
    }

    @Test
    void Length() {
        Substring func = Substring.of(ColumnReference.of("Customer", "name"), 5, 10);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SUBSTRING(\"Customer\".\"name\", 5, 10)");
    }

    @Test
    void literalStringExpression() {
        Substring func = Substring.of(Literal.of("Hello World"), 7);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SUBSTRING('Hello World', 7)");
    }

    @Test
    void scalarExpression() {
        Substring func = Substring.of(
                UnaryString.upper(ColumnReference.of("Customer", "name")),
                Literal.of(3),
                ArithmeticExpression.subtraction(new Length(ColumnReference.of("Product", "code")), Literal.of(2)));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SUBSTRING(UPPER(\"Customer\".\"name\"), 3, (LENGTH(\"Product\".\"code\") - 2))");
    }
}
