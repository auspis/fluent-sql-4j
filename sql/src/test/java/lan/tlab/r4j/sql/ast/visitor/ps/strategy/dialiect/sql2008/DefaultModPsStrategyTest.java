package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Mod;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultModPsStrategyTest {

    @Test
    void handlesModWithTwoLiterals() {
        var strategy = new DefaultModPsStrategy();
        var mod = new Mod(Literal.of(10), Literal.of(3));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(mod, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MOD(?, ?)");
        assertThat(result.parameters()).containsExactly(10, 3);
    }

    @Test
    void handlesModWithColumnAndLiteral() {
        var strategy = new DefaultModPsStrategy();
        var mod = new Mod(ColumnReference.of("orders", "total"), Literal.of(100));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(mod, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MOD(\"total\", ?)");
        assertThat(result.parameters()).containsExactly(100);
    }

    @Test
    void handlesModWithTwoColumns() {
        var strategy = new DefaultModPsStrategy();
        var mod =
                new Mod(ColumnReference.of("calculations", "dividend"), ColumnReference.of("calculations", "divisor"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(mod, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MOD(\"dividend\", \"divisor\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesModWithLiteralAndColumn() {
        var strategy = new DefaultModPsStrategy();
        var mod = new Mod(Literal.of(25), ColumnReference.of("settings", "page_size"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(mod, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MOD(?, \"page_size\")");
        assertThat(result.parameters()).containsExactly(25);
    }
}
