package br.com.sistema.modulos;

public class ModulosService {

    // 1. MÓDULO CARRO
    public void cadastrarCarro(Long id, String modelo, Integer ano, Double preco, String usuario) {
        Carro.cadastrarVeiculo(id, modelo, ano, preco, usuario);
    }

    // 2. MÓDULO VENDAS
    public void realizarVenda(Long idVenda, String cliente, Double valorTotal, String usuario) {
        Vendas.registrarVenda(idVenda, cliente, valorTotal, usuario);
    }

    // 3. MÓDULO TESTE DRIVE
    public void agendarTesteDrive(Long idAgenda, String cliente, String modeloCarro, String dataHora, String usuario) {
        TesteDrive.agendar(idAgenda, cliente, modeloCarro, dataHora, usuario);
    }

    // 4. MÓDULO CONTABILIDADE
    public void lancarFechamento(Long idLancamento, String descricao, Double valor, String tipo, String usuario) {
        Contabil.registrarLancamento(idLancamento, descricao, valor, tipo, usuario);
    }

    // 5. MÓDULO USUÁRIO
    public void criarUsuario(Long idUsuario, String nomeNovoUsuario, String email, String perfil, String usuarioLogado) {
        Usuario.criarUsuario(idUsuario, nomeNovoUsuario, email, perfil, usuarioLogado);
    }

    // 6. MÓDULO ADM
    public void alterarConfiguracao(Long idConfig, String parametro, String valor, String usuario, String perfil) {
        Adm.alterarParametro(idConfig, parametro, valor, usuario, perfil);
    }
}