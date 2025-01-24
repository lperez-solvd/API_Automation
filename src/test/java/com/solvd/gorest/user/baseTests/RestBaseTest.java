package com.solvd.gorest.user.baseTests;

import com.solvd.gorest.User;
import com.solvd.gorest.utils.HttpStatus;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static com.solvd.gorest.utils.Mappers.createRandomEmail;
import static io.restassured.RestAssured.given;

public abstract class RestBaseTest {

    // Load environment variables from the .env file
    Dotenv dotenv = Dotenv.load();
    String accessToken = dotenv.get("ACCESS_TOKEN");

    protected String randomMail = createRandomEmail();
    protected int createdUserId;
    protected User firstUserFromResponse;
    protected int createdPostId;
    protected int validPostId;


    protected Response getRequest(int userId) {
        Response response = given().get("https://gorest.co.in/public/v2/users/" + userId);
        response.then().statusCode(HttpStatus.USER_OK);
        return response;
    }

    protected Response getRequest() {
        Response response = given().get("https://gorest.co.in/public/v2/users");
        response.then().statusCode(HttpStatus.USER_OK);
        return response;

    }

    protected Response postRequest(String body, boolean withCredentials) {
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


    protected void deleteRequest(int userId) {

        Response response = given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .delete("https://gorest.co.in/public/v2/users/" + createdUserId);

        response.then().statusCode(HttpStatus.USER_DELETED);

    }

    protected Response patchRequest(User user) {

        return given()
                .auth()
                .oauth2(accessToken)
                .body(user)
                .contentType("application/json")
                .patch("https://gorest.co.in/public/v2/users/" + user.getId());
    }

    protected Response postCreationRequest(String request) {
        return given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .post("https://gorest.co.in/public/v2/users/" + createdUserId + "/posts");

    }

    protected Response commentCreationRequest(String request) {
        return given()
                .auth()
                .oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .post("https://gorest.co.in/public/v2/posts/" + validPostId + "/comments");
    }


}
