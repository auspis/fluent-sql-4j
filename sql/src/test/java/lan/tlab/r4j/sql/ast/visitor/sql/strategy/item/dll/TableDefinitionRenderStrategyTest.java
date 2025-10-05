package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.IndexDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
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
                .table(new TableIdentifier("my_table"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build()))
                .build();

        String sql = strategy.render(tableDef, renderer, new AstContext());
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
        PrimaryKeyDefinition pk = new PrimaryKeyDefinition("id");
        TableDefinition tableDef = TableDefinition.builder()
                .table(new TableIdentifier("my_table"))
                .primaryKey(pk)
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name")
                                .notNullConstraint(new NotNullConstraintDefinition())
                                .build()))
                .build();

        String sql = strategy.render(tableDef, renderer, new AstContext());

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
        PrimaryKeyDefinition pk = new PrimaryKeyDefinition("id");
        TableDefinition tableDef = TableDefinition.builder()
                .table(new TableIdentifier("my_table"))
                .primaryKey(pk)
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build(),
                        ColumnDefinitionBuilder.varchar("email").build(),
                        ColumnDefinitionBuilder.integer("age").build()))
                .constraint(new UniqueConstraintDefinition("email"))
                .constraint(new CheckConstraintDefinition(Comparison.gt(ColumnReference.of("", "age"), Literal.of(18))))
                .build();

        String sql = strategy.render(tableDef, renderer, new AstContext());

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
                .table(new TableIdentifier("my_table"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build(),
                        ColumnDefinitionBuilder.varchar("email").build()))
                .index(new IndexDefinition("idx_name", "name"))
                .index(new IndexDefinition("idx_email", "email"))
                .build();

        String sql = strategy.render(tableDef, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                "my_table" \
                (\
                "id" INTEGER, \
                "name" VARCHAR(255), \
                "email" VARCHAR(255), \
                INDEX "idx_name" ("name"), \
                INDEX "idx_email" ("email")\
                )\
                """);
    }
}
