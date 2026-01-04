module io.github.massimiliano.fluentsql4j.spi {
    // Transitive requirement on API and core - users get all SPI classes
    requires transitive io.github.massimiliano.fluentsql4j.api;
    requires transitive io.github.massimiliano.fluentsql4j.core;
    requires transitive org.slf4j;
    requires org.semver4j;
    requires java.sql;

    // ServiceLoader support for plugin discovery
    uses io.github.massimiliano.fluentsql4j.plugin.SqlDialectPluginProvider;
}
