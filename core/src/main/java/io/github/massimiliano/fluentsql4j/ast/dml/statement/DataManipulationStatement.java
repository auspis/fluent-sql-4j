package io.github.massimiliano.fluentsql4j.ast.dml.statement;

import io.github.massimiliano.fluentsql4j.ast.core.statement.Statement;

/**
 * Marker interface for Data Manipulation Language (DML) statements.
 * DML statements modify data within database objects.
 * Examples: INSERT, UPDATE, DELETE
 */
public interface DataManipulationStatement extends Statement {}
