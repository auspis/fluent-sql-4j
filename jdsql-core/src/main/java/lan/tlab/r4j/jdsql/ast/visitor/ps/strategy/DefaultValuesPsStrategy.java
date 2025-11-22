package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.DefaultValues;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface DefaultValuesPsStrategy {
    PsDto handle(DefaultValues defaultValues, Visitor<PsDto> visitor, AstContext ctx);
}
