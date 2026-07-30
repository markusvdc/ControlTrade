# SMART TRADE — Instruções do ambiente de desenvolvimento

Leia este arquivo antes de alterar, compilar ou testar o projeto.

## Ambiente

- Sistema: Windows com PowerShell.
- Minecraft: `26.2`.
- Fabric Loader: `0.19.3`.
- Fabric API: `0.155.0+26.2`.
- Mod Menu: `20.0.1`.
- Java/JDK: `25.0.3 LTS` (Temurin, build `25.0.3+9`).
- JDK usado no desenvolvimento:
  `D:\MARKUS\GAMES\minecraft\java\jdk-25.0.3`

## Build

Antes de executar qualquer operação potencialmente demorada, como build, varredura ampla ou outra tarefa pesada, informe em uma frase por que ela é necessária. Se não houver necessidade técnica, não execute a operação.

Execute o build com o JDK 25 configurado:

```powershell
$env:JAVA_HOME='D:\MARKUS\GAMES\minecraft\java\jdk-25.0.3'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat clean build --warning-mode all
```

Quando a alteração afetar código, recursos do mod, configuração de build ou o conteúdo do JAR, considere o trabalho concluído somente depois de um build bem-sucedido.

### Exceção para documentação

- Não execute build quando todas as alterações forem exclusivamente de documentação.
- Exemplos: `AGENTS.md`, `README.md` e outros arquivos informativos que não entram no JAR nem afetam o funcionamento do mod.
- Alterações somente de documentação também não exigem instalação automática na instância de teste.
- Se houver qualquer mudança de código, recurso, dependência ou configuração junto com a documentação, execute normalmente o build e a instalação automática.

## Instalação automática nas instâncias de teste

Depois de cada build bem-sucedido:

1. Faça o deploy nas duas instâncias abaixo:
   - `D:\MARKUS\GAMES\minecraft\instances\NEBULOSA6\minecraft\mods`
   - `D:\MARKUS\GAMES\minecraft\instances\NEBULOSA7\minecraft\mods`
2. Resolva e valide que cada destino corresponde exatamente a uma dessas pastas autorizadas.
3. Localize em cada pasta somente arquivos que correspondam a:
   `smarttrade-*.jar` ou ao padrão legado `smart-trade-*.jar`
4. Remova somente esses JARs antigos do SMART TRADE.
5. Copie o novo JAR de `build\libs` para as duas pastas de mods.
6. Confirme em cada destino que existe exatamente um `smarttrade-*.jar`, nenhum JAR legado `smart-trade-*.jar` e que ele corresponde à versão recém-compilada.

Nunca remova, mova ou substitua outros mods dessas pastas.

## Clareza e precisão do README

Ao criar ou alterar opções e funcionalidades destinadas ao jogador, revise também o `README.md`, desde que a solicitação autorize explicitamente a edição da documentação pública.

- Confira o comportamento real no código antes de editar a documentação e compare as descrições com constantes, listas, condições e valores implementados.
- Descreva todas as opções e funcionalidades destinadas ao jogador de forma completa, clara e explicativa.
- Informe todos os valores fixos relevantes, incluindo quantidades, multiplicadores, porcentagens, distâncias, alcances, durações, intervalos, limites, chances e tempos. Essa precisão é especialmente importante quando o mod não permite que o jogador altere esses valores.
- Não use expressões vagas como “mais rápido”, “por algum tempo”, “itens compatíveis” ou nomes genéricos de categorias quando existir um valor, prazo, lista ou critério exato no código.
- Para cada opção, explique o que faz, o que afeta e os valores, condições, exceções ou comportamentos especiais diretamente necessários para compreender seu funcionamento.
- Quando existir uma lista fechada de itens, blocos, entidades ou categorias, enumere seus integrantes ou defina precisamente a categoria usada.
- Informe os valores e estados padrão da primeira instalação e esclareça que configurações existentes são preservadas quando esse for o comportamento implementado.
- Não reduza a precisão para manter descrições curtas. Priorize clareza e exatidão para que o jogador entenda o comportamento oferecido.
- Ao adicionar ou corrigir uma opção, revise também as demais descrições do README e complete valores, limites, escopos ou exceções ausentes.
- Não invente comportamentos. Toda afirmação deve corresponder ao código atual.

## Sincronização dos tooltips globais

- O tooltip de cada opção global e a descrição correspondente no `README.md` devem permanecer sincronizados e apresentar exatamente o mesmo conteúdo informativo.
- No idioma principal `pt_br`, mantenha a redação do tooltip literalmente igual à descrição do README, desconsiderando somente formatações exclusivas do Markdown.
- Toda mudança de regra, valor, limite, escopo, condição ou exceção de uma opção global exige, no mesmo conjunto de alterações, a atualização da descrição no README, do tooltip dentro do jogo e de todas as traduções desse tooltip.
- Documente nos dois locais todos os valores fixos, multiplicadores, quantidades, distâncias, limites, tempos, condições de início e término, exceções, exclusões, elementos afetados e comportamentos que permanecem inalterados.
- As traduções dos tooltips globais devem permanecer completas e semanticamente equivalentes ao texto principal em `pt_br`.
- A largura máxima de quebra de linha dos tooltips globais deve permanecer em exatamente `425` pixels, equivalente a `2,5×` a largura vanilla de `170` pixels.
- A distância vertical entre as linhas dos tooltips globais deve permanecer uniforme e `2` pixels maior que a altura vanilla, aproveitando o espaçamento especial vanilla após a primeira linha sem duplicá-lo.
- Essas alterações de largura e espaçamento pertencem somente aos tooltips das opções globais. Tooltips de itens, botões e outras interfaces devem conservar o comportamento vanilla.
- Confira o código real antes de redigir ou atualizar os textos. Não invente comportamentos nem use redação vaga quando existir um critério exato implementado.

## Concisão das descrições e tooltips

- Descrições e tooltips devem ser sucintos, claros e restritos ao funcionamento da opção.
- Informe somente o que a opção faz, quais elementos afeta, valores fixos relevantes, condições necessárias para compreender seu funcionamento e comportamentos especiais diretamente produzidos por ela.
- Não transforme descrições ou tooltips em tutoriais de Minecraft. Exclua conhecimentos gerais do jogo, explicações de mecânicas vanilla e dúvidas que possam ser resolvidas pela wiki.
- Prefira afirmações positivas que definam diretamente o escopo. Não prolongue o texto enumerando tudo que a opção não faz, não altera ou não afeta.
- Não use “caso ativada”, “quando estiver ativa”, “ao ativar esta opção” ou expressões equivalentes. O contexto da interface já comunica que o comportamento depende da opção.
- Preserve sempre multiplicadores, quantidades, distâncias, limites, tempos e demais números relevantes que o jogador não possa configurar.
- Quando uma descrição ou tooltip ultrapassar `450` caracteres sem contar espaços, faça uma revisão obrigatória. O valor é um gatilho de revisão, não um limite rígido.
- Durante essa revisão, remova explicações gerais de Minecraft, redundâncias, frases negativas desnecessárias, exemplos que não definam a opção e detalhes técnicos internos sem impacto para o jogador.
- Listas fechadas podem permanecer quando forem indispensáveis para definir exatamente o escopo da opção.

## Controle de versão

- Agrupe implementação, refinamentos, correções e acabamentos da mesma entrega em um único commit.
- Considere uma entrega estrutural pronta somente depois de finalizada e validada conforme as regras do projeto.
- Antes de iniciar uma nova implementação estrutural de outra natureza, faça o commit da entrega anterior já concluída.
- Uma simples mudança de assunto ou arquivo não cria, por si só, uma fronteira entre commits; avalie intenção, tamanho e caráter estrutural.
- Pequenos ajustes feitos depois de um commit podem permanecer acumulados até formarem um conjunto coerente, antecederem uma nova entrega estrutural ou o usuário solicitar um commit.
- Não misture uma nova entrega estrutural com outra já concluída e ainda não registrada.
- Escreva as mensagens de commit sempre em inglês.
- Mantenha as mensagens com aproximadamente 45 caracteres. Esse valor é uma referência de tamanho, não um limite máximo; priorize mensagens descritivas com comprimentos visualmente semelhantes.
- Fazer um commit não autoriza um push.
- Execute push somente quando o usuário solicitar explicitamente.

### Releases no GitHub

- Crie toda Release inicialmente como rascunho e publique somente após autorização explícita do usuário.
- Use a tag `v<versão>` e anexe o JAR `smarttrade-<versão>.jar`.
- Atualize no texto as versões de Minecraft, Fabric Loader, Fabric API e Mod Menu correspondentes à Release.

#### Título

- Escreva em inglês no formato `<emoji> <título temático>`.
- Represente a identidade ou a principal mudança da versão em aproximadamente 30 caracteres.
- Não inclua `SMART TRADE` nem o número da versão; essas informações já aparecem no repositório e na tag.
- Referência: `👽 Bringing Variety to Life` — 27 caracteres.

#### Descrição

- Escreva em inglês, como uma única estrofe com aproximadamente 440 caracteres e sem negrito.
- Depois da estrofe, insira uma linha horizontal e somente as duas linhas técnicas do modelo abaixo.
- Referência: a descrição da Release `v1.3.0` possui 435 caracteres.

```markdown
<descrição da versão em uma única estrofe>

---

**Compatibility:** Minecraft 26.2 / [Fabric Loader 0.19.3+](https://fabricmc.net/use/installer/)
**Dependencies:** [Fabric API 0.155.0+26.2](https://modrinth.com/mod/fabric-api) / [Mod Menu 20.0.1+](https://modrinth.com/mod/modmenu)
```

## Validação

- Nunca abra o Minecraft ou qualquer instância para validar alterações.
- Nunca execute `runClient` ou outra tarefa que inicialize o jogo.
- Não use automação de interface para abrir ou controlar o Minecraft.
- Valide alterações somente por build, revisão estática do código e inspeção dos arquivos gerados.
- Quando uma alteração de interface precisar de confirmação dentro do jogo, informe o que deve ser observado e deixe o teste manual para o usuário.
- Continue projetando a interface para fullscreen, GUI Scale `2x`, identidade visual vanilla e ausência de cortes ou sobreposições, mas sem iniciar o jogo para conferir.

## Escopo atual

- Mod client-side focado em singleplayer.
- A configuração é acessada pelo Mod Menu.
- As alterações de comércio e demais regras de gameplay dependem do servidor integrado; não prometa suporte em servidores multiplayer externos apenas com instalação no cliente.
- A integração com Jade é opcional e exibe informações adicionais dos aldeões quando o mod estiver instalado.

## Padrões da primeira instalação

- Todas as opções principais, correspondentes às trocas disponíveis, devem iniciar ativadas.
- Todas as opções globais, incluindo informações adicionais e opções que alteram gameplay, devem iniciar desativadas.
- Esses padrões se aplicam somente quando ainda não existe um arquivo de configuração; configurações já salvas pelo jogador devem ser preservadas.
