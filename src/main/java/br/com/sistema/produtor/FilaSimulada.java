package br.com.sistema.produtor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FilaSimulada {

    // Fila em memória thread-safe que simula o broker do RabbitMQ
    private static final BlockingQueue<String> FILA = new LinkedBlockingQueue<>();

    public static void publicar(String jsonMessage) {
        FILA.offer(jsonMessage);
    }

    public static String consumir() throws InterruptedException {
        return FILA.take(); // Aguarda até que uma nova mensagem chegue na fila
    }
}