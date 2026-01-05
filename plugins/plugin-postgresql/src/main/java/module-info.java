module io.github.massimiliano.fluentsql4j.plugin.postgresql {
    requires transitive io.github.massimiliano.fluentsql4j.spi;
    requires java.sql;
    requires static lombok;

    // Provide PostgreSQL dialect plugin implementation
    // Allow this module to call ServiceLoader for SqlDialectPluginProvider in tests
    uses io.github.auspis.fluentsql4j.plugin.SqlDialectPluginProvider;

    provides io.github.auspis.fluentsql4j.plugin.SqlDialectPluginProvider with
            io.github.auspis.fluentsql4j.plugin.builtin.postgre.PostgreSqlDialectPluginProvider;

    // Export plugin packages (optional, for advanced use)
    exports io.github.auspis.fluentsql4j.plugin.builtin.postgre;
    exports io.github.auspis.fluentsql4j.plugin.builtin.postgre.dsl;
    exports io.github.auspis.fluentsql4j.plugin.builtin.postgre.ast.visitor.ps.strategy;
}
