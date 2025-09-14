package lan.tlab.sqlbuilder.ast.visitor;

import java.util.Objects;

public class AstContext {
    public enum Scope {
        DEFAULT,
        JOIN_ON
        // In futuro: WHERE, HAVING, GROUP_BY, ecc.
    }

    private final Scope scope;

    public AstContext() {
        scope = Scope.DEFAULT;
    }

    public AstContext(Scope scope) {
        this.scope = Objects.requireNonNull(scope);
    }

    public Scope getScope() {
        return scope;
    }

    public AstContext copy() {
        return new AstContext(getScope());
    }
}
