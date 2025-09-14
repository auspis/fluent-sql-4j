package lan.tlab.sqlbuilder.ast.clause.from.source.join;

import lan.tlab.sqlbuilder.ast.clause.from.source.FromSource;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OnJoin implements FromSource {

    private final FromSource left;
    private final JoinType type;
    private final FromSource right;
    private final BooleanExpression onCondition;

    public enum JoinType {
        INNER,
        LEFT, // LEFT OUTER
        RIGHT, // RIGHT OUTER
        FULL, // FULL OUTER
        CROSS // CROSS JOIN
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
