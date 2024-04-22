package com.jeanlima.springrestapiapp.rest.controllers;

import com.jeanlima.springrestapiapp.model.Estoque;
import com.jeanlima.springrestapiapp.repository.EstoqueRepository;
import com.jeanlima.springrestapiapp.repository.ProdutoRepository;
import com.jeanlima.springrestapiapp.rest.dto.EstoqueDTO;
import com.jeanlima.springrestapiapp.service.impl.EstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService service;

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private EstoqueService estoqueService;

    @GetMapping("{id}")
    public EstoqueDTO getEstoqueById(@PathVariable Integer id) {
        Estoque estoque = estoqueService.findById(id);
        return EstoqueDTO.builder()
                .id(estoque.getId())
                .produto(estoque.getProduto().getId())
                .quantidade(estoque.getQuantidade())
                .build();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstoqueDTO save(@RequestBody EstoqueDTO estoqueDTO ){


        produtoRepository.findById(estoqueDTO.getProduto()
        ).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Produto não encontrado."));

        //verify produto already has estoque


        Estoque estoque = estoqueService.save(estoqueDTO);
        return EstoqueDTO.builder()
                .id(estoque.getId())
                .produto(estoque.getProduto().getId())
                .quantidade(estoque.getQuantidade())
                .build();
    }

    @PutMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public ResponseEntity<EstoqueDTO> update( @PathVariable Integer id, @RequestBody EstoqueDTO estoqueDTO ){
        estoqueRepository.findById(id)
                .map( estoque -> {
                    estoque.setQuantidade(estoqueDTO.getQuantidade());
                    estoque.setProduto(
                            produtoRepository
                                    .findById(estoqueDTO.getProduto())
                                    .orElseThrow(
                                            () -> new ResponseStatusException(
                                                    NOT_FOUND,
                                                    "Produto não encontrado.")));
                    return estoqueRepository.save(estoque);
                }).orElseThrow( () -> new ResponseStatusException(NOT_FOUND, "Estoque não encontrado."));
        return ResponseEntity.ok().build();
    }

}
