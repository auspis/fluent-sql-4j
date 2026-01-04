package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.identifier.Alias;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlAsPsStrategyTest {

    private StandardSqlAsPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor specFactory;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlAsPsStrategy();
        specFactory = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void asWithSimpleName() {
        Alias as = new Alias("userId");

        PreparedStatementSpec result = strategy.handle(as, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"userId\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void asWithTableAlias() {
        Alias as = new Alias("u");

        PreparedStatementSpec result = strategy.handle(as, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"u\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void asWithColumnAlias() {
        Alias as = new Alias("totalCount");

        PreparedStatementSpec result = strategy.handle(as, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"totalCount\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void asWithComplexName() {
        Alias as = new Alias("user_full_name");

        PreparedStatementSpec result = strategy.handle(as, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"user_full_name\"");
        assertThat(result.parameters()).isEmpty();
    }
}
