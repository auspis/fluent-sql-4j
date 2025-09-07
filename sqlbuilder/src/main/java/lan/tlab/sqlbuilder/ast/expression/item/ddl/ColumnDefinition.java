package lan.tlab.sqlbuilder.ast.expression.item.ddl;

import java.util.List;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lan.tlab.sqlbuilder.ast.visitor.Visitable;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.Tolerate;

@Builder()
@Getter
public class ColumnDefinition implements Visitable {

//    private static final String YYYY_MM_DD = "YYYY-MM-dd";

    private final String name;
    @Default
    private final DataType type = DataType.VARCHAR_255;
    @Singular
    private final List<Constraint> constraints;


    // TODO: aaa - move the following fields in a "ColumnMetadata" class
    @Deprecated
    private final String businessName;

    @Deprecated
    @Default
    private final String businessFormat = "";
    

    public static ColumnDefinition nullObject() {
        return builder().build();
    }


//    public static ColumnDefinition string(String name) {
//        return columnBuilder(name, name, DataType.VARCHAR_255).build();
//    }
//
//    public static ColumnDefinition string(String businessName, String name) {
//        return columnBuilder(businessName, name, DataType.VARCHAR_255).build();
//    }
//
//    public static ColumnDefinition integer(String name) {
//        return columnBuilder(name, name, DataType.INTEGER).build();
//    }
//
//    public static ColumnDefinition date(String name) {
//        return columnBuilder(name, name, Type.DATE).businessFormat(YYYY_MM_DD).build();
//    }
//
//    private static ColumnDefinitionBuilder columnBuilder(String businessName, String name, Type type) {
//        return builder().businessName(businessName).name(name).type(type);
//    }

    public static class ColumnDefinitionBuilder {
        
        public static ColumnDefinitionBuilder integer(String name) {
            return builder(name, DataType.INTEGER);
        }

        public static ColumnDefinitionBuilder varchar(String name) {
            return builder(name, DataType.VARCHAR_255);
        }

        public static ColumnDefinitionBuilder date(String name) {
            return builder(name, DataType.DATE).businessFormat("YYYY-MM-dd");
        }
        
    }
    
    @Tolerate
    public static ColumnDefinitionBuilder builder(String name, DataType type) {
        return builder()
                .name(name)
                .businessName(name)
                .type(type);
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
