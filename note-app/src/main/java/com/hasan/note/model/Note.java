package com.hasan.note.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a note entity in the application.
 * This class serves as both a domain model and a data transfer object.
 */
public class Note {
  private Integer id;
  private Integer userId;
  private String title;
  private String content;
  private boolean isPinned;
  private boolean isArchived;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Set<Tag> tags = new HashSet<>();

  /**
   * Default constructor
   */
  public Note() {
  }

  /**
   * Constructor with essential fields
   *
   * @param userId the ID of the user who owns this note
   * @param title the title of the note
   * @param content the content of the note
   */
  public Note(Integer userId, String title, String content) {
    this.userId = userId;
    this.title = title;
    this.content = content;
    this.isPinned = false;
    this.isArchived = false;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = this.createdAt;
  }

  /**
   * Full constructor with all fields
   *
   * @param id the ID of the note
   * @param userId the ID of the user who owns this note
   * @param title the title of the note
   * @param content the content of the note
   * @param isPinned whether the note is pinned
   * @param isArchived whether the note is archived
   * @param createdAt when the note was created
   * @param updatedAt when the note was last updated
   */
  public Note(Integer id, Integer userId, String title, String content, boolean isPinned,
              boolean isArchived, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.content = content;
    this.isPinned = isPinned;
    this.isArchived = isArchived;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // Getters and Setters

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
    this.updatedAt = LocalDateTime.now();
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
    this.updatedAt = LocalDateTime.now();
  }

  public boolean isPinned() {
    return isPinned;
  }

  public void setPinned(boolean isPinned) {
    this.isPinned = isPinned;
    this.updatedAt = LocalDateTime.now();
  }

  public boolean isArchived() {
    return isArchived;
  }

  public void setArchived(boolean isArchived) {
    this.isArchived = isArchived;
    this.updatedAt = LocalDateTime.now();
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  /**
   * Add a tag to this note
   *
   * @param tag the tag to add
   * @return true if the tag was added, false if it was already present
   */
  public boolean addTag(Tag tag) {
    boolean result = this.tags.add(tag);
    if (result) {
      this.updatedAt = LocalDateTime.now();
    }
    return result;
  }

  /**
   * Remove a tag from this note
   *
   * @param tag the tag to remove
   * @return true if the tag was removed, false if it wasn't present
   */
  public boolean removeTag(Tag tag) {
    boolean result = this.tags.remove(tag);
    if (result) {
      this.updatedAt = LocalDateTime.now();
    }
    return result;
  }

  @Override
  public String toString() {
    return "Note{" +
            "id=" + id +
            ", userId=" + userId +
            ", title='" + title + '\'' +
            ", isPinned=" + isPinned +
            ", isArchived=" + isArchived +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            ", tagsCount=" + tags.size() +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Note note = (Note) o;

    return id != null ? id.equals(note.id) : note.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}