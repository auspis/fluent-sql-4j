package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonExists;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.Test;

class JsonExistsRenderStrategyTest {

    @Test
    void standardSql2016WithBasicArguments() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql2008();
        JsonExistsRenderStrategy strategy = JsonExistsRenderStrategy.standardSql2016();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"));
        String sql = strategy.render(jsonExists, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_EXISTS(\"products\".\"data\", '$.price')");
    }

    @Test
    void standardSql2016WithOnErrorBehavior() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql2008();
        JsonExistsRenderStrategy strategy = JsonExistsRenderStrategy.standardSql2016();
        JsonExists jsonExists =
                new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"), BehaviorKind.ERROR);
        String sql = strategy.render(jsonExists, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_EXISTS(\"products\".\"data\", '$.price' ERROR ON ERROR)");
    }

    @Test
    void postgreSql() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql2008();
        JsonExistsRenderStrategy strategy = JsonExistsRenderStrategy.postgreSql();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"));
        String sql = strategy.render(jsonExists, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_EXISTS(\"products\".\"data\", '$.price')");
    }
}
