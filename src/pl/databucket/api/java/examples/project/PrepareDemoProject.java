package pl.databucket.api.java.examples.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pl.databucket.api.java.client.Databucket;

import java.util.*;

public class PrepareDemoProject {
    private final String url = "http://localhost:8080";
    private final boolean logs = true;
    private final String username = "super";
    private final String password = "super";
    private final int MEMBER_ROLE = 3;

    private final int projectId = 18;

    Databucket databucket = new Databucket(url, username, password, projectId, logs);

    public static void main(String[] args) {
        new PrepareDemoProject().run();
    }

    private void run() {
        databucket.initiateExtension();

        prepareEnums();
        List<Map<String, Object>> enums = databucket.extension.getEnums();

        prepareClasses(enums);
        List<Map<String, Object>> classes = databucket.extension.getClasses();

        prepareGroups();
        List<Map<String, Object>> groups = databucket.extension.getGroups();

        prepareBuckets(classes, groups);
        List<Map<String, Object>> buckets = databucket.extension.getBuckets();

        prepareTags(classes);
        List<Map<String, Object>> tags = databucket.extension.getTags();
        databucket.loadTags();

        prepareUserColumns(classes);
        prepareSchedulerColumns(classes);
        prepareConfigColumns(classes);
        prepareJobBaseColumns(classes);
        prepareJobMoneyColumns(classes);
        List<Map<String, Object>> columns = databucket.extension.getColumns();

        prepareFilterTagActiveUser(classes, tags);
        prepareFilterTagActiveScheduledUser(classes, tags);
        prepareFilterTagNotTrashAndDeletedUser(classes, tags);
        prepareFilterTagTrashUser(classes, tags);
        prepareFilterTagScheduledUser(classes, tags);
        prepareFilterTagDeletedUser(classes, tags);
        prepareFilterTagAnalysisUser(classes, tags);
        prepareFilterTodo(classes, tags);
        prepareFilterDone(classes, tags);
        prepareFilterJobsBase(classes, tags);
        prepareFilterJobsMoney(classes, tags);
        List<Map<String, Object>> filters = databucket.extension.getFilters();

        prepareViewAllUser(classes, columns);
        prepareViewActiveUser(classes, columns, filters);
        prepareViewTrashUser(classes, columns, filters);
        prepareViewDeletedUser(classes, columns, filters);
        prepareViewScheduledUser(classes, columns, filters);
        prepareViewAnalysisUser(classes, columns, filters);
        prepareViewAll(classes, columns, filters);
        prepareViewTodo(classes, columns, filters);
        prepareViewDone(classes, columns, filters);
        prepareViewConfiguration(classes, columns, filters);
        prepareViewJobBase(classes, columns, filters);
        prepareViewJobMoney(classes, columns, filters);

        prepareTaskTrashActiveOrScheduled(classes, filters, tags);
        prepareTaskTrashNotTrashAndDeleted(classes, filters, tags);
        prepareTaskRemoveDeleted(classes, filters, tags);

        GenerateDataUsers genUsers = new GenerateDataUsers(databucket);
        genUsers.generateDevIntPrdUsers();

        GenerateDataScheduler genScheduler = new GenerateDataScheduler(databucket);
        genScheduler.generateDevIntPrdScheduler();

        GenerateDataJobs genJobs = new GenerateDataJobs(databucket);
        genJobs.generateDevIntJobs();

        GenerateDataConfig genConfig = new GenerateDataConfig(databucket);
        genConfig.generateConfig();

        genUsers.generateDevIntPrdUsersChanges();
    }

    private void prepareTaskTrashActiveOrScheduled(List<Map<String, Object>> classes, List<Map<String, Object>> filters, List<Map<String, Object>> tags) {
        Map<String, Object> userClass = getObjectByName(classes, "user");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) userClass.get("configuration");
        Map<String, Object> configuration = new HashMap<>();
        String actionsString = "{\"type\":\"modify\",\"setTag\":true,\"tagId\":" + getIdByName(tags, "trash") + ",\"setReserved\":false,\"reserved\":false,\"properties\":[]}";
        Map<String, Object> actions = new Gson().fromJson(actionsString, new TypeToken<HashMap<String, Object>>(){}.getType());
        configuration.put("actions", actions);
        configuration.put("properties", classProperties);
        databucket.extension.createTask(
                "trash active or scheduled",
                null,
                getIdByName(classes, "user"),
                getIdByName(filters, "tag is active or scheduled"),
                getIdsByNames(classes, Collections.singletonList("user")),
                configuration
        );
    }

    private void prepareTaskTrashNotTrashAndDeleted(List<Map<String, Object>> classes, List<Map<String, Object>> filters, List<Map<String, Object>> tags) {
        Map<String, Object> userClass = getObjectByName(classes, "user");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) userClass.get("configuration");
        Map<String, Object> configuration = new HashMap<>();
        String actionsString = "{\"type\":\"modify\",\"setTag\":true,\"tagId\":" + getIdByName(tags, "trash") + ",\"setReserved\":false,\"reserved\":false,\"properties\":[]}";
        Map<String, Object> actions = new Gson().fromJson(actionsString, new TypeToken<HashMap<String, Object>>(){}.getType());
        configuration.put("actions", actions);
        configuration.put("properties", classProperties);
        databucket.extension.createTask(
                "trash all",
                null,
                getIdByName(classes, "user"),
                getIdByName(filters, "tag is not trash and deleted"),
                getIdsByNames(classes, Collections.singletonList("user")),
                configuration
        );
    }

    private void prepareTaskRemoveDeleted(List<Map<String, Object>> classes, List<Map<String, Object>> filters, List<Map<String, Object>> tags) {
        Map<String, Object> userClass = getObjectByName(classes, "user");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) userClass.get("configuration");
        Map<String, Object> configuration = new HashMap<>();
        String actionsString = "{\"type\":\"remove\",\"setTag\":false,\"tagId\":0,\"setReserved\":false,\"reserved\":false,\"properties\":[]}";
        Map<String, Object> actions = new Gson().fromJson(actionsString, new TypeToken<HashMap<String, Object>>(){}.getType());
        configuration.put("actions", actions);
        configuration.put("properties", classProperties);
        databucket.extension.createTask(
                "remove deleted",
                "Permanently remove users with tag 'deleted'",
                getIdByName(classes, "user"),
                getIdByName(filters, "tag is deleted"),
                getIdsByNames(classes, Collections.singletonList("user")),
                configuration
        );
    }

    private void prepareViewAllUser(List<Map<String, Object>> classes, List<Map<String, Object>> columns) {
        databucket.extension.createView(
                "- all -",
                "All users",
                getIdByName(columns, "user columns"),
                null,
                getIdsByNames(classes, Collections.singletonList("user")),
                MEMBER_ROLE,
                null,
                Arrays.asList(1,2,3,4,5,6,7,8,10)
        );
    }

    private void prepareViewActiveUser(List<Map<String, Object>> classes, List<Map<String, Object>> columns, List<Map<String, Object>> filters) {
        databucket.extension.createView(
                "active",
                "Active users",
                getIdByName(columns, "user columns"),
                getIdByName(filters, "tag is active"),
                getIdsByNames(classes, Collections.singletonList("user")),
                MEMBER_ROLE,
                null,
                Arrays.asList(1,2,3,4,5,6,7,8,10)
        );
    }

    private void prepareViewTrashUser(List<Map<String, Object>> classes, List<Map<String, Object>> columns, List<Map<String, Object>> filters) {
        databucket.extension.createView(
                "trash",
                "Trashed users",
                getIdByName(columns, "user columns"),
                getIdByName(filters, "tag is trash"),
                getIdsByNames(classes, Collections.singletonList("user")),
                MEMBER_ROLE,
                null,
                Arrays.asList(1,2,3,4,5,6,7,8,10)
        );
    }

    private void prepareViewDeletedUser(List<Map<String, Object>> classes, List<Map<String, Object>> columns, List<Map<String, Object>> filters) {
        databucket.extension.createView(
                "deleted",
                "Deleted users",
                getIdByName(columns, "user columns"),
                getIdByName(filters, "tag is deleted"),
                getIdsByNames(classes, Collections.singletonList("user")),
                MEMBER_ROLE,
                null,
                Arrays.asList(1,2,3,4,5,6,7,8,10)
        );
    }

    private void prepareViewScheduledUser(List<Map<String, Object>> classes, List<Map<String, Object>> columns, List<Map<String, Object>> filters) {
        databucket.extension.createView(
                "scheduled",
                "Scheduled users",
                getIdByName(columns, "user columns"),
                getIdByName(filters, "tag is scheduled"),
                getIdsByNames(classes, Collections.singletonList("user")),
                MEMBER_ROLE,
                null,
                Arrays.asList(1,2,3,4,5,6,7,8,10)
        );
    }

    private void prepareViewAnalysisUser(List<Map<String, Object>> classes, List<Map<String, Object>> columns, List<Map<String, Object>> filters) {
        databucket.extension.createView(
                "analyzed",
                "Analyzed users",
                getIdByName(columns, "user columns"),
                getIdByName(filters, "tag is analysis"),
                getIdsByNames(classes, Collections.singletonList("user")),
                MEMBER_ROLE,
                null,
                Arrays.asList(1,2,3,4,5,6,7,8,10)
        );
    }

    private void prepareViewAll(List<Map<String, Object>> classes, List<Map<String, Object>> columns, List<Map<String, Object>> filters) {
        databucket.extension.createView(
                "- all -",
                "Scenarios - all",
                getIdByName(columns, "scheduler columns"),
                null,
                getIdsByNames(classes, Collections.singletonList("scheduler")),
                MEMBER_ROLE,
                null,
                Arrays.asList(1,2,3,4,5)
        );
    }

    private void prepareViewTodo(List<Map<String, Object>> classes, List<Map<String, Object>> columns, List<Map<String, Object>> filters) {
        databucket.extension.createView(
                "todo",
                "Scenarios - todo",
                getIdByName(columns, "scheduler columns"),
                getIdByName(filters, "tag is todo"),
                getIdsByNames(classes, Collections.singletonList("scheduler")),
                MEMBER_ROLE,
                null,
                Arrays.asList(1,2,3,4,5)
        );
    }

    private void prepareViewDone(List<Map<String, Object>> classes, List<Map<String, Object>> columns, List<Map<String, Object>> filters) {
        databucket.extension.createView(
                "done",
                "Scenarios - done",
                getIdByName(columns, "scheduler columns"),
                getIdByName(filters, "tag is done"),
                getIdsByNames(classes, Collections.singletonList("scheduler")),
                MEMBER_ROLE,
                null,
                Arrays.asList(1,2,3,4,5)
        );
    }

    private void prepareViewConfiguration(List<Map<String, Object>> classes, List<Map<String, Object>> columns, List<Map<String, Object>> filters) {
        databucket.extension.createView(
                "Configuration",
                null,
                getIdByName(columns, "config columns"),
                null,
                getIdsByNames(classes, Collections.singletonList("config")),
                MEMBER_ROLE,
                null,
                Arrays.asList(1,3,4)
        );
    }

    private void prepareViewJobBase(List<Map<String, Object>> classes, List<Map<String, Object>> columns, List<Map<String, Object>> filters) {
        databucket.extension.createView(
                "Base",
                null,
                getIdByName(columns, "jobs - base"),
                getIdByName(filters, "jobs - base"),
                getIdsByNames(classes, Collections.singletonList("job")),
                MEMBER_ROLE,
                null,
                Arrays.asList(1,2,3,4,5)
        );
    }

    private void prepareViewJobMoney(List<Map<String, Object>> classes, List<Map<String, Object>> columns, List<Map<String, Object>> filters) {
        databucket.extension.createView(
                "Money",
                null,
                getIdByName(columns, "jobs - money"),
                getIdByName(filters, "jobs - money"),
                getIdsByNames(classes, Collections.singletonList("job")),
                MEMBER_ROLE,
                null,
                Arrays.asList(1,2,3,4,5)
        );
    }

    private void prepareFilterTodo(List<Map<String, Object>> classes, List<Map<String, Object>> tags) {
        Map<String, Object> schedulerClass = getObjectByName(classes, "scheduler");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) schedulerClass.get("configuration");
        String logicString = "{\"and\":[{\"==\":[{\"var\":\"tagId\"}," + getIdByName(tags, "todo") + "]}]}";
        Map<String, Object> logic = new Gson().fromJson(logicString, new TypeToken<HashMap<String, Object>>(){}.getType());
        String treeString = "{\"id\":\"99b8a98a-cdef-4012-b456-717a0986215c\",\"type\":\"group\",\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\"],\"children1\":{\"babaaa9a-89ab-4cde-b012-317a0986293e\":{\"type\":\"rule\",\"id\":\"babaaa9a-89ab-4cde-b012-317a0986293e\",\"properties\":{\"field\":\"tagId\",\"operator\":\"select_equals\",\"value\":[" + getIdByName(tags, "todo") + "],\"valueSrc\":[\"value\"],\"operatorOptions\":null,\"valueType\":[\"select\"]},\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\",\"babaaa9a-89ab-4cde-b012-317a0986293e\"]}}}";
        Map<String, Object> tree = new Gson().fromJson(treeString, new TypeToken<HashMap<String, Object>>(){}.getType());

        Map<String, Object> activeUserFilterDef = new HashMap<>();
        activeUserFilterDef.put("properties", classProperties);
        activeUserFilterDef.put("logic", logic);
        activeUserFilterDef.put("tree", tree);
        databucket.extension.createFilter("tag is todo", null, getIdByName(classes, "scheduler"), activeUserFilterDef);
    }

    private void prepareFilterDone(List<Map<String, Object>> classes, List<Map<String, Object>> tags) {
        Map<String, Object> schedulerClass = getObjectByName(classes, "scheduler");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) schedulerClass.get("configuration");
        String logicString = "{\"and\":[{\"==\":[{\"var\":\"tagId\"}," + getIdByName(tags, "done") + "]}]}";
        Map<String, Object> logic = new Gson().fromJson(logicString, new TypeToken<HashMap<String, Object>>(){}.getType());
        String treeString = "{\"id\":\"99b8a98a-cdef-4012-b456-717a0986215c\",\"type\":\"group\",\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\"],\"children1\":{\"babaaa9a-89ab-4cde-b012-317a0986293e\":{\"type\":\"rule\",\"id\":\"babaaa9a-89ab-4cde-b012-317a0986293e\",\"properties\":{\"field\":\"tagId\",\"operator\":\"select_equals\",\"value\":[" + getIdByName(tags, "done") +"],\"valueSrc\":[\"value\"],\"operatorOptions\":null,\"valueType\":[\"select\"]},\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\",\"babaaa9a-89ab-4cde-b012-317a0986293e\"]}}}";
        Map<String, Object> tree = new Gson().fromJson(treeString, new TypeToken<HashMap<String, Object>>(){}.getType());

        Map<String, Object> activeUserFilterDef = new HashMap<>();
        activeUserFilterDef.put("properties", classProperties);
        activeUserFilterDef.put("logic", logic);
        activeUserFilterDef.put("tree", tree);
        databucket.extension.createFilter("tag is done", null, getIdByName(classes, "scheduler"), activeUserFilterDef);
    }

    private void prepareFilterJobsBase(List<Map<String, Object>> classes, List<Map<String, Object>> tags) {
        Map<String, Object> jobClass = getObjectByName(classes, "job");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) jobClass.get("configuration");
        String logicString = "{\"and\": [{\"in\": [\"base\",{\"var\": \"prop.$*jobName\"}]}]}";
        Map<String, Object> logic = new Gson().fromJson(logicString, new TypeToken<HashMap<String, Object>>(){}.getType());
        String treeString = "{\"id\": \"ab9baa9a-0123-4456-b89a-b17a3e5ab560\",\"path\": [\"ab9baa9a-0123-4456-b89a-b17a3e5ab560\"],\"type\": \"group\",\"children1\": {\"bbb8baab-cdef-4012-b456-717a3e5abf4d\": {\"id\": \"bbb8baab-cdef-4012-b456-717a3e5abf4d\",\t\"path\": [\"ab9baa9a-0123-4456-b89a-b17a3e5ab560\",\"bbb8baab-cdef-4012-b456-717a3e5abf4d\"],\"type\": \"rule\",\"properties\": {\"field\": \"prop.$*jobName\",\"value\": [\"base\"],\"operator\": \"like\",\t\"valueSrc\": [\"value\"],\"valueType\": [\"text\"],\"operatorOptions\": null\t}}}}";
        Map<String, Object> tree = new Gson().fromJson(treeString, new TypeToken<HashMap<String, Object>>(){}.getType());

        Map<String, Object> activeUserFilterDef = new HashMap<>();
        activeUserFilterDef.put("properties", classProperties);
        activeUserFilterDef.put("logic", logic);
        activeUserFilterDef.put("tree", tree);
        databucket.extension.createFilter("jobs - base", null, getIdByName(classes, "job"), activeUserFilterDef);
    }

    private void prepareFilterJobsMoney(List<Map<String, Object>> classes, List<Map<String, Object>> tags) {
        Map<String, Object> jobClass = getObjectByName(classes, "job");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) jobClass.get("configuration");
        String logicString = "{\"and\": [{\"in\": [\"money\",{\"var\": \"prop.$*jobName\"}]}]}";
        Map<String, Object> logic = new Gson().fromJson(logicString, new TypeToken<HashMap<String, Object>>(){}.getType());
        String treeString = "{\"id\": \"ab9baa9a-0123-4456-b89a-b17a3e5ab560\",\"path\": [\"ab9baa9a-0123-4456-b89a-b17a3e5ab560\"],\"type\": \"group\",\"children1\": {\"bbb8baab-cdef-4012-b456-717a3e5abf4d\": {\"id\": \"bbb8baab-cdef-4012-b456-717a3e5abf4d\",\t\"path\": [\"ab9baa9a-0123-4456-b89a-b17a3e5ab560\",\"bbb8baab-cdef-4012-b456-717a3e5abf4d\"],\"type\": \"rule\",\"properties\": {\"field\": \"prop.$*jobName\",\"value\": [\"money\"],\"operator\": \"like\",\t\"valueSrc\": [\"value\"],\"valueType\": [\"text\"],\"operatorOptions\": null\t}}}}";
        Map<String, Object> tree = new Gson().fromJson(treeString, new TypeToken<HashMap<String, Object>>(){}.getType());

        Map<String, Object> activeUserFilterDef = new HashMap<>();
        activeUserFilterDef.put("properties", classProperties);
        activeUserFilterDef.put("logic", logic);
        activeUserFilterDef.put("tree", tree);
        databucket.extension.createFilter("jobs - money", null, getIdByName(classes, "job"), activeUserFilterDef);
    }

    private void prepareFilterTagActiveUser(List<Map<String, Object>> classes, List<Map<String, Object>> tags) {
        Map<String, Object> userClass = getObjectByName(classes, "user");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) userClass.get("configuration");
        String logicString = "{\"and\":[{\"==\":[{\"var\":\"tagId\"}," + getIdByName(tags, "active") + "]}]}";
        Map<String, Object> logic = new Gson().fromJson(logicString, new TypeToken<HashMap<String, Object>>(){}.getType());
        String treeString = "{\"id\":\"99b8a98a-cdef-4012-b456-717a0986215c\",\"type\":\"group\",\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\"],\"children1\":{\"babaaa9a-89ab-4cde-b012-317a0986293e\":{\"type\":\"rule\",\"id\":\"babaaa9a-89ab-4cde-b012-317a0986293e\",\"properties\":{\"field\":\"tagId\",\"operator\":\"select_equals\",\"value\":[" + getIdByName(tags, "active") + "],\"valueSrc\":[\"value\"],\"operatorOptions\":null,\"valueType\":[\"select\"]},\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\",\"babaaa9a-89ab-4cde-b012-317a0986293e\"]}}}";
        Map<String, Object> tree = new Gson().fromJson(treeString, new TypeToken<HashMap<String, Object>>(){}.getType());

        Map<String, Object> activeUserFilterDef = new HashMap<>();
        activeUserFilterDef.put("properties", classProperties);
        activeUserFilterDef.put("logic", logic);
        activeUserFilterDef.put("tree", tree);
        databucket.extension.createFilter("tag is active", null, getIdByName(classes, "user"), activeUserFilterDef);
    }

    private void prepareFilterTagActiveScheduledUser(List<Map<String, Object>> classes, List<Map<String, Object>> tags) {
        Map<String, Object> userClass = getObjectByName(classes, "user");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) userClass.get("configuration");
        String tagsIds = "" + getIdsByNames(tags, Arrays.asList("active", "scheduled"));
        String logicString = "{\"and\":[{\"in\":[{\"var\":\"tagId\"}," + tagsIds + "]}]}";
        Map<String, Object> logic = new Gson().fromJson(logicString, new TypeToken<HashMap<String, Object>>(){}.getType());
        String treeString = "{\"id\":\"b89898bb-cdef-4012-b456-717a0aeb7a0a\",\"type\":\"group\",\"path\":[\"b89898bb-cdef-4012-b456-717a0aeb7a0a\"],\"children1\":{\"baa89998-89ab-4cde-b012-317a0aeb7f9e\":{\"type\":\"rule\",\"id\":\"baa89998-89ab-4cde-b012-317a0aeb7f9e\",\"properties\":{\"field\":\"tagId\",\"operator\":\"select_any_in\",\"value\":[" + tagsIds + "],\"valueSrc\":[\"value\"],\"operatorOptions\":null,\"valueType\":[\"multiselect\"]},\"path\":[\"b89898bb-cdef-4012-b456-717a0aeb7a0a\",\"baa89998-89ab-4cde-b012-317a0aeb7f9e\"]}}}\n";
        Map<String, Object> tree = new Gson().fromJson(treeString, new TypeToken<HashMap<String, Object>>(){}.getType());

        Map<String, Object> activeUserFilterDef = new HashMap<>();
        activeUserFilterDef.put("properties", classProperties);
        activeUserFilterDef.put("logic", logic);
        activeUserFilterDef.put("tree", tree);
        databucket.extension.createFilter("tag is active or scheduled", null, getIdByName(classes, "user"), activeUserFilterDef);
    }

    private void prepareFilterTagNotTrashAndDeletedUser(List<Map<String, Object>> classes, List<Map<String, Object>> tags) {
        Map<String, Object> userClass = getObjectByName(classes, "user");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) userClass.get("configuration");
        String tagsIds = "" + getIdsByNames(tags, Arrays.asList("trash", "deleted"));
        String logicString = "{\"and\":[{\"!\":{\"in\":[{\"var\":\"tagId\"}," + tagsIds + "]}}]}";
        Map<String, Object> logic = new Gson().fromJson(logicString, new TypeToken<HashMap<String, Object>>(){}.getType());
        String treeString = "{\"id\":\"a9bab9bb-89ab-4cde-b012-317a0b33ab69\",\"type\":\"group\",\"path\":[\"a9bab9bb-89ab-4cde-b012-317a0b33ab69\"],\"children1\":{\"b8a8abaa-4567-489a-bcde-f17a0b33b17c\":{\"type\":\"rule\",\"id\":\"b8a8abaa-4567-489a-bcde-f17a0b33b17c\",\"properties\":{\"field\":\"tagId\",\"operator\":\"select_not_any_in\",\"value\":[" + tagsIds + "],\"valueSrc\":[\"value\"],\"operatorOptions\":null,\"valueType\":[\"multiselect\"]},\"path\":[\"a9bab9bb-89ab-4cde-b012-317a0b33ab69\",\"b8a8abaa-4567-489a-bcde-f17a0b33b17c\"]}}}";
        Map<String, Object> tree = new Gson().fromJson(treeString, new TypeToken<HashMap<String, Object>>(){}.getType());

        Map<String, Object> activeUserFilterDef = new HashMap<>();
        activeUserFilterDef.put("properties", classProperties);
        activeUserFilterDef.put("logic", logic);
        activeUserFilterDef.put("tree", tree);
        databucket.extension.createFilter("tag is not trash and deleted", null, getIdByName(classes, "user"), activeUserFilterDef);
    }

    private void prepareFilterTagTrashUser(List<Map<String, Object>> classes, List<Map<String, Object>> tags) {
        Map<String, Object> userClass = getObjectByName(classes, "user");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) userClass.get("configuration");
        String logicString = "{\"and\":[{\"==\":[{\"var\":\"tagId\"}," + getIdByName(tags, "trash") + "]}]}";
        Map<String, Object> logic = new Gson().fromJson(logicString, new TypeToken<HashMap<String, Object>>(){}.getType());
        String treeString = "{\"id\":\"99b8a98a-cdef-4012-b456-717a0986215c\",\"type\":\"group\",\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\"],\"children1\":{\"babaaa9a-89ab-4cde-b012-317a0986293e\":{\"type\":\"rule\",\"id\":\"babaaa9a-89ab-4cde-b012-317a0986293e\",\"properties\":{\"field\":\"tagId\",\"operator\":\"select_equals\",\"value\":[" + getIdByName(tags, "trash") + "],\"valueSrc\":[\"value\"],\"operatorOptions\":null,\"valueType\":[\"select\"]},\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\",\"babaaa9a-89ab-4cde-b012-317a0986293e\"]}}}";
        Map<String, Object> tree = new Gson().fromJson(treeString, new TypeToken<HashMap<String, Object>>(){}.getType());

        Map<String, Object> activeUserFilterDef = new HashMap<>();
        activeUserFilterDef.put("properties", classProperties);
        activeUserFilterDef.put("logic", logic);
        activeUserFilterDef.put("tree", tree);
        databucket.extension.createFilter("tag is trash", null, getIdByName(classes, "user"), activeUserFilterDef);
    }

    private void prepareFilterTagDeletedUser(List<Map<String, Object>> classes, List<Map<String, Object>> tags) {
        Map<String, Object> userClass = getObjectByName(classes, "user");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) userClass.get("configuration");
        String logicString = "{\"and\":[{\"==\":[{\"var\":\"tagId\"}," + getIdByName(tags, "deleted") + "]}]}";
        Map<String, Object> logic = new Gson().fromJson(logicString, new TypeToken<HashMap<String, Object>>(){}.getType());
        String treeString = "{\"id\":\"99b8a98a-cdef-4012-b456-717a0986215c\",\"type\":\"group\",\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\"],\"children1\":{\"babaaa9a-89ab-4cde-b012-317a0986293e\":{\"type\":\"rule\",\"id\":\"babaaa9a-89ab-4cde-b012-317a0986293e\",\"properties\":{\"field\":\"tagId\",\"operator\":\"select_equals\",\"value\":[" + getIdByName(tags, "deleted") + "],\"valueSrc\":[\"value\"],\"operatorOptions\":null,\"valueType\":[\"select\"]},\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\",\"babaaa9a-89ab-4cde-b012-317a0986293e\"]}}}";
        Map<String, Object> tree = new Gson().fromJson(treeString, new TypeToken<HashMap<String, Object>>(){}.getType());

        Map<String, Object> activeUserFilterDef = new HashMap<>();
        activeUserFilterDef.put("properties", classProperties);
        activeUserFilterDef.put("logic", logic);
        activeUserFilterDef.put("tree", tree);
        databucket.extension.createFilter("tag is deleted", null, getIdByName(classes, "user"), activeUserFilterDef);
    }

    private void prepareFilterTagScheduledUser(List<Map<String, Object>> classes, List<Map<String, Object>> tags) {
        Map<String, Object> userClass = getObjectByName(classes, "user");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) userClass.get("configuration");
        String logicString = "{\"and\":[{\"==\":[{\"var\":\"tagId\"}," + getIdByName(tags, "scheduled") + "]}]}";
        Map<String, Object> logic = new Gson().fromJson(logicString, new TypeToken<HashMap<String, Object>>(){}.getType());
        String treeString = "{\"id\":\"99b8a98a-cdef-4012-b456-717a0986215c\",\"type\":\"group\",\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\"],\"children1\":{\"babaaa9a-89ab-4cde-b012-317a0986293e\":{\"type\":\"rule\",\"id\":\"babaaa9a-89ab-4cde-b012-317a0986293e\",\"properties\":{\"field\":\"tagId\",\"operator\":\"select_equals\",\"value\":[" + getIdByName(tags, "scheduled") + "],\"valueSrc\":[\"value\"],\"operatorOptions\":null,\"valueType\":[\"select\"]},\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\",\"babaaa9a-89ab-4cde-b012-317a0986293e\"]}}}";
        Map<String, Object> tree = new Gson().fromJson(treeString, new TypeToken<HashMap<String, Object>>(){}.getType());

        Map<String, Object> activeUserFilterDef = new HashMap<>();
        activeUserFilterDef.put("properties", classProperties);
        activeUserFilterDef.put("logic", logic);
        activeUserFilterDef.put("tree", tree);
        databucket.extension.createFilter("tag is scheduled", null, getIdByName(classes, "user"), activeUserFilterDef);
    }

    private void prepareFilterTagAnalysisUser(List<Map<String, Object>> classes, List<Map<String, Object>> tags) {
        Map<String, Object> userClass = getObjectByName(classes, "user");
        List<Map<String, Object>> classProperties = (List<Map<String, Object>>) userClass.get("configuration");
        String logicString = "{\"and\":[{\"==\":[{\"var\":\"tagId\"}," + getIdByName(tags, "analysis") + "]}]}";
        Map<String, Object> logic = new Gson().fromJson(logicString, new TypeToken<HashMap<String, Object>>(){}.getType());
        String treeString = "{\"id\":\"99b8a98a-cdef-4012-b456-717a0986215c\",\"type\":\"group\",\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\"],\"children1\":{\"babaaa9a-89ab-4cde-b012-317a0986293e\":{\"type\":\"rule\",\"id\":\"babaaa9a-89ab-4cde-b012-317a0986293e\",\"properties\":{\"field\":\"tagId\",\"operator\":\"select_equals\",\"value\":[" + getIdByName(tags, "analysis") + "],\"valueSrc\":[\"value\"],\"operatorOptions\":null,\"valueType\":[\"select\"]},\"path\":[\"99b8a98a-cdef-4012-b456-717a0986215c\",\"babaaa9a-89ab-4cde-b012-317a0986293e\"]}}}";
        Map<String, Object> tree = new Gson().fromJson(treeString, new TypeToken<HashMap<String, Object>>(){}.getType());

        Map<String, Object> activeUserFilterDef = new HashMap<>();
        activeUserFilterDef.put("properties", classProperties);
        activeUserFilterDef.put("logic", logic);
        activeUserFilterDef.put("tree", tree);
        databucket.extension.createFilter("tag is analysis", null, getIdByName(classes, "user"), activeUserFilterDef);
    }

    @SuppressWarnings("unchecked")
    private void prepareUserColumns(List<Map<String, Object>> classes) {
        Map<String, Object> userClass = getObjectByName(classes, "user");
        List<Map<String, Object>> uProperties = (List<Map<String, Object>>) userClass.get("configuration");
        List<Map<String, Object>> uColumns = new ArrayList<>();

        Map<String, Object> idCol = new HashMap<>();
        idCol.put("enabled", true);
        idCol.put("uuid", "uuid_data_id");
        idCol.put("editable", "never");
        idCol.put("sorting", true);
        idCol.put("filtering", true);
        uColumns.add(idCol);

        Map<String, Object> tagCol = new HashMap<>();
        tagCol.put("enabled", true);
        tagCol.put("uuid", "uuid_tag_id");
        tagCol.put("editable", "always");
        tagCol.put("sorting", true);
        tagCol.put("filtering", true);
        uColumns.add(tagCol);

        Map<String, Object> reservedCol = new HashMap<>();
        reservedCol.put("enabled", true);
        reservedCol.put("uuid", "uuid_reserved");
        reservedCol.put("editable", "always");
        reservedCol.put("sorting", true);
        reservedCol.put("filtering", true);
        uColumns.add(reservedCol);

        Map<String, Object> ownerCol = new HashMap<>();
        ownerCol.put("enabled", true);
        ownerCol.put("uuid", "uuid_owner");
        ownerCol.put("editable", "never");
        ownerCol.put("sorting", true);
        ownerCol.put("filtering", true);
        uColumns.add(ownerCol);

        Map<String, Object> firstNameCol = new HashMap<>();
        firstNameCol.put("enabled", true);
        firstNameCol.put("uuid", getUUID(uProperties, "First name"));
        firstNameCol.put("editable", "always");
        firstNameCol.put("sorting", true);
        firstNameCol.put("filtering", true);
        uColumns.add(firstNameCol);

        Map<String, Object> lastNameCol = new HashMap<>();
        lastNameCol.put("enabled", true);
        lastNameCol.put("uuid", getUUID(uProperties, "Last name"));
        lastNameCol.put("editable", "always");
        lastNameCol.put("sorting", true);
        lastNameCol.put("filtering", true);
        uColumns.add(lastNameCol);

        Map<String, Object> eyeColorCol = new HashMap<>();
        eyeColorCol.put("enabled", true);
        eyeColorCol.put("uuid", getUUID(uProperties, "Eye color"));
        eyeColorCol.put("editable", "always");
        eyeColorCol.put("sorting", true);
        eyeColorCol.put("filtering", true);
        uColumns.add(eyeColorCol);

        Map<String, Object> heightCol = new HashMap<>();
        heightCol.put("enabled", true);
        heightCol.put("uuid", getUUID(uProperties, "Height"));
        heightCol.put("editable", "always");
        heightCol.put("sorting", true);
        heightCol.put("filtering", true);
        uColumns.add(heightCol);

        Map<String, Object> modifiedAtCol = new HashMap<>();
        modifiedAtCol.put("enabled", true);
        modifiedAtCol.put("uuid", "uuid_modified_at");
        modifiedAtCol.put("editable", "never");
        modifiedAtCol.put("sorting", true);
        modifiedAtCol.put("filtering", true);
        uColumns.add(modifiedAtCol);

        Map<String, Object> modifiedByCol = new HashMap<>();
        modifiedByCol.put("enabled", true);
        modifiedByCol.put("uuid", "uuid_modified_by");
        modifiedByCol.put("editable", "never");
        modifiedByCol.put("sorting", true);
        modifiedByCol.put("filtering", true);
        uColumns.add(modifiedByCol);

        Map<String, Object> userColumnsDef = new HashMap<>();
        userColumnsDef.put("properties", uProperties);
        userColumnsDef.put("columns", uColumns);
        databucket.extension.createColumns("user columns", null, getIdByName(classes, "user"), userColumnsDef);
    }

    @SuppressWarnings("unchecked")
    private void prepareSchedulerColumns(List<Map<String, Object>> classes) {
        Map<String, Object> userClass = getObjectByName(classes, "scheduler");
        List<Map<String, Object>> uProperties = (List<Map<String, Object>>) userClass.get("configuration");
        List<Map<String, Object>> uColumns = new ArrayList<>();

        Map<String, Object> idCol = new HashMap<>();
        idCol.put("enabled", true);
        idCol.put("uuid", "uuid_data_id");
        idCol.put("editable", "never");
        idCol.put("sorting", true);
        idCol.put("filtering", true);
        uColumns.add(idCol);

        Map<String, Object> tagCol = new HashMap<>();
        tagCol.put("enabled", true);
        tagCol.put("uuid", "uuid_tag_id");
        tagCol.put("editable", "always");
        tagCol.put("sorting", true);
        tagCol.put("filtering", true);
        uColumns.add(tagCol);

        Map<String, Object> firstNameCol = new HashMap<>();
        firstNameCol.put("enabled", true);
        firstNameCol.put("uuid", getUUID(uProperties, "Scenario"));
        firstNameCol.put("editable", "always");
        firstNameCol.put("sorting", true);
        firstNameCol.put("filtering", true);
        uColumns.add(firstNameCol);

        Map<String, Object> scheduleTimeCol = new HashMap<>();
        scheduleTimeCol.put("enabled", true);
        scheduleTimeCol.put("uuid", getUUID(uProperties, "Schedule time"));
        scheduleTimeCol.put("editable", "always");
        scheduleTimeCol.put("sorting", true);
        scheduleTimeCol.put("filtering", true);
        uColumns.add(scheduleTimeCol);

        Map<String, Object> statusCol = new HashMap<>();
        statusCol.put("enabled", true);
        statusCol.put("uuid", getUUID(uProperties, "Status"));
        statusCol.put("editable", "always");
        statusCol.put("sorting", true);
        statusCol.put("filtering", true);
        uColumns.add(statusCol);

        Map<String, Object> reservedCol = new HashMap<>();
        reservedCol.put("enabled", true);
        reservedCol.put("uuid", "uuid_reserved");
        reservedCol.put("editable", "always");
        reservedCol.put("sorting", true);
        reservedCol.put("filtering", true);
        uColumns.add(reservedCol);

        Map<String, Object> dataIdCol = new HashMap<>();
        dataIdCol.put("enabled", true);
        dataIdCol.put("uuid", getUUID(uProperties, "Data id"));
        dataIdCol.put("editable", "always");
        dataIdCol.put("sorting", true);
        dataIdCol.put("filtering", true);
        uColumns.add(dataIdCol);

        Map<String, Object> modifiedAtCol = new HashMap<>();
        modifiedAtCol.put("enabled", true);
        modifiedAtCol.put("uuid", "uuid_modified_at");
        modifiedAtCol.put("editable", "never");
        modifiedAtCol.put("sorting", true);
        modifiedAtCol.put("filtering", true);
        uColumns.add(modifiedAtCol);

        Map<String, Object> modifiedByCol = new HashMap<>();
        modifiedByCol.put("enabled", true);
        modifiedByCol.put("uuid", "uuid_modified_by");
        modifiedByCol.put("editable", "never");
        modifiedByCol.put("sorting", true);
        modifiedByCol.put("filtering", true);
        uColumns.add(modifiedByCol);

        Map<String, Object> userColumnsDef = new HashMap<>();
        userColumnsDef.put("properties", uProperties);
        userColumnsDef.put("columns", uColumns);
        databucket.extension.createColumns("scheduler columns", null, getIdByName(classes, "scheduler"), userColumnsDef);
    }

    @SuppressWarnings("unchecked")
    private void prepareConfigColumns(List<Map<String, Object>> classes) {
        Map<String, Object> userClass = getObjectByName(classes, "config");
        List<Map<String, Object>> uProperties = (List<Map<String, Object>>) userClass.get("configuration");
        List<Map<String, Object>> uColumns = new ArrayList<>();

        Map<String, Object> activeCol = new HashMap<>();
        activeCol.put("enabled", true);
        activeCol.put("uuid", getUUID(uProperties, "Active"));
        activeCol.put("editable", "always");
        activeCol.put("sorting", true);
        activeCol.put("filtering", true);
        uColumns.add(activeCol);

        Map<String, Object> environmentCol = new HashMap<>();
        environmentCol.put("enabled", true);
        environmentCol.put("uuid", getUUID(uProperties, "Environment"));
        environmentCol.put("editable", "always");
        environmentCol.put("sorting", true);
        environmentCol.put("filtering", true);
        uColumns.add(environmentCol);

        Map<String, Object> keyCol = new HashMap<>();
        keyCol.put("enabled", true);
        keyCol.put("uuid", getUUID(uProperties, "Key"));
        keyCol.put("editable", "always");
        keyCol.put("sorting", true);
        keyCol.put("filtering", true);
        uColumns.add(keyCol);

        Map<String, Object> valueCol = new HashMap<>();
        valueCol.put("enabled", true);
        valueCol.put("uuid", getUUID(uProperties, "Value"));
        valueCol.put("editable", "always");
        valueCol.put("sorting", true);
        valueCol.put("filtering", true);
        uColumns.add(valueCol);

        Map<String, Object> encryptedCol = new HashMap<>();
        encryptedCol.put("enabled", true);
        encryptedCol.put("uuid", getUUID(uProperties, "Encrypted"));
        encryptedCol.put("editable", "always");
        encryptedCol.put("sorting", true);
        encryptedCol.put("filtering", true);
        uColumns.add(encryptedCol);

        Map<String, Object> descriptionCol = new HashMap<>();
        descriptionCol.put("enabled", true);
        descriptionCol.put("uuid", getUUID(uProperties, "Description"));
        descriptionCol.put("editable", "always");
        descriptionCol.put("sorting", true);
        descriptionCol.put("filtering", true);
        uColumns.add(descriptionCol);

        Map<String, Object> modifiedAtCol = new HashMap<>();
        modifiedAtCol.put("enabled", true);
        modifiedAtCol.put("uuid", "uuid_modified_at");
        modifiedAtCol.put("editable", "never");
        modifiedAtCol.put("sorting", true);
        modifiedAtCol.put("filtering", true);
        uColumns.add(modifiedAtCol);

        Map<String, Object> modifiedByCol = new HashMap<>();
        modifiedByCol.put("enabled", true);
        modifiedByCol.put("uuid", "uuid_modified_by");
        modifiedByCol.put("editable", "never");
        modifiedByCol.put("sorting", true);
        modifiedByCol.put("filtering", true);
        uColumns.add(modifiedByCol);

        Map<String, Object> userColumnsDef = new HashMap<>();
        userColumnsDef.put("properties", uProperties);
        userColumnsDef.put("columns", uColumns);
        databucket.extension.createColumns("config columns", null, getIdByName(classes, "config"), userColumnsDef);
    }

    @SuppressWarnings("unchecked")
    private void prepareJobBaseColumns(List<Map<String, Object>> classes) {
        Map<String, Object> jobClass = getObjectByName(classes, "job");
        List<Map<String, Object>> uProperties = (List<Map<String, Object>>) jobClass.get("configuration");
        List<Map<String, Object>> uColumns = new ArrayList<>();

        Map<String, Object> idCol = new HashMap<>();
        idCol.put("enabled", true);
        idCol.put("uuid", "uuid_data_id");
        idCol.put("editable", "never");
        idCol.put("sorting", true);
        idCol.put("filtering", true);
        uColumns.add(idCol);

        Map<String, Object> tagCol = new HashMap<>();
        tagCol.put("enabled", true);
        tagCol.put("uuid", "uuid_tag_id");
        tagCol.put("editable", "always");
        tagCol.put("sorting", true);
        tagCol.put("filtering", true);
        uColumns.add(tagCol);

        Map<String, Object> userIdCol = new HashMap<>();
        userIdCol.put("enabled", true);
        userIdCol.put("uuid", getUUID(uProperties, "User ID"));
        userIdCol.put("editable", "always");
        userIdCol.put("sorting", true);
        userIdCol.put("filtering", true);
        uColumns.add(userIdCol);

        Map<String, Object> jobNameCol = new HashMap<>();
        jobNameCol.put("enabled", true);
        jobNameCol.put("uuid", getUUID(uProperties, "Job name"));
        jobNameCol.put("editable", "always");
        jobNameCol.put("sorting", true);
        jobNameCol.put("filtering", true);
        uColumns.add(jobNameCol);

        Map<String, Object> statusCol = new HashMap<>();
        statusCol.put("enabled", true);
        statusCol.put("uuid", getUUID(uProperties, "Status"));
        statusCol.put("editable", "onUpdate");
        statusCol.put("sorting", true);
        statusCol.put("filtering", true);
        uColumns.add(statusCol);

        Map<String, Object> modifiedAtCol = new HashMap<>();
        modifiedAtCol.put("enabled", true);
        modifiedAtCol.put("uuid", "uuid_modified_at");
        modifiedAtCol.put("editable", "never");
        modifiedAtCol.put("sorting", true);
        modifiedAtCol.put("filtering", true);
        uColumns.add(modifiedAtCol);

        Map<String, Object> modifiedByCol = new HashMap<>();
        modifiedByCol.put("enabled", true);
        modifiedByCol.put("uuid", "uuid_modified_by");
        modifiedByCol.put("editable", "never");
        modifiedByCol.put("sorting", true);
        modifiedByCol.put("filtering", true);
        uColumns.add(modifiedByCol);

        Map<String, Object> userColumnsDef = new HashMap<>();
        userColumnsDef.put("properties", uProperties);
        userColumnsDef.put("columns", uColumns);
        databucket.extension.createColumns("jobs - base", null, getIdByName(classes, "job"), userColumnsDef);
    }


    @SuppressWarnings("unchecked")
    private void prepareJobMoneyColumns(List<Map<String, Object>> classes) {
        Map<String, Object> jobClass = getObjectByName(classes, "job");
        List<Map<String, Object>> uProperties = (List<Map<String, Object>>) jobClass.get("configuration");
        List<Map<String, Object>> uColumns = new ArrayList<>();

        Map<String, Object> idCol = new HashMap<>();
        idCol.put("enabled", true);
        idCol.put("uuid", "uuid_data_id");
        idCol.put("editable", "never");
        idCol.put("sorting", true);
        idCol.put("filtering", true);
        uColumns.add(idCol);

        Map<String, Object> tagCol = new HashMap<>();
        tagCol.put("enabled", true);
        tagCol.put("uuid", "uuid_tag_id");
        tagCol.put("editable", "always");
        tagCol.put("sorting", true);
        tagCol.put("filtering", true);
        uColumns.add(tagCol);

        Map<String, Object> userIdCol = new HashMap<>();
        userIdCol.put("enabled", true);
        userIdCol.put("uuid", getUUID(uProperties, "User ID"));
        userIdCol.put("editable", "always");
        userIdCol.put("sorting", true);
        userIdCol.put("filtering", true);
        uColumns.add(userIdCol);

        Map<String, Object> jobNameCol = new HashMap<>();
        jobNameCol.put("enabled", true);
        jobNameCol.put("uuid", getUUID(uProperties, "Job name"));
        jobNameCol.put("editable", "always");
        jobNameCol.put("sorting", true);
        jobNameCol.put("filtering", true);
        uColumns.add(jobNameCol);

        Map<String, Object> valueCol = new HashMap<>();
        valueCol.put("enabled", true);
        valueCol.put("uuid", getUUID(uProperties, "Value"));
        valueCol.put("editable", "always");
        valueCol.put("sorting", true);
        valueCol.put("filtering", true);
        uColumns.add(valueCol);

        Map<String, Object> statusCol = new HashMap<>();
        statusCol.put("enabled", true);
        statusCol.put("uuid", getUUID(uProperties, "Status"));
        statusCol.put("editable", "onUpdate");
        statusCol.put("sorting", true);
        statusCol.put("filtering", true);
        uColumns.add(statusCol);

        Map<String, Object> modifiedAtCol = new HashMap<>();
        modifiedAtCol.put("enabled", true);
        modifiedAtCol.put("uuid", "uuid_modified_at");
        modifiedAtCol.put("editable", "never");
        modifiedAtCol.put("sorting", true);
        modifiedAtCol.put("filtering", true);
        uColumns.add(modifiedAtCol);

        Map<String, Object> modifiedByCol = new HashMap<>();
        modifiedByCol.put("enabled", true);
        modifiedByCol.put("uuid", "uuid_modified_by");
        modifiedByCol.put("editable", "never");
        modifiedByCol.put("sorting", true);
        modifiedByCol.put("filtering", true);
        uColumns.add(modifiedByCol);

        Map<String, Object> userColumnsDef = new HashMap<>();
        userColumnsDef.put("properties", uProperties);
        userColumnsDef.put("columns", uColumns);
        databucket.extension.createColumns("jobs - money", null, getIdByName(classes, "job"), userColumnsDef);
    }


    private void prepareTags(List<Map<String, Object>> classes) {
        List<Integer> userClassIds = getIdsByNames(classes, Collections.singletonList("user"));
        databucket.extension.createTag("active", null, null, userClassIds);
        databucket.extension.createTag("trash", null, null, userClassIds);
        databucket.extension.createTag("deleted", null, null, userClassIds);
        databucket.extension.createTag("scheduled", null, null, userClassIds);
        databucket.extension.createTag("analysis", null, null, userClassIds);

        List<Integer> scheduledJobClassIds = getIdsByNames(classes, Arrays.asList("scheduler", "job"));
        databucket.extension.createTag("todo", null, null, scheduledJobClassIds);
        databucket.extension.createTag("done", null, null, scheduledJobClassIds);
    }

    private void prepareBuckets(List<Map<String, Object>> classes, List<Map<String, Object>> groups) {
        databucket.extension.createBucket(
                "person",
                "dev-users",
                null,
                true,
                false,
                getIdByName(classes, "user"),
                getGroupsIds(groups, Collections.singletonList("DEV")),
                null,
                MEMBER_ROLE,
                null
        );

        databucket.extension.createBucket(
                "person",
                "int-users",
                null,
                true,
                false,
                getIdByName(classes, "user"),
                getGroupsIds(groups, Collections.singletonList("INT")),
                null,
                MEMBER_ROLE,
                null
        );

        databucket.extension.createBucket(
                "person",
                "prd-users",
                null,
                true,
                false,
                getIdByName(classes, "user"),
                getGroupsIds(groups, Collections.singletonList("PRD")),
                null,
                MEMBER_ROLE,
                null
        );

        databucket.extension.createBucket(
                "pending_actions",
                "dev-scheduler",
                null,
                true,
                false,
                getIdByName(classes, "scheduler"),
                getGroupsIds(groups, Collections.singletonList("DEV")),
                null,
                MEMBER_ROLE,
                null
        );

        databucket.extension.createBucket(
                "pending_actions",
                "int-scheduler",
                null,
                true,
                false,
                getIdByName(classes, "scheduler"),
                getGroupsIds(groups, Collections.singletonList("INT")),
                null,
                MEMBER_ROLE,
                null
        );

        databucket.extension.createBucket(
                "pending_actions",
                "prd-scheduler",
                null,
                true,
                false,
                getIdByName(classes, "scheduler"),
                getGroupsIds(groups, Collections.singletonList("PRD")),
                null,
                MEMBER_ROLE,
                null
        );

        databucket.extension.createBucket(
                "settings_applications",
                "config",
                null,
                true,
                false,
                getIdByName(classes, "config"),
                getGroupsIds(groups, Arrays.asList("DEV", "INT", "PRD")),
                null,
                MEMBER_ROLE,
                null
        );

        databucket.extension.createBucket(
                "fact_check",
                "dev-jobs",
                null,
                false,
                false,
                getIdByName(classes, "job"),
                getGroupsIds(groups, Collections.singletonList("DEV")),
                null,
                MEMBER_ROLE,
                null
        );

        databucket.extension.createBucket(
                "fact_check",
                "int-jobs",
                null,
                false,
                false,
                getIdByName(classes, "job"),
                getGroupsIds(groups, Collections.singletonList("INT")),
                null,
                MEMBER_ROLE,
                null
        );
    }

    private void prepareGroups() {
        databucket.extension.createGroup("DEV", null, "Development environment zone", null, MEMBER_ROLE, null);
        databucket.extension.createGroup("INT", null, "Integration environment zone", null, MEMBER_ROLE, null);
        databucket.extension.createGroup("PRD", null, "Production environment zone", null, MEMBER_ROLE, null);
    }

    private void prepareClasses(List<Map<String, Object>> enums) {
        List<Map<String, Object>> userFields = new ArrayList<>();
        Map<String, Object> firstName = new HashMap<>();
        firstName.put("title", "First name");
        firstName.put("path", "$.firstName");
        firstName.put("type", "string");
        firstName.put("uuid", "74bf3a11-4a42-4318-909e-b668629299c9");
        userFields.add(firstName);
        Map<String, Object> lastName = new HashMap<>();
        lastName.put("title", "Last name");
        lastName.put("path", "$.lastName");
        lastName.put("type", "string");
        lastName.put("uuid", "cd42795b-74ce-4301-9aae-aac16ebc0841");
        userFields.add(lastName);
        Map<String, Object> eyeColor = new HashMap<>();
        eyeColor.put("title", "Eye color");
        eyeColor.put("path", "$.eyeColor");
        eyeColor.put("type", "select");
        eyeColor.put("enumId", getIdByName(enums, "Eye colors"));
        eyeColor.put("uuid", "cd42795b-74ce-4301-9aae-aac16ebc0842");
        userFields.add(eyeColor);
        Map<String, Object> height = new HashMap<>();
        height.put("title", "Height");
        height.put("path", "$.height");
        height.put("type", "select");
        height.put("enumId", getIdByName(enums, "Height"));
        height.put("uuid", "cd42795b-74ce-4301-9aae-aac16ebc0843");
        userFields.add(height);
        Map<String, Object> company = new HashMap<>();
        company.put("title", "Company");
        company.put("path", "$.company");
        company.put("type", "string");
        company.put("uuid", "3fa8a073-88b0-4a41-a454-9c39fdd21f1a");
        userFields.add(company);
        Map<String, Object> Address = new HashMap<>();
        Address.put("title", "Address");
        Address.put("path", "$.address");
        Address.put("type", "string");
        Address.put("uuid", "a2a3650a-5a6a-40b9-8b5d-bbf24e99aeb9");
        userFields.add(Address);
        Map<String, Object> City = new HashMap<>();
        City.put("title", "City");
        City.put("path", "$.city");
        City.put("type", "string");
        City.put("uuid", "85d3ccb3-f04c-48e0-a147-3a57d878f8ec");
        userFields.add(City);
        Map<String, Object> state = new HashMap<>();
        state.put("title", "State");
        state.put("path", "$.state");
        state.put("type", "string");
        state.put("uuid", "3dac9377-8e81-46e2-9e63-308eb9e31ae9");
        userFields.add(state);
        Map<String, Object> Post = new HashMap<>();
        Post.put("title", "Post");
        Post.put("path", "$.post");
        Post.put("type", "string");
        Post.put("uuid", "06f98ea7-fe84-4d46-acf4-76a4eec3f68e");
        userFields.add(Post);
        Map<String, Object> Phone = new HashMap<>();
        Phone.put("title", "Phone");
        Phone.put("path", "$.contact.phone");
        Phone.put("type", "string");
        Phone.put("uuid", "efbdbe67-12ba-417e-9a20-de41e0e8beda");
        userFields.add(Phone);
        Map<String, Object> Email = new HashMap<>();
        Email.put("title", "Email");
        Email.put("path", "$.contact.email");
        Email.put("type", "string");
        Email.put("uuid", "1aed3f5a-5848-43db-aa74-93036ff00306");
        userFields.add(Email);
        Map<String, Object> Web = new HashMap<>();
        Web.put("title", "Web");
        Web.put("path", "$.web");
        Web.put("type", "string");
        Web.put("uuid", "e9f4d22c-5256-49ff-9e6b-3088357c11f7");
        userFields.add(Web);
        databucket.extension.createClass("user", "Example user data class", userFields);

        List<Map<String, Object>> schedulerFields = new ArrayList<>();
        Map<String, Object> dataId = new HashMap<>();
        dataId.put("title", "Data id");
        dataId.put("path", "$.data.id");
        dataId.put("type", "numeric");
        dataId.put("uuid", "b57c440d-965b-463c-995f-64a5fa97e9d7");
        schedulerFields.add(dataId);
        Map<String, Object> scheduleTime = new HashMap<>();
        scheduleTime.put("title", "Schedule time");
        scheduleTime.put("path", "$.schedule.time");
        scheduleTime.put("type", "datetime");
        scheduleTime.put("uuid", "529609be-a93d-4b6d-97a5-ac0388f3c4b1");
        schedulerFields.add(scheduleTime);
        Map<String, Object> scenario = new HashMap<>();
        scenario.put("title", "Scenario");
        scenario.put("path", "$.schedule.scenario");
        scenario.put("type", "string");
        scenario.put("uuid", "ac1a7c48-502a-47b9-948c-2acbe6ac6230");
        schedulerFields.add(scenario);
        Map<String, Object> status = new HashMap<>();
        status.put("title", "Status");
        status.put("path", "$.execution.status");
        status.put("type", "select");
        status.put("enumId", getIdByName(enums, "Status"));
        status.put("uuid", "af74bed5-f022-4eec-85ab-e740ba02f68c");
        schedulerFields.add(status);
        Map<String, Object> executionTime = new HashMap<>();
        executionTime.put("title", "Execution time");
        executionTime.put("path", "$.execution.time");
        executionTime.put("type", "datetime");
        executionTime.put("uuid", "03bbcbda-86f5-4941-892d-88efa5ee082c");
        schedulerFields.add(executionTime);
        databucket.extension.createClass("scheduler", null, schedulerFields);

        List<Map<String, Object>> configurationFields = new ArrayList<>();
        Map<String, Object> active = new HashMap<>();
        active.put("title", "Active");
        active.put("path", "$.active");
        active.put("type", "boolean");
        active.put("uuid", "7b4db99d-ded6-4136-b497-ce29c89b3c62");
        configurationFields.add(active);
        Map<String, Object> environment = new HashMap<>();
        environment.put("title", "Environment");
        environment.put("path", "$.environment");
        environment.put("type", "select");
        environment.put("enumId", getIdByName(enums, "Environment"));
        environment.put("uuid", "cf372958-0a5f-4258-9548-f8bec970387a");
        configurationFields.add(environment);
        Map<String, Object> key = new HashMap<>();
        key.put("title", "Key");
        key.put("path", "$.key");
        key.put("type", "string");
        key.put("uuid", "92b3a1db-1f8e-414e-bbd3-5998d05cbea0");
        configurationFields.add(key);
        Map<String, Object> value = new HashMap<>();
        value.put("title", "Value");
        value.put("path", "$.value");
        value.put("type", "string");
        value.put("uuid", "ec217165-c557-4470-ad7f-1d4698efbd38");
        configurationFields.add(value);
        Map<String, Object> encrypted = new HashMap<>();
        encrypted.put("title", "Encrypted");
        encrypted.put("path", "$.encrypted");
        encrypted.put("type", "boolean");
        encrypted.put("uuid", "d8098c66-9fc3-46b2-8ce8-88910fa518e0");
        configurationFields.add(encrypted);
        Map<String, Object> description = new HashMap<>();
        description.put("title", "Description");
        description.put("path", "$.description");
        description.put("type", "string");
        description.put("uuid", "045935b6-0c0a-4a37-aa1e-8db9a8470b7c");
        configurationFields.add(description);
        databucket.extension.createClass("config", null, configurationFields);

        List<Map<String, Object>> jobFields = new ArrayList<>();
        Map<String, Object> userId = new HashMap<>();
        userId.put("title", "User ID");
        userId.put("path", "$.userId");
        userId.put("type", "string");
        userId.put("uuid", "b57c440d-965b-463c-995f-64a5fa97e9d8");
        jobFields.add(userId);
        Map<String, Object> jobName = new HashMap<>();
        jobName.put("title", "Job name");
        jobName.put("path", "$.jobName");
        jobName.put("type", "string");
        jobName.put("uuid", "529609be-a93d-4b6d-97a5-ac0388f3c4b2");
        jobFields.add(jobName);
        Map<String, Object> jobStatus = new HashMap<>();
        jobStatus.put("title", "Status");
        jobStatus.put("path", "$.status");
        jobStatus.put("type", "select");
        jobStatus.put("enumId", getIdByName(enums, "Status"));
        jobStatus.put("uuid", "af74bed5-f022-4eec-85ab-e740ba02f68c");
        jobFields.add(jobStatus);
        Map<String, Object> jobValue = new HashMap<>();
        jobValue.put("title", "Value");
        jobValue.put("path", "$.value");
        jobValue.put("type", "numeric");
        jobValue.put("uuid", "af74bed5-f022-4eec-85ab-e740ba02f68d");
        jobFields.add(jobValue);
        databucket.extension.createClass("job", null, jobFields);
    }

    private void prepareTeams() {
        databucket.extension.createTeam("Team A", null, Arrays.asList("super", "userSAMR", "userAMR", "userMR", "userR"));
        databucket.extension.createTeam("Team B", null, Arrays.asList("userDisabled", "userExpired"));
        databucket.extension.createTeam("Team C", null, Arrays.asList("userSAMR", "userAMR", "userMR"));
    }

    private void prepareEnums() {
        List<Map<String, String>> colors = new ArrayList<>();
        Map<String, String> blueColor = new HashMap<>();
        blueColor.put("value", "blue");
        blueColor.put("text", "Blue");
        colors.add(blueColor);
        Map<String, String> blackColor = new HashMap<>();
        blackColor.put("value", "black");
        blackColor.put("text", "Black");
        colors.add(blackColor);
        Map<String, String> greenColor = new HashMap<>();
        greenColor.put("value", "green");
        greenColor.put("text", "Green");
        colors.add(greenColor);
        Map<String, String> greyColor = new HashMap<>();
        greyColor.put("value", "grey");
        greyColor.put("text", "Grey");
        colors.add(greyColor);
        databucket.extension.createEnum("Eye colors", "Most popular eye colors", false, colors);

        List<Map<String, String>> height = new ArrayList<>();
        Map<String, String> lessThan1m = new HashMap<>();
        lessThan1m.put("value", "h<1m");
        lessThan1m.put("text", "Less than 1m");
        height.add(lessThan1m);
        Map<String, String> between1m2m = new HashMap<>();
        between1m2m.put("value", "1m<h<2m");
        between1m2m.put("text", "Between 1m and 2m");
        height.add(between1m2m);
        Map<String, String> moreThan2m = new HashMap<>();
        moreThan2m.put("value", "h>2m");
        moreThan2m.put("text", "More than 2m");
        height.add(moreThan2m);
        databucket.extension.createEnum("Height", "Height ranges", false, height);

        List<Map<String, String>> statuses = new ArrayList<>();
        Map<String, String> passed = new HashMap<>();
        passed.put("value", "passed");
        passed.put("text", "Passed");
        passed.put("icon", "check_circle_outline");
        statuses.add(passed);
        Map<String, String> warning = new HashMap<>();
        warning.put("value", "warning");
        warning.put("text", "Warning");
        warning.put("icon", "report_gmailerrorred");
        statuses.add(warning);
        Map<String, String> failed = new HashMap<>();
        failed.put("value", "failed");
        failed.put("text", "Failed");
        failed.put("icon", "cancel");
        statuses.add(failed);
        databucket.extension.createEnum("Status", null, true, statuses);

        List<Map<String, String>> environments = new ArrayList<>();
        Map<String, String> devDefault = new HashMap<>();
        devDefault.put("value", "default");
        devDefault.put("text", "default");
        environments.add(devDefault);
        Map<String, String> devEnv = new HashMap<>();
        devEnv.put("value", "DEV");
        devEnv.put("text", "DEV");
        environments.add(devEnv);
        Map<String, String> devINT = new HashMap<>();
        devINT.put("value", "INT");
        devINT.put("text", "INT");
        environments.add(devINT);
        Map<String, String> devPRD = new HashMap<>();
        devPRD.put("value", "PRD");
        devPRD.put("text", "PRD");
        environments.add(devPRD);
        databucket.extension.createEnum("Environment", "List of environments", false, environments);

        List<Map<String, String>> baseJobs = new ArrayList<>();
        Map<String, String> createNewUser = new HashMap<>();
        createNewUser.put("value", "base-create-new-user");
        createNewUser.put("text", "Create new user");
        baseJobs.add(createNewUser);
        Map<String, String> removeAllCreditCards = new HashMap<>();
        removeAllCreditCards.put("value", "base-remove-all-credit-cards");
        removeAllCreditCards.put("text", "Remove all credit cards");
        baseJobs.add(removeAllCreditCards);
        Map<String, String> suspendUser = new HashMap<>();
        suspendUser.put("value", "base-suspend-user");
        suspendUser.put("text", "Suspend user");
        baseJobs.add(suspendUser);
        Map<String, String> reactivateUser = new HashMap<>();
        reactivateUser.put("value", "base-reactivate-user");
        reactivateUser.put("text", "Reactivate user");
        baseJobs.add(reactivateUser);
        databucket.extension.createEnum("Jobs - base", null, false, baseJobs);

        List<Map<String, String>> moneyJobs = new ArrayList<>();
        Map<String, String> increaseMoney = new HashMap<>();
        increaseMoney.put("value", "money-increase");
        increaseMoney.put("text", "Increase money");
        moneyJobs.add(increaseMoney);
        Map<String, String> reduceMoney = new HashMap<>();
        reduceMoney.put("value", "money-reduce");
        reduceMoney.put("text", "Reduce money");
        moneyJobs.add(reduceMoney);
        databucket.extension.createEnum("Jobs - money", null, false, moneyJobs);
    }

    private void prepareProjectManagement() {
        databucket.authenticate(username, password, null);
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

    private Map<String, Object> getObjectByName(List<Map<String, Object>> listOfObjects, String objectName) {
        for (Map<String, Object> obj : listOfObjects) {
            if (obj.containsKey("name") && obj.get("name").equals(objectName)) {
                return obj;
            }
        }
        return null;
    }

    private int getIdByName(List<Map<String, Object>> listOfObjects, String objectName) {
        for (Map<String, Object> obj : listOfObjects) {
            if (obj.containsKey("name") && obj.get("name").equals(objectName)) {
                return ((Double) obj.get("id")).intValue();
            }
        }
        return -1;
    }

    private List<Integer> getIdsByNames(List<Map<String, Object>> listOfObjects, List<String> objectName) {
        List<Integer> ids = new ArrayList<>();

        for (Map<String, Object> obj : listOfObjects)
            if (obj.containsKey("name") && objectName.contains((String) obj.get("name")))
                ids.add(((Double) obj.get("id")).intValue());

        return ids;
    }

    private List<Integer> getGroupsIds(List<Map<String, Object>> listOfItems, List<String> itemsShortNames) {
        List<Integer> ids = new ArrayList<>();

        for (Map<String, Object> team : listOfItems)
            if (team.containsKey("shortName") && itemsShortNames.contains((String) team.get("shortName")))
                ids.add(((Double) team.get("id")).intValue());

        return ids;
    }

    private String getUUID(List<Map<String, Object>> properties, String title) {
        for (Map<String, Object> property : properties)
            if (property.containsKey("title") && property.get("title").equals(title))
                return (String) property.get("uuid");

        return null;
    }
}
