module lan.tlab.r4j.jdsql.api {
    // Required dependencies - make jdsql-core transitive so users get all API classes
    requires transitive lan.tlab.r4j.jdsql.core;
    requires transitive org.slf4j;
    requires java.sql;
}
