package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class ColumnDefinitionRenderStrategy implements SqlItemRenderStrategy {

    public String render(ColumnDefinition item, SqlRenderer sqlRenderer) {
        StringBuilder builder = new StringBuilder();

        // Rende il nome della colonna
        builder.append(sqlRenderer.getEscapeStrategy().apply(item.getName()));

        // Rende il tipo di dato
        builder.append(" ").append(item.getType());

        // Aggiunge i vincoli
        if (item.isNotNull()) {
            builder.append(" NOT NULL");
        }

        return builder.toString();
    }
}
