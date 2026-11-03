package br.com.sistema.modulos;
import br.com.sistema.consumidor.LogDTO;
import br.com.sistema.produtor.EnviarLog;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Adm {
    public static void alterarParametro(Long id, String parametro, String valor, String usuario, String perfil) {
        if (!"Dono".equalsIgnoreCase(perfil) && !"ADM".equalsIgnoreCase(perfil)) {
            System.out.println("Acesso Negado: Apenas perfil Dono/ADM pode alterar parâmetros.");
            return;
        }

        String sql = "INSERT INTO tb_adm (parametro, valor) VALUES (?, ?) ON DUPLICATE KEY UPDATE valor = ?";
        try (Connection conn = ConexaoDB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, parametro);
            stmt.setString(2, valor);
            stmt.setString(3, valor);
            stmt.executeUpdate();
            System.out.println("[BANCO OPERACIONAL] Parâmetro gravado em tb_adm!");
        } catch (SQLException e) {
            System.err.println("Erro ADM: " + e.getMessage());
        }

        LogDTO log = new LogDTO();
        log.setId(id); log.setModulo("ADM"); log.setAcao("ALTERAR_PARAMETRO: " + parametro);
        log.setUsuario(usuario);
        log.setData(LocalDate.now().toString()); log.setTimestamp(LocalDateTime.now().toString());
        EnviarLog.dispararLog(log);
    }
}