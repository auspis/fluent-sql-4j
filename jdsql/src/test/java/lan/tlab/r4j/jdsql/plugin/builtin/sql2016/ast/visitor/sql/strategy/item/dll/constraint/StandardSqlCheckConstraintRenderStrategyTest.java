package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlCheckConstraintRenderStrategyTest {

    private StandardSqlCheckConstraintRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlCheckConstraintRenderStrategy();
        renderer = StandardSqlRendererFactory.standardSql();
    }

    @Test
    void ok() {
        Comparison expr = Comparison.gt(ColumnReference.of("", "age"), Literal.of(18));
        CheckConstraintDefinition constraint = new CheckConstraintDefinition(expr);
        String sql = strategy.render(constraint, renderer, new AstContext());
        assertThat(sql).isEqualTo("CHECK (\"age\" > 18)");
    }
}
