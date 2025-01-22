package com.solvd.gorest.user;

import com.solvd.gorest.User;
import com.solvd.gorest.utils.HttpStatus;
import freemarker.template.TemplateException;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.solvd.gorest.utils.Mappers.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class UserTests {

    // Load environment variables from the .env file
    Dotenv dotenv = Dotenv.load();

    // Retrieve the "ACCESS_TOKEN" environment variable
    String accessToken = dotenv.get("ACCESS_TOKEN");

    String randomMail = createRandomEmail();
    int createdUserId;
    User firstUserFromResponse; // getUserById method needs to update the user that search for every time suite runs

    @Test(description = "Will get all the users but only page 1")
    public void getAllUsersByPage() throws Exception {
        Response response = given().get("https://gorest.co.in/public/v2/users");
        int numberOfResultsPerPage = Integer.parseInt(response.getHeader("X-Pagination-Limit"));
        response.then().statusCode(HttpStatus.USER_OK);

        List<User> users = null;
        users = convertJsonToUsers(response.then().extract().asString());
        firstUserFromResponse = users.getFirst();
        Assert.assertEquals(users.size(), numberOfResultsPerPage, "The number of users is not the expected");
    }

    @Test(dependsOnMethods = "getAllUsersByPage")
    public void getUserById() throws Exception {
        int idToSearchFor = firstUserFromResponse.getId();

        Response response =
                given()
                        .get("https://gorest.co.in/public/v2/users/" + idToSearchFor);

        response.then().statusCode(HttpStatus.USER_OK);

        String preparedResponse = response.getBody().asString();

        Assert.assertEquals(convertJsonToUser(preparedResponse), firstUserFromResponse, "The response is not the expected");

    }

    @Test
    public void createUserWithNoCredentialsTest() {
        User newUser = new User("Mr. Perez", randomMail, "male", "inactive");
        String userToJson;
        try {
            userToJson = convertUserToJson(newUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        given()
                .contentType(ContentType.JSON)
                .body(userToJson)
                .post("https://gorest.co.in/public/v2/users/")
                .then()
                .statusCode(HttpStatus.USER_NO_AUTH_TOKEN);

    }

    @Test()
    public void createUserTest() throws Exception {

        String newUser = replaceEmailPlaceholder("src/test/resources/com.solvd.gorest/json/post/CreateUserRequest.json", randomMail);

        Response response = given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(newUser)
                .post("https://gorest.co.in/public/v2/users/");

        response
                .then()
                .statusCode(HttpStatus.USER_CREATED);


        String generatedId = response.jsonPath().getString("id");
        String updatedTemplate = replaceResponsePlaceholders("src/test/resources/com.solvd.gorest/json/post/CreateUserResponse.json", generatedId, randomMail);
        String preparedResponse = response.getBody().asString();

        Assert.assertEquals(convertJsonToUser(preparedResponse), convertJsonToUser(updatedTemplate), "The response is not the expected");


    }

    @Test(dependsOnMethods = "createUserTest")
    public void createUserWithExistingEmail() {
        User newUser = new User("Mr. Lopez", randomMail, "female", "active");
        String userToJson;
        try {
            userToJson = convertUserToJson(newUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(userToJson)
                .post("https://gorest.co.in/public/v2/users/")
                .then()
                .statusCode(HttpStatus.USER_MAIL_ALREADY_EXISTS);
    }

    @Test(dependsOnMethods = "createUserTest")
    public void deleteUserTest() {
        given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .delete("https://gorest.co.in/public/v2/users/" + createdUserId)  // Use user ID in URL
                .then()
                .statusCode(204);
    }

    @Test
    public void modifyUserTest() throws Exception {

        randomMail = "NEW" + randomMail; // Modify the random mail
        String newUser = replaceEmailPlaceholder("src/test/resources/com.solvd.gorest/json/patch/CreateUserReq.json", randomMail);

        String response = given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(newUser)
                .post("https://gorest.co.in/public/v2/users/")
                .then()
                .statusCode(HttpStatus.USER_CREATED)
                .extract()
                .asString();

        User user = convertJsonToUser(response);

        Response patchResponse = given()
                .auth()
                .oauth2(accessToken)
                .body(user)
                .contentType("application/json")
                .patch("https://gorest.co.in/public/v2/users/" + user.getId());

        patchResponse
                .then()
                .statusCode(HttpStatus.USER_OK);


        String expectedResponse = replaceResponsePlaceholders("src/test/resources/com.solvd.gorest/json/patch/modifyUserRes.json", String.valueOf(user.getId()), randomMail);
        Assert.assertEquals(convertJsonToUser(expectedResponse), convertJsonToUser(patchResponse.then().extract().asString()), "The response is not the expected");

    }


    @Test
    public void createPostTest() {

    }

    @Test
    public void commentPostTest() {

    }


    // Helper
    private static String createRandomEmail() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        // Generate a random username with 10 characters
        StringBuilder username = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            username.append(characters.charAt(random.nextInt(characters.length())));
        }

        // Randomly select a domain
        String[] domains = {"gmail.com", "yahoo.com", "outlook.com", "hotmail.com"};
        String domain = domains[random.nextInt(domains.length)];

        return username.toString() + "@" + domain;
    }
}
