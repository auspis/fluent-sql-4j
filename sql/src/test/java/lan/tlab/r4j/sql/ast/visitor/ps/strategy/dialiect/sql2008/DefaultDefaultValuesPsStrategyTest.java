package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.item.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultDefaultValuesPsStrategyTest {

    private DefaultDefaultValuesPsStrategy strategy;
    private PreparedStatementVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new DefaultDefaultValuesPsStrategy();
        visitor = new PreparedStatementVisitor();
        ctx = new AstContext();
    }

    @Test
    void defaultValues() {
        DefaultValues defaultValues = new DefaultValues();

        PsDto result = strategy.handle(defaultValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT VALUES");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void defaultValuesWithDifferentContext() {
        DefaultValues defaultValues = new DefaultValues();
        AstContext differentCtx = new AstContext();

        PsDto result = strategy.handle(defaultValues, visitor, differentCtx);

        assertThat(result.sql()).isEqualTo("DEFAULT VALUES");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void defaultValuesIsStateless() {
        DefaultValues defaultValues1 = new DefaultValues();
        DefaultValues defaultValues2 = new DefaultValues();

        PsDto result1 = strategy.handle(defaultValues1, visitor, ctx);
        PsDto result2 = strategy.handle(defaultValues2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo("DEFAULT VALUES");
        assertThat(result1.parameters()).isEmpty();
        assertThat(result2.sql()).isEqualTo("DEFAULT VALUES");
        assertThat(result2.parameters()).isEmpty();

        // Results should be equal
        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
