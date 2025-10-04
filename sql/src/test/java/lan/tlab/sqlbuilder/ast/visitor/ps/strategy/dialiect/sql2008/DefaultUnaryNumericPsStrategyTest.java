package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.UnaryNumeric.abs;
import static lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.UnaryNumeric.ceil;
import static lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.UnaryNumeric.floor;
import static lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.UnaryNumeric.sqrt;
import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultUnaryNumericPsStrategyTest {

    private final DefaultUnaryNumericPsStrategy strategy = new DefaultUnaryNumericPsStrategy();
    private final PreparedStatementVisitor visitor =
            PreparedStatementVisitor.builder().build();
    private final AstContext ctx = new AstContext();

    @Test
    void absWithLiteral() {
        var absCall = abs(-42);

        PsDto result = strategy.handle(absCall, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ABS(?)");
        assertThat(result.parameters()).containsExactly(-42);
    }

    @Test
    void absWithColumn() {
        var absCall = abs(ColumnReference.of("transactions", "amount"));

        PsDto result = strategy.handle(absCall, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ABS(\"amount\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void ceilWithLiteral() {
        var ceilCall = ceil(3.14);

        PsDto result = strategy.handle(ceilCall, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CEIL(?)");
        assertThat(result.parameters()).containsExactly(3.14);
    }

    @Test
    void floorWithColumn() {
        var floorCall = floor(ColumnReference.of("sales", "price"));

        PsDto result = strategy.handle(floorCall, visitor, ctx);

        assertThat(result.sql()).isEqualTo("FLOOR(\"price\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void sqrtWithLiteral() {
        var sqrtCall = sqrt(Literal.of(16));

        PsDto result = strategy.handle(sqrtCall, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SQRT(?)");
        assertThat(result.parameters()).containsExactly(16);
    }

    @Test
    void sqrtWithColumn() {
        var sqrtCall = sqrt(ColumnReference.of("geometry", "area"));

        PsDto result = strategy.handle(sqrtCall, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SQRT(\"area\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void ceilWithExpressionLiteral() {
        var ceilCall = ceil(Literal.of(2.9));

        PsDto result = strategy.handle(ceilCall, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CEIL(?)");
        assertThat(result.parameters()).containsExactly(2.9);
    }

    @Test
    void absWithFloatValue() {
        var absCall = abs(-7.5f);

        PsDto result = strategy.handle(absCall, visitor, ctx);

        assertThat(result.sql()).isEqualTo("ABS(?)");
        assertThat(result.parameters()).containsExactly(-7.5f);
    }
}
