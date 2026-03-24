package br.com.decodex.gestaofinanceira.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.decodex.gestaofinanceira.config.property.GestaoApiProperty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketLifecycleConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.LifecycleExpiration;
import software.amazon.awssdk.services.s3.model.LifecycleRule;
import software.amazon.awssdk.services.s3.model.LifecycleRuleFilter;
import software.amazon.awssdk.services.s3.model.Tag;

@Configuration
public class S3Config {

    @Autowired
    private GestaoApiProperty property;
    
    @Value("${gerencia.s3.accessKeyId}")
    private String accessKeyId;

    @Value("${gerencia.s3.secretAccessKey}")
    private String secretAccessKey;

    @Value("${gerencia.s3.bucket}")
    private String bucket;

    @Value("${gerencia.s3.region}")
    private String region;
  
    @Bean
    public S3Client s3Client() {
        // Verificando se as variáveis estão sendo lidas corretamente
        String accessKeyId = property.getS3().getAccessKeyId();
        String secretAccessKey = property.getS3().getSecretAccessKey();
        String bucketName = property.getS3().getBucket();

        
        if (accessKeyId == null || secretAccessKey == null || bucketName == null) {
            throw new IllegalArgumentException("AWS Credentials or Bucket Name are missing in the configuration.");
        }

        // Criação das credenciais AWS
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        // Criação do cliente S3
        S3Client s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_2) // Defina sua região aqui, se necessário
                .build();

        // Verificar se o bucket existe e criar se necessário
        if (!s3Client.listBuckets().buckets().stream().anyMatch(bucket -> bucket.name().equals(bucketName))) {
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());

                // Configuração de Lifecycle para expiração dos arquivos
                LifecycleExpiration expiration = LifecycleExpiration.builder()
                        .days(1)  // Define a expiração após 1 dia
                        .build();

                // Define the tag filter for the lifecycle rule
                LifecycleRuleFilter filter = LifecycleRuleFilter.builder()
                        .tag(Tag.builder().key("expirar").value("true").build())  // Tag filter applied
                        .build();

                // Define the expiration rule
                LifecycleRule expirationRule = LifecycleRule.builder()
                        .id("Temporary Files Expiration Rule")
                        .filter(filter)  // Apply the filter here
                        .expiration(expiration)  // Set the expiration
                        .status("Enabled") // Set the rule status as Enabled
                        .build();

                BucketLifecycleConfiguration lifecycleConfiguration = BucketLifecycleConfiguration.builder()
                        .rules(expirationRule)
                        .build();

                // Definir a política de expiração no bucket
                s3Client.putBucketLifecycleConfiguration(builder -> builder
                        .bucket(bucketName)
                        .lifecycleConfiguration(lifecycleConfiguration));

            } catch (Exception e) {
                throw new RuntimeException("Error creating bucket or setting lifecycle configuration", e);
            }
        }

        return s3Client;
    }
}