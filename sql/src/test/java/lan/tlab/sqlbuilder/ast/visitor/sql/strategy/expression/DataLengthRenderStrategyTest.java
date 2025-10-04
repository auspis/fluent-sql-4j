package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.DataLength;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.Test;

class DataLengthRenderStrategyTest {

    @Test
    void sqlServer() {
        SqlRenderer sqlRenderer = SqlRendererFactory.sqlServer();
        DataLengthRenderStrategy strategy = DataLengthRenderStrategy.sqlServer();
        DataLength fun = new DataLength(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(fun, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("DATALENGTH([Customer].[name])");
    }

    @Test
    void standardSql2008() {
        SqlRenderer sqlRenderer = SqlRendererFactory.standardSql2008();
        DataLengthRenderStrategy strategy = DataLengthRenderStrategy.standardSql2008();
        DataLength fun = new DataLength(ColumnReference.of("Customer", "name"));
        assertThrows(UnsupportedOperationException.class, () -> strategy.render(fun, sqlRenderer, new AstContext()));
    }
}
