package br.com.sistema.produtor;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class Enviar {


    public static void main(String[] args) {
        enviarMensagem("lucas_denner,12345", "11999");
    }

    public static void enviarMensagem(String message, String queue) {
        ConnectionFactory factory = new ConnectionFactory();

        try {
            factory.setUri("amqp://15.228.215.36:5672");
            factory.setUsername("aluno");
            factory.setPassword("12345");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.queueDeclare(queue, true, false, false, null);
            channel.basicPublish("", queue, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("MENSAGEM ENVIADA = '" + message + "'");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


}
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
//
//import java.nio.charset.StandardCharsets;
//
//public class Enviar {
//
//    private static final String HOST = "56.228.215.36";
//    private static final int PORT = 5672;
//    private static final String USER = "aluno";
//    private static final String PASS = "12345";
//
//    public static void main(String[] args) {
//        enviarMensagem("lucas_denner,12345", "preco");
//    }
//
//    public static void enviarMensagem(String message, String queue) {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(HOST);
//        factory.setPort(PORT);
//        factory.setUsername(USER);
//        factory.setPassword(PASS);
//        factory.setConnectionTimeout(3000);
//
//        try (Connection connection = factory.newConnection();
//             Channel channel = connection.createChannel()) {
//
//            // Garante a existência da fila de destino
//            channel.queueDeclare(queue, true, false, false, null);
//
//            // Publica na fila especificada
//            channel.basicPublish("", queue, null, message.getBytes(StandardCharsets.UTF_8));
//            System.out.println(">>> MENSAGEM ENVIADA: '" + message + "' para a fila '" + queue + "'");
//
//        } catch (Exception e) {
//            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}