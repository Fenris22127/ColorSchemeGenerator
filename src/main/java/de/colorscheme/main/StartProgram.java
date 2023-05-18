package de.colorscheme.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * The class which starts the application and loads the fxml file.
 *
 * @author &copy; 2023 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.1
 * @since 18.0.1
 */
public class StartProgram extends Application {

    /**
     * The stage of the application.
     */
    private static Stage stage;

    /**
     * Gets the stage of the application.
     * @return A {@link Stage}: The stage of the application
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * The main method which launches the application.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws IOException If the fxml file cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("app.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        stage.setTitle("Color Scheme Generator");
        stage.setScene(scene);
        stage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResourceAsStream("logoFX.png"))));

        stage.show();
    }
}
