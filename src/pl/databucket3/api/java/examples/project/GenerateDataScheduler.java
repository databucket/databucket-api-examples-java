package pl.databucket3.api.java.examples.project;

import pl.databucket3.api.java.client.Bucket;
import pl.databucket3.api.java.client.Data;
import pl.databucket3.api.java.client.Databucket;
import pl.databucket3.api.java.examples.structure.Buckets;
import pl.databucket3.api.java.examples.structure.Scheduler;
import pl.databucket3.api.java.examples.structure.Tags;
import java.util.*;

public class GenerateDataScheduler {
    private final String url = "http://localhost:8080";
    private final boolean logs = true;
    private final String username = "super";
    private final String password = "super";
    private final int projectId = 1;
    private Databucket databucket;

    public GenerateDataScheduler() {
        databucket = new Databucket(url, username, password, projectId, logs);
    }

    public GenerateDataScheduler(Databucket databucket) {
        this.databucket = databucket;
    }

    public static void main(String[] args) {
        GenerateDataScheduler dataScheduler = new GenerateDataScheduler();
        dataScheduler.generateData(Buckets.DEV_SCHEDULER);
        dataScheduler.generateData(Buckets.INT_SCHEDULER);
        dataScheduler.generateData(Buckets.PRD_SCHEDULER);
    }

    public void generateDevIntPrdScheduler() {
        generateData(Buckets.DEV_SCHEDULER);
        generateData(Buckets.INT_SCHEDULER);
        generateData(Buckets.PRD_SCHEDULER);
    }

    private void generateData(String bucketName) {
        Bucket schedulerBucket = new Bucket(databucket, bucketName);
        Random ran = new Random();
        int count = ran.nextInt(70) + 50;
        for (int i = 0; i < count; i++) {
            String tag = getRandomTag();
            Data schedulerData = new Data(schedulerBucket);
            schedulerData.setTag(tag);
            schedulerData.setReserved(Math.random() < 0.1);
            schedulerData.setProperty(Scheduler.DATA_ID, getRandomDataId());
            schedulerData.setProperty(Scheduler.SCHEDULE_TIME, "2021-06-21T18:06:24.947Z");
            schedulerData.setProperty(Scheduler.SCHEDULE_SCENARIO, getRandomScenarioName());

            if (tag.equals(Tags.DONE)) {
                schedulerData.setProperty(Scheduler.STATUS, getRandomStatus());
            }

            schedulerBucket.insertData(schedulerData);
        }
    }

    private String getRandomTag() {
        double rand = Math.random();
        System.out.println(rand);

        if (rand < 0.6)
            return Tags.TODO;
        else
            return Tags.DONE;
    }

    private int getRandomDataId() {
        Random ran = new Random();
        return ran.nextInt(1) + 200;
    }

    private String getRandomScenarioName() {
        Random ran = new Random();
        int id = ran.nextInt(900) + 100;
        return "scenario-tag-name-" + id;
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
