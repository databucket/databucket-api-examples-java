package pl.databucket3.api.java.examples.project;

import pl.databucket3.api.java.client.Bucket;
import pl.databucket3.api.java.client.Data;
import pl.databucket3.api.java.client.Databucket;
import pl.databucket3.api.java.examples.structure.Buckets;
import pl.databucket3.api.java.examples.structure.Config;
import pl.databucket3.api.java.examples.structure.Env;


public class GenerateDataConfig {
    private final String url = "http://localhost:8080";
    private final boolean logs = true;
    private final String username = "super";
    private final String password = "super";
    private final int projectId = 1;
    private Databucket databucket;

    public GenerateDataConfig() {
        databucket = new Databucket(url, username, password, projectId, logs);
    }

    public GenerateDataConfig(Databucket databucket) {
        this.databucket = databucket;
    }

    public static void main(String[] args) {
        new GenerateDataConfig().generateData(Buckets.CONFIG);
    }

    public void generateConfig() {
        generateData(Buckets.CONFIG);
    }

    private void generateData(String bucketName) {
        Bucket conf = new Bucket(databucket, bucketName);
        conf.insertData(getConfigData(conf, true,  Env.DEFAULT, "SSH_USER_NAME", "undefined", false, null));
        conf.insertData(getConfigData(conf, true,  Env.DEV, "SSH_USER_NAME", "w5DDlMOTw5fDlMOrddss", true, null));
        conf.insertData(getConfigData(conf, false,  Env.INT, "SSH_USER_NAME", "w5vDn8OPw4zDiMOTw5fd", true, null));
        conf.insertData(getConfigData(conf, true,  Env.PRD, "SSH_USER_NAME", "mMOIwpnDh8OfwprDn8OP", true, null));

        conf.insertData(getConfigData(conf, true,  Env.DEFAULT, "SSH_USER_PASS", "undefined", false, null));
        conf.insertData(getConfigData(conf, true,  Env.DEV, "SSH_USER_PASS", "w5rDjsKVwqPCnMKiwpzCpMKYwp_CmcK", true, null));
        conf.insertData(getConfigData(conf, false,  Env.INT, "SSH_USER_PASS", "w5zDkMOWw57DnMOhw57Ekw==ddsadad", true, null));
        conf.insertData(getConfigData(conf, true,  Env.PRD, "SSH_USER_PASS", "Di8OWw6TCocKYw5rDjcOrwrPDdd2eed", true, null));

        conf.insertData(getConfigData(conf, true,  Env.DEFAULT, "APP_EXAMPLE_URL", "undefined", false, null));
        conf.insertData(getConfigData(conf, true,  Env.DEV, "APP_EXAMPLE_URL", "https://example.app.dev.com", false, null));
        conf.insertData(getConfigData(conf, true,  Env.INT, "APP_EXAMPLE_URL", "https://example.app.int.com", false, null));
        conf.insertData(getConfigData(conf, true,  Env.PRD, "APP_EXAMPLE_URL", "https://example.app.prd.com", false, null));

        conf.insertData(getConfigData(conf, true,  Env.DEFAULT, "ENABLE_BLOCK_PAYMENT", "True", false, null));
        conf.insertData(getConfigData(conf, true,  Env.DEV, "ENABLE_BLOCK_PAYMENT", "False", false, null));
        conf.insertData(getConfigData(conf, false,  Env.INT, "ENABLE_BLOCK_PAYMENT", "False", false, null));
        conf.insertData(getConfigData(conf, false,  Env.PRD, "ENABLE_BLOCK_PAYMENT", "False", false, null));

        conf.insertData(getConfigData(conf, true,  Env.DEFAULT, "APP_DEPLOY_TOKEN", "undefined", false, null));
        conf.insertData(getConfigData(conf, true,  Env.DEV, "APP_DEPLOY_TOKEN", "5XCosKtwqLDi8OWw6TCocKYw5rDjcOrwrPDhMKlw43CncOVw4PDlw", true, null));
        conf.insertData(getConfigData(conf, true,  Env.INT, "APP_DEPLOY_TOKEN", "5vDn8OPw4zDiMOTw5_DmMOIwpjDhsOVw4_DlMOTw5_Ck8OUw5nCnc", true, null));
        conf.insertData(getConfigData(conf, true,  Env.PRD, "APP_DEPLOY_TOKEN", "5Xq7CvMKkw4_DksObw5q7CvMKkw4_DksObw5w5rDjsKYwqPCnsKqw", true, null));
    }

    private Data getConfigData(Bucket bucket, boolean active, String env, String key, String val, boolean encrypted, String description) {
        Data data = new Data(bucket);
        data.setProperty(Config.ACTIVE, active);
        data.setProperty(Config.ENVIRONMENT, env);
        data.setProperty(Config.KEY, key);
        data.setProperty(Config.VALUE, val);
        data.setProperty(Config.ENCRYPTED, encrypted);
        data.setProperty(Config.DESCRIPTION, description);
        return data;
    }
}
