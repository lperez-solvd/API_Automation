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







    // generic methods


    public static String convertJsonFileToString(String filePath) throws IOException {
        File jsonFile = new File(filePath);
        return objectMapper.writeValueAsString(objectMapper.readTree(jsonFile)); // Convert the file to JSON string
    }


    // Helpers

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

