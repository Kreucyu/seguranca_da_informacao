package com.exemplo.hmacapi.produto;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public ProdutoResponse criar(ProdutoRequest req) {
        Produto p = new Produto(req.nome(), req.descricao(), req.preco(), req.quantidade());
        return ProdutoResponse.de(repository.save(p));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return repository.findAll().stream().map(ProdutoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscar(Long id) {
        return ProdutoResponse.de(obter(id));
    }

    public ProdutoResponse atualizar(Long id, ProdutoRequest req) {
        Produto p = obter(id);
        p.atualizar(req.nome(), req.descricao(), req.preco(), req.quantidade());
        return ProdutoResponse.de(p);
    }

    public void remover(Long id) {
        repository.delete(obter(id));
    }

    private Produto obter(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }
}
