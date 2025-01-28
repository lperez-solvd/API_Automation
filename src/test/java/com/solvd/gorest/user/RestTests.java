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



public class RestTests extends RestBaseTest {


    @Test(description = "Will get all the users but only page 1")
    public void getAllUsersByPage() throws Exception {

        Response response = restService.getRequest();

        int numberOfResultsPerPage = Integer.parseInt(response.getHeader("X-Pagination-Limit"));

        List<User> users = restService.convertResponseToUsers(response);
        firstUserFromResponse = users.getFirst();

        Assert.assertEquals(restService.getStatus(response), HttpStatus.USER_OK, "The status is not the expected");
        Assert.assertEquals(users.size(), numberOfResultsPerPage, "The number of users is not the expected");
    }

    @Test(dependsOnMethods = "getAllUsersByPage")
    public void getUserById() throws Exception {

        int idToSearchFor = firstUserFromResponse.getId();

        Response response = restService.getRequest(idToSearchFor);

        Assert.assertEquals(restService.getStatus(response), HttpStatus.USER_OK, "The status is not the expected");
        Assert.assertEquals(restService.convertResponseToUser(response), firstUserFromResponse, "The response is not the expected");

    }

    @Test
    public void createUserWithNoCredentialsTest() throws TemplateException, IOException {

        String newUser = restService.preparePostRequest(randomMail);

        Response response = restService.postRequest(newUser, false);

        Assert.assertEquals(restService.getStatus(response), HttpStatus.USER_NO_AUTH_TOKEN, "The status is not the expected");

    }

    @Test()
    public void createUserTest() throws Exception {

        String newUser = restService.preparePostRequest(randomMail);

        Response response = restService.postRequest(newUser, true);

        createdUserId = restService.getCreatedUserId(response);

        String updatedTemplate = restService.preparePostResponse(createdUserId, randomMail);

        Assert.assertEquals(restService.getStatus(response), HttpStatus.USER_CREATED, "The status is not the expected");
        Assert.assertEquals(restService.convertResponseToUser(response), restService.convertJsonToUser(updatedTemplate), "The response is not the expected");

    }

    @Test(dependsOnMethods = "createUserTest")
    public void createUserWithExistingEmail() throws TemplateException, IOException {

        String newUser = restService.preparePostRequest(randomMail);

        Response response = restService.postRequest(newUser, true);

        Assert.assertEquals(restService.getStatus(response), HttpStatus.USER_MAIL_ALREADY_EXISTS, "The status is not the expected");

    }

    @Test(dependsOnMethods = "createUserTest", priority = 4)
    public void deleteUserTest() {
        Response response = restService.deleteRequest(createdUserId);

        Assert.assertEquals(restService.getStatus(response), HttpStatus.USER_DELETED, "The status is not the expected");

    }

    @Test(priority = 5)
    public void modifyUserTest() throws Exception {

        randomMail = "NEW" + randomMail; // Modify the random mail

        String newUser = restService.preparePatchRequest(randomMail);

        Response response = restService.postRequest(newUser, true);

        Assert.assertEquals(restService.getStatus(response), HttpStatus.USER_CREATED, "The status is not the expected");


        User user = restService.convertResponseToUser(response);

        Response patchResponse = restService.patchRequest(user);

        Assert.assertEquals(restService.getStatus(patchResponse), HttpStatus.USER_OK, "The status is not the expected");


        String expectedResponse = restService.preparePatchResponse(user.getId(), randomMail);

        Assert.assertEquals(restService.convertJsonToUser(expectedResponse), restService.convertResponseToUser(patchResponse), "The response is not the expected");

    }

    @Test(dependsOnMethods = "createUserTest", priority = 4)
    public void createPostTest() throws Exception {

        String request = restService.prepareCreatePostRequest(createdUserId);

        Response response = restService.postCreationRequest(request, createdUserId);

        Assert.assertEquals(restService.getStatus(response), HttpStatus.USER_CREATED, "The status is not the expected");

        createdPostId = restService.getCreatedPostId(response);

        String updatedTemplate = restService.prepareCreatePostResponse(createdUserId, createdPostId);

        Assert.assertEquals(restService.convertResponseToPost(response), restService.convertJsonToPost(updatedTemplate), "The response is not the expected");

    }

    @Test(dependsOnMethods = "createPostTest", priority = 5)
    public void commentPostTest() throws Exception {

        String request = restService.prepareCreateCommentRequest(createdPostId);

        Response response = restService.commentCreationRequest(request, createdPostId);

        Assert.assertEquals(restService.getStatus(response), HttpStatus.USER_CREATED, "The status is not the expected");

        validCommentId = restService.getCreatedUserId(response);

        String updatedTemplate = restService.prepareCreateCommentResponse(validCommentId, validPostId);

        Assert.assertEquals(restService.convertResponseToComment(response), restService.convertJsonToComment(updatedTemplate), "The response is not the expected");

    }

    @Test(priority = 4)
    public void getAllPostsTest() {
        Response response = restService.getAllPosts();

        validPostId = restService.getFirstPostId(response);

        Assert.assertEquals(restService.getStatus(response), HttpStatus.USER_OK, "The status is not the expected");

    }

}
