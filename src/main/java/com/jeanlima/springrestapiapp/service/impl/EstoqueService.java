package com.jeanlima.springrestapiapp.service.impl;

import com.jeanlima.springrestapiapp.model.Estoque;
import com.jeanlima.springrestapiapp.model.Produto;
import com.jeanlima.springrestapiapp.repository.EstoqueRepository;
import com.jeanlima.springrestapiapp.repository.ProdutoRepository;
import com.jeanlima.springrestapiapp.rest.dto.EstoqueDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;

    public Estoque findById(Integer id) {
        return estoqueRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estoque não encontrado."));
    }

    public Estoque save(EstoqueDTO estoqueDTO) {
        Produto produto = produtoRepository.findById(estoqueDTO.getProduto()
        ).orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidade(estoqueDTO.getQuantidade());

        estoqueRepository.save(estoque);
        return estoque;
    }

    public Estoque update(Integer id, EstoqueDTO estoqueDTO) {
        return estoqueRepository.findById(id)
                .map( estoque -> {

                    estoque.setQuantidade(estoqueDTO.getQuantidade());
                    estoque.setProduto(
                            produtoRepository
                                    .findById(estoqueDTO.getProduto())
                                    .orElseThrow(
                                            () -> new ResponseStatusException(
                                                    HttpStatus.NOT_FOUND,
                                                    "Produto não encontrado.")));
                    return estoqueRepository.save(estoque);
                }).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estoque não encontrado."));
    }

    public boolean produtoTemEstoque(Integer idProduto) {
        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));

        if(produto.getEstoque() != null) {
            return true;
        }
        return false;
    }

    public boolean deletar(Integer id) {
        if (estoqueRepository.existsById(id)) {
            estoqueRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Estoque updateFields(Integer id, Map<String, Object> updates) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estoque não encontrado."));

        updates.forEach((key, value) -> {
            if (key.equals("quantidade")) {
                estoque.setQuantidade((Integer) value);
            }
        });

        estoqueRepository.save(estoque);
        return estoque;
    }


}
