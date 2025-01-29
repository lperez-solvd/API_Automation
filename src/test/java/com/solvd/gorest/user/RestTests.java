package com.solvd.gorest.user;

import com.solvd.gorest.User;
import com.solvd.gorest.user.baseTests.RestBaseTest;
import freemarker.template.TemplateException;
import io.restassured.response.Response;
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


        customAssertions.assertStatusOk(restService.getStatus(response));
        customAssertions.assertEquals(users.size(), numberOfResultsPerPage);

    }

    @Test(dependsOnMethods = "getAllUsersByPage")
    public void getUserById() throws Exception {

        int idToSearchFor = firstUserFromResponse.getId();

        Response response = restService.getRequest(idToSearchFor);

        customAssertions.assertStatusOk(restService.getStatus(response));
        customAssertions.assertUsers(restService.convertResponseToUser(response), firstUserFromResponse);

    }

    @Test
    public void createUserWithNoCredentialsTest() throws TemplateException, IOException {

        String newUser = restService.preparePostRequest(randomMail);

        Response response = restService.postRequest(newUser, false);

        customAssertions.assertStatusNoCredentials(restService.getStatus(response));

    }

    @Test()
    public void createUserTest() throws Exception {

        String newUser = restService.preparePostRequest(randomMail);

        Response response = restService.postRequest(newUser, true);

        createdUserId = restService.getCreatedUserId(response);

        String updatedTemplate = restService.preparePostResponse(createdUserId, randomMail);

        customAssertions.assertStatusUserCreated(restService.getStatus(response));
        customAssertions.assertUsers(restService.convertResponseToUser(response), restService.convertJsonToUser(updatedTemplate));

    }

    @Test(dependsOnMethods = "createUserTest")
    public void createUserWithExistingEmail() throws TemplateException, IOException {

        String newUser = restService.preparePostRequest(randomMail);

        Response response = restService.postRequest(newUser, true);

        customAssertions.assertStatusUserAlreadyExists(restService.getStatus(response));

    }

    @Test(dependsOnMethods = "createUserTest", priority = 4)
    public void deleteUserTest() {
        Response response = restService.deleteRequest(createdUserId);

        customAssertions.assertStatusUserDeleted(restService.getStatus(response));

    }

    @Test(priority = 5)
    public void modifyUserTest() throws Exception {

        randomMail = "NEW" + randomMail; // Modify the random mail

        String newUser = restService.preparePatchRequest(randomMail);

        Response response = restService.postRequest(newUser, true);


        customAssertions.assertStatusUserCreated(restService.getStatus(response));


        User user = restService.convertResponseToUser(response);

        Response patchResponse = restService.patchRequest(user);

        String expectedResponse = restService.preparePatchResponse(user.getId(), randomMail);


        customAssertions.assertStatusOk(restService.getStatus(patchResponse));
        customAssertions.assertUsers(restService.convertJsonToUser(expectedResponse), restService.convertResponseToUser(patchResponse));

    }

    @Test(dependsOnMethods = "createUserTest", priority = 4)
    public void createPostTest() throws Exception {

        String request = restService.prepareCreatePostRequest(createdUserId);

        Response response = restService.postCreationRequest(request, createdUserId);

        customAssertions.assertStatusUserCreated(restService.getStatus(response));

        createdPostId = restService.getCreatedPostId(response);

        String updatedTemplate = restService.prepareCreatePostResponse(createdUserId, createdPostId);
        customAssertions.assertPosts(restService.convertResponseToPost(response), restService.convertJsonToPost(updatedTemplate));

    }

    @Test(dependsOnMethods = "createPostTest", priority = 5)
    public void commentPostTest() throws Exception {

        String request = restService.prepareCreateCommentRequest(validPostId);
        Response response = restService.commentCreationRequest(request, validPostId);

        customAssertions.assertStatusUserCreated(restService.getStatus(response));

        validCommentId = restService.getCreatedUserId(response);

        String updatedTemplate = restService.prepareCreateCommentResponse(validCommentId, validPostId);


        customAssertions.assertComment(restService.convertResponseToComment(response), restService.convertJsonToComment(updatedTemplate));
    }

    @Test(priority = 4)
    public void getAllPostsTest() {
        Response response = restService.getAllPosts();

        validPostId = restService.getFirstPostId(response);

        customAssertions.assertStatusOk(restService.getStatus(response));

    }

}
