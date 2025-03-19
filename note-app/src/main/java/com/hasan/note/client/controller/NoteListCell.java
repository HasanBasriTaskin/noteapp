package com.hasan.note.client.controller;

import com.hasan.note.model.Note;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import java.time.format.DateTimeFormatter;

/**
 * Custom ListCell for displaying notes in the ListView
 */
public class NoteListCell extends ListCell<Note> {

    private VBox container;
    private Label titleLabel;
    private Label previewLabel;
    private Label dateLabel;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructor to initialize the UI components
     */
    public NoteListCell() {
        super();

        // Create the UI components
        titleLabel = new Label();
        titleLabel.getStyleClass().add("note-title");

        previewLabel = new Label();
        previewLabel.getStyleClass().add("note-preview");

        dateLabel = new Label();
        dateLabel.getStyleClass().add("note-date");

        // Create layout containers
        VBox contentBox = new VBox(titleLabel, previewLabel);
        contentBox.setSpacing(2);
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        HBox dateBox = new HBox(dateLabel);

        container = new VBox(contentBox, dateBox);
        container.setSpacing(5);
        container.setPadding(new Insets(5, 10, 5, 10));
    }

    @Override
    protected void updateItem(Note note, boolean empty) {
        super.updateItem(note, empty);

        if (empty || note == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Set the title
            titleLabel.setText(note.getTitle());

            // Set the preview text (first 50 characters of content)
            String content = note.getContent();
            String preview = content.length() > 50 ? content.substring(0, 47) + "..." : content;
            previewLabel.setText(preview);

            // Set the date
            dateLabel.setText(note.getUpdatedAt().format(formatter));

            // Set the graphic to our layout
            setGraphic(container);
        }
    }
}