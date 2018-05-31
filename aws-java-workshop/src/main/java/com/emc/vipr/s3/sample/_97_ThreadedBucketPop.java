package com.emc.vipr.s3.sample;

import com.amazonaws.services.s3.AmazonS3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class _97_ThreadedBucketPop implements Runnable {

    private ExecutorService executor;
    private AtomicLong createdObjects = new AtomicLong(0);

    public static void main(String[] args) throws Exception {

        _97_ThreadedBucketPop pop = new _97_ThreadedBucketPop();

        // update the user
        final AtomicBoolean monitorRunning = new AtomicBoolean(true);
        final _97_ThreadedBucketPop fPop = pop;
        Thread statusThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (monitorRunning.get()) {
                    try {
                        System.out.println("Objects created: " + fPop.getCreatedObjects() + "\r");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
        });
        statusThread.setDaemon(true);
        statusThread.start();

        pop.run();


    }

    @Override
    public void run() {
        executor = Executors.newFixedThreadPool(64);
        AmazonS3 s3 = AWSS3Factory.getS3Client();
        int keyIndex = 0;

        while (keyIndex < 1000000) {
            String key = String.format("key-%s-plymouth", keyIndex);
            executor.submit(new CreateObject(s3, AWSS3Factory.S3_BUCKET, key));
            keyIndex = keyIndex + 1;
        }
    }

    protected class CreateObject implements Runnable {
        private AmazonS3 client;
        private String bucket;
        private String key;

        public CreateObject(AmazonS3 client, String bucket, String key) {
            this.client = client;
            this.bucket = bucket;
            this.key = key;
        }

        @Override
        public void run() {
            String content = String.format("sample content for %s object ...", key);
            int count = 0;
            int maxRetry = 10;
            while (true) {
                try {
                    //client.putObject(bucket, key, content);
                    createdObjects.incrementAndGet();
                    break;
                } catch (Exception e) {
                    if (++count == maxRetry)
                        throw e;
                }
            }
       }
    }

    public long getCreatedObjects() {
        return createdObjects.get();
    }
}
