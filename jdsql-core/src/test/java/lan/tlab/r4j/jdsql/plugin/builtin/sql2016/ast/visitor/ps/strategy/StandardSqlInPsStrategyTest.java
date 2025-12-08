package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.predicate.In;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlInPsStrategyTest {

    private StandardSqlInPsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlInPsStrategy();
        visitor = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void inWithLiterals() {
        In in = new In(ColumnReference.of("User", "id"), List.of(Literal.of(1), Literal.of(2), Literal.of(3)));

        PreparedStatementSpec result = strategy.handle(in, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"id\" IN (?, ?, ?)");
        assertThat(result.parameters()).containsExactly(1, 2, 3);
    }

    @Test
    void inWithStrings() {
        In in = new In(
                ColumnReference.of("User", "name"),
                List.of(Literal.of("Alice"), Literal.of("Bob"), Literal.of("Charlie")));

        PreparedStatementSpec result = strategy.handle(in, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" IN (?, ?, ?)");
        assertThat(result.parameters()).containsExactly("Alice", "Bob", "Charlie");
    }

    @Test
    void inWithSingleValue() {
        In in = new In(ColumnReference.of("User", "status"), List.of(Literal.of("active")));

        PreparedStatementSpec result = strategy.handle(in, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"status\" IN (?)");
        assertThat(result.parameters()).containsExactly("active");
    }

    @Test
    void inWithColumnReferences() {
        In in = new In(
                ColumnReference.of("Order", "status"),
                List.of(ColumnReference.of("Config", "active_status"), ColumnReference.of("Config", "pending_status")));

        PreparedStatementSpec result = strategy.handle(in, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"status\" IN (\"active_status\", \"pending_status\")");
        assertThat(result.parameters()).isEmpty();
    }
}
