package pl.databucket.api.java.examples;

import pl.databucket.api.java.client.Bucket;
import pl.databucket.api.java.client.Databucket;
import pl.databucket.api.java.examples.constants.BucketName;
import pl.databucket.api.java.examples.constants.ServerConfig;

public class DatabucketInitialization {

    public Databucket databucket = new Databucket(
            ServerConfig.SERVER_URL,
            ServerConfig.USER_NAME,
            ServerConfig.PASSWORD,
            ServerConfig.PROJECT_ID,
            ServerConfig.DEBUG_LOG
    );

    public Bucket bucketDevUsers = new Bucket(databucket, BucketName.DEV_USERS);
    public Bucket bucketIntUsers = new Bucket(databucket, BucketName.INT_USERS);

}
