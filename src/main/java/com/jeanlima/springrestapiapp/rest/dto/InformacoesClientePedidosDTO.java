package com.jeanlima.springrestapiapp.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformacoesClientePedidosDTO {
    private Integer id;
    private String cpf;
    private String nome;
    private List<PedidoDTO> pedidos;
}
