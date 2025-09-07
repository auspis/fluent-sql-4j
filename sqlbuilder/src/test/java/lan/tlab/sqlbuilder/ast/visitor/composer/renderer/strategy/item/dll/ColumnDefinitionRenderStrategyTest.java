package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.CheckConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;

class ColumnDefinitionRenderStrategyTest {

    private ColumnDefinitionRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new ColumnDefinitionRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        ColumnDefinition column = ColumnDefinition.builder("id", DataType.INTEGER).build();
        String sql = strategy.render(column, renderer);
        assertThat(sql).isEqualTo("\"id\" INTEGER");
    }

    @Test
    void constraint() {
        ColumnDefinition column = ColumnDefinition.builder("name", DataType.VARCHAR_255)
                .constraint(new NotNullConstraint())
                .build();

        String sql = strategy.render(column, renderer);
        assertThat(sql).isEqualTo("\"name\" VARCHAR(255) NOT NULL");
    }
    
    @Test
    void constraints() {
        ColumnDefinition column = ColumnDefinition.builder("age", DataType.INTEGER)
                .constraint(new NotNullConstraint())
                .constraint(new CheckConstraint(Comparison.gt(
                        ColumnReference.of("", "age"),
                        Literal.of(18)
                 )))
                .build();
        
        String sql = strategy.render(column, renderer);
        assertThat(sql).isEqualTo("\"age\" INTEGER NOT NULL, CHECK (\"age\" > 18)");
    }
    
}
