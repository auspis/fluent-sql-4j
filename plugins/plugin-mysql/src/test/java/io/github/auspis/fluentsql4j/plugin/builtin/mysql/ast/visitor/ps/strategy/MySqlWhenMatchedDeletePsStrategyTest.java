package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedDelete;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import org.junit.jupiter.api.Test;

class MySqlWhenMatchedDeletePsStrategyTest {

    @Test
    void throwsUnsupportedOperationException() {
        WhenMatchedDelete item = new WhenMatchedDelete(null);

        AstToPreparedStatementSpecVisitor visitor = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MySqlWhenMatchedDeletePsStrategy strategy = new MySqlWhenMatchedDeletePsStrategy();

        AstContext ctx = new AstContext();
        assertThatThrownBy(() -> strategy.handle(item, visitor, ctx))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("MySQL does not support WHEN MATCHED THEN DELETE")
                .hasMessageContaining("INSERT...ON DUPLICATE KEY UPDATE");
    }
}
