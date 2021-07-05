package pl.databucket3.api.java.client;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;
import java.util.*;

public class DatabucketExtension {

    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private String serviceUrl;
    private Gson gson;
    private Client client = null;
    private Map<String, Object> httpHeaders;

    public DatabucketExtension(String serviceUrl, Gson gson, Client client, Map<String, Object> httpHeaders) {
        this.serviceUrl = serviceUrl;
        this.client = client;
        this.gson = gson;
        this.httpHeaders = httpHeaders;
    }

    /**
     * Builds headers configuration before send request
     *
     * @param builder - a map of headers
     */
    private void setHeaders(WebResource.Builder builder) {
        for (Map.Entry<String, Object> entry : httpHeaders.entrySet())
            builder = builder.header(entry.getKey(), entry.getValue());
    }

    private List<Integer> getUsersIds(List<String> users) {
        String resource = "/api/manage/users";

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.get(ClientResponse.class);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() == 200) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> usersObjects = gson.fromJson(responseBody, List.class);
            List<Integer> ids = new ArrayList<>();
            for (Map<String, Object> user : usersObjects)
                if (user.containsKey("username")) {
                    String username = (String) user.get("username");
                    if (users.contains(username))
                        ids.add(((Double) user.get("id")).intValue());
                }
            return ids;
        } else
            throw new RuntimeException("Response status: " + response.getStatus());
    }

    public void createUser(boolean enabled, String username, List<Integer> rolesIds, String expirationDate) {
        String resource = "/api/manage/users";

        Map<String, Object> json = new HashMap<>();
        json.put("enabled", enabled);
        json.put("username", username);
        json.put("rolesIds", rolesIds);
        if (expirationDate != null)
            json.put("expirationDate", expirationDate);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() != 201)
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }


    public void createProject(boolean enabled, String projectName, String description, List<String> users, String expirationDate) {
        List<Integer> usersIds = getUsersIds(users);
        String resource = "/api/manage/projects";

        Map<String, Object> json = new HashMap<>();
        json.put("enabled", enabled);
        json.put("name", projectName);
        if (description != null)
            json.put("description", description);
        json.put("usersIds", usersIds);
        if (expirationDate != null)
            json.put("expirationDate", expirationDate);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() != 201)
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }

    public void createTeam(String name, String description, List<String> users) {
        List<Integer> usersIds = getUsersIds(users);
        String resource = "/api/teams";

        Map<String, Object> json = new HashMap<>();
        json.put("name", name);
        if (description != null)
            json.put("description", description);
        json.put("usersIds", usersIds);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() != 201)
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }

    public List<Map<String, Object>> getTeams() {
        String resource = "/api/teams";

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.get(ClientResponse.class);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() == 200) {
            return (List<Map<String, Object>>) gson.fromJson(responseBody, List.class);
        } else
            throw new RuntimeException("Response status: " + response.getStatus());
    }

    public void createEnum(String name, String description, boolean iconsEnabled, List<Map<String, String>> items) {
        String resource = "/api/enums";

        Map<String, Object> json = new HashMap<>();
        json.put("name", name);
        if (description != null)
            json.put("description", description);
        json.put("iconsEnabled", iconsEnabled);
        json.put("items", items);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() != 201)
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }

    public List<Map<String, Object>> getEnums() {
        String resource = "/api/enums";

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.get(ClientResponse.class);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() == 200) {
            return (List<Map<String, Object>>) gson.fromJson(responseBody, List.class);
        } else
            throw new RuntimeException("Response status: " + response.getStatus());
    }

    public void createGroup(String shortName, String name, String description, List<Integer> usersIds, Integer roleId, List<Integer> teamsIds) {
        String resource = "/api/groups";

        Map<String, Object> json = new HashMap<>();
        json.put("shortName", shortName);
        json.put("name", name);
        if (description != null)
            json.put("description", description);
        if (usersIds != null)
            json.put("usersIds", usersIds);
        if (roleId != null)
            json.put("roleId", roleId);
        if (teamsIds != null)
            json.put("teamsIds", teamsIds);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() != 201)
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }


    public void createBucket(String iconName, String name, String description, Boolean history, Boolean protectData, Integer classId, List<Integer> groupsIds, List<Integer> usersIds, Integer roleId, List<Integer> teamsIds) {
        String resource = "/api/buckets";

        Map<String, Object> json = new HashMap<>();
        json.put("iconName", iconName);
        json.put("name", name);
        if (description != null)
            json.put("description", description);
        if (history != null)
            json.put("history", history);
        if (protectData != null)
            json.put("protectData", protectData);
        if (classId != null)
            json.put("classId", classId);
        if (groupsIds != null)
            json.put("groupsIds", groupsIds);
        if (usersIds != null)
            json.put("usersIds", usersIds);
        if (roleId != null)
            json.put("roleId", roleId);
        if (teamsIds != null)
            json.put("teamsIds", teamsIds);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() != 201)
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }

    public void createClass(String name, String description, List<Map<String, Object>> configuration) {
        String resource = "/api/classes";

        Map<String, Object> json = new HashMap<>();
        json.put("name", name);
        if (description != null)
            json.put("description", description);
        json.put("configuration", configuration);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() != 201)
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }

    public void createColumns(String name, String description, Integer classId, Map<String, Object> configuration) {
        String resource = "/api/columns";

        Map<String, Object> json = new HashMap<>();
        json.put("name", name);
        if (description != null)
            json.put("description", description);
        if (classId != null)
            json.put("classId", classId);
        json.put("configuration", configuration);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() != 201)
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }

    public void createFilter(String name, String description, Integer classId, Map<String, Object> configuration) {
        String resource = "/api/filters";

        Map<String, Object> json = new HashMap<>();
        json.put("name", name);
        if (description != null)
            json.put("description", description);
        if (classId != null)
            json.put("classId", classId);
        json.put("configuration", configuration);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() != 201)
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }

    public void createView(String name, String description, Integer columnsId, Integer filterId, List<Integer> classesIds, List<Integer> teamsIds, List<Integer> featuresIds) {
        String resource = "/api/views";

        Map<String, Object> json = new HashMap<>();
        json.put("name", name);
        if (description != null)
            json.put("description", description);
        json.put("columnsId", columnsId);
        if (filterId != null)
            json.put("filterId", filterId);
        if (classesIds != null)
            json.put("classesIds", classesIds);
        if (teamsIds != null)
            json.put("teamsIds", teamsIds);
        if (featuresIds != null)
            json.put("featuresIds", featuresIds);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() != 201)
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }

    public void createTask(String name, String description, Integer classId, Integer filterId, List<Integer> classesIds, Map<String, Object> configuration) {
        String resource = "/api/tasks";

        Map<String, Object> json = new HashMap<>();
        json.put("name", name);
        if (description != null)
            json.put("description", description);
        if (classId != null)
            json.put("classId", classId);
        if (filterId != null)
            json.put("filterId", filterId);
        if (classesIds != null)
            json.put("classesIds", classesIds);
        json.put("configuration", configuration);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() != 201)
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }

    public List<Map<String, Object>> getClasses() {
        String resource = "/api/classes";

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.get(ClientResponse.class);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() == 200) {
            return (List<Map<String, Object>>) gson.fromJson(responseBody, List.class);
        } else
            throw new RuntimeException("Response status: " + response.getStatus());
    }

    public List<Map<String, Object>> getColumns() {
        String resource = "/api/columns";

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.get(ClientResponse.class);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() == 200) {
            return (List<Map<String, Object>>) gson.fromJson(responseBody, List.class);
        } else
            throw new RuntimeException("Response status: " + response.getStatus());
    }

    public List<Map<String, Object>> getFilters() {
        String resource = "/api/filters";

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.get(ClientResponse.class);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() == 200) {
            return (List<Map<String, Object>>) gson.fromJson(responseBody, List.class);
        } else
            throw new RuntimeException("Response status: " + response.getStatus());
    }

    public List<Map<String, Object>> getGroups() {
        String resource = "/api/groups";

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.get(ClientResponse.class);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() == 200) {
            return (List<Map<String, Object>>) gson.fromJson(responseBody, List.class);
        } else
            throw new RuntimeException("Response status: " + response.getStatus());
    }

    public List<Map<String, Object>> getBuckets() {
        String resource = "/api/buckets";

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.get(ClientResponse.class);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() == 200) {
            return (List<Map<String, Object>>) gson.fromJson(responseBody, List.class);
        } else
            throw new RuntimeException("Response status: " + response.getStatus());
    }

    public List<Map<String, Object>> getTags() {
        String resource = "/api/tags";

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.get(ClientResponse.class);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() == 200) {
            return (List<Map<String, Object>>) gson.fromJson(responseBody, List.class);
        } else
            throw new RuntimeException("Response status: " + response.getStatus());
    }

    public void createTag(String name, String description, List<Integer> bucketsIds, List<Integer> classesIds) {
        String resource = "/api/tags";

        Map<String, Object> json = new HashMap<>();
        json.put("name", name);
        if (description != null)
            json.put("description", description);
        if (bucketsIds != null)
            json.put("bucketsIds", bucketsIds);
        if (classesIds != null)
            json.put("classesIds", classesIds);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(serviceUrl + resource);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() != 201)
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }
}
