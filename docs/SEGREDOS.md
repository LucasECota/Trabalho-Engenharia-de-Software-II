# Segredos das classes

Para cada classe: **que decisão ela esconde** (e que, por isso, pode mudar sem
que as outras percebam) e **que mudança ela absorve**. Segredo não é etapa do
fluxo: "recebe o pedido" não é segredo; "o formato em que o pedido chega" é.

## ru.catraca (exemplo resolvido)

| Classe | Segredo | Mudança que ela absorve |
|---|---|---|
| `Leitor` | protocolo do hardware do leitor | fabricante novo, outro formato de leitura |
| `Replica` | onde e como os dados locais são guardados | mapa em memória → SQLite |
| `FilaPassagens` | o formato da fila em disco | memória → arquivo, compressão |
| `Sincronizador` | o protocolo com o Serviço de Acesso | HTTPS → MQTT, lote × item a item |
| `IdentificadorPorCarteirinha` | como se prova quem está na catraca | hoje: número da carteirinha (fraco) |
| `Montagem` | quais implementações concretas são usadas | trocar uma peça por outra |
| `Catraca` | **deveria ser nenhum** — só orquestrar | hoje ainda guarda a regra de entrada (pedido 2) |

## ru.creditos (pedido 1)

| Classe | Segredo | Mudança que ela absorve |
|---|---|---|
| `ServicoCompra` | a regra de crédito: só credita e audita depois de uma cobrança aprovada | política de crédito (bônus, limites) sem tocar em fornecedor |
| `Saldos` | onde os saldos ficam (aqui, memória) | memória → banco de dados |
| `Auditoria` | onde e em que formato a trilha é gravada | memória → arquivo/tabela imutável |
| `MontagemCreditos` | qual gateway é o padrão e como o serviço é montado | troca de fornecedor (`GATEWAY_PADRAO`, uma linha) |
| `Pagamento` / `ResultadoPagamento` | o que a regra de crédito precisa saber de uma cobrança (aprovada, transação, motivo), sem vocabulário de fornecedor | nenhuma: é o contrato estável entre regra e fornecedores |
| `PagaPixAdapter` | a língua da PagaPix: centavos, `createCharge`, `Status`, `chargeId` | nova versão da API da PagaPix |
| `CobraFacilAdapter` | a língua da CobraFácil: reais, `RetornoCobranca`, exceção verificada | nova versão da API da CobraFácil |
| `FabricaPagamento` | quais adaptadores existem e como se escolhe um pelo nome | fornecedor novo = uma linha na fábrica |

## ru.catraca depois dos pedidos 2 e 3

| Classe | Segredo | Mudança que ela absorve |
|---|---|---|
| `Catraca` | nenhum além da ordem das etapas (só orquestra) | nenhuma regra nova a reabre |
| `RegraEntrada` | o que a Catraca precisa saber de uma regra: pode entrar pagando esta tarifa? | contrato estável para novas regras |
| `SaldoSuficiente` | critério "saldo ≥ tarifa" | o conselho redefinir o critério básico |
| `ToleraSaldoNegativo` | critério "saldo após a tarifa ≥ −limite" e o valor do limite | novo limite de tolerância |
| `UmaPorRefeicao` | como se lembra quem (bolsista) já passou em cada refeição | memória local → persistência/sincronizada entre catracas |
| `IdentificadorComTitularidade` | como se prova que quem passa é o titular: QR assinado (HMAC), validade de 30 s | QR → foto/biometria; outra janela de validade |
| `Montagem` | tradução da configuração em objetos e a ordem de empilhar os decoradores | regra nova ou camada nova por uma linha |

## ru.cardapio depois do pedido 4

| Classe | Segredo | Mudança que ela absorve |
|---|---|---|
| `Cardapio` | a semana atual e a lista de assinantes (e a política de falha: um canal não derruba os outros) | canal novo ou retirado sem editar o Cardapio |
| `ObservadorCardapio` | o que um interessado precisa receber: a semana publicada | contrato estável para novos canais |
| `MontagemCardapio` | quais canais existem e quem assina | bot do DCE = uma linha |
