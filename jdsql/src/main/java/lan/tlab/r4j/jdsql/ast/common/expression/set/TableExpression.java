package lan.tlab.r4j.jdsql.ast.common.expression.set;

public interface TableExpression extends SetExpression {

    /*
    Queste espressioni producono un set di righe e colonne.
        DerivedTable (o "Inline View"):
            Accetta come componente/parametro: Un'intera SelectStatement. Questa SelectStatement può produrre qualsiasi numero di colonne e righe.
            Esempio: (SELECT customer_id, SUM(amount) AS total_orders FROM orders GROUP BY customer_id) AS CustomerSummary
        CommonTableExpression (CTE) - nella clausola WITH:
            Accetta come componente/parametro: Un nome per la CTE e un'intera SelectStatement (o una VALUES clause, etc.) che definisce il set di risultati.
            Esempio: WITH SalesByMonth AS (SELECT MONTH(order_date) AS month, SUM(amount) AS monthly_sales FROM orders GROUP BY MONTH(order_date))
     */

    /*
    (TableIdentifier Expressions / Row Sets)
       - Definizione: Producono un set di righe e colonne (una tabella).
    - Dove possono essere usate:
           - SELECT (NO): Non puoi proiettare direttamente un'intera tabella come un "valore" in una colonna (a meno che non sia una subquery scalare, che è un caso specifico).
           - WHERE (NO, ma indirettamente con EXISTS/IN): Non puoi mettere una tabella direttamente in WHERE. Tuttavia, le sottoquery tabulari sono usate con operatori come EXISTS o NOT EXISTS, che sono operatori booleani che testano la presenza di righe nella sottoquery. Le sottoquery di colonna sono usate con IN.
           - HAVING (NO, ma indirettamente con EXISTS/IN): Come per WHERE, le sottoquery tabulari o di colonna possono essere usate in HAVING con EXISTS/NOT EXISTS/IN.
           - FROM (SÌ): Il loro ruolo primario. Le sottoquery che restituiscono tabelle (derived tables, inline views) sono usate pesantemente nella clausola FROM come sorgenti dati per la query principale. Le CTE (WITH) sono un'estensione di questo concetto.
               Esempio: FROM (SELECT customer_id, SUM(amount) AS total_spent FROM orders GROUP BY customer_id) AS customer_summary
           - GROUP BY (NO): Non raggruppi per una tabella.
           - ORDER BY (NO): Non ordini per una tabella.
     */
}
