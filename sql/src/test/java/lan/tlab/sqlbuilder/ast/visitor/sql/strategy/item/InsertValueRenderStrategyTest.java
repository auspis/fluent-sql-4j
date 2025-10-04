package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertValues;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsertValueRenderStrategyTest {

    private InsertValueRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new InsertValueRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
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
