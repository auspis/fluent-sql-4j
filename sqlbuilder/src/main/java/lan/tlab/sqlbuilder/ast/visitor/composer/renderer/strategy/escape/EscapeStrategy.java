package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.escape;

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
