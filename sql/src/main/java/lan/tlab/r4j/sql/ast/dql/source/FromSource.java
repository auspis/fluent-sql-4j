package lan.tlab.r4j.sql.ast.dql.source;

import lan.tlab.r4j.sql.ast.visitor.Visitable;

/**
 * Marker interface for table sources that can be used in FROM clause.
 * Permitted implementations: TableIdentifier, FromSubquery, OnJoin
 */
public interface FromSource extends Visitable {}
