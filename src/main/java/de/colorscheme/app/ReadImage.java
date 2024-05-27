package de.colorscheme.app;

import de.colorscheme.clustering.ColorData;
import de.fenris.logger.ColorLogger;
import javafx.concurrent.Task;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.colorscheme.app.AppController.*;
import static de.colorscheme.clustering.ColorData.createColorData;
import static de.colorscheme.clustering.ColorData.resize;
import static de.colorscheme.clustering.KMeans.kMeans;

/**
 * The class {@link ReadImage} reads the image selected by the user and generates the color scheme.
 *
 * @author &copy; 2024 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.0
 * @since 18.0.1
 */
public class ReadImage extends Task<Void> {

    /**
     * Creates a {@link ColorLogger} for this class
     */
    private static final Logger LOGGER = ColorLogger.newLogger(ReadImage.class.getName());
    /**
     * The {@link AppController} for this class
     */
    private AppController con = null;

    /**
     * Sets the {@link AppController} for this class
     * @param con The {@link AppController} to set
     */
    public void setController(AppController con) {
        this.con = con;
    }

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
        Objects.requireNonNull(getTextField()).appendText(
                getResBundle().getString("startFoundFile")
                        + System.lineSeparator());

        BufferedImage img;
        try {
            img = findImage();
            if (IS_DEBUG) {
                LOGGER.log(Level.INFO, "Found image.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException: Could not read file!");
            addToOutputField(getResBundle().getString("errorReadingFile") + System.lineSeparator(), true);
            return null;
        }

        updateProgress(24, 100);
        addToOutputField(getResBundle().getString("startReadingColours") + System.lineSeparator(), false);
        TimeUnit.MILLISECONDS.sleep(1000);

        setColorData(createColorData(img));
        if (!isCancelled()) {
            updateProgress(68, 100);
            addToOutputField(getResBundle().getString("startDeterminingColours") + System.lineSeparator(), false);
        }

        if (!isCancelled()) {
            kMeans(getColorData(), con.getSpinner().getValue());
            updateProgress(86, 100);
            addToOutputField(getResBundle().getString("startCreatingScheme") + System.lineSeparator(), false);
            if (IS_DEBUG) {
                LOGGER.log(Level.INFO, "Creating colour scheme.");
            }
        }

        if (!isCancelled()) {
            updateProgress(99, 100);
            addToOutputField(getResBundle().getString("startFinishing") + System.lineSeparator(), false);
            if (IS_DEBUG) {
                LOGGER.log(Level.INFO, "Finishing up.");
            }
        }

        if (!isCancelled()) {
            TimeUnit.MILLISECONDS.sleep(1000);
            updateProgress(100, 100);
            addToOutputField(getResBundle().getString("startDone") + System.lineSeparator(), false);
            con.getDownloadBtn().setDisable(false);
        }
        if (con.getAutoDownload().isSelected() && !isCancelled()) {
            con.downloadFile();
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
        BufferedImage image = ImageIO.read(con.getFileName().toFile());
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
}