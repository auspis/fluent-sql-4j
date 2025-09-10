package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.CheckConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.UniqueConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Index;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TableDefinitionRenderStrategyTest {

    private TableDefinitionRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new TableDefinitionRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        TableDefinition tableDef = TableDefinition.builder()
                .table(new Table("my_table"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build()))
                .build();

        String sql = strategy.render(tableDef, renderer);
        assertThat(sql)
                .isEqualTo(
                        """
                "my_table" \
                (\
                "id" INTEGER, \
                "name" VARCHAR(255)\
                )\
                """);
    }

    @Test
    void withPrimaryKeyAndNotNull() {
        PrimaryKey pk = new PrimaryKey("id");
        TableDefinition tableDef = TableDefinition.builder()
                .table(new Table("my_table"))
                .primaryKey(pk)
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name")
                                .notNullConstraint(new NotNullConstraint())
                                .build()))
                .build();

        String sql = strategy.render(tableDef, renderer);

        assertThat(sql)
                .isEqualTo(
                        """
                "my_table" \
                (\
                "id" INTEGER, \
                "name" VARCHAR(255) NOT NULL, \
                PRIMARY KEY ("id")\
                )\
                """);
    }

    @Test
    void constraints() {
        PrimaryKey pk = new PrimaryKey("id");
        TableDefinition tableDef = TableDefinition.builder()
                .table(new Table("my_table"))
                .primaryKey(pk)
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build(),
                        ColumnDefinitionBuilder.varchar("email").build(),
                        ColumnDefinitionBuilder.integer("age").build()))
                .constraint(new UniqueConstraint("email"))
                .constraint(new CheckConstraint(Comparison.gt(ColumnReference.of("", "age"), Literal.of(18))))
                .build();

        String sql = strategy.render(tableDef, renderer);

        assertThat(sql)
                .isEqualTo(
                        """
                "my_table" \
                (\
                "id" INTEGER, \
                "name" VARCHAR(255), \
                "email" VARCHAR(255), \
                "age" INTEGER, \
                PRIMARY KEY ("id"), \
                UNIQUE ("email"), \
                CHECK ("age" > 18)\
                )\
                """);
    }

    @Test
    void index() {
        TableDefinition tableDef = TableDefinition.builder()
                .table(new Table("my_table"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build(),
                        ColumnDefinitionBuilder.varchar("email").build()))
                .index(new Index("idx_email", "name"))
                .index(new Index("idx_email", "email"))
                .build();

        String sql = strategy.render(tableDef, renderer);
        assertThat(sql)
                .isEqualTo(
                        """
                "my_table" \
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
