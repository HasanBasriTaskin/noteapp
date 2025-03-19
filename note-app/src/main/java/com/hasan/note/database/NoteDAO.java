package com.hasan.note.database;

import com.hasan.note.model.Note;
import com.hasan.note.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Access Object for Note entities.
 * Handles database operations for notes.
 */
public class NoteDAO {
    private static final Logger logger = LoggerFactory.getLogger(NoteDAO.class);

    /**
     * Saves a new note to the database
     * 
     * @param note The note to save
     * @return The saved note with ID populated
     */
    public Note saveNote(Note note) {
        String sql = "INSERT INTO notes (user_id, title, content, is_pinned, is_archived, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Set note fields
            stmt.setInt(1, note.getUserId());
            stmt.setString(2, note.getTitle());
            stmt.setString(3, note.getContent());
            stmt.setBoolean(4, note.isPinned());
            stmt.setBoolean(5, note.isArchived());
            stmt.setTimestamp(6, Timestamp.valueOf(note.getCreatedAt()));
            stmt.setTimestamp(7, Timestamp.valueOf(note.getUpdatedAt()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        // Set the generated ID to the note
                        note.setId(rs.getInt(1));
                        
                        // Save tags if any
                        saveTags(note, conn);
                        
                        logger.info("Note saved with ID: {}", note.getId());
                        return note;
                    }
                }
            }
            
            logger.error("Failed to save note: {}", note.getTitle());
            return null;
            
        } catch (SQLException e) {
            logger.error("Error saving note", e);
            return null;
        }
    }

    /**
     * Updates an existing note in the database
     * 
     * @param note The note to update
     * @return True if the update was successful
     */
    public boolean updateNote(Note note) {
        String sql = "UPDATE notes SET title = ?, content = ?, is_pinned = ?, is_archived = ?, updated_at = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Begin transaction
            conn.setAutoCommit(false);
            
            // Set note fields
            stmt.setString(1, note.getTitle());
            stmt.setString(2, note.getContent());
            stmt.setBoolean(3, note.isPinned());
            stmt.setBoolean(4, note.isArchived());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(6, note.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update tags
                // First remove existing tag associations
                deleteNoteTags(note.getId(), conn);
                
                // Then save new tags
                saveTags(note, conn);
                
                // Commit transaction
                conn.commit();
                
                logger.info("Note updated with ID: {}", note.getId());
                return true;
            }
            
            // Rollback if no rows affected
            conn.rollback();
            logger.error("Failed to update note with ID: {}", note.getId());
            return false;
            
        } catch (SQLException e) {
            logger.error("Error updating note", e);
            return false;
        }
    }

    /**
     * Deletes a note from the database
     * 
     * @param noteId The ID of the note to delete
     * @return True if the deletion was successful
     */
    public boolean deleteNote(int noteId) {
        String sql = "DELETE FROM notes WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Begin transaction
            conn.setAutoCommit(false);
            
            // First delete note-tag associations
            deleteNoteTags(noteId, conn);
            
            // Then delete the note
            stmt.setInt(1, noteId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Commit transaction
                conn.commit();
                
                logger.info("Note deleted with ID: {}", noteId);
                return true;
            }
            
            // Rollback if no rows affected
            conn.rollback();
            logger.error("Failed to delete note with ID: {}", noteId);
            return false;
            
        } catch (SQLException e) {
            logger.error("Error deleting note", e);
            return false;
        }
    }

    /**
     * Retrieves a note by its ID
     * 
     * @param noteId The ID of the note to retrieve
     * @return The note if found, null otherwise
     */
    public Note getNoteById(int noteId) {
        String sql = "SELECT * FROM notes WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, noteId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Note note = mapResultSetToNote(rs);
                    
                    // Load tags for this note
                    loadNoteTags(note, conn);
                    
                    return note;
                }
            }
            
            logger.warn("Note not found with ID: {}", noteId);
            return null;
            
        } catch (SQLException e) {
            logger.error("Error getting note by ID", e);
            return null;
        }
    }

    /**
     * Retrieves all notes for a user
     * 
     * @param userId The ID of the user
     * @return A list of notes belonging to the user
     */
    public List<Note> getNotesByUserId(int userId) {
        String sql = "SELECT * FROM notes WHERE user_id = ? ORDER BY updated_at DESC";
        List<Note> notes = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Note note = mapResultSetToNote(rs);
                    notes.add(note);
                }
            }
            
            // Load tags for all notes
            for (Note note : notes) {
                loadNoteTags(note, conn);
            }
            
            logger.info("Retrieved {} notes for user ID: {}", notes.size(), userId);
            return notes;
            
        } catch (SQLException e) {
            logger.error("Error getting notes by user ID", e);
            return notes;
        }
    }

    /**
     * Searches for notes containing the given text in title or content
     * 
     * @param userId The ID of the user
     * @param searchText The text to search for
     * @return A list of matching notes
     */
    public List<Note> searchNotes(int userId, String searchText) {
        String sql = "SELECT * FROM notes WHERE user_id = ? AND (title LIKE ? OR content LIKE ?) ORDER BY updated_at DESC";
        List<Note> notes = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchText + "%";
            stmt.setInt(1, userId);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Note note = mapResultSetToNote(rs);
                    notes.add(note);
                }
            }
            
            // Load tags for all notes
            for (Note note : notes) {
                loadNoteTags(note, conn);
            }
            
            logger.info("Found {} notes matching search: {}", notes.size(), searchText);
            return notes;
            
        } catch (SQLException e) {
            logger.error("Error searching notes", e);
            return notes;
        }
    }

    /**
     * Maps a database result set to a Note object
     */
    private Note mapResultSetToNote(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        String title = rs.getString("title");
        String content = rs.getString("content");
        boolean isPinned = rs.getBoolean("is_pinned");
        boolean isArchived = rs.getBoolean("is_archived");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
        
        return new Note(id, userId, title, content, isPinned, isArchived, createdAt, updatedAt);
    }

    /**
     * Saves tags for a note
     */
    private void saveTags(Note note, Connection conn) throws SQLException {
        if (note.getTags() == null || note.getTags().isEmpty()) {
            return;
        }
        
        // SQL for inserting note-tag associations
        String insertNoteTagSql = "INSERT INTO note_tags (note_id, tag_id) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(insertNoteTagSql)) {
            for (Tag tag : note.getTags()) {
                // Ensure tag is saved first
                TagDAO tagDAO = new TagDAO();
                Tag savedTag = tagDAO.saveTag(tag);
                
                if (savedTag != null) {
                    stmt.setInt(1, note.getId());
                    stmt.setInt(2, savedTag.getId());
                    stmt.addBatch();
                }
            }
            
            stmt.executeBatch();
        }
    }

    /**
     * Deletes all tag associations for a note
     */
    private void deleteNoteTags(int noteId, Connection conn) throws SQLException {
        String sql = "DELETE FROM note_tags WHERE note_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, noteId);
            stmt.executeUpdate();
        }
    }

    /**
     * Loads tags for a note
     */
    private void loadNoteTags(Note note, Connection conn) throws SQLException {
        String sql = "SELECT t.* FROM tags t " +
                    "JOIN note_tags nt ON t.id = nt.tag_id " +
                    "WHERE nt.note_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, note.getId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                Set<Tag> tags = new HashSet<>();
                
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int userId = rs.getInt("user_id");
                    String color = rs.getString("color");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    
                    Tag tag = new Tag(id, name, userId, color, createdAt);
                    tags.add(tag);
                }
                
                note.setTags(tags);
            }
        }
    }
}
