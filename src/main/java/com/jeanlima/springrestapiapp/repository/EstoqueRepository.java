package com.jeanlima.springrestapiapp.repository;

import com.jeanlima.springrestapiapp.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface EstoqueRepository extends JpaRepository<Estoque,Integer> {

    //Query to get estoque from produto id
    Estoque findByProdutoId(@Param("produto_id") Integer produto_id);
}
