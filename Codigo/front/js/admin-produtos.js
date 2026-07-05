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
    const container = document.getElementById('produtos-container');
    if (!container) return;

    container.className = 'row'; // Deixa o container em grid
    container.innerHTML = '';

    if (!produtos || produtos.length === 0) {
        mostrarErro('Nenhum produto disponível no momento.');
        return;
    }

    produtos.forEach(produto => {
        const produtoHTML = `
            <div class="col-md-4 mb-4">
                <div class="box">
                    <div class="img-box">
                        <img src="${produto.imagemUrl}" alt="${produto.nome}" class="produto-img-admin">
                    </div>
                    <div class="detail-box">
                        <h2>${produto.nome}</h2>
                        <p>Preço - R$ <span class="preco">${produto.precoBase.toFixed(2)}</span></p>
                        <div class="admin-actions">
                            <button class="btn btn-warning" onclick="abrirPopupEditarProduto(${produto.id})">
                                <i class="fas fa-edit"></i> Editar
                            </button>
                            <button class="btn btn-danger" onclick="excluirProduto(${produto.id})">
                                <i class="fas fa-trash"></i> Excluir
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += produtoHTML;
    });
}

// Função para mostrar mensagem de erro
function mostrarErro(mensagem) {
    const container = document.getElementById('produtos-container');
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

// Função para abrir popup de adicionar produto
function abrirPopupAdicionarProduto() {
    const popup = document.getElementById('popup-carrinho');
    const mensagemPopup = document.getElementById('mensagem-popup');
    
    mensagemPopup.innerHTML = `
        <h4>Adicionar Produto</h4>
        <form id="form-adicionar-produto">
            <div class="form-group">
                <label for="novo-nome">Nome:</label>
                <input type="text" id="novo-nome" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="novo-descricao">Descrição:</label>
                <textarea id="novo-descricao" class="form-control" required></textarea>
            </div>
            <div class="form-group">
                <label for="novo-preco">Preço Base:</label>
                <input type="number" id="novo-preco" class="form-control" step="0.01" required>
            </div>
            <div class="form-group">
                <label for="novo-imagem">URL da Imagem:</label>
                <input type="text" id="novo-imagem" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="novo-categoria">Categoria:</label>
                <select id="novo-categoria" class="form-control" required>
                    <option value="towel">Toalhas</option>
                    <option value="apron">Aventais</option>
                    <option value="shirt">Camisas</option>
                    <option value="kitchen">Kit Cozinha</option>
                </select>
            </div>
            <div class="form-group text-right">
                <button type="button" class="btn btn-secondary" onclick="fecharPopup()">Cancelar</button>
                <button type="submit" class="btn btn-primary">Salvar</button>
            </div>
        </form>
    `;
    
    popup.classList.add('mostrar');
    
    // Adicionar evento de submit ao formulário
    document.getElementById('form-adicionar-produto').addEventListener('submit', async function(e) {
        e.preventDefault();
        await salvarNovoProduto();
    });
}

// Função para salvar novo produto
async function salvarNovoProduto() {
    try {
        const imagemUrl = document.getElementById('novo-imagem').value;
        // Validação simples de URL de imagem
        if (!/^https?:\/\/.+\.(jpg|jpeg|png|gif)$/i.test(imagemUrl)) {
            mostrarPopup('URL da imagem inválida. Use um link direto para uma imagem (jpg, png, jpeg ou gif).');
            return;
        }
        const produto = {
            nome: document.getElementById('novo-nome').value,
            descricao: document.getElementById('novo-descricao').value,
            precoBase: parseFloat(document.getElementById('novo-preco').value),
            imagemUrl: imagemUrl,
            categoria: document.getElementById('novo-categoria').value
        };
        
        await ProdutoAPI.adicionarProduto(produto);
        fecharPopup();
        carregarProdutos();
        mostrarPopup('Produto adicionado com sucesso!');
    } catch (error) {
        console.error('Erro:', error);
        mostrarPopup(error.message || 'Erro ao adicionar produto');
    }
}

// Função para abrir popup de editar produto
async function abrirPopupEditarProduto(id) {
    try {
        const produto = await ProdutoAPI.obterProduto(id);
        const popup = document.getElementById('popup-carrinho');
        const mensagemPopup = document.getElementById('mensagem-popup');
        
        mensagemPopup.innerHTML = `
            <h4>Editar Produto</h4>
            <form id="form-editar-produto">
                <div class="form-group">
                    <label for="edit-nome">Nome:</label>
                    <input type="text" id="edit-nome" class="form-control" value="${produto.nome}" required>
                </div>
                <div class="form-group">
                    <label for="edit-descricao">Descrição:</label>
                    <textarea id="edit-descricao" class="form-control" required>${produto.descricao}</textarea>
                </div>
                <div class="form-group">
                    <label for="edit-preco">Preço Base:</label>
                    <input type="number" id="edit-preco" class="form-control" value="${produto.precoBase}" step="0.01" required>
                </div>
                <div class="form-group">
                    <label for="edit-imagem">URL da Imagem:</label>
                    <input type="text" id="edit-imagem" class="form-control" value="${produto.imagemUrl}" required>
                </div>
                <div class="form-group">
                    <label for="edit-categoria">Categoria:</label>
                    <select id="edit-categoria" class="form-control" required>
                        <option value="towel" ${produto.categoria === 'towel' ? 'selected' : ''}>Toalhas</option>
                        <option value="apron" ${produto.categoria === 'apron' ? 'selected' : ''}>Aventais</option>
                        <option value="shirt" ${produto.categoria === 'shirt' ? 'selected' : ''}>Camisas</option>
                        <option value="kitchen" ${produto.categoria === 'kitchen' ? 'selected' : ''}>Kit Cozinha</option>
                    </select>
                </div>
                <div class="form-group text-right">
                    <button type="button" class="btn btn-secondary" onclick="fecharPopup()">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Salvar</button>
                </div>
            </form>
        `;
        
        popup.classList.add('mostrar');
        
        // Adicionar evento de submit ao formulário
        document.getElementById('form-editar-produto').addEventListener('submit', async function(e) {
            e.preventDefault();
            await salvarEdicaoProduto(id);
        });
    } catch (error) {
        console.error('Erro:', error);
        mostrarPopup(error.message || 'Erro ao carregar produto');
    }
}

// Função para salvar edição de produto
async function salvarEdicaoProduto(id) {
    try {
        const imagemUrl = document.getElementById('edit-imagem').value;
        // Validação simples de URL de imagem
        if (!/^https?:\/\/.+\.(jpg|jpeg|png|gif)$/i.test(imagemUrl)) {
            mostrarPopup('URL da imagem inválida. Use um link direto para uma imagem (jpg, png, jpeg ou gif).');
            return;
        }
        const produto = {
            nome: document.getElementById('edit-nome').value,
            descricao: document.getElementById('edit-descricao').value,
            precoBase: parseFloat(document.getElementById('edit-preco').value),
            imagemUrl: imagemUrl,
            categoria: document.getElementById('edit-categoria').value
        };
        
        await ProdutoAPI.editarProduto(id, produto);
        fecharPopup();
        carregarProdutos();
        mostrarPopup('Produto editado com sucesso!');
    } catch (error) {
        console.error('Erro:', error);
        mostrarPopup(error.message || 'Erro ao editar produto');
    }
}

// Função para mostrar popup
function mostrarPopup(mensagem) {
    const popup = document.getElementById('popup-carrinho');
    const mensagemPopup = document.getElementById('mensagem-popup');
    mensagemPopup.innerHTML = `<p>${mensagem}</p>`;
    popup.classList.add('mostrar');
    setTimeout(fecharPopup, 3000);
}

// Função para fechar popup
function fecharPopup() {
    const popup = document.getElementById('popup-carrinho');
    popup.classList.remove('mostrar');
}

// Função para mostrar modal de confirmação customizado
function mostrarConfirmacao(mensagem, onConfirm) {
    let modal = document.getElementById('confirmacao-modal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'confirmacao-modal';
        modal.className = 'popup-carrinho';
        document.body.appendChild(modal);
    }
    modal.innerHTML = `
        <div class="popup-conteudo">
            <p id="confirmacao-mensagem">${mensagem}</p>
            <div class="popup-botoes">
                <button id="confirmar-excluir" class="btn btn-danger">Excluir</button>
                <button id="cancelar-excluir" class="btn btn-secondary">Cancelar</button>
            </div>
        </div>
    `;
    modal.style.display = 'flex';
    modal.classList.add('mostrar');

    modal.querySelector('#confirmar-excluir').onclick = function() {
        modal.style.display = 'none';
        modal.classList.remove('mostrar');
        onConfirm();
    };
    modal.querySelector('#cancelar-excluir').onclick = function() {
        modal.style.display = 'none';
        modal.classList.remove('mostrar');
    };
}

// Função para excluir produto
async function excluirProduto(id) {
    mostrarConfirmacao('Tem certeza que deseja excluir este produto?', async function() {
        try {
            await ProdutoAPI.removerProduto(id);
            carregarProdutos();
            mostrarPopup('Produto excluído com sucesso!');
        } catch (error) {
            console.error('Erro:', error);
            // Verifica se a mensagem do erro indica restrição de integridade
            const msg = error.message || error.erro || '';
            if (
                msg.includes('ConstraintViolationException') ||
                msg.includes('não foi possível remover o produto') ||
                msg.toLowerCase().includes('vinculado')
            ) {
                mostrarPopup('Este produto não pode ser excluído pois está vinculado a pedidos ou outras operações.');
            } else {
                mostrarPopup(msg || 'Erro ao excluir produto');
            }
        }
    });
}

// Carregar produtos quando a página for carregada
document.addEventListener('DOMContentLoaded', carregarProdutos); 