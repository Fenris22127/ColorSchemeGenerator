package de.colorscheme.app;

import de.colorscheme.clustering.ColorData;
import de.colorscheme.output.ColorHarmony;
import de.colorscheme.output.OutputColors;
import de.fenris.logger.ColorLogger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.converter.NumberStringConverter;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.colorscheme.main.StartProgram.getStage;
import static de.colorscheme.output.OutputColors.createOutput;

/**
 * The controller for the FXML file which is the main window of the program.
 *
 * @author &copy; 2024 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 2.0
 * @since 18.0.1
 */
public class AppController implements Initializable {
    /**
     * A {@link Boolean} indicating whether the program is in debug mode or not.
     */
    protected static final boolean IS_DEBUG = false;
    /**
     * Creates a {@link ColorLogger Logger} for this class
     */
    private static final Logger LOGGER = ColorLogger.newLogger(AppController.class.getName());
    /**
     * The path to the user's home directory
     */
    private static final String USER_HOME = System.getProperty("user.home");
    /**
     * A {@link List} containing the different color harmonies that can be used to calculate from the main colors.
     */
    private static final List<ColorHarmony> harmony = new LinkedList<>();
    /**
     * The {@link ResourceBundle} containing the {@link String}s for the program in the language chosen by the user
     */
    protected static ResourceBundle bundle;
    /**
     * The {@link Scene} object for the current scene
     */
    private static Scene currentScene;
    /**
     * The {@link ColorData} object used for all processes for the currently inspected image
     */
    private static ColorData colorData;
    /**
     * The {@link StringProperty} for the {@link Label} displaying the progress in the GUI
     */
    private static StringProperty textProp;

    static {
        String language = System.getProperty("user.language");
        if (language.equals("de")) {
            bundle = ResourceBundle.getBundle("messages_DE");
        } else {
            bundle = ResourceBundle.getBundle("messages_EN");
        }
    }

    /**
     * The {@link Path} containing the path to the image chosen by the user
     */
    protected Path fileName;
    /**
     * The {@link Button} for uploading an image
     */
    @FXML
    private Button uploadButton;
    /**
     * The {@link HBox} containing the start button and the progress bar
     */
    @FXML
    private HBox startBox;
    /**
     * The {@link Button} for starting the process
     */
    @FXML
    private Button startBtn;
    /**
     * The {@link Button} for re-uploading an image
     */
    @FXML
    private Button reuploadButton;
    /**
     * The {@link Button} for downloading the color scheme
     */
    @FXML
    private Button downloadBtn;
    /**
     * The {@link VBox} containing the upload pane
     */
    @FXML
    private VBox uploadPane;
    /**
     * The {@link VBox} containing the progress bar and the progress text field
     */
    @FXML
    private VBox progressBox;
    /**
     * The {@link Spinner} for choosing the number of colors in the color scheme
     */
    @FXML
    private Spinner<Integer> spinner;
    /**
     * The {@link ProgressBar} displaying the progress of the process
     */
    @FXML
    private ProgressBar progressBar;
    /**
     * The {@link TextArea} displaying the progress of the process
     */
    @FXML
    private TextArea progressTextField;
    /**
     * The {@link TitledPane} containing the different color harmonies
     * that can be used to calculate from the main colors
     */
    @FXML
    private TitledPane harmonicsDropdown;
    /**
     * The {@link Label} for the {@link #spinner}
     */
    @FXML
    private Label numberOfColors;
    /**
     * The {@link ChoiceBox} for choosing the language of the program. Currently only English and German are supported.
     */
    @FXML
    private ChoiceBox<String> languageChoice;
    /**
     * The {@link Label} displaying the title of the program
     */
    @FXML
    private Label title;
    /**
     * The {@link Label} displaying the progress of the process
     */
    @FXML
    private Label progressLabel;
    /**
     * The {@link Label} displaying the name of the file chosen by the user
     */
    @FXML
    private Label fileNameLabel;
    /**
     * The {@link CheckBox} for automatically downloading the color scheme
     */
    @FXML
    private CheckBox autoDownload;
    /**
     * The {@link CheckBox} for adding harmonics to the color scheme
     */
    @FXML
    private CheckBox addHarmonics;
    /**
     * The {@link ToggleButton}s for the complementary color harmony
     */
    @FXML
    private ToggleButton complementary;
    /**
     * The {@link ToggleButton}s for the monochromatic color harmony
     */
    @FXML
    private ToggleButton monochromatic;
    /**
     * The {@link ToggleButton}s for the analogous color harmony
     */
    @FXML
    private ToggleButton analogous;
    /**
     * The {@link ToggleButton}s for the split complementary color harmony
     */
    @FXML
    private ToggleButton splitcomplementary;
    /**
     * The {@link ToggleButton}s for the triadic color harmony
     */
    @FXML
    private ToggleButton triadic;
    /**
     * The {@link ToggleButton}s for the tetradic color harmony
     */
    @FXML
    private ToggleButton tetradic;

    /**
     * Returns the {@link TextArea} displaying the progress of the process
     *
     * @return A {@link TextArea}: The TextField displaying the progress of the process
     */
    public static TextArea getTextField() {
        Node node = currentScene.lookup("#progressTextField");
        if (node.getClass() == TextArea.class) {
            return (TextArea) node;
        } else {
            return null;
        }
    }

    /**
     * Returns the {@link List} of {@link ColorHarmony}s that are to be included in the color scheme
     *
     * @return A {@link List} of {@link ColorHarmony}s: The color harmonies to be included in the color scheme
     */
    public static List<ColorHarmony> getHarmony() {
        return harmony;
    }

    /**
     * Returns the {@link ResourceBundle} containing the {@link String}s for the program in the language
     * chosen by the user
     *
     * @return A {@link ResourceBundle}:
     * The {@link ResourceBundle} containing the {@link String}s for the program in the language chosen by the user
     */
    public static ResourceBundle getResBundle() {
        return bundle;
    }

    /**
     * Adds a {@link String} to the {@link TextArea} displaying the progress of the process <br>
     * If the passed text is an error message:
     * <ul>
     *     <li>Set the text color to red</li>
     *     <li>Add the message to the text field where status messages are displayed</li>
     *     <li>Set the text colour back to black (Amy Winehouse was here)</li>
     * </ul>
     * If the passed text isn't an error message:
     * <ul>
     *     <li>Add the message to the text field where status messages are displayed</li>
     * </ul>
     *
     * @param text    A {@link String}: The text to be added to the {@link TextArea}
     * @param isError A {@link Boolean}: Whether the text is an error message or not
     */
    public static void addToOutputField(String text, boolean isError) {
        if (isError) {
            Objects.requireNonNull(getTextField()).setStyle("-fx-text-fill: #cc0000");
            Objects.requireNonNull(getTextField()).appendText(text + System.lineSeparator());
            Objects.requireNonNull(getTextField()).setStyle("-fx-text-fill: black");
        } else {
            Objects.requireNonNull(getTextField()).appendText(text);
        }
    }

    /**
     * Opens a file chooser for the user to choose an image
     *
     * @return A {@link Path}: The path to the image chosen by the user
     */
    private static Path selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("selectImgUpload"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
        fileChooser.setInitialDirectory(Paths.get(USER_HOME).toFile());
        File chosen = fileChooser.showOpenDialog(getStage());

        return chosen == null ? null : chosen.toPath();
    }

    /**
     * Gets the {@link ColorData} object used for all processes for the currently inspected image
     *
     * @return A {@link ColorData}: The {@link ColorData} object used for all processes for the currently inspected image
     */
    public static ColorData getColorData() {
        return colorData;
    }

    /**
     * Sets the {@link ColorData} object used for all processes for the currently inspected image
     *
     * @param data A {@link ColorData}: The {@link ColorData} object used for all processes for the currently inspected image
     */
    public static void setColorData(ColorData data) {
        colorData = data;
    }

    /**
     * Initializes the GUI
     *
     * @param url            The {@link URL} of the FXML file
     * @param resourceBundle The {@link ResourceBundle} containing the {@link String}s for the program in the language chosen by the user
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Hide the box for starting the process, the progress bar and the label containing the file name
        startBox.setVisible(false);
        startBox.setManaged(false);
        progressBox.setVisible(false);
        progressBox.setManaged(false);
        progressTextField.setVisible(false);
        progressTextField.setManaged(false);
        fileNameLabel.setVisible(false);
        fileNameLabel.setManaged(false);

        // Set the image for the upload button
        ImageView imageView = new ImageView(Objects.requireNonNull(getClass().getResource("upload_darker.png")).toExternalForm());
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(25);
        uploadButton.setGraphic(imageView);
        uploadButton.setGraphicTextGap(13);

        // Set the image for the re-upload button
        imageView = new ImageView(Objects.requireNonNull(getClass().getResource("upload_darker.png")).toExternalForm());
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(18);
        reuploadButton.setGraphic(imageView);
        reuploadButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        // Styles the buttons for the different color harmonies
        styleButtons(complementary, "complementary_light");
        styleButtons(monochromatic, "monochromatic_light");
        styleButtons(analogous, "analogous_light");
        styleButtons(splitcomplementary, "splitcomplementary_light");
        styleButtons(triadic, "triadic_light");
        styleButtons(tetradic, "tetradic_light");

        // Styles the spinner for choosing the number of colors
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 4));

        // Sets the available languages in the choice box
        languageChoice.setItems(FXCollections.observableList(Arrays.asList("English", "Deutsch")));
        languageChoice.setValue("English");

        // Sets the language of the program according to the choice of the user
        languageChoice.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            setBundle(newValue);
            title.setText(bundle.getString("appTitle"));
            autoDownload.setText(bundle.getString("appAutoDownload"));
            numberOfColors.setText(bundle.getString("appColorAmount"));
            addHarmonics.setText(bundle.getString("appIncludeHarmonics"));
            downloadBtn.setText(bundle.getString("appDownload"));
            uploadButton.setText(bundle.getString("appUpload"));
            harmonicsDropdown.setText(bundle.getString("appHarmonics"));
        });

        // Binds the progress bar to the progress label
        IntegerProperty progressProp = new SimpleIntegerProperty((int) progressBar.getProgress());
        bind(progressLabel);
        Bindings.bindBidirectional(textProp, progressProp, new NumberStringConverter("#'%'"));

    }

    /**
     * Binds the progress bar to the progress label
     * @param progressLabel The {@link Label} displaying the progress of the process
     */
    private static synchronized void bind(Label progressLabel) {
        textProp = progressLabel.textProperty();
    }

    /**
     * Sets the language of the program according to the choice of the user
     * @param newValue A {@link Number}: The index of the chosen language
     */
    private static synchronized void setBundle(Number newValue) {
        ClassLoader classLoader = AppController.class.getClassLoader();
        if (newValue.intValue() == 0) {
            bundle = ResourceBundle.getBundle("messages_EN", Locale.getDefault(), classLoader);
        } else if (newValue.intValue() == 1) {
            bundle = ResourceBundle.getBundle("messages_DE", Locale.getDefault(), classLoader);
        } else {
            throw new IllegalStateException("Unexpected value: " + newValue.intValue());
        }
    }

    /**
     * Styles the buttons for the different color harmonies
     *
     * @param button   The {@link ToggleButton} to be styled
     * @param fileName The name of the file containing the icon for the button
     */
    private void styleButtons(ToggleButton button, String fileName) {
        int iconSize = 42;
        ImageView imageView = new ImageView(Objects.requireNonNull(getClass().getResource("icons/" + fileName + ".png")).toExternalForm());
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(iconSize);
        button.setGraphic(imageView);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    /**
     * Toggles the visibility of the harmonics dropdown
     *
     * @param event The {@link ActionEvent} that triggered the method
     */
    @FXML
    private void toggleHarmonicsCheckbox(ActionEvent event) {
        CheckBox check = (CheckBox) event.getSource();
        if (check.isSelected()) {
            harmonicsDropdown.setDisable(false);
            harmonicsDropdown.setExpanded(true);
        } else {
            harmonicsDropdown.setDisable(true);
            harmonicsDropdown.setExpanded(false);
        }
    }

    /**
     * Toggles the style of the buttons for the different color harmonies
     * and adds or removes the harmonies from the list
     *
     * @param event The {@link ActionEvent} that triggered the method
     */
    @FXML
    private void toggleBtn(ActionEvent event) {
        ToggleButton button = (ToggleButton) event.getSource();
        if (button.isSelected()) {
            styleButtons(button, button.getId().concat("_selected"));
            switch(button.getId()) {
                case "complementary": {
                    harmony.add(ColorHarmony.COMPLEMENTARY);
                    break;
                }
                case "splitcomplementary": {
                    harmony.add(ColorHarmony.SPLITCOMPLEMENTARY);
                    break;
                }
                case "analogous": {
                    harmony.add(ColorHarmony.ANALOGOUS);
                    break;
                }
                case "triadic": {
                    harmony.add(ColorHarmony.TRIADIC);
                    break;
                }
                case "tetradic": {
                    harmony.add(ColorHarmony.TETRADIC);
                    break;
                }
                case "monochromatic": {
                    harmony.add(ColorHarmony.MONOCHROMATIC);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unexpected value: " + button.getId());
                }
            }
        } else {
            styleButtons(button, button.getId().concat("_light"));
            harmony.remove(ColorHarmony.valueOf(button.getId().toUpperCase()));
        }
    }

    /**
     * Toggles the visibility of the start button,
     * the progress bar and field and the file name label and starts the process
     */
    @FXML
    private void startProcess() {
        startBtn.setVisible(false);
        startBtn.setManaged(false);
        progressBox.setVisible(true);
        progressBox.setManaged(true);
        progressTextField.setVisible(true);
        progressTextField.setManaged(true);
        startBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        fileNameLabel.setVisible(false);
        fileNameLabel.setManaged(false);

        startProgram();
    }

    /**
     * Opens/Reopens the file chooser for the user to choose a new image
     */
    @FXML
    private void upload() {
        Path file = selectFile();
        if (file != null) {
            fileName = file;
        }

        if (IS_DEBUG) {
            LOGGER.log(Level.INFO, String.format("File name: %s", fileName));
        }

        reset();
        setScene(startBox.getScene());
        if (fileName != null) {
            uploadButton.setVisible(false);
            uploadButton.setManaged(false);
            uploadPane.getStyleClass().clear();

            fileNameLabel.setText(fileName.getFileName().toString());
            fileNameLabel.setVisible(true);
            fileNameLabel.setManaged(true);
            startBox.setVisible(true);
            startBox.setManaged(true);
        }
    }

    /**
     * Returns the path to the image chosen by the user
     *
     * @return A {@link Path}: The path to the image chosen by the user
     */
    public Path getFileName() {
        return fileName;
    }

    /**
     * Returns the {@link Button} for downloading the color scheme
     *
     * @return A {@link Button}: The button for downloading the color scheme
     */
    public Button getDownloadBtn() {
        return downloadBtn;
    }

    /**
     * Returns the {@link CheckBox} for automatically downloading the color scheme
     *
     * @return A {@link CheckBox}: The checkbox for automatically downloading the color scheme
     */
    public CheckBox getAutoDownload() {
        return autoDownload;
    }

    /**
     * Returns the {@link Spinner} for choosing the number of colors in the color scheme
     *
     * @return A {@link Spinner}: The spinner for choosing the number of colors in the color scheme
     */
    public Spinner<Integer> getSpinner() {
        return spinner;
    }

    /**
     * Returns the {@link Scene} object for the current scene
     *
     * @param scene A {@link Scene}: The scene to be set as the current scene
     */
    public static synchronized void setScene(Scene scene) {
        currentScene = scene;
    }

    /**
     * Resets the GUI to its initial state to start a new process
     */
    private void reset() {
        startBtn.setVisible(true);
        startBtn.setManaged(true);
        progressBox.setVisible(false);
        progressBox.setManaged(false);
        progressTextField.setVisible(false);
        progressTextField.setManaged(false);
        startBox.setPrefHeight(182);
        fileNameLabel.setVisible(true);
        fileNameLabel.setManaged(true);

        progressBar.progressProperty().unbind();
        Bindings.unbindBidirectional(textProp, progressBar.progressProperty());
        progressBar.progressProperty().set(0);
        textProp.set("0%");
        downloadBtn.setDisable(true);
        progressTextField.setText("");
    }

    /**
     * Opens a file chooser for the user to choose an image and starts a new  {@link Task} for reading
     * the image and generating the color scheme
     */
    @FXML
    protected void startProgram() {
        if (autoDownload.isSelected()) {
            OutputColors.setDownloadPath(USER_HOME.concat("/Downloads"));
        }
        if (fileName != null) {
            if (IS_DEBUG) {
                LOGGER.log(Level.INFO, harmony.toString());
            }
            ReadImage readImage = new ReadImage();
            readImage.setController(this);
            progressBar.progressProperty().bind(readImage.progressProperty());
            Bindings.bindBidirectional(textProp, progressBar.progressProperty(), new NumberStringConverter("#%"));
            Thread thread = new Thread(readImage);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Creates the output file and writes the color scheme to it
     */
    @FXML
    protected void downloadFile() {
        if (!autoDownload.isSelected()) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(bundle.getString("chooseDirSaveIn"));
            directoryChooser.setInitialDirectory(Paths.get(USER_HOME).toFile());
            File chosen = directoryChooser.showDialog(getStage());
            if (chosen != null) {
                OutputColors.setDownloadPath(String.valueOf(chosen.toPath()));
                createOutput(getColorData(), fileName);
                progressTextField.appendText(bundle.getString("startDownloaded"));
            }
        } else {
            createOutput(getColorData(), fileName);
        }
    }
}