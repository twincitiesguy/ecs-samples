package com.dellemc.objectscale.sample;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.util.Random;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractBucketTest {
    private static final Random random = new Random();
    private String fullBucketName;

    S3Client client;

    abstract String getBucketPrefix();

    String getFullBucketName() {
        if (fullBucketName == null) {
            synchronized (this) {
                if (fullBucketName == null) {
                    fullBucketName = getBucketPrefix() + "-" + (random.nextInt(899999) + 100000);
                }
            }
        }
        return fullBucketName;
    }

    @BeforeAll
    public void initClientAndBucket() {
        client = ClientFactory.createClient();
        client.createBucket(CreateBucketRequest.builder()
                .bucket(getFullBucketName())
                .build());
    }

    @AfterAll
    public void destroyBucket() {
        client.listObjects(builder -> builder.bucket(getFullBucketName()))
                .contents().forEach(object ->
                client.deleteObject(builder -> builder
                        .bucket(getFullBucketName())
                        .key(object.key())));
        client.deleteBucket(builder -> builder.bucket(getFullBucketName()));
    }
}
