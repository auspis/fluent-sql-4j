package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.Test;

class JsonValueRenderStrategyTest {

    @Test
    void standardSql2016WithBasicArguments() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql2008();
        JsonValueRenderStrategy strategy = JsonValueRenderStrategy.standardSql2016();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"));
        String sql = strategy.render(jsonValue, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_VALUE(\"products\".\"data\", '$.price')");
    }

    @Test
    void standardSql2016WithReturningType() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql2008();
        JsonValueRenderStrategy strategy = JsonValueRenderStrategy.standardSql2016();
        JsonValue jsonValue = new JsonValue(
                ColumnReference.of("products", "data"), Literal.of("$.price"), "VARCHAR(100)", null, null);
        String sql = strategy.render(jsonValue, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_VALUE(\"products\".\"data\", '$.price' RETURNING VARCHAR(100))");
    }

    @Test
    void standardSql2016WithAllOptions() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql2008();
        JsonValueRenderStrategy strategy = JsonValueRenderStrategy.standardSql2016();
        JsonValue jsonValue = new JsonValue(
                ColumnReference.of("products", "data"), Literal.of("$.price"), "DECIMAL(10,2)", "DEFAULT 0.0", "NULL");
        String sql = strategy.render(jsonValue, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "JSON_VALUE(\"products\".\"data\", '$.price' RETURNING DECIMAL(10,2) DEFAULT 0.0 ON EMPTY NULL ON ERROR)");
    }

    @Test
    void postgreSql() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql2008();
        JsonValueRenderStrategy strategy = JsonValueRenderStrategy.postgreSql();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"));
        String sql = strategy.render(jsonValue, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_VALUE(\"products\".\"data\", '$.price')");
    }
}
