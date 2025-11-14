package lan.tlab.r4j.sql.ast.common.expression.scalar.function;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;

/**
 * Rappresenta una chiamata a una funzione scalare standard in SQL (es. LENGTH(str), CONCAT(s1,
 * s2)). Implementa ScalarExpression.
 */
public interface FunctionCall extends ScalarExpression {

    /*
    Categorie Comuni di FunctionExpression

    1. Funzioni Stringa
    Operano su valori di tipo stringa e restituiscono stringhe o numeri.
    ok  LENGTH(string_expr) / CHAR_LENGTH(string_expr) / CHARACTER_LENGTH(string_expr)
            Descrizione: Restituisce la lunghezza della stringa.
            Parametri: 1 ScalarExpression (tipo stringa).
    ok  LOWER(string_expr) / UPPER(string_expr)
            Descrizione: Converte la stringa in minuscolo/maiuscolo.
            Parametri: 1 ScalarExpression (tipo stringa).
    ok  CONCAT(string_expr1, string_expr2, ...) / CONCAT_WS(separator, string_expr1, string_expr2, ...)
            Descrizione: Concatena due o più stringhe. CONCAT_WS concatena con un separatore.
            Parametri: 2 o più ScalarExpression (tutti di tipo stringa).
    ok  SUBSTRING(string_expr, start_pos, [length]) / SUBSTR(...)
            Descrizione: Estrae una sottostringa.
            Parametri: 1 ScalarExpression (stringa), 1 ScalarExpression (intero per posizione di inizio), 1 ScalarExpression (intero per lunghezza, opzionale).
    ok  LEFT(string_expr, length) / RIGHT(string_expr, length)
            Descrizione: Estrae un numero specifico di caratteri dall'inizio/fine della stringa.
            Parametri: 1 ScalarExpression (stringa), 1 ScalarExpression (intero per lunghezza).
    ok TRIM([ [BOTH | LEADING | TRAILING] [rem_str FROM] ] string_expr)
            Descrizione: Rimuove spazi (o caratteri specifici) dall'inizio, dalla fine o da entrambi i lati di una stringa.
            Parametri: 1 ScalarExpression (stringa), 1 ScalarExpression (stringa per rem_str, opzionale). Le parole chiave BOTH, LEADING, TRAILING sono modificatori della funzione.
    ok    REPLACE(string_expr, from_str, to_str)
            Descrizione: Sostituisce tutte le occorrenze di una sottostringa con un'altra.
            Parametri: 3 ScalarExpression (tutti di tipo stringa).

    2. Funzioni Numeriche
    Operano su valori numerici e restituiscono numeri.
    ok  ABS(numeric_expr)
            Descrizione: Restituisce il valore assoluto.
            Parametri: 1 ScalarExpression (tipo numerico).
    ok  ROUND(numeric_expr, [decimals])
            Descrizione: Arrotonda un numero al numero specificato di decimali.
            Parametri: 1 ScalarExpression (numerico), 1 ScalarExpression (intero per decimali, opzionale).
    ok  CEIL(numeric_expr) / CEILING(numeric_expr)
            Descrizione: Restituisce il più piccolo intero maggiore o uguale al numero dato.
            Parametri: 1 ScalarExpression (numerico).
    ok  FLOOR(numeric_expr)
            Descrizione: Restituisce il più grande intero minore o uguale al numero dato.
            Parametri: 1 ScalarExpression (numerico).
    ok  POWER(base, exponent) / POW(base, exponent)
            Descrizione: Eleva un numero a una potenza.
            Parametri: 2 ScalarExpression (entrambi numerici).
    ok  SQRT(numeric_expr)
            Descrizione: Restituisce la radice quadrata.
            Parametri: 1 ScalarExpression (numerico).
    ok  MOD(numeric_expr1, numeric_expr2) (Già citato come operatore, ma anche funzione)
            Descrizione: Restituisce il resto della divisione.
            Parametri: 2 ScalarExpression (entrambi numerici).

    3. Funzioni Data e Ora
    Operano su valori di tipo data/ora e restituiscono date, ore, o parti di esse.
    ok  NOW() / CURRENT_TIMESTAMP()
            Descrizione: Restituisce la data e l'ora correnti.
            Parametri: Nessuno.
    ok  CURDATE() / CURRENT_DATE()
            Descrizione: Restituisce la data corrente.
            Parametri: Nessuno.
    ok  YEAR(date_expr) / MONTH(date_expr) / DAY(date_expr)
            Descrizione: Estrae l'anno/mese/giorno da una data.
            Parametri: 1 ScalarExpression (tipo data/ora).
    ok  DATE_ADD(date_expr, INTERVAL value unit) / DATE_SUB(date_expr, INTERVAL value unit)
            Descrizione: Aggiunge/sottrae un intervallo a una data.
            Parametri: 1 ScalarExpression (data/ora), INTERVAL (parola chiave), 1 ScalarExpression (numerico per il valore), unit (parola chiave, es. DAY, MONTH, YEAR).
    NON standard    DATEDIFF(date_expr1, date_expr2) (MySQL) / DATE_PART('part', source) (PostgreSQL)
            Descrizione: Calcola la differenza tra date.
            Parametri: 2 ScalarExpression (tipo data/ora).

    4. Funzioni di Conversione
    Convertono un tipo di dato in un altro.
    ok  CAST(expression AS data_type) / CONVERT(data_type, expression)
            Descrizione: Converte il tipo di dato di un'espressione.
            Parametri: 1 ScalarExpression (qualsiasi tipo), data_type (tipo di dato SQL target, es. INT, DECIMAL(10,2), DATE, VARCHAR(255)). data_type non è una ScalarExpression ma una specifica del tipo SQL.
            Esempio: CAST(price AS DECIMAL(10,2))

    5. Funzioni Condizionali (Non CASE Statement)
    Alcuni DBMS hanno funzioni che agiscono come brevi condizionali.
        IF(condition, value_if_true, value_if_false) (MySQL)
            Descrizione: Valuta una condizione e restituisce un valore basato su TRUE/FALSE.
            Parametri: 1 Predicate (per la condizione), 2 ScalarExpression (per i valori di ritorno).
        COALESCE(expr1, expr2, ...)
            Descrizione: Restituisce il primo valore non NULL nella lista.
            Parametri: 2 o più ScalarExpression (di tipo compatibile).
        NULLIF(expr1, expr2)
            Descrizione: Restituisce NULL se expr1 è uguale a expr2, altrimenti restituisce expr1.
            Parametri: 2 ScalarExpression (di tipo compatibile).
     */

}
