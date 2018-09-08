package ru.avaneev.imagetiler.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import ru.avaneev.imagetiler.model.OptionsHolder;

import java.io.File;

/**
 * @author Andrey Vaneev
 * Creation date: 26.08.2018
 */
@Slf4j
public class PreviewController {

    @FXML
    public WebView browser;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) browser.getScene().getWindow();
            stage.setWidth(OptionsHolder.getOptions().getPreviewWidth());
            stage.setHeight(OptionsHolder.getOptions().getPreviewHeight() + 30);
        });
        browser.setFontSmoothingType(FontSmoothingType.GRAY);
        browser.getEngine().load(new File(OptionsHolder.getOptions().getOutputDir() + "/map/leaflet.html").toURI().toString());
    }
}
