package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
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
                ColumnDefinition.builder("id", DataType.integer()).build();
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
        ColumnDefinition column = ColumnDefinition.builder("name", DataType.varchar(255))
                .notNullConstraint(new NotNullConstraintDefinition())
                .build();

        String sql = strategy.render(column, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"name\" VARCHAR(255) NOT NULL");
    }

    @Test
    void defaultConstraint() {
        ColumnDefinition column = ColumnDefinitionBuilder.varchar("name")
                .defaultConstraint(new DefaultConstraintDefinition(Literal.of("def-val")))
                .build();

        String sql = strategy.render(column, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"name\" VARCHAR(255) DEFAULT 'def-val'");
    }

    @Test
    void notNullAndDefault() {
        ColumnDefinition column = ColumnDefinitionBuilder.varchar("name")
                .notNullConstraint(new NotNullConstraintDefinition())
                .defaultConstraint(new DefaultConstraintDefinition(Literal.of("def-val")))
                .build();

        String sql = strategy.render(column, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"name\" VARCHAR(255) NOT NULL DEFAULT 'def-val'");
    }
}
