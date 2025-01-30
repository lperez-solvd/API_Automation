package com.solvd.gorest.user.service;

import com.solvd.gorest.User;
import com.solvd.gorest.utils.HttpStatus;
import com.solvd.gorest.utils.JsonMappers;
import com.solvd.gorest.utils.PlaceholdersUtils;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static io.restassured.RestAssured.given;

public class GraphQLService {

    Dotenv dotenv = Dotenv.load();
    String accessToken = dotenv.get("ACCESS_TOKEN");

    private final JsonMappers jsonMappers = new JsonMappers();
    private final PlaceholdersUtils placeholderUtils = new PlaceholdersUtils();

    public Response getUsers() {
        String query = loadGraphQLQuery("getUsers.graphql");
        return sendRequest(query);
    }

    public Response getUserById(int id) {
        String query = completeQueryWithPlaceholders(loadGraphQLQuery("getUserById.graphql"), id);
        return sendRequest(query);
    }

    public Response createUser(User user) throws Exception {

        String query = completeQueryWithPlaceholders(loadGraphQLQuery("createUser.graphql"), user.getName(), user.getEmail(), user.getGender(), user.getStatus());

        return sendRequest(query);
    }

    public Response deleteUser(int userId) {

        String query = completeQueryWithPlaceholders(loadGraphQLQuery("deleteUser.graphql"), userId);
        return sendRequest(query);
    }

    public Response updateUser(int createdUserId, User userRequest) {

        String nquery = completeQueryWithPlaceholders(loadGraphQLQuery("updateUser.graphql"), createdUserId, userRequest.getName(), userRequest.getEmail(), userRequest.getGender(), userRequest.getStatus());
        return sendRequest(nquery);
    }

    protected Response sendRequest(String query) {

        Response response =
                given()
                        .auth()
                        .oauth2(accessToken)
                        .contentType(ContentType.JSON)
                        .body(query)
                        .post("https://gorest.co.in/public/v2/graphql");

        response.then().statusCode(HttpStatus.GRAPHQL_OK);
        return response;

    }

    // Query creator

    private String loadGraphQLQuery(String fileName) {
        Path path = Paths.get("src/test/java/com/solvd/gorest/user/service/queries/" + fileName);
        try {
            // Read the plain GraphQL query from the file
            String query = Files.readString(path, StandardCharsets.UTF_8).trim();

            // Remove newlines and excessive spaces from the GraphQL query
            query = query.replaceAll("\\s+", " ");  // Replace multiple spaces with a single space
            query = query.replace("\n", " ");        // Replace newlines with a space
            query = query.replace("\r", " ");        // Replace carriage returns with a space

            // Wrap the query in the proper JSON format
            return "{ \"query\": \"" + query.replace("\"", "\\\"") + "\" }";
        } catch (IOException e) {
            throw new RuntimeException("Failed to load query file: " + fileName, e);
        }
    }

    private String completeQueryWithPlaceholders(String query, int intPlaceholder) {
        try {
            return String.format(query, intPlaceholder);
        } catch (Exception e) {
            System.out.println("The query doesn't have the expected placeholders");
            throw new RuntimeException();
        }

    }

    private String completeQueryWithPlaceholders(String query, String placeholder1, String placeholder2, String placeholder3, String placeholder4) {
        try {
            return String.format(query, placeholder1, placeholder2, placeholder3, placeholder4);
        } catch (Exception e) {
            System.out.println("The query doesn't have the expected placeholders");
            throw new RuntimeException();
        }
    }

    private String completeQueryWithPlaceholders(String query, int placeholder1, String placeholder2, String placeholder3, String placeholder4, String placeholder5) {
        try {
            return String.format(query, placeholder1, placeholder2, placeholder3, placeholder4, placeholder5);
        } catch (Exception e) {
            System.out.println("The query doesn't have the expected placeholders");
            throw new RuntimeException();
        }
    }

    // helpers

    public int getStatus(Response response) {
        return response.getStatusCode();
    }

    public List<User> getUsersFromResponse(Response response) {
        return response.body().jsonPath().getList("data.users.nodes", User.class);
    }

    public User getUserFromResponse(Response response) {
        return response.body().jsonPath().getObject("data.user", User.class);
    }

    public User getUserForRequest(String randomMail) throws Exception {
        String jsonRequest = placeholderUtils.replaceEmailPlaceholder("src/test/resources/com.solvd.gorest/json/post/CreateUserRequest.json", randomMail);
        return jsonMappers.convertJsonToUser(jsonRequest);
    }

    public User getUserForRequestPatch(String randomMail) throws Exception {
        String jsonRequest = placeholderUtils.replaceEmailPlaceholder("src/test/resources/com.solvd.gorest/json/patch/modifyUserReq.json", randomMail);
        return jsonMappers.convertJsonToUser(jsonRequest);
    }

    public User getCreatedUserFromResponse(Response response) {
        return response.body().jsonPath().getObject("data.createUser.user", User.class);
    }

    public User getUpdatedUserFromResponse(Response response) {
        return response.body().jsonPath().getObject("data.updateUser.user", User.class);
    }

    public int getDeletedUserId(Response response) {
        return response.jsonPath().getInt("data.deleteUser.user.id");
    }

}
