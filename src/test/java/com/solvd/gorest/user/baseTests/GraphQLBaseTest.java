package com.solvd.gorest.user.baseTests;

import com.solvd.gorest.user.service.GraphQLService;
import com.solvd.gorest.user.utils.CustomAssertions;

import static com.solvd.gorest.utils.Mappers.createRandomEmail;

public abstract class GraphQLBaseTest {

    protected int firstUserId;
    protected int createdUserId;

    protected String randomMail = createRandomEmail();

    protected GraphQLService graphQLService = new GraphQLService();
    protected CustomAssertions customAssertions = new CustomAssertions();


}
