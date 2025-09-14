package lan.tlab.sqlbuilder.ast.visitor;

public interface Visitable {

    <T> T accept(SqlVisitor<T> visitor, AstContext ctx);
}
