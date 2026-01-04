package io.github.massimiliano.fluentsql4j.ast.dql.source;

import io.github.massimiliano.fluentsql4j.ast.visitor.Visitable;

/**
 * Marker interface for table sources that can be used in FROM clause.
 * Permitted implementations: TableIdentifier, FromSubquery, OnJoin
 */
public interface FromSource extends Visitable {}
