package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Index;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.statement.CreateTableStatement;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;

class CreateTableStatementRenderStrategyTest {

    private CreateTableStatementRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new CreateTableStatementRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }
    
    @Test
    void ok() {
        CreateTableStatement statement = new CreateTableStatement(TableDefinition.builder()
                .table(new Table("my_table"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build()
                 ))
                .build());

        String sql = strategy.render(statement, renderer);
        assertThat(sql).isEqualTo("""
                CREATE TABLE "my_table" \
                (\
                "id" INTEGER, \
                "name" VARCHAR(255)\
                )\
                """);
    }
    
    @Test
    void withPrimaryKeyAndConstraint() {
        PrimaryKey pk = new PrimaryKey("id");
        CreateTableStatement statement = new CreateTableStatement(TableDefinition.builder()
                .table(new Table("my_table"))
                .primaryKey(pk)
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name")
                            .constraint(new NotNullConstraint())
                            .build()))
                .build());

        String sql = strategy.render(statement, renderer);

        assertThat(sql).isEqualTo("""
                CREATE TABLE "my_table" \
                (\
                "id" INTEGER, \
                "name" VARCHAR(255) NOT NULL, \
                PRIMARY KEY ("id")\
                )\
                """);
    }
    
    @Test
    void index() {
        CreateTableStatement statement = new CreateTableStatement(TableDefinition.builder()
                .table(new Table("my_table"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build(),
                        ColumnDefinitionBuilder.varchar("email").build()
                 ))
                .index(new Index("idx_email", "name"))
                .index(new Index("idx_email", "email"))
                .build());

        String sql = strategy.render(statement, renderer);
        assertThat(sql).isEqualTo("""
                CREATE TABLE "my_table" \
                (\
                "id" INTEGER, \
                "name" VARCHAR(255), \
                "email" VARCHAR(255), \
                INDEX "idx_email" ("name"), \
                INDEX "idx_email" ("email")\
                )\
                """);
    }

}
