package com.cloudfiles.api.dtos;

//DTO conforme o contrato que definimos (com os campos id, nome, url, tamanho, tipo).
public record ArquivoResponseDTO(
    String id,
    String nome,
    String url,
    Long tamanho,
    String tipo
) {}