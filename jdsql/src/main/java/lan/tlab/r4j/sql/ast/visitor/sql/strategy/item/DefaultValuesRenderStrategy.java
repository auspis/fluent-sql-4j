package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.dml.component.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface DefaultValuesRenderStrategy {
    String render(DefaultValues item, SqlRenderer sqlRenderer, AstContext ctx);
}
