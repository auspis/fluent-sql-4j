package lan.tlab.r4j.sql.ast.common.expression.set;

import lan.tlab.r4j.sql.ast.common.expression.Expression;

public interface SetExpression extends Expression {
    /*
     Espressioni di Set (Set Expressions):
        Combinano i risultati di più query.
        Esempi: UNION, UNION ALL, INTERSECT, EXCEPT (MINUS in Oracle).


        Le SetExpression in SQL sono utilizzate per combinare i risultati di due o più istruzioni SELECT in un unico set di risultati. La condizione fondamentale è che le istruzioni SELECT combinate devono avere lo stesso numero di colonne, e le colonne corrispondenti devono avere tipi di dati compatibili (o convertibili).
    	Le principali SetExpression sono:

        UNION:
            Funzione: Combina i set di risultati di due o più istruzioni SELECT e rimuove i duplicati.
            Comportamento: Se una riga appare identica in entrambi i set di risultati (o più volte nello stesso set), viene inclusa solo una volta nel risultato finale. L'operazione di deduplicazione può influire sulle prestazioni per set di dati molto grandi.
            Esempio SQL:
    		    SELECT column1, column2 FROM table_a
    		    UNION
    		    SELECT column1, column2 FROM table_b;

    	UNION ALL:
        	Funzione: Combina i set di risultati di due o più istruzioni SELECT e mantiene tutti i duplicati.
        	Comportamento: È generalmente più performante di UNION perché non esegue la deduplicazione. Se una riga appare in entrambi i set di risultati, verrà inclusa due volte (o più, a seconda di quante volte appare).
        	Esempio SQL:
    		    SELECT id, name FROM employees
    		    UNION ALL
    		    SELECT id, name FROM ex_employees;

    	INTERSECT:
        	Funzione: Restituisce solo le righe che sono presenti in tutti i set di risultati delle istruzioni SELECT combinate.
        	Comportamento: Implica anche una deduplicazione, restituendo ogni riga comune una sola volta.
        	Disponibilità: Non tutti i database supportano INTERSECT. È comune in PostgreSQL, Oracle, SQL Server. MySQL non lo supporta direttamente, ma si può emulare con JOIN.
        	Esempio SQL:
    		    SELECT product_id FROM products_in_store_a
    		    INTERSECT
    		    SELECT product_id FROM products_in_store_b;

    	EXCEPT (o MINUS in Oracle):
        	Funzione: Restituisce le righe che sono presenti nel primo set di risultati, ma non sono presenti in alcuno dei set di risultati successivi.
        	Comportamento: Restituisce le righe uniche del primo set che non hanno corrispondenze negli altri. Implica anche una deduplicazione.
        	Disponibilità: Similmente a INTERSECT, non tutti i database lo supportano. EXCEPT è usato in PostgreSQL, SQL Server. MINUS è l'equivalente in Oracle. MySQL non lo supporta direttamente, ma si può emulare con NOT EXISTS o LEFT JOIN ... WHERE ... IS NULL.
        	Esempio SQL:
    	        SELECT customer_id FROM all_customers
    	        EXCEPT
    	        SELECT customer_id FROM active_customers;

    Implicazioni per il Tuo Modello AST
    Dato che SetExpression è una classe/interfaccia base sotto Expression, le tue classi concrete potrebbero essere:
        UnionExpression (con un flag/tipo per ALL o due classi UnionAllExpression e UnionDistinctExpression)
        IntersectExpression
        ExceptExpression (o MinusExpression se vuoi un supporto specifico per Oracle)
    Ciascuna di queste classi avrebbe al suo interno riferimenti a due o più SelectStatement (o un'altra SetExpression se si vuole permettere il chaining come (SELECT ...) UNION (SELECT ...) INTERSECT (SELECT ...)).
    La tua struttura gerarchica sembra ben predisposta per accogliere queste espressioni!
     */

}
