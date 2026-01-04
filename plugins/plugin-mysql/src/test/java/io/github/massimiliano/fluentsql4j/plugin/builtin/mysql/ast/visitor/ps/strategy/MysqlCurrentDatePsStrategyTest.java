package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.CurrentDate;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.MysqlAstToPreparedStatementSpecVisitorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlCurrentDatePsStrategyTest {

    private MysqlCurrentDatePsStrategy strategy;
    private AstToPreparedStatementSpecVisitor astToPsSpecVisitor;

    @BeforeEach
    public void setUp() {
        strategy = new MysqlCurrentDatePsStrategy();
        astToPsSpecVisitor = MysqlAstToPreparedStatementSpecVisitorFactory.create();
    }

    @Test
    void ok() {
        PreparedStatementSpec result = strategy.handle(new CurrentDate(), astToPsSpecVisitor, new AstContext());
        assertThat(result.sql()).isEqualTo("CURDATE()");
        assertThat(result.parameters()).isEmpty();
    }
}
