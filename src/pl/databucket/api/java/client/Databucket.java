package pl.databucket.api.java.client;

import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

public class Databucket {

    private String serviceUrl;
    private Gson gson;
    private Client client;
    private Map<String, Object> httpHeaders = new HashMap();
    public DatabucketExtension extension = null;
    private Map<String, Integer> tags;

    public Databucket(String serviceUrl, boolean logs) {
        this.serviceUrl = serviceUrl;
        client = Client.create();
        if (logs)
            client.addFilter(new LoggingFilter(System.out));
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    public Databucket(String serviceUrl, String username, String password, Integer projectId, boolean logs) {
        this.serviceUrl = serviceUrl;
        client = Client.create();
        if (logs)
            client.addFilter(new LoggingFilter(System.out));
        gson = new GsonBuilder().disableHtmlEscaping().create();
        authenticate(username, password, projectId);
        loadTags();
    }

    public Databucket(String serviceUrl, String username, String password, Integer projectId, boolean logs, Proxy proxy) {
        this.serviceUrl = serviceUrl;

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.initializeProxy(proxy);
        URLConnectionClientHandler clientHandler = new URLConnectionClientHandler(connectionFactory);
        client = new Client(clientHandler);
        if (logs)
            client.addFilter(new LoggingFilter(System.out));

        gson = new GsonBuilder().disableHtmlEscaping().create();
        authenticate(username, password, projectId);
        loadTags();
    }

    public Client getClient() {
        return client;
    }

    public String buildUrl(String resource) {
        return serviceUrl + resource;
    }

    public void addHeader(String name, Object value) {
        httpHeaders.put(name, value);
    }


    public void setHeaders(Builder builder) {
        for (Map.Entry<String, Object> entry : httpHeaders.entrySet())
            builder = builder.header(entry.getKey(), entry.getValue());
    }

    @SuppressWarnings("unchecked")
    public void authenticate(String username, String password, Integer projectId) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("username", username);
        json.put("password", password);
        if (projectId != null)
            json.put("projectId", projectId);

        String payload = gson.toJson(json);

        WebResource webResource = client.resource(buildUrl("/api/public/signin"));
        Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.post(ClientResponse.class, payload);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() == 200) {
            Map<String, Object> result = gson.fromJson(responseBody, Map.class);
            addHeader("Authorization", "Bearer " + result.get(ResponseField.TOKEN));
        } else
            throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
    }

    @SuppressWarnings("unchecked")
    public void loadTags() {
        WebResource webResource = client.resource(buildUrl("/api/tags"));
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON);
        setHeaders(builder);

        ClientResponse response = builder.get(ClientResponse.class);
        String responseBody = response.getEntity(String.class);

        if (response.getStatus() == 200) {
            Map<String, Integer> tagsMap = new HashMap<>();
            List<Map<String, Object>> tagsList = gson.fromJson(responseBody, List.class);
            tagsList.forEach(tagDef -> tagsMap.put((String) tagDef.get("name"), ((Double) tagDef.get("id")).intValue()));
            tags = tagsMap;
        } else
            throw new RuntimeException("Response status: " + response.getStatus());
    }

    public Integer getTagId(String tagName) {
    	if (tags.containsKey(tagName))
    		return tags.get(tagName);
    	return null;
	}

    public void initiateExtension() {
        this.extension = new DatabucketExtension(serviceUrl, gson, client, httpHeaders);
    }
}
