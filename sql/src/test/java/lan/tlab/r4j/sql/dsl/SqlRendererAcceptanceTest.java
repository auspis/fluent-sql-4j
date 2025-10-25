package lan.tlab.r4j.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.delete.DeleteBuilder;
import lan.tlab.r4j.sql.dsl.insert.InsertBuilder;
import lan.tlab.r4j.sql.dsl.select.SelectBuilder;
import lan.tlab.r4j.sql.dsl.select.SelectProjectionBuilder;
import lan.tlab.r4j.sql.dsl.table.CreateTableBuilder;
import lan.tlab.r4j.sql.dsl.update.UpdateBuilder;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.Test;

/**
 * Test that all builder classes properly accept and use SqlRenderer instances
 * for dialect-specific SQL generation.
 */
class SqlRendererAcceptanceTest {

    @Test
    void selectBuilderUsesStandardSql2008Renderer() {
        // DialectRenderer renderer = createDialectRenderer(TestDialectRendererFactory.standardSql2008());
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
        String sql = new SelectBuilder(renderer, "name", "email")
                .from("users")
                .where("age")
                .gt(18)
                .build();

        assertThat(sql).contains("\"users\"").contains("\"name\"").contains("\"email\"");
    }

    @Test
    void selectBuilderUsesMySqlRenderer() {
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererMysql();
        String sql = new SelectBuilder(renderer, "name", "email")
                .from("users")
                .where("age")
                .gt(18)
                .build();

        // MySQL uses backticks for escaping
        assertThat(sql).contains("`users`").contains("`name`").contains("`email`");
    }

    @Test
    void selectBuilderUsesSqlServerRenderer() {
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererSqlServer();
        String sql = new SelectBuilder(renderer, "name", "email")
                .from("users")
                .where("age")
                .gt(18)
                .build();

        // SQL Server uses square brackets for escaping
        assertThat(sql).contains("[users]").contains("[name]").contains("[email]");
    }

    @Test
    void selectProjectionBuilderUsesStandardSql2008Renderer() {
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
        String sql = new SelectProjectionBuilder(renderer)
                .column("name")
                .column("email")
                .from("users")
                .build();

        assertThat(sql).contains("\"users\"").contains("\"name\"").contains("\"email\"");
    }

    @Test
    void selectProjectionBuilderUsesMySqlRenderer() {
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererMysql();
        String sql = new SelectProjectionBuilder(renderer)
                .column("name")
                .column("email")
                .from("users")
                .build();

        assertThat(sql).contains("`users`").contains("`name`").contains("`email`");
    }

    @Test
    void insertBuilderUsesStandardSql2008Renderer() {
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
        String sql = new InsertBuilder(renderer, "users")
                .set("name", "John")
                .set("age", 30)
                .build();

        assertThat(sql).contains("INSERT INTO \"users\"").contains("\"name\"").contains("\"age\"");
    }

    @Test
    void insertBuilderUsesMySqlRenderer() {
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererMysql();
        String sql = new InsertBuilder(renderer, "users")
                .set("name", "John")
                .set("age", 30)
                .build();

        assertThat(sql).contains("INSERT INTO `users`").contains("`name`").contains("`age`");
    }

    @Test
    void updateBuilderUsesStandardSql2008Renderer() {
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
        String sql = new UpdateBuilder(renderer, "users")
                .set("name", "John")
                .set("age", 30)
                .where("id")
                .eq(1)
                .build();

        assertThat(sql).contains("UPDATE \"users\"").contains("SET \"name\"").contains("\"age\"");
    }

    @Test
    void updateBuilderUsesMySqlRenderer() {
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererMysql();
        String sql = new UpdateBuilder(renderer, "users")
                .set("name", "John")
                .set("age", 30)
                .where("id")
                .eq(1)
                .build();

        assertThat(sql).contains("UPDATE `users`").contains("SET `name`").contains("`age`");
    }

    @Test
    void deleteBuilderUsesStandardSql2008Renderer() {
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
        String sql = new DeleteBuilder(renderer, "users").where("id").eq(1).build();

        assertThat(sql).contains("DELETE FROM \"users\"").contains("WHERE \"users\".\"id\"");
    }

    @Test
    void deleteBuilderUsesMySqlRenderer() {
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererMysql();
        String sql = new DeleteBuilder(renderer, "users").where("id").eq(1).build();

        assertThat(sql).contains("DELETE FROM `users`").contains("WHERE `users`.`id`");
    }

    @Test
    void createTableBuilderUsesStandardSql2008Renderer() {
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
        String sql = new CreateTableBuilder(renderer, "users")
                .columnIntegerPrimaryKey("id")
                .columnVarcharNotNull("name", 100)
                .build();

        assertThat(sql).contains("CREATE TABLE \"users\"").contains("\"id\"").contains("\"name\"");
    }

    @Test
    void createTableBuilderUsesMySqlRenderer() {
        DialectRenderer renderer = TestDialectRendererFactory.dialectRendererMysql();
        String sql = new CreateTableBuilder(renderer, "users")
                .columnIntegerPrimaryKey("id")
                .columnVarcharNotNull("name", 100)
                .build();

        assertThat(sql).contains("CREATE TABLE `users`").contains("`id`").contains("`name`");
    }

    @Test
    void dslInstanceMethodsUseConfiguredRenderer() {
        // Create a DSL instance with StandardSQL renderer
        DSL dsl = TestDialectRendererFactory.dslStandardSql2008();

        // These should use the configured StandardSql2008 renderer
        String selectSql = dsl.select("name").from("users").build();
        String insertSql = dsl.insertInto("users").set("name", "John").build();
        String updateSql =
                dsl.update("users").set("name", "John").where("id").eq(1).build();
        String deleteSql = dsl.deleteFrom("users").where("id").eq(1).build();

        // All should use double quotes (StandardSql2008)
        assertThat(selectSql).contains("\"users\"").contains("\"name\"");
        assertThat(insertSql).contains("\"users\"").contains("\"name\"");
        assertThat(updateSql).contains("\"users\"").contains("\"name\"");
        assertThat(deleteSql).contains("\"users\"");
    }

    @Test
    void dslStaticMethodsAcceptCustomRenderer() {
        DialectRenderer mysqlRenderer = TestDialectRendererFactory.dialectRendererMysql();

        // These should use the provided MySQL renderer
        String selectSql = DSL.select(mysqlRenderer, "name").from("users").build();
        String insertSql =
                DSL.insertInto(mysqlRenderer, "users").set("name", "John").build();
        String updateSql = DSL.update(mysqlRenderer, "users")
                .set("name", "John")
                .where("id")
                .eq(1)
                .build();
        String deleteSql =
                DSL.deleteFrom(mysqlRenderer, "users").where("id").eq(1).build();

        // All should use backticks (MySQL)
        assertThat(selectSql).contains("`users`").contains("`name`");
        assertThat(insertSql).contains("`users`").contains("`name`");
        assertThat(updateSql).contains("`users`").contains("`name`");
        assertThat(deleteSql).contains("`users`");
    }
}
