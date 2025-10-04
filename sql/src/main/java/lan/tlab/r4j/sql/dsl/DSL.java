package lan.tlab.r4j.sql.dsl;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import lan.tlab.r4j.sql.dsl.select.SelectBuilder;
import lan.tlab.r4j.sql.dsl.table.TableBuilder;

public class DSL {

    static final SqlRenderer SQL_RENDERER = SqlRendererFactory.standardSql2008();

    public static TableBuilder createTable(String tableName) {
        return new TableBuilder(SQL_RENDERER, tableName);
    }

    public static SelectBuilder select(String... columns) {
        return new SelectBuilder(SQL_RENDERER, columns);
    }

    public static SelectBuilder selectAll() {
        return new SelectBuilder(SQL_RENDERER, "*");
    }
}
