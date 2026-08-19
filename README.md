# Runtime Image API (Java 17)

API stateless desenvolvida com Spring Boot que gera avatares e placeholders em SVG no momento da requisição.
Ela não utiliza banco de dados nem armazenamento em memória: cada resposta é gerada a partir dos dados recebidos na requisição.

Compilar e executar os testes:

```bash
mvn test
```

Executar a aplicação:

```bash
mvn spring-boot:run
```

Swagger UI (após iniciar a aplicação):

```bash
# Acesse http://localhost:8080/api
```

## Endpoints

Gerar um avatar com iniciais:

```bash
curl "http://localhost:8080/api/v1/avatar?name=Ada%20Lovelace&size=128&shape=CIRCLE" -o avatar.svg
```

Gerar um placeholder:

```bash
curl "http://localhost:8080/api/v1/placeholder?width=640&height=360&label=Hero" -o placeholder.svg
```

Parâmetros de consulta suportados:

* `/api/v1/avatar`: `name`, `size`, `shape`, `background`, `color`
* `/api/v1/placeholder`: `width`, `height`, `label`, `background`, `color`

As cores devem ser informadas em formato hexadecimal, como `#2563eb`.

## Receitas do OpenRewrite

Este projeto utiliza o [OpenRewrite](https://docs.openrewrite.org/) para demonstrar refatorações automatizadas e conscientes da estrutura do código-fonte, em vez de editar manualmente números de versão e códigos repetitivos.

As receitas são definidas declarativamente no arquivo [`recipe/rewrite.yml`](recipe/rewrite.yml) e integradas ao processo de build por meio do bloco `rewrite-maven-plugin` no [`pom.xml`](pom.xml).

### Como utilizar

Para visualizar quais alterações uma receita realizaria sem modificar nenhum arquivo:

```bash
mvn rewrite:dryRun
```

Esse comando gera um diff em `target/rewrite/rewrite.patch` e exibe um resumo indicando quais arquivos e receitas seriam afetados. Nenhum arquivo da árvore de trabalho é modificado.

Para aplicar as alterações:

```bash
mvn rewrite:run
```

Por padrão, esse comando executa todas as receitas listadas em `<activeRecipes>` no `pom.xml`. Para executar apenas uma delas, sobrescreva a configuração pela linha de comando:

```bash
mvn rewrite:run -Drewrite.activeRecipes=com.example.demo.UpgradeJavaAndSpringBoot
mvn rewrite:run -Drewrite.activeRecipes=com.example.demo.FinalizeVariables
```

### O que está definido e por quê

**`com.example.demo.UpgradeJavaAndSpringBoot`** — atualiza o projeto para a versão LTS mais recente do Java e para a versão mais recente do Spring Boot:

* `org.openrewrite.java.migrate.UpgradeToJava25` — percorre a cadeia de migração Java 17 → 21 → 25: atualiza `java.version` e a propriedade `release` do plugin de compilação, além de reescrever trechos do código-fonte que dependam de APIs removidas ou depreciadas durante essas versões.
* `org.openrewrite.java.spring.boot4.UpgradeSpringBoot_4_0` — percorre a cadeia de migração Spring Boot 3.2 → 3.3 → 3.4 → 3.5 → 4.0: atualiza a versão do POM pai, migra para os starters modulares do Spring Boot 4 (por exemplo, `spring-boot-starter-web` → `spring-boot-starter-webmvc`) e atualiza código-fonte e configurações que sofreram alterações entre essas versões.

Por que utilizar uma receita em vez de editar o `pom.xml` manualmente: uma atualização de versão desse porte nunca envolve apenas alterar um número. Ela também pode envolver renomeação de starters, movimentação de pacotes e substituição de APIs depreciadas, mudanças que são fáceis de esquecer ao realizar a migração manualmente e trabalhosas de verificar.

A receita identifica e corrige essas alterações em uma única execução, enquanto o `dryRun` permite revisar o diff exato antes que qualquer arquivo seja modificado.

**`com.example.demo.FinalizeVariables`** — realiza uma refatoração voltada à qualidade do código, e não uma atualização de versão:

* `org.openrewrite.staticanalysis.FinalizeLocalVariables` — adiciona `final` às variáveis locais que nunca são reatribuídas.
* `org.openrewrite.staticanalysis.FinalizeMethodArguments` — realiza o mesmo processo para parâmetros de métodos e construtores.

Por quê: este projeto já utiliza recursos como records, text blocks e streams, porém nada impede que uma alteração futura reatribua acidentalmente uma variável que deveria receber um valor apenas uma vez.

Marcar essas variáveis como `final` torna essa intenção explícita e transforma uma reatribuição acidental em um erro de compilação, sem alterar o comportamento da aplicação em tempo de execução.

Nenhuma das receitas foi aplicada até o momento. Ambas foram deixadas para uma execução deliberada de:

```bash
mvn rewrite:run
```

Dessa forma, o diff resultante pode ser revisado antes que as alterações sejam efetivamente aplicadas.
