package com.jeanlima.springrestapiapp.service.impl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jeanlima.springrestapiapp.model.*;
import com.jeanlima.springrestapiapp.repository.*;
import org.springframework.stereotype.Service;

import com.jeanlima.springrestapiapp.enums.StatusPedido;
import com.jeanlima.springrestapiapp.exception.PedidoNaoEncontradoException;
import com.jeanlima.springrestapiapp.exception.RegraNegocioException;
import com.jeanlima.springrestapiapp.rest.dto.ItemPedidoDTO;
import com.jeanlima.springrestapiapp.rest.dto.PedidoDTO;
import com.jeanlima.springrestapiapp.service.PedidoService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    
    private final PedidoRepository repository;
    private final ClienteRepository clientesRepository;
    private final ProdutoRepository produtosRepository;
    private final ItemPedidoRepository itemsPedidoRepository;
    private final EstoqueRepository estoqueRepository;
    private final PedidoRepository pedidoRepository;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido."));

        Pedido pedido = new Pedido();

        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        List<ItemPedido> itemsPedido = converterItems(pedido, dto.getItems());

        for (ItemPedido itemPedido : itemsPedido) {
            Produto produto = itemPedido.getProduto();
            Integer quantidade = itemPedido.getQuantidade();

            Estoque estoque = estoqueRepository.findByProdutoId(produto.getId());

            if(estoque.getQuantidade() < quantidade || estoque == null){
                throw new RegraNegocioException("Não há estoque suficiente para o produto "+ produto.getId());
            }

            estoque.setQuantidade(estoque.getQuantidade() - quantidade);

            estoqueRepository.save(estoque);

        }

        pedido.setTotal(itemsPedido
                .stream()
                .map(
                        i -> i.getProduto()
                                .getPreco()
                                .multiply(
                                        new BigDecimal(i.getQuantidade()
                                        ))).reduce(BigDecimal.ZERO, BigDecimal::add));

        repository.save(pedido);
        itemsPedidoRepository.saveAll(itemsPedido);
        pedido.setItens(itemsPedido);
        return pedido;
    }

    @Override
    public boolean deletar(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private List<ItemPedido> converterItems(Pedido pedido, List<ItemPedidoDTO> items){
        if(items.isEmpty()){
            throw new RegraNegocioException("Não é possível realizar um pedido sem items.");
        }



        return items
                .stream()
                .map( dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtosRepository
                            .findById(idProduto)
                            .orElseThrow(
                                    () -> new RegraNegocioException(
                                            "Código de produto inválido: "+ idProduto
                                    ));

                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());

    }
    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return repository.findByIdFetchItens(id);
    }
    @Override
    public void atualizaStatus(Integer id, StatusPedido statusPedido) {
        repository
        .findById(id)
        .map( pedido -> {
            pedido.setStatus(statusPedido);
            return repository.save(pedido);
        }).orElseThrow(() -> new PedidoNaoEncontradoException() );
    }

    @Override
    public List<Pedido> obterPedidosPorCliente(Cliente cliente) {
        return repository.findByCliente(cliente);
    }

    @Override
    public void atualizar(Integer id, PedidoDTO dto) {

    }

    @Override
    public PedidoDTO atualizarCliente(Integer idPedido, Integer idCliente) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(PedidoNaoEncontradoException::new);

        Cliente cliente = clientesRepository.findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido."));

        pedido.setCliente(cliente);

        pedidoRepository.save(pedido);

        return PedidoDTO.builder()
                .cliente(cliente.getId())
                .total(pedido.getTotal())
                .items(
                        pedido.getItens()
                                .stream()
                                .map(item -> ItemPedidoDTO.builder()
                                        .produto(item.getProduto().getId())
                                        .quantidade(item.getQuantidade())
                                        .build()
                                ).collect(Collectors.toList())
                ).build();
    }


}
