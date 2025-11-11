package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.sql.ast.dml.component.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DefaultValuesPsStrategy;

public class StandardSqlDefaultValuesPsStrategy implements DefaultValuesPsStrategy {
    @Override
    public PsDto handle(DefaultValues defaultValues, Visitor<PsDto> renderer, AstContext ctx) {
        // For SQL DEFAULT VALUES
        return new PsDto("DEFAULT VALUES", List.of());
    }
}
