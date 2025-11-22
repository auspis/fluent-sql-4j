package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertValues;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlInsertValueRenderStrategyTest {

    private StandardSqlInsertValueRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlInsertValueRenderStrategy();
        renderer = StandardSqlRendererFactory.standardSql();
    }

    @Test
    void ok() {
        InsertValues item = InsertValues.of(
                Literal.of("Success"), Literal.of(200), new CurrentDate(), Literal.of(LocalDate.of(2025, 8, 28)));
        String sql = strategy.render(item, renderer, new AstContext());
        assertThat(sql).isEqualTo("VALUES ('Success', 200, CURRENT_DATE(), '2025-08-28')");
    }

    @Test
    void empty() {
        InsertValues item = InsertValues.of();
        String sql = strategy.render(item, renderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }
}
