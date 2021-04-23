package com.dellemc.objectscale.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;

public class CustomStorageClassTest extends AbstractBucketTest {
    private static final Logger log = LoggerFactory.getLogger(CustomStorageClassTest.class);

    static final String BUCKET = "custom-storage-class-test";
    static final String KEY = "custom-storage-class-test";

    @Override
    String getBucketPrefix() {
        return BUCKET;
    }

    // NOTE: you'll need to examine the request headers to see if the correct x-amz-storage-class value is sent
    @Test
    @Order(20)
    public void testCreateObject() {
        client.putObject(builder -> builder
                        .bucket(BUCKET)
                        .key(KEY)
                        .storageClass("foo")
                , RequestBody.fromString(""));
        // success is implied if no exceptions
    }

    @Test
    @Order(30)
    public void testHeadObject() {
        String storageClass = client.headObject(builder -> builder
                .bucket(BUCKET)
                .key(KEY)).storageClassAsString();

        log.info("key: {}, storage-class: {}", KEY, storageClass);
        Assertions.assertNotNull(storageClass);
    }

    @Test
    @Order(40)
    public void testGetObject() throws IOException {
        try (ResponseInputStream<GetObjectResponse> responseStream = client.getObject(builder -> builder
                .bucket(BUCKET)
                .key(KEY))) {
            String storageClass = responseStream.response().storageClassAsString();

            responseStream.abort();

            log.info("key: {}, storage-class: {}", KEY, storageClass);
            Assertions.assertNotNull(storageClass);
        }
    }

    @Test
    @Order(50)
    public void testListObjects() {
        client.listObjects(builder -> builder.bucket(BUCKET))
                .contents().forEach(object -> {
            log.info("key: {}, storage-class: {}", object.key(), object.storageClassAsString());
            Assertions.assertNotNull(object.storageClassAsString());
        });
    }
}
