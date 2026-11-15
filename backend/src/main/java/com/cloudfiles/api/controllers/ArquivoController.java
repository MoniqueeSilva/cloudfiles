package com.cloudfiles.api.controllers;

import com.cloudfiles.api.dtos.ArquivoResponseDTO;
import com.cloudfiles.api.services.S3Service;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// Recebe as requisições do front e encaminha ao S3Service
@RestController
@RequestMapping("/api/arquivos")
public class ArquivoController {

    private final S3Service s3Service;

    public ArquivoController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    // Recebe um arquivo e solicita o envio ao S3
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArquivoResponseDTO> enviarArquivo(
            @RequestParam("arquivo") MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (arquivo.getOriginalFilename() == null ||
                arquivo.getOriginalFilename().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        ArquivoResponseDTO resposta = s3Service.upload(arquivo);

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    // Solicita a listagem dos arquivos armazenados
    @GetMapping
    public ResponseEntity<List<ArquivoResponseDTO>> listarArquivos() {
        return ResponseEntity.ok(s3Service.listarArquivos());
    }
}


//Métodos que tem que ser implementados no S3Service:
/*public ArquivoResponseDTO upload(MultipartFile arquivo);

public List<ArquivoResponseDTO> listarArquivos(); */