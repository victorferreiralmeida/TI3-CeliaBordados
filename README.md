# Célia Bordados

O projeto Célia Bordados tem como objetivo principal desenvolver um sistema web para automatizar e otimizar a gestão de vendas e administração de uma empreendedora de bordados personalizados. Atualmente, os processos manuais via WhatsApp e caderno são ineficientes e propensos a erros. A solução proposta inclui uma vitrine online de produtos exemplares (sem estoque ativo), permitindo que clientes iniciem pedidos pelo site ou sejam redirecionados ao WhatsApp para finalização. O sistema oferecerá personalização básica (seleção de imagens, textos e fontes), área administrativa para controle de materiais com alertas de reposição, dashboard de análises de vendas e suporte a pagamentos via Pix, dinheiro e depósito. Além de melhorar a eficiência operacional, o projeto visa ampliar o alcance das vendas e fornecer insights estratégicos, mantendo fora do escopo funcionalidades como estoque ativo fora de datas comemorativas, pagamento por cartão e personalização avançada. A interface priorizará responsividade, segurança e usabilidade, atendendo tanto clientes quanto a administradora.

## Alunos integrantes da equipe

* Arthur Henrique Araujo Santos
* Eddie Christian Pereira
* Lucas Jácome Magalhães de Jesus
* Marcelo Emboaba de Matos
* Pedro Henrique de Vasconcellos Franco
* Victor Ferreira de Almeida

## Professores responsáveis

* Eveline Alonso Veloso
* Joana Gabriela Ribeiro de Souza
* Ramon Lacerda Marques

## Instruções de utilização

## Pré-requisitos
- Java 11 ou superior
- Maven
- MySQL 8.0 ou superior
- Navegador web moderno (Chrome, Firefox, Edge, etc.)

## Configuração do Ambiente
### Backend (Java Spring Boot)

1. **Configuração do Banco de Dados**
   - Crie um banco de dados MySQL chamado `celiabordados`
   - Atualize as configurações de conexão no arquivo `application.properties`

2. **Compilar e executar o Backend**
   ```bash
   cd Codigo/back/src/celia-app
   mvn clean install
   mvn spring-boot:run
   ```
   O servidor backend será inicializado na porta 8080.

### Frontend (HTML, CSS, JavaScript)

1. **Executar o Frontend**
   - Você pode simplesmente abrir os arquivos HTML em um navegador moderno
   - Ou usar um servidor web simples como o Live Server (extensão do VSCode) para evitar problemas de CORS

## Estrutura do Projeto

### Backend

- `Codigo/back/src/celia-app/src/main/java/com/celiabordados/` - Contém as classes principais do sistema
  - `controller/` - Controladores REST que expõem as APIs
  - `service/` - Camada de serviço com a lógica de negócios
  - `security/` - Componentes de segurança, autenticação e autorização
  - `model/` - Modelos de dados e DTOs

### Frontend

- `Codigo/front/` - Contém os arquivos do frontend
  - Arquivos HTML - Páginas da aplicação
  - `css/` - Estilos da aplicação
  - `js/` - Scripts JavaScript, incluindo o arquivo `api.js` que contém as funções para comunicação com o backend
  - `images/` - Imagens e recursos gráficos

## APIs Disponíveis

### Autenticação
- `POST /api/auth/cliente/login` - Login de cliente
- `POST /api/auth/admin/login` - Login de administrador
- `POST /api/auth/logout` - Logout (tanto cliente quanto administrador)

### Clientes
- `GET /api/clientes` - Listar todos os clientes (acesso restrito a administradores)
- `POST /api/clientes/cadastro` - Cadastrar novo cliente
- `GET /api/clientes/{email}` - Buscar cliente por email
- `GET /api/clientes/id/{id}` - Buscar cliente por ID
- `PUT /api/clientes/atualizar` - Atualizar dados do cliente (telefone e endereço)

## Funcionalidades Implementadas

1. **Autenticação e Autorização**
   - Sistema de login para clientes e administradores
   - Autenticação baseada em JWT
   - Controle de acesso baseado em perfis (CLIENTE/ADMIN)

2. **Gestão de Perfil**
   - Visualização de dados do cliente
   - Edição de telefone e endereço com validações
   - Validação para impedir duplicação de telefones entre clientes

3. **Interface de Usuário**
   - Design responsivo
   - Feedback visual para ações do usuário
   - Máscaras para campos de entrada (ex: telefone)

## Testes

Para executar os testes:
```bash
cd Codigo/back/src/celia-app
mvn test
```

## Contribuição
Para contribuir com o projeto:
1. Clone o repositório
2. Crie sua branch (`git checkout -b seu-nome`)
3. Faça commit das alterações (`git commit -m 'Adiciona nova funcionalidade'`)
4. Envie para a branch (`git push origin seu-nome`)
5. Abra um Pull Request

## Pontos de Atenção
- Certifique-se de que o banco de dados está configurado corretamente
- O frontend espera que o backend esteja rodando na porta 8080
- Para editar o perfil, é necessário estar logado como cliente

