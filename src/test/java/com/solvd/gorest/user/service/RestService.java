package com.solvd.gorest.user.service;

import com.solvd.gorest.Comment;
import com.solvd.gorest.Post;
import com.solvd.gorest.User;
import com.solvd.gorest.utils.JsonMappers;
import com.solvd.gorest.utils.PlaceholdersUtils;
import freemarker.template.TemplateException;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;

public class RestService {

    Dotenv dotenv = Dotenv.load();
    String accessToken = dotenv.get("ACCESS_TOKEN");

    private final String createUserRequestTemplate = "src/test/resources/com.solvd.gorest/json/post/CreateUserRequest.json";
    private final String createUserResponseTemplate = "src/test/resources/com.solvd.gorest/json/post/CreateUserResponse.json";
    private final String updateUserRequestTemplate = "src/test/resources/com.solvd.gorest/json/patch/modifyUserReq.json";
    private final String updateUserResponseTemplate = "src/test/resources/com.solvd.gorest/json/patch/modifyUserRes.json";
    private final String createPostRequestTemplate = "src/test/resources/com.solvd.gorest/json/post/createNewPostReq.json";
    private final String createPostResponseTemplate = "src/test/resources/com.solvd.gorest/json/post/createNewPostRes.json";
    private final String createCommentRequestTemplate = "src/test/resources/com.solvd.gorest/json/post/createNewCommentReq.json";
    private final String createCommentResponseTemplate = "src/test/resources/com.solvd.gorest/json/post/createNewCommentRes.json";


    private static final String BASE_URL = "https://gorest.co.in/public/v2/users/";
    private static final String POST_URL = "https://gorest.co.in/public/v2/posts/";
    private final JsonMappers jsonMappers = new JsonMappers();
    private final PlaceholdersUtils placeholderUtils = new PlaceholdersUtils();

    public RestService() {
    }

    public Response getRequest() {
        return given().get(BASE_URL);
    }

    public Response getRequest(int userId) {

        return given().get("https://gorest.co.in/public/v2/users/" + userId);
    }

    public Response getAllPosts() {
        return given().get(POST_URL);
    }

    public Response postRequest(String body, boolean withCredentials) {
        if (withCredentials) {
            return given()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentType.JSON)
                    .body(body)
                    .post("https://gorest.co.in/public/v2/users/");
        } else {

            return given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .post("https://gorest.co.in/public/v2/users/");
        }

    }

    public Response deleteRequest(int createdUserId) {

        return given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .delete("https://gorest.co.in/public/v2/users/" + createdUserId);

    }

    public Response patchRequest(User user) {

        return given()
                .auth()
                .oauth2(accessToken)
                .body(user)
                .contentType("application/json")
                .patch("https://gorest.co.in/public/v2/users/" + user.getId());
    }

    public Response postCreationRequest(String request, int createdUserId) {
        return given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .post("https://gorest.co.in/public/v2/users/" + createdUserId + "/posts");

    }

    public Response commentCreationRequest(String request, int postId) {
        return given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .post("https://gorest.co.in/public/v2/posts/" + postId + "/comments");
    }

    // Helpers

    public int getStatus(Response response) {
        return response.getStatusCode();
    }

    public int getCreatedUserId(Response response) {
        return Integer.parseInt(response.jsonPath().getString("id"));
    }


    public int getCreatedPostId(Response response) {
        return Integer.parseInt(response.jsonPath().getString("id"));
    }

    public int getFirstPostId(Response response) {
        return response.jsonPath().getInt("[0].id");
    }

    public List<User> convertResponseToUsers(Response response) throws Exception {
        return jsonMappers.convertJsonToUsers(response.then().extract().asString());
    }

    public User convertResponseToUser(Response response) throws Exception {
        return jsonMappers.convertJsonToUser(response.getBody().asString());
    }

    public Post convertResponseToPost(Response response) throws Exception {
        return jsonMappers.convertJsonToPost(response.getBody().asString());
    }

    public Comment convertResponseToComment(Response response) throws Exception {
        return jsonMappers.convertJsonToComment(response.getBody().asString());
    }

    public String preparePostRequest(String email) throws TemplateException, IOException {
        return placeholderUtils.replaceEmailPlaceholder(createUserRequestTemplate, email);
    }

    public String preparePostResponse(int newUserId, String email) throws TemplateException, IOException {
        return placeholderUtils.replaceResponsePlaceholders(createUserResponseTemplate, String.valueOf(newUserId), email);
    }

    public String preparePatchRequest(String email) throws TemplateException, IOException {
        return placeholderUtils.replaceEmailPlaceholder(updateUserRequestTemplate, email);
    }

    public String preparePatchResponse(int newUserId, String email) throws TemplateException, IOException {
        return placeholderUtils.replaceResponsePlaceholders(updateUserResponseTemplate, String.valueOf(newUserId), email);
    }

    public String prepareCreatePostRequest(int userId) throws TemplateException, IOException {
        return placeholderUtils.replaceNewPostReqPlaceholders(createPostRequestTemplate, String.valueOf(userId));
    }

    public String prepareCreatePostResponse(int userId, int postId) throws TemplateException, IOException {
        return placeholderUtils.replaceNewPostResPlaceholders(createPostResponseTemplate, String.valueOf(userId), String.valueOf(postId));
    }

    public String prepareCreateCommentRequest(int postId) throws TemplateException, IOException {
        return placeholderUtils.replacePlaceholders(createCommentRequestTemplate, "post_id", String.valueOf(postId));
    }

    public String prepareCreateCommentResponse(int commentId, int postId) throws TemplateException, IOException {
        return placeholderUtils.replacePlaceholders(createCommentResponseTemplate, "id", String.valueOf(commentId), "post_id", String.valueOf(postId));
    }

    public User convertJsonToUser(String json) throws Exception {
        return jsonMappers.convertJsonToUser(json);
    }

    public Post convertJsonToPost(String json) throws Exception {
        return jsonMappers.convertJsonToPost(json);
    }

    public Comment convertJsonToComment(String json) throws Exception {
        return jsonMappers.convertJsonToComment(json);
    }


}
