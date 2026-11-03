package br.com.sistema.modulos;

import br.com.sistema.consumidor.LogDTO;
import br.com.sistema.produtor.EnviarLog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Carro {

    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/db_sistema_integrado?useSSL=false&serverTimezone=UTC";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASS = "";

    public static void cadastrarVeiculo(Long id, String modelo, Integer ano, Double preco, String usuario) {
        // 1. Grava no banco de dados operacional (db_sistema_integrado)
        String sql = "INSERT INTO tb_carro (modelo, ano, preco) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, modelo);
            stmt.setInt(2, ano);
            stmt.setDouble(3, preco);
            stmt.executeUpdate();
            System.out.println(" [CARRO] Veículo " + modelo + " gravado na tabela tb_carro do MySQL!");

        } catch (Exception e) {
            System.err.println(" [ERRO CARRO] Falha ao salvar no banco local: " + e.getMessage());
        }

        // 2. Monta e envia o Log de Auditoria para o RabbitMQ
        LogDTO log = new LogDTO();
        log.setId(id);
        log.setModulo("Carro");
        log.setAcao("CADASTRAR_CARRO");
        log.setUsuario(usuario);
        log.setModelo(modelo);
        log.setAno(ano);
        log.setPreco(preco);
        log.setData(LocalDate.now().toString());
        log.setTimestamp(LocalDateTime.now().toString());

        EnviarLog.dispararLog(log);
    }
}