package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.function.number.Round;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlRoundPsStrategy;

class StandardSqlRoundPsStrategyTest {

    private StandardSqlRoundPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlRoundPsStrategy();
        visitor = AstToPreparedStatementSpecVisitor.builder().build();
        ctx = new AstContext();
    }

    @Test
    void roundWithLiteralOnly() {
        Round round = Round.of(Literal.of(3.14159));

        PreparedStatementSpec result = strategy.handle(round, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ROUND(?)");
        assertThat(result.parameters()).containsExactly(3.14159);
    }

    @Test
    void roundWithLiteralAndDecimalPlaces() {
        Round round = new Round(Literal.of(3.14159), Literal.of(2));

        PreparedStatementSpec result = strategy.handle(round, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ROUND(?, ?)");
        assertThat(result.parameters()).containsExactly(3.14159, 2);
    }

    @Test
    void roundWithColumnOnly() {
        Round round = Round.of(ColumnReference.of("products", "price"));

        PreparedStatementSpec result = strategy.handle(round, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ROUND(\"price\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void roundWithColumnAndDecimalPlaces() {
        Round round = Round.of(ColumnReference.of("products", "price"), 2);

        PreparedStatementSpec result = strategy.handle(round, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ROUND(\"price\", ?)");
        assertThat(result.parameters()).containsExactly(2);
    }

    @Test
    void roundWithMixedExpressions() {
        Round round = new Round(ColumnReference.of("orders", "amount"), Literal.of(3));

        PreparedStatementSpec result = strategy.handle(round, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ROUND(\"amount\", ?)");
        assertThat(result.parameters()).containsExactly(3);
    }
}
