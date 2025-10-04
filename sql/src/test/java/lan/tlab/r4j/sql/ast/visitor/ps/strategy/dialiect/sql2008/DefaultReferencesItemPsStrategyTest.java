package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ReferencesItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultReferencesItemPsStrategyTest {

    private final DefaultReferencesItemPsStrategy strategy = new DefaultReferencesItemPsStrategy();

    @Test
    void shouldHandleReferencesWithSingleColumn() {
        // given
        var referencesItem = new ReferencesItem("users", "id");
        var visitor = PreparedStatementVisitor.builder().build();
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
        var visitor = PreparedStatementVisitor.builder().build();
        var ctx = new AstContext();

        // when
        PsDto result = strategy.handle(referencesItem, visitor, ctx);

        // then
        assertThat(result.sql()).isEqualTo("REFERENCES \"users\" (\"tenant_id\", \"user_id\")");
        assertThat(result.parameters()).isEmpty();
    }
}
