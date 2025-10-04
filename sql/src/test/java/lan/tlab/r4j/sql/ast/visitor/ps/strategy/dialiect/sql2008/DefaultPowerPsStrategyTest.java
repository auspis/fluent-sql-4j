package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Power;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultPowerPsStrategyTest {

    @Test
    void handlesPowerWithTwoLiterals() {
        var strategy = new DefaultPowerPsStrategy();
        var power = new Power(Literal.of(2), Literal.of(8));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(power, visitor, ctx);

        assertThat(result.sql()).isEqualTo("POWER(?, ?)");
        assertThat(result.parameters()).containsExactly(2, 8);
    }

    @Test
    void handlesPowerWithColumnAndLiteral() {
        var strategy = new DefaultPowerPsStrategy();
        var power = new Power(ColumnReference.of("calculations", "base"), Literal.of(3));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(power, visitor, ctx);

        assertThat(result.sql()).isEqualTo("POWER(\"base\", ?)");
        assertThat(result.parameters()).containsExactly(3);
    }

    @Test
    void handlesPowerWithTwoColumns() {
        var strategy = new DefaultPowerPsStrategy();
        var power = new Power(ColumnReference.of("math", "base"), ColumnReference.of("math", "exponent"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(power, visitor, ctx);

        assertThat(result.sql()).isEqualTo("POWER(\"base\", \"exponent\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesPowerWithLiteralAndColumn() {
        var strategy = new DefaultPowerPsStrategy();
        var power = new Power(Literal.of(10), ColumnReference.of("settings", "log_level"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(power, visitor, ctx);

        assertThat(result.sql()).isEqualTo("POWER(?, \"log_level\")");
        assertThat(result.parameters()).containsExactly(10);
    }
}
