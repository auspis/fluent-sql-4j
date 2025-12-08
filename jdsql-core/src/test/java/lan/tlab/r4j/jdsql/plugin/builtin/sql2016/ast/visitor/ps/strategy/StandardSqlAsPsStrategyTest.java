package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.core.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlAsPsStrategyTest {

    private StandardSqlAsPsStrategy strategy;
    private PreparedStatementRenderer specFactory;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlAsPsStrategy();
        specFactory = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void asWithSimpleName() {
        Alias as = new Alias("userId");

        PreparedStatementSpec result = strategy.handle(as, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"userId\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void asWithTableAlias() {
        Alias as = new Alias("u");

        PreparedStatementSpec result = strategy.handle(as, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"u\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void asWithColumnAlias() {
        Alias as = new Alias("totalCount");

        PreparedStatementSpec result = strategy.handle(as, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"totalCount\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void asWithComplexName() {
        Alias as = new Alias("user_full_name");

        PreparedStatementSpec result = strategy.handle(as, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"user_full_name\"");
        assertThat(result.parameters()).isEmpty();
    }
}
