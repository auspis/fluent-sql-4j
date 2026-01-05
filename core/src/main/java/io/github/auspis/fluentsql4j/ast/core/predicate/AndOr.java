package io.github.auspis.fluentsql4j.ast.core.predicate;

import java.util.List;
import java.util.stream.Stream;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record AndOr(LogicalOperator operator, List<Predicate> operands) implements LogicalExpression {

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
