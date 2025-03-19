package com.hasan.note.database;

import com.hasan.note.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Tag entities.
 * Handles database operations for tags.
 */
public class TagDAO {
    private static final Logger logger = LoggerFactory.getLogger(TagDAO.class);

    /**
     * Saves a tag to the database. If the tag already exists for the user, returns the existing tag.
     * 
     * @param tag The tag to save
     * @return The saved tag with ID populated
     */
    public Tag saveTag(Tag tag) {
        // First, check if the tag already exists for this user
        Tag existingTag = getTagByNameAndUserId(tag.getName(), tag.getUserId());
        if (existingTag != null) {
            return existingTag;
        }

        String sql = "INSERT INTO tags (name, user_id, color, created_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, tag.getName());
            stmt.setInt(2, tag.getUserId());
            stmt.setString(3, tag.getColor());
            stmt.setTimestamp(4, Timestamp.valueOf(tag.getCreatedAt() != null ? tag.getCreatedAt() : LocalDateTime.now()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        tag.setId(rs.getInt(1));
                        logger.info("Tag saved with ID: {}", tag.getId());
                        return tag;
                    }
                }
            }
            
            logger.error("Failed to save tag: {}", tag.getName());
            return null;
            
        } catch (SQLException e) {
            logger.error("Error saving tag", e);
            return null;
        }
    }

    /**
     * Updates an existing tag in the database
     * 
     * @param tag The tag to update
     * @return True if the update was successful
     */
    public boolean updateTag(Tag tag) {
        String sql = "UPDATE tags SET name = ?, color = ? WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tag.getName());
            stmt.setString(2, tag.getColor());
            stmt.setInt(3, tag.getId());
            stmt.setInt(4, tag.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Tag updated with ID: {}", tag.getId());
                return true;
            }
            
            logger.error("Failed to update tag with ID: {}", tag.getId());
            return false;
            
        } catch (SQLException e) {
            logger.error("Error updating tag", e);
            return false;
        }
    }

    /**
     * Deletes a tag from the database
     * 
     * @param tagId The ID of the tag to delete
     * @param userId The ID of the user who owns the tag
     * @return True if the deletion was successful
     */
    public boolean deleteTag(int tagId, int userId) {
        String sql = "DELETE FROM tags WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Begin transaction
            conn.setAutoCommit(false);
            
            // First delete tag from note_tags junction table
            String deleteNoteTagsSql = "DELETE FROM note_tags WHERE tag_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteNoteTagsSql)) {
                stmt.setInt(1, tagId);
                stmt.executeUpdate();
            }
            
            // Then delete the tag
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, tagId);
                stmt.setInt(2, userId);
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Commit transaction
                    conn.commit();
                    
                    logger.info("Tag deleted with ID: {}", tagId);
                    return true;
                }
                
                // Rollback if no rows affected
                conn.rollback();
                logger.error("Failed to delete tag with ID: {}", tagId);
                return false;
            }
            
        } catch (SQLException e) {
            logger.error("Error deleting tag", e);
            return false;
        }
    }

    /**
     * Retrieves a tag by its ID and user ID
     * 
     * @param tagId The ID of the tag
     * @param userId The ID of the user who owns the tag
     * @return The tag if found, null otherwise
     */
    public Tag getTagById(int tagId, int userId) {
        String sql = "SELECT * FROM tags WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tagId);
            stmt.setInt(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTag(rs);
                }
            }
            
            logger.warn("Tag not found with ID: {}", tagId);
            return null;
            
        } catch (SQLException e) {
            logger.error("Error getting tag by ID", e);
            return null;
        }
    }

    /**
     * Retrieves a tag by its name and user ID
     * 
     * @param name The name of the tag
     * @param userId The ID of the user who owns the tag
     * @return The tag if found, null otherwise
     */
    public Tag getTagByNameAndUserId(String name, int userId) {
        String sql = "SELECT * FROM tags WHERE name = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setInt(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTag(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            logger.error("Error getting tag by name and user ID", e);
            return null;
        }
    }

    /**
     * Retrieves all tags for a user
     * 
     * @param userId The ID of the user
     * @return A list of tags belonging to the user
     */
    public List<Tag> getTagsByUserId(int userId) {
        String sql = "SELECT * FROM tags WHERE user_id = ? ORDER BY name";
        List<Tag> tags = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(mapResultSetToTag(rs));
                }
            }
            
            logger.info("Retrieved {} tags for user ID: {}", tags.size(), userId);
            return tags;
            
        } catch (SQLException e) {
            logger.error("Error getting tags by user ID", e);
            return tags;
        }
    }

    /**
     * Maps a database result set to a Tag object
     */
    private Tag mapResultSetToTag(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int userId = rs.getInt("user_id");
        String color = rs.getString("color");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        
        return new Tag(id, name, userId, color, createdAt);
    }
}
