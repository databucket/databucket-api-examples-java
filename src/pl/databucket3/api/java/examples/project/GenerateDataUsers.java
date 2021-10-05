package pl.databucket3.api.java.examples.project;

import com.opencsv.CSVReader;
import pl.databucket3.api.java.client.*;
import pl.databucket3.api.java.examples.structure.Buckets;
import pl.databucket3.api.java.examples.structure.Tags;
import pl.databucket3.api.java.examples.structure.User;

import java.io.FileReader;
import java.util.*;

public class GenerateDataUsers {
    private final String url = "http://localhost:8080";
    private final boolean logs = true;
    private final String username = "super";
    private final String password = "super";
    private final int projectId = 1;
    private Databucket databucket;

    public GenerateDataUsers() {
        databucket = new Databucket(url, username, password, projectId, logs);
    }

    public GenerateDataUsers(Databucket databucket) {
        this.databucket = databucket;
    }

    public static void main(String[] args) {
        GenerateDataUsers dataUsers = new GenerateDataUsers();
//        dataUsers.importData("./resources/import/users.csv", Buckets.DEV_USERS);
//        dataUsers.importData("./resources/import/users.csv", Buckets.INT_USERS);
//        dataUsers.importData("./resources/import/users.csv", Buckets.PRD_USERS);
        dataUsers.makeRandomChanges(Buckets.DEV_USERS);
    }

    public void generateDevIntPrdUsers() {
        importData("./resources/import/users.csv", Buckets.DEV_USERS);
        importData("./resources/import/users.csv", Buckets.INT_USERS);
        importData("./resources/import/users.csv", Buckets.PRD_USERS);
    }

    public void generateDevIntPrdUsersChanges() {
        makeRandomChanges(Buckets.DEV_USERS);
    }

    private void importData(String sourceFileFullPath, String bucketName) {
        Bucket usersBucket = new Bucket(databucket, bucketName);
        try {
            FileReader filereader = new FileReader(sourceFileFullPath);
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;

            // skip first row with column names
            csvReader.readNext();

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                Data userData = new Data(usersBucket);
                userData.setTag(getRandomTag());
                userData.setReserved(Math.random() < 0.3);
                userData.setProperty(User.FIRST_NAME, nextRecord[0]);
                userData.setProperty(User.LAST_NAME, nextRecord[1]);
                userData.setProperty(User.COMPANY, nextRecord[2]);
                userData.setProperty(User.ADDRESS, nextRecord[3]);
                userData.setProperty(User.CITY, nextRecord[4]);
                userData.setProperty(User.STATE, nextRecord[5]);
                userData.setProperty(User.POST, nextRecord[6]);
                userData.setProperty(User.PHONE, nextRecord[7]);
                userData.setProperty(User.EMAIL, nextRecord[8]);
                userData.setProperty(User.WEB, nextRecord[9]);
                userData.setProperty(User.HEIGHT, getRandomHeight());
                userData.setProperty(User.COLOR, getRandomColor());
                List<Map<String, String>> scenarios = getRandomScenarios();
                if (scenarios != null)
                    userData.setProperty(User.SCENARIOS, scenarios);

                usersBucket.insertData(userData);
            }

            csvReader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeRandomChanges(String bucketName) {
        Bucket usersBucket = new Bucket(databucket, bucketName);
        Random ran = new Random();
        int count = ran.nextInt(10) + 30;
        Condition[] conditions = {
                new Condition(Source.FIELD, Field.TAG_ID, Operator.equal, Source.CONST, databucket.getTagId(Tags.ACTIVE)),
                new Condition(Source.PROPERTY, User.LAST_NAME, Operator.like, Source.CONST, "K%")
        };
        for (int i = 0; i < count; i++) {
            Data randomUser = usersBucket.reserveData(conditions, true);
            randomUser.setProperty(User.UUID, UUID.randomUUID());
            randomUser.setReserved(false);
            usersBucket.updateData(randomUser);
        }
    }

    private String getRandomTag() {
        double rand = Math.random();

        if (rand <= 0.1)
            return Tags.SCHEDULED;
        else if (rand <= 0.2)
            return Tags.ANALYSIS;
        else if (rand <= 0.4)
            return Tags.DELETED;
        else if (rand <= 0.6)
            return Tags.TRASH;
        else
            return Tags.ACTIVE;
    }

    private String getRandomHeight() {
        double rand = Math.random();
        if (rand <= 0.4)
            return "h<1m";
        else if (rand <= 0.8)
            return "1m<h<2m";
        else
            return "h>2m";
    }

    private String getRandomColor() {
        double rand = Math.random();

        if (rand <= 0.3)
            return "blue";
        else if (rand <= 0.5)
            return "black";
        else if (rand <= 0.8)
            return "green";
        else
            return "grey";
    }

    private List<Map<String, String>> getRandomScenarios() {
        double rand = Math.random();
        if (rand > 0.4) {
            List<Map<String, String>> listOfScenarios = new ArrayList<>();
            Random ran = new Random();
            int count = ran.nextInt(6);
            for (int i = 0; i < count; i++) {
                Map<String, String> scenario = new HashMap<>();
                scenario.put("report", getRandomReportLink());
                scenario.put("startTime", getRandomStartTime());
                scenario.put("scenarioName", getRandomScenarioName());
                listOfScenarios.add(scenario);
            }
            return listOfScenarios;
        }
        return null;
    }

    private String getRandomReportLink() {
        Random ran = new Random();
        int id = ran.nextInt(900) + 100;
        return "https://server/test/report/" + id + "/index.html";
    }

    private String getRandomStartTime() {
        return "2021-06-08 16:31:42";
    }

    private String getRandomScenarioName() {
        Random ran = new Random();
        int id = ran.nextInt(900) + 100;
        return "Example scenario name " + id;
    }

}
