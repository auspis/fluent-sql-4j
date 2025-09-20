package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.CheckConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.DefaultConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Index;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.statement.CreateTableStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        CreateTableStatement statement = new CreateTableStatement(
                TableDefinition.builder().table(new Table("empty_table")).build());
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
                .table(new Table("types_table"))
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
                .table(new Table("multi_constraint"))
                .columns(List.of(ColumnDefinitionBuilder.varchar("code")
                        .notNullConstraint(new NotNullConstraint())
                        .defaultConstraint(new DefaultConstraint(Literal.of("UNKNOWN")))
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
                .table(new Table("complex_table"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build(),
                        ColumnDefinitionBuilder.varchar("email").build()))
                .primaryKey(new PrimaryKey("id"))
                .index(new Index("idx_name", "name"))
                .index(new Index("idx_email", "email"))
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
                .table(new Table("user-profile"))
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
        PrimaryKey pk = new PrimaryKey("id");
        CreateTableStatement statement = new CreateTableStatement(TableDefinition.builder()
                .table(new Table("my_table"))
                .primaryKey(pk)
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name")
                                .notNullConstraint(new NotNullConstraint())
                                .build(),
                        ColumnDefinitionBuilder.integer("age").build()))
                .constraint(new CheckConstraint(Comparison.gt(ColumnReference.of("", "age"), Literal.of(18))))
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
                .table(new Table("my_table"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build(),
                        ColumnDefinitionBuilder.varchar("email").build()))
                .index(new Index("idx_name", "name"))
                .index(new Index("idx_email", "email"))
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
