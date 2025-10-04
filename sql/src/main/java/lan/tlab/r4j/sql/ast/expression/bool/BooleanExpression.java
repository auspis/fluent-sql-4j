package lan.tlab.r4j.sql.ast.expression.bool;

import lan.tlab.r4j.sql.ast.expression.Expression;

/**
 * Rappresenta un'espressione che si valuta a un valore booleano (TRUE/FALSE/UNKNOWN). Utilizzata in
 * clausole WHERE, HAVING, ON.
 */
public interface BooleanExpression extends Expression {

    /*
    Un'espressione booleana produce un valore TRUE/FALSE/UNKNOWN.
       ComparisonPredicate: (Es. =, >, <, IS NULL, LIKE, IN, BETWEEN)
           Accetta come componente/parametro:
               Un ComparisonOperator (es. RelationalComparisonOperator, LikeComparisonOperator, InComparisonOperator, BetweenComparisonOperator).
               Uno o più ScalarExpression come operandi, il cui numero e ruolo dipendono dall'operatore specifico:
                   Operatori binari (=, >): 2 ScalarExpression (left e right operand).
                   IS NULL/IS NOT NULL: 1 ScalarExpression.
                   IN: 1 ScalarExpression (valore da confrontare) e un set di valori (che può essere una lista di ScalarExpression o il risultato di una ColumnSubquery).
                   BETWEEN: 1 ScalarExpression (valore da confrontare) e 2 ScalarExpression (lower e upper bound).
           Esempio: price > 100 (price e 100 sono ScalarExpression; > è un RelationalComparisonOperator).
       LogicalPredicate: (Es. AND, OR, NOT)
           Accetta come componente/parametro:
               Un LogicalOperator (AND, OR, NOT).
               Uno o più BooleanExpression come operandi.
                   AND/OR: Due o più BooleanExpression.
                   NOT: Una singola BooleanExpression.
           Esempio: (condition1 AND condition2) (condition1 e condition2 sono BooleanExpression; AND è un ConjunctiveLogicalOperator).
    */

    /*
    - Definizione: Producono TRUE, FALSE, o UNKNOWN.
    - Dove possono essere usate:
       	- SELECT (SÌ/NO - dipende dal DB): Alcuni DB (es. PostgreSQL, MySQL 8.0+) permettono di proiettare direttamente espressioni booleane che restituiscono TRUE/FALSE (o 1/0). Altri no o richiedono un CASE esplicito.
           	Esempio (PostgreSQL): SELECT CustomerName, (Salary > 50000) AS HighEarner FROM Employees;
           	Esempio (Generalmente): SELECT CustomerName, CASE WHEN Salary > 50000 THEN 'Yes' ELSE 'No' END AS HighEarner FROM Employees;
    	- WHERE (SÌ): Il loro ruolo primario. La clausola WHERE si aspetta una singola espressione booleana che determina quali righe includere.
           	Esempio: WHERE OrderDate > '2023-01-01' AND CustomerID = 123
       	- HAVING (SÌ): Il loro ruolo primario. La clausola HAVING si aspetta una singola espressione booleana che determina quali gruppi includere.
           	Esempio: HAVING COUNT(*) > 10 OR SUM(Amount) > 10000
       	- GROUP BY (NO): Non ha senso. Non si raggruppa per un valore booleano di TRUE/FALSE/UNKNOWN che non sia direttamente legato a una colonna.
       	- ORDER BY (SÌ/NO - dipende dal DB): Simile alla SELECT, alcuni DB le accettano direttamente, altri no. Se accettate, ordinano per TRUE/FALSE (spesso mappato a 1/0).
     */

}
