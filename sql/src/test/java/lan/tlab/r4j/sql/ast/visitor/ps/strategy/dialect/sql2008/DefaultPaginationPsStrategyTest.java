package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultPaginationPsStrategyTest {

    private final DefaultPaginationPsStrategy strategy = new DefaultPaginationPsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();

    @Test
    void handleLimitOnly() {
        Fetch pagination = new Fetch(0, 10);

        PsDto result = strategy.handle(pagination, renderer, ctx);

        assertThat(result.sql()).isEqualTo(" FETCH NEXT 10 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetPage1() {
        Fetch pagination = new Fetch(0, 5);

        PsDto result = strategy.handle(pagination, renderer, ctx);

        assertThat(result.sql()).isEqualTo(" FETCH NEXT 5 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetPage2() {
        Fetch pagination = new Fetch(10, 10);

        PsDto result = strategy.handle(pagination, renderer, ctx);

        assertThat(result.sql()).isEqualTo(" OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetPage3() {
        Fetch pagination = new Fetch(10, 5);

        PsDto result = strategy.handle(pagination, renderer, ctx);

        assertThat(result.sql()).isEqualTo(" OFFSET 10 ROWS FETCH NEXT 5 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetLargePage() {
        Fetch pagination = new Fetch(80, 20);

        PsDto result = strategy.handle(pagination, renderer, ctx);

        assertThat(result.sql()).isEqualTo(" OFFSET 80 ROWS FETCH NEXT 20 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetCalculation() {
        Fetch pagination = new Fetch(45, 15);

        PsDto result = strategy.handle(pagination, renderer, ctx);

        assertThat(result.sql()).isEqualTo(" OFFSET 45 ROWS FETCH NEXT 15 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }
}
