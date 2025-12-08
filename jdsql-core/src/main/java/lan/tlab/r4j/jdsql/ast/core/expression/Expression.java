package lan.tlab.r4j.jdsql.ast.core.expression;

import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitable;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

/**
 * Interfaccia base per tutte le espressioni nell'AST SQL. Una Expression rappresenta una parte di
 * una query che si valuta a un risultato. Estende SQLable per la generazione della stringa SQL.
 */
public interface Expression extends Visitable {

    @Override
    <T> T accept(Visitor<T> visitor, AstContext ctx);
}
