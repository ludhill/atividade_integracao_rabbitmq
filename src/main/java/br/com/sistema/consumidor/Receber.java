package br.com.sistema.consumidor;

import br.com.sistema.produtor.Enviar;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Receber {

    static Enviar l = new Enviar();

    public static void main(String[] args) {
        try {
            lerMensagem();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void lerMensagem() throws IOException {
        ConnectionFactory factory;
        Connection connection;
        Channel channel;

        String QUEUE_NAME1 = "11999";
        String EXCHANGE_NAME1 = "11999";
        try {
            factory = new ConnectionFactory();
            factory.setUri("amqp://15.228.215.36:5672");
            factory.setUsername("aluno");
            factory.setPassword("12345");

            connection = factory.newConnection();
            channel = connection.createChannel();

            //declaring exchanges
            channel.exchangeDeclare(EXCHANGE_NAME1, "fanout", true, false, null);

            //declaring queues
            channel.queueDeclare(QUEUE_NAME1, true, false, false, null);

            //binding queues to exchanges
            channel.queueBind(QUEUE_NAME1, EXCHANGE_NAME1, "");

            DeliverCallback deliverCallbackQuestion = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("MENSAGEM RECEBIDA = '" + message + "'");

                String partes[] = message.split(",");

                if(partes[1].equals("12345")){
                    Enviar.enviarMensagem("true", partes[0]);
                }

            };

            channel.basicConsume(QUEUE_NAME1, true, deliverCallbackQuestion, consumerTag -> {
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
//import br.com.sistema.produtor.Enviar;
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
//import com.rabbitmq.client.DeliverCallback;
//
//import java.nio.charset.StandardCharsets;
//
//public class Receber {
//
//    private static final String HOST = "56.228.215.36";
//    private static final int PORT = 5672;
//    private static final String USER = "aluno";
//    private static final String PASS = "12345";
//
//    public static void main(String[] args) {
//        lerMensagem();
//    }
//
//    public static void lerMensagem() {
//        String QUEUE_NAME1 = "preco";
//        String EXCHANGE_NAME1 = "preco";
//
//        try {
//            ConnectionFactory factory = new ConnectionFactory();
//            factory.setHost(HOST);
//            factory.setPort(PORT);
//            factory.setUsername(USER);
//            factory.setPassword(PASS);
//            factory.setConnectionTimeout(3000);
//
//            Connection connection = factory.newConnection();
//            Channel channel = connection.createChannel();
//
//            // Declaração de Exchange e Fila com Bind
//            channel.exchangeDeclare(EXCHANGE_NAME1, "fanout", true, false, null);
//            channel.queueDeclare(QUEUE_NAME1, true, false, false, null);
//            channel.queueBind(QUEUE_NAME1, EXCHANGE_NAME1, "");
//
//            System.out.println(">>> [CONSUMIDOR ATIVO] Aguardando mensagens na fila '" + QUEUE_NAME1 + "'...");
//
//            DeliverCallback deliverCallbackQuestion = (consumerTag, delivery) -> {
//                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
//                System.out.println(">>> MENSAGEM RECEBIDA: '" + message + "'");
//
//                // Validação defensiva antes do split
//                String[] partes = message.split(",");
//
//                if (partes.length >= 2 && "12345".equals(partes[1].trim())) {
//                    String filaResposta = partes[0].trim();
//                    System.out.println(" Senha validada! Enviando resposta 'true' para a fila '" + filaResposta + "'...");
//                    Enviar.enviarMensagem("true", filaResposta);
//                } else {
//                    System.out.println("⚠️ Mensagem não atende ao formato esperado ('fila,12345'): " + message);
//                }
//            };
//
//            channel.basicConsume(QUEUE_NAME1, true, deliverCallbackQuestion, consumerTag -> {});
//
//        } catch (Exception e) {
//            System.err.println(" Erro no consumidor Receber: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}