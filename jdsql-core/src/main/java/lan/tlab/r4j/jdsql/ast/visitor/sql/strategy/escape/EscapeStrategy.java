package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.escape;

public interface EscapeStrategy {
    String apply(String value);
}
