// checkout.js
// Carregar e exibir os itens do carrinho na tela de finalização de compra

document.addEventListener('DOMContentLoaded', function() {
    carregarCheckout();
    // Botão de alterar endereço agora abre o modal
    const btnAlterarEndereco = document.querySelector('.btn-outline-primary');
    if (btnAlterarEndereco) {
        btnAlterarEndereco.addEventListener('click', function() {
            // Preencher o campo do modal com o endereço atual
            const cliente = SessionManager.obterCliente();
            document.getElementById('novoEndereco').value = cliente && cliente.enderecoCompleto ? cliente.enderecoCompleto : '';
            document.getElementById('endereco-feedback').style.display = 'none';
            $('#modalEditarEndereco').modal('show');
        });
    }

    // Evento de salvar novo endereço
    document.getElementById('salvarEnderecoBtn').addEventListener('click', async function() {
        const novoEndereco = document.getElementById('novoEndereco').value.trim();
        const feedback = document.getElementById('endereco-feedback');
        if (!novoEndereco) {
            feedback.textContent = 'Por favor, informe o novo endereço.';
            feedback.style.display = 'block';
            return;
        }
        // Obter cliente logado
        let cliente = SessionManager.obterCliente();
        if (!cliente || !cliente.token) {
            feedback.textContent = 'Sessão expirada. Faça login novamente.';
            feedback.style.display = 'block';
            return;
        }
        // Chamar API para atualizar endereço
        try {
            const response = await fetch(`${API_BASE_URL}/clientes/atualizar`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${cliente.token}`
                },
                body: JSON.stringify({
                    id: cliente.userId,
                    telefone: cliente.telefone ? cliente.telefone.replace(/\D/g, '') : '',
                    enderecoCompleto: novoEndereco
                })
            });
            if (!response.ok) {
                const data = await response.json();
                feedback.textContent = data.erro || 'Erro ao atualizar endereço.';
                feedback.style.display = 'block';
                return;
            }
            // Atualizar sessionStorage/localStorage
            cliente.enderecoCompleto = novoEndereco;
            SessionManager.salvarCliente(cliente);
            if (localStorage.getItem('userData')) {
                let userData = JSON.parse(localStorage.getItem('userData'));
                userData.enderecoCompleto = novoEndereco;
                localStorage.setItem('userData', JSON.stringify(userData));
            }
            document.getElementById('delivery-address').innerText = novoEndereco;
            feedback.style.display = 'none';
            $('#modalEditarEndereco').modal('hide');
            mostrarPopup('Endereço atualizado com sucesso!');
        } catch (error) {
            feedback.textContent = 'Erro ao atualizar endereço.';
            feedback.style.display = 'block';
        }
    });

    // Adicionar evento de clique ao botão do WhatsApp
    const whatsappBtn = document.getElementById('whatsapp-checkout-btn');
    if (whatsappBtn) {
        whatsappBtn.addEventListener('click', async function(e) {
            e.preventDefault();
            
            try {
                // Criar pedido pendente antes de abrir o WhatsApp
                const pedido = await criarPedidoPendente();
                
                // Adicionar o ID do pedido na mensagem do WhatsApp
                const message = formatWhatsAppMessage(pedido.id);
                const whatsappUrl = `https://api.whatsapp.com/send/?phone=5531993502017&text=${message}&type=phone_number&app_absent=0`;
                
                // Limpar o carrinho após criar o pedido
                await limparCarrinho();
                
                // Abrir WhatsApp
                window.open(whatsappUrl, '_blank');
                
                // Redirecionar para página de sucesso após alguns segundos
                setTimeout(() => {
                    window.location.href = 'pedido-sucesso.html';
                }, 2000);
            } catch (error) {
                mostrarPopup('Erro ao finalizar pedido: ' + error.message);
            }
        });
    }

    // Adicionar evento de clique ao botão do Mercado Pago
    const mercadopagoBtn = document.getElementById('mercadopago-checkout-btn');
    if (mercadopagoBtn) {
        mercadopagoBtn.addEventListener('click', async function(e) {
            e.preventDefault();
            
            try {
                const cliente = SessionManager.obterCliente();
                if (!cliente || !cliente.token) {
                    mostrarPopup('Sessão expirada. Faça login novamente.');
                    window.location.href = 'login.html';
                    return;
                }

                // Criar pedido pendente antes de redirecionar para o Mercado Pago
                const pedido = await criarPedidoPendente();

                // Obter o valor total do pedido
                const totalElement = document.getElementById('total-with-freight');
                const total = parseFloat(totalElement.textContent.replace('R$ ', ''));
                const email = cliente.email;

                // Call backend to create preference
                const response = await fetch(`${API_BASE_URL}/pagamento/criar-preferencia`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${cliente.token}`
                    },
                    body: JSON.stringify({
                        valor: total,
                        email: email,
                        pedidoId: pedido.id // Adicionando o ID do pedido na requisição
                    })
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.error || 'Erro ao criar preferência de pagamento');
                }

                const preference = await response.json();
                
                // Limpar o carrinho após criar o pedido
                await limparCarrinho();
                
                // Abrir Mercado Pago em nova aba e redirecionar página atual para sucesso
                if (preference.init_point) {
                    window.open(preference.init_point, '_blank');
                    window.location.href = 'pedido-sucesso.html';
                } else {
                    throw new Error('URL de redirecionamento do Mercado Pago não recebida.');
                }

            } catch (error) {
                console.error('Erro ao iniciar pagamento com Mercado Pago:', error);
                mostrarPopup('Erro ao iniciar pagamento com Mercado Pago: ' + error.message);
            }
        });
    }

});

async function carregarCheckout() {
    // Atualizar ano no rodapé
    if (document.getElementById('displayYear')) {
        document.getElementById('displayYear').textContent = new Date().getFullYear();
    }

    // Verificar se o cliente está logado
    if (!SessionManager.isClienteLogado()) {
        mostrarPopup('Você precisa estar logado para finalizar a compra.');
        window.location.href = 'login.html';
        return;
    }

    const cliente = SessionManager.obterCliente();
    const token = cliente.token;
    if (!token) {
        mostrarPopup('Sessão expirada. Faça login novamente.');
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/carrinho`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error('Erro ao buscar itens do carrinho.');
        }
        const items = await response.json();
        if (!items || items.length === 0) {
            mostrarPopup('Seu carrinho está vazio!');
            window.location.href = 'cart.html';
            return;
        }
        exibirItensCheckout(items);
        preencherResumoCheckout(items, cliente);
    } catch (error) {
        console.error(error);
        mostrarPopup('Erro ao carregar o carrinho.');
    }
}

function exibirItensCheckout(items) {
    const orderItemsContainer = document.getElementById('order-items');
    orderItemsContainer.innerHTML = '';
    items.forEach(item => {
        const itemElement = document.createElement('div');
        itemElement.classList.add('d-flex', 'align-items-center', 'border-bottom', 'py-2');
        itemElement.innerHTML = `
            <img src="${item.produto.imagemUrl || 'images/default-product.jpg'}" alt="${item.produto.nome}" class="rounded mr-3" style="width:60px;height:60px;object-fit:cover;">
            <div class="flex-grow-1">
                <div class="font-weight-bold">${item.produto.nome}</div>
                <div class="text-muted">R$ ${item.produto.precoBase.toFixed(2)} (unidade) | Quantidade: ${item.quantidade}</div>
            </div>
            <div class="font-weight-bold ml-3">R$ ${(item.produto.precoBase * item.quantidade).toFixed(2)}</div>
        `;
        orderItemsContainer.appendChild(itemElement);
    });
}

function preencherResumoCheckout(items, cliente) {
    // Calcular subtotal
    const subtotal = items.reduce((sum, item) => sum + (item.produto.precoBase * item.quantidade), 0);
    // Frete fixo de exemplo (pode ser dinâmico)
    const freightCost = 15.00;
    const estimatedDeliveryTime = '20 dias úteis';
    const totalWithFreight = subtotal + freightCost;

    document.getElementById('freight-cost').innerText = `R$ ${freightCost.toFixed(2)}`;
    document.getElementById('estimated-delivery-time').innerText = estimatedDeliveryTime;
    document.getElementById('subtotal').innerText = `R$ ${subtotal.toFixed(2)}`;
    document.getElementById('total-with-freight').innerText = `R$ ${totalWithFreight.toFixed(2)}`;

    // Endereço de entrega (exemplo: pegar do cliente logado)
    if (cliente && cliente.enderecoCompleto) {
        document.getElementById('delivery-address').innerText = cliente.enderecoCompleto;
    } else {
        document.getElementById('delivery-address').innerText = 'Endereço não cadastrado.';
    }

}

// Função para criar pedido pendente (Keep if WhatsApp payment is still needed)
async function criarPedidoPendente() {
    const cliente = SessionManager.obterCliente();
    if (!cliente || !cliente.token) {
        throw new Error('Sessão expirada');
    }

    // Buscar itens do carrinho para enviar ao backend
    let items = [];
    let totalWithFreight = 0;
    try {
        const response = await fetch(`${API_BASE_URL}/carrinho`, {
            headers: {
                'Authorization': `Bearer ${cliente.token}`
            }
        });
        if (!response.ok) {
            throw new Error('Erro ao buscar itens do carrinho.');
        }
        items = await response.json();
        totalWithFreight = items.reduce((sum, item) => sum + (item.produto.precoBase * item.quantidade), 0) + 15.00; // frete fixo
    } catch (error) {
        throw new Error('Erro ao buscar itens do carrinho.');
    }

    try {
        const response = await fetch(`${API_BASE_URL}/pedidos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${cliente.token}`
            },
            body: JSON.stringify({
                total: totalWithFreight,
                itens: items.map(item => ({
                    produtoId: item.produto.id,
                    quantidade: item.quantidade
                }))
            })
        });

        if (!response.ok) {
            const data = await response.json();
            throw new Error(data.erro || 'Erro ao criar pedido');
        }

        const pedido = await response.json();
        return pedido;
    } catch (error) {
        console.error('Erro ao criar pedido:', error);
        throw error;
    }
}

// Função para limpar o carrinho após criar o pedido (Keep if WhatsApp payment is still needed)
async function limparCarrinho() {
    const cliente = SessionManager.obterCliente();
    if (!cliente || !cliente.token) {
        throw new Error('Sessão expirada');
    }

    try {
        const response = await fetch(`${API_BASE_URL}/carrinho/limpar/${cliente.userId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${cliente.token}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao limpar carrinho');
        }
    } catch (error) {
        console.error('Erro ao limpar carrinho:', error);
        throw error;
    }
}

// Modificar a função formatWhatsAppMessage para include the pedido ID (Keep if WhatsApp payment is still needed)
function formatWhatsAppMessage(pedidoId) {
    const orderItems = document.getElementById('order-items');
    const subtotal = document.getElementById('subtotal').textContent;
    const freight = document.getElementById('freight-cost').textContent;
    const total = document.getElementById('total-with-freight').textContent;
    const address = document.getElementById('delivery-address').textContent;

    // Montar itens do pedido com marcadores
    let itensMsg = '';
    const itemDivs = orderItems.querySelectorAll('div.d-flex');
    itemDivs.forEach(div => {
        const nome = div.querySelector('.font-weight-bold')?.textContent?.trim() || '';
        const detalhes = div.querySelector('.text-muted')?.textContent?.trim() || '';
        const valor = div.querySelector('.ml-3')?.textContent?.trim() || '';
        itensMsg += `- ${nome}\n  ${detalhes}\n  Total: ${valor}\n\n`;
    });

    let message = `Olá! Gostaria de finalizar meu pedido:\n\n`;
    message += `*Número do Pedido:* ${pedidoId}\n\n`;
    message += `*Itens do Pedido:*\n${itensMsg}`;
    message += `*Endereço de Entrega:*\n${address}\n\n`;
    message += `*Valores:*\n`;
    message += `• Subtotal: ${subtotal}\n`;
    message += `• Frete: ${freight}\n`;
    message += `• Total: ${total}\n\n`;
    message += `Gostaria de combinar as personalizações e forma de pagamento.`;

    return encodeURIComponent(message);
}

// Remove PIX specific functions as they are not needed for the redirection flow.
/*
// Função para verificar o status do pagamento
async function verificarStatusPagamento(paymentId) {
    const checkInterval = setInterval(async () => {
        try {
            const cliente = SessionManager.obterCliente();
            if (!cliente || !cliente.token) {
                clearInterval(checkInterval);
                return;
            }

            const response = await fetch(`${API_BASE_URL}/api/pagamento/status/${paymentId}`, {
                headers: {
                    'Authorization': `Bearer ${cliente.token}`
                }
            });

            if (!response.ok) {
                throw new Error('Erro ao verificar status do pagamento');
            }

            const statusData = await response.json();

            // Verificar se o pagamento foi aprovado
            if (statusData.status === 'approved') {
                clearInterval(checkInterval);
                alert('Pagamento aprovado! Seu pedido será processado em breve.');
                window.location.href = 'pedido-sucesso.html';
            } else if (statusData.status === 'rejected' || statusData.status === 'cancelled') {
                clearInterval(checkInterval);
                alert('Pagamento não foi aprovado. Por favor, tente novamente.');
            }
            // Para outros status (pending, in_process), continuar verificando

        } catch (error) {
            console.error('Erro ao verificar status do pagamento:', error);
            clearInterval(checkInterval);
        }
    }, 5000); // Verificar a cada 5 segundos
}
*/

// Add these helper functions at the end of the file
function mostrarPopup(mensagem) {
    const popup = document.getElementById('popup-carrinho');
    const mensagemPopup = document.getElementById('mensagem-popup');
    mensagemPopup.textContent = mensagem;
    popup.classList.add('mostrar');
}

function fecharPopup() {
    const popup = document.getElementById('popup-carrinho');
    popup.classList.remove('mostrar');
}
