package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Trim;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Trim.TrimMode;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.TrimPsStrategy;

public class StandardSqlTrimPsStrategy implements TrimPsStrategy {

    @Override
    public PsDto handle(Trim functionCall, PreparedStatementRenderer renderer, AstContext ctx) {
        TrimMode mode = functionCall.mode();
        ScalarExpression charactersToRemove = functionCall.charactersToRemove();
        ScalarExpression stringExpression = functionCall.stringExpression();

        StringBuilder sb = new StringBuilder();
        sb.append("TRIM(");

        if (mode != null) {
            sb.append(mode.name()).append(" ");
        }

        if (charactersToRemove != null) {
            PsDto charactersDto = charactersToRemove.accept(renderer, ctx);
            sb.append(charactersDto.sql()).append(" FROM ");
            PsDto stringDto = stringExpression.accept(renderer, ctx);
            sb.append(stringDto.sql()).append(")");

            List<Object> allParameters = new ArrayList<>(charactersDto.parameters());
            allParameters.addAll(stringDto.parameters());
            return new PsDto(sb.toString(), allParameters);
        } else {
            PsDto stringDto = stringExpression.accept(renderer, ctx);
            sb.append(stringDto.sql()).append(")");
            return new PsDto(sb.toString(), stringDto.parameters());
        }
    }
}
