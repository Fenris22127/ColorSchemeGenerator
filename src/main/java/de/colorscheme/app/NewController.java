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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.colorscheme.clustering.ColorData.resize;
import static de.colorscheme.clustering.KMeans.kMeans;
import static de.colorscheme.main.StartProgram.getStage;
import static de.colorscheme.output.OutputColors.createOutput;

public class NewController implements Initializable {
    /**
     * Creates a {@link ColorLogger} for this class
     */
    private static final Logger LOGGER = ColorLogger.newLogger(AppController.class.getName());
    /**
     * A {@link Boolean} indicating whether the program is in debug mode or not.
     */
    private static final boolean IS_DEBUG = true;
    private static Scene currentScene;
    /**
     * The {@link ResourceBundle} containing the {@link String}s for the program in the language chosen by the user
     */
    private static ResourceBundle bundle;
    /**
     * A {@link Boolean} indicating whether the process has been cancelled or not
     */
    private static boolean cancelled = false;
    private static ColorHarmony harmony = ColorHarmony.COMPLEMENTARY;
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
    @FXML
    Button uploadButton;
    @FXML
    HBox startBox;
    @FXML
    Button startBtn;
    @FXML
    Button reuploadButton;
    @FXML
    Button downloadBtn;
    @FXML
    VBox uploadPane;
    @FXML
    VBox progressBox;
    @FXML
    Spinner<Integer> spinner;
    @FXML
    ProgressBar progressBar;
    @FXML
    TextArea progressTextField;
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
    @FXML
    private Label progressLabel;
    @FXML
    private Label fileNameLabel;
    @FXML
    private CheckBox autoDownload;
    @FXML
    private CheckBox addHarmonics;
    @FXML
    private ToggleButton complementary;
    @FXML
    private ToggleButton monochromatic;
    @FXML
    private ToggleButton analogous;
    @FXML
    private ToggleButton splitcomplementary;
    @FXML
    private ToggleButton triadic;
    @FXML
    private ToggleButton tetradic;

    /**
     * Returns the {@link TextArea} displaying the progress of the process
     *
     * @return A {@link TextArea}: The TextField displaying the progress of the process
     */
    public static TextArea getTextField() {
        Node node = currentScene.lookup("#progressTextField");
        if (node instanceof TextArea textarea) {
            return textarea;
        } else {
            return null;
        }
    }

    /**
     * Sets {@link #cancelled} to {@code true} and cancels the process
     *
     * @param isCancelled A {@link Boolean}: {@code true} if the process has been cancelled, {@code false} otherwise
     */
    public static void setCancelled(boolean isCancelled) {
        cancelled = isCancelled;
    }

    public static ResourceBundle getResBundle() {
        return bundle;
    }

    public static void addToOutputField(String text, boolean isError) {
        if (isError) {
            Objects.requireNonNull(getTextField()).setStyle("-fx-text-fill: red");
            Objects.requireNonNull(getTextField()).appendText(text + System.lineSeparator());
            Objects.requireNonNull(getTextField()).setStyle("-fx-text-fill: black");
        } else {
            Objects.requireNonNull(getTextField()).appendText(text);
        }
    }

    // Fine
    private static Path selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("selectImgUpload"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
        fileChooser.setInitialDirectory(Path.of(System.getProperty("user.home")).toFile());
        File chosen = fileChooser.showOpenDialog(getStage());

        return chosen == null ? null : chosen.toPath();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startBox.setVisible(false);
        startBox.setManaged(false);
        progressBox.setVisible(false);
        progressBox.setManaged(false);
        progressTextField.setVisible(false);
        progressTextField.setManaged(false);
        fileNameLabel.setVisible(false);
        fileNameLabel.setManaged(false);

        ImageView imageView = new ImageView(Objects.requireNonNull(getClass().getResource("upload_darker.png")).toExternalForm());
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(25);
        uploadButton.setGraphic(imageView);
        uploadButton.setGraphicTextGap(13);

        imageView = new ImageView(Objects.requireNonNull(getClass().getResource("upload_darker.png")).toExternalForm());
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(18);
        reuploadButton.setGraphic(imageView);
        reuploadButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        styleButtons(complementary, "complementary_light");
        styleButtons(monochromatic, "monochromatic_light");
        styleButtons(analogous, "analogous_light");
        styleButtons(splitcomplementary, "splitcomplementary_light");
        styleButtons(triadic, "triadic_light");
        styleButtons(tetradic, "tetradic_light");

        spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 4));

        languageChoice.setItems(FXCollections.observableList(List.of("English", "Deutsch")));
        languageChoice.setValue("Language");

        languageChoice.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            bundle = switch (newValue.intValue()) {
                case 0 -> ResourceBundle.getBundle("messages_EN");
                case 1 -> ResourceBundle.getBundle("messages_DE");
                default -> throw new IllegalStateException("Unexpected value: " + newValue.intValue());
            };
            title.setText(bundle.getString("appTitle"));
            autoDownload.setText(bundle.getString("appAutoDownload"));
            addHarmonics.setText(bundle.getString("appIncludeHarmonics"));
            downloadBtn.setText(bundle.getString("appDownload"));
            uploadButton.setText(bundle.getString("appUpload"));
        });

        IntegerProperty progressProp = new SimpleIntegerProperty((int) progressBar.getProgress());
        textProp = progressLabel.textProperty();
        Bindings.bindBidirectional(textProp, progressProp, new NumberStringConverter("#'%'"));

    }

    private void styleButtons(ToggleButton button, String fileName) {
        int iconSize = 42;
        ImageView imageView = new ImageView(getClass().getResource("icons/" + fileName + ".png").toExternalForm());
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(iconSize);
        button.setGraphic(imageView);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @FXML
    private void toggleBtn(ActionEvent event) {
        ToggleButton o = (ToggleButton) event.getSource();
        if (o.isSelected()) {
            styleButtons(o, o.getId().concat("_selected"));
        } else {
            styleButtons(o, o.getId().concat("_light"));
        }
        setHarmony(event);
    }

    private void setHarmony(ActionEvent actionEvent) {
        ToggleButton tBtn = (ToggleButton) actionEvent.getSource();
        List<ToggleButton> buttons = List.of(complementary, splitcomplementary, analogous, triadic, tetradic, monochromatic);
        for (ToggleButton button : buttons) {
            if (button != tBtn) {
                button.setSelected(false);
                styleButtons(tBtn, tBtn.getId().concat("_light"));
            } else {
                button.setSelected(true);
                switch (button.getId()) {
                    case "complementary" -> harmony = ColorHarmony.COMPLEMENTARY;
                    case "splitcomplementary" -> harmony = ColorHarmony.SPLIT_COMPLEMENTARY;
                    case "analogous" -> harmony = ColorHarmony.ANALOGOUS;
                    case "triadic" -> harmony = ColorHarmony.TRIADIC;
                    case "tetradic" -> harmony = ColorHarmony.TETRADIC;
                    case "monochromatic" -> harmony = ColorHarmony.MONOCHROMATIC;
                    default -> throw new IllegalStateException("Unexpected value: " + button.getId());
                }
            }
        }
    }

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

    public void setScene(Scene scene) {
        currentScene = scene;
    }

    public ColorData getColorData() {
        return colorData;
    }

    public static void setColorData(ColorData data) {
        colorData = data;
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
            OutputColors.setDownloadPath(System.getProperty("user.home").concat("/Downloads"));
        }
        if (fileName != null) {
            if (IS_DEBUG) {
                LOGGER.log(Level.INFO, "File chosen.");
            }
            Task<Void> task = new ReadImage();
            progressBar.progressProperty().bind(task.progressProperty());
            Bindings.bindBidirectional(textProp, progressBar.progressProperty(), new NumberStringConverter("#%"));
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }


    /**
     * An inner class that extends {@link Task} and is used to read the image and generate the color scheme
     */
    private class ReadImage extends Task<Void> {
        /**
         * The method that's called when the {@link Task} is started. <br>
         * Reads the image and generates the color scheme
         *
         * @return {@code null}
         * @throws Exception If an error occurs while reading the image
         */
        @Override
        protected Void call() throws Exception {
            updateProgress(10, 100);
            if (IS_DEBUG) {
                LOGGER.log(Level.INFO, "Entered task.");
            }
            progressTextField.appendText(
                    bundle.getString("startFoundFile")
                            + System.lineSeparator());

            BufferedImage img;
            try {
                img = findImage();
                if (IS_DEBUG) {
                    LOGGER.log(Level.INFO, "Found image.");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "IOException: Could not read file!");
                addToOutputField(bundle.getString("errorReadingFile") + System.lineSeparator(), true);
                return null;
            }

            updateProgress(24, 100);
            addToOutputField(bundle.getString("startReadingColours") + System.lineSeparator(), false);
            TimeUnit.MILLISECONDS.sleep(1000);

            setColorData(new ColorData(img));
            if (!cancelled) {
                updateProgress(68, 100);
                addToOutputField(bundle.getString("startDeterminingColours") + System.lineSeparator(), false);
            }

            if (!cancelled) {
                kMeans(getColorData(), spinner.getValue());
                updateProgress(86, 100);
                addToOutputField(bundle.getString("startCreatingScheme") + System.lineSeparator(), false);
                if (IS_DEBUG) {
                    LOGGER.log(Level.INFO, "Creating colour scheme.");
                }
            }

            if (!cancelled) {
                updateProgress(99, 100);
                addToOutputField(bundle.getString("startFinishing") + System.lineSeparator(), false);
                if (IS_DEBUG) {
                    LOGGER.log(Level.INFO, "Finishing up.");
                }
            }

            if (!cancelled) {
                TimeUnit.MILLISECONDS.sleep(1000);
                updateProgress(100, 100);
                addToOutputField(bundle.getString("startDone") + System.lineSeparator(), false);
                downloadBtn.setDisable(false);
            }
            if (autoDownload.isSelected() && !cancelled) {
                downloadFile();
            }
            return null;
        }

        /**
         * Gets the image from the path saved in  by {@link } and
         * resizes it to 150 pixels on the longest side, using the {@link ColorData#resize(BufferedImage) resize} method,
         * while keeping the aspect ratio.
         *
         * @return A {@link BufferedImage} - The image selected by the user in its original size or scaled down, if it
         * exceeds a certain Dimension
         * @throws IOException An {@link IOException} - If the selected file cannot be read
         */
        private BufferedImage findImage() throws IOException {
            BufferedImage image = ImageIO.read(fileName.toFile());
            if (IS_DEBUG) {
                LOGGER.log(Level.INFO, String.format("Image found? %s", image != null ? "Yes" : "No"));
            }
            assert image != null;
            double width = image.getWidth();
            double height = image.getHeight();
            if (width > 150 || height > 150) {
                image = resize(image);
            }
            return image;
        }

        /**
         * Creates the output file and writes the color scheme to it
         */
        @FXML
        private void downloadFile() {
            if (!autoDownload.isSelected()) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle(bundle.getString("chooseDirSaveIn"));
                directoryChooser.setInitialDirectory(Path.of(System.getProperty("user.home")).toFile());
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
}
