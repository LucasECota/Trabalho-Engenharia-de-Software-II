# RU Digital — esqueleto em Java para a ATV de CSI410

Protótipo **didático** do RU Digital: só as partes que importam para a
atividade de arquitetura, princípios e padrões de projeto. Nada aqui fala com
rede, banco ou hardware de verdade — tudo é simulado.

Requer **JDK 17 ou mais novo** (teste com `javac -version`). Não usa Maven,
Gradle nem JUnit: não há nada para instalar além do JDK.

## Como rodar (na pasta onde está este arquivo)

| Windows | Linux / macOS | O que faz |
|---|---|---|
| `rodar testes` | `./rodar.sh testes` | compila e roda todos os testes |
| `rodar testes pedido1` | `./rodar.sh testes pedido1` | só uma suíte |
| `rodar simular --sem-rede` | `./rodar.sh simular --sem-rede` | um almoço com o enlace caído |
| `rodar grafo ru.creditos` | `./rodar.sh grafo ru.creditos` | dependências reais de um pacote |
| `rodar diff --marcar` / `rodar diff --desde-marco` | idem com `./rodar.sh` | o que mudou desde um marco |

No **PowerShell** (prompt começando com `PS`, padrão no Windows 10/11 e no VS Code),
escrevam `.\rodar testes` — o PowerShell não executa scripts da pasta atual sem o `.\`.
No Prompt de Comando (`cmd`), `rodar testes` funciona como está.

O script recompila tudo a cada execução — não é preciso compilar à parte. Se
usar uma IDE (IntelliJ, Eclipse, VS Code), marque `src`, `test` e
`ferramentas` como pastas de código-fonte; mas **confira sempre pelo script**,
que é como o professor vai corrigir.

## O que está verde e o que está vermelho ao receber

| Suíte | Estado | O que significa |
|---|---|---|
| `funcionais` | verde | comportamento que nenhuma refatoração pode quebrar |
| `fronteira` | verde | as regras de fronteira da catraca (ADR-001) |
| `pedido1` | **vermelho** | aceitação do pedido 1 — trocar o gateway (Adaptador + Fábrica) |
| `pedido2` | **vermelho** | aceitação do pedido 2 — nova regra de entrada (Strategy) |
| `pedido3` | **vermelho** | aceitação do pedido 3 — bolsista titular, uma vez por refeição (Decorador) |
| `pedido4` | **vermelho** | aceitação do pedido 4 — avisar sobre o cardápio (Observador) |

## Regras

- **Não editem** nada em `test/` exceto `TestesFronteira.java` (lá vocês *acrescentam* regras),
  nada em `src/ru/gateways/`, `src/ru/app/`, `src/ru/rede/` nem em `ferramentas/`.
- **Não mudem a assinatura** dos métodos de montagem (`Montagem.criarCatraca`,
  `MontagemCreditos.criarServicoCompra`, `MontagemCardapio.criar`): é por eles que os testes entram.
  O *conteúdo* desses métodos é de vocês.
- Podem criar quantas classes, interfaces e pacotes quiserem dentro de `src/ru/`.

## Mapa do código × diagrama de contêineres

| Pacote | Contêiner do C4 |
|---|---|
| `ru.catraca` | App da Catraca + Réplica local + Fila + Sincronizador (borda) |
| `ru.creditos` | API de Créditos (nuvem) |
| `ru.cardapio`, `ru.avisos` | Painel Web / App Móvel — publicação do cardápio e avisos |
| `ru.app` | App Móvel — só a geração do QR code |
| `ru.gateways` | SDKs de terceiros (Gateway de pagamento, sistema externo) |
| `ru.rede` | o enlace entre borda e nuvem, simulado |

## Documentação de arquitetura (vocês completam)

- `docs/decisoes/` — registros de decisão. O ADR-001 é o exemplo resolvido; o `_MODELO.md` é o molde.
- `docs/c4-catraca.md`, `docs/c4-creditos.md`, `docs/c4-cardapio.md` — nível 3 em Mermaid.
- `docs/SEGREDOS.md` — o segredo de cada classe (a catraca vem preenchida).

Para ver os diagramas Mermaid: GitHub, VS Code (extensão *Markdown Preview
Mermaid Support*) ou cole o bloco em https://mermaid.live.
