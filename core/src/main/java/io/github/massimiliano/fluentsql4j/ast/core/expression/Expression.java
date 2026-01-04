package io.github.massimiliano.fluentsql4j.ast.core.expression;

import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitable;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

/**
 * Interfaccia base per tutte le espressioni nell'AST SQL. Una Expression rappresenta una parte di
 * una query che si valuta a un risultato. Estende SQLable per la generazione della stringa SQL.
 */
public interface Expression extends Visitable {

    @Override
    <T> T accept(Visitor<T> visitor, AstContext ctx);
}
