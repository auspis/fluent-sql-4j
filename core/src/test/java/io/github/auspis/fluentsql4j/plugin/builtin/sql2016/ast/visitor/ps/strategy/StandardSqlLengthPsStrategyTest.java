package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.function.string.Length;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import org.junit.jupiter.api.Test;

class StandardSqlLengthPsStrategyTest {

    @Test
    void handlesLengthWithLiteralString() {
        var strategy = new StandardSqlLengthPsStrategy();
        var length = new Length(Literal.of("Hello World"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(length, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LENGTH(?)");
        assertThat(result.parameters()).containsExactly("Hello World");
    }

    @Test
    void handlesLengthWithColumnReference() {
        var strategy = new StandardSqlLengthPsStrategy();
        var length = new Length(ColumnReference.of("users", "email"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(length, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LENGTH(\"email\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesLengthWithColumnOnly() {
        var strategy = new StandardSqlLengthPsStrategy();
        var length = new Length(ColumnReference.of("products", "description"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(length, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LENGTH(\"description\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesLengthWithTableAlias() {
        var strategy = new StandardSqlLengthPsStrategy();
        var length = new Length(ColumnReference.of("p", "product_name"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(length, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LENGTH(\"product_name\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }
}
