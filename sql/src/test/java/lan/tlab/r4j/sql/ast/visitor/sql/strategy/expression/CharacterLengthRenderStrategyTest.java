package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.CharacterLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.Test;

class CharacterLengthRenderStrategyTest {

    @Test
    void standardSql2008() {
        SqlRenderer sqlRenderer = SqlRendererFactory.standardSql2008();
        CharacterLengthRenderStrategy strategy = CharacterLengthRenderStrategy.standardSql2008();
        CharacterLength fun = new CharacterLength(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(fun, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CHARACTER_LENGTH(\"Customer\".\"name\")");
    }

    @Test
    void sqlServer() {
        SqlRenderer sqlRenderer = SqlRendererFactory.sqlServer();
        CharacterLengthRenderStrategy strategy = CharacterLengthRenderStrategy.sqlServer();
        CharacterLength fun = new CharacterLength(ColumnReference.of("Customer", "name"));
        assertThrows(UnsupportedOperationException.class, () -> strategy.render(fun, sqlRenderer, new AstContext()));
    }
}
