package lan.tlab.r4j.jdsql.ast.core.expression.aggregate;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarExpression;

/**
 * Rappresenta una funzione di aggregazione (es. COUNT, SUM, AVG, MAX, MIN). Sebbene restituisca un
 * valore scalare, ha una semantica di gruppo e restrizioni d'uso.
 */
public interface AggregateCall extends ScalarExpression {

    /*
    Un'espressione di aggregazione produce un singolo valore riepilogativo per un gruppo di righe.
        AggregateFunctionCall: (Es. SUM(), AVG(), COUNT(), MAX(), MIN())
            Accetta come argomento/parametro:
                Un AggregateFunction (che rappresenta il nome della funzione es. SUM, AVG).
                Un ScalarExpression come argomento (per SUM, AVG, MAX, MIN, COUNT(colonna)).
                Un jolly * (per COUNT(*)).
                Opzionalmente, la parola chiave DISTINCT per specificare l'aggregazione su valori unici.
            Esempio: AVG(Salary * 1.10) (Argomento: Salary * 1.10 (ScalarExpression); Funzione: AVG)
     */

    /*
    - Definizione: Producono un singolo valore riepilogativo per un gruppo.
    - Dove possono essere usate:
       	- SELECT (SÌ): Tipicamente usate qui per visualizzare i risultati aggregati.
           	Esempio: SELECT Department, AVG(Salary) FROM Employees GROUP BY Department;
       	- WHERE (ASSOLUTAMENTE NO): Regola cruciale. Le funzioni di aggregazione non possono mai essere usate nella clausola WHERE. Questo perché WHERE opera sulle singole righe prima che il raggruppamento (GROUP BY) e l'aggregazione avvengano. Non esiste ancora un "gruppo" su cui calcolare SUM() o AVG().
           	Sbagliato: SELECT * FROM Orders WHERE SUM(Amount) > 1000;
       	- HAVING (SÌ): Il loro ruolo primario per il filtro. HAVING opera sui gruppi dopo che l'aggregazione è avvenuta, quindi è il luogo perfetto per filtrare in base ai risultati delle aggregazioni.
           	Esempio: HAVING SUM(Amount) > 1000;
       	- GROUP BY (NO): Non ha senso raggruppare per il risultato di un'aggregazione. Si raggruppa per colonne o espressioni scalari.
       	- ORDER BY (SÌ): Puoi ordinare il set di risultati finale in base a un valore aggregato.
           	Esempio: ORDER BY AVG(Salary) DESC;
     */
    static AggregateCall max(ScalarExpression expression) {
        return new AggregateCallImpl(AggregateOperator.MAX, expression);
    }

    static AggregateCall min(ScalarExpression expression) {
        return new AggregateCallImpl(AggregateOperator.MIN, expression);
    }

    static AggregateCall avg(ScalarExpression expression) {
        return new AggregateCallImpl(AggregateOperator.AVG, expression);
    }

    static AggregateCall sum(ScalarExpression expression) {
        return new AggregateCallImpl(AggregateOperator.SUM, expression);
    }

    static AggregateCall count(ScalarExpression expression) {
        return new AggregateCallImpl(AggregateOperator.COUNT, expression);
    }

    static AggregateCall countStar() {
        return new CountStar();
    }

    static AggregateCall countDistinct(ScalarExpression expression) {
        return new CountDistinct(expression);
    }
}
