package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.CharacterLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CharacterLengthRenderStrategy;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlCharacterLengthRenderStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlCharacterLengthRenderStrategyTest {

    @Test
    void ok() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql();
        CharacterLengthRenderStrategy strategy = new StandardSqlCharacterLengthRenderStrategy();
        CharacterLength fun = new CharacterLength(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(fun, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CHARACTER_LENGTH(\"Customer\".\"name\")");
    }
}
