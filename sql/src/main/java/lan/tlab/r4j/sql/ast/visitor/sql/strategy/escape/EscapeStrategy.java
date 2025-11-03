package lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape;

public interface EscapeStrategy {
    String apply(String value);
}
