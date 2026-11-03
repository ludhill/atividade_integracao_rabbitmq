package br.com.sistema.consumidor;

import br.com.sistema.produtor.FilaSimulada;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReceberLog {

    // Configurações do RabbitMQ (Ajuste o IP e credenciais do seu ambiente)
    private static final String RABBIT_HOST = "56.125.4.XXX";
    private static final int RABBIT_PORT = 5672;
    private static final String RABBIT_USER = "aluno";
    private static final String RABBIT_PASS = "12345";
    private static final String FILA_LOGS = "dupla_maria_lucas";

    // Configurações de Redundância (Disco e Banco)
    private static final String ARQUIVO_LOG = "logs_operacoes.json";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/db_logs_auditoria?useSSL=false&serverTimezone=UTC";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASS = "";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        System.out.println(">>> [MÓDULO DE LOGS ATIVO] Iniciando serviço de recepção...");

        // 1. WORKER DE CONTINGÊNCIA: Escuta a FilaSimulada (memória) caso o RabbitMQ caia
        new Thread(() -> {
            while (true) {
                try {
                    String jsonRecebido = FilaSimulada.consumir();
                    System.out.println(">>> [LOG RECEBIDO VIA FILA SIMULADA]: " + jsonRecebido);
                    processarEPersistir(jsonRecebido);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println(">>> Erro no worker de contingência: " + e.getMessage());
                }
            }
        }, "Worker-FilaSimulada").start();

        // 2. CONSUMIDOR NATIVO RABBITMQ
        iniciarConsumidorRabbitMQ();
    }

    private static void iniciarConsumidorRabbitMQ() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(RABBIT_HOST);
            factory.setPort(RABBIT_PORT);
            factory.setUsername(RABBIT_USER);
            factory.setPassword(RABBIT_PASS);
            factory.setConnectionTimeout(3000); // Timeout rápido de conexão

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(FILA_LOGS, true, false, false, null);
            System.out.println(">>> [RABBITMQ CONECTADO] Escutando a fila '" + FILA_LOGS + "'...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String jsonRecebido = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(">>> [LOG RECEBIDO VIA RABBITMQ]: " + jsonRecebido);
                processarEPersistir(jsonRecebido);
            };

            channel.basicConsume(FILA_LOGS, true, deliverCallback, consumerTag -> {});

        } catch (Exception e) {
            System.err.println("⚠️ [RABBITMQ INDISPONÍVEL] O módulo operará em contingência local (Fila Simulada).");
            System.err.println("   Motivo: " + e.getMessage());
        }
    }

    // Método único para garantir a dupla persistência
    public static void processarEPersistir(String jsonRecebido) {
        // Redundância 1: Salva no arquivo local (.json)
        salvarEmArquivo(jsonRecebido);

        // Converte para DTO
        LogDTO dto = null;
        try {
            dto = mapper.readValue(jsonRecebido, LogDTO.class);
        } catch (Exception e) {
            System.err.println(">>> Falha no parse do DTO (o payload bruto será salvo mesmo assim): " + e.getMessage());
        }

        // Redundância 2: Salva no MySQL
        salvarNoMySQL(jsonRecebido, dto);
    }

    private static void salvarEmArquivo(String conteudo) {
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String registro = "[" + dataHora + "] LOG: " + conteudo;

        try (FileWriter fw = new FileWriter(ARQUIVO_LOG, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(registro);
            System.out.println(">>> [DISCO LOCAL] Log gravado em '" + ARQUIVO_LOG + "'");

        } catch (Exception e) {
            System.err.println(">>> Erro ao escrever no arquivo de log: " + e.getMessage());
        }
    }

    private static void salvarNoMySQL(String json, LogDTO dto) {
        String sql = "INSERT INTO tb_log_operacoes (origem_id, usuario, timestamp_operacao, data_operacao, acao, payload_json) VALUES (?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASS);
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 1. ID de Origem
            if (dto != null && dto.getId() != null) {
                stmt.setLong(1, dto.getId());
            } else {
                stmt.setNull(1, java.sql.Types.BIGINT);
            }

            // 2. Usuário (Fallback para valor genérico)
            String usuario = (dto != null && dto.getUsuario() != null) ? dto.getUsuario() : "Sistema Central";
            stmt.setString(2, usuario);

            // 3. Timestamp (Gera data/hora atual se não for informado)
            String timestamp = (dto != null && dto.getTimestamp() != null)
                    ? dto.getTimestamp()
                    : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            stmt.setString(3, timestamp);

            // 4. Data (Gera data atual YYYY-MM-DD se não for informada)
            String data = (dto != null && dto.getData() != null)
                    ? dto.getData()
                    : LocalDate.now().toString();
            stmt.setString(4, data);

            // 5. Ação
            String acao;
            if (dto != null && dto.getAcao() != null) {
                acao = dto.getAcao();
            } else if (dto != null && dto.getModelo() != null) {
                acao = "LOG DE CARRO: " + dto.getModelo() + " (Ano " + dto.getAno() + ")";
            } else {
                acao = "OPERACAO DESCONHECIDA";
            }
            stmt.setString(5, acao);

            // 6. Payload JSON completo
            stmt.setString(6, json);

            stmt.executeUpdate();
            System.out.println(">>> [BANCO MYSQL] Log inserido na tb_log_operacoes com sucesso!");

        } catch (Exception e) {
            System.err.println(">>> [ERRO MYSQL] Falha ao salvar no banco de dados: " + e.getMessage());
        }
    }
}