# SMART TRADE

O SMART TRADE é um mod client-side para singleplayer que adiciona nove trocas configuráveis com aldeões e cinco opções globais. A configuração é aberta pelo Mod Menu e salva em `config/smarttrade.json`. As regras de gameplay são executadas pelo servidor integrado do mundo; instalar o mod somente no cliente não oferece suporte a servidores multiplayer externos.

![Tela de seleção do SMART TRADE](docs/images/smarttrade-selection.png)

## Como a configuração funciona

- Marcar ou desmarcar entradas na interface não altera a configuração imediatamente: é necessário selecionar **APLICAR** na tela correspondente. O botão **ALTERNAR** marca todas as entradas quando pelo menos uma está desmarcada e desmarca todas quando todas estão marcadas.
- Na primeira instalação, quando `config/smarttrade.json` ainda não existe, as nove opções principais de troca começam marcadas e as cinco opções globais — **INFORMAÇÕES ADICIONAIS**, **REPUTAÇÃO MÁXIMA**, **VELOCIDADE CONDICIONADA**, **ALTURA LIMITADA** e **VIDA COMPACTA** — começam desativadas.
- Configurações já salvas são carregadas e preservadas. Ao atualizar versões antigas, o mod migra os campos reconhecidos e descarta somente identificadores de troca que não pertençam à lista atual de nove opções. Se o arquivo não puder ser lido ou contiver JSON inválido, os padrões da primeira instalação são usados durante aquela execução.
- As alterações passam a ser usadas assim que são aplicadas. Fechar uma tela sem selecionar **APLICAR** descarta as mudanças ainda não salvas daquela tela.

## Regras comuns das trocas configuráveis

As quantidades da tabela abaixo são preços-base. Cada oferta:

- recebe somente o item indicado e entrega exatamente **1 esmeralda**;
- pode ser usada **12 vezes** antes de ficar sem estoque;
- adiciona **2 pontos de experiência ao aldeão** por negociação;
- gera para o jogador a experiência vanilla da negociação: **3 a 6 pontos**, ou **8 a 11 pontos** quando a mesma negociação inicia a subida de nível da profissão;
- usa multiplicador de preço **0,05 (5%)** para o cálculo vanilla de demanda. Demanda, reputação e outros modificadores vanilla podem alterar a quantidade efetivamente cobrada; o preço final é limitado pelo jogo entre **1** e o tamanho máximo da pilha do item, que é **64** para os nove itens;
- segue o reabastecimento vanilla do aldeão. Um reabastecimento zera os usos da oferta, e o aldeão pode se reabastecer no máximo **2 vezes por dia**; entre o primeiro e o segundo reabastecimento há um intervalo mínimo de **2.400 ticks (2 minutos a 20 ticks por segundo)**. O contador diário é reiniciado quando passam mais de **12.000 ticks (10 minutos a 20 ticks por segundo)** desde o último reabastecimento ou quando avança o dia da linha do tempo do Overworld. Local de trabalho, horário, acesso e demais requisitos vanilla para o aldeão trabalhar não são alterados.

Quando marcada, a oferta é inserida para qualquer aldeão da profissão correspondente, novo ou já existente e independentemente do nível da profissão. Ela é colocada depois das duas primeiras posições reservadas às ofertas vanilla de novato, sem substituir ofertas vanilla nem participar dos sorteios ou das probabilidades que as geram.

Quando desmarcada, a oferta deixa de ser adicionada aos aldeões que ainda não a possuem. Uma oferta da versão atual que já tenha sido gravada em um aldeão não é removida ao desmarcar a opção; ela continua disponível nesse aldeão com seus usos, demanda e demais dados preservados.

## Trocas disponíveis

| Opção | Comportamento exato |
| --- | --- |
| **OVOS** | O agricultor compra **20 ovos** pelo preço-base de **1 esmeralda**. |
| **SEMENTES DE CACAU** | O agricultor compra **20 sementes de cacau** pelo preço-base de **1 esmeralda**. |
| **FAVOS DE MEL** | O agricultor compra **10 favos de mel** pelo preço-base de **1 esmeralda**. |
| **OLHOS DE ARANHA** | O clérigo compra **15 olhos de aranha** pelo preço-base de **1 esmeralda**. |
| **PÉROLAS DO ENDER** | O clérigo compra **3 pérolas do Ender** pelo preço-base de **1 esmeralda**. |
| **PÓS DE REDSTONE** | O clérigo compra **20 pós de redstone** pelo preço-base de **1 esmeralda**. |
| **LÁPIS-LAZÚLI** | O clérigo compra **20 lápis-lazúli** pelo preço-base de **1 esmeralda**. |
| **OSSOS** | O açougueiro compra **20 ossos** pelo preço-base de **1 esmeralda**. |
| **FLECHAS** | O flecheiro compra **15 flechas** pelo preço-base de **1 esmeralda**. |

Ao carregar as ofertas de um aldeão, o mod também remove variantes antigas dessas mesmas trocas e, se a opção atual estiver marcada, insere a versão atual. São reconhecidos como preços-base legados: ovos **16**; sementes de cacau **16 ou 32**; favos de mel **8 ou 12**; olhos de aranha **12 ou 24**; pérolas do Ender **2 ou 4**; pós de redstone **16 ou 24**; lápis-lazúli **16 ou 24**; ossos **16 ou 32**; e flechas **12**. Também são removidas antigas ofertas do agricultor de **16 ou 32 canas-de-açúcar por 1 esmeralda**. Essa migração só reconhece ofertas com **12 usos máximos**, **2 XP para o aldeão**, sem segundo item de custo e com resultado de **1 esmeralda**, evitando remover trocas diferentes que apenas usem o mesmo item.

## Opções globais

### INFORMAÇÕES ADICIONAIS

Exibe no Jade três dados do aldeão observado: reputação em relação ao jogador, em verde e com sinal + acima de 0, em vermelho abaixo de 0 e em cinza quando igual a 0; reabastecimentos realizados no dia, no formato atual/2; e Curado como Sim quando a reputação major_positive desse jogador é maior que 0. Com REPUTAÇÃO MÁXIMA, exibe +150, enquanto Curado usa o dado real. Requer Jade.

### REPUTAÇÃO MÁXIMA

Faz toda consulta de reputação de qualquer aldeão retornar 150 para o jogador, afetando os preços especiais de ofertas vanilla e do SMART TRADE. Nas nove ofertas do mod, o multiplicador 0,05 reduz 7 itens antes da demanda e de outros modificadores, respeitando o preço final mínimo de 1 item. Ofertas vanilla usam seus próprios multiplicadores. Os dados reais de reputação permanecem preservados.

### VELOCIDADE CONDICIONADA

Restringe os efeitos de Velocidade das Almas ao Nether. Nas outras dimensões, entidades vivas com qualquer nível do encantamento recebem fator de velocidade 1,0 sobre blocos da tag minecraft:soul_speed_blocks. A verificação considera o bloco na posição da entidade e o bloco inferior. No conteúdo vanilla, a tag contém exatamente areia das almas e terra das almas; datapacks e mods podem ampliá-la.

### ALTURA LIMITADA

Padroniza crescimentos com pó de osso: cogumelos vermelho/marrom sobre nicélios carmesim/distorcido usam caules de 5/4 blocos e ocupam até 6/5 camadas com a copa; mudas da selva e acácia usam tronco de 6 blocos; fungos carmesim/distorcido no nicélio correspondente usam caule de 6 blocos em 11/12 dos casos ou 12 blocos em 1/12, ocupando até 7/13 camadas. Preserva as chances: 40% para cogumelos e fungos e 45% para mudas, aplicando a altura quando a árvore é gerada, além dos requisitos de suporte, espaço, altura e substituição.

### VIDA COMPACTA

Limita a HUD de vida de cavalos, burros, mulas, cavalos-zumbis e cavalos-esqueletos a uma linha de 10 corações, preenchidos proporcionalmente à vida máxima da montaria. Altera somente a exibição; a vida real, o dano e a cura permanecem inalterados.
