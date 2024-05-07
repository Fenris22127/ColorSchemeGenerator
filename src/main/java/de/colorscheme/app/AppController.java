package de.colorscheme.app;

import de.colorscheme.clustering.ColorData;
import de.colorscheme.output.ColorHarmony;
import de.colorscheme.output.OutputColors;
import de.fenris.logger.ColorLogger;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
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

/**
 * The controller for the FXML file which is the main window of the program.
 *
 * @author &copy; 2023 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.2
 * @since 18.0.1
 */
public class AppController implements Initializable {
    /**
     * A {@link Boolean} indicating whether the program is in debug mode or not.
     */
    private static final boolean IS_DEBUG = false;
    /**
     * Creates a {@link ColorLogger} for this class
     */
    private static final Logger LOGGER = ColorLogger.newLogger(AppController.class.getName());
    /**
     * The {@link Image} which is displayed when the process has been completed
     */
    private static final ImageView done = new ImageView(Objects.requireNonNull(AppController.class.getResource("done.png")).toExternalForm());
    /**
     * The {@link ResourceBundle} containing the {@link String}s for the program in the language chosen by the user
     */
    private static ResourceBundle bundle;
    /**
     * A {@link Boolean} indicating whether the process has been cancelled or not
     */
    private static boolean cancelled = false;

    /**
     * The {@link ColorData} object used for all processes for the currently inspected image
     */
    private static ColorData colorData;

    /**
     * The {@link StringProperty} for the {@link Label} displaying the progress in the GUI
     */
    private static StringProperty textProp;
    private static Scene currentScene;
    private static ColorHarmony harmony = ColorHarmony.COMPLEMENTARY;
    @FXML
    private static ToggleButton complementary;
    @FXML
    private static ToggleButton splitComplementary;
    @FXML
    private static ToggleButton analogous;
    @FXML
    private static ToggleButton triadic;
    @FXML
    private static ToggleButton tetradic;
    @FXML
    private static ToggleButton monochromatic;

    static {
        String language = System.getProperty("user.language");
        if (language.equals("de")) {
            bundle = ResourceBundle.getBundle("messages_DE");
        } else {
            bundle = ResourceBundle.getBundle("messages_EN");
        }
    }

    public HBox colorsBox;
    /**
     * The {@link Path} containing the path to the image chosen by the user
     */
    protected Path fileName;
    @FXML
    private VBox basePane;
    @FXML
    private Pane harmonicPane;
    @FXML
    private CheckBox includeHarmonics;
    /**
     * The {@link TextArea} displaying the progress of the process in text form
     */
    @FXML
    private TextArea progressTextField;
    /**
     * The {@link Spinner} for the number of colors the user wants to have in the resulting color scheme
     */
    @FXML
    private Spinner<Integer> numberColors;
    /**
     * The {@link CheckBox} for enabling or disabling the auto-download feature
     */
    @FXML
    private CheckBox checkbox;
    /**
     * The {@link ProgressBar} displaying the progress of the process
     */
    @FXML
    private ProgressBar progressBar;
    /**
     * The {@link Label} displaying the progress of the process in percent
     */
    @FXML
    private Label progressLabel;
    /**
     * The {@link Label} displaying the icon indicating that the process has been completed
     */
    @FXML
    private Label progressImage;
    /**
     * The {@link Button} for downloading the resulting color scheme
     */
    @FXML
    private Button download;
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
     * The {@link Image} which is displayed in the {@link ImageView} after the user has chosen an image
     */
    @FXML
    private ImageView imageFrame;
    /**
     * The {@link Label} displaying the text for {@link #numberColors}
     */
    @FXML
    private Label colorAmountLabel;
    /**
     * The {@link Button} for uploading an image
     */
    @FXML
    private Button upload;
    /**
     * The {@link Tab} with the main content of the program
     */
    @FXML
    private Tab generateTab;
    /**
     * The {@link Tab} with uploaded image
     */
    @FXML
    private Tab imageTab;
    /**
     * The {@link Label} displaying the text for when no image has been selected for {@link #imageFrame} to display
     */
    @FXML
    private Label noImageLabel;
    private HBox hbox;
    private double height = 0;
    private double opacity = 1;

    /**
     * Returns the {@link TextField} displaying the progress of the process
     *
     * @return A {@link TextField}: The TextField displaying the progress of the process
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

    /*public static ResourceBundle getResBundle() {
        return bundle;
    }*/

    public static void addToOutputField(String text, boolean isError) {
        if (isError) {
            Objects.requireNonNull(getTextField()).setStyle("-fx-text-fill: red");
            Objects.requireNonNull(getTextField()).appendText(text + System.lineSeparator());
            Objects.requireNonNull(getTextField()).setStyle("-fx-text-fill: black");
        } else {
            Objects.requireNonNull(getTextField()).appendText(text);
        }
    }

    private static Path selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("selectImgUpload"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
        fileChooser.setInitialDirectory(Path.of(System.getProperty("user.home")).toFile());
        File chosen = fileChooser.showOpenDialog(getStage());

        return chosen == null ? null : chosen.toPath();
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
     * The setup methods that's called when the program is started
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setScene(numberColors.getScene());
        numberColors.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 4));
        IntegerProperty progressProp = new SimpleIntegerProperty((int) progressBar.getProgress());
        textProp = progressLabel.textProperty();
        Bindings.bindBidirectional(textProp, progressProp, new NumberStringConverter("#'%'"));
        done.setPreserveRatio(true);
        done.setFitHeight(32);

        languageChoice.setItems(FXCollections.observableList(List.of("English", "Deutsch")));
        languageChoice.setValue("Language");

        Image compIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/complementary_icon.png")), 30, 30, true, true);
        complementary = new ToggleButton();
        complementary.setId("complementary");
        complementary.setGraphic(new ImageView(compIcon));
        complementary.setStyle("-fx-background-radius: 50px; -fx-padding: 0;");

        Image monoIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/monochromatic_icon.png")), 30, 30, true, true);
        monochromatic = new ToggleButton();
        monochromatic.setId("monochromatic");
        monochromatic.setGraphic(new ImageView(monoIcon));
        monochromatic.setStyle("-fx-background-radius: 50px; -fx-padding: 0;");

        Image analogIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/analogous_icon.png")), 30, 30, true, true);
        analogous = new ToggleButton();
        analogous.setId("analogous");
        analogous.setGraphic(new ImageView(analogIcon));
        analogous.setStyle("-fx-background-radius: 50px; -fx-padding: 0;");

        Image splitCompIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/split-complementary_icon.png")), 30, 30, true, true);
        splitComplementary = new ToggleButton();
        splitComplementary.setId("split_complementary");
        splitComplementary.setGraphic(new ImageView(splitCompIcon));
        splitComplementary.setStyle("-fx-background-radius: 50px; -fx-padding: 0;");

        Image triIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/triadic_icon.png")), 30, 30, true, true);
        triadic = new ToggleButton();
        triadic.setId("triadic");
        triadic.setGraphic(new ImageView(triIcon));
        triadic.setStyle("-fx-background-radius: 50px; -fx-padding: 0 1px 0 0;");

        Image tetIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/tetradic_icon.png")), 30, 30, true, true);
        tetradic = new ToggleButton();
        tetradic.setId("tetradic");
        tetradic.setGraphic(new ImageView(tetIcon));
        tetradic.setStyle("-fx-background-radius: 50px; -fx-padding: 0 1px 0 0;");

        hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(5, 0, 0, 5));

        List<ToggleButton> buttons = List.of(complementary, splitComplementary, analogous, triadic, tetradic, monochromatic);
        for (ToggleButton button : buttons) {
            button.getGraphic().setOpacity(0);
            button.setPrefSize(40, 40);
            button.setMaxSize(40, 40);
            button.setMinSize(40, 40);
            button.setOnAction(this::toggleButton);

            button.setClip(new Rectangle(42, 42));
        }
        AnimationTimer collapse = new CollapsePane();
        AnimationTimer expand = new ExpandPane();
        includeHarmonics.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            if (includeHarmonics.isSelected()) {
                collapse.stop();
                expand.start();
            } else {
                expand.stop();
                collapse.start();
            }
        });
        hbox.setPrefSize(400, 0);
        hbox.setMaxHeight(0);
        hbox.setMinHeight(0);
        harmonicPane.getChildren().add(hbox);


        languageChoice.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            bundle = switch (newValue.intValue()) {
                case 0 -> ResourceBundle.getBundle("messages_EN");
                case 1 -> ResourceBundle.getBundle("messages_DE");
                default -> throw new IllegalStateException("Unexpected value: " + newValue.intValue());
            };
            title.setText(bundle.getString("appTitle"));
            colorAmountLabel.setText(bundle.getString("appColorAmount"));
            checkbox.setText(bundle.getString("appAutoDownload"));
            includeHarmonics.setText(bundle.getString("appIncludeHarmonics"));
            download.setText(bundle.getString("appDownload"));
            upload.setText(bundle.getString("appUpload"));
            generateTab.setText(bundle.getString("appGenerate"));
            imageTab.setText(bundle.getString("appImage"));
            noImageLabel.setText(bundle.getString("appNoImage"));
        });

        if (IS_DEBUG) {
            LOGGER.log(Level.INFO, "Spinner Default Value: {0}", numberColors.getValue());
        }
    }

    private void toggleButton(ActionEvent actionEvent) {
        ToggleButton tBtn = (ToggleButton) actionEvent.getSource();
        List<ToggleButton> buttons = List.of(complementary, splitComplementary, analogous, triadic, tetradic, monochromatic);
        for (ToggleButton button : buttons) {
            if (button != tBtn) {
                button.setSelected(false);
            } else {
                button.setSelected(true);
                switch (button.getId()) {
                    case "complementary" -> harmony = ColorHarmony.COMPLEMENTARY;
                    case "split_complementary" -> harmony = ColorHarmony.SPLIT_COMPLEMENTARY;
                    case "analogous" -> harmony = ColorHarmony.ANALOGOUS;
                    case "triadic" -> harmony = ColorHarmony.TRIADIC;
                    case "tetradic" -> harmony = ColorHarmony.TETRADIC;
                    case "monochromatic" -> harmony = ColorHarmony.MONOCHROMATIC;
                    default -> throw new IllegalStateException("Unexpected value: " + button.getId());
                }
            }
        }
    }

    /**
     * Resets the GUI to its initial state to start a new process
     */
    private void reset() {
        progressBar.progressProperty().unbind();
        Bindings.unbindBidirectional(textProp, progressBar.progressProperty());
        progressBar.progressProperty().set(0);
        textProp.set("0%");
        download.setDisable(true);
        progressImage.setGraphic(null);
        progressTextField.setText("");
    }

    /**
     * Opens a file chooser for the user to choose an image and starts a new {@link ReadImage} {@link Task} for reading
     * the image and generating the color scheme
     */
    @FXML
    protected void uploadImage() {
        reset();
        fileName = selectFile();
        if (checkbox.isSelected()) {
            OutputColors.setDownloadPath(System.getProperty("user.home").concat("/Downloads"));
        }
        if (fileName != null) {
            if (IS_DEBUG) {
                LOGGER.log(Level.INFO, "File chosen.");
            }
            Task<Void> task = new ReadImage();
            progressBar.progressProperty().bind(task.progressProperty());
            Bindings.bindBidirectional(textProp, progressBar.progressProperty(), new NumberStringConverter("#%"));
            progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
                if (Double.compare(newValue.doubleValue(), 1.0) == 0) {
                    progressImage.setGraphic(done);
                }
                if (newValue.doubleValue() < 0.45) {
                    progressLabel.setStyle("-fx-text-fill: black");
                }
                if (newValue.doubleValue() > 0.45) {
                    progressLabel.setStyle("-fx-text-fill: white");
                }
            });
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Creates the output file and writes the color scheme to it
     */
    @FXML
    private void downloadFile() {
        if (!checkbox.isSelected()) {
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

    private class CollapsePane extends AnimationTimer {
        @Override
        public void handle(long now) {
            collapse();
        }

        private void collapse() {
            height -= 7;
            opacity -= 0.1;

            basePane.setMinHeight(basePane.getMinHeight() - 7);
            hbox.setPrefHeight(height);
            hbox.setMaxHeight(height);
            hbox.setMinHeight(height);
            List<ToggleButton> harmonics = List.of(complementary, monochromatic, analogous, splitComplementary, triadic, tetradic);
            for (ToggleButton harmonic : harmonics) {
                harmonic.getGraphic().setOpacity(0);
                harmonic.clipProperty().setValue(new Rectangle(42, height));
            }
            hbox.getChildren().clear();
            if (height <= 0) {
                stop();
            }
        }
    }

    private class ExpandPane extends AnimationTimer {
        @Override
        public void handle(long now) {
            expand();
        }

        private void expand() {
            height += 7;
            opacity += 0.1;
            basePane.setMinHeight(basePane.getMinHeight() + 7);
            hbox.setMaxHeight(height);
            hbox.setPrefHeight(height);
            hbox.setMinHeight(height);
            List<ToggleButton> harmonics = List.of(complementary, monochromatic, analogous, splitComplementary, triadic, tetradic);
            if (hbox.getChildren().isEmpty()) {
                hbox.getChildren().addAll(harmonics);
            } else {
                for (ToggleButton harmonic : harmonics) {
                    harmonic.getGraphic().setOpacity(1);
                    harmonic.clipProperty().setValue(new Rectangle(42, height));
                }
            }
            if (height >= 44) {
                stop();
            }
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
            progressTextField.appendText(
                    bundle.getString("startFoundFile")
                            + System.lineSeparator());

            BufferedImage img;
            try {
                img = findImage();
                imageFrame.setImage(new Image(fileName.toUri().toString()));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "IOException: Could not read file!");
                progressTextField.appendText(bundle.getString("errorReadingFile"));
                return null;
            }

            updateProgress(24, 100);
            progressTextField.appendText(
                    bundle.getString("startReadingColours")
                            + System.lineSeparator());
            TimeUnit.MILLISECONDS.sleep(1000);

            setColorData(new ColorData(img));
            if (!cancelled) {
                updateProgress(68, 100);
                progressTextField.appendText(bundle.getString("startDeterminingColours")
                        + System.lineSeparator());
            }

            if (!cancelled) {
                kMeans(getColorData(), numberColors.getValue());
                updateProgress(86, 100);
                progressTextField.appendText(bundle.getString("startCreatingScheme")
                        + System.lineSeparator());
            }

            if (!cancelled) {
                updateProgress(99, 100);
                progressTextField.appendText(bundle.getString("startFinishing")
                        + System.lineSeparator());
            }

            if (!cancelled) {
                TimeUnit.MILLISECONDS.sleep(1000);
                updateProgress(100, 100);
                progressTextField.appendText(bundle.getString("startDone")
                        + System.lineSeparator());
                download.setDisable(false);
            }
            if (checkbox.isSelected() && !cancelled) {
                downloadFile();
            }
            return null;
        }

        /**
         * Gets the image from the path saved in {@link #fileName fileName} by {@link } and
         * resizes it to 150 pixels on the longest side, using the {@link ColorData#resize(BufferedImage) resize} method,
         * while keeping the aspect ratio.
         *
         * @return A {@link BufferedImage} - The image selected by the user in its original size or scaled down, if it
         * exceeds a certain Dimension
         * @throws IOException An {@link IOException} - If the selected file cannot be read
         */
        private BufferedImage findImage() throws IOException {
            BufferedImage image = ImageIO.read(fileName.toFile());
            double width = image.getWidth();
            double height = image.getHeight();
            if (width > 150 || height > 150) {
                image = resize(image);
            }
            return image;
        }
    }
}

