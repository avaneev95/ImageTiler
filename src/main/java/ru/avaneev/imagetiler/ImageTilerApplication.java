package ru.avaneev.imagetiler;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.avaneev.imagetiler.component.ViewRouter;

public class ImageTilerApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.setProperty("prism.lcdtext", "false");

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 640, 480);

        scene.getStylesheets().addAll(
                ImageTilerApplication.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                ImageTilerApplication.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                ImageTilerApplication.class.getResource("/css/styles.css").toExternalForm()
        );

        primaryStage.setTitle("Image Tiler");
        primaryStage.getIcons().add(new Image(ImageTilerApplication.class.getResourceAsStream("/logo.png")));
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        ViewRouter.getInstance().init(primaryStage, ViewRouter.View.LOAD_IMAGE_VIEW);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
