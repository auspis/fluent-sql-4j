package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Left;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class StandardSqlLeftPsStrategyTest {

    @Test
    void handlesLeftWithLiteralStringAndNumber() {
        var strategy = new StandardSqlLeftPsStrategy();
        var left = new Left(Literal.of("Hello World"), Literal.of(5));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(left, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LEFT(?, ?)");
        assertThat(result.parameters()).containsExactly("Hello World", 5);
    }

    @Test
    void handlesLeftWithColumnAndNumber() {
        var strategy = new StandardSqlLeftPsStrategy();
        var left = Left.of(ColumnReference.of("users", "full_name"), 3);
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(left, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LEFT(\"full_name\", ?)");
        assertThat(result.parameters()).containsExactly(3);
    }

    @Test
    void handlesLeftWithStringAndColumnLength() {
        var strategy = new StandardSqlLeftPsStrategy();
        var left = new Left(Literal.of("Test String"), ColumnReference.of("config", "name_length"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(left, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LEFT(?, \"name_length\")");
        assertThat(result.parameters()).containsExactly("Test String");
    }

    @Test
    void handlesLeftWithColumnsOnly() {
        var strategy = new StandardSqlLeftPsStrategy();
        var left = new Left(
                ColumnReference.of("products", "description"), ColumnReference.of("settings", "preview_length"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(left, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LEFT(\"description\", \"preview_length\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }
}
