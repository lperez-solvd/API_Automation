package com.solvd.gorest.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solvd.gorest.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mappers {


    private static final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper for JSON processing

    public static User convertJsonToUser(String jsonResponse) throws Exception {

        return objectMapper.readValue(jsonResponse, User.class); // Deserialize JSON to User (including the id)
    }

    public static List<User> convertJsonToUsers(String jsonResponse) throws Exception {
        // Deserialize JSON to List<User> (including the id)
        return objectMapper.readValue(jsonResponse, new TypeReference<List<User>>() {
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

}

