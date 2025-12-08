package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.UnaryNumeric.abs;
import static lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.UnaryNumeric.ceil;
import static lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.UnaryNumeric.floor;
import static lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.UnaryNumeric.sqrt;
import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UnaryNumericPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlUnaryNumericPsStrategyTest {

    private final UnaryNumericPsStrategy strategy = new StandardSqlUnaryNumericPsStrategy();
    private final PreparedStatementRenderer specFactory =
            PreparedStatementRenderer.builder().build();
    private final AstContext ctx = new AstContext();

    @Test
    void absWithLiteral() {
        var absCall = abs(-42);

        PreparedStatementSpec result = strategy.handle(absCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("ABS(?)");
        assertThat(result.parameters()).containsExactly(-42);
    }

    @Test
    void absWithColumn() {
        var absCall = abs(ColumnReference.of("transactions", "amount"));

        PreparedStatementSpec result = strategy.handle(absCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("ABS(\"amount\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void ceilWithLiteral() {
        var ceilCall = ceil(3.14);

        PreparedStatementSpec result = strategy.handle(ceilCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("CEIL(?)");
        assertThat(result.parameters()).containsExactly(3.14);
    }

    @Test
    void floorWithColumn() {
        var floorCall = floor(ColumnReference.of("sales", "price"));

        PreparedStatementSpec result = strategy.handle(floorCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("FLOOR(\"price\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void sqrtWithLiteral() {
        var sqrtCall = sqrt(Literal.of(16));

        PreparedStatementSpec result = strategy.handle(sqrtCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("SQRT(?)");
        assertThat(result.parameters()).containsExactly(16);
    }

    @Test
    void sqrtWithColumn() {
        var sqrtCall = sqrt(ColumnReference.of("geometry", "area"));

        PreparedStatementSpec result = strategy.handle(sqrtCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("SQRT(\"area\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void ceilWithExpressionLiteral() {
        var ceilCall = ceil(Literal.of(2.9));

        PreparedStatementSpec result = strategy.handle(ceilCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("CEIL(?)");
        assertThat(result.parameters()).containsExactly(2.9);
    }

    @Test
    void absWithFloatValue() {
        var absCall = abs(-7.5f);

        PreparedStatementSpec result = strategy.handle(absCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("ABS(?)");
        assertThat(result.parameters()).containsExactly(-7.5f);
    }
}
