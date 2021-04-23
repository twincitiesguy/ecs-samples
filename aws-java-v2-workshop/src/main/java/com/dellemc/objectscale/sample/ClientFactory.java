package com.dellemc.objectscale.sample;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Factory class to create the AWS S3 client.  The client will be used in the examples.
 */
final class ClientFactory {
    /*
     * The end point of the S3 REST interface - this should take the form of
     * http://ecs-address:9020 or https://ecs-address:9021
     * or
     * https://object.ecstestdrive.com
     */
    static final String S3_ENDPOINT = "https://object.ecstestdrive.com";

    // the AWS profile name to use (will pull from the standard AWS credential chain)
    // credentials are stored in ${HOME}/.aws/credentials
    // (see https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html#cli-configure-files-where)
    static final String AWS_PROFILE = "default";

    // a unique bucket name to store objects
    public static final String S3_BUCKET = "workshop-bucket";

    // another unique bucket name to store objects for copying
    public static final String S3_BUCKET_2 = "workshop-bucket-2";

    // a unique bucket name to store versioned objects
    public static final String S3_VERSIONED_BUCKET = "workshop-versioned-bucket";

    // a unique object name
    public static final String S3_OBJECT = "workshop-object";

    // used for pre-signed/public URLs (if different)
    // for classic ECS, this should be a namespace-enabled baseURL w/ wildcard DNS & SSL
//    public static final String PUBLIC_ENDPOINT = "https://<namespace>.public.ecstestdrive.com";

    static S3Client createClient() {
        try {
            return S3Client.builder()
                    .endpointOverride(new URI(S3_ENDPOINT))
                    // below option is required if you use a proxy
                    .httpClientBuilder(ApacheHttpClient.builder()
                            .proxyConfiguration(ProxyConfiguration.builder()
                                    .endpoint(new URI("http://127.0.0.1:8888")).build())
                    )
                    .credentialsProvider(ProfileCredentialsProvider.create(AWS_PROFILE))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private ClientFactory() {
    }
}
