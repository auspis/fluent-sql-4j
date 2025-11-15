package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Length;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLengthPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlLengthPsStrategyTest {

    @Test
    void handlesLengthWithLiteralString() {
        var strategy = new StandardSqlLengthPsStrategy();
        var length = new Length(Literal.of("Hello World"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(length, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LENGTH(?)");
        assertThat(result.parameters()).containsExactly("Hello World");
    }

    @Test
    void handlesLengthWithColumnReference() {
        var strategy = new StandardSqlLengthPsStrategy();
        var length = new Length(ColumnReference.of("users", "email"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(length, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LENGTH(\"email\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesLengthWithColumnOnly() {
        var strategy = new StandardSqlLengthPsStrategy();
        var length = new Length(ColumnReference.of("products", "description"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(length, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LENGTH(\"description\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesLengthWithTableAlias() {
        var strategy = new StandardSqlLengthPsStrategy();
        var length = new Length(ColumnReference.of("p", "product_name"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(length, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LENGTH(\"product_name\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }
}
