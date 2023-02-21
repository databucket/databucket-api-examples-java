package pl.databucket.api.java.examples.project;

import pl.databucket.api.java.client.Bucket;
import pl.databucket.api.java.client.Data;
import pl.databucket.api.java.client.Databucket;
import pl.databucket.api.java.examples.constants.BucketName;
import pl.databucket.api.java.examples.constants.ConfigProp;
import pl.databucket.api.java.examples.constants.EnvName;


public class GenerateDataConfig {
    private final String url = "http://localhost:8080";
    private final boolean logs = true;
    private final String username = "super";
    private final String password = "super";
    private final int projectId = 22;
    private Databucket databucket;

    public GenerateDataConfig() {
        databucket = new Databucket(url, username, password, projectId, logs);
    }

    public GenerateDataConfig(Databucket databucket) {
        this.databucket = databucket;
    }

    public static void main(String[] args) {
        new GenerateDataConfig().generateData(BucketName.CONFIG);
    }

    public void generateConfig() {
        generateData(BucketName.CONFIG);
    }

    private void generateData(String bucketName) {
        Bucket conf = new Bucket(databucket, bucketName);
        conf.insertData(getConfigData(conf, true,  EnvName.DEFAULT, "SSH_USER_NAME", "undefined", false, null));
        conf.insertData(getConfigData(conf, true,  EnvName.DEV, "SSH_USER_NAME", "w5DDlMOTw5fDlMOrddss", true, null));
        conf.insertData(getConfigData(conf, false,  EnvName.INT, "SSH_USER_NAME", "w5vDn8OPw4zDiMOTw5fd", true, null));
        conf.insertData(getConfigData(conf, true,  EnvName.PRD, "SSH_USER_NAME", "mMOIwpnDh8OfwprDn8OP", true, null));

        conf.insertData(getConfigData(conf, true,  EnvName.DEFAULT, "SSH_USER_PASS", "undefined", false, null));
        conf.insertData(getConfigData(conf, true,  EnvName.DEV, "SSH_USER_PASS", "w5rDjsKVwqPCnMKiwpzCpMKYwp_CmcK", true, null));
        conf.insertData(getConfigData(conf, false,  EnvName.INT, "SSH_USER_PASS", "w5zDkMOWw57DnMOhw57Ekw==ddsadad", true, null));
        conf.insertData(getConfigData(conf, true,  EnvName.PRD, "SSH_USER_PASS", "Di8OWw6TCocKYw5rDjcOrwrPDdd2eed", true, null));

        conf.insertData(getConfigData(conf, true,  EnvName.DEFAULT, "APP_EXAMPLE_URL", "undefined", false, null));
        conf.insertData(getConfigData(conf, true,  EnvName.DEV, "APP_EXAMPLE_URL", "https://example.app.dev.com", false, null));
        conf.insertData(getConfigData(conf, true,  EnvName.INT, "APP_EXAMPLE_URL", "https://example.app.int.com", false, null));
        conf.insertData(getConfigData(conf, true,  EnvName.PRD, "APP_EXAMPLE_URL", "https://example.app.prd.com", false, null));

        conf.insertData(getConfigData(conf, true,  EnvName.DEFAULT, "ENABLE_BLOCK_PAYMENT", "True", false, null));
        conf.insertData(getConfigData(conf, true,  EnvName.DEV, "ENABLE_BLOCK_PAYMENT", "False", false, null));
        conf.insertData(getConfigData(conf, false,  EnvName.INT, "ENABLE_BLOCK_PAYMENT", "False", false, null));
        conf.insertData(getConfigData(conf, false,  EnvName.PRD, "ENABLE_BLOCK_PAYMENT", "False", false, null));

        conf.insertData(getConfigData(conf, true,  EnvName.DEFAULT, "APP_DEPLOY_TOKEN", "undefined", false, null));
        conf.insertData(getConfigData(conf, true,  EnvName.DEV, "APP_DEPLOY_TOKEN", "5XCosKtwqLDi8OWw6TCocKYw5rDjcOrwrPDhMKlw43CncOVw4PDlw", true, null));
        conf.insertData(getConfigData(conf, true,  EnvName.INT, "APP_DEPLOY_TOKEN", "5vDn8OPw4zDiMOTw5_DmMOIwpjDhsOVw4_DlMOTw5_Ck8OUw5nCnc", true, null));
        conf.insertData(getConfigData(conf, true,  EnvName.PRD, "APP_DEPLOY_TOKEN", "5Xq7CvMKkw4_DksObw5q7CvMKkw4_DksObw5w5rDjsKYwqPCnsKqw", true, null));
    }

    private Data getConfigData(Bucket bucket, boolean active, String env, String key, String val, boolean encrypted, String description) {
        Data data = new Data(bucket);
        data.setProperty(ConfigProp.ACTIVE, active);
        data.setProperty(ConfigProp.ENVIRONMENT, env);
        data.setProperty(ConfigProp.KEY, key);
        data.setProperty(ConfigProp.VALUE, val);
        data.setProperty(ConfigProp.ENCRYPTED, encrypted);
        data.setProperty(ConfigProp.DESCRIPTION, description);
        return data;
    }
}
