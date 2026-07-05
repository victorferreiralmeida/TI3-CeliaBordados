// Configuração da API
const API_BASE_URL = 'http://localhost:8080/api';

// Funções de API para Cliente
const ClienteAPI = {
    // Listar todos os clientes
    listarClientes: async function() {
        try {
            const response = await fetch(`${API_BASE_URL}/clientes`);
            if (!response.ok) {
                throw new Error('Erro ao buscar clientes');
            }
            return await response.json();
        } catch (error) {
            console.error('Erro:', error);
            throw error;
        }
    },
    
    // Cadastrar novo cliente
    cadastrarCliente: async function(cliente) {
        try {
            const response = await fetch(`${API_BASE_URL}/clientes/cadastro`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(cliente)
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.erro || 'Erro ao cadastrar cliente');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro:', error);
            throw error;
        }
    },
    
    // Login de cliente
    login: async function(email, senha) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/cliente/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, senha })
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.erro || 'Erro ao fazer login');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro:', error);
            throw error;
        }
    },
    
    // Buscar cliente por email
    buscarPorEmail: async function(email) {
        try {
            const response = await fetch(`${API_BASE_URL}/clientes/${email}`);
            
            if (!response.ok) {
                if (response.status === 404) {
                    return null;
                }
                throw new Error('Erro ao buscar cliente');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro:', error);
            throw error;
        }
    }
};

// Funções de API para Administrador
const AdministradorAPI = {
    // Login de administrador
    login: async function(email, senha) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/admin/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, senha })
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.erro || 'Erro ao fazer login');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro:', error);
            throw error;
        }
    }
};

// Funções para gerenciamento de sessão
const SessionManager = {
    // Salvar dados do cliente na sessão
    salvarCliente: function(cliente) {
        sessionStorage.setItem('clienteLogado', JSON.stringify(cliente));
    },
    
    // Obter dados do cliente da sessão
    obterCliente: function() {
        const clienteJSON = sessionStorage.getItem('clienteLogado');
        return clienteJSON ? JSON.parse(clienteJSON) : null;
    },
    
    // Verificar se há cliente logado
    isClienteLogado: function() {
        return sessionStorage.getItem('clienteLogado') !== null;
    },
    
    // Salvar dados do administrador na sessão
    salvarAdmin: function(admin) {
        sessionStorage.setItem('adminLogado', JSON.stringify(admin));
    },
    
    // Obter dados do administrador da sessão
    obterAdmin: function() {
        const adminJSON = sessionStorage.getItem('adminLogado');
        return adminJSON ? JSON.parse(adminJSON) : null;
    },
    
    // Verificar se há administrador logado
    isAdminLogado: function() {
        return sessionStorage.getItem('adminLogado') !== null;
    },
    
    // Fazer logout
    logout: function() {
        console.log("Realizando logout...");
        
        // Remover dados da sessão
        sessionStorage.removeItem('clienteLogado');
        sessionStorage.removeItem('adminLogado');
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('role');
        sessionStorage.removeItem('userId');
        
        // Limpar também localStorage para garantir
        localStorage.removeItem('userData');
        localStorage.removeItem('adminData');
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        
        console.log("Logout completo - Dados de sessão removidos");
        
        // Para depuração
        if (this.isClienteLogado() || this.isAdminLogado()) {
            console.error("ERRO: Logout incompleto - usuário ainda logado!");
        } else {
            console.log("Verificação de logout bem-sucedida - usuário não está mais logado");
        }
    }
};

// Verificador de status de autenticação
const AuthChecker = {
    // Verificar estado de autenticação atual
    verificarAutenticacao: function() {
        // Se não tem token na sessionStorage, não está autenticado
        if (!sessionStorage.getItem('clienteLogado') && !sessionStorage.getItem('adminLogado')) {
            return false;
        }
        
        return true;
    },
    
    // Redirecionar para página de login se não estiver autenticado
    redirecionarSeNaoAutenticado: function(role, loginPage) {
        if (!this.verificarAutenticacao()) {
            window.location.href = loginPage;
            return false;
        }
        
        // Verificar se o papel do usuário corresponde ao esperado
        if (role === 'cliente' && !SessionManager.isClienteLogado()) {
            window.location.href = loginPage;
            return false;
        } else if (role === 'admin' && !SessionManager.isAdminLogado()) {
            window.location.href = loginPage;
            return false;
        }
        
        return true;
    },
    
    // Redirecionar para a página inicial se estiver autenticado
    redirecionarSeAutenticado: function(homePage) {
        if (this.verificarAutenticacao()) {
            window.location.href = homePage;
            return true;
        }
        return false;
    },
    
    // Atualizar elementos da UI baseado no status de autenticação
    atualizarUI: function() {
        // Elementos para usuários autenticados
        const authElements = document.querySelectorAll('.auth-only');
        // Elementos para usuários não autenticados
        const nonAuthElements = document.querySelectorAll('.non-auth-only');
        // Elementos específicos para clientes
        const clienteElements = document.querySelectorAll('.cliente-only');
        // Elementos específicos para administradores
        const adminElements = document.querySelectorAll('.admin-only');
        
        const isAuth = this.verificarAutenticacao();
        const isCliente = SessionManager.isClienteLogado();
        const isAdmin = SessionManager.isAdminLogado();
        
        // Mostrar/esconder elementos baseado no status de autenticação
        authElements.forEach(el => {
            el.style.display = isAuth ? '' : 'none';
        });
        
        nonAuthElements.forEach(el => {
            el.style.display = isAuth ? 'none' : '';
        });
        
        clienteElements.forEach(el => {
            el.style.display = isCliente ? '' : 'none';
        });
        
        adminElements.forEach(el => {
            el.style.display = isAdmin ? '' : 'none';
        });
    }
};

// Funções de API para Produtos
const ProdutoAPI = {
    // Listar todos os produtos
    listarProdutos: async function() {
        try {
            const response = await fetch(`${API_BASE_URL}/produtos`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                throw new Error('Erro ao buscar produtos');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro:', error);
            throw error;
        }
    },

    // Adicionar novo produto
    adicionarProduto: async function(produto) {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/produtos`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${admin.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(produto)
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensagem || 'Erro ao adicionar produto');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro:', error);
            throw error;
        }
    },

    // Editar produto existente
    editarProduto: async function(id, produto) {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/produtos/${id}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${admin.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(produto)
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensagem || 'Erro ao editar produto');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro:', error);
            throw error;
        }
    },

    // Remover produto
    removerProduto: async function(id) {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) {
                throw new Error('Não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/produtos/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${admin.token}`
                }
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                console.error('Detalhe do erro da API ao remover produto:', errorData);
                throw new Error(errorData.mensagem || errorData.erro || 'Erro ao remover produto');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro:', error);
            throw error;
        }
    },

    // Obter produto por ID
    obterProduto: async function(id) {
        try {
            const response = await fetch(`${API_BASE_URL}/produtos/${id}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                throw new Error('Erro ao buscar produto');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro:', error);
            throw error;
        }
    },

    // Adicionar ao carrinho
    adicionarAoCarrinho: async function(produtoId, quantidade = 1) {
        try {
            const clienteLogado = JSON.parse(sessionStorage.getItem('clienteLogado'));
            if (!clienteLogado || !clienteLogado.token) {
                throw new Error('Usuário não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/carrinho/adicionar?produtoId=${produtoId}&quantidade=${quantidade}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${clienteLogado.token}`
                }
            });

            if (!response.ok) {
                if (response.status === 400) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Erro ao adicionar ao carrinho');
                }
                throw new Error('Erro ao adicionar ao carrinho');
            }

            return await response.json();
        } catch (error) {
            console.error('Erro:', error);
            throw error;
        }
    }
};

const PersonalizacaoAPI = {
    // Cadastrar nova personalização
    cadastrarPersonalizacao: async function(personalizacao) {
        try {
            const cliente = SessionManager.obterCliente();
            if (!cliente || !cliente.token) {
                throw new Error('Usuário não autenticado');
            }

            const response = await fetch(`${API_BASE_URL}/personalizacoes`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${cliente.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(personalizacao)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensagem || 'Erro ao cadastrar personalização');
            }

            return await response.json();
        } catch (error) {
            console.error('Erro ao cadastrar personalização:', error);
            throw error;
        }
    }
};

const RelatorioAPI = {
    // Buscar vendas filtradas para relatório
    buscarVendas: async function({ dataInicial, dataFinal, status, produto }) {
        try {
            const admin = SessionManager.obterAdmin();
            if (!admin || !admin.token) throw new Error('Não autenticado');

            // Montar query string
            const params = new URLSearchParams();
            if (dataInicial) params.append('dataInicial', dataInicial);
            if (dataFinal) params.append('dataFinal', dataFinal);
            if (status && status !== 'Todos') params.append('status', status);
            if (produto) params.append('produto', produto);

            const response = await fetch(`${API_BASE_URL}/pedidos/relatorio?${params.toString()}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${admin.token}`
                }
            });

            if (!response.ok) throw new Error('Erro ao buscar vendas');
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar vendas:', error);
            throw error;
        }
    }
};