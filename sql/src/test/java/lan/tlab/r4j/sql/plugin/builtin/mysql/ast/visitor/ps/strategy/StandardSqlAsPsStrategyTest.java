package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.identifier.Alias;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlAsPsStrategyTest {

    private StandardSqlAsPsStrategy strategy;
    private PreparedStatementRenderer renderer;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlAsPsStrategy();
        renderer = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void asWithSimpleName() {
        Alias as = new Alias("userId");

        PsDto result = strategy.handle(as, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"userId\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void asWithTableAlias() {
        Alias as = new Alias("u");

        PsDto result = strategy.handle(as, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"u\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void asWithColumnAlias() {
        Alias as = new Alias("totalCount");

        PsDto result = strategy.handle(as, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"totalCount\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void asWithComplexName() {
        Alias as = new Alias("user_full_name");

        PsDto result = strategy.handle(as, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"user_full_name\"");
        assertThat(result.parameters()).isEmpty();
    }
}
