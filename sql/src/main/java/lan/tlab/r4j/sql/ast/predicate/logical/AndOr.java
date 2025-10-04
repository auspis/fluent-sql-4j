package lan.tlab.r4j.sql.ast.predicate.logical;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AndOr implements LogicalExpression {

    private final LogicalOperator operator;
    private final List<Predicate> operands;

    public static AndOr and(Predicate... operands) {
        return and(Stream.of(operands).toList());
    }

    public static AndOr and(List<Predicate> operands) {
        return new AndOr(LogicalOperator.AND, operands);
    }

    public static AndOr or(Predicate... operands) {
        return or(Stream.of(operands).toList());
    }

    public static AndOr or(List<Predicate> operands) {
        return new AndOr(LogicalOperator.OR, operands);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
