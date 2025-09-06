package lan.tlab.sqlbuilder.ast.visitor.composer.renderer;

import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.escape.EscapeStrategy;

public interface SqlRenderer extends SqlVisitor<String> {

    EscapeStrategy getEscapeStrategy();
}
