document.addEventListener('DOMContentLoaded', async function () {
    // Verifica se admin está logado
    if (!SessionManager.isAdminLogado()) {
        window.location.href = 'login.html';
    }

    // Custom select de produtos
    let produtosList = [];
    let produtosSelecionados = [];
    const categorySelect = document.getElementById('category-select');
    const optionsList = document.getElementById('options');

    // Preencher opções de produtos
    try {
        produtosList = await ProdutoAPI.listarProdutos();
        optionsList.innerHTML = '';
        produtosList.forEach(produto => {
            const li = document.createElement('li');
            li.textContent = produto.nome;
            li.dataset.value = produto.nome;
            optionsList.appendChild(li);
        });
    } catch (error) {
        // Se der erro, mantém vazio
    }

    // Mostrar/ocultar opções ao clicar na box
    categorySelect.addEventListener('click', function(e) {
        optionsList.style.display = optionsList.style.display === 'block' ? 'none' : 'block';
        categorySelect.classList.toggle('active');
    });
    // Fechar ao clicar fora
    document.addEventListener('click', function(e) {
        if (!categorySelect.contains(e.target) && !optionsList.contains(e.target)) {
            optionsList.style.display = 'none';
            categorySelect.classList.remove('active');
        }
    });
    // Selecionar/deselecionar produto
    optionsList.addEventListener('click', function(e) {
        if (e.target.tagName === 'LI') {
            const value = e.target.dataset.value;
            const idx = produtosSelecionados.indexOf(value);
            if (idx === -1) {
                produtosSelecionados.push(value);
            } else {
                produtosSelecionados.splice(idx, 1);
            }
            renderSelectedTags();
            renderOptions();
        }
    });
    // Remover tag selecionada
    categorySelect.addEventListener('click', function(e) {
        if (e.target.classList.contains('remove-tag')) {
            const value = e.target.parentElement.dataset.value;
            produtosSelecionados = produtosSelecionados.filter(v => v !== value);
            renderSelectedTags();
            renderOptions();
        }
    });
    function renderSelectedTags() {
        if (produtosSelecionados.length === 0) {
            categorySelect.innerHTML = 'Selecione os produtos';
        } else {
            categorySelect.innerHTML = '';
            produtosSelecionados.forEach(prod => {
                const tag = document.createElement('span');
                tag.className = 'selected-tag';
                tag.dataset.value = prod;
                tag.innerHTML = prod + ' <span class="remove-tag">&times;</span>';
                categorySelect.appendChild(tag);
            });
        }
    }
    function renderOptions() {
        Array.from(optionsList.children).forEach(li => {
            if (produtosSelecionados.includes(li.dataset.value)) {
                li.classList.add('selected');
            } else {
                li.classList.remove('selected');
            }
        });
    }
    // Inicializa visual
    renderSelectedTags();
    renderOptions();

    // Custom select de status
    const statusList = ["Pendente", "Pago", "Processando", "Em Produção", "Enviado", "Entregue", "Concluído", "Cancelado"];
    let statusSelecionados = [];
    const statusSelect = document.getElementById('status-select');
    const statusOptions = document.getElementById('status-options');
    // Preencher opções de status
    statusOptions.innerHTML = '';
    statusList.forEach(status => {
        const li = document.createElement('li');
        li.textContent = status;
        li.dataset.value = status;
        statusOptions.appendChild(li);
    });
    // Mostrar/ocultar opções ao clicar na box
    statusSelect.addEventListener('click', function(e) {
        statusOptions.style.display = statusOptions.style.display === 'block' ? 'none' : 'block';
        statusSelect.classList.toggle('active');
    });
    // Fechar ao clicar fora
    document.addEventListener('click', function(e) {
        if (!statusSelect.contains(e.target) && !statusOptions.contains(e.target)) {
            statusOptions.style.display = 'none';
            statusSelect.classList.remove('active');
        }
    });
    // Selecionar/deselecionar status
    statusOptions.addEventListener('click', function(e) {
        if (e.target.tagName === 'LI') {
            const value = e.target.dataset.value;
            const idx = statusSelecionados.indexOf(value);
            if (idx === -1) {
                statusSelecionados.push(value);
            } else {
                statusSelecionados.splice(idx, 1);
            }
            renderStatusTags();
            renderStatusOptions();
        }
    });
    // Remover tag selecionada
    statusSelect.addEventListener('click', function(e) {
        if (e.target.classList.contains('remove-tag')) {
            const value = e.target.parentElement.dataset.value;
            statusSelecionados = statusSelecionados.filter(v => v !== value);
            renderStatusTags();
            renderStatusOptions();
        }
    });
    function renderStatusTags() {
        if (statusSelecionados.length === 0) {
            statusSelect.innerHTML = 'Selecione o(s) status';
        } else {
            statusSelect.innerHTML = '';
            statusSelecionados.forEach(stat => {
                const tag = document.createElement('span');
                tag.className = 'selected-tag';
                tag.dataset.value = stat;
                tag.innerHTML = stat + ' <span class="remove-tag">&times;</span>';
                statusSelect.appendChild(tag);
            });
        }
    }
    function renderStatusOptions() {
        Array.from(statusOptions.children).forEach(li => {
            if (statusSelecionados.includes(li.dataset.value)) {
                li.classList.add('selected');
            } else {
                li.classList.remove('selected');
            }
        });
    }
    // Inicializa visual
    renderStatusTags();
    renderStatusOptions();

    function gerarPDF(vendas, filtros) {
        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();

        // URL da logo
        const logoUrl = 'https://i.imgur.com/8kwdhqP.png';

        // Função para converter imagem de URL para base64
        function getBase64FromUrl(url, callback) {
            var img = new window.Image();
            img.setAttribute('crossOrigin', 'anonymous');
            img.onload = function () {
                var canvas = document.createElement('canvas');
                canvas.width = img.width;
                canvas.height = img.height;
                var ctx = canvas.getContext('2d');
                ctx.drawImage(img, 0, 0);
                var dataURL = canvas.toDataURL('image/png');
                callback(dataURL);
            };
            img.src = url;
        }

        getBase64FromUrl(logoUrl, function(logoBase64) {
            doc.addImage(logoBase64, 'PNG', 14, 8, 40, 18);

            doc.setFontSize(18);
            doc.text('Relatório de Vendas', 60, 22);

            doc.setFontSize(12);
            let filtrosText = `Período: ${filtros.dataInicial} a ${filtros.dataFinal}`;
            if (filtros.status) filtrosText += ` | Status: ${filtros.status}`;
            if (filtros.produto) filtrosText += ` | Produto(s): ${filtros.produto}`;

            // Quebra de linha automática para filtros longos
            const filtrosLines = doc.splitTextToSize(filtrosText, 180);
            doc.text(filtrosLines, 14, 32);

            const columns = [
                { header: 'Data', dataKey: 'data' },
                { header: 'Produto', dataKey: 'produto' },
                { header: 'Valor', dataKey: 'valor' },
                { header: 'Status', dataKey: 'status' }
            ];
            const rows = vendas.map(venda => ({
                data: new Date(venda.data).toLocaleDateString('pt-BR'),
                produto: venda.produto,
                valor: venda.valor ? `R$ ${Number(venda.valor).toFixed(2)}` : '',
                status: venda.status
            }));
            doc.autoTable({
                columns,
                body: rows,
                startY: 38 + (filtrosLines.length - 1) * 7,
                styles: { fontSize: 11 },
                headStyles: { fillColor: [241, 97, 121] }
            });
            doc.save('relatorio-vendas.pdf');
        });
    }

    document.getElementById('relatorioForm').addEventListener('submit', async function (e) {
        e.preventDefault();
        document.getElementById('errorMsg').style.display = 'none';
        const dataInicial = document.getElementById('dataInicial').value;
        const dataFinal = document.getElementById('dataFinal').value;
        let status = statusSelecionados.length > 0 ? statusSelecionados.join(',') : '';
        let produto = produtosSelecionados.length > 0 ? produtosSelecionados.join(',') : '';

        if (!dataInicial || !dataFinal) {
            document.getElementById('errorMsg').textContent = 'Preencha as datas inicial e final.';
            document.getElementById('errorMsg').style.display = 'block';
            return;
        }

        try {
            const vendas = await RelatorioAPI.buscarVendas({ dataInicial, dataFinal, status, produto });
            if (!vendas || vendas.length === 0) {
                document.getElementById('errorMsg').textContent = 'Nenhuma venda encontrada para os filtros selecionados.';
                document.getElementById('errorMsg').style.display = 'block';
                return;
            }
            gerarPDF(vendas, { dataInicial, dataFinal, status, produto });
        } catch (error) {
            document.getElementById('errorMsg').textContent = error.message || 'Erro ao buscar vendas.';
            document.getElementById('errorMsg').style.display = 'block';
        }
    });

    // Evento de logout admin
    const logoutBtn = document.getElementById('admin-logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            SessionManager.logout();
            window.location.href = 'index.html';
        });
    }
}); 