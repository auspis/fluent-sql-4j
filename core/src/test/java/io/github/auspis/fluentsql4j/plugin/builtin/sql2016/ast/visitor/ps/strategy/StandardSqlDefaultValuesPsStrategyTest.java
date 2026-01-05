package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.DefaultValues;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlDefaultValuesPsStrategyTest {

    private StandardSqlDefaultValuesPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlDefaultValuesPsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void defaultValues() {
        DefaultValues defaultValues = new DefaultValues();

        PreparedStatementSpec result = strategy.handle(defaultValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT VALUES");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void defaultValuesWithDifferentContext() {
        DefaultValues defaultValues = new DefaultValues();
        AstContext differentCtx = new AstContext();

        PreparedStatementSpec result = strategy.handle(defaultValues, visitor, differentCtx);

        assertThat(result.sql()).isEqualTo("DEFAULT VALUES");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void defaultValuesIsStateless() {
        DefaultValues defaultValues1 = new DefaultValues();
        DefaultValues defaultValues2 = new DefaultValues();

        PreparedStatementSpec result1 = strategy.handle(defaultValues1, visitor, ctx);
        PreparedStatementSpec result2 = strategy.handle(defaultValues2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo("DEFAULT VALUES");
        assertThat(result1.parameters()).isEmpty();
        assertThat(result2.sql()).isEqualTo("DEFAULT VALUES");
        assertThat(result2.parameters()).isEmpty();

        // Results should be equal
        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
