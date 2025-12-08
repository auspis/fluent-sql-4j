package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

public interface EscapeStrategy {
    String apply(String value);
}
