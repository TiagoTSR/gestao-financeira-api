package br.com.decodex.gestaofinanceira.storage;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.com.decodex.gestaofinanceira.config.property.GestaoApiProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3 {

    private static final Logger logger = LoggerFactory.getLogger(S3.class);

    @Autowired
    private GestaoApiProperty property;

    @Autowired
    private S3Client s3Client;

    public String salvarTemporariamente(MultipartFile arquivo) {
        String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(property.getS3().getBucket())
                    .key(nomeUnico)
                    .contentType(arquivo.getContentType())  
                    .contentLength(arquivo.getSize())       
                    .build(); // Removida a ACL

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(arquivo.getInputStream(), arquivo.getSize()));

            if (logger.isDebugEnabled()) {
                logger.debug("Arquivo {} enviado com sucesso para o S3.", arquivo.getOriginalFilename());
            }

            return nomeUnico;
        } catch (IOException e) {
            throw new RuntimeException("Problemas ao tentar enviar o arquivo para o S3.", e);
        }
    }

    public String configurarUrl(String objeto) {
        return "https://" + property.getS3().getBucket() + ".s3.amazonaws.com/" + objeto;
    }

    private String gerarNomeUnico(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }
}