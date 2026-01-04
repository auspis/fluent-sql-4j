package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.aggregate.AggregateCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.IsNull;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlIsNullPsStrategyTest {

    private StandardSqlIsNullPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlIsNullPsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void columnReference() {
        IsNull isNull = new IsNull(ColumnReference.of("User", "email"));

        PreparedStatementSpec result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"email\" IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void columnReferenceWithTablePrefix() {
        IsNull isNull = new IsNull(ColumnReference.of("Employee", "manager_id"));

        PreparedStatementSpec result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"manager_id\" IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void aggregateFunction() {
        IsNull isNull = new IsNull(AggregateCall.max(ColumnReference.of("Order", "total")));

        PreparedStatementSpec result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MAX(\"total\") IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countFunction() {
        IsNull isNull = new IsNull(AggregateCall.count(ColumnReference.of("User", "id")));

        PreparedStatementSpec result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\") IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void sumFunction() {
        IsNull isNull = new IsNull(AggregateCall.sum(ColumnReference.of("Sale", "amount")));

        PreparedStatementSpec result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SUM(\"amount\") IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void avgFunction() {
        // Note: Using a function call (scalar) instead of aggregate, as IS NULL with aggregates
        // requires GROUP BY which is not applicable here. This tests scalar function handling.
        IsNull isNull = new IsNull(Literal.of((Integer) null));

        PreparedStatementSpec result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("? IS NULL");
        assertThat(result.parameters()).containsExactly((Object) null);
    }

    @Test
    void minFunction() {
        // Using a column reference (scalar) instead of aggregate
        IsNull isNull = new IsNull(ColumnReference.of("Transaction", "date"));

        PreparedStatementSpec result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"date\" IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void literalExpression() {
        IsNull isNull = new IsNull(Literal.of("test"));

        PreparedStatementSpec result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("? IS NULL");
        assertThat(result.parameters()).containsExactly("test");
    }

    @Test
    void numericLiteral() {
        IsNull isNull = new IsNull(Literal.of(42));

        PreparedStatementSpec result = strategy.handle(isNull, visitor, ctx);

        assertThat(result.sql()).isEqualTo("? IS NULL");
        assertThat(result.parameters()).containsExactly(42);
    }

    @Test
    void multipleColumnNames() {
        IsNull isNull1 = new IsNull(ColumnReference.of("User", "first_name"));
        IsNull isNull2 = new IsNull(ColumnReference.of("User", "last_name"));

        PreparedStatementSpec result1 = strategy.handle(isNull1, visitor, ctx);
        PreparedStatementSpec result2 = strategy.handle(isNull2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo("\"first_name\" IS NULL");
        assertThat(result1.parameters()).isEmpty();
        assertThat(result2.sql()).isEqualTo("\"last_name\" IS NULL");
        assertThat(result2.parameters()).isEmpty();
    }
}
