// Função para carregar os produtos da API
async function carregarProdutos() {
    try {
        const produtos = await ProdutoAPI.listarProdutos();
        exibirProdutos(produtos);
    } catch (error) {
        console.error('Erro:', error);
        mostrarErro('Não foi possível carregar os produtos. Tente novamente mais tarde.');
    }
}

// Função para exibir os produtos na página
function exibirProdutos(produtos) {
    const container = document.querySelector('.row');
    if (!container) return;

    container.innerHTML = ''; // Limpa o conteúdo existente

    if (!produtos || produtos.length === 0) {
        mostrarErro('Nenhum produto disponível no momento.');
        return;
    }

    // Verificar se o cliente está logado
    const clienteLogado = JSON.parse(sessionStorage.getItem('clienteLogado'));
    const estaLogado = clienteLogado && clienteLogado.token;

    produtos.forEach(produto => {
        // Configurar botão de ação diferente dependendo do estado de login
        let botaoAcao;
        if (estaLogado) {
            botaoAcao = `<button class="btn btn-primary" onclick="adicionarAoCarrinho(${produto.id})">
                            Adicionar ao Carrinho
                        </button>`;
        } else {
            botaoAcao = `<button class="btn btn-primary" onclick="redirecionarParaLogin()">
                            Faça login para comprar
                        </button>`;
        }

        // Card do produto agora é clicável para abrir o modal
        const produtoHTML = `
            <div class="col-sm-6 col-md-4 col-lg-3">
                <div class="box produto-card" data-produto='${JSON.stringify(produto)}' style="cursor:pointer;">
                    <div class="img-box">
                        <img src="${produto.imagemUrl}" alt="${produto.nome}">
                    </div>
                    <div class="detail-box">
                        <h2>${produto.nome}</h2>
                        <p>Preço - R$ <span class="preco">${produto.precoBase.toFixed(2)}</span></p>
                        ${botaoAcao}
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += produtoHTML;
    });

    // Adiciona evento de clique para abrir o modal de detalhes
    document.querySelectorAll('.produto-card').forEach(card => {
        card.addEventListener('click', function(e) {
            // Evita conflito com o botão de adicionar ao carrinho
            if (e.target.tagName === 'BUTTON') return;
            const produto = JSON.parse(this.getAttribute('data-produto'));
            abrirModalDetalhesProduto(produto);
        });
    });
}

// Função para redirecionar para a página de login
function redirecionarParaLogin() {
    window.location.href = 'login.html';
}

// Função para adicionar ao carrinho
async function adicionarAoCarrinho(produtoId) {
    try {
        const clienteLogado = JSON.parse(sessionStorage.getItem('clienteLogado'));
        if (!clienteLogado || !clienteLogado.token) {
            alert('Você precisa estar logado para adicionar itens ao carrinho.');
            window.location.href = 'login.html';
            return;
        }

        // Registrar a adição ao carrinho
        const responseAdicao = await fetch(`${API_BASE_URL}/produtos/${produtoId}/adicionar-ao-carrinho`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${clienteLogado.token}`
            }
        });

        if (!responseAdicao.ok) {
            throw new Error('Erro ao registrar adição ao carrinho');
        }

        // Adicionar o produto ao carrinho
        const resultado = await ProdutoAPI.adicionarAoCarrinho(produtoId);
        console.log('Resposta da API:', resultado); // Debug
        
        // Usar a função de popup existente na página
        if (typeof mostrarPopup === 'function') {
            mostrarPopup("Produto adicionado ao carrinho!");
        } else {
            alert("Produto adicionado ao carrinho!");
        }
    } catch (error) {
        console.error('Erro detalhado:', error);
        alert(error.message || 'Erro ao adicionar ao carrinho. Por favor, tente novamente.');
    }
}

// Função para mostrar mensagem de erro
function mostrarErro(mensagem) {
    const container = document.querySelector('.row');
    if (!container) return;

    const errorHTML = `
        <div class="col-12 text-center">
            <div class="alert alert-danger" role="alert">
                ${mensagem}
            </div>
        </div>
    `;
    container.innerHTML = errorHTML;
}

// Função para mostrar popup
function mostrarPopup(mensagem) {
    // Implementar conforme necessário
    alert(mensagem);
}

// Função para abrir e preencher o modal de detalhes do produto com avaliações reais
async function abrirModalDetalhesProduto(produtoInicial) {
    try {
        const clienteLogado = JSON.parse(sessionStorage.getItem('clienteLogado'));
        const headers = {};
        if (clienteLogado && clienteLogado.token) {
            headers['Authorization'] = `Bearer ${clienteLogado.token}`;
        }

        // Busca os detalhes do produto novamente para registrar a visualização no backend
        const response = await fetch(`${API_BASE_URL}/produtos/${produtoInicial.id}`, {
            method: 'GET',
            headers: headers
        });

        if (!response.ok) {
            throw new Error('Erro ao buscar detalhes do produto e registrar visualização.');
        }

        const produto = await response.json();

        // Preenche os dados do produto
        document.getElementById('modalDetalhesProdutoLabel').textContent = produto.nome;
        document.getElementById('modalProdutoImagem').src = produto.imagemUrl;
        document.getElementById('modalProdutoImagem').alt = produto.nome;
        document.getElementById('modalProdutoDescricao').textContent = produto.descricao || 'Sem descrição disponível.';
        document.getElementById('modalProdutoPreco').textContent = produto.precoBase.toFixed(2);

        // Lógica do botão Adicionar ao Carrinho / Faça login para comprar
        const btnAdicionarAoCarrinhoModal = document.getElementById('btnAdicionarAoCarrinhoModal');

        if (clienteLogado && clienteLogado.token) {
            btnAdicionarAoCarrinhoModal.textContent = 'Adicionar ao Carrinho';
            btnAdicionarAoCarrinhoModal.onclick = () => {
                adicionarAoCarrinho(produto.id);
                $('#modalDetalhesProduto').modal('hide'); // Fechar modal após adicionar ao carrinho
            };
        } else {
            btnAdicionarAoCarrinhoModal.textContent = 'Faça login para comprar';
            btnAdicionarAoCarrinhoModal.onclick = () => {
                redirecionarParaLogin();
            };
        }

        // Buscar avaliações reais do backend
        fetch(`${API_BASE_URL}/avaliacoes/produto/${produto.id}`)
            .then(response => response.json())
            .then(avaliacoes => {
                const comentariosDiv = document.getElementById('modalProdutoComentarios');
                comentariosDiv.innerHTML = '';
                if (avaliacoes && avaliacoes.length > 0) {
                    // Calcular média
                    const media = (avaliacoes.reduce((acc, a) => acc + a.nota, 0) / avaliacoes.length).toFixed(1);
                    comentariosDiv.innerHTML += `<div class="mb-2"><span style="color: #ffc107;">${'★'.repeat(Math.round(media))}${'☆'.repeat(5-Math.round(media))}</span> Média: ${media} (${avaliacoes.length} avaliação(ões))</div>`;
                    avaliacoes.forEach(a => {
                        comentariosDiv.innerHTML += `<div style="background: #ffe4ec; border-radius: 6px; padding: 6px 10px; margin-bottom: 8px;">
                            <span style="font-weight: bold; color: #e3435e;">${a.cliente?.nome || 'Cliente'} - </span>
                            <span style="color: #ffc107;">${'★'.repeat(a.nota)}${'☆'.repeat(5-a.nota)}</span>
                            <div style="overflow-wrap: break-word;">${a.comentario}</div>
                        </div>`;
                    });
                } else {
                    comentariosDiv.innerHTML = '<div>Nenhum comentário ainda.</div>';
                }
            })
            .catch(() => {
                const comentariosDiv = document.getElementById('modalProdutoComentarios');
                comentariosDiv.innerHTML = '<div>Não foi possível carregar as avaliações.</div>';
            });

        // Abre o modal (usando jQuery/Bootstrap)
        $('#modalDetalhesProduto').modal('show');

    } catch (error) {
        console.error('Erro ao abrir modal de detalhes do produto:', error);
        alert('Não foi possível carregar os detalhes do produto. Tente novamente mais tarde.');
    }
}

// Adicionar evento de clique nas imagens dos produtos após carregá-los
function adicionarEventoCliqueImagens() {
    document.querySelectorAll('.img-produto').forEach(img => {
        img.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const produto = window.PRODUTOS.find(p => p.id == id);
            if (produto) abrirModalDetalhesProduto(produto);
        });
    });
}

// Carregar produtos quando a página for carregada
document.addEventListener('DOMContentLoaded', carregarProdutos); 