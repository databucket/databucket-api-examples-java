package pl.databucket.api.java.examples.project;

import pl.databucket.api.java.client.Bucket;
import pl.databucket.api.java.client.Data;
import pl.databucket.api.java.client.Databucket;
import pl.databucket.api.java.examples.constants.BucketName;
import pl.databucket.api.java.examples.constants.JobProp;
import pl.databucket.api.java.examples.constants.TagName;

import java.util.Random;

public class GenerateDataJobs {
    private final String url = "http://localhost:8080";
    private final boolean logs = true;
    private final String username = "super";
    private final String password = "super";
    private final int projectId = 1;
    private Databucket databucket;

    public GenerateDataJobs() {
        databucket = new Databucket(url, username, password, projectId, logs);
    }

    public GenerateDataJobs(Databucket databucket) {
        this.databucket = databucket;
    }

    public static void main(String[] args) {
        GenerateDataJobs dataJobs = new GenerateDataJobs();
        dataJobs.generateData(BucketName.DEV_JOBS);
//        dataJobs.generateData(Buckets.INT_JOBS);
    }

    public void generateDevIntJobs() {
        generateData(BucketName.DEV_JOBS);
        generateData(BucketName.INT_JOBS);
    }

    private void generateData(String bucketName) {
        Bucket jobsBucket = new Bucket(databucket, bucketName);
        Random ran = new Random();
        int count = ran.nextInt(10) + 30;
        for (int i = 0; i < count; i++) {
            String tag = getRandomTag();
            Data jobData = new Data(jobsBucket);
            jobData.setTag(tag);
            jobData.setProperty(JobProp.USER_ID, getRandomDataId());

            String jobName = getRandomJobName();
            jobData.setProperty(JobProp.JOB_NAME, jobName);

            if (jobName.startsWith("money"))
                jobData.setProperty(JobProp.VALUE, getRandomValue());

            if (tag.equals(TagName.DONE)) {
                jobData.setProperty(JobProp.STATUS, getRandomStatus());
            }

            jobsBucket.insertData(jobData);
        }
    }

    private String getRandomJobName() {
        double rand = Math.random();

        if (rand <= 0.1)
            return "base-create-new-user";
        else if (rand <= 0.2)
            return "base-remove-all-credit-cards";
        else if (rand <= 0.4)
            return "base-suspend-user";
        else if (rand <= 0.6)
            return "base-reactivate-user";
        else if (rand <= 0.8)
            return "money-increase";
        else
            return "money-reduce";
    }

    private String getRandomTag() {
        double rand = Math.random();
        if (rand < 0.6)
            return TagName.TODO;
        else
            return TagName.DONE;
    }

    private int getRandomDataId() {
        Random ran = new Random();
        return ran.nextInt(100000) + 200;
    }

    private int getRandomValue() {
        Random ran = new Random();
        return ran.nextInt(20) + 1;
    }

    private String getRandomStatus() {
        double rand = Math.random();

        if (rand <= 0.7)
            return "passed";
        else if (rand <= 0.8)
            return "failed";
        else
            return "warning";
    }

}
