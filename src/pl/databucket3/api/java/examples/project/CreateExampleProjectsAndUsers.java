package pl.databucket3.api.java.examples.project;

import pl.databucket3.api.java.client.Databucket;

import java.util.*;

public class CreateExampleProjectsAndUsers {

    private final String url = "http://localhost:8080";
    private final boolean logs = true;

    Databucket databucket = new Databucket(url, logs);

    public static void main(String[] args) {
        new CreateExampleProjectsAndUsers().run();
    }

    private void run() {
        prepareProjectManagement();
    }

    private void prepareProjectManagement() {
        databucket.authenticate("super", "super", null);
        databucket.initiateExtension();

        databucket.extension.createUser(true, "userSAMR", Arrays.asList(1, 2, 3, 4), null);
        databucket.extension.createUser(true, "userAMR", Arrays.asList(2, 3, 4), null);
        databucket.extension.createUser(true, "userMR", Arrays.asList(3, 4), null);
        databucket.extension.createUser(true, "userR", Collections.singletonList(4), null);
        databucket.extension.createUser(false, "userDisabled", Collections.singletonList(3), null);
        databucket.extension.createUser(true, "userExpired", Collections.singletonList(3), "2021-06-08T15:34:00.000Z");
        databucket.extension.createProject(true, "Project A", "Example DEMO project A", Arrays.asList("super", "userSAMR", "userAMR", "userMR", "userR", "userDisabled", "userExpired"), null);
        databucket.extension.createProject(true, "Project B", "Example DEMO project B", Arrays.asList("super", "userSAMR", "userAMR", "userMR", "userR", "userDisabled", "userExpired"), null);
        databucket.extension.createProject(true, "Project C", "Example DEMO project C", Arrays.asList("super", "userSAMR", "userAMR", "userMR", "userR", "userDisabled", "userExpired"), null);
        databucket.extension.createProject(false, "Project disabled", null, Arrays.asList("super", "userSAMR", "userAMR", "userMR", "userR"), null);
        databucket.extension.createProject(true, "Project expired", "With passed expiration date", Arrays.asList("super", "userSAMR", "userAMR", "userMR", "userR"), "2021-06-08T15:34:00.000Z");
    }
}
