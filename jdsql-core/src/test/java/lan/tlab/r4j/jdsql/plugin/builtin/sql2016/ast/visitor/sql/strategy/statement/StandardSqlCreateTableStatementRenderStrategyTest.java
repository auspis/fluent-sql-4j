package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.IndexDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.TableDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.statement.CreateTableStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlCreateTableStatementRenderStrategyTest {

    private StandardSqlCreateTableStatementRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlCreateTableStatementRenderStrategy();
        renderer = StandardSqlRendererFactory.standardSql();
    }

    @Test
    void ok() {
        CreateTableStatement statement = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("my_table"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build()))
                .build());

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                CREATE TABLE "my_table" \
                (\
                "id" INTEGER, \
                "name" VARCHAR(255)\
                )\
                """);
    }

    @Test
    void emptyColumns() {
        CreateTableStatement statement = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("empty_table"))
                .build());
        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                        CREATE TABLE \"empty_table\" \
                        (\
                        )\
                        """);
    }

    @Test
    void multipleColumnTypes() {
        CreateTableStatement statement = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("types_table"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("desc").build(),
                        ColumnDefinitionBuilder.bool("flag").build()))
                .build());
        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                        CREATE TABLE \"types_table\" \
                        (\
                        \"id\" INTEGER, \
                        \"desc\" VARCHAR(255), \
                        \"flag\" BOOLEAN\
                        )\
                        """);
    }

    @Test
    void constraints() {
        CreateTableStatement statement = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("multi_constraint"))
                .columns(List.of(ColumnDefinitionBuilder.varchar("code")
                        .notNullConstraint(new NotNullConstraintDefinition())
                        .defaultConstraint(new DefaultConstraintDefinition(Literal.of("UNKNOWN")))
                        .build()))
                .build());
        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                        CREATE TABLE \"multi_constraint\" \
                        (\
                        \"code\" VARCHAR(255) NOT NULL DEFAULT 'UNKNOWN'\
                        )\
                        """);
    }

    @Test
    void multipleIndexesAndPrimaryKey() {
        CreateTableStatement statement = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("complex_table"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build(),
                        ColumnDefinitionBuilder.varchar("email").build()))
                .primaryKey(new PrimaryKeyDefinition("id"))
                .index(new IndexDefinition("idx_name", "name"))
                .index(new IndexDefinition("idx_email", "email"))
                .build());
        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                        CREATE TABLE \"complex_table\" \
                        (\
                        \"id\" INTEGER, \
                        \"name\" VARCHAR(255), \
                        \"email\" VARCHAR(255), \
                        PRIMARY KEY (\"id\"), \
                        INDEX \"idx_name\" (\"name\"), \
                        INDEX \"idx_email\" (\"email\")\
                        )\
                        """);
    }

    @Test
    void tableNameWithSpecialChars() {
        CreateTableStatement statement = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("user-profile"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("user-id").build(),
                        ColumnDefinitionBuilder.varchar("e-mail").build()))
                .build());
        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                        CREATE TABLE \"user-profile\" \
                        (\
                        \"user-id\" INTEGER, \
                        \"e-mail\" VARCHAR(255)\
                        )\
                        """);
    }

    @Test
    void withPrimaryKeyAndConstraint() {
        PrimaryKeyDefinition pk = new PrimaryKeyDefinition("id");
        CreateTableStatement statement = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("my_table"))
                .primaryKey(pk)
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name")
                                .notNullConstraint(new NotNullConstraintDefinition())
                                .build(),
                        ColumnDefinitionBuilder.integer("age").build()))
                .constraint(new CheckConstraintDefinition(Comparison.gt(ColumnReference.of("", "age"), Literal.of(18))))
                .build());

        String sql = strategy.render(statement, renderer, new AstContext());

        assertThat(sql)
                .isEqualTo(
                        """
                CREATE TABLE "my_table" \
                (\
                "id" INTEGER, \
                "name" VARCHAR(255) NOT NULL, \
                "age" INTEGER, \
                PRIMARY KEY ("id"), \
                CHECK ("age" > 18)\
                )\
                """);
    }

    @Test
    void index() {
        CreateTableStatement statement = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("my_table"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build(),
                        ColumnDefinitionBuilder.varchar("email").build()))
                .index(new IndexDefinition("idx_name", "name"))
                .index(new IndexDefinition("idx_email", "email"))
                .build());

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                CREATE TABLE "my_table" \
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
