package com.celiabordados.service;

import com.celiabordados.Avaliacao;
import com.celiabordados.Cliente;
import com.celiabordados.Pedido;
import com.celiabordados.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    public Avaliacao salvar(Avaliacao avaliacao) {
        return avaliacaoRepository.save(avaliacao);
    }

    public List<Avaliacao> listarTodas() {
        return avaliacaoRepository.findAll();
    }

    public Optional<Avaliacao> buscarPorId(Long id) {
        return avaliacaoRepository.findById(id);
    }

    public List<Avaliacao> buscarPorProduto(Produto produto) {
        return avaliacaoRepository.findByProduto(produto);
    }

    public List<Avaliacao> buscarPorPedido(Pedido pedido) {
        return avaliacaoRepository.findByPedido(pedido);
    }

    public List<Avaliacao> buscarPorCliente(Cliente cliente) {
        return avaliacaoRepository.findByCliente(cliente);
    }

    public void deletar(Long id) {
        avaliacaoRepository.deleteById(id);
    }
} 