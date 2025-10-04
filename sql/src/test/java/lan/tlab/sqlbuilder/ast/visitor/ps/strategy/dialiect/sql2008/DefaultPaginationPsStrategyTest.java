package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.fetch.Fetch;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultPaginationPsStrategyTest {

    private final DefaultPaginationPsStrategy strategy = new DefaultPaginationPsStrategy();
    private final PreparedStatementVisitor visitor = new PreparedStatementVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void handleLimitOnly() {
        Fetch pagination = Fetch.builder().rows(10).build();

        PsDto result = strategy.handle(pagination, visitor, ctx);

        assertThat(result.sql()).isEqualTo(" FETCH NEXT 10 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetPage1() {
        Fetch pagination = Fetch.builder().rows(5).offset(0).build();

        PsDto result = strategy.handle(pagination, visitor, ctx);

        assertThat(result.sql()).isEqualTo(" FETCH NEXT 5 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetPage2() {
        Fetch pagination = Fetch.builder().rows(10).offset(10).build();

        PsDto result = strategy.handle(pagination, visitor, ctx);

        assertThat(result.sql()).isEqualTo(" OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetPage3() {
        Fetch pagination = Fetch.builder().rows(5).offset(10).build();

        PsDto result = strategy.handle(pagination, visitor, ctx);

        assertThat(result.sql()).isEqualTo(" OFFSET 10 ROWS FETCH NEXT 5 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetLargePage() {
        Fetch pagination = Fetch.builder().rows(20).offset(80).build();

        PsDto result = strategy.handle(pagination, visitor, ctx);

        assertThat(result.sql()).isEqualTo(" OFFSET 80 ROWS FETCH NEXT 20 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetCalculation() {
        Fetch pagination = Fetch.builder().rows(15).offset(45).build();

        PsDto result = strategy.handle(pagination, visitor, ctx);

        assertThat(result.sql()).isEqualTo(" OFFSET 45 ROWS FETCH NEXT 15 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }
}
