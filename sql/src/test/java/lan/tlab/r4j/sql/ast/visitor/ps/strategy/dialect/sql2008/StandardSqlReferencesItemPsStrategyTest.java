package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ReferencesItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ReferencesItemPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlReferencesItemPsStrategyTest {

    private final ReferencesItemPsStrategy strategy = new StandardSqlReferencesItemPsStrategy();

    @Test
    void shouldHandleReferencesWithSingleColumn() {
        // given
        var referencesItem = new ReferencesItem("users", "id");
        var visitor = PreparedStatementRenderer.builder().build();
        var ctx = new AstContext();

        // when
        PsDto result = strategy.handle(referencesItem, visitor, ctx);

        // then
        assertThat(result.sql()).isEqualTo("REFERENCES \"users\" (\"id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleReferencesWithMultipleColumns() {
        // given
        var referencesItem = new ReferencesItem("users", "tenant_id", "user_id");
        var visitor = PreparedStatementRenderer.builder().build();
        var ctx = new AstContext();

        // when
        PsDto result = strategy.handle(referencesItem, visitor, ctx);

        // then
        assertThat(result.sql()).isEqualTo("REFERENCES \"users\" (\"tenant_id\", \"user_id\")");
        assertThat(result.parameters()).isEmpty();
    }
}
