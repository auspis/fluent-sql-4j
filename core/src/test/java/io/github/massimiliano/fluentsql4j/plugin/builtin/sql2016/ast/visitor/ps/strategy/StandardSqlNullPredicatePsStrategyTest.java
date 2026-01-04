package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import org.junit.jupiter.api.Test;

class StandardSqlNullPredicatePsStrategyTest {

    @Test
    void handleNullPredicate() {
        var expression = new NullPredicate();

        var strategy = new StandardSqlNullPredicatePsStrategy();
        var visitor = new AstToPreparedStatementSpecVisitor();
        var result = strategy.handle(expression, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("NULL");
        assertThat(result.parameters()).isEmpty();
    }
}
