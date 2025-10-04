package lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape;

public interface EscapeStrategy {
    String apply(String value);

    static EscapeStrategy standard() {
        return value -> String.format("\"%s\"", value);
    }

    static EscapeStrategy sqlServer() {
        return value -> String.format("[%s]", value);
    }

    static EscapeStrategy mysql() {
        return value -> String.format("`%s`", value);
    }
}
