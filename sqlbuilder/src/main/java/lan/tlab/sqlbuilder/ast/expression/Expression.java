package lan.tlab.sqlbuilder.ast.expression;

import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lan.tlab.sqlbuilder.ast.visitor.Visitable;

/**
 * Interfaccia base per tutte le espressioni nell'AST SQL. Una Expression rappresenta una parte di
 * una query che si valuta a un risultato. Estende SQLable per la generazione della stringa SQL.
 */
public interface Expression extends Visitable {

    @Override
    <T> T accept(SqlVisitor<T> visitor, AstContext ctx);
}
