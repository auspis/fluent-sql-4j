module lan.tlab.r4j.jdsql.plugin.mysql {
    requires transitive lan.tlab.r4j.jdsql.spi;
    requires java.sql;
    requires static lombok;

    // Provide MySQL dialect plugin implementation
    // Allow this module to call ServiceLoader for SqlDialectPluginProvider in tests
    uses lan.tlab.r4j.jdsql.plugin.SqlDialectPluginProvider;

    provides lan.tlab.r4j.jdsql.plugin.SqlDialectPluginProvider with
            lan.tlab.r4j.jdsql.plugin.builtin.mysql.MysqlDialectPluginProvider;

    // Export plugin packages (optional, for advanced use)
    exports lan.tlab.r4j.jdsql.plugin.builtin.mysql;
    exports lan.tlab.r4j.jdsql.plugin.builtin.mysql.dsl;
    exports lan.tlab.r4j.jdsql.plugin.builtin.mysql.dsl.select;
    exports lan.tlab.r4j.jdsql.plugin.builtin.mysql.dsl.select.builders;
    exports lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;
}
