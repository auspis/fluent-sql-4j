package lan.tlab.sqlbuilder.ast.visitor;

public interface Visitable {

    <T> T accept(Visitor<T> visitor, AstContext ctx);
}
