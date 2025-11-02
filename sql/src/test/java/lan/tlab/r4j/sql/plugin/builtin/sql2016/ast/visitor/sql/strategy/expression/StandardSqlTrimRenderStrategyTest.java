package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Trim;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlTrimRenderStrategyTest {

    private StandardSqlTrimRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlTrimRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql2008();
    }

    @Test
    void trim() {
        Trim trim = Trim.trim(Literal.of(" ciao "));
        String sql = strategy.render(trim, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("TRIM(' ciao ')");

        trim = Trim.trim(ColumnReference.of("Customer", "name"));
        sql = strategy.render(trim, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("TRIM(\"Customer\".\"name\")");
    }

    @Test
    void trimChars() {
        Trim trim = Trim.trim(Literal.of(" c"), Literal.of(" ciao "));
        String sql = strategy.render(trim, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("TRIM(' c' FROM ' ciao ')");

        trim = Trim.trim(Literal.of("-"), ColumnReference.of("Customer", "name"));
        sql = strategy.render(trim, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("TRIM('-' FROM \"Customer\".\"name\")");
    }

    @Test
    void trimBoth() {
        Trim trim = Trim.trimBoth(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(trim, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("TRIM(BOTH \"Customer\".\"name\")");
    }

    @Test
    void trimLeading() {
        Trim trim = Trim.trimLeading(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(trim, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("TRIM(LEADING \"Customer\".\"name\")");
    }

    @Test
    void trimTrailing() {
        Trim trim = Trim.trimTrailing(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(trim, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("TRIM(TRAILING \"Customer\".\"name\")");
    }

    @Test
    void trimBothChars() {
        Trim trim = Trim.trimBoth(Literal.of("-"), ColumnReference.of("Customer", "name"));
        String sql = strategy.render(trim, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("TRIM(BOTH '-' FROM \"Customer\".\"name\")");
    }

    @Test
    void trimLeadingChars() {
        Trim trim = Trim.trimLeading(Literal.of("-"), ColumnReference.of("Customer", "name"));
        String sql = strategy.render(trim, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("TRIM(LEADING '-' FROM \"Customer\".\"name\")");
    }

    @Test
    void trimTrailingChars() {
        Trim trim = Trim.trimTrailing(Literal.of("-"), ColumnReference.of("Customer", "name"));
        String sql = strategy.render(trim, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("TRIM(TRAILING '-' FROM \"Customer\".\"name\")");
    }
}
