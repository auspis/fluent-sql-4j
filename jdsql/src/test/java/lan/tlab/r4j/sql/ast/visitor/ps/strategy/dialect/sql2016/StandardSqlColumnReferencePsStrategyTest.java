package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ColumnReferencePsStrategy;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlColumnReferencePsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlColumnReferencePsStrategyTest {
    private final ColumnReferencePsStrategy strategy = new StandardSqlColumnReferencePsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();

    @Test
    void noTable() {
        ColumnReference col = ColumnReference.of("", "name");
        PsDto dto = strategy.handle(col, renderer, new AstContext());
        assertThat(dto.sql()).isEqualTo("\"name\"");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void table() {
        ColumnReference col = ColumnReference.of("Customer", "name");
        PsDto dto = strategy.handle(col, renderer, new AstContext());
        assertThat(dto.sql()).isEqualTo("\"name\"");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void join() {
        ColumnReference col = ColumnReference.of("Customer", "name");
        AstContext ctx = new AstContext(AstContext.Scope.JOIN_ON);
        PsDto dto = strategy.handle(col, renderer, ctx);
        assertThat(dto.sql()).isEqualTo("\"Customer\".\"name\"");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void blankTableJoinOnContext() {
        ColumnReference col = ColumnReference.of("   ", "name");
        AstContext ctx = new AstContext(AstContext.Scope.JOIN_ON);
        PsDto dto = strategy.handle(col, renderer, ctx);
        assertThat(dto.sql()).isEqualTo("\"name\"");
        assertThat(dto.parameters()).isEmpty();
    }
}
