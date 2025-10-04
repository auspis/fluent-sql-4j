package lan.tlab.r4j.sql.ast.visitor;

public interface Visitable {

    <T> T accept(Visitor<T> visitor, AstContext ctx);
}
