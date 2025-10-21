package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.DataLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultDataLengthPsStrategyTest {

    @Test
    void handlesDataLengthWithLiteralString() {
        var strategy = new DefaultDataLengthPsStrategy();
        var dataLength = new DataLength(Literal.of("Hello World"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(dataLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATALENGTH(?)");
        assertThat(result.parameters()).containsExactly("Hello World");
    }

    @Test
    void handlesDataLengthWithColumnReference() {
        var strategy = new DefaultDataLengthPsStrategy();
        var dataLength = new DataLength(ColumnReference.of("files", "content"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(dataLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATALENGTH(\"content\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesDataLengthWithBinaryColumn() {
        var strategy = new DefaultDataLengthPsStrategy();
        var dataLength = new DataLength(ColumnReference.of("documents", "binary_data"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(dataLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATALENGTH(\"binary_data\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesDataLengthWithAliasedColumn() {
        var strategy = new DefaultDataLengthPsStrategy();
        var dataLength = new DataLength(ColumnReference.of("d", "blob_field"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(dataLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATALENGTH(\"blob_field\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }
}
