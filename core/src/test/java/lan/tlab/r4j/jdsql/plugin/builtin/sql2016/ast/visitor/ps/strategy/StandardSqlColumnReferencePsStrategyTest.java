package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ColumnReferencePsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlColumnReferencePsStrategyTest {
    private final ColumnReferencePsStrategy strategy = new StandardSqlColumnReferencePsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();

    @Test
    void noTable() {
        ColumnReference col = ColumnReference.of("", "name");
        PreparedStatementSpec dto = strategy.handle(col, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("\"name\"");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void table() {
        ColumnReference col = ColumnReference.of("Customer", "name");
        PreparedStatementSpec dto = strategy.handle(col, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("\"name\"");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void join() {
        ColumnReference col = ColumnReference.of("Customer", "name");
        AstContext ctx = new AstContext(AstContext.Feature.JOIN_ON);
        PreparedStatementSpec dto = strategy.handle(col, specFactory, ctx);
        assertThat(dto.sql()).isEqualTo("\"Customer\".\"name\"");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void blankTableJoinOnContext() {
        ColumnReference col = ColumnReference.of("   ", "name");
        AstContext ctx = new AstContext(AstContext.Feature.JOIN_ON);
        PreparedStatementSpec dto = strategy.handle(col, specFactory, ctx);
        assertThat(dto.sql()).isEqualTo("\"name\"");
        assertThat(dto.parameters()).isEmpty();
    }
}
