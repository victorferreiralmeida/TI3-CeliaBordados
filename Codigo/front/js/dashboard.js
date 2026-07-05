// Dashboard API e Funções
const DashboardAPI = {
    // Buscar todos os dados do dashboard
    getDashboardData: async function() {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/dashboard`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${admin.token}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Erro ao buscar dados do dashboard');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar dados do dashboard:', error);
            throw error;
        }
    },
    
    // Buscar pedidos hoje
    getPedidosHoje: async function() {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/dashboard/pedidos-hoje`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${admin.token}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Erro ao buscar pedidos de hoje');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar pedidos de hoje:', error);
            throw error;
        }
    },
    
    // Buscar receita mensal
    getReceitaMensal: async function() {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/dashboard/receita-mensal`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${admin.token}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Erro ao buscar receita mensal');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar receita mensal:', error);
            throw error;
        }
    },
    
    // Buscar clientes novos
    getClientesNovos: async function() {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/dashboard/clientes-novos`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${admin.token}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Erro ao buscar clientes novos');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar clientes novos:', error);
            throw error;
        }
    },
    
    // Buscar itens em estoque
    getItensEstoque: async function() {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/dashboard/itens-estoque`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${admin.token}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Erro ao buscar itens em estoque');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar itens em estoque:', error);
            throw error;
        }
    },
    
    // Buscar vendas por categoria
    getVendasPorCategoria: async function() {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/dashboard/vendas-por-categoria`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${admin.token}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Erro ao buscar vendas por categoria');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar vendas por categoria:', error);
            throw error;
        }
    },
    
    // Buscar vendas mensais
    getVendasMensais: async function() {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/dashboard/vendas-mensais`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${admin.token}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Erro ao buscar vendas mensais');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar vendas mensais:', error);
            throw error;
        }
    },
    
    // Buscar pedidos recentes
    getPedidosRecentes: async function() {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/dashboard/pedidos-recentes`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${admin.token}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Erro ao buscar pedidos recentes');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar pedidos recentes:', error);
            throw error;
        }
    },
    
    // Buscar pedido por ID
    getPedidoById: async function(id) {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/dashboard/pedidos/${id}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${admin.token}`
                }
            });
            
            if (!response.ok) {
                throw new Error(`Erro ao buscar pedido #${id}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error(`Erro ao buscar pedido #${id}:`, error);
            throw error;
        }
    },
    
    // Buscar dados de engajamento dos produtos
    getEngajamentoProdutos: async function() {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/dashboard/engajamento-produtos`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${admin.token}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Erro ao buscar dados de engajamento dos produtos');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar dados de engajamento dos produtos:', error);
            throw error;
        }
    }
};

// Funções para atualizar a UI
const DashboardUI = {
    // Atualizar estatísticas
    atualizarEstatisticas: function(data) {
        // Pedidos hoje
        const pedidosHoje = document.getElementById('pedidos-hoje');
        if (pedidosHoje) pedidosHoje.textContent = data.pedidosHoje || 0;
        
        // Receita mensal
        const receitaMensal = document.getElementById('receita-mensal');
        if (receitaMensal) receitaMensal.textContent = `R$ ${(data.receitaMensal || 0).toFixed(2).replace('.', ',')}`;
        
        // Clientes novos
        const clientesNovos = document.getElementById('clientes-novos');
        if (clientesNovos) clientesNovos.textContent = data.clientesNovos || 0;
        
        // Itens em estoque
        const itensEstoque = document.getElementById('itens-estoque');
        if (itensEstoque) itensEstoque.textContent = data.itensEmEstoque || 0;
    },
    
    // Atualizar gráfico de vendas por categoria
    atualizarGraficoVendasPorCategoria: function(vendasPorCategoria) {
        const chartContainer = document.getElementById('vendas-categoria-container');
        if (!chartContainer) return;
        chartContainer.innerHTML = '<canvas id="vendasPorCategoriaChart"></canvas>';
        
        const ctx = document.getElementById('vendasPorCategoriaChart').getContext('2d');
        new Chart(ctx, {
            type: 'pie',
            data: {
                labels: Object.keys(vendasPorCategoria),
                datasets: [{
                    data: Object.values(vendasPorCategoria),
                    backgroundColor: [
                        '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'right',
                    },
                    title: {
                        display: true,
                        text: 'Vendas por Categoria'
                    }
                }
            }
        });
    },
    
    // Atualizar gráfico de vendas mensais
    atualizarGraficoVendasMensais: function(vendasMensais) {
        const chartContainer = document.getElementById('vendas-mensais-container');
        if (!chartContainer) return;
        chartContainer.innerHTML = '<canvas id="vendasMensaisChart"></canvas>';
        
        const ctx = document.getElementById('vendasMensaisChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: Object.keys(vendasMensais),
                datasets: [{
                    label: 'Vendas (R$)',
                    data: Object.values(vendasMensais),
                    backgroundColor: '#36A2EB',
                    borderColor: '#2980b9',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return 'R$ ' + value.toFixed(2).replace('.', ',');
                            }
                        }
                    }
                },
                plugins: {
                    title: {
                        display: true,
                        text: 'Vendas Mensais'
                    }
                }
            }
        });
    },
    
    // Atualizar tabela de pedidos recentes usando IDs
    atualizarPedidosRecentes: async function(pedidoIds) {
        const tbody = document.getElementById('pedidos-recentes-tabela');
        if (!tbody) {
            console.warn('Elemento pedidos-recentes-tabela não encontrado');
            return;
        }
        
        tbody.innerHTML = '';
        
        if (!pedidoIds || !pedidoIds.length) {
            tbody.innerHTML = '<tr><td colspan="6">Nenhum pedido recente encontrado</td></tr>';
            return;
        }
        
        // Mapeamento de status do enum para texto amigável
        const statusMap = {
            'PENDENTE': 'Pendente',
            'PAGO': 'Pago',
            'CANCELADO': 'Cancelado',
            'PROCESSANDO': 'Processando',
            'EM_PRODUCAO': 'Em Produção',
            'ENVIADO': 'Enviado',
            'ENTREGUE': 'Entregue',
            'CONCLUIDO': 'Concluído'
        };
        
        try {
            // Buscar detalhes de cada pedido por ID
            for (const id of pedidoIds) {
                try {
                    const pedido = await DashboardAPI.getPedidoById(id);
                    
                    if (!pedido) continue;
                    
                    // Formatar a data
                    const data = new Date(pedido.dataPedido);
                    const dataFormatada = `${String(data.getDate()).padStart(2, '0')}/${String(data.getMonth() + 1).padStart(2, '0')}/${data.getFullYear()}`;
                    
                    // Determinar o status e o texto amigável
                    const statusTexto = statusMap[pedido.status] || pedido.status;
                    let statusClass = '';
                    switch(pedido.status) {
                        case 'PENDENTE': statusClass = 'pending'; break;
                        case 'PAGO': statusClass = 'completed'; break;
                        case 'PROCESSANDO': statusClass = 'processing'; break;
                        case 'EM_PRODUCAO': statusClass = 'processing'; break;
                        case 'ENVIADO': statusClass = 'shipped'; break;
                        case 'ENTREGUE': statusClass = 'delivered'; break;
                        case 'CONCLUIDO': statusClass = 'completed'; break;
                        case 'CANCELADO': statusClass = 'cancelled'; break;
                        default: statusClass = 'processing'; break;
                    }
                    
                    // Obter o primeiro item do pedido para exibição
                    const primeiroItem = pedido.itens && pedido.itens.length > 0 ? pedido.itens[0].produto.nome : 'Produto não especificado';
                    
                    // Adicionar a linha na tabela
                    tbody.innerHTML += `
                        <tr>
                            <td>#${pedido.id}</td>
                            <td>${pedido.cliente.nome}</td>
                            <td>${primeiroItem}</td>
                            <td>R$ ${pedido.total.toFixed(2).replace('.', ',')}</td>
                            <td>${dataFormatada}</td>
                            <td><span class="status ${statusClass}">${statusTexto}</span></td>
                        </tr>
                    `;
                } catch (err) {
                    console.error(`Erro ao processar pedido #${id}:`, err);
                }
            }
        } catch (error) {
            console.error('Erro ao atualizar pedidos recentes:', error);
            tbody.innerHTML = '<tr><td colspan="6">Erro ao carregar pedidos recentes</td></tr>';
        }
    },
    
    // Atualizar gráfico de produtos mais visitados
    atualizarGraficoProdutosVisitados: function(produtosVisitados) {
        // Ordenar produtos por número de visualizações (decrescente)
        const produtosOrdenados = Object.entries(produtosVisitados)
            .sort((a, b) => b[1] - a[1])
            .slice(0, 5); // Pegar os 5 mais visitados
        
        const produtos = produtosOrdenados.map(p => p[0]); // Nomes dos produtos
        const visualizacoes = produtosOrdenados.map(p => p[1]); // Número de visualizações
        
        const chartContainer = document.getElementById('produtos-visitados-container');
        chartContainer.innerHTML = '<canvas id="produtosVisitadosChart"></canvas>';
        
        const ctx = document.getElementById('produtosVisitadosChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: produtos,
                datasets: [{
                    label: 'Número de Visualizações',
                    data: visualizacoes,
                    backgroundColor: 'rgba(255, 99, 132, 0.8)',
                    borderColor: 'rgba(255, 99, 132, 1)',
                    borderWidth: 2,
                    borderRadius: 5,
                    barThickness: 40,
                    maxBarThickness: 50
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0,
                            font: {
                                size: 12
                            }
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.1)'
                        }
                    },
                    x: {
                        ticks: {
                            font: {
                                size: 12
                            }
                        },
                        grid: {
                            display: false
                        }
                    }
                },
                plugins: {
                    title: {
                        display: true,
                        text: 'Produtos Mais Visitados',
                        font: {
                            size: 16,
                            weight: 'bold'
                        },
                        padding: 20
                    },
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        titleFont: {
                            size: 14
                        },
                        bodyFont: {
                            size: 13
                        },
                        callbacks: {
                            label: function(context) {
                                return `Visualizações: ${context.raw}`;
                            }
                        }
                    }
                },
                animation: {
                    duration: 2000,
                    easing: 'easeInOutQuart'
                }
            }
        });
    },
    
    // Atualizar gráfico de taxa de conversão
    atualizarGraficoTaxaConversao: function(taxaConversao) {
        // Ordenar produtos por taxa de conversão (decrescente)
        const produtosOrdenados = Object.entries(taxaConversao)
            .sort((a, b) => b[1] - a[1])
            .slice(0, 5); // Pegar os 5 com maior conversão
        
        const produtos = produtosOrdenados.map(p => p[0]); // Nomes dos produtos
        const taxas = produtosOrdenados.map(p => (p[1] * 100)); // Taxas de conversão
        
        const chartContainer = document.getElementById('taxa-conversao-container');
        chartContainer.innerHTML = '<canvas id="taxaConversaoChart"></canvas>';
        
        const ctx = document.getElementById('taxaConversaoChart').getContext('2d');
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: produtos,
                datasets: [{
                    label: 'Taxa de Conversão (%)',
                    data: taxas,
                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 3,
                    tension: 0.4,
                    fill: true,
                    pointBackgroundColor: 'rgba(54, 162, 235, 1)',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2,
                    pointRadius: 6,
                    pointHoverRadius: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                        ticks: {
                            callback: function(value) {
                                return value + '%';
                            },
                            font: {
                                size: 12
                            }
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.1)'
                        }
                    },
                    x: {
                        ticks: {
                            font: {
                                size: 12
                            }
                        },
                        grid: {
                            display: false
                        }
                    }
                },
                plugins: {
                    title: {
                        display: true,
                        text: 'Taxa de Conversão por Produto',
                        font: {
                            size: 16,
                            weight: 'bold'
                        },
                        padding: 20
                    },
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        titleFont: {
                            size: 14
                        },
                        bodyFont: {
                            size: 13
                        },
                        callbacks: {
                            label: function(context) {
                                return `Taxa de Conversão: ${context.raw.toFixed(1)}%`;
                            }
                        }
                    }
                },
                animation: {
                    duration: 2000,
                    easing: 'easeInOutQuart'
                }
            }
        });
    },
      // Atualizar tabela de produtos populares
    atualizarTabelaProdutosPopulares: function(produtosPopulares, taxaConversao) {
        const tbody = document.getElementById('produtos-populares-tabela');
        tbody.innerHTML = '';
        
        if (!produtosPopulares || !produtosPopulares.length) {
            tbody.innerHTML = '<tr><td colspan="6">Nenhum dado de produto encontrado</td></tr>';
            return;
        }
        
        // Ordenar por número de visualizações (decrescente)
        produtosPopulares.sort((a, b) => b.visualizacoes - a.visualizacoes);
        
        produtosPopulares.forEach(produto => {
            // Buscar taxa de conversão pelo nome do produto
            const conversao = taxaConversao && taxaConversao[produto.nome] !== undefined
                ? (taxaConversao[produto.nome] * 100).toFixed(1) + '%'
                : '0%';
            // Exibir adições ao carrinho corretamente
            const adicoes = produto.adicoesCarrinho !== undefined ? produto.adicoesCarrinho : 0;
            // Obter média de avaliações e número de comentários
            const mediaAvaliacoes = produto.mediaAvaliacoes !== undefined ? produto.mediaAvaliacoes : 'N/A';
            const numeroComentarios = produto.numeroComentarios !== undefined ? produto.numeroComentarios : 0;

            tbody.innerHTML += `
                <tr>
                    <td>${produto.nome}</td>
                    <td>${produto.visualizacoes}</td>
                    <td>${adicoes}</td>
                    <td>${conversao}</td>
                    <td>${mediaAvaliacoes}</td>
                    <td>${numeroComentarios}</td>
                </tr>
            `;
        });
    },
    
    // Atualizar a seção de engajamento dos produtos
    atualizarEngajamentoProdutos: async function() {
        try {
            const dados = await DashboardAPI.getEngajamentoProdutos();
            this.atualizarGraficoProdutosVisitados(dados.produtosVisitados);
            this.atualizarGraficoTaxaConversao(dados.taxaConversao);
            // Passar taxaConversao como segundo parâmetro
            this.atualizarTabelaProdutosPopulares(dados.produtosPopulares, dados.taxaConversao);
        } catch (error) {
            console.error('Erro ao carregar dados de engajamento dos produtos:', error);
            if (document.getElementById('produtos-visitados-container')) {
                document.getElementById('produtos-visitados-container').innerHTML = '<p class="error-message">Erro ao carregar gráfico.</p>';
            }
            if (document.getElementById('taxa-conversao-container')) {
                document.getElementById('taxa-conversao-container').innerHTML = '<p class="error-message">Erro ao carregar gráfico.</p>';
            }            if (document.getElementById('produtos-populares-tabela')) {
                document.getElementById('produtos-populares-tabela').innerHTML = '<tr><td colspan="6">Erro ao carregar dados de produtos populares</td></tr>';
            }
        }
    },
    
    // Carregar todos os dados do dashboard
    carregarDashboard: async function() {
        try {
            const data = await DashboardAPI.getDashboardData();
            
            // Verificar se os elementos existem antes de atualizar
            if (document.getElementById('pedidos-hoje')) {
                this.atualizarEstatisticas(data);
            }
            
            if (document.getElementById('vendas-categoria-container')) {
                this.atualizarGraficoVendasPorCategoria(data.vendasPorCategoria);
            }
            
            if (document.getElementById('vendas-mensais-container')) {
                this.atualizarGraficoVendasMensais(data.vendasMensais);
            }
            
            if (document.getElementById('pedidos-recentes-tabela')) {
                this.atualizarPedidosRecentes(data.pedidosRecentesIds);
            }
            
            this.atualizarEngajamentoProdutos();
        } catch (error) {
            console.error('Erro ao carregar dashboard:', error);
            alert('Não foi possível carregar os dados do dashboard. Por favor, tente novamente mais tarde.');
        }
    }
};

// Inicializar dashboard quando a página carregar
document.addEventListener('DOMContentLoaded', function() {
    // Verificar se Chart.js está carregado
    if (typeof Chart === 'undefined') {
        console.error('Chart.js não está carregado. Por favor, adicione a biblioteca na página.');
        return;
    }
    
    // Inicializar o dashboard
    DashboardUI.carregarDashboard();
});