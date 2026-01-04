package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Between;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlBetweenPsStrategyTest {

    private StandardSqlBetweenPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor specFactory;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlBetweenPsStrategy();
        specFactory = new AstToPreparedStatementSpecVisitor();
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
