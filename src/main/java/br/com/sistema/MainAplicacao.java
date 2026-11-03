package br.com.sistema;

import br.com.sistema.consumidor.ReceberLog;
import br.com.sistema.modulos.ModulosService;
import java.util.Scanner;

public class MainAplicacao {

    public static void main(String[] args) {
        // 1. INICIA O CONSUMIDOR (RECEBERLOG) EM SEGUNDO PLANO NA MESMA JVM
        new Thread(() -> {
            ReceberLog.main(new String[]{});
        }, "Thread-ReceberLog").start();

        // Aguarda 1 segundo para estabilizar a inicialização do consumidor
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        Scanner scanner = new Scanner(System.in);
        ModulosService service = new ModulosService();

        System.out.print("\nInforme seu nome de usuário: ");
        String usuario = scanner.nextLine();

        System.out.print("Informe seu perfil (ex: Dono, Vendedor, ADM): ");
        String perfil = scanner.nextLine();

        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n=== SISTEMA DE CONCESSIONÁRIA INTEGRADO ===");
            System.out.println("1. [Módulo Carro] Cadastrar Veículo");
            System.out.println("2. [Módulo Vendas] Registrar Venda");
            System.out.println("3. [Módulo TesteDrive] Agendar Teste Drive");
            System.out.println("4. [Módulo Contabilidade] Lançar Fechamento");
            System.out.println("5. [Módulo Usuario] Cadastrar Novo Usuário");
            System.out.println("6. [Módulo ADM] Alterar Configurações");
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
                    System.out.print("Descrição do Lançamento: ");
                    String desc = scanner.nextLine();
                    System.out.print("Valor (R$): ");
                    double valorContabil = Double.parseDouble(scanner.nextLine());

                    service.lancarFechamento(System.currentTimeMillis(), desc, valorContabil, "RECEITA", usuario);
                    break;

                case 5:
                    System.out.print("Nome do Novo Usuário: ");
                    String nomeUser = scanner.nextLine();
                    System.out.print("Email: ");
                    String emailUser = scanner.nextLine();
                    System.out.print("Perfil (Vendedor/ADM): ");
                    String perfilUser = scanner.nextLine();

                    service.criarUsuario(System.currentTimeMillis(), nomeUser, emailUser, perfilUser, usuario);
                    break;

                case 6:
                    System.out.print("Nome do Parâmetro ADM: ");
                    String param = scanner.nextLine();
                    System.out.print("Novo Valor: ");
                    String valParam = scanner.nextLine();

                    service.alterarConfiguracao(System.currentTimeMillis(), param, valParam, usuario, perfil);
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