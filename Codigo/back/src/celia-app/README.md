# CÃ©lia Bordados - AplicaÃ§Ã£o Spring Boot

## Sobre o Projeto
Este projeto foi migrado para o Spring Boot, permitindo que a aplicaÃ§Ã£o seja executada tanto em modo console quanto como uma API REST.

## Estrutura do Projeto
- `com.celiabordados.App`: Classe principal do Spring Boot
- `com.celiabordados.Cliente`: Modelo de cliente
- `com.celiabordados.Administrador`: Modelo de administrador
- `com.celiabordados.controller`: Controladores REST para Cliente e Administrador
- `com.celiabordados.config`: ConfiguraÃ§Ãµes da aplicaÃ§Ã£o

## PrÃ©-requisitos
Para executar este projeto, vocÃª precisa:

1. **Java JDK 11 ou superior**
   - [Download do JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
   - Configure a variÃ¡vel de ambiente JAVA_HOME:
     - Windows: `setx JAVA_HOME "C:\Program Files\Java\jdk-11"`
     - Linux/Mac: `export JAVA_HOME=/usr/lib/jvm/java-11`

2. **Maven (opcional)**
   - O projeto inclui o Maven Wrapper, entÃ£o nÃ£o Ã© necessÃ¡rio instalar o Maven globalmente
   - Se preferir instalar o Maven: [Download do Maven](https://maven.apache.org/download.cgi)

## Como Executar

### Configurando o Backend

#### Usando o Maven Wrapper (recomendado)
```
# No Windows
cd Codigo\back\src\celia-app
.\mvnw.cmd spring-boot:run

# No Linux/Mac
cd Codigo/back/src/celia-app
./mvnw spring-boot:run
```

#### Usando o Maven instalado globalmente
```
cd Codigo\back\src\celia-app
mvn spring-boot:run
```

### Integrando Frontend com Backend

Para integrar o frontend com o backend:

1. Inicie o backend Spring Boot (instruÃ§Ãµes acima)
2. Abra os arquivos HTML do frontend diretamente no navegador
3. O frontend estÃ¡ configurado para se comunicar com a API REST no endereÃ§o `http://localhost:8080/api`

### Modos de ExecuÃ§Ã£o do Backend

#### Modo Web (API REST)
Para executar a aplicaÃ§Ã£o como uma API REST:

1. Certifique-se de que no arquivo `application.properties` a configuraÃ§Ã£o esteja:
   ```
   spring.profiles.active=web
   ```

2. Execute a aplicaÃ§Ã£o usando o Maven Wrapper ou Maven global
3. A API estarÃ¡ disponÃ­vel em `http://localhost:8080`

#### Modo Console
Para executar a aplicaÃ§Ã£o no modo console:

1. Altere no arquivo `application.properties` a configuraÃ§Ã£o para:
   ```
   spring.profiles.active=console
   ```

2. Execute a aplicaÃ§Ã£o usando o Maven Wrapper ou Maven global

## Endpoints da API

### Clientes
- `GET /api/clientes`: Lista todos os clientes
- `POST /api/clientes/cadastro`: Cadastra um novo cliente
- `POST /api/clientes/login`: Autentica um cliente
- `GET /api/clientes/{email}`: Busca um cliente pelo email

### Administrador
- `POST /api/administradores/login`: Autentica um administrador

## SoluÃ§Ã£o de Problemas

### Erro "JAVA_HOME not found"
Se vocÃª receber o erro "JAVA_HOME not found", configure a variÃ¡vel de ambiente JAVA_HOME:

```
# No Windows (PowerShell)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-11"

# No Windows (CMD)
set JAVA_HOME=C:\Program Files\Java\jdk-11

# No Linux/Mac
export JAVA_HOME=/usr/lib/jvm/java-11
```

### Erro "mvn not recognized"
Se vocÃª receber o erro "mvn not recognized", use o Maven Wrapper incluÃ­do no projeto:

```
# No Windows
.\mvnw.cmd spring-boot:run

# No Linux/Mac
./mvnw spring-boot:run
```

