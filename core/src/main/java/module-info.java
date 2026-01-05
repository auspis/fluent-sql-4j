module io.github.massimiliano.fluentsql4j.core {
    requires transitive org.slf4j;
    requires org.semver4j;
    requires transitive java.sql;
    requires static lombok;

    // Export all packages with actual Java files - visibility will be controlled by api and spi
    exports io.github.auspis.fluentsql4j.dsl;
    exports io.github.auspis.fluentsql4j.dsl.select;
    exports io.github.auspis.fluentsql4j.dsl.insert;
    exports io.github.auspis.fluentsql4j.dsl.update;
    exports io.github.auspis.fluentsql4j.dsl.delete;
    exports io.github.auspis.fluentsql4j.dsl.merge;
    exports io.github.auspis.fluentsql4j.dsl.table;
    exports io.github.auspis.fluentsql4j.dsl.clause;
    exports io.github.auspis.fluentsql4j.dsl.util;
    exports io.github.auspis.fluentsql4j.plugin;
    exports io.github.auspis.fluentsql4j.plugin.util;
    exports io.github.auspis.fluentsql4j.plugin.builtin.sql2016;
    exports io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;
    exports io.github.auspis.fluentsql4j.ast.visitor;
    exports io.github.auspis.fluentsql4j.ast.visitor.ps;
    exports io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;
    exports io.github.auspis.fluentsql4j.ast.core.clause;
    exports io.github.auspis.fluentsql4j.ast.core.expression;
    exports io.github.auspis.fluentsql4j.ast.core.expression.scalar;
    exports io.github.auspis.fluentsql4j.ast.core.expression.aggregate;
    exports io.github.auspis.fluentsql4j.ast.core.expression.function;
    exports io.github.auspis.fluentsql4j.ast.core.expression.function.string;
    exports io.github.auspis.fluentsql4j.ast.core.expression.function.datetime;
    exports io.github.auspis.fluentsql4j.ast.core.expression.function.json;
    exports io.github.auspis.fluentsql4j.ast.core.expression.function.number;
    exports io.github.auspis.fluentsql4j.ast.core.expression.set;
    exports io.github.auspis.fluentsql4j.ast.core.expression.window;
    exports io.github.auspis.fluentsql4j.ast.core.identifier;
    exports io.github.auspis.fluentsql4j.ast.core.predicate;
    exports io.github.auspis.fluentsql4j.ast.core.statement;
    exports io.github.auspis.fluentsql4j.ast.ddl.definition;
    exports io.github.auspis.fluentsql4j.ast.ddl.statement;
    exports io.github.auspis.fluentsql4j.ast.dml.component;
    exports io.github.auspis.fluentsql4j.ast.dml.statement;
    exports io.github.auspis.fluentsql4j.ast.dql.clause;
    exports io.github.auspis.fluentsql4j.ast.dql.projection;
    exports io.github.auspis.fluentsql4j.ast.dql.source;
    exports io.github.auspis.fluentsql4j.ast.dql.source.join;
    exports io.github.auspis.fluentsql4j.ast.dql.statement;
    exports io.github.auspis.fluentsql4j.functional;

    // ServiceLoader support for plugin discovery
    uses io.github.auspis.fluentsql4j.plugin.SqlDialectPluginProvider;

    provides io.github.auspis.fluentsql4j.plugin.SqlDialectPluginProvider with
            io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPluginProvider;
}
