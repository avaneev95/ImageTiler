package ru.avaneev.imagetiler.controller;

import com.jfoenix.controls.JFXButton;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import ru.avaneev.imagetiler.model.Options;
import ru.avaneev.imagetiler.model.OptionsHolder;
import ru.avaneev.imagetiler.component.ViewRouter;
import ru.avaneev.imagetiler.component.generator.MapHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Slf4j
public class LoadImageController {

    @FXML
    public VBox fileDropBox;
    @FXML
    public Label errorLabel;
    @FXML
    public VBox fileLoadingBox;
    @FXML
    public AnchorPane fileDropZone;
    @FXML
    private JFXButton browseFilesButton;

    private final List<String> extensions = Arrays.asList(".jpeg", ".jpg", ".png", ".gif");

    @FXML
    public void initialize() {
    }

    @FXML
    private void chooseFile() {
        final FileChooser fileChooser = new FileChooser();
        String[] extensionMasks = extensions.stream().map(e -> "*" + e).toArray(String[]::new);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", extensionMasks));
        fileChooser.setTitle("Choose image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(browseFilesButton.getScene().getWindow());
        if (file != null) {
            setImage(file);
        }
    }

    @FXML
    private void onFileOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            File file = db.getFiles().get(0);
            String path = file.toPath().toString();
            if (extensions.stream().anyMatch(path::endsWith)) {
                event.acceptTransferModes(TransferMode.COPY);
            }
        }
        event.consume();
    }

    @FXML
    private void onFileDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            setImage(db.getFiles().get(0));
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private void setImage(File file) {
        Task<BufferedImage> task = new BufferedImageTask(file);
        fileDropBox.setVisible(false);
        fileLoadingBox.setVisible(true);
        ForkJoinPool.commonPool().submit(task);
    }

    private class BufferedImageTask extends Task<BufferedImage> {
        private final File file;

        BufferedImageTask(File file) {
            this.file = file;
        }

        @Override
        public BufferedImage call() throws Exception {
            return ImageIO.read(file);
        }

        @Override
        protected void succeeded() {
            Options options = OptionsHolder.getOptions();
            options.setSourceFile(this.getValue());
            options.setSourceFileName(file.getName());
            options.setImageSize(options.getSourceFile().getWidth());
            options.setZoomLevel(MapHelper.optimalZoomLevel(options.getTileSize(), options.getImageSize()));

            ViewRouter.getInstance().navigateTo(ViewRouter.View.SETTINGS_VIEW);
        }

        @Override
        protected void failed() {
            log.error("Failed to read image.", this.getException());
            this.showError();
        }

        private void showError() {
            fileLoadingBox.setVisible(false);
            fileDropBox.setVisible(true);
            errorLabel.setText(String.format("Can't read file \"%s\"! Try another one!", file.getName()));
            errorLabel.getParent().setVisible(true);
        }
    }
}
