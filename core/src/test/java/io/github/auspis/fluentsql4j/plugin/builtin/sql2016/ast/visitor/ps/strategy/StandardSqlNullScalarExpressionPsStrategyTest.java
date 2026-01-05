package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.NullScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNullScalarExpressionPsStrategy;

class StandardSqlNullScalarExpressionPsStrategyTest {

    @Test
    void handlesNullScalarExpression() {
        var strategy = new StandardSqlNullScalarExpressionPsStrategy();
        var nullExpression = new NullScalarExpression();
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(nullExpression, visitor, ctx);

        assertThat(result.sql()).isEqualTo("NULL");
        assertThat(result.parameters()).isEmpty();
    }
}
