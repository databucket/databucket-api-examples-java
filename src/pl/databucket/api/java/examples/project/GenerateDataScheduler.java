package pl.databucket.api.java.examples.project;

import pl.databucket.api.java.client.Bucket;
import pl.databucket.api.java.client.Data;
import pl.databucket.api.java.client.Databucket;
import pl.databucket.api.java.examples.constants.BucketName;
import pl.databucket.api.java.examples.constants.SchedulerProp;
import pl.databucket.api.java.examples.constants.TagName;

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
        dataScheduler.generateData(BucketName.DEV_SCHEDULER);
        dataScheduler.generateData(BucketName.INT_SCHEDULER);
        dataScheduler.generateData(BucketName.PRD_SCHEDULER);
    }

    public void generateDevIntPrdScheduler() {
        generateData(BucketName.DEV_SCHEDULER);
        generateData(BucketName.INT_SCHEDULER);
        generateData(BucketName.PRD_SCHEDULER);
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
            schedulerData.setProperty(SchedulerProp.DATA_ID, getRandomDataId());
            schedulerData.setProperty(SchedulerProp.SCHEDULE_TIME, "2021-06-21T18:06:24.947Z");
            schedulerData.setProperty(SchedulerProp.SCHEDULE_SCENARIO, getRandomScenarioName());

            if (tag.equals(TagName.DONE)) {
                schedulerData.setProperty(SchedulerProp.STATUS, getRandomStatus());
            }

            schedulerBucket.insertData(schedulerData);
        }
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
