package com.solvd.gorest.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solvd.gorest.Comment;
import com.solvd.gorest.Post;
import com.solvd.gorest.User;

import java.util.List;

public class JsonMappers {

    private static final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper for JSON processing

    public JsonMappers() {
    }

    public User convertJsonToUser(String jsonResponse) throws Exception {

        return objectMapper.readValue(jsonResponse, User.class);
    }

    public Post convertJsonToPost(String jsonResponse) throws Exception {
        return objectMapper.readValue(jsonResponse, Post.class);
    }

    public Comment convertJsonToComment(String jsonResponse) throws Exception {
        return objectMapper.readValue(jsonResponse, Comment.class);
    }

    public List<User> convertJsonToUsers(String jsonResponse) throws Exception {

        return objectMapper.readValue(jsonResponse, new TypeReference<List<User>>() {
        });
    }

    public List<Post> convertJsonToPosts(String jsonResponse) throws Exception {
        return objectMapper.readValue(jsonResponse, new TypeReference<List<Post>>() {
        });
    }

    public String convertUserToJson(User user) throws Exception {
        // Create a copy of the user to avoid modifying the original user object
        User userCopy = new User();
        userCopy.setName(user.getName());
        userCopy.setEmail(user.getEmail());
        userCopy.setGender(user.getGender());
        userCopy.setStatus(user.getStatus());

        // Serialize the user (excluding the id field)
        return objectMapper.writeValueAsString(userCopy); // Serialize User to JSON (without id)
    }

}
