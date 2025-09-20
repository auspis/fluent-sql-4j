package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.DefaultConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        ColumnDefinition column =
                ColumnDefinition.builder("id", DataType.INTEGER).build();
        String sql = strategy.render(column, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"id\" INTEGER");
    }

    @Test
    void nullObject() {
        ColumnDefinition column = ColumnDefinition.nullObject();
        String sql = strategy.render(column, renderer, new AstContext());
        assertThat(sql).isBlank();
    }

    @Test
    void notNullConstraint() {
        ColumnDefinition column = ColumnDefinition.builder("name", DataType.VARCHAR_255)
                .notNullConstraint(new NotNullConstraint())
                .build();

        String sql = strategy.render(column, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"name\" VARCHAR(255) NOT NULL");
    }

    @Test
    void defaultConstraint() {
        ColumnDefinition column = ColumnDefinitionBuilder.varchar("name")
                .defaultConstraint(new DefaultConstraint(Literal.of("def-val")))
                .build();

        String sql = strategy.render(column, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"name\" VARCHAR(255) DEFAULT 'def-val'");
    }

    @Test
    void notNullAndDefault() {
        ColumnDefinition column = ColumnDefinitionBuilder.varchar("name")
                .notNullConstraint(new NotNullConstraint())
                .defaultConstraint(new DefaultConstraint(Literal.of("def-val")))
                .build();

        String sql = strategy.render(column, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"name\" VARCHAR(255) NOT NULL DEFAULT 'def-val'");
    }
}
