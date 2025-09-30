package lan.tlab.sqlbuilder.dsl;

import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.dsl.select.SelectBuilder;

public class DSL {

    static final SqlRenderer SQL_RENDERER = SqlRenderer.builder().build();

    public static TableBuilder createTable(String tableName) {
        return new TableBuilder(tableName);
    }

    public static SelectBuilder select(String... columns) {
        return new SelectBuilder(columns);
    }

    public static SelectBuilder selectAll() {
        return new SelectBuilder("*");
    }
}
