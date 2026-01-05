package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.CurrentDateTime;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.MysqlAstToPreparedStatementSpecVisitorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlCurrentDateTimePsStrategyTest {

    private MysqlCurrentDateTimePsStrategy strategy;
    private AstToPreparedStatementSpecVisitor astToPsSpecVisitor;

    @BeforeEach
    public void setUp() {
        strategy = new MysqlCurrentDateTimePsStrategy();
        astToPsSpecVisitor = MysqlAstToPreparedStatementSpecVisitorFactory.create();
    }

    @Test
    void ok() {
        PreparedStatementSpec result = strategy.handle(new CurrentDateTime(), astToPsSpecVisitor, new AstContext());
        assertThat(result.sql()).isEqualTo("NOW()");
        assertThat(result.parameters()).isEmpty();
    }
}
