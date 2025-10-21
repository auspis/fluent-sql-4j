package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.predicate.IsNull;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultIsNullPsStrategyTest {

    private DefaultIsNullPsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new DefaultIsNullPsStrategy();
        visitor = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void columnReference() {
        IsNull isNull = new IsNull(ColumnReference.of("User", "email"));

        PsDto result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"email\" IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void columnReferenceWithTablePrefix() {
        IsNull isNull = new IsNull(ColumnReference.of("Employee", "manager_id"));

        PsDto result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"manager_id\" IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void aggregateFunction() {
        IsNull isNull = new IsNull(AggregateCall.max(ColumnReference.of("Order", "total")));

        PsDto result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MAX(\"total\") IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countFunction() {
        IsNull isNull = new IsNull(AggregateCall.count(ColumnReference.of("User", "id")));

        PsDto result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\") IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void sumFunction() {
        IsNull isNull = new IsNull(AggregateCall.sum(ColumnReference.of("Sale", "amount")));

        PsDto result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SUM(\"amount\") IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void avgFunction() {
        IsNull isNull = new IsNull(AggregateCall.avg(ColumnReference.of("Product", "price")));

        PsDto result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("AVG(\"price\") IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void minFunction() {
        IsNull isNull = new IsNull(AggregateCall.min(ColumnReference.of("Transaction", "date")));

        PsDto result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MIN(\"date\") IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void literalExpression() {
        IsNull isNull = new IsNull(Literal.of("test"));

        PsDto result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("? IS NULL");
        assertThat(result.parameters()).containsExactly("test");
    }

    @Test
    void numericLiteral() {
        IsNull isNull = new IsNull(Literal.of(42));

        PsDto result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("? IS NULL");
        assertThat(result.parameters()).containsExactly(42);
    }

    @Test
    void multipleColumnNames() {
        IsNull isNull1 = new IsNull(ColumnReference.of("User", "first_name"));
        IsNull isNull2 = new IsNull(ColumnReference.of("User", "last_name"));

        PsDto result1 = strategy.handle(isNull1, visitor, ctx);
        PsDto result2 = strategy.handle(isNull2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo("\"first_name\" IS NULL");
        assertThat(result1.parameters()).isEmpty();
        assertThat(result2.sql()).isEqualTo("\"last_name\" IS NULL");
        assertThat(result2.parameters()).isEmpty();
    }
}
