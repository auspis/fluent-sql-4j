package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Trim;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Trim.TrimMode;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.TrimPsStrategy;

public class DefaultTrimPsStrategy implements TrimPsStrategy {

    @Override
    public PsDto handle(Trim functionCall, PreparedStatementVisitor visitor, AstContext ctx) {
        TrimMode mode = functionCall.getMode();
        ScalarExpression charactersToRemove = functionCall.getCharactersToRemove();
        ScalarExpression stringExpression = functionCall.getStringExpression();

        StringBuilder sb = new StringBuilder();
        sb.append("TRIM(");

        if (mode != null) {
            sb.append(mode.name()).append(" ");
        }

        if (charactersToRemove != null) {
            PsDto charactersDto = charactersToRemove.accept(visitor, ctx);
            sb.append(charactersDto.sql()).append(" FROM ");
            PsDto stringDto = stringExpression.accept(visitor, ctx);
            sb.append(stringDto.sql()).append(")");

            List<Object> allParameters = new ArrayList<>(charactersDto.parameters());
            allParameters.addAll(stringDto.parameters());
            return new PsDto(sb.toString(), allParameters);
        } else {
            PsDto stringDto = stringExpression.accept(visitor, ctx);
            sb.append(stringDto.sql()).append(")");
            return new PsDto(sb.toString(), stringDto.parameters());
        }
    }
}
