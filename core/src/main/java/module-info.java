module lan.tlab.r4j.jdsql.core {
    requires transitive org.slf4j;
    requires org.semver4j;
    requires transitive java.sql;
    requires static lombok;

    // Export all packages with actual Java files - visibility will be controlled by jdsql-api and jdsql-spi
    exports lan.tlab.r4j.jdsql.dsl;
    exports lan.tlab.r4j.jdsql.dsl.select;
    exports lan.tlab.r4j.jdsql.dsl.insert;
    exports lan.tlab.r4j.jdsql.dsl.update;
    exports lan.tlab.r4j.jdsql.dsl.delete;
    exports lan.tlab.r4j.jdsql.dsl.merge;
    exports lan.tlab.r4j.jdsql.dsl.table;
    exports lan.tlab.r4j.jdsql.dsl.clause;
    exports lan.tlab.r4j.jdsql.dsl.util;
    exports lan.tlab.r4j.jdsql.plugin;
    exports lan.tlab.r4j.jdsql.plugin.util;
    exports lan.tlab.r4j.jdsql.plugin.builtin.sql2016;
    exports lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;
    exports lan.tlab.r4j.jdsql.ast.visitor;
    exports lan.tlab.r4j.jdsql.ast.visitor.ps;
    exports lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;
    exports lan.tlab.r4j.jdsql.ast.core.clause;
    exports lan.tlab.r4j.jdsql.ast.core.expression;
    exports lan.tlab.r4j.jdsql.ast.core.expression.scalar;
    exports lan.tlab.r4j.jdsql.ast.core.expression.aggregate;
    exports lan.tlab.r4j.jdsql.ast.core.expression.function;
    exports lan.tlab.r4j.jdsql.ast.core.expression.function.string;
    exports lan.tlab.r4j.jdsql.ast.core.expression.function.datetime;
    exports lan.tlab.r4j.jdsql.ast.core.expression.function.json;
    exports lan.tlab.r4j.jdsql.ast.core.expression.function.number;
    exports lan.tlab.r4j.jdsql.ast.core.expression.set;
    exports lan.tlab.r4j.jdsql.ast.core.expression.window;
    exports lan.tlab.r4j.jdsql.ast.core.identifier;
    exports lan.tlab.r4j.jdsql.ast.core.predicate;
    exports lan.tlab.r4j.jdsql.ast.core.statement;
    exports lan.tlab.r4j.jdsql.ast.ddl.definition;
    exports lan.tlab.r4j.jdsql.ast.ddl.statement;
    exports lan.tlab.r4j.jdsql.ast.dml.component;
    exports lan.tlab.r4j.jdsql.ast.dml.statement;
    exports lan.tlab.r4j.jdsql.ast.dql.clause;
    exports lan.tlab.r4j.jdsql.ast.dql.projection;
    exports lan.tlab.r4j.jdsql.ast.dql.source;
    exports lan.tlab.r4j.jdsql.ast.dql.source.join;
    exports lan.tlab.r4j.jdsql.ast.dql.statement;
    exports lan.tlab.r4j.jdsql.functional;

    // ServiceLoader support for plugin discovery
    uses lan.tlab.r4j.jdsql.plugin.SqlDialectPluginProvider;

    provides lan.tlab.r4j.jdsql.plugin.SqlDialectPluginProvider with
            lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSQLDialectPluginProvider;
}
