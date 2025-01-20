package com.solvd.gorest.user;

import com.solvd.gorest.User;
import com.solvd.gorest.utils.HttpStatus;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

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

    @Test
    public void getAllUsers() {
        String jsonResponse = given()
                .get("https://gorest.co.in/public/v2/users")
                .then()
                .statusCode(HttpStatus.USER_OK)
                .extract()
                .asString();

        List<User> users = null;
        try {
            users = convertJsonToUsers(jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(users.size(), 10, "The number of users is not the expected");
    }

    @Test
    public void getUserByNumber() {
        given()
                .get("https://gorest.co.in/public/v2/users/7646357")
                .then()
                .statusCode(HttpStatus.USER_OK)
                .body("id", equalTo(7646357))
                .body("name", equalTo("Dwaipayana Achari"))
                .body("email", equalTo("achari_dwaipayana@flatley-luettgen.test"))
                .body("gender", equalTo("male"))
                .body("status", equalTo("active"));
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

    @Test(dependsOnMethods = "createUserWithNoCredentialsTest")
    public void createUserTest() {

        User newUser = new User("Mr. Perez", randomMail, "male", "inactive");
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
                .statusCode(HttpStatus.USER_CREATED)
                .body("id", Matchers.any(Integer.class))
                .body("name", equalTo("Mr. Perez"))
                .body("email", equalTo(randomMail));

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

    @Test
    public void modifyUserTests() {

        User newUser = new User("Mr. Perez2", randomMail, "male", "inactive");
        String userToJson;
        try {
            userToJson = convertUserToJson(newUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String response = given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(userToJson)
                .post("https://gorest.co.in/public/v2/users/")
                .then()
                .statusCode(HttpStatus.USER_CREATED)
                .extract()
                .asString();

        User user = null;
        try {
            user = convertJsonToUser(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert user != null;
        given()
                .auth()
                .oauth2(accessToken)
                .body(user) // Send the updated User object as the body
                .contentType("application/json") // Set content type to JSON
                .patch("https://gorest.co.in/public/v2/users/" + user.getId()) // Send PUT request with user ID
                .then()
                .statusCode(HttpStatus.USER_OK);

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
