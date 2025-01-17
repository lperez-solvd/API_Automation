package com.solvd.gorest.user;

import io.github.cdimascio.dotenv.Dotenv;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;



public class userTests {


    @Test
    public void getUserByNumber() {
     given()
             .get("https://gorest.co.in/public/v2/users/7001840")
             .then()
             .statusCode(200)
             .body("id", equalTo(7001840))
             .body("name", equalTo("Mrs. Arun Pilla"))
             .body("email", equalTo("pilla_mrs_arun@crooks.test"))
             .body("gender", equalTo("male"))
             .body("status", equalTo("inactive"))
             .log().all();
    }

    public void createUserTest () {
        given()
                .auth()
                .oauth2()
    }
}
