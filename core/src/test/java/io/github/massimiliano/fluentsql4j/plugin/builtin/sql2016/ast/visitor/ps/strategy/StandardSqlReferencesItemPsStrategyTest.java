package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ReferencesItem;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.ReferencesItemPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlReferencesItemPsStrategyTest {

    private final ReferencesItemPsStrategy strategy = new StandardSqlReferencesItemPsStrategy();

    @Test
    void shouldHandleReferencesWithSingleColumn() {
        // given
        var referencesItem = new ReferencesItem("users", "id");
        var visitor = AstToPreparedStatementSpecVisitor.builder().build();
        var ctx = new AstContext();

        // when
        PreparedStatementSpec result = strategy.handle(referencesItem, visitor, ctx);

        // then
        assertThat(result.sql()).isEqualTo("REFERENCES \"users\" (\"id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleReferencesWithMultipleColumns() {
        // given
        var referencesItem = new ReferencesItem("users", "tenant_id", "user_id");
        var visitor = AstToPreparedStatementSpecVisitor.builder().build();
        var ctx = new AstContext();

        // when
        PreparedStatementSpec result = strategy.handle(referencesItem, visitor, ctx);

        // then
        assertThat(result.sql()).isEqualTo("REFERENCES \"users\" (\"tenant_id\", \"user_id\")");
        assertThat(result.parameters()).isEmpty();
    }
}
