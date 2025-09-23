package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Left;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultLeftPsStrategyTest {

    @Test
    void handlesLeftWithLiteralStringAndNumber() {
        var strategy = new DefaultLeftPsStrategy();
        var left = Left.of(Literal.of("Hello World"), Literal.of(5));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(left, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LEFT(?, ?)");
        assertThat(result.parameters()).containsExactly("Hello World", 5);
    }

    @Test
    void handlesLeftWithColumnAndNumber() {
        var strategy = new DefaultLeftPsStrategy();
        var left = Left.of(ColumnReference.of("users", "full_name"), 3);
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(left, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LEFT(\"full_name\", ?)");
        assertThat(result.parameters()).containsExactly(3);
    }

    @Test
    void handlesLeftWithStringAndColumnLength() {
        var strategy = new DefaultLeftPsStrategy();
        var left = Left.of(Literal.of("Test String"), ColumnReference.of("config", "name_length"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(left, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LEFT(?, \"name_length\")");
        assertThat(result.parameters()).containsExactly("Test String");
    }

    @Test
    void handlesLeftWithColumnsOnly() {
        var strategy = new DefaultLeftPsStrategy();
        var left = Left.of(
                ColumnReference.of("products", "description"), ColumnReference.of("settings", "preview_length"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(left, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LEFT(\"description\", \"preview_length\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }
}
