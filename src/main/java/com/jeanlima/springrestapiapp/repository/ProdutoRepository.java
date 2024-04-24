package com.jeanlima.springrestapiapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jeanlima.springrestapiapp.model.Produto;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ProdutoRepository extends JpaRepository<Produto,Integer>{
    @Query("SELECT p FROM Produto p WHERE p.descricao = :nomeProduto")
    Optional<Produto> findByNome(String nomeProduto);
}
