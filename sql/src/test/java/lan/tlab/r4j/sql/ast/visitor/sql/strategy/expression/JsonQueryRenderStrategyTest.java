package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonQuery;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.WrapperBehavior;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.Test;

class JsonQueryRenderStrategyTest {

    @Test
    void standardSql2016WithBasicArguments() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql2008();
        JsonQueryRenderStrategy strategy = JsonQueryRenderStrategy.standardSql2016();
        JsonQuery jsonQuery = JsonQuery.of(ColumnReference.of("products", "data"), Literal.of("$.tags"));
        String sql = strategy.render(jsonQuery, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_QUERY(\"products\".\"data\", '$.tags')");
    }

    @Test
    void standardSql2016WithReturningType() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql2008();
        JsonQueryRenderStrategy strategy = JsonQueryRenderStrategy.standardSql2016();
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("products", "data"), Literal.of("$.tags"), "JSON", null, null, null, null);
        String sql = strategy.render(jsonQuery, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_QUERY(\"products\".\"data\", '$.tags' RETURNING JSON)");
    }

    @Test
    void standardSql2016WithAllOptions() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql2008();
        JsonQueryRenderStrategy strategy = JsonQueryRenderStrategy.standardSql2016();
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("products", "data"),
                Literal.of("$.tags"),
                "JSON",
                WrapperBehavior.WITH_WRAPPER,
                BehaviorKind.DEFAULT,
                "EMPTY ARRAY",
                BehaviorKind.NULL);
        String sql = strategy.render(jsonQuery, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "JSON_QUERY(\"products\".\"data\", '$.tags' RETURNING JSON WITH WRAPPER DEFAULT EMPTY ARRAY ON EMPTY NULL ON ERROR)");
    }

    @Test
    void postgreSql() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql2008();
        JsonQueryRenderStrategy strategy = JsonQueryRenderStrategy.postgreSql();
        JsonQuery jsonQuery = JsonQuery.of(ColumnReference.of("products", "data"), Literal.of("$.tags"));
        String sql = strategy.render(jsonQuery, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_QUERY(\"products\".\"data\", '$.tags')");
    }
}
