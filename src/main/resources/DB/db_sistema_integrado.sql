-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 04-Nov-2026 às 15:59
-- Versão do servidor: 10.4.27-MariaDB
-- versão do PHP: 8.2.0
-- Atividade 04/11/2026

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `db_sistema_integrado`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `tb_adm`
--

CREATE TABLE `tb_adm` (
  `id` bigint(20) NOT NULL,
  `parametro` varchar(100) NOT NULL,
  `valor` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Extraindo dados da tabela `tb_adm`
--

INSERT INTO `tb_adm` (`id`, `parametro`, `valor`) VALUES
(1, 'LIMITE_DESCONTO_GERENTE', '15%'),
(2, 'TEMPO_SESSAO_MINUTOS', '30'),
(3, 'MODO_MANUTENCAO', 'DESATIVADO'),
(4, 'TEMPO_SESSAO_MINUTOS', '120');

-- --------------------------------------------------------

--
-- Estrutura da tabela `tb_carro`
--

CREATE TABLE `tb_carro` (
  `id` bigint(20) NOT NULL,
  `modelo` varchar(50) NOT NULL,
  `ano` int(11) NOT NULL,
  `preco` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Extraindo dados da tabela `tb_carro`
--

INSERT INTO `tb_carro` (`id`, `modelo`, `ano`, `preco`) VALUES
(1, 'Civic Touring', 2024, '150000.00'),
(2, 'Corolla XEi', 2023, '135000.00'),
(3, 'Onix Premier', 2022, '85000.00'),
(4, 'Maverik', 1960, '15000.00'),
(5, 'Corolla', 2024, '140000.00'),
(6, 'Maverick 1978 85000', 1978, '85000.00'),
(7, 'Uno', 1995, '30000.00'),
(8, 'fusca', 1975, '10000.00'),
(9, 'BMW', 2010, '150000.00');

-- --------------------------------------------------------

--
-- Estrutura da tabela `tb_contabilidade`
--

CREATE TABLE `tb_contabilidade` (
  `id` bigint(20) NOT NULL,
  `descricao` varchar(150) NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `tipo` enum('RECEITA','DESPESA') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Extraindo dados da tabela `tb_contabilidade`
--

INSERT INTO `tb_contabilidade` (`id`, `descricao`, `valor`, `tipo`) VALUES
(1, 'Venda Honda Civic 2024', '150000.00', 'RECEITA'),
(2, 'Manutenção de Frota', '4500.50', 'DESPESA'),
(3, 'Pagamento de Comissão Vendedores', '12000.00', 'DESPESA'),
(4, 'Combustivel', '500.00', 'RECEITA');

-- --------------------------------------------------------

--
-- Estrutura da tabela `tb_teste_drive`
--

CREATE TABLE `tb_teste_drive` (
  `id` bigint(20) NOT NULL,
  `cliente` varchar(100) NOT NULL,
  `modelo` varchar(50) NOT NULL,
  `data_agendamento` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Extraindo dados da tabela `tb_teste_drive`
--

INSERT INTO `tb_teste_drive` (`id`, `cliente`, `modelo`, `data_agendamento`) VALUES
(1, 'Carlos Andrade', 'Corolla Cross', '2026-11-05 14:00'),
(2, 'Fernanda Lima', 'Jeep Compass', '2026-11-06 10:30'),
(3, 'Roberto Souza', 'VW Nivus', '2026-11-07 16:00'),
(4, 'AlunoL', 'Corolla', '2026-11-05 14:00'),
(5, 'Junior', 'Fusca', '2026-11-05 14:00'),
(6, 'Joao', 'BMW', '2026-11-05 14:00');

-- --------------------------------------------------------

--
-- Estrutura da tabela `tb_usuario`
--

CREATE TABLE `tb_usuario` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `perfil` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Extraindo dados da tabela `tb_usuario`
--

INSERT INTO `tb_usuario` (`id`, `nome`, `email`, `perfil`) VALUES
(1, 'Lucas Silva', 'lucas@concessionaria.com', 'Dono'),
(2, 'Maria Santos', 'maria@concessionaria.com', 'ADM'),
(3, 'João Pedro', 'joao@concessionaria.com', 'Vendedor'),
(4, 'Carlos', 'carlos@email.com', 'Vendedor');

-- --------------------------------------------------------

--
-- Estrutura da tabela `tb_vendas`
--

CREATE TABLE `tb_vendas` (
  `id` bigint(20) NOT NULL,
  `cliente` varchar(100) NOT NULL,
  `valor_total` decimal(10,2) NOT NULL,
  `data_venda` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Extraindo dados da tabela `tb_vendas`
--

INSERT INTO `tb_vendas` (`id`, `cliente`, `valor_total`, `data_venda`) VALUES
(1, 'Ana Clara', '135000.00', '2026-10-28'),
(2, 'Bruno Mendes', '85000.00', '2026-10-30'),
(3, 'Camila Rodrigues', '150000.00', '2026-11-01'),
(4, 'Joao', '10000.00', '2026-11-04');

--
-- Índices para tabelas despejadas
--

--
-- Índices para tabela `tb_adm`
--
ALTER TABLE `tb_adm`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `tb_carro`
--
ALTER TABLE `tb_carro`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `tb_contabilidade`
--
ALTER TABLE `tb_contabilidade`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `tb_teste_drive`
--
ALTER TABLE `tb_teste_drive`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `tb_usuario`
--
ALTER TABLE `tb_usuario`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Índices para tabela `tb_vendas`
--
ALTER TABLE `tb_vendas`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `tb_adm`
--
ALTER TABLE `tb_adm`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de tabela `tb_carro`
--
ALTER TABLE `tb_carro`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de tabela `tb_contabilidade`
--
ALTER TABLE `tb_contabilidade`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de tabela `tb_teste_drive`
--
ALTER TABLE `tb_teste_drive`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de tabela `tb_usuario`
--
ALTER TABLE `tb_usuario`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de tabela `tb_vendas`
--
ALTER TABLE `tb_vendas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
