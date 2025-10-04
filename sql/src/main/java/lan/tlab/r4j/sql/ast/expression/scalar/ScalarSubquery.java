package lan.tlab.r4j.sql.ast.expression.scalar;

import lan.tlab.r4j.sql.ast.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScalarSubquery implements ScalarExpression {

    private final TableExpression tableExpression;

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
