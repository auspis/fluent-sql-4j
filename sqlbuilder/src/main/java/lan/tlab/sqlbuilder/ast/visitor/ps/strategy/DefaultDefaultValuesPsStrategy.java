package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.DefaultValues;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public class DefaultDefaultValuesPsStrategy implements DefaultValuesPsStrategy {
    @Override
    public PsDto handle(DefaultValues defaultValues, Visitor<PsDto> visitor, AstContext ctx) {
        // For SQL DEFAULT VALUES
        return new PsDto("DEFAULT VALUES", List.of());
    }
}
