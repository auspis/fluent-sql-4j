package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.predicate.Between;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlBetweenPsStrategyTest {

    private StandardSqlBetweenPsStrategy strategy;
    private PreparedStatementRenderer specFactory;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlBetweenPsStrategy();
        specFactory = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void betweenWithLiterals() {
        Between between = new Between(ColumnReference.of("User", "age"), Literal.of(18), Literal.of(65));

        PreparedStatementSpec result = strategy.handle(between, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"age\" BETWEEN ? AND ?");
        assertThat(result.parameters()).containsExactly(18, 65);
    }

    @Test
    void betweenWithStrings() {
        Between between = new Between(ColumnReference.of("User", "name"), Literal.of("Alice"), Literal.of("John"));

        PreparedStatementSpec result = strategy.handle(between, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" BETWEEN ? AND ?");
        assertThat(result.parameters()).containsExactly("Alice", "John");
    }

    @Test
    void betweenWithColumnReference() {
        Between between = new Between(
                ColumnReference.of("Order", "total"),
                ColumnReference.of("Discount", "min_amount"),
                ColumnReference.of("Discount", "max_amount"));

        PreparedStatementSpec result = strategy.handle(between, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"total\" BETWEEN \"min_amount\" AND \"max_amount\"");
        assertThat(result.parameters()).isEmpty();
    }
}
