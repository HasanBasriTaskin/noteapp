package com.hasan.note.client.controller;

import com.hasan.note.database.NoteDAO;
import com.hasan.note.database.TagDAO;
import com.hasan.note.model.Note;
import com.hasan.note.model.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for the main view of the Note App.
 */
public class MainViewController {

    private static final Logger logger = LoggerFactory.getLogger(MainViewController.class);

    @FXML
    private ListView<Note> noteListView;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea contentArea;

    @FXML
    private TextField tagsField;

    @FXML
    private TextField searchField;

    @FXML
    private Label statusLabel;

    // Observable list to store notes
    private ObservableList<Note> notes = FXCollections.observableArrayList();

    // Current selected note
    private Note currentNote;

    // DAOs for database operations
    private final NoteDAO noteDAO = new NoteDAO();
    private final TagDAO tagDAO = new TagDAO();

    // Current user ID (temporary, should be replaced with actual user authentication)
    private final int currentUserId = 1;

    /**
     * Initialize method, called after the FXML has been loaded
     */
    @FXML
    public void initialize() {
        logger.info("Initializing MainViewController");

        // Set up the list view
        noteListView.setItems(notes);
        noteListView.setCellFactory(param -> new NoteListCell());

        // Add a listener for when a note is selected
        noteListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayNote(newValue);
            }
        });

        // Add a listener for search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchNotes(newValue);
        });

        // Load notes from database
        loadNotes();

        statusLabel.setText("Status: Ready");
    }

    /**
     * Loads notes from the database
     */
    private void loadNotes() {
        notes.clear();
        List<Note> userNotes = noteDAO.getNotesByUserId(currentUserId);
        notes.addAll(userNotes);

        if (!notes.isEmpty()) {
            noteListView.getSelectionModel().select(0);
        } else {
            clearEditor();
            statusLabel.setText("Status: No notes found");
        }
    }

    /**
     * Displays a note in the editor
     *
     * @param note the note to display
     */
    private void displayNote(Note note) {
        currentNote = note;
        titleField.setText(note.getTitle());
        contentArea.setText(note.getContent());

        // Set tags
        String tagsString = note.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.joining(", "));
        tagsField.setText(tagsString);
    }

    /**
     * Clears the editor
     */
    private void clearEditor() {
        currentNote = null;
        titleField.clear();
        contentArea.clear();
        tagsField.clear();
    }

    /**
     * Searches for notes containing the given text
     *
     * @param searchText the text to search for
     */
    private void searchNotes(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadNotes();
            return;
        }

        notes.clear();
        List<Note> searchResults = noteDAO.searchNotes(currentUserId, searchText);
        notes.addAll(searchResults);

        if (!notes.isEmpty()) {
            noteListView.getSelectionModel().select(0);
            statusLabel.setText("Status: Found " + notes.size() + " notes matching '" + searchText + "'");
        } else {
            clearEditor();
            statusLabel.setText("Status: No notes found for '" + searchText + "'");
        }
    }

    /**
     * Handler for the new note button
     */
    @FXML
    private void handleNewNote() {
        logger.info("Creating new note");

        // Clear the selection
        noteListView.getSelectionModel().clearSelection();

        // Clear the editor
        clearEditor();

        // Set focus to the title field
        titleField.requestFocus();

        statusLabel.setText("Status: Creating new note");
    }

    /**
     * Handler for the save note button
     */
    @FXML
    private void handleSaveNote() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Title", "Please enter a title for your note.");
            titleField.requestFocus();
            return;
        }

        // Process tags
        Set<Tag> tags = processTagsFromField();

        if (currentNote == null) {
            // Create a new note
            Note newNote = new Note(currentUserId, title, content);
            newNote.setTags(tags);

            Note savedNote = noteDAO.saveNote(newNote);
            if (savedNote != null) {
                notes.add(0, savedNote);
                noteListView.getSelectionModel().select(savedNote);
                statusLabel.setText("Status: Note created successfully");
                logger.info("New note created: {}", savedNote.getTitle());
            } else {
                statusLabel.setText("Status: Failed to create note");
                logger.error("Failed to create new note");
                showAlert(Alert.AlertType.ERROR, "Save Error", "Failed to save the note. Please try again.");
            }
        } else {
            // Update existing note
            currentNote.setTitle(title);
            currentNote.setContent(content);
            currentNote.setTags(tags);

            boolean updated = noteDAO.updateNote(currentNote);
            if (updated) {
                // Refresh the list to update the display
                noteListView.refresh();
                statusLabel.setText("Status: Note updated successfully");
                logger.info("Note updated: {}", currentNote.getTitle());
            } else {
                statusLabel.setText("Status: Failed to update note");
                logger.error("Failed to update note: {}", currentNote.getTitle());
                showAlert(Alert.AlertType.ERROR, "Update Error", "Failed to update the note. Please try again.");
            }
        }
    }

    /**
     * Handler for the delete note button
     */
    @FXML
    private void handleDeleteNote() {
        if (currentNote != null) {
            boolean deleted = noteDAO.deleteNote(currentNote.getId());
            if (deleted) {
                notes.remove(currentNote);
                clearEditor();
                statusLabel.setText("Status: Note deleted successfully");
                logger.info("Note deleted: {}", currentNote.getTitle());
            } else {
                statusLabel.setText("Status: Failed to delete note");
                logger.error("Failed to delete note: {}", currentNote.getTitle());
                showAlert(Alert.AlertType.ERROR, "Delete Error", "Failed to delete the note. Please try again.");
            }
        }
    }

    /**
     * Processes tags from the tags field
     *
     * @return a set of Tag objects
     */
    private Set<Tag> processTagsFromField() {
        Set<Tag> tags = new HashSet<>();
        String tagsText = tagsField.getText().trim();

        if (!tagsText.isEmpty()) {
            String[] tagNames = tagsText.split(",");
            for (String tagName : tagNames) {
                String trimmedName = tagName.trim();
                if (!trimmedName.isEmpty()) {
                    Tag tag = new Tag(trimmedName, currentUserId);
                    tags.add(tag);
                }
            }
        }

        return tags;
    }

    /**
     * Shows an alert dialog
     *
     * @param type the type of alert
     * @param title the title of the alert
     * @param message the message to display
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}