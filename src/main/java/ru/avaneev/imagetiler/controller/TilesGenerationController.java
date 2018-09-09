package ru.avaneev.imagetiler.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXProgressBar;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import ru.avaneev.imagetiler.component.ViewRouter;
import ru.avaneev.imagetiler.component.generator.PreviewGenerator;
import ru.avaneev.imagetiler.component.generator.TileGenerator;
import ru.avaneev.imagetiler.model.OptionsHolder;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Andrey Vaneev
 * Creation date: 26.08.2018
 */
@Slf4j
public class TilesGenerationController {

    @FXML
    public JFXProgressBar progressBar;
    @FXML
    public Label percentageLabel;
    @FXML
    public Label tilesGeneratedLabel;
    @FXML
    public Label timeLabel;
    @FXML
    public JFXButton finishButton;

    @FXML
    public void initialize() {
        finishButton.setDisable(true);
        generateTiles();
    }

    private void generateTiles() {
        Instant startTime = Instant.now();
        TileGenerator.newInstance(OptionsHolder.getOptions())
                .onProgressUpdate(event -> updateLabels(startTime, event))
                .runAsync()
                .thenRun(() -> {
                    OptionsHolder.disposeSourceFile();
                    finishButton.setDisable(false);
                    if (OptionsHolder.getOptions().isGeneratePreview()) {
                        new PreviewGenerator(OptionsHolder.getOptions()).generate();
                    }
                })
                .exceptionally(t -> {
                    onError(t);
                    return null;
                });
    }

    private void onError(Throwable t) {
        log.error("Failed to generate tiles", t);
        Platform.runLater(() -> {
            finishButton.setDisable(false);
            finishButton.setOnAction(e -> Platform.exit());
            progressBar.getStyleClass().add("error");
            progressBar.setProgress(100.0);

            JFXDialog alert = new JFXDialog();
            alert.setOverlayClose(false);
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.getStyleClass().add("alert-dialog-error");
            Label label = new Label("Process Failed!");
            label.setPadding(new Insets(0, 0, 0, 40));
            layout.setHeading(new MaterialDesignIconView(MaterialDesignIcon.ALERT_OCTAGON, "32"), label);
            layout.setBody(new VBox(new Label("Failed to generate tiles for image."), new Label("Cause: " + t.getMessage())));
            JFXButton closeButton = new JFXButton("CLOSE");
            closeButton.setOnAction(event -> Platform.exit());
            layout.setActions(closeButton);
            alert.setContent(layout);
            alert.show((StackPane) finishButton.getScene().getRoot());
        });
    }

    @FXML
    private void onFinish() {
        if (OptionsHolder.getOptions().isGeneratePreview()) {
            ViewRouter.getInstance().navigateTo(ViewRouter.View.PREVIEW_VIEW);
        } else {
            Platform.exit();
        }
    }

    private void updateLabels(Instant startTime, TileGenerator.ProgressEvent event) {
        Platform.runLater(() -> {
            progressBar.setProgress(event.getProgress());
            percentageLabel.setText(((int) event.getPercents()) + "%");
            tilesGeneratedLabel.setText(((int) event.getWorkDone()) + " / " + ((int) event.getTotalWork()));
            Duration elapsed = Duration.between(startTime, Instant.now());
            timeLabel.setText(formatDuration(elapsed) + " / " + eta(elapsed, event));
        });
    }

    private String eta(Duration elapsed, TileGenerator.ProgressEvent event) {
        if (event.getWorkDone() == 0) {
            return "?";
        } else {
            return formatDuration(elapsed
                    .dividedBy((long) event.getWorkDone())
                    .multipliedBy((long) (event.getTotalWork() - event.getWorkDone())));
        }
    }

    private String formatDuration(Duration d) {
        long s = d.getSeconds();
        return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60);
    }
}
