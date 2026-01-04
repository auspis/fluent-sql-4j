package io.github.massimiliano.fluentsql4j.ast.visitor;

public interface Visitable {

    <T> T accept(Visitor<T> visitor, AstContext ctx);
}
