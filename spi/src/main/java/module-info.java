module io.github.auspis.fluentsql4j.spi {
    // Transitive requirement on API and core - users get all SPI classes
    requires transitive io.github.auspis.fluentsql4j.api;
    requires transitive io.github.auspis.fluentsql4j.core;
    requires transitive org.slf4j;
    requires org.semver4j;
    requires java.sql;

    // ServiceLoader support for plugin discovery
    uses io.github.auspis.fluentsql4j.plugin.SqlDialectPluginProvider;
}
