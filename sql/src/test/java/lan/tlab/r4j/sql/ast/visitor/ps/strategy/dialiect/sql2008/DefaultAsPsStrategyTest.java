package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.item.As;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultAsPsStrategyTest {

    private DefaultAsPsStrategy strategy;
    private PreparedStatementVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new DefaultAsPsStrategy();
        visitor = new PreparedStatementVisitor();
        ctx = new AstContext();
    }

    @Test
    void asWithSimpleName() {
        As as = new As("userId");

        PsDto result = strategy.handle(as, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"userId\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void asWithTableAlias() {
        As as = new As("u");

        PsDto result = strategy.handle(as, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"u\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void asWithColumnAlias() {
        As as = new As("totalCount");

        PsDto result = strategy.handle(as, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"totalCount\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void asWithComplexName() {
        As as = new As("user_full_name");

        PsDto result = strategy.handle(as, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"user_full_name\"");
        assertThat(result.parameters()).isEmpty();
    }
}
