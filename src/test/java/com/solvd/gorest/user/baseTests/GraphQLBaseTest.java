package com.solvd.gorest.user.baseTests;

import com.solvd.gorest.utils.HttpStatus;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static com.solvd.gorest.utils.Mappers.createRandomEmail;
import static io.restassured.RestAssured.given;

public abstract class GraphQLBaseTest {

    Dotenv dotenv = Dotenv.load();
    String accessToken = dotenv.get("ACCESS_TOKEN");

    protected int firstUserId;
    protected int createdUserId;

    protected String randomMail = createRandomEmail();

    protected Response sendRequest(String query) {

        Response response =
                given()
                        .auth()
                        .oauth2(accessToken)
                        .contentType(ContentType.JSON)
                        .body(query)
                        .post("https://gorest.co.in/public/v2/graphql");

        response.then().statusCode(HttpStatus.GRAPHQL_OK);
        return response;

    }

}
