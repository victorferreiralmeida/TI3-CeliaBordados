# Inserts Manuais no Banco de Dados

Este documento descreve os inserts manuais realizados no banco de dados, explicando cada operação e incluindo o comando SQL correspondente.

## Ao fazer os inserts manualmente, recomendo seguir a ordem:

**"Clientes" → "Produtos" → "Pedidos" → "Pedido-itens"**

---

## Insert 1 - Inserção de Clientes

**Descrição:**  
Inserção de clientes de teste no banco de dados, cada um com email, endereço, nome, senha e telefone. Essas entradas são usadas para simular uma base inicial de usuários.

**Comando SQL:**
```sql
INSERT INTO clientes (data_cadastro, email, endereco_completo, nome, senha, telefone) VALUES
('2021-03-15 14:23:00', 'mourafernando@correa.com', 'Rua Fernandes, 852, Vila Velha, ES', 'Fernando Moura', 'z3mC1!vK', '(27) 99412-5487'),
('2023-07-08 09:12:00', 'oliveiraana@uol.com.br', 'Rua Azevedo, 41, Campinas, SP', 'Ana Oliveira', 'k9L*e2wA', '(19) 98245-1133'),
('2022-01-19 17:45:00', 'pereirajoao@gmail.com', 'Avenida Atlântica, 377, Rio de Janeiro, RJ', 'João Pereira', 'fR6#lMn8', '(21) 99874-6200'),
('2024-05-03 11:30:00', 'souzacarla@terra.com.br', 'Rua São Pedro, 812, Salvador, BA', 'Carla Souza', 'uP2@r7Tf', '(71) 99652-7433'),
('2020-11-25 16:10:00', 'diaslucas@yahoo.com.br', 'Rua Goiás, 1055, Belo Horizonte, MG', 'Lucas Dias', 'nQ1!f3Lp', '(31) 99581-3671'),
('2022-09-12 08:55:00', 'barbarasilva@outlook.com', 'Rua dos Andradas, 234, Porto Alegre, RS', 'Bárbara Silva', 'xW7$o3Vs', '(51) 99725-8765'),
('2023-02-20 13:40:00', 'costaeduardo@hotmail.com', 'Rua XV de Novembro, 763, Curitiba, PR', 'Eduardo Costa', 'dS5@k1Hy', '(41) 99463-7801'),
('2021-06-18 10:05:00', 'almeidaisabela@gmail.com', 'Rua Floriano, 980, Recife, PE', 'Isabela Almeida', 'eZ4&l9Rm', '(81) 99122-4789'),
('2022-10-23 15:20:00', 'ribeirocamila@live.com', 'Rua Independência, 388, Fortaleza, CE', 'Camila Ribeiro', 'mY6!n3Px', '(85) 99367-1894'),
('2024-04-14 09:00:00', 'goncalvesmarcos@ig.com.br', 'Rua Santos Dumont, 142, Natal, RN', 'Marcos Gonçalves', 'pU8#t5Jc', '(84) 99473-6002'),
('2020-12-05 18:45:00', 'silvapatricia@gmail.com', 'Rua Amazonas, 654, Cuiabá, MT', 'Patrícia Silva', 'bG3$y7Kd', '(65) 99815-2307'),
('2023-03-28 12:30:00', 'machadopaulo@globo.com', 'Rua Olavo Bilac, 412, João Pessoa, PB', 'Paulo Machado', 'zJ9&k6Vu', '(83) 99314-8461'),
('2021-07-17 11:15:00', 'fernandesaline@yahoo.com.br', 'Rua da Paz, 789, Aracaju, SE', 'Aline Fernandes', 'fN7^x2Rm', '(79) 99263-1995'),
('2022-05-06 10:10:00', 'cardosomateus@hotmail.com', 'Rua Rui Barbosa, 237, Campo Grande, MS', 'Mateus Cardoso', 'qT4%j3Wx', '(67) 99724-6339'),
('2024-08-11 14:25:00', 'rodrigueslara@icloud.com', 'Rua Paraíba, 302, Belém, PA', 'Lara Rodrigues', 'wC1#u8Tz', '(91) 99186-5022'),
('2023-09-15 16:50:00', 'castroluana@yahoo.com.br', 'Rua Ipiranga, 212, Manaus, AM', 'Luana Castro', 'hD2&g7Qt', '(92) 99154-7480'),
('2022-12-22 19:05:00', 'araujoandre@terra.com.br', 'Rua Barão do Rio Branco, 123, Palmas, TO', 'André Araújo', 'rL8$e5Yf', '(63) 99217-2241'),
('2021-10-10 07:40:00', 'mendesjuliana@uol.com.br', 'Rua das Palmeiras, 431, Florianópolis, SC', 'Juliana Mendes', 'sT3^l4Mp', '(48) 99874-6130'),
('2020-09-30 20:30:00', 'limaadriana@gmail.com', 'Rua Marquês de Pombal, 678, Boa Vista, RR', 'Adriana Lima', 'vB6#p3Df', '(95) 99123-5504'),
('2023-04-25 11:55:00', 'mendescarlos@ig.com.br', 'Rua Dom Pedro II, 845, Porto Velho, RO', 'Carlos Mendes', 'yG2!r8Xv', '(69) 99415-2031'),
('2021-01-03 18:20:00', 'dantasrafael@live.com', 'Rua Osvaldo Cruz, 59, Macapá, AP', 'Rafael Dantas', 'kH9&t3Wp', '(96) 99748-9182'),
('2024-02-27 09:45:00', 'teixeirafernanda@bol.com.br', 'Rua Getúlio Vargas, 1120, Teresina, PI', 'Fernanda Teixeira', 'cM4!v9Ft', '(86) 99325-1174'),
('2022-06-14 08:35:00', 'vieiramonica@globo.com', 'Rua São João, 903, São Luís, MA', 'Mônica Vieira', 'jA7%r1Nk', '(98) 99178-4413'),
('2020-10-08 14:50:00', 'freitastatiane@gmail.com', 'Rua Tiradentes, 145, Vitória, ES', 'Tatiane Freitas', 'eU6#o5Yk', '(27) 99431-6029'),
('2023-11-19 13:10:00', 'rochasabrina@terra.com.br', 'Rua da Liberdade, 799, Goiânia, GO', 'Sabrina Rocha', 'bQ5^x7Hp', '(62) 99264-7788'),
('2021-05-01 17:30:00', 'souzaigor@outlook.com', 'Rua Domingos Martins, 623, Maceió, AL', 'Igor Souza', 'zN4&d3Pw', '(82) 99145-2366'),
('2022-02-13 12:45:00', 'costapaula@yahoo.com.br', 'Rua Nilo Peçanha, 246, João Monlevade, MG', 'Paula Costa', 'vJ8@l2Xe', '(31) 99467-1122'),
('2024-07-05 15:00:00', 'santanathais@uol.com.br', 'Rua Antônio Carlos, 377, São José dos Campos, SP', 'Thais Santana', 'dH7!t9Uv', '(12) 99812-5077'),
('2023-08-21 10:20:00', 'silveiramurilo@gmail.com', 'Rua Hugo Lisboa, 98, Blumenau, SC', 'Murilo Silveira', 'mT5^p6Ac', '(47) 99653-4201'),
('2020-06-27 19:15:00', 'brazalessandra@bol.com.br', 'Rua Padre Cícero, 55, Juazeiro do Norte, CE', 'Alessandra Braz', 'pK3@e1Zs', '(88) 99723-6014');
```

---

## Insert 2 - Inserção de Produtos

**Descrição:**  
Cadastro inicial de produtos disponíveis para venda, incluindo categoria, descrição, disponibilidade, imagem de apresentação, nome e preço base.

**Comando SQL:**
```sql
INSERT INTO produtos (categoria, descricao, disponivel, imagem_url, nome, preco_base) VALUES
('Acessórios', 'Bolsinha com toalhinha personalizada', true, 'https://i.imgur.com/w8Ers40.jpeg', 'Bolsinha com Toalhinha', 100.00),
('Acessórios', 'Bolsinha personalizada com bordados', true, 'https://i.imgur.com/mF4hCPt.jpeg', 'Bolsinha Personalizada', 100.00),
('Decoração', 'Bordados com temas de anime', true, 'https://i.imgur.com/iGOz1VU.jpeg', 'Bordados Anime', 100.00),
('Decoração', 'Bordados com desenhos variados', true, 'https://i.imgur.com/p1x26GN.jpeg', 'Bordados Desenhos Variados', 100.00),
('Bebê', 'Kit completo personalizado para bebê', true, 'https://i.imgur.com/T9AtFYu.jpeg', 'Kit Bebê Personalizado', 100.00),
('Bebê', 'Kit bebê com motivo jardim', true, 'https://i.imgur.com/DeHUs0I.jpeg', 'Kit Bebê - Motivo Jardim', 100.00),
('Decoração', 'Kit com temas natalinos', true, 'https://i.imgur.com/l5pRYhy.jpeg', 'Kit Natal', 100.00),
('Escolar', 'Estojo escolar com bordados personalizados', true, 'https://i.imgur.com/jeKUIym.jpeg', 'Estojo Escolar Personalizado', 100.00),
('Cozinha', 'Panos de copa com bordados diversos', true, 'https://i.imgur.com/OUPTYYJ.jpeg', 'Panos de Copa Bordados', 100.00),
('Cozinha', 'Panos de copa com bordados - Modelo 2', true, 'https://i.imgur.com/1mB8okq.jpeg', 'Panos de Copa Bordados - Modelo 2', 100.00),
('Esporte', 'Pano do galo', true, 'https://i.imgur.com/ihoONMq.jpeg', 'Panos Bordados - Times', 13.00),
('Esporte', 'Panos de copa com temas de times', true, 'https://i.imgur.com/4rRnNjZ.jpeg', 'Panos de Copa - Times', 100.00),
('Lembranças', 'Toalhinhas personalizadas para lembrancinhas', true, 'https://i.imgur.com/AmoDQP8.jpeg', 'Toalhinhas de Lembrancinha', 100.00),
('Profissional', 'Bordados para terapeuta integrativa', true, 'https://i.imgur.com/wq4bSga.jpeg', 'Bordados Terapeuta', 100.00);
```

---

## Insert 3 - Inserção de Pedidos

**Descrição:**  
Inserção de pedidos feitos pelos clientes. Cada pedido contém o ID do cliente, a data do pedido, o status (PENDENTE, PAGO, PROCESSANDO, ENVIADO, ENTREGUE, CONCLUIDO ou CANCELADO) e o valor total.

**Comando SQL:**
```sql
INSERT INTO pedidos (cliente_id, data_pedido, status, total, avaliado) VALUES
(1, CURRENT_TIMESTAMP - INTERVAL '30 days', 'PAGO', 250.00, false),
(2, CURRENT_TIMESTAMP - INTERVAL '28 days', 'PROCESSANDO', 320.50, false),
(3, CURRENT_TIMESTAMP - INTERVAL '25 days', 'ENVIADO', 150.75, false),
(4, CURRENT_TIMESTAMP - INTERVAL '23 days', 'ENTREGUE', 420.30, false),
(5, CURRENT_TIMESTAMP - INTERVAL '20 days', 'CONCLUIDO', 280.90, false),
(6, CURRENT_TIMESTAMP - INTERVAL '18 days', 'CANCELADO', 310.25, false),
(7, CURRENT_TIMESTAMP - INTERVAL '15 days', 'PAGO', 195.60, false),
(8, CURRENT_TIMESTAMP - INTERVAL '12 days', 'PROCESSANDO', 430.80, false),
(9, CURRENT_TIMESTAMP - INTERVAL '10 days', 'ENVIADO', 275.40, false),
(10, CURRENT_TIMESTAMP - INTERVAL '9 days', 'ENTREGUE', 320.10, false),
(11, CURRENT_TIMESTAMP - INTERVAL '8 days', 'CONCLUIDO', 190.30, false),
(12, CURRENT_TIMESTAMP - INTERVAL '7 days', 'PENDENTE', 315.75, false),
(13, CURRENT_TIMESTAMP - INTERVAL '6 days', 'PAGO', 260.45, false),
(14, CURRENT_TIMESTAMP - INTERVAL '5 days', 'PROCESSANDO', 485.20, false),
(15, CURRENT_TIMESTAMP - INTERVAL '4 days', 'ENVIADO', 340.90, false),
(16, CURRENT_TIMESTAMP - INTERVAL '3 days', 'ENTREGUE', 175.30, false),
(17, CURRENT_TIMESTAMP - INTERVAL '3 days', 'CONCLUIDO', 290.60, false),
(18, CURRENT_TIMESTAMP - INTERVAL '2 days', 'CANCELADO', 210.40, false),
(19, CURRENT_TIMESTAMP - INTERVAL '2 days', 'PAGO', 310.20, false),
(20, CURRENT_TIMESTAMP - INTERVAL '1 day', 'PROCESSANDO', 180.75, false),
(21, CURRENT_TIMESTAMP - INTERVAL '1 day', 'ENVIADO', 370.30, false),
(22, CURRENT_TIMESTAMP, 'ENTREGUE', 225.50, false),
(23, CURRENT_TIMESTAMP, 'CONCLUIDO', 410.65, false),
(24, CURRENT_TIMESTAMP - INTERVAL '45 days', 'PAGO', 295.70, false),
(25, CURRENT_TIMESTAMP - INTERVAL '60 days', 'PROCESSANDO', 340.25, false),
(26, CURRENT_TIMESTAMP - INTERVAL '75 days', 'ENVIADO', 180.90, false),
(27, CURRENT_TIMESTAMP - INTERVAL '90 days', 'ENTREGUE', 260.35, false),
(28, CURRENT_TIMESTAMP - INTERVAL '75 days', 'CONCLUIDO', 305.15, false),
(29, CURRENT_TIMESTAMP - INTERVAL '60 days', 'CANCELADO', 230.50, false),
(30, CURRENT_TIMESTAMP - INTERVAL '45 days', 'PENDENTE', 390.80, false);
```

---

## Insert 4 - Inserção de Itens de Pedido

**Descrição:**  
Inserção dos itens de cada pedido. Cada linha representa um item adquirido em um pedido específico, associando o pedido, o produto, a quantidade comprada e o preço unitário na ocasião.

**Comando SQL:**
```sql
INSERT INTO pedido_itens (pedido_id, produto_id, quantidade, preco_unitario) VALUES
(1, 1, 2, 75.00),
(1, 3, 1, 100.00),
(2, 2, 3, 85.50),
(2, 5, 1, 65.00),
(3, 4, 2, 45.25),
(3, 7, 1, 60.25),
(4, 6, 3, 90.10),
(4, 8, 2, 75.00),
(5, 9, 1, 110.90),
(5, 10, 2, 85.00),
(6, 1, 2, 75.00),
(6, 4, 2, 45.25),
(6, 7, 1, 60.25),
(7, 3, 1, 100.00),
(7, 5, 1, 65.00),
(7, 8, 1, 75.00),
(8, 2, 2, 85.50),
(8, 9, 1, 110.90),
(8, 10, 2, 85.00),
(9, 1, 1, 75.00),
(9, 6, 2, 90.10),
(9, 7, 1, 60.25),
(10, 3, 2, 100.00),
(10, 8, 1, 75.00),
(10, 10, 1, 85.00),
(11, 2, 1, 85.50),
(11, 5, 1, 65.00),
(12, 1, 2, 75.00),
(12, 4, 1, 45.25),
(12, 9, 1, 110.90),
(13, 3, 2, 100.00),
(13, 6, 1, 90.10),
(14, 7, 3, 60.25),
(14, 10, 3, 85.00),
(14, 2, 1, 85.50),
(15, 1, 2, 75.00),
(15, 8, 2, 75.00),
(15, 5, 1, 65.00),
(16, 4, 2, 45.25),
(16, 9, 1, 110.80),
(17, 3, 2, 100.00),
(17, 6, 1, 90.60),
(18, 2, 1, 85.50),
(18, 7, 2, 60.25),
(19, 1, 2, 75.00),
(19, 10, 2, 85.00),
(20, 5, 2, 65.00),
(20, 8, 1, 75.00),
(21, 3, 3, 100.00),
(21, 9, 1, 110.90),
(22, 2, 1, 85.50),
(22, 4, 2, 45.25),
(22, 6, 1, 90.10),
(23, 1, 2, 75.00),
(23, 7, 1, 60.25),
(23, 10, 2, 85.00),
(24, 3, 2, 100.00),
(24, 8, 1, 75.00),
(25, 5, 3, 65.00),
(25, 9, 1, 110.90),
(26, 2, 1, 85.50),
(26, 6, 1, 90.10),
(27, 1, 2, 75.00),
(27, 4, 1, 45.25),
(27, 10, 1, 85.00),
(28, 3, 2, 100.00),
(28, 7, 1, 60.25),
(28, 8, 1, 75.00),
(29, 5, 2, 65.00),
(29, 9, 1, 110.90),
(30, 1, 2, 75.00),
(30, 2, 1, 85.50),
(30, 6, 2, 90.10);
```

---

# Observações

- **Clientes**: Senhas foram geradas aleatoriamente para fins de teste.
- **Produtos**: Links de imagens são exemplos e estão hospedados no Imgur.
- **Pedidos**: Algumas datas de pedidos simulam intervalos variados para representar histórico.
- **Itens de Pedido**: Cada pedido contém de 2 a 3 produtos associados, simulando compras reais.

---
