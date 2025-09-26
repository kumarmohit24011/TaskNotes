package com.tasknotesapp.models;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private String id;
    private String title;
    private String content;
    private String category;
    private Date createdDate;
    private Date modifiedDate;
    private String userId;

    public Note() {
        // Default constructor required for Firestore
    }

    public Note(String title, String content, String category, String userId) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.userId = userId;
        this.createdDate = new Date();
        this.modifiedDate = new Date();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}