package lan.tlab.sqlbuilder.debug;

import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.DateArithmetic;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public class DateArithmeticDebug {
    public static void main(String[] args) {
        var interval = Interval.of(Literal.of(1), Interval.IntervalUnit.MONTH);
        var dateAdd = DateArithmetic.add(ColumnReference.of("subscriptions", "start_date"), interval);
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("subscriptions", "id"))))
                .from(From.of(new Table("subscriptions")))
                .where(Where.of(Comparison.lt(dateAdd, Literal.of("2023-12-31"))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());

        System.out.println("SQL: " + result.sql());
        System.out.println("Parameters: " + result.parameters());
    }
}
