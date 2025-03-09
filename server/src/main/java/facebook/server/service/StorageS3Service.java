package facebook.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
@Service
public class StorageS3Service {
    @Value("${filebase.endpoint}")
    private String filebaseEndpoint;

    @Value("${filebase.bucket.name}")
    private String bucketName;

    @Autowired
    private S3Client s3Client;

    public void uploadFile(MultipartFile file, String uploadName) {
        if(file.isEmpty())
            throw new IllegalArgumentException("File name cannot be empty.");

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty.");
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uploadName)
                .build();

        try {
            PutObjectResponse putResp = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            System.out.println("[" + java.time.LocalDateTime.now() + "] AWS Response: " + putResp);
        } catch (IOException e) {
            System.out.println("\u001B[33m[" + java.time.LocalDateTime.now() + "] AWS ERROR: " + e.getMessage() + "\u001B[0m");
        }
    }

    public byte[] getImage(String imageName) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(imageName)
                .build();

        ResponseInputStream<GetObjectResponse> s3ObjectStream = s3Client.getObject(getObjectRequest);

        return s3ObjectStream.readAllBytes();
    }
}
