package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Length;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultLengthPsStrategyTest {

    @Test
    void handlesLengthWithLiteralString() {
        var strategy = new DefaultLengthPsStrategy();
        var length = new Length(Literal.of("Hello World"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(length, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LENGTH(?)");
        assertThat(result.parameters()).containsExactly("Hello World");
    }

    @Test
    void handlesLengthWithColumnReference() {
        var strategy = new DefaultLengthPsStrategy();
        var length = new Length(ColumnReference.of("users", "email"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(length, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LENGTH(\"email\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesLengthWithColumnOnly() {
        var strategy = new DefaultLengthPsStrategy();
        var length = new Length(ColumnReference.of("products", "description"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(length, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LENGTH(\"description\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesLengthWithTableAlias() {
        var strategy = new DefaultLengthPsStrategy();
        var length = new Length(ColumnReference.of("p", "product_name"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(length, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LENGTH(\"product_name\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }
}
