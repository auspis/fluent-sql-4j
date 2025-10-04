package lan.tlab.r4j.sql.ast.visitor;

public class AstContext {
    public enum Scope {
        // In futuro: WHERE, HAVING, GROUP_BY, ecc.
        DEFAULT,
        JOIN_ON,
        UNION
    }

    private final Scope scope;

    public AstContext() {
        scope = Scope.DEFAULT;
    }

    public AstContext(Scope scope) {
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }

    public AstContext copy() {
        return new AstContext(getScope());
    }
}
