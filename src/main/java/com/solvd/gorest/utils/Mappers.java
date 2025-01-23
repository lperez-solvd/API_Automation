package com.solvd.gorest.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solvd.gorest.Comment;
import com.solvd.gorest.Post;
import com.solvd.gorest.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Mappers {


    private static final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper for JSON processing

    public static User convertJsonToUser(String jsonResponse) throws Exception {

        return objectMapper.readValue(jsonResponse, User.class);
    }

    public static Post convertJsonToPost(String jsonResponse) throws Exception {
        return objectMapper.readValue(jsonResponse, Post.class);
    }

    public static Comment convertJsonToComment(String jsonResponse) throws Exception {
        return objectMapper.readValue(jsonResponse, Comment.class);
    }

    public static List<User> convertJsonToUsers(String jsonResponse) throws Exception {

        return objectMapper.readValue(jsonResponse, new TypeReference<List<User>>() {
        });
    }

    public static List<Post> convertJsonToPosts(String jsonResponse) throws Exception {
        return objectMapper.readValue(jsonResponse, new TypeReference<List<Post>>() {
        });
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

    public static String replaceEmailPlaceholder(String filePath, String randomEmail) throws IOException, TemplateException {
        // Load the JSON template
        File jsonFile = new File(filePath);
        FileReader fileReader = new FileReader(jsonFile);

        // Set up Freemarker configuration
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDefaultEncoding("UTF-8");

        // Create a template object from the JSON file
        Template template = new Template("jsonTemplate", fileReader, cfg);

        // Prepare the context with the values to replace placeholders
        Map<String, Object> context = new HashMap<>();
        context.put("random_mail", randomEmail);  // Use the passed random email

        // Process the template and replace placeholders
        StringWriter writer = new StringWriter();
        template.process(context, writer);

        // Return the processed JSON string (ready for use in HTTP request body)
        return writer.toString();
    }

    public static String replaceResponsePlaceholders(String filePath, String generatedId, String randomEmail) throws IOException, TemplateException {
        // Load the JSON template
        File jsonFile = new File(filePath);
        FileReader fileReader = new FileReader(jsonFile);

        // Set up Freemarker configuration
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDefaultEncoding("UTF-8");

        // Create a template object from the JSON file
        Template template = new Template("jsonTemplate", fileReader, cfg);

        // Prepare the context with values to replace placeholders
        Map<String, Object> context = new HashMap<>();
        context.put("generated_id", generatedId); // Replace with actual generated ID
        context.put("random_mail", randomEmail);  // Replace with random email

        // Process the template and replace placeholders
        StringWriter writer = new StringWriter();
        template.process(context, writer);

        // Return the processed JSON string (ready for use in HTTP request body or further validation)
        return writer.toString();
    }

    public static String replaceNewPostReqPlaceholders(String filePath, String userId) throws IOException, TemplateException {

        File jsonFile = new File(filePath);
        FileReader fileReader = new FileReader(jsonFile);

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDefaultEncoding("UTF-8");

        Template template = new Template("jsonTemplate", fileReader, cfg);

        Map<String, Object> context = new HashMap<>();
        context.put("user_id", userId);

        StringWriter writer = new StringWriter();
        template.process(context, writer);

        return writer.toString();
    }

    public static String replaceNewPostResPlaceholders(String filePath, String userId, String postId) throws IOException, TemplateException {

        File jsonFile = new File(filePath);
        FileReader fileReader = new FileReader(jsonFile);

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDefaultEncoding("UTF-8");

        Template template = new Template("jsonTemplate", fileReader, cfg);

        Map<String, Object> context = new HashMap<>();
        context.put("user_id", userId);
        context.put("post_id", postId);

        StringWriter writer = new StringWriter();
        template.process(context, writer);

        return writer.toString();
    }

    // generic methods
    public static String replacePlaceholders(String filePath, String placeholder1Name, String placeholder1Value, String placeholder2Name, String placeholder2Value) throws IOException, TemplateException {

        File jsonFile = new File(filePath);
        FileReader fileReader = new FileReader(jsonFile);

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDefaultEncoding("UTF-8");

        Template template = new Template("jsonTemplate", fileReader, cfg);

        Map<String, Object> context = new HashMap<>();
        context.put(placeholder1Name, placeholder1Value);
        context.put(placeholder2Name, placeholder2Value);

        StringWriter writer = new StringWriter();
        template.process(context, writer);

        return writer.toString();
    }

    public static String replacePlaceholders(String filePath, String placeholder1Name, String placeholder1Value) throws IOException, TemplateException {

        File jsonFile = new File(filePath);
        FileReader fileReader = new FileReader(jsonFile);

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDefaultEncoding("UTF-8");

        Template template = new Template("jsonTemplate", fileReader, cfg);

        Map<String, Object> context = new HashMap<>();
        context.put(placeholder1Name, placeholder1Value);

        StringWriter writer = new StringWriter();
        template.process(context, writer);

        return writer.toString();
    }

    public static String convertJsonFileToString(String filePath) throws IOException {
        File jsonFile = new File(filePath);
        return objectMapper.writeValueAsString(objectMapper.readTree(jsonFile)); // Convert the file to JSON string
    }

    // GraphQL


    // Helpers

    // Helper
    public static String createRandomEmail() {
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

