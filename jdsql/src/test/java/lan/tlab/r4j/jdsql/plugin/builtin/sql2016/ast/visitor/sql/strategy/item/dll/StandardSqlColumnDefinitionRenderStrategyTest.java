package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlColumnDefinitionRenderStrategyTest {
    private StandardSqlColumnDefinitionRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlColumnDefinitionRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
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
