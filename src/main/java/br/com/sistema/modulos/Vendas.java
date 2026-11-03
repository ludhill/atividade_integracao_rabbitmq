package br.com.sistema.modulos;
import br.com.sistema.consumidor.LogDTO;
import br.com.sistema.produtor.EnviarLog;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Vendas {
    public static void registrarVenda(Long id, String cliente, Double valorTotal, String usuario) {
        String sql = "INSERT INTO tb_vendas (cliente, valor_total, data_venda) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente);
            stmt.setDouble(2, valorTotal);
            stmt.setString(3, LocalDate.now().toString());
            stmt.executeUpdate();
            System.out.println(" [BANCO OPERACIONAL] Venda gravada em tb_vendas!");
        } catch (SQLException e) {
            System.err.println(" Erro ao salvar venda: " + e.getMessage());
        }

        LogDTO log = new LogDTO();
        log.setId(id); log.setModulo("Vendas"); log.setAcao("FINALIZAR_VENDA: " + cliente);
        log.setUsuario(usuario); log.setPreco(valorTotal);
        log.setData(LocalDate.now().toString()); log.setTimestamp(LocalDateTime.now().toString());
        EnviarLog.dispararLog(log);
    }
}