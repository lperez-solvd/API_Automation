package com.solvd.gorest.user.service;

import com.solvd.gorest.User;
import com.solvd.gorest.utils.HttpStatus;
import com.solvd.gorest.utils.JsonMappers;
import com.solvd.gorest.utils.PlaceholdersUtils;
import freemarker.template.TemplateException;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;

public class GraphQLService {

    Dotenv dotenv = Dotenv.load();
    String accessToken = dotenv.get("ACCESS_TOKEN");

    private final JsonMappers jsonMappers = new JsonMappers();
    private final PlaceholdersUtils placeholderUtils = new PlaceholdersUtils();

    public Response getUsers() {
        String query = "{ \"query\": \"query Users { users { nodes { email gender id name status } } }\" }";
        return sendRequest(query);
    }

    public Response getUserById(int id) {
        String query = String.format("{ \"query\": \"{ user(id: %s) { id name email } }\" }", id);
        return sendRequest(query);
    }

    public Response createUser(User user) throws Exception {

        String query = String.format("{ \"query\": \"mutation CreateUser { createUser(input: { name: \\\"%s\\\" email: \\\"%s\\\" gender: \\\"%s\\\"  status: \\\"%s\\\"  clientMutationId: \\\"123123123\\\" }) { clientMutationId user { email name status id gender } } }\" }",
                user.getName(),
                user.getEmail(),
                user.getGender(),
                user.getStatus());

        return sendRequest(query);
    }

    public Response deleteUser(int userId) {

        String query = String.format("{ \"query\": \"mutation DeleteUser { deleteUser(input: { id: %s }) { user {id} } }\" }", userId);

        return sendRequest(query);
    }

    public Response updateUser(int createdUserId, User userRequest) {

        String query = String.format("{ \"query\": \"mutation UpdateUser { updateUser(input: { id: %d name: \\\"%s\\\" email: \\\"%s\\\" gender: \\\"%s\\\" status: \\\"%s\\\" clientMutationId: \\\"123123123\\\" }) { clientMutationId user { email name status id gender } } }\" }",
                createdUserId, userRequest.getName(), userRequest.getEmail(), userRequest.getGender(), userRequest.getStatus());

        return sendRequest(query);
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

}
