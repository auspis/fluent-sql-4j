package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.Trim;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.Trim.TrimMode;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.TrimPsStrategy;

public class StandardSqlTrimPsStrategy implements TrimPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Trim functionCall, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        TrimMode mode = functionCall.mode();
        ScalarExpression charactersToRemove = functionCall.charactersToRemove();
        ScalarExpression stringExpression = functionCall.stringExpression();

        StringBuilder sb = new StringBuilder();
        sb.append("TRIM(");

        if (mode != null) {
            sb.append(mode.name()).append(" ");
        }

        if (charactersToRemove != null) {
            PreparedStatementSpec charactersDto = charactersToRemove.accept(astToPsSpecVisitor, ctx);
            sb.append(charactersDto.sql()).append(" FROM ");
            PreparedStatementSpec stringDto = stringExpression.accept(astToPsSpecVisitor, ctx);
            sb.append(stringDto.sql()).append(")");

            List<Object> allParameters = new ArrayList<>(charactersDto.parameters());
            allParameters.addAll(stringDto.parameters());
            return new PreparedStatementSpec(sb.toString(), allParameters);
        } else {
            PreparedStatementSpec stringDto = stringExpression.accept(astToPsSpecVisitor, ctx);
            sb.append(stringDto.sql()).append(")");
            return new PreparedStatementSpec(sb.toString(), stringDto.parameters());
        }
    }
}
