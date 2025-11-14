package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.common.predicate.IsNotNull;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlIsNotNullPsStrategyTest {

    private StandardSqlIsNotNullPsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlIsNotNullPsStrategy();
        visitor = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void columnReference() {
        IsNotNull isNotNull = new IsNotNull(ColumnReference.of("User", "email"));

        PsDto result = strategy.handle(isNotNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"email\" IS NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void columnReferenceWithTablePrefix() {
        IsNotNull isNotNull = new IsNotNull(ColumnReference.of("Employee", "manager_id"));

        PsDto result = strategy.handle(isNotNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"manager_id\" IS NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void aggregateFunction() {
        IsNotNull isNotNull = new IsNotNull(AggregateCall.max(ColumnReference.of("Order", "total")));

        PsDto result = strategy.handle(isNotNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MAX(\"total\") IS NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countFunction() {
        IsNotNull isNotNull = new IsNotNull(AggregateCall.count(ColumnReference.of("User", "id")));

        PsDto result = strategy.handle(isNotNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\") IS NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void sumFunction() {
        IsNotNull isNotNull = new IsNotNull(AggregateCall.sum(ColumnReference.of("Sale", "amount")));

        PsDto result = strategy.handle(isNotNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SUM(\"amount\") IS NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void avgFunction() {
        IsNotNull isNotNull = new IsNotNull(AggregateCall.avg(ColumnReference.of("Product", "price")));

        PsDto result = strategy.handle(isNotNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("AVG(\"price\") IS NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void minFunction() {
        IsNotNull isNotNull = new IsNotNull(AggregateCall.min(ColumnReference.of("Transaction", "date")));

        PsDto result = strategy.handle(isNotNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MIN(\"date\") IS NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void literalExpression() {
        IsNotNull isNotNull = new IsNotNull(Literal.of("test"));

        PsDto result = strategy.handle(isNotNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("? IS NOT NULL");
        assertThat(result.parameters()).containsExactly("test");
    }

    @Test
    void numericLiteral() {
        IsNotNull isNotNull = new IsNotNull(Literal.of(42));

        PsDto result = strategy.handle(isNotNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("? IS NOT NULL");
        assertThat(result.parameters()).containsExactly(42);
    }

    @Test
    void multipleColumnNames() {
        IsNotNull isNotNull1 = new IsNotNull(ColumnReference.of("User", "first_name"));
        IsNotNull isNotNull2 = new IsNotNull(ColumnReference.of("User", "last_name"));

        PsDto result1 = strategy.handle(isNotNull1, visitor, ctx);
        PsDto result2 = strategy.handle(isNotNull2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo("\"first_name\" IS NOT NULL");
        assertThat(result1.parameters()).isEmpty();
        assertThat(result2.sql()).isEqualTo("\"last_name\" IS NOT NULL");
        assertThat(result2.parameters()).isEmpty();
    }
}
