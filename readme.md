
# Sistema Integrado de Concessionária & Auditoria

Este projeto consiste em um sistema de gestão de concessionária integrado a uma arquitetura assíncrona de auditoria distribuída. O sistema suporta persistência operacional local, mensageria via RabbitMQ com *fallback* transparente em memória e gravação redundante de logs.

---

## 🏗️ Arquitetura do Sistema

```text
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                           MAINAPLICACAO (CLI Scanner)                       │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │ (1) Execução da Ação do Usuário
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         BACK-END (Módulos de Serviço)                        │
 │       Carro / Vendas / TesteDrive / Contabil / Usuario / Adm                │
 └──────────────┬──────────────────────────────────────────────┬───────────────┘
                │                                              │
  (2) Persistência Operacional                                 │ (3) Envio do Log
      db_sistema_integrado                                     │     EnviarLog.java
                ▼                                              ▼
 ┌──────────────────────────────┐              ┌──────────────────────────────┐
 │    MySQL Operacional         │              │      PRODUTOR DE LOGS        │
 │ (tb_carro, tb_vendas, etc)   │              │   (EnviarLog + FilaSimulada) │
 └──────────────────────────────┘              └──────────────┬───────────────┘
                                                              │
                                       (4) Envio com Contingência Automática
                                                              │
                     ┌────────────────────────────────────────┴────────────────────────────────────────┐
                     │                                                                                 │
             [RabbitMQ ONLINE]                                                                [RabbitMQ OFFLINE]
                     │                                                                                 │
                     ▼                                                                                 ▼
 ┌──────────────────────────────────────┐                                           ┌──────────────────────────────────────┐
 │          BROKER RABBITMQ             │                                           │            FILA SIMULADA             │
 └──────────────────┬───────────────────┘                                           └──────────────────┬───────────────────┘
                    │                                                                                  │
                    └───────────────────────────────────┬──────────────────────────────────────────────┘
                                                        │
                                                        │ (5) Consumo de Mensagens
                                                        ▼
                                      ┌───────────────────────────────────┐
                                      │    CONSUMIDOR (ReceberLog.java)   │
                                      └─────────────────┬─────────────────┘
                                                        │
                                   ┌────────────────────┴────────────────────┐
                                   │ (6) Dupla Persistência Redundante       │
                                   ▼                                         ▼
                    ┌──────────────────────────────┐          ┌──────────────────────────────┐
                    │       ARQUIVO LOCAL          │          │      BANCO DE AUDITORIA      │
                    │    logs_operacoes.json       │          │   db_logs_auditoria          │
                    │    (Disco Físico)            │          │   (tb_log_operacoes)         │
                    └──────────────────────────────┘          └──────────────────────────────┘

```

---

## 🗄️ Estrutura dos Bancos de Dados (MySQL)

Crie no MySQL os dois bancos de dados utilizados pela aplicação:

```sql
-- 1. BANCO OPERACIONAL
CREATE DATABASE IF NOT EXISTS db_sistema_integrado;
USE db_sistema_integrado;

CREATE TABLE IF NOT EXISTS tb_carro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    modelo VARCHAR(100),
    ano INT,
    preco DOUBLE
);

CREATE TABLE IF NOT EXISTS tb_vendas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente VARCHAR(100),
    valor_total DOUBLE,
    data_venda VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS tb_teste_drive (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente VARCHAR(100),
    modelo VARCHAR(100),
    data_agendamento VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS tb_contabilidade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(150),
    valor DOUBLE,
    tipo VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS tb_usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    email VARCHAR(100),
    perfil VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS tb_adm (
    parametro VARCHAR(100) PRIMARY KEY,
    valor VARCHAR(255)
);

-- 2. BANCO DE AUDITORIA
CREATE DATABASE IF NOT EXISTS db_logs_auditoria;
USE db_logs_auditoria;

CREATE TABLE IF NOT EXISTS tb_log_operacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    origem_id BIGINT,
    usuario VARCHAR(100),
    timestamp_operacao VARCHAR(50),
    data_operacao VARCHAR(20),
    acao VARCHAR(255),
    payload_json TEXT,
    recebido_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

```

---

## 🔒 Matriz de Permissões (RBAC)

O controle de acesso é validado diretamente na `MainAplicacao.java` antes da digitação dos dados:

| Módulo | Perfil **Dono** | Perfil **ADM** | Perfil **Vendedor** |
| --- | --- | --- | --- |
| **1. Carro** | ✅ Permitido | ✅ Permitido | ✅ Permitido |
| **2. Vendas** | ✅ Permitido | ❌ Negado | ✅ Permitido |
| **3. TesteDrive** | ✅ Permitido | ✅ Permitido | ✅ Permitido |
| **4. Contabilidade** | ✅ Permitido | ❌ Negado | ❌ Negado |
| **5. Usuario** | ✅ Permitido | ✅ Permitido | ❌ Negado |
| **6. ADM** | ✅ Permitido | ✅ Permitido | ❌ Negado |

---

## 🖥️ Onde e Como Interagir com o Scanner (Execução)

Toda a interação com o sistema ocorre na classe `MainAplicacao.java`.

### Passo a Passo para Rodar no IntelliJ:

1. Abra o projeto no IntelliJ IDEA.
2. Certifique-se de que o MySQL local esteja ativo (XAMPP/phpMyAdmin/Docker).
3. Localize o arquivo `br.com.sistema.MainAplicacao.java`.
4. Clique com o botão direito sobre o arquivo e escolha **Run 'MainAplicacao.main()'**.
5. Na parte inferior da tela, clique na aba chamada **`Run` / `MainAplicacao**` (este é o **Console interativo** onde o `Scanner` aguarda a digitação).

### Fluxo de Digitação no Console:

1. **Nome de usuário:** Digite seu nome (ex: `Carlos`) e pressione `Enter`.
2. **Perfil:** Digite `Dono`, `ADM` ou `Vendedor` e pressione `Enter`.
3. **Menu:** Digite o número da opção (ex: `1` para Cadastrar Veículo).
4. **Campos:** Responda às perguntas sequenciais sobre modelo, ano e preço solicitados pelo `Scanner`.

> **Nota de Resiliência:** A `MainAplicacao.java` inicializa o consumidor `ReceberLog` em uma *Thread* separada na mesma JVM. Portanto, todas as mensagens registradas aparecerão no mesmo console em tempo real.

---

## 🛠️ Guia de Ajustes e Adaptações Futuras

### 1. Alterar Credenciais do Banco MySQL

Caso sua senha do MySQL não seja vazia (`""`), atualize os arquivos:

* `ConexaoDB.java` (banco operacional): `MYSQL_PASS`
* `ReceberLog.java` (banco de auditoria): `MYSQL_PASS`

### 2. Alterar o IP do Broker RabbitMQ

Para conectar ao RabbitMQ do professor quando o servidor estiver online:

* No arquivo `EnviarLog.java`, altere `RABBIT_HOST` para o IP correspondente.
* No arquivo `ReceberLog.java`, altere `RABBIT_HOST` para o mesmo IP.

### 3. Conectar a Interface HTML ao Java

O arquivo `src/main/resources/static/index.html` fornece o protótipo visual. Para que ele altere o banco via navegador em vez do `Scanner`, adicione um servidor web nativo ao Java (`com.sun.net.httpserver.HttpServer`) mapeando os métodos da `ModulosService.java` para rotas REST HTTP (`POST /api/carro`, `POST /api/venda`).

```

```