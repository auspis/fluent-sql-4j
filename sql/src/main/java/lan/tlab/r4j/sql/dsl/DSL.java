package lan.tlab.r4j.sql.dsl;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import lan.tlab.r4j.sql.dsl.delete.DeleteBuilder;
import lan.tlab.r4j.sql.dsl.insert.InsertBuilder;
import lan.tlab.r4j.sql.dsl.select.SelectBuilder;
import lan.tlab.r4j.sql.dsl.select.SelectProjectionBuilder;
import lan.tlab.r4j.sql.dsl.table.CreateTableBuilder;
import lan.tlab.r4j.sql.dsl.update.UpdateBuilder;

public class DSL {

    static final SqlRenderer SQL_RENDERER = SqlRendererFactory.standardSql2008();

    public static CreateTableBuilder createTable(String tableName) {
        return new CreateTableBuilder(SQL_RENDERER, tableName);
    }

    public static SelectProjectionBuilder select() {
        return new SelectProjectionBuilder(SQL_RENDERER);
    }

    public static SelectBuilder select(String... columns) {
        return new SelectBuilder(SQL_RENDERER, columns);
    }

    public static SelectBuilder selectAll() {
        return new SelectBuilder(SQL_RENDERER, "*");
    }

    public static InsertBuilder insertInto(String tableName) {
        return new InsertBuilder(SQL_RENDERER, tableName);
    }

    public static DeleteBuilder deleteFrom(String tableName) {
        return new DeleteBuilder(SQL_RENDERER, tableName);
    }

    public static UpdateBuilder update(String tableName) {
        return new UpdateBuilder(SQL_RENDERER, tableName);
    }
}
