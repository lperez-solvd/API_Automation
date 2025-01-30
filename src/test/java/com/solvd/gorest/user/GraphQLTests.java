package com.solvd.gorest.user;

import com.solvd.gorest.User;
import com.solvd.gorest.user.baseTests.GraphQLBaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;


public class GraphQLTests extends GraphQLBaseTest {


    @Test(priority = 1)
    public void getAllUsersByPageTest() {

        Response response = graphQLService.getUsers();

        List<User> users = graphQLService.getUsersFromResponse(response);
        firstUserId = users.getFirst().getId();

        customAssertions.assertEquals(users.size(), 10);

    }

    @Test(dependsOnMethods = "getAllUsersByPageTest", priority = 2)
    public void getUserByIdTest() {


        Response response = graphQLService.getUserById(firstUserId);
        User user = graphQLService.getUserFromResponse(response);

        customAssertions.assertStatusOk(graphQLService.getStatus(response));
        customAssertions.assertEquals(firstUserId, user.getId());

    }

    @Test(priority = 2)
    public void createUserTest() throws Exception {

        User requestUser = graphQLService.getUserForRequest(randomMail);
        Response response = graphQLService.createUser(requestUser);

        User reponseUser = graphQLService.getCreatedUserFromResponse(response);

        requestUser.setId(reponseUser.getId());
        createdUserId = reponseUser.getId();

        customAssertions.assertStatusOk(graphQLService.getStatus(response));
        customAssertions.assertUsers(requestUser, reponseUser);

    }

    @Test(dependsOnMethods = "createUserTest", priority = 3)
    public void deleteUser() {

        Response response = graphQLService.deleteUser(createdUserId);

        customAssertions.assertStatusOk(graphQLService.getStatus(response));
        customAssertions.assertEquals(graphQLService.getDeletedUserId(response), createdUserId);
    }

    @Test(dependsOnMethods = "createUserTest")
    public void updateUserUser() throws Exception {

        User requestUser = graphQLService.getUserForRequestPatch(randomMail);

        Response response = graphQLService.updateUser(createdUserId, requestUser);

        User responseUser = graphQLService.getUpdatedUserFromResponse(response);

        requestUser.setId(responseUser.getId());


        customAssertions.assertStatusOk(graphQLService.getStatus(response));
        customAssertions.assertUsers(responseUser, requestUser);

    }

}
