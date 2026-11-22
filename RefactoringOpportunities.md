# Oportunidades de refabricação

Identificadas com o Inspect Code e o Qodana do IntelliJ (Ficha 3) e por revisão do código.

| Local | Cheiro no código | Refabricação | Nº aluno |
|---|---|---|---|
| `Tasks::menu` | Duplicated Code (fim de jogo repetido nos comandos `rajada` e `simula`) | Extract Method | 87907 |
| `GameReportPdf::export` | Magic Number (tamanhos de letra 16, 13 e 9) | Introduce Constant | 87907 |
| `Game::getRemainingShips` | Variável temporária desnecessária | Inline Variable | 87907 |
| `Game::printBoard`, `IGame::printMyBoard`, `IGame::printAlienBoard` | Nomes de parâmetros fora da convenção do Java (`show_shots`, `show_legend`) | Rename | 87907 |
| `Fleet::addShip` | Condição complexa difícil de ler | Introduce Variable | 87907 |
| `IMove` | Declaração redundante do `toString()` (já herdado de `Object`) | Safe Delete | 87907 |
