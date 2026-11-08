# Territory Predictive Insights — Chuva e Alagamento

Global Solution 2/2026 · Programação Orientada a Objetos · FIAP 

| Integrante | RM |
|---|---|
| Lucas M. Moraes | RM563667 |
| Antonio Lucas | RM565516 |

Sistema de console em Java que monitora um ponto vulnerável a alagamento (um trecho de córrego urbano), grava todas as leituras de sensores em banco de dados, **prevê** o comportamento das próximas leituras com duas estratégias diferentes e emite alertas (ATENÇÃO / ALERTA / CRÍTICO) explicando **qual sensor**, **qual valor previsto** e **qual limite foi ultrapassado**.

---

## 1. Problema e público beneficiado

**Quem sofre com o problema?** moradores de áreas baixas próximas a córregos urbanos, onde chuvas fortes de poucas horas fazem o córrego transbordar, alagar ruas e invadir casas. Quem mora ali costuma ter pouco tempo de reação: quando a água já está na rua, é tarde para tirar móveis, carros e pessoas com mobilidade reduzida.

**Quem usa o sistema?** a equipe local de Defesa Civil (ou a coordenação de um núcleo comunitário de proteção e defesa civil) responsável pela região.

**Qual decisão ele apoia?** *"preciso agir agora, daqui a pouco, ou só acompanhar?"* — por exemplo, intensificar o monitoramento, avisar os moradores do trecho, acionar a limpeza de bueiros e bocas de lobo, ou deixar uma equipe de prontidão. O sistema **não decide** nada sozinho: ele organiza os dados e sinaliza; a decisão continua com a pessoa responsável (ver a reflexão crítica, seção 8).

> O território é descrito por **região e equipamento**, nunca por pessoas. O sistema não guarda nome, CPF ou endereço de ninguém; a população em risco é só um número estimado da região.

---

## 2. Decisões da equipe

| Decisões | O que escolhemos | Por quê |
|---|---|---|
| Recorte | Chuva e alagamento | É um risco recorrente nas cidades brasileiras e dá para medir com sensores simples. |
| Sensores | Chuva (mm/h), nível do córrego (m) e umidade do solo (%) | Chuva é a causa; o nível do córrego é a consequência direta; a umidade do solo mostra se o chão ainda absorve água ou se tudo vai escorrer para o córrego. |
| Banco de dados | JDBC puro + H2 em modo arquivo | Não precisa instalar servidor de banco; os dados ficam num arquivo e sobrevivem ao reinício. |
| Limiares de chuva | 10 / 25 / 50 mm/h | Inspirados nas classes usuais de intensidade de chuva (chuva forte a partir de ~10 mm/h, muito forte acima de ~50 mm/h). **Não são valores oficiais** de nenhum órgão: são um ponto de partida ajustável no menu. |
| Limiares do córrego | 2,0 / 3,0 / 3,8 m | Supomos um córrego cuja margem fica a 4 m de profundidade; CRÍTICO = 20 cm antes de transbordar. |
| Limiares de umidade | 70 / 85 / 95 % | Quanto mais perto de 100 %, mais perto o solo está de saturar. |
| Mínimo de leituras | 5 | Abaixo disso o sistema prefere ficar em silêncio a chutar (seção 8). |
| Bônus | Simulação de 24 horas | Mostra, numa tempestade fictícia, se a previsão avisa **antes** do valor real chegar ao perigo. |

---

## 3. Estrutura do projeto

```
src/main/java/
├── SistemaMonitoramento.java        → main: menu (Scanner em loop); captura as exceções
├── dominio/
│   ├── ComponenteTerritorial.java   → classe ABSTRATA (id, nome, região, status) + verificarStatus()
│   ├── PontoMonitoramento.java      → subclasse concreta: o trecho do córrego e seus sensores
│   ├── Sensor.java                  → INTERFACE: lerValor(), estaFuncionando(), getTipo()
│   ├── SensorChuva.java             → implementação 1 (Random)
│   ├── SensorNivelCorrego.java      → implementação 2 (Random)
│   ├── SensorUmidadeSolo.java       → implementação 3 (Random)
│   ├── TipoSensor.java              → enum: nome, unidade e faixa válida de cada sensor
│   ├── NivelAlerta.java             → enum: NORMAL, ATENÇÃO, ALERTA, CRÍTICO + recomendação
│   ├── Medicao.java                 → uma leitura; o construtor valida a faixa
│   ├── Alerta.java                  → um alerta com sensor, valor previsto, limite e motivo
│   ├── DadosTerritorio.java         → atributos privados + código de acesso para mudar limiares
│   ├── EstrategiaPrevisao.java      → classe ABSTRATA de previsão (valida o histórico)
│   ├── PrevisaoMediaMovel.java      → estratégia 1 (sobrescreve prever() e usa super)
│   └── PrevisaoComparacao.java      → estratégia 2 (sobrescreve prever() e usa super)
├── excecao/
│   ├── DadosInvalidosException.java
│   └── FonteIndisponivelException.java
├── persistencia/
│   ├── ConexaoBanco.java            → URL do H2 e criação das tabelas
│   ├── MedicaoDAO.java              → PreparedStatement + try-with-resources
│   └── AlertaDAO.java               → PreparedStatement + try-with-resources
└── servico/
    ├── ConfiguracaoSistema.java     → SINGLETON com os limiares
    ├── FabricaPrevisao.java         → FACTORY das estratégias
    ├── ServicoMonitoramento.java    → regras: ler, prever, classificar, alertar, simular 24 h
    └── ResultadoPrevisao.java       → valor previsto + significado + alerta (se houver)
src/main/resources/schema.sql        → estrutura das tabelas (documentação)
src/test/java/                       → 27 testes JUnit 5 (incluindo Mockito e H2 em memória)
```

---

## 4. Como os requisitos foram atendidos

| Requisito | Onde está |
|---|---|
| Classe abstrata `ComponenteTerritorial` com `verificarStatus()` abstrato | `dominio/ComponenteTerritorial.java`, implementado em `PontoMonitoramento` (OPERANTE / FALHA PARCIAL / SEM SENSORES) |
| Interface `Sensor` com 3 implementações | `SensorChuva`, `SensorNivelCorrego`, `SensorUmidadeSolo` |
| `DadosTerritorio` com atributos privados e código de acesso | `alterarLimiar(codigo, ...)` recusa código errado com `DadosInvalidosException` |
| `EstrategiaPrevisao` abstrata + 2 filhas com `prever()` sobrescrito e `super` | `PrevisaoMediaMovel` e `PrevisaoComparacao` chamam `super(nome)` no construtor e `super.prever(historico)` para reaproveitar a validação |
| Escolha da estratégia sem `if tipo ==` nem `instanceof` | `FabricaPrevisao` usa um mapa nome → construtor; o menu numera os nomes que a fábrica conhece |
| `DadosInvalidosException` | medição fora da faixa (`Medicao`), população negativa (`DadosTerritorio`), limiar inválido (`ConfiguracaoSistema`), histórico insuficiente, código errado, estratégia desconhecida |
| `FonteIndisponivelException` | sensor sem resposta (`lerValor()`) |
| Exceções capturadas no menu com mensagem clara, sem `catch` vazio | `SistemaMonitoramento` (ex.: `[FALHA] Pluviômetro sem resposta... Leitura não gravada.`) |
| DAOs com `PreparedStatement` e try-with-resources | `MedicaoDAO`, `AlertaDAO`, `ConexaoBanco` — nenhum SQL é montado concatenando valores |
| Toda leitura válida e todo alerta são gravados; histórico por sensor; lista de alertas | opções 1, 2, 3 e 4 do menu |
| Dados sobrevivem ao reinício | H2 em modo arquivo (`banco/territorio.mv.db`) |
| Padrão criacional justificado | Singleton + Factory (seção 5) |
| ≥ 5 testes AAA, `assertThrows`, Mockito | 27 testes; 13 usam `assertThrows`; `ServicoMonitoramentoTest` usa Mockito nos dois DAOs |
| Limiares vêm da `ConfiguracaoSistema` | `ServicoMonitoramento.classificar()` consulta o Singleton |
| Menu: sensores · previsões · histórico · território · status | opções 1 a 6 (+ 7 = bônus) |

---

## 5. Padrões criacionais de projeto 

### Singleton — `ConfiguracaoSistema`
Os limiares de alerta precisam ser **os mesmos para o sistema inteiro**: se o menu altera o limiar de ATENÇÃO da chuva, o serviço de alertas precisa enxergar o valor novo na mesma hora. Se cada classe fizesse `new ConfiguracaoSistema()`, cada uma teria a sua cópia e elas poderiam discordar. Por isso o construtor é **privado** e o único jeito de obter a configuração é `ConfiguracaoSistema.getInstancia()`, que sempre devolve o mesmo objeto (o teste `deveExistirUmaUnicaInstancia` confere isso com `assertSame`).

### Factory — `FabricaPrevisao`
O sistema precisa criar a estratégia de previsão que o usuário escolher. Sem a fábrica, o menu teria uma escada de `if (tipo == ...) new ...` — exatamente o que o enunciado proíbe. A fábrica guarda um **mapa nome → construtor**:

```java
CATALOGO.put("MEDIA_MOVEL", PrevisaoMediaMovel::new);
CATALOGO.put("COMPARACAO", PrevisaoComparacao::new);
```

O menu nunca dá `new PrevisaoMediaMovel()`: ele pede `FabricaPrevisao.criar(nome)`. Para incluir uma terceira estratégia basta criar a classe e adicionar **uma linha** no mapa; menu e serviço continuam iguais.

---

## 6. Como executar

### Requisitos
- **JDK 17 ou mais novo** (`java -version`)
- **Maven 3.8+** (`mvn -version`) — no Ubuntu: `sudo apt install maven`
- Não precisa instalar banco: o H2 é baixado pelo Maven e roda internamente

### Rodar os testes
```bash
mvn test
```
Resultado esperado: `Tests run: 27, Failures: 0, Errors: 0, Skipped: 0` `BUILD SUCCESS`.

### Rodar o sistema (menu)
```bash
mvn compile exec:java
```

Pela IDE (IntelliJ IDEA ou Eclipse): abrir a pasta como projeto Maven e executar a classe `SistemaMonitoramento`.

### Roteiro rápido de demonstração
1. Opção **1** com 6 rodadas → grava leituras (de vez em quando aparecem `[FALHA]` e `[DESCARTADA]`, que são as exceções sendo tratadas).
2. Opção **2** → escolha "Todas" para comparar as duas estratégias lado a lado.
3. Opção **4** → os alertas gerados, com motivo e recomendação.
4. Saia (0) e rode de novo → opção **3**: o histórico continua lá (persistência).
5. Opção **7** → simulação de 24 horas (bônus).

### Exemplo de saída (trecho real)
```
-- Rodada 6 --
  [GRAVADA]    06/11/2026 16:11 | Chuva             | 29.8 mm/h
  [GRAVADA]    06/11/2026 16:11 | Nível do córrego  | 1.8 m
  [GRAVADA]    06/11/2026 16:11 | Umidade do solo   | 67.7 %
Status do ponto: OPERANTE

== Comparação de tendência: compara as leituras recentes com as anteriores e projeta a subida/descida (reage rápido, mas pode exagerar)
  Chuva             | Comparação de tendência  | previsão: 35.2 mm/h  | ALERTA
      Chuva: a estratégia 'Comparação de tendência' prevê 35.2 mm/h (histórico analisado: 6 leituras),
      o que atinge o limite de ALERTA (25.0 mm/h). Recomendação: Avaliar medidas preventivas (avisar moradores, liberar bueiros).
  Umidade do solo   | Comparação de tendência  | previsão: 66.3 %     | Normal
      Abaixo do limiar de ATENÇÃO (70.0 %). Nenhum alerta emitido.
```

Quando um sensor falha -> mostra a exceção e o status do ponto muda:
```
  [FALHA]      Sensor de umidade do solo sem resposta (bateria fraca?). Leitura não gravada.
Status do ponto: FALHA PARCIAL (1 sensor(es) sem resposta)
```

---

## 7. Banco de dados (H2)

| Item | Valor |
|---|---|
| Modo | arquivo (embutido no programa) |
| URL JDBC | `jdbc:h2:./banco/territorio` |
| Usuário / senha | `sa` / *(vazia)* |
| Arquivo gerado | `banco/territorio.mv.db` (na pasta onde o programa foi executado) |
| Tabelas | `medicao` e `alerta` — criadas automaticamente na primeira execução (`CREATE TABLE IF NOT EXISTS`); estrutura em `src/main/resources/schema.sql` |
| Zerar os dados | apague a pasta `banco/` com o programa fechado |

Para inspecionar o banco com o programa **fechado**, dá para abrir o console web do H2:
```bash
java -cp ~/.m2/repository/com/h2database/h2/2.3.232/h2-2.3.232.jar org.h2.tools.Console
```
e conectar com a URL `jdbc:h2:<caminho-do-projeto>/banco/territorio`, usuário `sa`, senha vazia.

A pasta `banco/` está no `.gitignore`: cada pessoa que clonar o repositório começa com um banco limpo.

---

## 8. Reflexão crítica

**Quando o sistema deve ficar em silêncio.** Com menos de 5 leituras de um sensor, o sistema **não emite previsão nem alerta** — ele diz explicitamente que o histórico é insuficiente. Uma média de duas leituras pode ser só um pico isolado, e um alerta falso logo no começo ensina as pessoas a ignorar os próximos. O mesmo vale para leituras impossíveis (chuva negativa, umidade acima de 100 %): elas são descartadas e nunca entram no banco, porque um único valor absurdo distorceria todas as médias seguintes. Quando um sensor não responde, o ponto aparece como "FALHA PARCIAL": a ausência de alerta naquele sensor significa "não sei", e não "está tudo bem".

**O que os dados não representam.** As leituras são **simuladas** com `Random`; servem para demonstrar o funcionamento, não descrevem nenhum córrego real. Mesmo com sensores reais, um único ponto de medição não representa a bacia inteira: pode estar chovendo forte rio acima enquanto o pluviômetro local marca garoa. Os limiares são aproximações nossas, não valores calibrados com o histórico de enchentes do lugar. Também não estão no modelo coisas decisivas para um alagamento, como bueiros entupidos, obras, lixo no córrego e a maré de outros rios. Por fim, a "população em risco" é uma estimativa agregada da região — o sistema não sabe quem está em casa, quem tem mobilidade reduzida ou quem já saiu.

**Por que o alerta apoia e não decide.** As duas estratégias erram de jeitos diferentes. Na simulação de 24 horas, a média móvel é estável mas **atrasada**: costuma apontar ALERTA só depois que o valor real já chegou lá. A comparação de tendência costuma avisar **1 a 2 horas antes**, mas **exagera** no pico (num dos testes previu 82 mm/h para uma hora em que a chuva real foi de 54,5 mm/h) e, quando a chuva começa a diminuir, projeta queda rápida demais. Nenhuma das duas sabe o que uma pessoa da Defesa Civil sabe olhando para o céu, para o córrego e para a previsão meteorológica. Por isso cada alerta traz o **motivo** (sensor, valor previsto, limite) e uma **recomendação** em forma de verbo brando ("acompanhar", "avaliar") — quem decide evacuar, interditar ou avisar é a pessoa responsável, que pode concordar com o sistema ou desconfiar dele com base no que está vendo.

---

## 9. Bônus — simulação de 24 horas

A opção **7** gera uma tempestade fictícia hora a hora (a chuva cresce, atinge o pico e diminui; o córrego enche depois da chuva). A cada hora chega uma leitura nova e a estratégia escolhida prevê a hora seguinte. No final o sistema compara: **a previsão chegou ao nível de ALERTA antes do valor real?**

```
08h   | 16.6 mm/h  [ATENÇÃO]  | 23.5 mm/h  [ATENÇÃO]
09h   | 13.0 mm/h  [ATENÇÃO]  | 22.0 mm/h  [ATENÇÃO]
10h   | 19.6 mm/h  [ATENÇÃO]  | 31.1 mm/h  [ALERTA]
11h   | 32.1 mm/h  [ALERTA]   | 43.3 mm/h  [ALERTA]
...
Resumindo: a previsão apontou *alerta* às 10h, o valor real só chegou lá às 11h (1 hora(s) de antecedência para agir).
```

Rodando o cenário 200 vezes por sensor, a **comparação de tendência** avisou com 1 a 2 horas de antecedência em cerca de 87 % das vezes; a **média móvel** nunca avisou antes (sempre na mesma hora ou atrasada). A simulação **não grava nada no banco**, para não misturar dados fictícios com as leituras do monitoramento.

---

## 10. Limitações conhecidas

- Os limiares alterados no menu e a população em risco ficam na memória: ao reiniciar, voltam ao padrão (o enunciado pede persistência das medições e alertas, que estão no banco).
- Há um único ponto de monitoramento com três sensores.
- As leituras são simuladas; trocar por dados reais exigiria apenas uma nova classe que implemente `Sensor`.

---

## 11. Uso de IA

Usamos o **Claude da Anthropic** como assistente durante o desenvolvimento:

- **Onde foi usado:** geração da primeira versão das classes, dos testes e desse README aqui, a partir das decisões tomadas pela equipe (recorte de chuva e alagamento, sensores, JDBC + H2, padrões Singleton e Factory, bônus da simulação); sugestão das fórmulas das duas estratégias; revisão de mensagens de erro.
- **Como revisamos:** rodamos `mvn test` (27 testes passando) e o menu completo, incluindo os casos de erro (código de acesso errado, população negativa, limiar fora de ordem, histórico insuficiente); fechamos e reabrimos o programa para confirmar que os dados persistem; ajustamos a estratégia de comparação depois de ver na simulação que a primeira versão quase nunca antecipava os alertas (de ~2 % para ~87 % de avisos antecipados); lemos cada classe para conseguir explicá-la na conversa de correção.
- **O que é responsabilidade nossa:** as escolhas de recorte, sensores, limiares e a interpretação dos resultados na reflexão crítica.

---

## 12. Divisão de tarefas

| Integrante | Responsabilidades |
|---|---|
| Lucas M. Moraes (RM563667) | Domínio (classes abstratas, interface `Sensor` e sensores, `Medicao`, `Alerta`, `DadosTerritorio`), estratégias de previsão, `FabricaPrevisao` e `ConfiguracaoSistema`, simulação de 24 horas |
| Antonio Lucas (RM565516) | Persistência (`ConexaoBanco`, DAOs, `schema.sql`), `ServicoMonitoramento`, menu `SistemaMonitoramento`, testes JUnit/Mockito |
| Ambos | Definição do problema e do público, limiares, reflexão crítica, revisão do código e README |
