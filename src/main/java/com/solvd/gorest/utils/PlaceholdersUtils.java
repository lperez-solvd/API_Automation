package com.solvd.gorest.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class PlaceholdersUtils {

    public String replaceEmailPlaceholder(String filePath, String randomEmail) throws IOException, TemplateException {
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

    public String replaceResponsePlaceholders(String filePath, String generatedId, String randomEmail) throws IOException, TemplateException {
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

    public String replaceNewPostReqPlaceholders(String filePath, String userId) throws IOException, TemplateException {

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

    public String replaceNewPostResPlaceholders(String filePath, String userId, String postId) throws IOException, TemplateException {

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

    public String replacePlaceholders(String filePath, String placeholder1Name, String placeholder1Value) throws IOException, TemplateException {

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

    public String replacePlaceholders(String filePath, String placeholder1Name, String placeholder1Value, String placeholder2Name, String placeholder2Value) throws IOException, TemplateException {

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

}
