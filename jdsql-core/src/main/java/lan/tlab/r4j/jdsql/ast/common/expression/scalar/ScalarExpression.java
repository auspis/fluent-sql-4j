package lan.tlab.r4j.jdsql.ast.common.expression.scalar;

import lan.tlab.r4j.jdsql.ast.common.expression.Expression;

/**
 * Rappresenta un'espressione che si valuta a un singolo valore scalare (es. numero, stringa, data,
 * booleano, riferimento a colonna, risultato di funzione scalare).
 */
public interface ScalarExpression extends Expression {
    /*
     Un'espressione scalare è un costrutto che produce un singolo valore. Ciò che accetta come "parametro" dipende dal suo sottotipo specifico.
        ColumnReference:
            Accetta come componente/parametro: Nessuno in sé. Rappresenta solo un riferimento a una colonna esistente (nome della tabella, nome della colonna).
            Esempio: CustomerName
        Literal:
            Accetta come componente/parametro: Nessuno in sé. Rappresenta solo un valore costante (stringa, numero, booleano, data).
            Esempio: 'John Doe', 123, TRUE
        ArithmeticExpression:
            Accetta come componente/parametro:
                Operandi (1 o 2): Altre ScalarExpression (es. ColumnReference, Literal, risultato di altre ArithmeticExpression o ScalarFunctionCall).
                Operatore (1): Un ArithmeticOperator (+, -, *, /, % / MOD, negazione unaria -).
            Esempio: price * quantity (Operandi: price (ScalarExpression), quantity (ScalarExpression); Operatore: * (ArithmeticOperator))
    ===> ScalarFunctionCall: (Es. UPPER(), LENGTH(), ROUND(), YEAR())
            Accetta come argomento/parametro: Uno o più ScalarExpression. Il tipo di ScalarExpression accettato dipende dalla funzione specifica (es. UPPER() accetta una stringa, ROUND() un numero, YEAR() una data).
            Esempio: UPPER(CustomerName) (Argomento: CustomerName (ScalarExpression)); ROUND(Price, 2) (Argomenti: Price (ScalarExpression), 2 (Literal/ScalarExpression))
        CaseExpression:
            Accetta come componente/parametro:
                Una sequenza di clausole WHEN...THEN...: Ogni WHEN accetta una Predicate. Ogni THEN accetta una ScalarExpression (il valore da restituire).
                Una clausola ELSE opzionale: Accetta una ScalarExpression (il valore predefinito).
            Esempio: CASE WHEN Age >= 18 THEN 'Adult' ELSE 'Minor' END
        ScalarSubquery:
            Accetta come componente/parametro: Un'intera SelectStatement (che internamente avrà tutte le sue clausole: SELECT, FROM, WHERE, ecc.). La SelectStatement deve essere costruita per restituire esattamente una colonna e una riga.
            Esempio: (SELECT AVG(Salary) FROM Employees)
     */

    /*
    - Definizione: Producono un singolo valore per ogni riga (o per ogni valutazione).
    - Dove possono essere usate:
    	- SELECT (SÌ): Molto comuni. Puoi selezionare direttamente colonne, letterali, risultati di funzioni scalari, espressioni aritmetiche, CASE statement.
    		Esempio: SELECT CustomerName, Price * Quantity, UPPER(City), (SELECT COUNT(*) FROM Orders) FROM ...
    	- WHERE (SÌ): Usate come operandi negli operatori di confronto e logici. WHERE opera riga per riga, quindi le espressioni scalari sono perfette.
       		Esempio: WHERE Salary > 50000 AND LENGTH(FirstName) > 5
    	- HAVING (SÌ): Usate come operandi per gli operatori di confronto e logici, ma solo se sono riferimenti a colonne presenti nel GROUP BY o se sono argomenti dentro una funzione di aggregazione.
       		Esempio: HAVING AVG(Salary * 1.10) > 60000 (qui Salary * 1.10 è scalare, ma è all'interno di AVG)
    	- GROUP BY (SÌ): Le colonne o espressioni su cui raggruppi devono essere espressioni scalari.
       		Esempio: GROUP BY City, YEAR(OrderDate)
    	- ORDER BY (SÌ): Le colonne o espressioni su cui ordini devono essere espressioni scalari.
       		Esempio: ORDER BY LastName, LENGTH(FirstName) DESC
     */

    // TODO: complete scalar expression support
    /*
      [OK]   column_name (riferimento a colonna)
      [OK]   123, 'text' (letterali)
      [OK]   price * quantity (aritmetica)
      [TODO] UPPER(name) (funzione scalare)
      [TODO] CASE WHEN ... THEN ... END (condizionale)
      [OK]   (SELECT COUNT(*) FROM another_table) (sottoquery scalare)
    */

}
