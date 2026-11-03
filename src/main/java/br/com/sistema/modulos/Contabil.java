package br.com.sistema.modulos;
import br.com.sistema.consumidor.LogDTO;
import br.com.sistema.produtor.EnviarLog;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Contabil {
    public static void registrarLancamento(Long id, String descricao, Double valor, String tipo, String usuario) {
        String sql = "INSERT INTO tb_contabilidade (descricao, valor, tipo) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, descricao);
            stmt.setDouble(2, valor);
            stmt.setString(3, tipo.toUpperCase());
            stmt.executeUpdate();
            System.out.println("[BANCO OPERACIONAL] Lançamento gravado em tb_contabilidade!");
        } catch (SQLException e) {
            System.err.println("Erro contábil: " + e.getMessage());
        }

        LogDTO log = new LogDTO();
        log.setId(id); log.setModulo("Contabilidade"); log.setAcao("LANCAMENTO_" + tipo + ": " + descricao);
        log.setUsuario(usuario); log.setPreco(valor);
        log.setData(LocalDate.now().toString()); log.setTimestamp(LocalDateTime.now().toString());
        EnviarLog.dispararLog(log);
    }
}