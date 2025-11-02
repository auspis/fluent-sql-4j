package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Between;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlBetweenPsStrategyTest {

    private StandardSqlBetweenPsStrategy strategy;
    private PreparedStatementRenderer renderer;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlBetweenPsStrategy();
        renderer = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void betweenWithLiterals() {
        Between between = new Between(ColumnReference.of("User", "age"), Literal.of(18), Literal.of(65));

        PsDto result = strategy.handle(between, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"age\" BETWEEN ? AND ?");
        assertThat(result.parameters()).containsExactly(18, 65);
    }

    @Test
    void betweenWithStrings() {
        Between between = new Between(ColumnReference.of("User", "name"), Literal.of("Alice"), Literal.of("John"));

        PsDto result = strategy.handle(between, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" BETWEEN ? AND ?");
        assertThat(result.parameters()).containsExactly("Alice", "John");
    }

    @Test
    void betweenWithColumnReference() {
        Between between = new Between(
                ColumnReference.of("Order", "total"),
                ColumnReference.of("Discount", "min_amount"),
                ColumnReference.of("Discount", "max_amount"));

        PsDto result = strategy.handle(between, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"total\" BETWEEN \"min_amount\" AND \"max_amount\"");
        assertThat(result.parameters()).isEmpty();
    }
}
