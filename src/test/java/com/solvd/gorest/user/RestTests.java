package com.solvd.gorest.user;

import com.solvd.gorest.User;
import com.solvd.gorest.user.baseTests.RestBaseTest;
import com.solvd.gorest.utils.HttpStatus;
import freemarker.template.TemplateException;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static com.solvd.gorest.utils.Mappers.*;
import static io.restassured.RestAssured.given;


public class RestTests extends RestBaseTest {


    // Suite shared attributes


    @Test(description = "Will get all the users but only page 1")
    public void getAllUsersByPage() throws Exception {
        Response response = getRequest();
        int numberOfResultsPerPage = Integer.parseInt(response.getHeader("X-Pagination-Limit"));


        List<User> users = convertJsonToUsers(response.then().extract().asString());
        firstUserFromResponse = users.getFirst();

        Assert.assertEquals(users.size(), numberOfResultsPerPage, "The number of users is not the expected");
    }

    @Test(dependsOnMethods = "getAllUsersByPage")
    public void getUserById() throws Exception {

        int idToSearchFor = firstUserFromResponse.getId();

        Response response = getRequest(idToSearchFor);

        String preparedResponse = response.getBody().asString();

        Assert.assertEquals(convertJsonToUser(preparedResponse), firstUserFromResponse, "The response is not the expected");

    }

    @Test
    public void createUserWithNoCredentialsTest() throws TemplateException, IOException {

        String newUser = replaceEmailPlaceholder("src/test/resources/com.solvd.gorest/json/post/CreateUserRequest.json", randomMail);

        Response response = postRequest(newUser, false);

        response
                .then()
                .statusCode(HttpStatus.USER_NO_AUTH_TOKEN);

    }

    @Test()
    public void createUserTest() throws Exception {

        String newUser = replaceEmailPlaceholder("src/test/resources/com.solvd.gorest/json/post/CreateUserRequest.json", randomMail);

        Response response = postRequest(newUser, true);

        response
                .then()
                .statusCode(HttpStatus.USER_CREATED);


        String generatedId = response.jsonPath().getString("id");
        createdUserId = Integer.parseInt(generatedId);
        String updatedTemplate = replaceResponsePlaceholders("src/test/resources/com.solvd.gorest/json/post/CreateUserResponse.json", generatedId, randomMail);
        String preparedResponse = response.getBody().asString();

        Assert.assertEquals(convertJsonToUser(preparedResponse), convertJsonToUser(updatedTemplate), "The response is not the expected");


    }

    @Test(dependsOnMethods = "createUserTest")
    public void createUserWithExistingEmail() throws TemplateException, IOException {

        String newUser = replaceEmailPlaceholder("src/test/resources/com.solvd.gorest/json/post/CreateUserRequest.json", randomMail);

        Response response = postRequest(newUser, true);

        response
                .then()
                .statusCode(HttpStatus.USER_MAIL_ALREADY_EXISTS);
    }

    @Test(dependsOnMethods = "createUserTest", priority = 4)
    public void deleteUserTest() {
        deleteRequest();
    }

    @Test(priority = 5)
    public void modifyUserTest() throws Exception {

        randomMail = "NEW" + randomMail; // Modify the random mail
        String newUser = replaceEmailPlaceholder("src/test/resources/com.solvd.gorest/json/patch/modifyUserReq.json", randomMail);

        Response response = postRequest(newUser, true);

        String jsonResponse = response
                .then()
                .statusCode(HttpStatus.USER_CREATED)
                .extract()
                .asString();

        User user = convertJsonToUser(jsonResponse);

        Response patchResponse = patchRequest(user);

        patchResponse
                .then()
                .statusCode(HttpStatus.USER_OK);


        String expectedResponse = replaceResponsePlaceholders("src/test/resources/com.solvd.gorest/json/patch/modifyUserRes.json", String.valueOf(user.getId()), randomMail);
        Assert.assertEquals(convertJsonToUser(expectedResponse), convertJsonToUser(patchResponse.then().extract().asString()), "The response is not the expected");

    }

    @Test(dependsOnMethods = "createUserTest", priority = 4)
    public void createPostTest() throws Exception {

        String request = replaceNewPostReqPlaceholders("src/test/resources/com.solvd.gorest/json/post/createNewPostReq.json", String.valueOf(createdUserId));

        Response response = postCreationRequest(request);

        response.then().statusCode(HttpStatus.USER_CREATED);

        String generatedId = response.jsonPath().getString("id");
        createdPostId = Integer.parseInt(generatedId);
        String updatedTemplate = replaceNewPostResPlaceholders("src/test/resources/com.solvd.gorest/json/post/createNewPostRes.json", String.valueOf(createdUserId), generatedId);
        String preparedResponse = response.getBody().asString();

        Assert.assertEquals(convertJsonToPost(preparedResponse), convertJsonToPost(updatedTemplate), "The response is not the expected");

    }

    @Test(dependsOnMethods = "createPostTest", priority = 5)
    public void commentPostTest() throws Exception {

        String request = replacePlaceholders("src/test/resources/com.solvd.gorest/json/post/createNewCommentReq.json", "post_id", String.valueOf(createdPostId));

        Response response = commentCreationRequest(request);
        response.then().statusCode(HttpStatus.USER_CREATED);

        String generatedId = response.jsonPath().getString("id");
        String updatedTemplate = replacePlaceholders("src/test/resources/com.solvd.gorest/json/post/createNewCommentRes.json", "id", generatedId, "post_id", String.valueOf(validPostId));
        String preparedResponse = response.getBody().asString();

        Assert.assertEquals(convertJsonToComment(preparedResponse), convertJsonToComment(updatedTemplate), "The response is not the expected");

    }

    @Test(priority = 4)
    public void getAllPostsTest() {
        Response response = given().get("https://gorest.co.in/public/v2/posts/");
        response.then().statusCode(HttpStatus.USER_OK);

        validPostId = response.jsonPath().getInt("[0].id");
    }

}
