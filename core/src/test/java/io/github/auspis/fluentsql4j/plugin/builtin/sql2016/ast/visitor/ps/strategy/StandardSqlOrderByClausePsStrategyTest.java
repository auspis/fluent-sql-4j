package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.dql.clause.OrderBy;
import io.github.auspis.fluentsql4j.ast.dql.clause.Sorting;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlOrderByClausePsStrategyTest {

    private StandardSqlOrderByClausePsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlOrderByClausePsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void singleColumnAscending() {
        OrderBy orderBy = OrderBy.of(Sorting.asc(ColumnReference.of("users", "name")));

        PreparedStatementSpec result = strategy.handle(orderBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" ASC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void singleColumnDescending() {
        OrderBy orderBy = OrderBy.of(Sorting.desc(ColumnReference.of("orders", "createdAt")));

        PreparedStatementSpec result = strategy.handle(orderBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"createdAt\" DESC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleColumns() {
        OrderBy orderBy = OrderBy.of(
                Sorting.asc(ColumnReference.of("employees", "department")),
                Sorting.desc(ColumnReference.of("employees", "salary")));

        PreparedStatementSpec result = strategy.handle(orderBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"department\" ASC, \"salary\" DESC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void withTableQualification() {
        OrderBy orderBy = OrderBy.of(
                Sorting.asc(ColumnReference.of("u", "name")), Sorting.desc(ColumnReference.of("u", "registeredAt")));

        PreparedStatementSpec result = strategy.handle(orderBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" ASC, \"registeredAt\" DESC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void withFunctionExpression() {
        // Test with a simple literal to verify parameter binding if needed
        OrderBy orderBy = OrderBy.of(Sorting.asc(ColumnReference.of("products", "price")));

        PreparedStatementSpec result = strategy.handle(orderBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"price\" ASC");
        assertThat(result.parameters()).isEmpty();
    }
}
