package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Cast;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlScalarExpressionProjectionRenderStrategyTest {

    private StandardSqlScalarExpressionProjectionRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlScalarExpressionProjectionRenderStrategy();
        sqlRenderer = StandardSqlRendererFactory.standardSql();
    }

    @Test
    void columnReference() {
        ScalarExpressionProjection projection = new ScalarExpressionProjection(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(projection, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\".\"name\"");
    }

    @Test
    void literalWithAlias() {
        ScalarExpressionProjection projection = new ScalarExpressionProjection(Literal.of(23), "age");
        String sql = strategy.render(projection, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("23 AS age");
    }

    @Test
    void castColumnToVarchar() {
        ScalarExpressionProjection projection =
                new ScalarExpressionProjection(Cast.of(ColumnReference.of("Customer", "id"), "VARCHAR(255)"), "strId");
        String sql = strategy.render(projection, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CAST(\"Customer\".\"id\" AS VARCHAR(255)) AS strId");
    }
}
