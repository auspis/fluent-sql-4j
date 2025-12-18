package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.dql.clause.Fetch;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.FetchPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlPaginationPsStrategyTest {

    private final FetchPsStrategy strategy = new StandardSqlPaginationPsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void handleLimitOnly() {
        Fetch pagination = new Fetch(0, 10);

        PreparedStatementSpec result = strategy.handle(pagination, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("FETCH NEXT 10 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetPage1() {
        Fetch pagination = new Fetch(0, 5);

        PreparedStatementSpec result = strategy.handle(pagination, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("FETCH NEXT 5 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetPage2() {
        Fetch pagination = new Fetch(10, 10);

        PreparedStatementSpec result = strategy.handle(pagination, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetPage3() {
        Fetch pagination = new Fetch(10, 5);

        PreparedStatementSpec result = strategy.handle(pagination, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("OFFSET 10 ROWS FETCH NEXT 5 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetLargePage() {
        Fetch pagination = new Fetch(80, 20);

        PreparedStatementSpec result = strategy.handle(pagination, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("OFFSET 80 ROWS FETCH NEXT 20 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleLimitWithOffsetCalculation() {
        Fetch pagination = new Fetch(45, 15);

        PreparedStatementSpec result = strategy.handle(pagination, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("OFFSET 45 ROWS FETCH NEXT 15 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }
}
