package com.jeanlima.springrestapiapp.rest.dto;

import com.jeanlima.springrestapiapp.model.Estoque;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstoqueDTO {
    private Integer id;
    private Integer produto;
    private Integer quantidade;

}
