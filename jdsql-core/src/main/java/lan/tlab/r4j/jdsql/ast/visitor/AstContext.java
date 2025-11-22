package lan.tlab.r4j.jdsql.ast.visitor;

public record AstContext(Scope scope) {
    public enum Scope {
        // In futuro: WHERE, HAVING, GROUP_BY, ecc.
        DEFAULT,
        JOIN_ON,
        UNION
    }

    public AstContext() {
        this(Scope.DEFAULT);
    }

    public AstContext copy() {
        return new AstContext(scope());
    }
}
