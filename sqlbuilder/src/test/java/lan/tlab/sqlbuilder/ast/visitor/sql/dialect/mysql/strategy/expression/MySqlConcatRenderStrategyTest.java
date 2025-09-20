package lan.tlab.sqlbuilder.ast.visitor.sql.dialect.mysql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Concat;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Substring;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression.ConcatRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MySqlConcatRenderStrategyTest {

    private ConcatRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new MySqlConcatRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void concat_literal() {
        Concat concat = Concat.concat(Literal.of("first"), Literal.of("second"), Literal.of("third"));
        String sql = strategy.render(concat, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CONCAT('first', 'second', 'third')");
    }

    @Test
    void concat_columnReference() {
        Concat concat = Concat.concat(Literal.of("Mr."), ColumnReference.of("Customer", "name"));
        String sql = strategy.render(concat, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CONCAT('Mr.', \"Customer\".\"name\")");
    }

    @Test
    void concatWithSeparator() {
        Concat concat = Concat.concatWithSeparator(
                " - ", Literal.of("Mr."), Literal.of("other"), ColumnReference.of("Customer", "name"));
        String sql = strategy.render(concat, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CONCAT_WS(' - ', 'Mr.', 'other', \"Customer\".\"name\")");
    }

    @Test
    void concatWithSeparator_functionCall() {
        Concat concat = Concat.concatWithSeparator(
                " - ",
                Literal.of("prefix"),
                Substring.of(ColumnReference.of("Customer", "name"), Literal.of(1), Literal.of(5)),
                Literal.of("suffix"));
        String sql = strategy.render(concat, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CONCAT_WS(' - ', 'prefix', SUBSTRING(\"Customer\".\"name\", 1, 5), 'suffix')");
    }
}
