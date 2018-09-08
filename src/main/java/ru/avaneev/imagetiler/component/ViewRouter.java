package ru.avaneev.imagetiler.component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

/**
 * @author Andrey Vaneev
 * Creation date: 05.09.2018
 */
@Slf4j
public class ViewRouter {
    private static final ViewRouter router = new ViewRouter();

    private Stage primaryStage;
    private Object currentController;

    private ViewRouter() {
    }

    public static ViewRouter getInstance() {
        return router;
    }

    public Object getCurrentController() {
        return currentController;
    }

    public void init(Stage stage, View startScene) {
        this.primaryStage = stage;
        this.navigateTo(startScene);
    }

    public void navigateTo(View scene) {
        if (primaryStage != null) {
            log.info("Navigating to: {}", scene.getViewName());
            Parent pane = this.loadFXML(scene.getFilename());
            this.primaryStage.getScene().setRoot(pane);
        }
    }

    private Parent loadFXML(String fxml) {
        try {
            URL resource = ViewRouter.class.getResource(fxml);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent load = loader.load();
            this.currentController = loader.getController();
            return load;
        } catch(Exception e) {
            log.error("Error loading fxml file", e);
        }
        return null;
    }

    public enum View {
        LOAD_IMAGE_VIEW("LoadImageView"),
        SETTINGS_VIEW("SettingsView"),
        TILES_GENERATION_VIEW("TilesGenerationView"),
        PREVIEW_VIEW("PreviewView");

        private String viewName;

        View(String viewName) {
            this.viewName = viewName;
        }

        public String getViewName() {
            return viewName;
        }

        public String getFilename() {
            return "/views/" + viewName + ".fxml";
        }
    }
}
