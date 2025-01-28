package com.solvd.gorest.user;

import com.solvd.gorest.User;
import com.solvd.gorest.user.baseTests.GraphQLBaseTest;
import com.solvd.gorest.utils.HttpStatus;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;


public class GraphQLTests extends GraphQLBaseTest {


    @Test(priority = 1)
    public void getAllUsersByPageTest() {

        Response response = graphQLService.getUsers();

        List<User> users = graphQLService.getUsersFromResponse(response);
        firstUserId = users.getFirst().getId();

        Assert.assertEquals(users.size(), 10, "The number of users is not the expected");

    }

    @Test(dependsOnMethods = "getAllUsersByPageTest", priority = 2)
    public void getUserByIdTest() {


        Response response = graphQLService.getUserById(firstUserId);
        User user = graphQLService.getUserFromResponse(response);

        Assert.assertEquals(graphQLService.getStatus(response), HttpStatus.GRAPHQL_OK, "The response is not the expected");
        Assert.assertEquals(firstUserId, user.getId(), "The response user id is not the expected");

    }

    @Test(priority = 2)
    public void createUserTest() throws Exception {

        User requestUser = graphQLService.getUserForRequest(randomMail);
        Response response = graphQLService.createUser(requestUser);

        response.then().statusCode(HttpStatus.GRAPHQL_OK);

        User reponseUser = graphQLService.getCreatedUserFromResponse(response);

        requestUser.setId(reponseUser.getId());
        createdUserId = reponseUser.getId();

        Assert.assertEquals(graphQLService.getStatus(response), HttpStatus.GRAPHQL_OK, "The response is not the expected");
        Assert.assertEquals(requestUser, reponseUser, "The response is not the expected");

    }

    @Test(dependsOnMethods = "createUserTest", priority = 3)
    public void deleteUser() {

        Response response = graphQLService.deleteUser(createdUserId);

        Assert.assertEquals(response.jsonPath().getInt("data.deleteUser.user.id"), createdUserId, "The deleted user id is not the expected");
    }

    @Test(dependsOnMethods = "createUserTest")
    public void updateUserUser() throws Exception {

        User requestUser = graphQLService.getUserForRequestPatch(randomMail);

        Response response = graphQLService.updateUser(createdUserId, requestUser);

        User responseUser = graphQLService.getCreatedUserFromResponse(response);



        Assert.assertEquals(graphQLService.getStatus(response), HttpStatus.GRAPHQL_OK, "The response is not the expected");
        Assert.assertEquals(responseUser, requestUser, "The request and response doesn't match");

    }

}
