package com.dellemc.objectscale.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.EncodingType;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EncodingTypeTest extends AbstractBucketTest {
    String plainTextKey = "abcd1234", encodableKey = "abcd%&1234";

    @Override
    String getBucketPrefix() {
        return "encoding-type-test";
    }

    @BeforeAll
    public void createObjects() {
        client.putObject(builder -> builder
                        .bucket(getFullBucketName())
                        .key(plainTextKey),
                RequestBody.fromString(""));
        client.putObject(builder -> builder
                        .bucket(getFullBucketName())
                        .key(encodableKey),
                RequestBody.fromString(""));
    }

    @Test
    public void testDefaultEncoding() {
        ListObjectsResponse response = client.listObjects(builder -> builder
                .bucket(getFullBucketName()));
        // make sure encodingType is null by default (we did not set it)
        Assertions.assertNull(response.encodingType());
        // make sure all keys are decoded automatically by SDK
        Assertions.assertTrue(response.contents().stream().anyMatch(o -> o.key().equals(plainTextKey)));
        Assertions.assertTrue(response.contents().stream().anyMatch(o -> o.key().equals(encodableKey)));
    }

    @Test
    public void testExplicitEncoding() {
        ListObjectsResponse response = client.listObjects(builder -> builder
                .bucket(getFullBucketName())
                .encodingType(EncodingType.URL));
        // make sure encodingType is set in response (we did set it in the request)
        Assertions.assertEquals(EncodingType.URL, response.encodingType());
        // make sure all keys are decoded automatically by SDK
        Assertions.assertTrue(response.contents().stream().anyMatch(o -> o.key().equals(plainTextKey)));
        Assertions.assertTrue(response.contents().stream().anyMatch(o -> o.key().equals(encodableKey)));
    }

    @Test
    public void testDisableEncoding() {

    }
}
