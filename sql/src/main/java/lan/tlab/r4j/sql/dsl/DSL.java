package lan.tlab.r4j.sql.dsl;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.delete.DeleteBuilder;
import lan.tlab.r4j.sql.dsl.insert.InsertBuilder;
import lan.tlab.r4j.sql.dsl.select.SelectBuilder;
import lan.tlab.r4j.sql.dsl.select.SelectProjectionBuilder;
import lan.tlab.r4j.sql.dsl.table.CreateTableBuilder;
import lan.tlab.r4j.sql.dsl.update.UpdateBuilder;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginRegistry;
import lan.tlab.r4j.sql.plugin.builtin.standardsql2008.StandardSQLDialectPlugin;

public class DSL {

    static final DialectRenderer DIALECT_RENDERER = getDefaultRenderer();

    /**
     * Gets the default dialect renderer for the DSL.
     * <p>
     * This method retrieves the Standard SQL:2008 dialect renderer from the
     * plugin registry. If the registry lookup fails, it creates a renderer
     * directly from the plugin.
     *
     * @return the default Standard SQL:2008 dialect renderer, never {@code null}
     */
    private static DialectRenderer getDefaultRenderer() {
        SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();
        return registry.getDialectRenderer(
                        StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION)
                .orElse(StandardSQLDialectPlugin.instance().createRenderer());
    }

    public static CreateTableBuilder createTable(String tableName) {
        return new CreateTableBuilder(DIALECT_RENDERER, tableName);
    }

    public static CreateTableBuilder createTable(DialectRenderer renderer, String tableName) {
        return new CreateTableBuilder(renderer, tableName);
    }

    public static SelectProjectionBuilder select() {
        return new SelectProjectionBuilder(DIALECT_RENDERER);
    }

    public static SelectProjectionBuilder select(DialectRenderer renderer) {
        return new SelectProjectionBuilder(renderer);
    }

    public static SelectBuilder select(String... columns) {
        return new SelectBuilder(DIALECT_RENDERER, columns);
    }

    public static SelectBuilder select(DialectRenderer renderer, String... columns) {
        return new SelectBuilder(renderer, columns);
    }

    public static SelectBuilder selectAll() {
        return new SelectBuilder(DIALECT_RENDERER, "*");
    }

    public static SelectBuilder selectAll(DialectRenderer renderer) {
        return new SelectBuilder(renderer, "*");
    }

    public static InsertBuilder insertInto(String tableName) {
        return new InsertBuilder(DIALECT_RENDERER, tableName);
    }

    public static InsertBuilder insertInto(DialectRenderer renderer, String tableName) {
        return new InsertBuilder(renderer, tableName);
    }

    public static DeleteBuilder deleteFrom(String tableName) {
        return new DeleteBuilder(DIALECT_RENDERER, tableName);
    }

    public static DeleteBuilder deleteFrom(DialectRenderer renderer, String tableName) {
        return new DeleteBuilder(renderer, tableName);
    }

    public static UpdateBuilder update(String tableName) {
        return new UpdateBuilder(DIALECT_RENDERER, tableName);
    }

    public static UpdateBuilder update(DialectRenderer renderer, String tableName) {
        return new UpdateBuilder(renderer, tableName);
    }
}
