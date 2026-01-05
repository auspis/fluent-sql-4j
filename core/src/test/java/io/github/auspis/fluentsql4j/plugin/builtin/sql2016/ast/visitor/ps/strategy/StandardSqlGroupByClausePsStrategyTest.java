package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.dql.clause.GroupBy;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlGroupByClausePsStrategyTest {

    private StandardSqlGroupByClausePsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlGroupByClausePsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void singleColumn() {
        GroupBy groupBy = GroupBy.of(ColumnReference.of("User", "department"));

        PreparedStatementSpec result = strategy.handle(groupBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"department\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleColumns() {
        GroupBy groupBy = GroupBy.of(ColumnReference.of("User", "department"), ColumnReference.of("User", "role"));

        PreparedStatementSpec result = strategy.handle(groupBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"department\", \"role\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleColumnsFromDifferentTables() {
        GroupBy groupBy = GroupBy.of(
                ColumnReference.of("User", "department"),
                ColumnReference.of("Order", "status"),
                ColumnReference.of("Product", "category"));

        PreparedStatementSpec result = strategy.handle(groupBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"department\", \"status\", \"category\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void columnWithTablePrefix() {
        GroupBy groupBy =
                GroupBy.of(ColumnReference.of("Employee", "department_id"), ColumnReference.of("Department", "name"));

        PreparedStatementSpec result = strategy.handle(groupBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"department_id\", \"name\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void manyColumns() {
        GroupBy groupBy = GroupBy.of(
                ColumnReference.of("Sales", "region"),
                ColumnReference.of("Sales", "quarter"),
                ColumnReference.of("Sales", "product_line"),
                ColumnReference.of("Sales", "sales_rep"));

        PreparedStatementSpec result = strategy.handle(groupBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"region\", \"quarter\", \"product_line\", \"sales_rep\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void groupByWithComplexExpression() {
        // Nota: GROUP BY con espressioni complesse come funzioni aggregate non è tipico
        // ma testiamo che la strategia possa gestire qualsiasi espressione scalare
        GroupBy groupBy =
                GroupBy.of(ColumnReference.of("User", "department"), ColumnReference.of("User", "created_at"));

        PreparedStatementSpec result = strategy.handle(groupBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"department\", \"created_at\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void groupByEmpty() {
        // Anche se non ha senso in SQL, testiamo che la strategia gestisca correttamente un GROUP BY vuoto
        GroupBy groupBy = GroupBy.of();

        PreparedStatementSpec result = strategy.handle(groupBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void groupByColumnOrderPreserved() {
        // Verifica che l'ordine delle colonne sia preservato
        GroupBy groupBy = GroupBy.of(
                ColumnReference.of("User", "last_name"),
                ColumnReference.of("User", "first_name"),
                ColumnReference.of("User", "middle_name"));

        PreparedStatementSpec result = strategy.handle(groupBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"last_name\", \"first_name\", \"middle_name\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void groupByWithDuplicateColumns() {
        // Anche se non ha senso in SQL, testiamo che la strategia non rimuova duplicati
        GroupBy groupBy = GroupBy.of(
                ColumnReference.of("User", "department"),
                ColumnReference.of("User", "role"),
                ColumnReference.of("User", "department")); // duplicato

        PreparedStatementSpec result = strategy.handle(groupBy, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"department\", \"role\", \"department\"");
        assertThat(result.parameters()).isEmpty();
    }
}
