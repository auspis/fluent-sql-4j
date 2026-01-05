package io.github.auspis.fluentsql4j.ast.ddl.statement;

import io.github.auspis.fluentsql4j.ast.core.statement.Statement;

/**
 * Marker interface for Data Definition Language (DDL) statements.
 * DDL statements define or modify the structure of database objects.
 * Examples: CREATE, DROP, ALTER
 */
public interface DataDefinitionStatement extends Statement {}
