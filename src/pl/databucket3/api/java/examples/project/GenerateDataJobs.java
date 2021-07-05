package pl.databucket3.api.java.examples.project;

import pl.databucket3.api.java.client.Bucket;
import pl.databucket3.api.java.client.Data;
import pl.databucket3.api.java.client.Databucket;
import pl.databucket3.api.java.examples.structure.Buckets;
import pl.databucket3.api.java.examples.structure.Job;
import pl.databucket3.api.java.examples.structure.Tags;

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
        dataJobs.generateData(Buckets.DEV_JOBS);
//        dataJobs.generateData(Buckets.INT_JOBS);
    }

    public void generateDevIntJobs() {
        generateData(Buckets.DEV_JOBS);
        generateData(Buckets.INT_JOBS);
    }

    private void generateData(String bucketName) {
        Bucket jobsBucket = new Bucket(databucket, bucketName);
        Random ran = new Random();
        int count = ran.nextInt(10) + 30;
        for (int i = 0; i < count; i++) {
            String tag = getRandomTag();
            Data jobData = new Data(jobsBucket);
            jobData.setTag(tag);
            jobData.setProperty(Job.USER_ID, getRandomDataId());

            String jobName = getRandomJobName();
            jobData.setProperty(Job.JOB_NAME, jobName);

            if (jobName.startsWith("money"))
                jobData.setProperty(Job.VALUE, getRandomValue());

            if (tag.equals(Tags.DONE)) {
                jobData.setProperty(Job.STATUS, getRandomStatus());
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
            return Tags.TODO;
        else
            return Tags.DONE;
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
