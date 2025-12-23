module lan.tlab.r4j.jdsql.spi {
    // Transitive requirement on API and core - users get all SPI classes
    requires transitive lan.tlab.r4j.jdsql.api;
    requires transitive lan.tlab.r4j.jdsql.core;
    requires transitive org.slf4j;
    requires transitive semver4j;
    requires java.sql;

    // ServiceLoader support for plugin discovery
    uses lan.tlab.r4j.jdsql.plugin.SqlDialectPluginProvider;
}
