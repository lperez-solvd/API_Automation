package com.solvd.gorest.user;

import com.solvd.gorest.User;
import com.solvd.gorest.user.baseTests.GraphQLBaseTest;
import com.solvd.gorest.utils.HttpStatus;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static com.solvd.gorest.utils.Mappers.*;


public class GraphQLTests extends GraphQLBaseTest {


    @Test(priority = 1)
    public void getAllUsersByPageTest() {

        String query = "{ \"query\": \"query Users { users { nodes { email gender id name status } } }\" }";

        Response response = sendRequest(query);

        List<User> users = response.body().jsonPath().getList("data.users.nodes", User.class);
        firstUserId = users.getFirst().getId();

        Assert.assertEquals(users.size(), 10, "The number of users is not the expected");

    }

    @Test(dependsOnMethods = "getAllUsersByPageTest", priority = 2)
    public void getUserByIdTest() {

        String query = String.format("{ \"query\": \"{ user(id: %s) { id name email } }\" }", firstUserId);

        Response response = sendRequest(query);

        response.then().statusCode(HttpStatus.GRAPHQL_OK);

        User user = response.body().jsonPath().getObject("data.user", User.class);

        Assert.assertEquals(firstUserId, user.getId(), "The response user id is not the expected");

    }

    @Test(priority = 2)
    public void createUserTest() throws Exception {

        String jsonRequest = replaceEmailPlaceholder("src/test/resources/com.solvd.gorest/json/post/CreateUserRequest.json", randomMail);
        User user = convertJsonToUser(jsonRequest);

        String query = String.format("{ \"query\": \"mutation CreateUser { createUser(input: { name: \\\"%s\\\" email: \\\"%s\\\" gender: \\\"%s\\\"  status: \\\"%s\\\"  clientMutationId: \\\"123123123\\\" }) { clientMutationId user { email name status id gender } } }\" }", user.getName(), user.getEmail(), user.getGender(), user.getStatus());

        Response response = sendRequest(query);

        response.then().statusCode(HttpStatus.GRAPHQL_OK);

        User userResponse = response.body().jsonPath().getObject("data.createUser.user", User.class);
        user.setId(userResponse.getId());
        createdUserId = userResponse.getId();

        Assert.assertEquals(user, userResponse, "The response is not the expected");

    }

    @Test(dependsOnMethods = "createUserTest", priority = 3)
    public void deleteUser() {

        String query = String.format("{ \"query\": \"mutation DeleteUser { deleteUser(input: { id: %s }) { user {id} } }\" }", createdUserId);

        Response response = sendRequest(query);

        response.then().statusCode(HttpStatus.GRAPHQL_OK);

        Assert.assertEquals(response.jsonPath().getInt("data.deleteUser.user.id"), createdUserId, "The deleted user id is not the expected");
    }

    @Test(dependsOnMethods = "createUserTest")
    public void updateUserUser() throws Exception {

        String jsonRequest = replaceEmailPlaceholder("src/test/resources/com.solvd.gorest/json/patch/modifyUserReq.json", randomMail);
        User userRequest = convertJsonToUser(jsonRequest);

        String query = String.format("{ \"query\": \"mutation UpdateUser { updateUser(input: { id: %d name: \\\"%s\\\" email: \\\"%s\\\" gender: \\\"%s\\\" status: \\\"%s\\\" clientMutationId: \\\"123123123\\\" }) { clientMutationId user { email name status id gender } } }\" }",
                createdUserId, userRequest.getName(), userRequest.getEmail(), userRequest.getGender(), userRequest.getStatus());

        Response response = sendRequest(query);

        response.then().statusCode(HttpStatus.GRAPHQL_OK);

        String jsonResponse = replaceEmailPlaceholder("src/test/resources/com.solvd.gorest/json/patch/modifyUserReq.json", randomMail);
        User userResponse = convertJsonToUser(jsonResponse);

        Assert.assertEquals(userResponse, userRequest, "The request and response doesn't match");

    }

}
