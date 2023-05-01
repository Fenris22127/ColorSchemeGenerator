package de.colorscheme.app.controller;

import de.colorscheme.app.SelectImage;
import de.colorscheme.clustering.ColorData;
import de.customlogger.logger.ColorLogger;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.colorscheme.app.App.*;
//import static de.colorscheme.app.App.colorData;  FIXME
import static de.colorscheme.app.ChooseDirectory.autoSave;
import static de.colorscheme.app.SelectImage.chooseFile;
import static de.colorscheme.clustering.ColorData.resize;
import static de.colorscheme.clustering.KMeans.kMeans;
import static de.colorscheme.output.OutputColors.createOutput;

public class FXAppController<T> {
    public Spinner<T> number_colors;
    public Button upload;
    public CheckBox checkbox;
    public ProgressBar progress_bar;
    public Label progress_label;
    public TextArea progress_textfield;
    public Button download;
    public ImageView image_frame;

    /**
     * The {@link String} containing the path to the image chosen by the user
     */
    protected String fileName = "";

    /**
     * The {@link Boolean} storing if auto-download is enabled
     * Default: false - Auto-Download is disabled
     */
    protected boolean autoDownload = false;

    /**
     * The path, where the resulting color scheme file will be saved, depending on whether auto-download is enabled or
     * disabled
     */
    private String downloadPath;

    private static final Logger LOGGER = ColorLogger.newLogger(FXAppController.class.getName());

    @FXML
    protected void uploadImage() {
        fileName = chooseFile();
        if (!Objects.equals(fileName, "cancel")) {
            if (checkbox.isSelected()) {
                autoDownload = true;
                downloadPath = autoSave();
            }
            try {
                setProgress(10);
                appendOutput("Found file." + System.lineSeparator());

                BufferedImage img = findImage();
                setProgress(24);
                appendOutput("Reading colours in image." + System.lineSeparator());
                TimeUnit.MILLISECONDS.sleep(1000);

                ColorData data = new ColorData(img);

                setProgress(68);
                getOutputField().append("Determining main colors." + System.lineSeparator());

                //kMeans(data, selectedCentroids); FIXME
                setProgress(86);
                getOutputField().append("Creating the color scheme." + System.lineSeparator());

                setProgress(99);
                getOutputField().append("Finishing up..." + System.lineSeparator());

                TimeUnit.MILLISECONDS.sleep(1000);
                setProgress(100);
                getOutputField().append("Done!");
                download.cancelButtonProperty().setValue(true);
                //colorData = data; FIXME
                if (autoDownload) {
                    //createOutput(colorData, fileName); FIXME
                }
            }
            catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not find FXML file!");
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Could not find execute timeout!");
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void appendOutput(String s) {
        progress_textfield.appendText(s);
    }

    private void setProgress(double progress) {
        progress_bar.setProgress(progress);
    }

    /**
     * Gets the image from the path saved in {@link #fileName fileName} by {@link SelectImage#chooseFile()} and
     * resizes it to 150 pixels on the longest side, using the {@link ColorData#resize(BufferedImage) resize} method,
     * while keeping the aspect ratio.
     * @return A {@link BufferedImage} - The image selected by the user in its original size or scaled down, if it
     *          exceeds a certain Dimension
     * @throws IOException An {@link IOException} - If the selected file cannot be read
     */
    private BufferedImage findImage() throws IOException {
        File file = new File(fileName);
        BufferedImage image = ImageIO.read(file);
        double width = image.getWidth();
        double height = image.getHeight();
        if (width > 150 || height > 150) {
            image = resize(image);
        }
        return image;
    }
}

