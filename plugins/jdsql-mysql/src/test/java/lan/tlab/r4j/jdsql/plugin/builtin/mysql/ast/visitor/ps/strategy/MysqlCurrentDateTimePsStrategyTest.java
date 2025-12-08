package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDateTime;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.MysqlPreparedStatementRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlCurrentDateTimePsStrategyTest {

    private MysqlCurrentDateTimePsStrategy strategy;
    private PreparedStatementRenderer psRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new MysqlCurrentDateTimePsStrategy();
        psRenderer = MysqlPreparedStatementRendererFactory.create();
    }

    @Test
    void ok() {
        PreparedStatementSpec result = strategy.handle(new CurrentDateTime(), psRenderer, new AstContext());
        assertThat(result.sql()).isEqualTo("NOW()");
        assertThat(result.parameters()).isEmpty();
    }
}
