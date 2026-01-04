package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

public interface EscapeStrategy {
    String apply(String value);
}
