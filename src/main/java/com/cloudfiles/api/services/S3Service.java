package com.cloudfiles.api.services;

import com.cloudfiles.api.dtos.ArquivoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public ArquivoResponseDTO upload(MultipartFile arquivo) {
        try {
            // Gera um nome único para o arquivo para evitar sobrescrever arquivos com o mesmo nome
            String nomeOriginal = arquivo.getOriginalFilename();
            String extensao = "";
            if (nomeOriginal != null && nomeOriginal.contains(".")) {
                extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
            }
            String key = UUID.randomUUID().toString() + extensao;

            // Configura a requisição para o S3
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(arquivo.getContentType())
                    .build();

            // Envia o arquivo para o S3
            s3Client.putObject(request, RequestBody.fromInputStream(arquivo.getInputStream(), arquivo.getSize()));

            // Monta a URL do arquivo (usaremos o S3 padrão por enquanto, o CloudFront virá depois)
            String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);

            return new ArquivoResponseDTO(key, nomeOriginal, url, arquivo.getSize(), arquivo.getContentType());

        } catch (IOException e) {
            throw new RuntimeException("Falha ao processar o arquivo", e);
        }
    }

    public List<ArquivoResponseDTO> listarArquivos() {
        // Implementação futura para listar arquivos
        return List.of();
    }
}