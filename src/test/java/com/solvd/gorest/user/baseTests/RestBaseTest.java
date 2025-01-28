package com.solvd.gorest.user.baseTests;

import com.solvd.gorest.User;
import com.solvd.gorest.user.service.RestService;
import com.solvd.gorest.utils.PlaceholdersUtils;

import static com.solvd.gorest.utils.Mappers.createRandomEmail;


public abstract class RestBaseTest {

    protected String randomMail = createRandomEmail();
    protected int createdUserId;
    protected User firstUserFromResponse;
    protected int createdPostId;
    protected int validPostId;
    protected int validCommentId;

    protected RestService restService = new RestService();

    protected PlaceholdersUtils placeholdersUtils = new PlaceholdersUtils();





}
