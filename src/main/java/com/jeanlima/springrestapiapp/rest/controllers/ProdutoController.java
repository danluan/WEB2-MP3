package com.jeanlima.springrestapiapp.rest.controllers;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

        updates.forEach((key, value) -> updateField(produto, key, value));

        repository.save(produto);
    }

    private void updateField(Produto produto, String fieldName, Object value) {
        Field field;
        try {
            field = Produto.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            if (field.get(produto) != value){
                if (field.getType() == Double.class){
                    field.set(produto, (BigDecimal) value);
                }
                field.set(produto, value);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field " + fieldName + " not found on Produto.");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Field " + fieldName + " not accessible on Produto.");
        }

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
    public Produto getById(@PathVariable Integer id){
        return repository
                .findById(id)
                .orElseThrow( () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto não encontrado."));
    }


    @GetMapping
    public List<Produto> find(Produto filtro ){
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(
                        ExampleMatcher.StringMatcher.CONTAINING );

        Example example = Example.of(filtro, matcher);
        return repository.findAll(example);
    }
}