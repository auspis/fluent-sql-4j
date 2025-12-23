module lan.tlab.r4j.jdsql.plugin.postgresql {
    requires transitive lan.tlab.r4j.jdsql.spi;
    requires java.sql;
    requires static lombok;

    // Provide PostgreSQL dialect plugin implementation
    provides lan.tlab.r4j.jdsql.plugin.SqlDialectPluginProvider with
            lan.tlab.r4j.jdsql.plugin.builtin.postgre.PostgreSqlDialectPluginProvider;

    // Export plugin packages (optional, for advanced use)
    exports lan.tlab.r4j.jdsql.plugin.builtin.postgre;
    exports lan.tlab.r4j.jdsql.plugin.builtin.postgre.dsl;
    exports lan.tlab.r4j.jdsql.plugin.builtin.postgre.ast.visitor.ps.strategy;
}
