package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.number.Mod;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import org.junit.jupiter.api.Test;

class StandardSqlModPsStrategyTest {

    @Test
    void handlesModWithTwoLiterals() {
        var strategy = new StandardSqlModPsStrategy();
        var mod = new Mod(Literal.of(10), Literal.of(3));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(mod, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MOD(?, ?)");
        assertThat(result.parameters()).containsExactly(10, 3);
    }

    @Test
    void handlesModWithColumnAndLiteral() {
        var strategy = new StandardSqlModPsStrategy();
        var mod = new Mod(ColumnReference.of("orders", "total"), Literal.of(100));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(mod, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MOD(\"total\", ?)");
        assertThat(result.parameters()).containsExactly(100);
    }

    @Test
    void handlesModWithTwoColumns() {
        var strategy = new StandardSqlModPsStrategy();
        var mod =
                new Mod(ColumnReference.of("calculations", "dividend"), ColumnReference.of("calculations", "divisor"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(mod, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MOD(\"dividend\", \"divisor\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesModWithLiteralAndColumn() {
        var strategy = new StandardSqlModPsStrategy();
        var mod = new Mod(Literal.of(25), ColumnReference.of("settings", "page_size"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(mod, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MOD(?, \"page_size\")");
        assertThat(result.parameters()).containsExactly(25);
    }
}
