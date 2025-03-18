package com.hasan.note.model;

import java.time.LocalDateTime;

/**
 * Represents a tag entity in the application.
 * Tags can be associated with notes for categorization.
 */
public class Tag {
    private Integer id;
    private String name;
    private Integer userId;
    private String color;
    private LocalDateTime createdAt;

    /**
     * Default constructor
     */
    public Tag() {
    }

    /**
     * Constructor with essential fields
     * 
     * @param name the name of the tag
     * @param userId the ID of the user who owns this tag
     */
    public Tag(String name, Integer userId) {
        this.name = name;
        this.userId = userId;
        this.color = "#607D8B"; // Default color
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructor with essential fields including color
     * 
     * @param name the name of the tag
     * @param userId the ID of the user who owns this tag
     * @param color the color of the tag in hexadecimal format (e.g., "#607D8B")
     */
    public Tag(String name, Integer userId, String color) {
        this.name = name;
        this.userId = userId;
        this.color = color;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Full constructor with all fields
     * 
     * @param id the ID of the tag
     * @param name the name of the tag
     * @param userId the ID of the user who owns this tag
     * @param color the color of the tag in hexadecimal format
     * @param createdAt when the tag was created
     */
    public Tag(Integer id, String name, Integer userId, String color, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.color = color;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", color='" + color + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (id != null ? !id.equals(tag.id) : tag.id != null) return false;
        if (name != null ? !name.equals(tag.name) : tag.name != null) return false;
        return userId != null ? userId.equals(tag.userId) : tag.userId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
