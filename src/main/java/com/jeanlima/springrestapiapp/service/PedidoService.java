package com.jeanlima.springrestapiapp.service;

import java.util.List;
import java.util.Optional;

import com.jeanlima.springrestapiapp.enums.StatusPedido;
import com.jeanlima.springrestapiapp.model.Cliente;
import com.jeanlima.springrestapiapp.model.Pedido;
import com.jeanlima.springrestapiapp.rest.dto.PedidoDTO;



public interface PedidoService {
    Pedido salvar( PedidoDTO dto );
    boolean deletar(Integer id);
    Optional<Pedido> obterPedidoCompleto(Integer id);
    void atualizaStatus(Integer id, StatusPedido statusPedido);
    List<Pedido> obterPedidosPorCliente(Cliente cliente);
    void atualizar(Integer id, PedidoDTO dto);
    PedidoDTO atualizarCliente(Integer idPedido, Integer idCliente);
}
