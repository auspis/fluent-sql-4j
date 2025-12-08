package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.number.Power;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.Test;

class StandardSqlPowerPsStrategyTest {

    @Test
    void handlesPowerWithTwoLiterals() {
        var strategy = new StandardSqlPowerPsStrategy();
        var power = new Power(Literal.of(2), Literal.of(8));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(power, visitor, ctx);

        assertThat(result.sql()).isEqualTo("POWER(?, ?)");
        assertThat(result.parameters()).containsExactly(2, 8);
    }

    @Test
    void handlesPowerWithColumnAndLiteral() {
        var strategy = new StandardSqlPowerPsStrategy();
        var power = new Power(ColumnReference.of("calculations", "base"), Literal.of(3));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(power, visitor, ctx);

        assertThat(result.sql()).isEqualTo("POWER(\"base\", ?)");
        assertThat(result.parameters()).containsExactly(3);
    }

    @Test
    void handlesPowerWithTwoColumns() {
        var strategy = new StandardSqlPowerPsStrategy();
        var power = new Power(ColumnReference.of("math", "base"), ColumnReference.of("math", "exponent"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(power, visitor, ctx);

        assertThat(result.sql()).isEqualTo("POWER(\"base\", \"exponent\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesPowerWithLiteralAndColumn() {
        var strategy = new StandardSqlPowerPsStrategy();
        var power = new Power(Literal.of(10), ColumnReference.of("settings", "log_level"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(power, visitor, ctx);

        assertThat(result.sql()).isEqualTo("POWER(?, \"log_level\")");
        assertThat(result.parameters()).containsExactly(10);
    }
}
