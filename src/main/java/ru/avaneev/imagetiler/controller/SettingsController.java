package ru.avaneev.imagetiler.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import lombok.extern.slf4j.Slf4j;
import ru.avaneev.imagetiler.model.Options;
import ru.avaneev.imagetiler.model.OptionsHolder;
import ru.avaneev.imagetiler.component.ViewRouter;
import ru.avaneev.imagetiler.component.generator.MapHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrey Vaneev
 * Creation date: 26.08.2018
 */
@Slf4j
public class SettingsController {

    @FXML
    private Label filenameLabel;
    @FXML
    public Label imageSizeLabel;
    @FXML
    public JFXTextField tileSizeInput;
    @FXML
    public JFXTextField zoomLevelInput;
    @FXML
    public JFXTextField outputDirectoryInput;
    @FXML
    public JFXButton browseOutputDirectoryButton;
    @FXML
    public JFXCheckBox generatePreviewCheckbox;
    @FXML
    public JFXTextField previewWidthInput;
    @FXML
    public JFXTextField previewHeightInput;
    @FXML
    public JFXButton generateButton;

    private Validation validation = new Validation();

    @FXML
    public void initialize() {
        Platform.runLater(() -> generateButton.requestFocus());

        Options options = OptionsHolder.getOptions();
        filenameLabel.setText(options.getSourceFileName());
        imageSizeLabel.setText(String.format("%dx%d", options.getSourceFile().getWidth(), options.getSourceFile().getHeight()));
        tileSizeInput.setText(String.valueOf(options.getTileSize()));
        zoomLevelInput.setText(String.valueOf(options.getZoomLevel()));
        previewWidthInput.setText(String.valueOf(options.getPreviewWidth()));
        previewHeightInput.setText(String.valueOf(options.getPreviewHeight()));
        outputDirectoryInput.setText(options.getOutputDir());

        generateButton.disableProperty().bind(validation.hasErrorsProperty());

        tileSizeInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                validation.setFieldValid("tileSizeInput", tileSizeInput.validate());
            }
        });
        tileSizeInput.textProperty().addListener(event -> {
            boolean valid = tileSizeInput.validate();
            validation.setFieldValid("tileSizeInput", valid);
            if (valid) {
                int tileSize = Integer.parseInt(tileSizeInput.getText());
                zoomLevelInput.setText(String.valueOf(MapHelper.optimalZoomLevel(tileSize, options.getImageSize())));
            }
        });

        zoomLevelInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                validation.setFieldValid("zoomLevelInput", zoomLevelInput.validate());
            }
        });
        zoomLevelInput.textProperty().addListener((o, oldVal, newVal) -> validation.setFieldValid("zoomLevelInput", zoomLevelInput.validate()));

        previewWidthInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                validation.setFieldValid("previewWidthInput", previewWidthInput.validate());
            }
        });
        previewHeightInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                validation.setFieldValid("previewHeightInput", previewHeightInput.validate());
            }
        });

        outputDirectoryInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                validation.setFieldValid("outputDirectoryInput", outputDirectoryInput.validate());
            }
        });

        generatePreviewCheckbox.selectedProperty().addListener((o, oldVal, newVal) -> previewWidthInput.getParent().setVisible(newVal));
    }

    @FXML
    private void generateTiles() {
        Options options = OptionsHolder.getOptions();
        options.setTileSize(Integer.parseInt(tileSizeInput.getText()));
        options.setZoomLevel(Integer.parseInt(zoomLevelInput.getText()));
        options.setOutputDir(outputDirectoryInput.getText());
        options.setGeneratePreview(generatePreviewCheckbox.isSelected());
        options.setPreviewHeight(Integer.parseInt(previewWidthInput.getText()));
        options.setPreviewWidth(Integer.parseInt(previewWidthInput.getText()));

        ViewRouter.getInstance().navigateTo(ViewRouter.View.TILES_GENERATION_VIEW);
    }

    @FXML
    private void browseOutputDirectory() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File directory = directoryChooser.showDialog(browseOutputDirectoryButton.getScene().getWindow());
        if (directory != null) {
            outputDirectoryInput.setText(directory.getAbsolutePath());
        }
    }

    private class Validation {
        private Map<String, Boolean> fieldValidation = new HashMap<>();
        private BooleanProperty hasErrors = new SimpleBooleanProperty();

        void setFieldValid(String fieldName, boolean valid) {
            this.fieldValidation.put(fieldName, valid);
            checkHasErrors();
        }

        BooleanProperty hasErrorsProperty() {
            return hasErrors;
        }

        private void checkHasErrors() {
            hasErrors.set(this.fieldValidation.values().stream().anyMatch(v -> !v));
        }
    }
}
