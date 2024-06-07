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
 * @author &copy; 2024 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.2
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
    public static synchronized Stage getStage() {
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
        setStage(primaryStage);
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("app.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 628, 712);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("newstyle.css")).toExternalForm());
        scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Quattrocento+Sans");
        scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Mulish");
        stage.setTitle("Color Scheme Generator");
        stage.setScene(scene);
        stage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResourceAsStream("logoFX.png"))));

        stage.show();
    }

    /**
     * Sets the stage of the application.
     * @param stage The stage to set
     */
    private static synchronized void setStage(Stage stage) {
        StartProgram.stage = stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
