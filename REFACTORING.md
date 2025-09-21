# Opportunità di Refactoring

## Bug Fix: Parameter Propagation in PreparedStatement Strategies

Durante l'implementazione della strategia per `BinaryArithmeticExpression`, ho scoperto e corretto un bug importante nel sistema di gestione dei parametri per PreparedStatement:

### Problema

Le strategie per `ScalarExpressionProjection` e `SelectClause` non propagavano correttamente i parametri delle espressioni, restituendo sempre una lista vuota.

### Soluzioni Applicate

1. **DefaultScalarExpressionProjectionPsStrategy**: Ora restituisce `exprResult.parameters()` invece di `List.of()`
2. **DefaultSelectClausePsStrategy**: Ora raccoglie e propaga tutti i parametri dalle proiezioni

### Test da Aggiornare

I seguenti test devono essere aggiornati per riflettere il comportamento corretto:
- DefaultScalarExpressionProjectionPsStrategyTest
- DefaultSelectClausePsStrategyTest

### Impatto

Questo fix migliora significativamente l'accuratezza del sistema PreparedStatement, garantendo che tutti i parametri siano correttamente raccolti e passati al database.

