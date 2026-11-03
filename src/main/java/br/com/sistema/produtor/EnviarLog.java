package br.com.sistema.produtor;

import br.com.sistema.consumidor.LogDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class EnviarLog {

    private static final String RABBIT_HOST = "56.125.4.XXX"; // Host do laboratório
    private static final String FILA_LOGS = "dupla_maria_lucas";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void dispararLog(LogDTO dto) {
        try {
            String jsonMessage = mapper.writeValueAsString(dto);

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(RABBIT_HOST);
            factory.setPort(5672);
            factory.setUsername("aluno");
            factory.setPassword("12345");
            factory.setConnectionTimeout(2000); // Timeout rápido para não travar a aplicação

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(FILA_LOGS, true, false, false, null);
                channel.basicPublish("", FILA_LOGS, null, jsonMessage.getBytes(StandardCharsets.UTF_8));
                System.out.println(">>> [RABBITMQ ONLINE] Log enviado com sucesso.");

            } catch (Exception e) {
                // SE O RABBITMQ ESTIVER OFFLINE, UTILIZA A FILA EM MEMÓRIA
                System.out.println("⚠️ [RABBITMQ INDISPONÍVEL] Redirecionando log para a Fila Simulada...");
                FilaSimulada.publicar(jsonMessage);
            }

        } catch (Exception e) {
            System.err.println("Erro na serialização do JSON: " + e.getMessage());
        }
    }
}