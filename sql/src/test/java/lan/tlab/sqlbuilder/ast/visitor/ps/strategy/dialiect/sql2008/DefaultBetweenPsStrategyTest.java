package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.bool.Between;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultBetweenPsStrategyTest {

    private DefaultBetweenPsStrategy strategy;
    private PreparedStatementVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new DefaultBetweenPsStrategy();
        visitor = new PreparedStatementVisitor();
        ctx = new AstContext();
    }

    @Test
    void betweenWithLiterals() {
        Between between = new Between(ColumnReference.of("User", "age"), Literal.of(18), Literal.of(65));

        PsDto result = strategy.handle(between, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"age\" BETWEEN ? AND ?");
        assertThat(result.parameters()).containsExactly(18, 65);
    }

    @Test
    void betweenWithStrings() {
        Between between = new Between(ColumnReference.of("User", "name"), Literal.of("Alice"), Literal.of("John"));

        PsDto result = strategy.handle(between, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" BETWEEN ? AND ?");
        assertThat(result.parameters()).containsExactly("Alice", "John");
    }

    @Test
    void betweenWithColumnReference() {
        Between between = new Between(
                ColumnReference.of("Order", "total"),
                ColumnReference.of("Discount", "min_amount"),
                ColumnReference.of("Discount", "max_amount"));

        PsDto result = strategy.handle(between, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"total\" BETWEEN \"min_amount\" AND \"max_amount\"");
        assertThat(result.parameters()).isEmpty();
    }
}
