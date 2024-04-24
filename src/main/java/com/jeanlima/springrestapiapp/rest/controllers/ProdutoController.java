package com.jeanlima.springrestapiapp.rest.controllers;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
import java.util.stream.Collectors;


import com.jeanlima.springrestapiapp.Util;
import com.jeanlima.springrestapiapp.rest.dto.EstoqueDTO;
import com.jeanlima.springrestapiapp.rest.dto.PedidoDTO;
import com.jeanlima.springrestapiapp.rest.dto.ProdutoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.ReflectionUtils;

import com.jeanlima.springrestapiapp.model.Produto;
import com.jeanlima.springrestapiapp.repository.ProdutoRepository;





@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @PostMapping
    @ResponseStatus(CREATED)
    public Produto save( @RequestBody Produto produto ){
        return repository.save(produto);
    }

    @PutMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void update( @PathVariable Integer id, @RequestBody Produto produto ){
        repository
                .findById(id)
                .map( p -> {
                   produto.setId(p.getId());
                   repository.save(produto);
                   return produto;
                }).orElseThrow( () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto não encontrado."));
    }

    @PatchMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void patch( @PathVariable Integer id, @RequestBody Map<String, Object> updates ){
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));

        updates.forEach((key, value) -> Util.updateField(produto, key, value));

        repository.save(produto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Integer id){
        repository
                .findById(id)
                .map( p -> {
                    repository.delete(p);
                    return Void.TYPE;
                }).orElseThrow( () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto não encontrado."));
    }

    @GetMapping("{id}")
    public ProdutoDTO getById(@PathVariable Integer id){
        Produto produto = repository
                .findById(id)
                .orElseThrow( () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto não encontrado."));

        return criarProdutoDTO(produto);
    }


    @GetMapping
    public List<ProdutoDTO> find(Produto filtro){
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example example = Example.of(filtro, matcher);
        List<Produto> produtos = repository.findAll(example);
        return produtos.stream()
                .map(this::criarProdutoDTO)
                .collect(Collectors.toList());
    }

    private ProdutoDTO criarProdutoDTO(Produto produto) {
        EstoqueDTO estoqueDTO = null;
        if (produto.getEstoque() != null) {
            estoqueDTO = EstoqueDTO.builder()
                    .id(produto.getEstoque().getId())
                    .quantidade(produto.getEstoque().getQuantidade())
                    .build();
        }
        return ProdutoDTO.builder()
                .id(produto.getId())
                .descricao(produto.getDescricao())
                .preco(produto.getPreco())
                .estoque(estoqueDTO)
                .build();
    }
}
