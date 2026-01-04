package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.Left;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import org.junit.jupiter.api.Test;

class StandardSqlLeftPsStrategyTest {

    @Test
    void handlesLeftWithLiteralStringAndNumber() {
        var strategy = new StandardSqlLeftPsStrategy();
        var left = new Left(Literal.of("Hello World"), Literal.of(5));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(left, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LEFT(?, ?)");
        assertThat(result.parameters()).containsExactly("Hello World", 5);
    }

    @Test
    void handlesLeftWithColumnAndNumber() {
        var strategy = new StandardSqlLeftPsStrategy();
        var left = Left.of(ColumnReference.of("users", "full_name"), 3);
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(left, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LEFT(\"full_name\", ?)");
        assertThat(result.parameters()).containsExactly(3);
    }

    @Test
    void handlesLeftWithStringAndColumnLength() {
        var strategy = new StandardSqlLeftPsStrategy();
        var left = new Left(Literal.of("Test String"), ColumnReference.of("config", "name_length"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(left, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LEFT(?, \"name_length\")");
        assertThat(result.parameters()).containsExactly("Test String");
    }

    @Test
    void handlesLeftWithColumnsOnly() {
        var strategy = new StandardSqlLeftPsStrategy();
        var left = new Left(
                ColumnReference.of("products", "description"), ColumnReference.of("settings", "preview_length"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(left, visitor, ctx);

        assertThat(result.sql()).isEqualTo("LEFT(\"description\", \"preview_length\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }
}
