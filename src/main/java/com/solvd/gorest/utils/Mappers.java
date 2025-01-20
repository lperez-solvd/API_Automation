package com.solvd.gorest.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solvd.gorest.User;

import java.util.List;

public class Mappers {


    private static final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper for JSON processing

    public static User convertJsonToUser(String jsonResponse) throws Exception {
        return objectMapper.readValue(jsonResponse, User.class); // Deserialize JSON to User (including the id)
    }

    public static List<User> convertJsonToUsers(String jsonResponse) throws Exception {
        // Deserialize JSON to List<User> (including the id)
        return objectMapper.readValue(jsonResponse, new TypeReference<List<User>>(){});
    }

    public static String convertUserToJson(User user) throws Exception {
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

