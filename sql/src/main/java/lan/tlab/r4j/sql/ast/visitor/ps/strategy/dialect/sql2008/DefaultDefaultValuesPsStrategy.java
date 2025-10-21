package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DefaultValuesPsStrategy;

public class DefaultDefaultValuesPsStrategy implements DefaultValuesPsStrategy {
    @Override
    public PsDto handle(DefaultValues defaultValues, Visitor<PsDto> renderer, AstContext ctx) {
        // For SQL DEFAULT VALUES
        return new PsDto("DEFAULT VALUES", List.of());
    }
}
