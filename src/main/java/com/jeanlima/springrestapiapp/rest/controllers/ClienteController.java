package com.jeanlima.springrestapiapp.rest.controllers;

import java.util.List;

import com.jeanlima.springrestapiapp.model.ItemPedido;
import com.jeanlima.springrestapiapp.model.Pedido;
import com.jeanlima.springrestapiapp.rest.dto.InformacoesClientePedidosDTO;
import com.jeanlima.springrestapiapp.rest.dto.ItemPedidoDTO;
import com.jeanlima.springrestapiapp.rest.dto.PedidoDTO;
import com.jeanlima.springrestapiapp.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.jeanlima.springrestapiapp.model.Cliente;
import com.jeanlima.springrestapiapp.repository.ClienteRepository;



@RequestMapping("/api/clientes")
@RestController //anotação especializadas de controller - todos já anotados com response body!
public class ClienteController {

    @Autowired
    private ClienteRepository clientes;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("{id}")
    public Cliente getClienteById( @PathVariable Integer id ){
        return clientes
                .findById(id)
                .orElseThrow(() -> //se nao achar lança o erro!
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Cliente não encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente save( @RequestBody Cliente cliente ){
        return clientes.save(cliente);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete( @PathVariable Integer id ){
        clientes.findById(id)
                .map( cliente -> {
                    clientes.delete(cliente );
                    return cliente;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado") );

    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update( @PathVariable Integer id,
                        @RequestBody Cliente cliente ){
        clientes
                .findById(id)
                .map( clienteExistente -> {
                    cliente.setId(clienteExistente.getId());
                    clientes.save(cliente);
                    return clienteExistente;
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Cliente não encontrado") );
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patch( @PathVariable Integer id, @RequestBody Cliente clienteDetalhes ){
        Cliente client = clientes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado"));

        if (clienteDetalhes.getNome() != null) {
            client.setNome(clienteDetalhes.getNome());
        }
        if (clienteDetalhes.getCpf() != null) {
            client.setCpf(clienteDetalhes.getCpf());
        }

        clientes.save(client);
    }


    @GetMapping
    public List<Cliente> find( Cliente filtro ){
        ExampleMatcher matcher = ExampleMatcher
                                    .matching()
                                    .withIgnoreCase()
                                    .withStringMatcher(
                                            ExampleMatcher.StringMatcher.CONTAINING );

        Example example = Example.of(filtro, matcher);
        return clientes.findAll(example);
    }

    @GetMapping("detalhes/{id}")
    public InformacoesClientePedidosDTO getInformacoesClientePedidos(@PathVariable Integer id){
        Cliente cliente = clientes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado"));

        List<Pedido> pedidos = pedidoService.obterPedidosPorCliente(cliente);

        return InformacoesClientePedidosDTO
                .builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cpf(cliente.getCpf())
                .pedidos(
                        pedidos
                                .stream()
                                .map( pedido -> {
                                    return PedidoDTO.builder()
                                            .cliente(cliente.getId())
                                            .total(pedido.getTotal())
                                            .items(pedido.getItens()
                                                    .stream()
                                                    .map(item -> {
                                                        return ItemPedidoDTO
                                                                .builder()
                                                                .produto(item.getProduto().getId())
                                                                .quantidade(item.getQuantidade())
                                                                .build();
                                                    }).toList())
                                            .build();
                                }).toList())
                .build();
    }

}
