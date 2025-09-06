package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.CheckConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
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
        ColumnDefinition column = ColumnDefinition.builder()
                .name("id")
                .type(ColumnDefinition.Type.INTEGER)
                .build();

        String sql = strategy.render(column, renderer);
        assertThat(sql).isEqualTo("\"id\" INTEGER");
    }

    @Test
    void constraint() {
        ColumnDefinition column = ColumnDefinition.builder()
                .name("name")
                .type(ColumnDefinition.Type.VARCHAR)
                .constraint(new NotNullConstraint())
                .build();

        String sql = strategy.render(column, renderer);
        assertThat(sql).isEqualTo("\"name\" VARCHAR NOT NULL");
    }
    
    @Test
    void constraints() {
        ColumnDefinition column = ColumnDefinition.builder()
                .name("age")
                .type(ColumnDefinition.Type.INTEGER)
                .constraint(new NotNullConstraint())
                .constraint(new CheckConstraint(Comparison.gt(
                        ColumnReference.of("", "age"),
                        Literal.of(18)
                 )))
                .build();
        
        String sql = strategy.render(column, renderer);
        assertThat(sql).isEqualTo("\"age\" INTEGER NOT NULL, CHECK (\"age\" > 18)");
    }
    
    @Test
    void varchar255() {
        fail();
//        ColumnDefinition column = ColumnDefinition.builder()
//                .name("name")
////                .type("VARCHAR(255)")
//                .type(ColumnDefinition.Type.VARCHAR)
//                .constraint(new NotNullConstraint())
//                .build();
//
//        String sql = strategy.render(column, renderer);
////        assertThat(sql).isEqualTo("\"name\" VARCHAR(255) NOT NULL");
//        assertThat(sql).isEqualTo("\"name\" VARCHAR NOT NULL");
    }
}
