package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultColumnReferencePsStrategyTest {
    private final DefaultColumnReferencePsStrategy strategy = new DefaultColumnReferencePsStrategy();

    @Test
    void noTable() {
        ColumnReference col = ColumnReference.of("", "name");
        PsDto dto = strategy.handle(col, null, new AstContext());
        assertThat(dto.sql()).isEqualTo("\"name\"");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void table() {
        ColumnReference col = ColumnReference.of("Customer", "name");
        PsDto dto = strategy.handle(col, null, new AstContext());
        assertThat(dto.sql()).isEqualTo("\"name\"");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void join() {
        ColumnReference col = ColumnReference.of("Customer", "name");
        AstContext ctx = new AstContext(AstContext.Scope.JOIN_ON);
        PsDto dto = strategy.handle(col, null, ctx);
        assertThat(dto.sql()).isEqualTo("\"Customer\".\"name\"");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void blankTableJoinOnContext() {
        ColumnReference col = ColumnReference.of("   ", "name");
        AstContext ctx = new AstContext(AstContext.Scope.JOIN_ON);
        PsDto dto = strategy.handle(col, null, ctx);
        assertThat(dto.sql()).isEqualTo("\"name\"");
        assertThat(dto.parameters()).isEmpty();
    }
}
