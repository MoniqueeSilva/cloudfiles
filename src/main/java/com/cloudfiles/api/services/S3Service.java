package com.cloudfiles.api.services;

import com.cloudfiles.api.dtos.ArquivoResponseDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
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
            String nomeOriginal = arquivo.getOriginalFilename();

            String extensao = "";
            if (nomeOriginal != null && nomeOriginal.contains(".")) {
                extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
            }

            String key = UUID.randomUUID() + extensao;

            String nomeCodificado = Base64.getEncoder()
                    .encodeToString(nomeOriginal.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(
                            arquivo.getContentType() != null
                                    ? arquivo.getContentType()
                                    : "application/octet-stream")
                    .metadata(java.util.Map.of(
                            "nome-original", nomeCodificado))
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            arquivo.getInputStream(),
                            arquivo.getSize()));

            String url = String.format(
                    "https://%s.s3.%s.amazonaws.com/%s",
                    bucketName,
                    region,
                    key);

            return new ArquivoResponseDTO(
                    key,
                    nomeOriginal,
                    url,
                    arquivo.getSize(),
                    arquivo.getContentType());

        } catch (IOException e) {
            throw new RuntimeException("Falha ao processar o arquivo", e);
        }
    }

    public List<ArquivoResponseDTO> listarArquivos() {

        List<ArquivoResponseDTO> arquivos = new ArrayList<>();

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response response;

        do {
            response = s3Client.listObjectsV2(request);

            for (S3Object objeto : response.contents()) {

                HeadObjectResponse detalhes = s3Client.headObject(
                        HeadObjectRequest.builder()
                                .bucket(bucketName)
                                .key(objeto.key())
                                .build());

                String nomeOriginal = detalhes.metadata()
                        .getOrDefault("nome-original", objeto.key());

                try {
                    nomeOriginal = new String(
                            Base64.getDecoder().decode(nomeOriginal),
                            java.nio.charset.StandardCharsets.UTF_8);
                } catch (IllegalArgumentException e) {
                    // Mantém o nome armazenado caso não esteja codificado.
                }

                String url = String.format(
                        "https://%s.s3.%s.amazonaws.com/%s",
                        bucketName,
                        region,
                        objeto.key());

                arquivos.add(new ArquivoResponseDTO(
                        objeto.key(),
                        nomeOriginal,
                        url,
                        objeto.size(),
                        detalhes.contentType()));
            }

            request = request.toBuilder()
                    .continuationToken(response.nextContinuationToken())
                    .build();

        } while (response.isTruncated());

        return arquivos;
    }
}
