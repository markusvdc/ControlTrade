# Migração para Minecraft 26.3 — ControlTrade

Data: 2026-09-18. Build final concluído com Temurin 25.0.3+9 e Gradle 9.6.0.

Dependências: Loader 0.19.5, Fabric API 0.160.7+26.3, Mod Menu 21.0.0-beta.1 e Jade 26.3.1+fabric (opcional). Loom 1.17.14 e versão 1.0 preservados.

Adaptações: entrada SDL pela API vanilla; constantes de mouse; nova assinatura de tooltips; setter da invulnerabilidade; swingForAttack; retorno MoveResult do relógio; parâmetro BonemealSource; mixin de cogumelos adequado à nova interface. Controles de dificuldade migrados para WorldOptionsScreen.DifficultyButtons com access widener limitado à classe e ao construtor, validado pelo Gradle. Menu compacto agora abre as opções de mundo pelo botão MUNDO/WORLD, substituto do antigo LAN, com tooltip e README sincronizados; construtor de OptionsScreen e detecção de mundo aberto atualizados.

Validação: clean build --warning-mode all e validateAccessWidener; revisão estática das assinaturas, campos e referências dos mixins, incluindo menus e renderização. Não houve inicialização do Minecraft nem aplicação dos mixins em execução.

Artefato: build/libs/ControlTrade-1.0.jar. Instalação adiada até a atualização da NEBULOSA. Nenhum commit, push ou release. AGENTS.md preservado, com versões compartilhadas ainda da 26.2.

Teste manual pendente: menus compactos e opções de mundo/dificuldade, telas em GUI Scale 2x; trocas e reposição ao amanhecer; sono de dia e noite; crescimento fixo de árvores/cogumelos/fungos; dano montado, ondas de invasão, atalhos e cores das orbes.

Fontes: https://www.fabricmc.net/2026/09/15/263.html ; https://feedback.minecraft.net/hc/en-us/articles/48913133328013-Minecraft-Java-Edition-26-3 ; APIs Fabric/Modrinth e classes/dados 26.3 resolvidos pelo Loom.

Auditoria estática adicional do JAR: 104 verificações de seletores, parâmetros de callbacks, campos, instruções e constantes; nenhuma divergência nos pontos verificados. Integrações opcionais externas excluídas desta auditoria. Todos os JSONs empacotados passaram na verificação de sintaxe.

Logs desta etapa: build/reports/migration-26.3/build.log e static-audit.txt. JSONs empacotados verificados sintaticamente.
