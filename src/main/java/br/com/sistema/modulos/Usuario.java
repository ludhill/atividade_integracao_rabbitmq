package br.com.sistema.modulos;
import br.com.sistema.consumidor.LogDTO;
import br.com.sistema.produtor.EnviarLog;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Usuario {
    public static void criarUsuario(Long id, String nome, String email, String perfil, String usuarioLogado) {
        String sql = "INSERT INTO tb_usuario (nome, email, perfil) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, perfil);
            stmt.executeUpdate();
            System.out.println("[BANCO OPERACIONAL] Usuário gravado em tb_usuario!");
        } catch (SQLException e) {
            System.err.println("Erro ao criar usuário: " + e.getMessage());
        }

        LogDTO log = new LogDTO();
        log.setId(id); log.setModulo("Usuario"); log.setAcao("CRIAR_USUARIO: " + nome);
        log.setUsuario(usuarioLogado);
        log.setData(LocalDate.now().toString()); log.setTimestamp(LocalDateTime.now().toString());
        EnviarLog.dispararLog(log);
    }
}