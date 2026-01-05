module io.github.massimiliano.fluentsql4j.plugin.mysql {
    requires transitive io.github.massimiliano.fluentsql4j.spi;
    requires java.sql;
    requires static lombok;

    // Provide MySQL dialect plugin implementation
    // Allow this module to call ServiceLoader for SqlDialectPluginProvider in tests
    uses io.github.auspis.fluentsql4j.plugin.SqlDialectPluginProvider;

    provides io.github.auspis.fluentsql4j.plugin.SqlDialectPluginProvider with
            io.github.auspis.fluentsql4j.plugin.builtin.mysql.MysqlDialectPluginProvider;

    // Export plugin packages (optional, for advanced use)
    exports io.github.auspis.fluentsql4j.plugin.builtin.mysql;
    exports io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl;
    exports io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.select;
    exports io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.select.builders;
    exports io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;
}
