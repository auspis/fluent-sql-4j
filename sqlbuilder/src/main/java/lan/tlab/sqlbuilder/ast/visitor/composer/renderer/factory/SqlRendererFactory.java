package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory;

import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.pagination.PaginationRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.escape.EscapeStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.ConcatRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.CurrentDateRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.CurrentDateTimeRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.DataLengthRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.DateArithmeticRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.ExceptRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.LegthRenderStrategy;

public class SqlRendererFactory {

    public static SqlRendererImpl standardSql2008() {
        return SqlRendererImpl.builder().build();
    }

    public static SqlRendererImpl sqlServer() {
        return SqlRendererImpl.builder()
                .escapeStrategy(EscapeStrategy.sqlServer())
                .currentDateTimeStrategy(CurrentDateTimeRenderStrategy.sqlServer())
                .dateArithmeticStrategy(DateArithmeticRenderStrategy.sqlServer())
                .lengthStrategy(LegthRenderStrategy.sqlServer())
                .build();
    }

    public static SqlRendererImpl mysql() {
        /*
         * [OK] PaginationRenderStrategy: MySQL utilizza la sintassi LIMIT <offset>, <count> per la
         * paginazione, diversa da OFFSET ... ROWS ... standard. Dovresti sostituire la strategia di default
         * (OffsetRowsRenderStrategy) con una specifica per MySQL.
         *
         * [OK] CurrentDateRenderStrategy: MySQL ha la funzione CURDATE().
         *
         * [OK] CurrentDateTimeRenderStrategy: MySQL usa NOW() o CURRENT_TIMESTAMP() che hanno un
         * comportamento simile ma non identico.
         *
         * [OK] DateArithmeticRenderStrategy: MySQL usa funzioni come DATE_ADD() e DATE_SUB() per
         * l'aritmetica sulle date, a differenza della sintassi DATE '...' + INTERVAL ... dello standard.
         *
         * [OK] ConcatRenderStrategy: MySQL usa la funzione CONCAT() per concatenare le stringhe, mentre SQL
         * standard usa l'operatore ||.
         *
         * CharLengthRenderStrategy: MySQL supporta CHAR_LENGTH().
         *
         * ExceptRenderStrategy: Come discusso in precedenza, MySQL supporta EXCEPT solo dalla versione
         * 8.0.31, mentre prima si doveva usare un approccio basato su LEFT JOIN. Se devi supportare
         * versioni precedenti, questa strategia va sostituita.
         *
         * InsertStatementRenderStrategy: Per supportare l'inserimento di una riga con valori di default, la
         * strategia di rendering deve generare INSERT INTO table () VALUES () o INSERT INTO table DEFAULT
         * VALUES a seconda della versione di MySQL.
         *
         * AsRenderStrategy: Sebbene la sintassi AS sia supportata, per le tabelle MySQL a volte non la
         * richiede. Le virgolette di escape sono \ (backtick), non " (doppi apici). La tua EscapeStrategy
         * deve essere sostituita per usare i backtick.
         *
         * CastRenderStrategy: La funzione CAST in MySQL ha un comportamento leggermente diverso e potrebbe
         * richiedere tipi di dati specifici.
         */
        return SqlRendererImpl.builder()
                .escapeStrategy(EscapeStrategy.mysql())
                .paginationStrategy(PaginationRenderStrategy.mysql())
                .currentDateStrategy(CurrentDateRenderStrategy.mysql())
                .currentDateTimeStrategy(CurrentDateTimeRenderStrategy.mysql())
                .dateArithmeticStrategy(DateArithmeticRenderStrategy.mysql())
                .concatStrategy(ConcatRenderStrategy.mysql())
                .dataLengthStrategy(DataLengthRenderStrategy.mysql())
                .build();
    }

    // TODO: comple oracle configuration
    public static SqlRendererImpl oracle() {
        return SqlRendererImpl.builder()
                .currentDateTimeStrategy(CurrentDateTimeRenderStrategy.oracle())
                .exceptStrategy(ExceptRenderStrategy.oracle())
                .build();
    }
}
