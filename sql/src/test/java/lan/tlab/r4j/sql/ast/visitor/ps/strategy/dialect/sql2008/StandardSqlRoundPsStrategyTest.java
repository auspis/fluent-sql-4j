package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Round;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlRoundPsStrategyTest {

    private StandardSqlRoundPsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlRoundPsStrategy();
        visitor = PreparedStatementRenderer.builder().build();
        ctx = new AstContext();
    }

    @Test
    void roundWithLiteralOnly() {
        Round round = Round.of(Literal.of(3.14159));

        PsDto result = strategy.handle(round, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ROUND(?)");
        assertThat(result.parameters()).containsExactly(3.14159);
    }

    @Test
    void roundWithLiteralAndDecimalPlaces() {
        Round round = new Round(Literal.of(3.14159), Literal.of(2));

        PsDto result = strategy.handle(round, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ROUND(?, ?)");
        assertThat(result.parameters()).containsExactly(3.14159, 2);
    }

    @Test
    void roundWithColumnOnly() {
        Round round = Round.of(ColumnReference.of("products", "price"));

        PsDto result = strategy.handle(round, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ROUND(\"price\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void roundWithColumnAndDecimalPlaces() {
        Round round = Round.of(ColumnReference.of("products", "price"), 2);

        PsDto result = strategy.handle(round, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ROUND(\"price\", ?)");
        assertThat(result.parameters()).containsExactly(2);
    }

    @Test
    void roundWithMixedExpressions() {
        Round round = new Round(ColumnReference.of("orders", "amount"), Literal.of(3));

        PsDto result = strategy.handle(round, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ROUND(\"amount\", ?)");
        assertThat(result.parameters()).containsExactly(3);
    }
}
