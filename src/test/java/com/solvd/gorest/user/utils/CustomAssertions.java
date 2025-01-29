package com.solvd.gorest.user.utils;

import com.solvd.gorest.Comment;
import com.solvd.gorest.Post;
import com.solvd.gorest.User;
import com.solvd.gorest.utils.HttpStatus;
import org.testng.Assert;

public class CustomAssertions {

    public void assertUsers(User actual, User expected) {

    }

    public void assertPosts(Post actual, Post expected) {
        Assert.assertEquals(actual, expected, "The actual and expected Post are not equal");
    }

    public void assertComment(Comment actual, Comment expected) {
        Assert.assertEquals(actual, expected, "The actual and expected Comments are not equal");
    }

    public void assertEquals(Object actual, Object expected) {
        Assert.assertEquals(actual, expected, "The two elements are not the same. The assertion fails.");
    }

    public void assertStatusOk(int status) {
        assertStatus(status, HttpStatus.USER_OK);
    }

    public void assertStatusNoCredentials(int status) {
        assertStatus(status, HttpStatus.USER_NO_AUTH_TOKEN);
    }

    public void assertStatusUserCreated(int status) {
        assertStatus(status, HttpStatus.USER_CREATED);
    }

    public void assertStatusUserAlreadyExists(int status) {
        assertStatus(status, HttpStatus.USER_MAIL_ALREADY_EXISTS);
    }

    public void assertStatusUserDeleted(int status) {
        assertStatus(status, HttpStatus.USER_DELETED);
    }

    private void assertStatus(int actual, int expected) {
        Assert.assertEquals(actual, expected, "The status is not the expected, expected" + expected + "but get " + actual);

    }


}
