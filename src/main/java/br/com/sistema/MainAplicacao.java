package br.com.sistema;

import br.com.sistema.consumidor.ReceberLog;
import br.com.sistema.modulos.ModulosService;
import java.util.Scanner;

public class MainAplicacao {

    public static void main(String[] args) {

        new Thread(() -> ReceberLog.main(new String[]{})).start();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        Scanner scanner = new Scanner(System.in);
        ModulosService service = new ModulosService();

        System.out.print("\nInforme seu nome de usuário: ");
        String usuario = scanner.nextLine();

        System.out.print("Informe seu perfil (Dono, ADM, Vendedor): ");
        String perfil = scanner.nextLine().trim();

        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n=== SISTEMA DE CONCESSIONÁRIA INTEGRADO ===");
            System.out.println("Usuário: " + usuario + " | Perfil: " + perfil);
            System.out.println("-------------------------------------------");
            System.out.println("1. [Módulo Carro] Cadastrar Veículo");
            System.out.println("2. [Módulo Vendas] Registrar Venda");
            System.out.println("3. [Módulo TesteDrive] Agendar Teste Drive");
            System.out.println("4. [Módulo Contabilidade] Lançar Fechamento (Restrito: Dono)");
            System.out.println("5. [Módulo Usuario] Cadastrar Novo Usuário (Restrito: Dono/ADM)");
            System.out.println("6. [Módulo ADM] Alterar Configurações (Restrito: Dono/ADM)");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    System.out.print("Modelo do veículo: ");
                    String modelo = scanner.nextLine();
                    System.out.print("Ano: ");
                    int ano = Integer.parseInt(scanner.nextLine());
                    System.out.print("Preço (R$): ");
                    double preco = Double.parseDouble(scanner.nextLine());

                    service.cadastrarCarro(System.currentTimeMillis(), modelo, ano, preco, usuario);
                    break;

                case 2:
                    if ("ADM".equalsIgnoreCase(perfil)) {
                        System.out.println("Acesso Negado: Perfil ADM não possui permissão para registrar vendas.");
                        break;
                    }
                    System.out.print("Nome do Cliente: ");
                    String clienteVenda = scanner.nextLine();
                    System.out.print("Valor Total (R$): ");
                    double valorVenda = Double.parseDouble(scanner.nextLine());

                    service.realizarVenda(System.currentTimeMillis(), clienteVenda, valorVenda, usuario);
                    break;

                case 3:
                    System.out.print("Nome do Cliente: ");
                    String clienteTD = scanner.nextLine();
                    System.out.print("Modelo para Teste: ");
                    String modeloTD = scanner.nextLine();

                    service.agendarTesteDrive(System.currentTimeMillis(), clienteTD, modeloTD, "2026-11-05 14:00", usuario);
                    break;

                case 4:
                    if (!"Dono".equalsIgnoreCase(perfil)) {
                        System.out.println("Acesso Negado: Apenas o perfil 'Dono' pode realizar lançamentos contábeis.");
                        break;
                    }
                    System.out.print("Descrição do Lançamento: ");
                    String desc = scanner.nextLine();
                    System.out.print("Valor (R$): ");
                    double valorContabil = Double.parseDouble(scanner.nextLine());

                    service.lancarFechamento(System.currentTimeMillis(), desc, valorContabil, "RECEITA", usuario);
                    break;

                case 5:
                    if ("Vendedor".equalsIgnoreCase(perfil)) {
                        System.out.println("Acesso Negado: Vendedores não têm permissão para criar usuários.");
                        break;
                    }
                    System.out.print("Nome do Novo Usuário: ");
                    String nomeUser = scanner.nextLine();
                    System.out.print("Email: ");
                    String emailUser = scanner.nextLine();
                    System.out.print("Perfil (Vendedor/ADM): ");
                    String perfilUser = scanner.nextLine();

                    service.criarUsuario(System.currentTimeMillis(), nomeUser, emailUser, perfilUser, usuario);
                    break;

                case 6:
                    if (!"Dono".equalsIgnoreCase(perfil) && !"ADM".equalsIgnoreCase(perfil)) {
                        System.out.println("Acesso Negado: Apenas perfil Dono/ADM pode alterar parâmetros de sistema.");
                        break;
                    }

                    System.out.println("\n--- PARÂMETROS DO SISTEMA DISPONÍVEIS ---");
                    System.out.println("1. TEMPO_SESSAO_MINUTOS");
                    System.out.println("2. PERCENTUAL_DESCONTO_MAXIMO");
                    System.out.println("3. MARGEM_LUCRO_PADRAO");
                    System.out.print("Escolha o parâmetro (1-3): ");

                    String paramOpcao = scanner.nextLine();
                    String paramNome = switch (paramOpcao) {
                        case "1" -> "TEMPO_SESSAO_MINUTOS";
                        case "2" -> "PERCENTUAL_DESCONTO_MAXIMO";
                        case "3" -> "MARGEM_LUCRO_PADRAO";
                        default -> "PARAMETRO_CUSTOMIZADO";
                    };

                    System.out.print("Novo Valor para '" + paramNome + "': ");
                    String valParam = scanner.nextLine();

                    service.alterarConfiguracao(System.currentTimeMillis(), paramNome, valParam, usuario, perfil);
                    break;

                case 0:
                    System.out.println("Encerrando a aplicação...");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        }
        scanner.close();
        System.exit(0);
    }
}