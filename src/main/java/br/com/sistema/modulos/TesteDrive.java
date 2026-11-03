package br.com.sistema.modulos;
import br.com.sistema.consumidor.LogDTO;
import br.com.sistema.produtor.EnviarLog;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TesteDrive {
    public static void agendar(Long id, String cliente, String modelo, String dataHora, String usuario) {
        String sql = "INSERT INTO tb_teste_drive (cliente, modelo, data_agendamento) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente);
            stmt.setString(2, modelo);
            stmt.setString(3, dataHora);
            stmt.executeUpdate();
            System.out.println(" [BANCO OPERACIONAL] Agendamento gravado em tb_teste_drive!");
        } catch (SQLException e) {
            System.err.println(" Erro no teste drive: " + e.getMessage());
        }

        LogDTO log = new LogDTO();
        log.setId(id); log.setModulo("TesteDrive"); log.setAcao("AGENDAR_TESTE_DRIVE: " + modelo);
        log.setUsuario(usuario); log.setModelo(modelo);
        log.setData(LocalDate.now().toString()); log.setTimestamp(LocalDateTime.now().toString());
        EnviarLog.dispararLog(log);
    }
}