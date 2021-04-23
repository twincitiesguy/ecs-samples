package com.dellemc.objectscale.sample;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import java.nio.charset.StandardCharsets;

public class PutObjectRetention {
    public static void main(String[] args) {
        S3Client s3Client = ClientFactory.createClient();
        String key = "retention-test";
        byte[] data = "Hello Retention".getBytes(StandardCharsets.UTF_8);


        String retentionPeriod = String.valueOf(3600); // 1 hour

        // set retention on single PUT
        s3Client.putObject(rBuilder -> rBuilder
                        .bucket("my-bucket")
                        .key("my-object")
                        .overrideConfiguration(oBuilder ->
                                oBuilder.putHeader("x-emc-retention-period", retentionPeriod))
                , RequestBody.fromBytes(data));

        // set retention on MPU
        s3Client.createMultipartUpload(mpu -> mpu
                .bucket("my-bucket")
                .key("my-object")
                .overrideConfiguration(override ->
                        override.putHeader("x-emc-retention-period", retentionPeriod)));
    }
}
