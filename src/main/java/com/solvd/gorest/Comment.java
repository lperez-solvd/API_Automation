package com.solvd.gorest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Comment {

    private int id;
    private int postId;
    private String name;
    private String email;
    private String body;

    public Comment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("post_id")
    public int getPostId() {
        return postId;
    }

    @JsonProperty("post_id")
    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Check if the same object
        if (obj == null || getClass() != obj.getClass()) return false; // Check null or different class
        Comment comment = (Comment) obj; // Cast to Comment
        return id == comment.id && postId == comment.postId && Objects.equals(email, comment.email);
    }

    // Override hashCode() to ensure consistency with equals()
    @Override
    public int hashCode() {
        return Objects.hash(id, postId, email); // Hash based on 'id', 'postId', and 'email'
    }

}
