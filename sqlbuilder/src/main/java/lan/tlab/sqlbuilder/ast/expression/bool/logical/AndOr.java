package lan.tlab.sqlbuilder.ast.expression.bool.logical;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AndOr implements LogicalExpression {

    private final LogicalOperator operator;
    private final List<BooleanExpression> operands;

    public static AndOr and(BooleanExpression... operands) {
        return and(Stream.of(operands).toList());
    }

    public static AndOr and(List<BooleanExpression> operands) {
        return new AndOr(LogicalOperator.AND, operands);
    }

    public static AndOr or(BooleanExpression... operands) {
        return or(Stream.of(operands).toList());
    }

    public static AndOr or(List<BooleanExpression> operands) {
        return new AndOr(LogicalOperator.OR, operands);
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
